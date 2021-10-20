#include <MemoryFree.h>
#include <avr/wdt.h>
#include <QueueArray.h>
#include <Wire.h>
#include <SD.h>
#include <SPI.h>
#include "RTClib.h"
#include <Time.h>
#include <TimeAlarms.h>
#include <DS3231.h>
#include <Ultrasonic.h>

//Pino para leitura do SD
#define SDCARD 48
//Numero de leituras para chegar ao valor da porta
#define NUM_READS 20

//Alteração para leitura dos sensores ultrassônicos
#define pino_trigger_s1 22
#define pino_echo_s1 24
#define pino_trigger_s2 26
#define pino_echo_s2 28

Ultrasonic ultrasonic(pino_trigger_s1, pino_echo_s1);
Ultrasonic ultrasonic2(pino_trigger_s2, pino_echo_s2);

// Init the DS3231 using the hardware interface
// Arduino Mega:
// ----------------------
// DS3231:  SDA pin   -> Arduino Digital 20 (SDA) or the dedicated SDA pin
//          SCL pin   -> Arduino Digital 21 (SCL) or the dedicated SCL pin
DS3231 rtc(SDA, SCL);

struct parameters {
  String nome = "DEFAULT";
  int intervaloColeta = 5;
  String portaSaida = "RS232";
  String RS485 = "false";
  String D1 = "true";
  String D2 = "false";
  String A1 = "'A1', true, 0, 10 ,0";
  String A2 = "'A2', true, 0, 10 ,0";
  String A3 = "'A3', true, 0, 10 ,0";
  String A4 = "'A4', true, 0, 10 ,0";
  String A5 = "'A5', true, 0, 10 ,0";
  String A6 = "'A6', true, 0, 10 ,0";
  String A7 = "'A7', true, 0, 10 ,0";
  String A8 = "'A8', true, 0, 10 ,0";
  String A9 = "'A9', true, 0, 10 ,0";
  String A10 = "'A10', true, 0, 10 ,0";
  String A11 = "'A11', true, 0, 10 ,0";
  String A12 = "'A12', true, 0, 10 ,0";
  String A13 = "'A13', true, 0, 10 ,0";
  String A14 = "'A14', true, 0, 10 ,0";
  String A15 = "'A15', true, 0, 10 ,0";
  String A16 = "'A16', true, 0, 10 ,0";
  String ULTRASSONIC1 = "true";
  String ULTRASSONIC2 = "true";
} configs;

//Demais variaveis
volatile int pulseCounterD2 = 0;
volatile int pulseCounterD3 = 0;
volatile int mainLoopCounter = 0;
boolean alarmed = false;
boolean keepRunning = true;
boolean hasError = false;
int countError = 0;
AlarmId lastAlarm;
boolean accumulate = false;

//SD card utilitarios
Sd2Card card;
SdVolume volume;
SdFile root;
boolean failSD = false;

//Varivel de DATA geral
DateTime dtPrincipal;

void setup() {
  // Inicializa a comunicacao SERIAL -- debug USB
  Serial.begin(9600);
  // Serial 1 -- Conexão raspberry
  Serial1.begin(9600);
  // Serial 2 -- Conector 2
  Serial2.begin(9600);
  // Serial 3 -- Conector 3 (pode ser RS485 ou RS232, entrada ou saída)
  Serial3.begin(9600);

  //Inicializa o RTC
  Wire.begin();
  rtc.begin();

  //Atualiza a hora da compilacao como horario inicial
  Serial.print("Data/Hora: ");

  //Sincroniza o metodo se alarme
  setSyncProvider(syncProvider);
  setSyncInterval(1);
  printDate();

  //Inicializa o SD card
  Serial.print("Inicializando cartao SD... ");

  if (!SD.begin(SDCARD)) {
    Serial.print("Modulo SD falhou! ");
    failSD = true;
    hasError = true;
  }

  if (!card.init(SPI_HALF_SPEED, SDCARD)) {
    Serial.print("Cartao SD falhou! ");
    failSD = true;
    hasError = true;
  }

  if ((!volume.init(card)) && (!failSD)) {
    Serial.println("Nao foi possivel encontrar uma particao FAT16 ou FAT32.\nVerifique se voce formatou o cartao corretamente ");
    failSD = true;
  }

  if (hasError) {
    Serial.print("Inicializacao falhou! ");
  } else {
    Serial.println("Inicializacao completa. ");
  }

  Serial.println();

  //Se o SD não falhou, carrega as configurações
  if (!failSD) {
    //Busca configuracoes
    keepRunning = getSettings();
    //Mostra configuracoes
    printConfigs();
    //Preenche variaveis com as configurações
    fillSettings();
  } else {
    keepRunning = false;
  }

  //Inicializa o watchdog, com 8 segundos de inatividade para reset.
  wdt_enable(WDTO_8S);
}

