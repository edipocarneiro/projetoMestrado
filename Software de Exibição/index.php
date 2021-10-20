<?php


//Inicia Sessão
session_start();

//Habilita exibição de erros (somente ambiente desenvolvimento)
ini_set('display_errors', 1);
ini_set('display_startup_erros', 1);
ini_set('session.gc_maxlifetime', 86400);
error_reporting(E_ALL);
//Desabilita exibição de erros (ambiente produção)
//ini_set('display_errors', false);
//ini_set('display_startup_erros', false);
//error_reporting(false);

?>

<!DOCTYPE html>
<html lang='pt-br'>
    <html>
        <head>
            <meta charset="UTF-8">
            <meta http-equiv="X-UA-Compatible" content="IE=edge">
            <meta name="viewport" content="width=device-width, height=device-height, initial-scale=0.5">
            <meta name="description" content="Projeto Mestrado">
            <meta name="author" content="Édipo Carneiro">

            <title>Projeto Mestrado</title>

            <!-- Importação dos CSS e Fonts-->
            <link rel="stylesheet" type="text/css" href="css/style.css">            
            <link rel="stylesheet" type="text/css" href="css/slider.css"/>
            <link rel="stylesheet" type="text/css" href="css/table.css">
            <link rel="stylesheet" type="text/css" href="js/jquery-ui-1.11.2.custom/jquery-ui.min.css">			
            <link rel="stylesheet" type="text/css" href="js/jquery-ui-1.11.2.custom/jquery-ui.theme.min.css">			
            <link rel="stylesheet" type="text/css" href="css/tablesorter.css"/>
            <link href='http://fonts.googleapis.com/css?family=Roboto+Slab' rel='stylesheet' type='text/css'>
            <link rel="stylesheet" href="css/colorbox.css" />
            <link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
            <link rel="stylesheet" type="text/css" href="css/style.css">

            <!-- Importação dos JS-->                        
            <!-- Slider -->
            <script src="js/slider.js"></script>
            <!-- jQuery -->
            <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
            <script type="text/javascript" src="js/jquery-ui-1.11.2.custom//jquery-ui.min.js"></script>

            <!-- HighCharts -->
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/series-label.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
<script src="https://code.highcharts.com/modules/export-data.js"></script>
<script src="https://code.highcharts.com/modules/accessibility.js"></script>

<!--
            <script type="text/javascript" src="js/highcharts.js"></script>
            <script type="text/javascript" src="js/highcharts-more.js"></script>
            <script type="text/javascript" src="js/exporting.js"></script>    -->


            <script type="text/javascript" src="js/tablesorter.min.js"></script>
            <script type="text/javascript" src="js/jquery.tablesorter.staticrow.min.js"></script>


            <!--Imports da dialog (caixa de mensagem de aviso)-->
            <script src="js/jquery-impromptu.js" type="text/javascript"></script>
            <script type="text/javascript" src="js/bootstrap.min.js"></script>
            <link rel="stylesheet" href="css/jquery-impromptu.css"/>
        </head>
        <!-- Função adjustScreen para forçar o carregamento da janela em seu tamanho correto -->
        <!--        <body onresize="adjustScreen()" oncontextmenu="return false">-->

        <header>
            <img class="" alt="Logo PPGCOMP" title="Logo PPGCOMP" src="img/ppgcomp.png" style="float: right; height: 85px">            
            <img class="" alt="Logo UNIOESTE" title="Logo UNIOESTE" src="img/logounioeste.png" style="float: right; height: 75px">            
        </header>
        
        <body>
            <div id="content">
                <div class="centro" id="centro"> 
                    <div id="centroHistorico"></div>                    
                </div>                
            </div>                        

            <script>
                jQuery(function () {
                    //quando a estação tiver dados meteorologicos esta variavel vai ter codigos java script
                    jQuery("#centroHistorico").load('dado.php');                    
                });               
            </script>
        </body>
    </html>

