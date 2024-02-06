(function ($, ApexCharts, chartGisungData, chartJobSheetData) {
    function drawChartBar(id, title, chartData, categories) {
        var options = {
            series: chartData,
            chart: {
                foreColor: '#999',
                type: 'area',
                height: 265,
                toolbar: {
                    show:false
                }
            },
            colors: ['#1e2e3e', '#24af7b'],
            fill: {
                gradient: {
                    enabled: true,
                    opacityFrom: 0.55,
                    opacityTo: 0
                }
            },
            stroke: {
                curve: 'straight',
            },
            // markers: {
            //     size: 5,
            //     colors: ['#1e2e3e', '#e13931'],
            //     strokeWidth: 2
            // },
            /**
             plotOptions: {
                bar: {
                    horizontal: false,
                    columnWidth: '55%',
                    //endingShape: 'rounded'
                },
            },
             **/
            plotOptions: {
                bar: {
                    horizontal: false,
                    columnWidth: '200%',
                    dataLabels: {
                        //position: 'top',

                        /**
                         maxItems: 100,
                         hideOverflowingLabels: true,
                         orientation: 'horizontal',
                         total: {
                            enabled: true,
                            formatter: undefined,
                            offsetX: 0,
                            offsetY: 0,
                            style: {
                                color: '#373d3f',
                                fontSize: '12px',
                                fontFamily: undefined,
                                fontWeight: 600
                            }
                        }
                         **/
                    }
                },
            },
            dataLabels: {
                enabled: false,
                //offsetY: -20
            },
            // title: {
            //     text: title,
            //     align: 'left'
            // },
            xaxis: {
                categories: categories,
                title: {
                    text: ''
                },
            },
            yaxis: {
                labels: {
                    formatter: function (value) {
                        // 천원 단위로 콤마를 추가하여 형식 지정
                        return new Intl.NumberFormat('en-US').format(value) + '만원';
                    }
                }
            },
            tooltip: {
                theme: 'dark',
                y: {
                    formatter: function (val) {
                        return new Intl.NumberFormat('en-US').format(val) + '만원';
                    },
                },
                style: {
                    fontSize: '12px',
                    fontFamily: undefined,
                    color:'#fff'
                },
                /**
                 custom: function({series, seriesIndex, dataPointIndex, w}) {
                    var content = '<div class="gp-bubble">\n' +
                        '<div class="gp-bubble-tit">\n' +
                        '<strong>'+w.globals.labels[dataPointIndex]+'</strong>\n' +
                        '</div>\n' +
                        '<div class="gp-bubble-con">\n';
                    content += '<ul>';
                    for( let i = 0; i < w.globals.series.length; i++){
                        if (w.globals.series[i][dataPointIndex] != undefined && w.globals.series[i][dataPointIndex] != null) {
                            content += '<li>\n' +
                                '<span><i class="gp-dot" style="background-color: ' + w.globals.colors[i] + '"></i></span>\n' +
                                '<span>' + w.globals.seriesNames[i] + ' : </span>\n' +
                                '<span>' + w.globals.series[i][dataPointIndex] + '</span>\n' +
                                '</li>';
                        }
                    }
                    content +='</ul>\n' +
                        '</div>\n' +
                        '</div>';
                    console.log(w);
                    return content;
                },
                 **/
            },

            legend: {
                show:false,
                position: 'top',
                offsetY: 0,
                labels: {
                    colors: '#000',
                }
            },
            grid: {
                show: true,
                borderColor: '#ccc',
                strokeDashArray: 3,
                position: 'back'
            }
        };
        return options;
    }

    var o = {
        init: function () {
            const chartGisungDataJson = JSON.parse(chartGisungData);
            console.log(chartGisungDataJson);
            if(chartGisungDataJson !="" && chartGisungDataJson[0].data.length) {
                options = drawChartBar(chartGisungDataJson[0].id, chartGisungDataJson[0].title, chartGisungDataJson[0].data, chartGisungDataJson[0].categories);
                var chartBar01 = new ApexCharts(document.querySelector("#" + chartGisungDataJson[0].id), options);
                chartBar01.render().then(() => {});
            }

            const chartJobSheetDataJson = JSON.parse(chartJobSheetData);
            console.log(chartJobSheetDataJson);
            if(chartJobSheetDataJson !="" && chartJobSheetDataJson[0].data.length) {
                options = drawChartBar(chartJobSheetDataJson[0].id, chartJobSheetDataJson[0].title, chartJobSheetDataJson[0].data, chartJobSheetDataJson[0].categories);
                var chartBar02 = new ApexCharts(document.querySelector("#" + chartJobSheetDataJson[0].id), options);
                chartBar02.render().then(() => {});
            }
        },
    };

    $( document ).ready(function() {
        o.init();
    });

})(jQuery, ApexCharts, chartGisungData, chartJobSheetData)