void loop() {
  //Reinicia se ocorreu erro na inicialização
  if (hasError) {
    Serial.println(" Reiniciando devido erro de inicializacao");
    delay(10000);
  }

  //Verifica se leu o arquivo de configuracoes e se deve continuar rodando
  if (keepRunning) {
    //Busca data e hora atuais
    dtPrincipal = rtc.getUnixTime(rtc.getTime());

    //Se tiver sido alarmado, incrementa 1 segundo. Caso contrario, incrementa o loop.
    if (alarmed) {
      //Se houver algo chegando pela RS485 e estiver ativo a configuração para gravação, recebe dado e grava
      if (configs.RS485) {
        if (Serial3.available() > 0) {
          String comando = Serial3.readString();
          //Chama gravação em arquivo
          fileRS485(dtPrincipal, comando);   
          //Envia para raspberry
          sendMessage(comando);
        }
      }

      //Necessario para incrementar o contador de segundos do alarme.
      Alarm.delay(1000);

      //Reseta o watchdog, ou seja se o programa travar e não passar por esse reset, em 8 segundos o módulo será reiniciado
      wdt_reset();
    } else {
      Serial.print(".");

      delay(1000);
      //Reseta o watchdog, ou seja se o programa travar e não passar por esse reset, em 8 segundos o módulo será reiniciado
      wdt_reset();
    }

    if (!alarmed) {
      //Quanto o resto da divisão do minuto pelo intervalo configurado for 0, alarma
      //---------------------------------------------------------------------- alterar para debug -------------------------------------------------------------
      //if ((dtPrincipal.second() % configs.intervaloColeta) == 0) {
      if ((dtPrincipal.minute() % configs.intervaloColeta) == 0) {
        alarm();
      } else
        //Se intervalo = 60, testar minuto 0
        //---------------------------------------------------------------------- alterar para debug -------------------------------------------------------------
        //if ((dtPrincipal.second() == 0) && (configs.intervaloColeta == 60)) {
        if ((dtPrincipal.minute() == 0) && (configs.intervaloColeta == 60)) {
          alarm();
        }
    }
  } else {
    Serial.println("Erro!");
    if (countError == 0) {
      Serial2.print("Erro: ErroKeepRunning");
      Serial2.println();
      countError++;
    }
    delay(10000);
  }
}

void alarm() {
  alarmed = true;

  //Programa alarme (HORA, MINUTO, SEGUNDO, METODO)
  //Alarm.timerRepeat(horaAlarm, minAlarm, secAlarm, mainLoop);
  //------------------------------------------------------------------------------------------ alterar para debug -----------------------------------
  //Alarm.timerRepeat((configs.intervaloColeta), mainLoop);
  Alarm.timerRepeat((configs.intervaloColeta * 60), mainLoop);

  Serial.println("Sistema alarmado com sucesso");
  Serial.println("");

  //Chama loop principal
  mainLoop();
}

void mainLoop() {
  mainLoopCounter++;

  //Obtem a data e hora atuais
  DateTime dt = now();

  //Exibe dados atuais na serial de Debug
  printPortStatus(dt);

  //Chama a escrita em arquivo e envio, de acordo com a quantidade configurada para ser acumulada antes de ennviar.
  if (mainLoopCounter == 1) {
    fileLoop(dtPrincipal);
    mainLoopCounter = 0;    
    Serial1.flush();
    Serial2.flush();
  }

  //Exibe memoria disponivel depois do processo
  Serial.print("Memoria livre e temperatura: ");
  Serial.print(freeMemory());
  Serial.print(" ");
  Serial.print(rtc.getTemp());
  Serial.println("C");
  Serial.println("");
}

void fileRS485(DateTime dt, String data) {
   //Gerando arquivo (um por dia, dentro da pasta com o nome da estacao)
  SD.mkdir(configs.nome);
  String fileName = "/" + configs.nome + "/";
  fileName += printFileName(dt);
  fileName += ".dig";

  //Gerando arquivo DIG
  File file = SD.open(fileName.c_str() , FILE_WRITE);

  Serial.print("Abrindo o arquivo: ");
  Serial.print(fileName);
  Serial.print(" ... ");

  //Se o arquivo abrir corretamente, escrever.
  if (file) {
    file.println(data);
    file.close();
    Serial.println("Escreveu a leitura!");
  }
  else {
    //Se o arquivo nao abrir, exibir erro.
    Serial.println("Erro ao abrir o arquivo");
  }  
}

