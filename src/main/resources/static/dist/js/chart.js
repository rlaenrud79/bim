(function ($, ApexCharts) {
    function drawChartDonut(id, title, chartData, categories, colors, widthsize, heightsize) {
        const numberWithCommas = (x) => {
            return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        }

        var options = {
            series: chartData,
            chart: {
                fontFamily: 'NanumSquareNeo, Jost, Pretendard, sans-serif',
                height: heightsize,
                type: 'donut'
            },
            title: {
                text: title,
                align: 'left'
            },
            dataLabels: {
                enabled: true
            },
            fill: {
                colors: colors
            },
            labels: categories,
            legend: {
                show: false
            },
            plotOptions: {
                pie: {
                    donut: {
                        labels: {
                            show: true,
                            total: {
                                show: true,
                                showAlways: true,
                                fill:'#fff',
                                label: 'Total',
                                fontSize: '13px',
                                fontFamily: 'NanumSquareNeo, Helvetica, Arial, sans-serif',
                                fontWeight: 200,
                                cutoutPercentage:0,
                                color: 'white',
                                formatter: function (w) {
                                    return w.globals.seriesTotals.reduce((a, b) => {
                                        console.log("result : " + w.globals.series[0] + " : " + w.globals.series[1] + " : " + b + " / " + a + " = " + (a + b) + "");
                                        var result = 0;
                                        if (w.globals.series[0] > 0) {
                                            result = (parseInt(w.globals.series[1]) / parseInt(w.globals.series[0])) * 100;
                                            result = result.toFixed();
                                        }
                                        console.log("result : " + w.globals.seriesTotals);
                                        return result + "%"
                                        //return '<span style="color: #fff">' + result + ' %</span>';
                                    }, 0)
                                }
                            }
                        }
                    },
                    //size: 200
                    //customScale: 1.0
                }
            },
            tooltip: {
                enabled: true,
                y: {
                    formatter: function (val) {
                        return new Intl.NumberFormat('en-US').format(Math.round(val / 10000)) + '만원';
                    }
                },
            },
            responsive: [{
                breakpoint: 480,
                options: {
                    chart: {
                        width: widthsize
                    },
                    legend: {
                        position: 'bottom'
                    }
                }
            }]
        }
        return options;
    }

    var o = {
        init: function () {
            var options = "";
            if(chartDataWorkTotal !="" && chartDataWorkTotal[0].categories != "" && chartDataWorkTotal[0].data.length) {
                options = drawChartDonut(chartDataWorkTotal[0].id, chartDataWorkTotal[0].title, chartDataWorkTotal[0].data, chartDataWorkTotal[0].categories, chartDataWorkTotal[0].colors, 150, 150);
                var chartPie = new ApexCharts(document.querySelector("#" + chartDataWorkTotal[0].id), options);
                chartPie.rendered = function () {
                    // 차트가 렌더링된 후에 실행할 작업
                    $("#piechart_total .apexcharts-datalabels-group text:nth-child(2)").text("10");
                };
                chartPie.render();
            }

            if(chartDataWork !="" && chartDataWork.length > 0) {
                for (var i = 0; i < chartDataWork.length; i++) {
                    if(chartDataWork[i] !="" && chartDataWork[i][0].categories != "" && chartDataWork[i][0].data.length) {
                        options = drawChartDonut(chartDataWork[i][0].id, chartDataWork[i][0].title, chartDataWork[i][0].data, chartDataWork[i][0].categories, chartDataWork[i][0].colors, 150, 150);
                        var chartPie = new ApexCharts(document.querySelector("#" + chartDataWork[i][0].id), options);
                        chartPie.render();
                    }
                }
            }
        },
    };

    $( document ).ready(function() {
        o.init();
    });
})(jQuery, ApexCharts)