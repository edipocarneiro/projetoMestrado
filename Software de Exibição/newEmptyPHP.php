<script>


Highcharts.setOptions({
    lang: {
    months: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
            shortMonths: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'],
            weekdays: ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'],
            loading: ['Atualizando o gráfico... aguarde'],
            contextButtonTitle: 'Exportar',
            decimalPoint: ',',
            thousandsSep: '.',
            downloadPDF: 'Baixar imagem PDF',
            downloadPNG: 'Baixar imagem PNG',
            printChart: 'Imprimir gráfico',
            rangeSelectorFrom: 'De',
            rangeSelectorTo: 'Para',
            rangeSelectorZoom: 'Zoom',
            resetZoom: 'Limpar Zoom',
            resetZoomTitle: 'Voltar Zoom para nível 1:1',
            viewData: 'Ver tabela de dados',
            viewFullscreen: 'Ver em tela cheia',
            exitFullscreen: 'Sair da tela cheia',
            hideData: 'Ocultar tabela de dados',
    }
    });

Highcharts.chart('container', {
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
            text: 'Valor coletado'
        },
        minorGridLineWidth: 0,
        gridLineWidth: 0,
    },
		tooltip: {
			useHTML: true,
			shared: true,
			headerFormat: '<small align=”center” style="width: 100%">Horário da coleta: <b>{point.key:%H:%M:%S}</b></small><br/>',
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
            <?php
            series: [{
        name: 'Hestavollane',
        data: [
            3.7, 3.3, 3.9, 5.1, 3.5, 3.8, 4.0, 5.0, 6.1, 3.7, 3.3, 6.4,
            6.9, 6.0, 6.8, 4.4, 4.0, 3.8, 5.0, 4.9, 9.2, 9.6, 9.5, 6.3,
            9.5, 10.8, 14.0, 11.5, 10.0, 10.2, 10.3, 9.4, 8.9, 10.6, 10.5, 11.1,
            10.4, 10.7, 11.3, 10.2, 9.6, 10.2, 11.1, 10.8, 13.0, 12.5, 12.5, 11.3,
            10.1
        ]

    }, {
        name: 'Vik',
        data: [
            0.2, 0.1, 0.1, 0.1, 0.3, 0.2, 0.3, 0.1, 0.7, 0.3, 0.2, 0.2,
            0.3, 0.1, 0.3, 0.4, 0.3, 0.2, 0.3, 0.2, 0.4, 0.0, 0.9, 0.3,
            0.7, 1.1, 1.8, 1.2, 1.4, 1.2, 0.9, 0.8, 0.9, 0.2, 0.4, 1.2,
            0.3, 2.3, 1.0, 0.7, 1.0, 0.8, 2.0, 1.2, 1.4, 3.7, 2.1, 2.0,
            1.5
        ]
    }],
            ?>
    
    navigation: {
        menuItemStyle: {
            fontSize: '10px'
        }
    }
});
</script>