void fileLoop(DateTime dt) {
  //Instancia String de dados
  String dataString = "";
  String dataStringRAW = "";
  String dataStringOUT = "";

  boolean printHeader = false;

  //Gerando arquivo (um por dia, dentro da pasta com o nome da estacao)
  SD.mkdir(configs.nome);
  String fileName = "/" + configs.nome + "/";
  fileName += printFileName(dt);
  fileName += ".txt";
  String fileNameRAW = "/" + configs.nome + "/";
  fileNameRAW += printFileName(dt);
  fileNameRAW += ".raw";

  /* Data
  */
  dataStringRAW += "DT=";
  dataStringRAW += printFormatedDate(dt);
  dataStringRAW += " ";
  dataStringRAW += printFormatedHour(dt);
  dataStringRAW += ";";

  dataString += printFormatedDate(dt);
  dataString += ";";
  dataString += printFormatedHour(dt);
  dataString += ";";

  /* Portas digitais
     Registrador: Porta Digital (D1, D2);
     Unidade de medida: cacambadas
  */
  //Pega os valores dos sensores
  int chuva;
  if (configs.D1 == "true") {
    dataStringRAW += "D1=";
    dataStringRAW += pulseCounterD2;
    dataStringRAW += ";";

    dataString += "D1=";
    dataString += pulseCounterD2;
    dataString += ";";

    if (!accumulate) {
      pulseCounterD2 = 0;
    }
  }
  if (configs.D2 == "true") {
    dataStringRAW += "D2=";
    dataStringRAW += pulseCounterD3;
    dataStringRAW += ";";

    dataString += "D2=";
    dataString += pulseCounterD3;
    dataString += ";";
    
    if (!accumulate) {
      pulseCounterD3 = 0;
    }
  }

  dataStringOUT = dataStringRAW;

  /* Analogicos
     Registrador: Portas Analogicas (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16);
  */
  if (getSplittedValue(configs.A1, ',', 1) == "true") {
    float auxRead = getAnalogRead(1, true);
    dataStringRAW += getSplittedValue(configs.A1, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A1, ',', 0)+"=";
    dataStringOUT += getAnalogRead(1, false);
    dataStringOUT += ";";
    
    dataString += getAnalogRead(1, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A2, ',', 1) == "true") {
    float auxRead = getAnalogRead(2, true);
    dataStringRAW += getSplittedValue(configs.A2, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A2, ',', 0)+"=";
    dataStringOUT += getAnalogRead(2, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(2, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A3, ',', 1) == "true") {
    float auxRead = getAnalogRead(3, true);
    dataStringRAW += getSplittedValue(configs.A3, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A3, ',', 0)+"=";
    dataStringOUT += getAnalogRead(3, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(3, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A4, ',', 1) == "true") {
    float auxRead = getAnalogRead(4, true);
    dataStringRAW += getSplittedValue(configs.A4, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A4, ',', 0)+"=";
    dataStringOUT += getAnalogRead(4, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(4, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A5, ',', 1) == "true") {
    float auxRead = getAnalogRead(5, true);
    dataStringRAW += getSplittedValue(configs.A5, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A5, ',', 0)+"=";
    dataStringOUT += getAnalogRead(5, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(5, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A6, ',', 1) == "true") {
    float auxRead = getAnalogRead(6, true);
    dataStringRAW += getSplittedValue(configs.A6, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT +=getSplittedValue(configs.A6, ',', 0)+"=";
    dataStringOUT += getAnalogRead(6, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(6, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A7, ',', 1) == "true") {
    float auxRead = getAnalogRead(7, true);
    dataStringRAW += getSplittedValue(configs.A7, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A7, ',', 0)+"=";
    dataStringOUT += getAnalogRead(7, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(7, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A8, ',', 1) == "true") {
    float auxRead = getAnalogRead(8, true);
    dataStringRAW += getSplittedValue(configs.A8, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A8, ',', 0)+"=";
    dataStringOUT += getAnalogRead(8, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(8, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A9, ',', 1) == "true") {
    float auxRead = getAnalogRead(9, true);
    dataStringRAW += getSplittedValue(configs.A9, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A9, ',', 0)+"=";
    dataStringOUT += getAnalogRead(9, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(9, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A10, ',', 1) == "true") {
    float auxRead = getAnalogRead(10, true);
    dataStringRAW += getSplittedValue(configs.A10, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A10, ',', 0)+"=";
    dataStringOUT += getAnalogRead(10, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(10, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A11, ',', 1) == "true") {
    float auxRead = getAnalogRead(11, true);
    dataStringRAW += getSplittedValue(configs.A11, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A11, ',', 0)+"=";
    dataStringOUT += getAnalogRead(11, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(11, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A12, ',', 1) == "true") {
    float auxRead = getAnalogRead(12, true);
    dataStringRAW += getSplittedValue(configs.A12, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A12, ',', 0)+"=";
    dataStringOUT += getAnalogRead(12, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(12, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A13, ',', 1) == "true") {
    float auxRead = getAnalogRead(13, true);
    dataStringRAW += getSplittedValue(configs.A13, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A13, ',', 0)+"=";
    dataStringOUT += getAnalogRead(13, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(13, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A14, ',', 1) == "true") {
    float auxRead = getAnalogRead(14, true);
    dataStringRAW += getSplittedValue(configs.A14, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A14, ',', 0)+"=";
    dataStringOUT += getAnalogRead(14, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(14, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A15, ',', 1) == "true") {
    float auxRead = getAnalogRead(15, true);
    dataStringRAW += getSplittedValue(configs.A15, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A15, ',', 0)+"=";
    dataStringOUT += getAnalogRead(15, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(15, false);
    dataString += ";";
  }
  if (getSplittedValue(configs.A16, ',', 1) == "true") {
    float auxRead = getAnalogRead(16, true);
    dataStringRAW += getSplittedValue(configs.A16, ',', 0)+"=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += getSplittedValue(configs.A16, ',', 0)+"=";
    dataStringOUT += getAnalogRead(16, false);
    dataStringOUT += ";";

    dataString += getAnalogRead(16, false);
    dataString += ";";
  }

  if (configs.ULTRASSONIC1 == "true") {
    float auxRead = getDistancia(1);
    dataStringRAW += "ULTRASSONIC1=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += "ULTRASSONIC1=";
    dataStringOUT += auxRead;
    dataStringOUT += ";";

    dataString += auxRead;
    dataString += ";";
  }
  if (configs.ULTRASSONIC2 == "true") {
    float auxRead = getDistancia(2);
    dataStringRAW += "ULTRASSONIC2=";
    dataStringRAW += auxRead;
    dataStringRAW += ";";

    dataStringOUT += "ULTRASSONIC2=";
    dataStringOUT += auxRead;
    dataStringOUT += ";";

    dataString += auxRead;
    dataString += ";";
  }

  //Testa se o arquivo já existe para determinar se terá ou não header
  if (!SD.exists(fileName)) {
    printHeader = true;
  }

  //Gerando arquivo CSV
  File file = SD.open(fileName.c_str() , FILE_WRITE);

  Serial.print("Abrindo o arquivo: ");
  Serial.print(fileName);
  Serial.print(" ... ");

  //Se o arquivo abrir corretamente, escrever.
  if (file) {
    if (printHeader) {
      file.println(createHeader());
    }

    file.println(dataString);
    file.close();
    Serial.println("Escreveu a leitura!");
  }
  else {
    //Se o arquivo nao abrir, exibir erro.
    Serial.println("Erro ao abrir o arquivo");
  }

  //Gerando arquivo RAW
  file = SD.open(fileNameRAW.c_str() , FILE_WRITE);

  Serial.print("Abrindo o arquivo: ");
  Serial.print(fileNameRAW);
  Serial.print(" ... ");

  //Se o arquivo abrir corretamente, escrever.
  if (file) {
    file.println(dataStringRAW);
    file.close();
    Serial.println("Escreveu a leitura RAW!");
  }
  else {
    //Se o arquivo nao abrir, exibir erro.
    Serial.println("Erro ao abrir o arquivo");
  }
  
  //Envia os dados para o raspberry 
  sendMessage(dataStringRAW);

  //Envia os dados para as demais seriais, caso necessário
  sendMessageOUT(dataStringOUT);
  
}

String createHeader() {
  String aux = "data;hora;";

  if (configs.D1 == "true") {
    aux += "digital1;";
  }
  if (configs.D2 == "true") {
    aux += "digital2;";
  }
  if (getSplittedValue(configs.A1, ',', 1) == "true") {
    aux += "analogico1;";
  }
  if (getSplittedValue(configs.A2, ',', 1) == "true") {
    aux += "analogico2;";
  }
  if (getSplittedValue(configs.A3, ',', 1) == "true") {
    aux += "analogico3;";
  }
  if (getSplittedValue(configs.A4, ',', 1) == "true") {
    aux += "analogico4;";
  }
  if (getSplittedValue(configs.A5, ',', 1) == "true") {
    aux += "analogico5;";
  }
  if (getSplittedValue(configs.A6, ',', 1) == "true") {
    aux += "analogico6;";
  }
  if (getSplittedValue(configs.A7, ',', 1) == "true") {
    aux += "analogico7;";
  }
  if (getSplittedValue(configs.A8, ',', 1) == "true") {
    aux += "analogico8;";
  }
  if (getSplittedValue(configs.A9, ',', 1) == "true") {
    aux += "analogico9;";
  }
  if (getSplittedValue(configs.A10, ',', 1) == "true") {
    aux += "analogico10;";
  }
  if (getSplittedValue(configs.A11, ',', 1) == "true") {
    aux += "analogico11;";
  }
  if (getSplittedValue(configs.A12, ',', 1) == "true") {
    aux += "analogico12;";
  }
  if (getSplittedValue(configs.A13, ',', 1) == "true") {
    aux += "analogico13;";
  }
  if (getSplittedValue(configs.A14, ',', 1) == "true") {
    aux += "analogico14;";
  }
  if (getSplittedValue(configs.A15, ',', 1) == "true") {
    aux += "analogico15;";
  }
  if (getSplittedValue(configs.A16, ',', 1) == "true") {
    aux += "analogico16;";
  }
  if (configs.ULTRASSONIC1 == "true") {
    aux += "ultrassonic1;";
  }
  if (configs.ULTRASSONIC2 == "true") {
    aux += "ultrassonic2;";
  }

  return aux;
}

void printPortStatus(DateTime dt) {
  Serial.print("Data: ");
  Serial.print(printFormatedDate(dt));

  //Pega os valores dos sensores
  if (configs.D1 == "true") {
    Serial.print(" D1: ");
    Serial.print(pulseCounterD2);
  }
  if (configs.D2 == "true") {
    Serial.print(" D2: ");
    Serial.print(pulseCounterD3);
  }

  if (getSplittedValue(configs.A1, ',', 1) == "true") {
    Serial.print(" A1: ");
    Serial.print(getAnalogRead(1, false));
  }
  if (getSplittedValue(configs.A2, ',', 1) == "true") {
    Serial.print(" A2: ");
    Serial.print(getAnalogRead(2, false));
  }
  if (getSplittedValue(configs.A3, ',', 1) == "true") {
    Serial.print(" A3: ");
    Serial.print(getAnalogRead(3, false));
  }
  if (getSplittedValue(configs.A4, ',', 1) == "true") {
    Serial.print(" A4: ");
    Serial.print(getAnalogRead(4, false));
  }
  if (getSplittedValue(configs.A5, ',', 1) == "true") {
    Serial.print(" A5: ");
    Serial.print(getAnalogRead(5, false));
  }
  if (getSplittedValue(configs.A6, ',', 1) == "true") {
    Serial.print(" A6: ");
    Serial.print(getAnalogRead(6, false));
  }
  if (getSplittedValue(configs.A7, ',', 1) == "true") {
    Serial.print(" A7: ");
    Serial.print(getAnalogRead(7, false));
  }
  if (getSplittedValue(configs.A8, ',', 1) == "true") {
    Serial.print(" A8: ");
    Serial.print(getAnalogRead(8, false));
  }
  if (getSplittedValue(configs.A9, ',', 1) == "true") {
    Serial.print(" A9: ");
    Serial.print(getAnalogRead(9, false));
  }
  if (getSplittedValue(configs.A10, ',', 1) == "true") {
    Serial.print(" A10: ");
    Serial.print(getAnalogRead(10, false));
  }
  if (getSplittedValue(configs.A11, ',', 1) == "true") {
    Serial.print(" A11: ");
    Serial.print(getAnalogRead(11, false));
  }
  if (getSplittedValue(configs.A12, ',', 1) == "true") {
    Serial.print(" A12: ");
    Serial.print(getAnalogRead(12, false));
  }
  if (getSplittedValue(configs.A13, ',', 1) == "true") {
    Serial.print(" A13: ");
    Serial.print(getAnalogRead(13, false));
  }
  if (getSplittedValue(configs.A14, ',', 1) == "true") {
    Serial.print(" A14: ");
    Serial.print(getAnalogRead(14, false));
  }
  if (getSplittedValue(configs.A15, ',', 1) == "true") {
    Serial.print(" A15: ");
    Serial.print(getAnalogRead(15, false));
  }
  if (getSplittedValue(configs.A16, ',', 1) == "true") {
    Serial.print(" A16: ");
    Serial.print(getAnalogRead(16, false));
  }
  if (configs.ULTRASSONIC1 == "true") {
    Serial.print(" ULTRASSONIC1: ");
    Serial.print(getDistancia(1));
  }
  if (configs.ULTRASSONIC2 == "true") {
    Serial.print(" ULTRASSONIC2: ");
    Serial.print(getDistancia(2));
  }

  Serial.println("");

}

void printDate() {
  Serial.print(rtc.getDateStr(FORMAT_LONG, FORMAT_LITTLEENDIAN, '/'));
  Serial.print(" ");
  Serial.println(rtc.getTimeStr());
  Serial.println("");
}

void printTemp() {
  Serial.println(rtc.getTemp());
}


String printDateString() {
  String data = "";
  data += rtc.getDateStr(FORMAT_LONG, FORMAT_LITTLEENDIAN, '/');
  data += " ";
  data += rtc.getTimeStr();
  return data;
}

String printFileName(DateTime dt) {
  String fileName = "";
  String strDay = "";
  if (dt.day() < 10) {
    strDay += "0";
    strDay += dt.day();
  } else {
    strDay += dt.day();
  }
  fileName += strDay;

  String strMonth = "";
  if (dt.month()  < 10) {
    strMonth += "0";
    strMonth += dt.month();
  } else {
    strMonth += dt.month();
  }

  fileName += strMonth;
  fileName += dt.year();
  return fileName;
}

String printFormatedDate(DateTime dt) {
  String date = "";

  String strDay = "";
  if (dt.day() < 10) {
    strDay += "0";
    strDay += dt.day();
  } else {
    strDay += dt.day();
  }
  date += strDay;
  date += '/';

  String strMonth = "";
  if (dt.month()  < 10) {
    strMonth += "0";
    strMonth += dt.month();
  } else {
    strMonth += dt.month();
  }
  date += strMonth;
  date += '/';

  date += dt.year();

  return date;
}

String printFormatedHour(DateTime dt) {
  String date = "";
  date += dt.hour();
  date += ':';
  date += dt.minute();
  date += ':';
  date += dt.second();

  return date;
}

String printDataLoggerDate(DateTime dt) {
  //Formato: 9/23/2014
  String date = "";

  date += dt.month();
  date += '/';
  date += dt.day();
  date += '/';
  date += dt.year();
  date += ' ';
  date += dt.hour();
  date += ':';
  date += dt.minute();
  date += ':';
  date += dt.second();
  date += ' ';
  return date;
}

void countPulseD2() {
  pulseCounterD2++;
}
void countPulseD3() {
  pulseCounterD3++;
}

float getAnalogRead(int sensor, boolean isRaw) {
  float valorFinal = 0;
  int port = 0;
  String isTopCase = "false";
  float intervaloIni = 0.0;
  float intervaloFim = 0.0;
  float offset = 0.0;
  float slope = 0;

  //Testa qual sensor está ativo
  if (sensor == 1) {
    if (getSplittedValue(configs.A1, ',', 1) == "true") {
      port = 0;
      intervaloIni = getSplittedValue(configs.A1, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A1, ',', 3).toFloat();
      offset = getSplittedValue(configs.A1, ',', 4).toFloat();
    }
  } else if (sensor == 2) {
    if (getSplittedValue(configs.A2, ',', 1) == "true") {
      port = 1;
      intervaloIni = getSplittedValue(configs.A2, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A2, ',', 3).toFloat();
      offset = getSplittedValue(configs.A2, ',', 4).toFloat();
    }
  } else if (sensor == 3) {
    if (getSplittedValue(configs.A3, ',', 1) == "true") {
      port = 2;
      intervaloIni = getSplittedValue(configs.A3, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A3, ',', 3).toFloat();
      offset = getSplittedValue(configs.A3, ',', 4).toFloat();
    }
  } else if (sensor == 4) {
    if (getSplittedValue(configs.A4, ',', 1) == "true") {
      port = 3;
      intervaloIni = getSplittedValue(configs.A4, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A4, ',', 3).toFloat();
      offset = getSplittedValue(configs.A4, ',', 4).toFloat();
    }
  } else if (sensor == 5) {
    if (getSplittedValue(configs.A5, ',', 1) == "true") {
      port = 4;
      intervaloIni = getSplittedValue(configs.A5, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A5, ',', 3).toFloat();
      offset = getSplittedValue(configs.A5, ',', 4).toFloat();
    }
  } else if (sensor == 6) {
    if (getSplittedValue(configs.A6, ',', 1) == "true") {
      port = 5;
      intervaloIni = getSplittedValue(configs.A6, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A6, ',', 3).toFloat();
      offset = getSplittedValue(configs.A6, ',', 4).toFloat();
    }
  } else if (sensor == 7) {
    if (getSplittedValue(configs.A7, ',', 1) == "true") {
      port = 6;
      intervaloIni = getSplittedValue(configs.A7, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A7, ',', 3).toFloat();
      offset = getSplittedValue(configs.A7, ',', 4).toFloat();
    }
  } else if (sensor == 8) {
    if (getSplittedValue(configs.A8, ',', 1) == "true") {
      port = 7;
      intervaloIni = getSplittedValue(configs.A8, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A8, ',', 3).toFloat();
      offset = getSplittedValue(configs.A8, ',', 4).toFloat();
    }
  } else if (sensor == 9) {
    if (getSplittedValue(configs.A9, ',', 1) == "true") {
      port = 8;
      intervaloIni = getSplittedValue(configs.A9, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A9, ',', 3).toFloat();
      offset = getSplittedValue(configs.A9, ',', 4).toFloat();
    }
  } else if (sensor == 10) {
    if (getSplittedValue(configs.A10, ',', 1) == "true") {
      port = 9;
      intervaloIni = getSplittedValue(configs.A10, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A10, ',', 3).toFloat();
      offset = getSplittedValue(configs.A10, ',', 4).toFloat();
    }
  } else if (sensor == 11) {
    if (getSplittedValue(configs.A11, ',', 1) == "true") {
      port = 10;
      intervaloIni = getSplittedValue(configs.A11, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A11, ',', 3).toFloat();
      offset = getSplittedValue(configs.A11, ',', 4).toFloat();
    }
  } else if (sensor == 12) {
    if (getSplittedValue(configs.A12, ',', 1) == "true") {
      port = 11;
      intervaloIni = getSplittedValue(configs.A12, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A12, ',', 3).toFloat();
      offset = getSplittedValue(configs.A12, ',', 4).toFloat();
    }
  } else if (sensor == 13) {
    if (getSplittedValue(configs.A13, ',', 1) == "true") {
      port = 12;
      intervaloIni = getSplittedValue(configs.A13, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A13, ',', 3).toFloat();
      offset = getSplittedValue(configs.A13, ',', 4).toFloat();
    }
  } else if (sensor == 14) {
    if (getSplittedValue(configs.A14, ',', 1) == "true") {
      port = 13;
      intervaloIni = getSplittedValue(configs.A14, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A14, ',', 3).toFloat();
      offset = getSplittedValue(configs.A14, ',', 4).toFloat();
    }
  } else if (sensor == 15) {
    if (getSplittedValue(configs.A15, ',', 1) == "true") {
      port = 14;
      intervaloIni = getSplittedValue(configs.A15, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A15, ',', 3).toFloat();
      offset = getSplittedValue(configs.A15, ',', 4).toFloat();
    }
  } else if (sensor == 16) {
    if (getSplittedValue(configs.A16, ',', 1) == "true") {
      port = 15;
      intervaloIni = getSplittedValue(configs.A16, ',', 2).toFloat();
      intervaloFim = getSplittedValue(configs.A16, ',', 3).toFloat();
      offset = getSplittedValue(configs.A16, ',', 4).toFloat();
    }
  }

  //Debug
  //  Serial.print("intervaloIni = ");
  //  Serial.println(intervaloIni);
  //  Serial.print("intervaloFim = ");
  //  Serial.println(intervaloFim);
  //  Serial.print("offset = ");
  //  Serial.println(offset);

  //Testa se o intevalo inicial é negativo
  boolean wasNegative = false;
  float intervaloIniAux = 0;
  if (intervaloIni < 0) {
    intervaloIniAux = intervaloIni * -1;
    wasNegative = true;
  } else {
    intervaloIniAux = intervaloIni;
  }

  //  Serial.println(intervaloIni);

  //Calcula range
  float range = 0.0;
  if (wasNegative) {
    range = intervaloFim + intervaloIniAux;
  } else {
    range = intervaloFim - intervaloIniAux;
  }
  //  Serial.println(range);

  //Fator de variacao (range = variacao total 1023 divido pelo range informado pelo usuário)
  float fatorVariacao = 1023 / range;
  //  Serial.println(fatorVariacao);

  //Faz a leitura bruta do sensor
  int leituraBruta = readAnalog(port);

  if (isRaw) {
    return leituraBruta;
  }
  //  Serial.print("Leitura Bruta:");
  //  Serial.println(leituraBruta);

  //Encontra diferenca entre o valor encontrado e o zero. 203/205 é a leitura referente a 4mA ou 0v.
  //float leituraLimpa = leituraBruta - 203;
  float leituraLimpa = leituraBruta;
  //  Serial.print("Leitura Limpa:");
  //  Serial.println(leituraLimpa);

  //Leitura bruta
  float valorBruto = (float)leituraLimpa / fatorVariacao;
  //  Serial.print("Valor Bruto");
  //  Serial.println(valorBruto);

  //Leitura real
  float leituraReal = valorBruto + (float) intervaloIniAux;
  if (wasNegative) {
    leituraReal = valorBruto - (float) intervaloIniAux;
  }
  //  Serial.print("Leitura Real:");
  //  Serial.println(leituraReal);

  //Valor final
  float sensorFinal = leituraReal;

  //Somar OFFSET
  if (isTopCase.indexOf("true") > -1) {
    sensorFinal = offset - sensorFinal;
  } else {
    sensorFinal = offset + sensorFinal;
  }

  //  Serial.print("Leitura Final:");
  //  Serial.println(sensorFinal);

  return sensorFinal;
}

float readAnalog(int sensorpin) {
  int sortedValues[NUM_READS];
  for (int i = 0; i < NUM_READS; i++) {
    int value = analogRead(sensorpin);
    int j;
    if (value < sortedValues[0] || i == 0) {
      j = 0; 
    }
    else {
      for (j = 1; j < i; j++) {
        if (sortedValues[j - 1] <= value && sortedValues[j] >= value) {          
          break;
        }
      }
    }
    for (int k = i; k > j; k--) {
      sortedValues[k] = sortedValues[k - 1];
    }
    sortedValues[j] = value; 
  }
  long returnval = 0;
  for (int i = 0; i < NUM_READS; i++) {
    returnval += sortedValues[i];
  }
  returnval = returnval / NUM_READS;
  return returnval;
}

boolean getSettings() {
  // Abre o arquivo com as configurações:
  File myFile = SD.open("config.txt");

  if (!myFile) {
    Serial.println("Arquivo de configuracoes nao encontrado.");
    Serial.println("Parando a aplicacao!");
    return false;
  }
  Serial.print("Abrindo arquivo configuracoes... ");

  char character;
  String description = "";
  String value = "";
  // Varre o arquivo ate que nao haja mais nada a ser lido
  while (myFile.available()) {
    character = myFile.read();

    //Ignora os comentários
    if (character == '/') {
      while (character != '\n') {
        character = myFile.read();
      };
    } else if (isalnum(character)) { //Se for alfanumerico, faz parte da descricao
      description.concat(character);
    } else if (character == '=') {
      //Remove espacos em branco
      do {
        character = myFile.read();
      } while (character == ' ');
      //Pega os parametros na ordem do arquivo
      //Serial.println(description);
      if (description == "nome") {
        value = "";
        //Concatena enquanto nao houver quebra de linha
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.nome = value;
        //      Serial.println(value);
      } else if (description == "intervaloColeta") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.intervaloColeta = atoi(value.c_str());
        //  Serial.println(value);
      } else if (description == "portaSaida") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.portaSaida = value;
        //    Serial.println(value);
      } else if (description == "D1") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.D1 = value;
        //  Serial.println(value);
      } else if (description == "D2") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.D2 = value;
        //  Serial.println(value);
      } else if (description == "RS485") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.RS485 = value;
        //  Serial.println(value);
      } else if (description == "A1") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A1 = value;
        //  Serial.println(value);
      } else if (description == "A2") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A2 = value;
        //  Serial.println(value);
      } else if (description == "A3") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A3 = value;
        //  Serial.println(value);
      } else if (description == "A4") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A4 = value;
        //  Serial.println(value);
      } else if (description == "A5") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A5 = value;
        //  Serial.println(value);
      } else if (description == "A6") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A6 = value;
        //  Serial.println(value);
      } else if (description == "A7") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A7 = value;
        //  Serial.println(value);
      } else if (description == "A8") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A8 = value;
        //  Serial.println(value);
      } else if (description == "A9") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A9 = value;
        //  Serial.println(value);
      } else if (description == "A10") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A10 = value;
        //  Serial.println(value);
      } else if (description == "A11") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A11 = value;
        //  Serial.println(value);
      } else if (description == "A12") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A12 = value;
        //  Serial.println(value);
      } else if (description == "A13") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A13 = value;
        //  Serial.println(value);
      } else if (description == "A14") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A14 = value;
        //  Serial.println(value);
      } else if (description == "A15") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A15 = value;
        //  Serial.println(value);
      } else if (description == "A16") {
        value = "";
        do {
          value.concat(character);
          character = myFile.read();
        } while (character != '\n');
        configs.A16 = value;
        //  Serial.println(value);
      } else {
        while (character != '\n')
          character = myFile.read();
      }
      description = "";
    } else {
    }
  }

  Serial.println("Configuracoes carregadas com sucesso!");
  Serial.println("");

  //Fecha a leitura do arquivo
  myFile.close();

  return true;
}

