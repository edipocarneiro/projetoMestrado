<?php
include ('fcn/Bll_Dado.php');

//Inicia sessão pois fica em outro contexto
session_start();

$data = '';

//Recebe o id e data do request
@$data = $_REQUEST['data'];

//Se a data não for informada no request pega data atual
if ($data == '') {
//Busca data atual
    $data = date("Y-m-d");
//Busca data no formato do campo datePicker
    $dataExibicao = date("d/m/Y");
} else {
//Se data futura, volta pra data atual
    $dataAtual = date("d/m/Y");

//Recebe data do request que está no formado do campo datePicker
    $dataExibicao = $data;

//Formata a data para yyyy-mm-dd para busca no banco de dados
    list($dia, $mes, $ano) = explode('/', $data);
    $data = $ano . '-' . $mes . '-' . $dia;
}

$fcn_Dado = new Bll_Dado();
$ls_dados = $fcn_Dado->getByData($data);

$intervaloCalculado = 1;

//Busca última data que possui dados
$lastData = $fcn_Dado->getLastDate();
$lastData = date('d/m/Y H:i', strtotime($lastData));

foreach ($ls_dados as $d) {
//Se o dado estiver vazio, ignora.
    if ($d == null) {
        continue;
    }
}

$arrayByDate = array();
$arrayBySensor = array();
foreach ($ls_dados as $d) {
    if ($d == null) {
        continue;
    }
    $dado = new VO_Dado();
    $dado = $d;

    list($data, $hora) = explode(' ', $dado->getData());
    $dataBR = formata_data_br($data);


//Monta array de dados por horario de transmissao
    if (@$lastDataHora != ($dataBR . ' ' . $hora)) {
        unset($arr);
    }
    $arr[] = $dado->getValor();
    @$arrayByDate[$dataBR . ' ' . $hora] = $arr;

    $lastDataHora = $dataBR . ' ' . $hora;

//Monta Array de dados por sensor
    if (@$lastSensor != $dado->getSensorNome()) {
        $strSensor = '';
        $strSensor = @$arrayBySensor[$dado->getSensorNome()];
    }
    $strSensor .= $dado->getValor() . ', ';
    @$arrayBySensor[$dado->getSensorNome()] = $strSensor;

    $lastSensor = $dado->getSensorNome();
}
$lsSensores = $fcn_Dado->getBySensoresData($data);


if ((count(array_keys($ls_dados)) <= 1)) {
    ?>
    <script>
        var mensagem = {
        state0: {
        title: "Data not found!",
                html: '<p>There are no data to the selected date. </p><p>The last data for this station is: <?= $lastData ?></p>'
        }
        };
        jQuery.prompt(mensagem);</script>   
        <?php
    }

    function formata_data_br($data) {
        $data = explode('-', $data);
        $data = $data[2] . '/' . $data[1] . '/' . $data[0];
        return $data;
    }
    ?>

<div id="centro">
    <div id="headerDados"> 
        <div id="hdEsquerda">                               
        </div>

        <div id="hdDireita" style="width:auto">
            <text>Date:</text> 
            <div id="dataPicker">        
                <input type="text" value="<?= $dataExibicao ?>" id="datepicker">
            </div>    
        </div>
        <hr class="style-three" style="float: left;width: 100%;"></hr>
    </div>
    <div id="chart"> </div>
    <div id="dadosChartContent"> 
        <div id="accordion">
            <!--            <h3>Dados</h3>-->
            <div>
                <table class="table1 tablesorter" id="tabela" style="width: 85%;">
                    <col class="colUltimoEnvio"></col>
                    <?php
                    foreach ($lsSensores as &$value) {
                        echo ('<col></col>');
                    }
                    ?>
                    <thead>
                        <tr>
                            <th class="colUltimoEnvio" scope="col"><img class="thImage" alt="Hor&aacute;rio" title="HOR&Aacute;RIO" src="img/datahora.png"></br><text class="thTitulo">TIME</text></img></th>
                            <?php
                            foreach ($lsSensores as &$value) {
                                if (is_array($value)) {
                                    continue;
                                }
								if (strpos($value,'UMIDADE') !== false) {
									$value = str_replace("UMIDADE", "HUMIDITY", $value);		
								} else if ($value == 'D1') {
									$value = 'FLOW 1';
								} else if ($value == 'D2') {
									$value = 'FLOW 2';
								}
                                echo ('<th class="" scope="col"><img class="thImage" alt="' . $value . '" title="' . $value . '" src="img/sensor.png"></br><text class="thTitulo">' . $value . '</text></img></th>');
                            }
                            ?>                                                        
                        </tr>
                    </thead>
                    <tbody>
                        <?php
                        while (list($key, $val) = @each($arrayByDate)) {
                            ?>
                            <tr>
                                <td style="font-weight: normal"> <?= $key ?></td>
                                <?php
                                foreach ($val as $value) {
                                    echo '<td>' . $value . '</td>';
                                }
                                ?>
                            </tr>
                        <?php } ?>
                    </tbody>
                </table>
            </div>							
        </div>
    </div>