void fillSettings() {
  //Inicializa portas digitais (contadoras de pulso)
  if (configs.D1 == "true") {
    pinMode(2, INPUT);
    digitalWrite(2, HIGH);
    attachInterrupt(0, countPulseD2, FALLING);
  }
  if (configs.D2 == "true") {
    pinMode(3, INPUT);
    digitalWrite(3, HIGH);
    attachInterrupt(1, countPulseD3, FALLING);
  }

}

void sendMessage(String message) {
  Serial.print("Enviando mensagem para o Rasberry (");
  //@9/23/2014 16:30:0 A 1: 1.88 P 1: 0 B 1: 13.13
  Serial.print(message);
  Serial1.print(message);
  Serial1.print("--EOF");
  Serial1.println();
  Serial.println(")... Mensagem enviada!");
  Serial.println("");
}

void sendMessageOUT(String message) {
  Serial.print("Enviando mensagem para Seriais (");
  //@9/23/2014 16:30:0 A 1: 1.88 P 1: 0 B 1: 13.13
  Serial.print(message);
  
  if (configs.portaSaida.indexOf("RS232") > -1) {
    Serial2.print(message);
    Serial2.println();
  }
  if (configs.portaSaida.indexOf("RS485") > -1) {
    Serial3.print(message);
    Serial3.println();  
  }
  
  Serial.println(")... Mensagem enviada!");
  Serial.println("");
}

void printConfigs() {
  Serial.print("Nome: ");
  Serial.println(configs.nome);
  Serial.print("Intervalo de Coleta: ");
  Serial.println(configs.intervaloColeta);
  Serial.print("Porta de Saida: ");
  Serial.println(configs.portaSaida);
  Serial.print("D1: ");
  Serial.println(configs.D1);
  Serial.print("D2: ");
  Serial.println(configs.D2);
  Serial.print("RS485: ");
  Serial.println(configs.RS485);
  Serial.print("A1: ");
  Serial.println(configs.A1);
  Serial.print("A2: ");
  Serial.println(configs.A2);
  Serial.print("A3: ");
  Serial.println(configs.A3);
  Serial.print("A4: ");
  Serial.println(configs.A4);
  Serial.print("A5: ");
  Serial.println(configs.A5);
  Serial.print("A6: ");
  Serial.println(configs.A6);
  Serial.print("A7: ");
  Serial.println(configs.A7);
  Serial.print("A8: ");
  Serial.println(configs.A8);
  Serial.print("A9: ");
  Serial.println(configs.A9);
  Serial.print("A10: ");
  Serial.println(configs.A10);
  Serial.print("A11: ");
  Serial.println(configs.A11);
  Serial.print("A12: ");
  Serial.println(configs.A12);
  Serial.print("A13: ");
  Serial.println(configs.A13);
  Serial.print("A14: ");
  Serial.println(configs.A14);
  Serial.print("A15: ");
  Serial.println(configs.A15);
  Serial.print("A16: ");
  Serial.println(configs.A16);
  Serial.print("ULTRASSONIC1: ");
  Serial.println(configs.ULTRASSONIC1);
  Serial.print("ULTRASSONIC2: ");
  Serial.println(configs.ULTRASSONIC2);
  Serial.println("");  
}

//Funções uteis
String getSplittedValue(String data, char separator, int index) {
  /* Exemplo:
    String split = "hi this is a split test";
    String word3 = getValue(split, ' ', 2);
    Serial.println(word3);
  */

  int found = 0;
  int strIndex[] = {0, -1};
  int maxIndex = data.length() - 1;

  for (int i = 0; i <= maxIndex && found <= index; i++) {
    if (data.charAt(i) == separator || i == maxIndex) {
      found++;
      strIndex[0] = strIndex[1] + 1;
      strIndex[1] = (i == maxIndex) ? i + 1 : i;
    }
  }
  return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}

time_t syncProvider() {
  return rtc.getUnixTime(rtc.getTime());
}

float getDistancia(int sensor) {
  if (sensor == 1) {
    float cmMsec;
    long microsec = ultrasonic.timing();
    cmMsec = ultrasonic.convert(microsec, Ultrasonic::CM);
    //Exibe informacoes no serial monitor
    return cmMsec;  
  } else if (sensor == 2) {    
    float cmMsec;
    long microsec = ultrasonic2.timing();
    cmMsec = ultrasonic2.convert(microsec, Ultrasonic::CM);
    //Exibe informacoes no serial monitor
    return cmMsec;    
  }  
}