</div>                    

<script>
    //Inicializa o datePicker
    jQuery(function () {
    //Busca dia mes e ano atuais para limitar o datepicker.
    var date = new Date();
    var currentMonth = date.getMonth();
    var currentDate = date.getDate();
    var currentYear = date.getFullYear();
    jQuery("#datepicker").datepicker({
    onSelect: function (date) {
    // alert(date);
    loadDatepiker(date);
    },
            dateFormat: 'dd/mm/yy',
            dayNames: ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'],
            dayNamesMin: ['D', 'S', 'T', 'Q', 'Q', 'S', 'S', 'D'],
            dayNamesShort: ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb', 'Dom'],
            monthNames: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
            monthNamesShort: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'],
            nextText: 'Próximo',
            prevText: 'Anterior',
            maxDate: new Date(currentYear, currentMonth, currentDate)
    });
    });
    //Busca a data selecionada
    var dataSelecionada = jQuery("#datepicker").datepicker('getDate');
    var intervaloColeta = <?= $intervaloCalculado ?>;
    //Load do grafico caso a solicitação seja proveniente do datePicker
    function loadDatepiker(data) {
    jQuery("#centroHistorico").load('dado.php', {data: data});
    }

    //Inicializa o tableSorter
    jQuery(function () {
    // Parser para configurar a data para o formato do Brasil
    jQuery.tablesorter.addParser({
    id: 'hour',
            is: function (s) {
            return false;
            },
            format: function (s, table) {
            //Adiciona uma data qualquer apenas para conseguir ordenar apenas pelo horario 
            //Removido em 17/04/2015 pois foi adicionada a data na exibição do dado.
            //s = "2000-01-01 " + s;
            s = s.replace(/\-/g, "/");
            s = s.replace(/(\d{1,2})[\/\-](\d{1,2})[\/\-](\d{4})/, "$3/$2/$1");
            return jQuery.tablesorter.formatFloat(new Date(s).getTime());
            },
            type: 'numeric'
    });
    // Inicializa o tablesorter
    jQuery('.tablesorter').tablesorter({
    // Envia os cabeçalhos 
    headers: {
    0: {
    // Ativa o parser de data na coluna 1 (começa do 0) 
    sorter: 'hour'
    }
    },
            // Formato de data
            dateFormat: 'HH:MM:SS'
    });
    });
    Highcharts.setOptions({    
    });
    Highcharts.chart('chart', {
    chart: {
    type: 'spline'
    },
            credits: {
            enabled: false
            },
            title: {
            text: ''
            },
            xAxis: {
            lineColor: false,
                    type: 'datetime',
                    dateTimeLabelFormats: {
                    day: '%H:%M'
                    },
                    title: {
                    text: null
                    },
                    labels: {
                    style: {
                    fontFamily: '"Lucida Grande", "Lucida Sans Unicode", Verdana, Arial, Helvetica, sans-serif', fontSize: '11px'}
                    }
            },
            yAxis: {
            title: {
            text: 'Value'
            },
                    minorGridLineWidth: 0,
                    gridLineWidth: 0,
            },
            tooltip: {
            useHTML: true,
                    shared: true,
                    headerFormat: '<small align=”center” style="width: 100%">Date: <b>{point.key:%H:%M:%S}</b></small><br/>',
                    borderWidth: 3,
            },
            plotOptions: {
            spline: {
            lineWidth: 4,
                    states: {
                    hover: {
                    lineWidth: 5
                    }
                    },
                    marker: {
                    enabled: false
                    },
                    pointStart: Date.UTC(dataSelecionada.getUTCFullYear(), dataSelecionada.getUTCMonth() - 1, dataSelecionada.getUTCDate()),
                    pointInterval: intervaloColeta * 60 * 1000
            }
            },
            series: [
<?php
$first = true;
while (list($key, $val) = each($arrayBySensor)) {
    if ($first) {
        $first = false;
    } else {
        echo(',');
    }
	if (strpos($key,'UMIDADE') !== false) {
		$key = str_replace("UMIDADE", "HUMIDITY", $key);		
	} else if ($key == 'D1') {
		$key = 'FLOW 1';
	} else if ($key == 'D2') {
		$key = 'FLOW 2';
	}
	
    echo ('{');
    echo ("name: '" . $key . "',");
    //trata o valor para retirar a ultima virgula
    $valAux = substr($val, 0, -1);
    echo ("data: [" . $valAux . "]}");
}
echo ('],');
?>

            navigation: {
            menuItemStyle: {
            fontSize: '10px'
            }
            }
            });

</script>