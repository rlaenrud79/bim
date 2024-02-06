(function ($) {
    var o = {
        processItem: [],
        processItemTotal: [],
        processSearch: false,
        searchProcessPopup: false,

        init: function () {

            // 기성 등록 열기
            $.nfx.click('#btnAdd', function () {
                const params = {};
                const callback = function (data) {
                    $('#modalAddGisung').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/gisung/addGisung`);
            });

            // search
            $.nfx.click('#btnSearchGisung', function () {
                o.reloadCardBody();
            });

            // reset
            $.nfx.click('#btnResetSearchCondition', function () {
                o.resetAndSearch();
            });

            // 기성 수정 열기
            $(document).on("click", '.openUpdateModal', function () {
                const status = $(this).data('status');
                const params = {};
                const param = `id=${$(this).data('gisung-id')}`;
                const callback = function (data) {
                    $('#modalUpdateGisung').find('.popup-con').html(data);
                    $('#modalUpdateGisung').attr('tabindex',0).show().focus();
                };

                if (status == "COMPLETE") {
                    location.href = `/gisung/jobSheetList?${param}`;
                } else {
                    $.nfx.ajaxGet(params, 'html', callback, `/gisung/updateGisung?${param}`);
                }
            });

            // 공정추가 버튼 클릭
            $(document).on("click", '#btnPrcessAdd', function () {
                o.onClickBtnGisungPrcessAdd();
            });

            // 리스트 이동
            $.nfx.click('#btnMoveListPage', function () {
                console.log("btnMoveListPage");
                window.location.href = '/gisung/gisungList';
            });

            // 임시 저장
            $.nfx.click('#btnWriteGisung', function () {
                o.updateGisungAggregation("WRITING");
            });

            // 최종 저장
            $.nfx.click('#btnWriteGisungComplete', function () {
                if (confirm(confirmCompleteSave)) {
                    o.updateGisungAggregation("COMPLETE");
                }
            });

            // 엑셀다운
            $.nfx.click('#exportXlsx', function () {
                window.open("/gisungApi/exportGisungCostXlsx/"+gisungId);
            });

            // 전체엑셀생성
            /**
            $.nfx.click('#exportTotalXlsx', function () {
                // WebSocket 연결을 열기
                const socket = new WebSocket('ws://localhost:8765');

                // 연결이 열린 경우 실행되는 이벤트 핸들러
                socket.addEventListener('open', (event) => {
                    console.log('WebSocket 연결이 열렸습니다.', event);

                    // 서버에 메시지 전송
                    socket.send('make_gisung_excel_total');
                });

                // 메시지를 받은 경우 실행되는 이벤트 핸들러
                socket.addEventListener('message', (event) => {
                    console.log('서버로부터 메시지를 수신했습니다:', event.data);
                    $(this).html("전체 기성 엑셀 다운로드");
                    $(this).attr("id", "exportTotalXlsxDown");
                    socket.close();
                    // 받은 메시지를 처리하는 코드를 추가하세요.
                });

                // 에러가 발생한 경우 실행되는 이벤트 핸들러
                socket.addEventListener('error', (event) => {
                    console.error('WebSocket 오류:', event);
                });

                // 연결이 닫힌 경우 실행되는 이벤트 핸들러
                socket.addEventListener('close', (event) => {
                    console.log('WebSocket 연결이 닫혔습니다.', event);
                });
                reqGet("/gisungApi/exportGisungCostTotalXlsxPython/"+gisungId,
                    function (data) {
                        if (!data.result) {
                            alert(data.message);
                        } else {
                            alert(data.message);
                            //toastr.success(data.message);
                            //location.reload();
                        }
                    },
                    function (xhr) {
                        alert(xhr.responseJSON.message);
                    }
                )
            });
             **/

            // 엑셀다운
            $.nfx.click('#exportGcodeXlsx', function () {
                window.open("/gisungApi/exportGisungGcodeXlsx/"+gisungId);
            });

            // 전체엑셀다운
            $(document).on("click", '#exportTotalXlsxDown', function () {
                window.open("/commonApi/downloadFile/GISUNG_EXCEL_FILE/0");
            });

            // 기성 삭제
            $(document).on("click", '#btnDeleteGisung', function () {
                let gisungId = $(this).data('gisung-id');
                if (confirm(confirmDeleteDocument)) {
                    o.deleteGisung(gisungId)
                }
            });

            $(document).on("click", '#btnDeleteGisungProcessItem', function () {
                let id = $(this).data('id');
                if (confirm(confirmDeleteDocument)) {
                    o.deleteGisungProcessItem(id)
                }
            });

            // 복합단가 팝업 열기
            $(document).on("click", '.btnCostDetail', function () {
                let processItemId = $(this).data("id");
                let taskName = $(this).data("task-name");
                //console.log(processItemId + " : " + taskName);

                reqGet("/gisung/costDetail/" + processItemId
                    , function (data) {
                        $('#costDetail').find('.popup-tit').html(taskName);
                        $('#costDetail').find('.popup-con').html(data);
                        $('#costDetail').attr('tabindex',0).show().focus();
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });

            // 공정추가 검색 복합단가 팝업 열기
            $(document).on("click", '.btnCostDetailSearch', function () {
                let processItemId = $(this).data("id");
                let taskName = $(this).data("task-name");
                //console.log(processItemId + " : " + taskName);

                reqGet("/gisung/processCostDetail/" + processItemId + "/R"
                    , function (data) {
                        $('#costDetail').find('.popup-tit').html(taskName);
                        $('#costDetail').find('.popup-con').html(data);
                        $('#costDetail').attr('tabindex',0).show().focus();
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });

            // 리스트 업로드
            $.nfx.click('#btnListExcelUpload', function () {
                let gisungId = $("#gisungId").val();
                //console.log(gisungId);
                const params = {};
                const callback = function (data) {
                    $('#listExcelUpload').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/gisung/addListExcelUpload?id=${gisungId}`);
                /**
                reqGet("/gisung/addListExcelUpload/" + gisungId
                    , function (data) {
                        $('#listExcelUpload').find('.popup-con').html(data);
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
                 **/
            });

            // 검증/비교 팝업 열기
            $.nfx.click('#btnCompareGisung', function () {
                let gisungId = $("#gisungId").val();
                //console.log(gisungId);
                const params = {};
                const callback = function (data) {
                    $('#taskComparisonPopup').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/gisung/gisungCompareList?id=${gisungId}`);
            });

            // 복합단가 팝업 열기
            $(document).on("click", '.btnCompareCostDetail', function () {
                let processItemId = $(this).data("id");
                let taskName = $(this).data("task-name");
                //console.log(processItemId + " : " + taskName);

                reqGet("/gisung/compareCostDetail/" + processItemId
                    , function (data) {
                        $('#compareCostDetail').find('.popup-tit').html(taskName);
                        $('#compareCostDetail').find('.popup-con').html(data);
                        $('#compareCostDetail').attr('tabindex',0).show().focus();
                    }
                    , function (xhr) {
                        alert($.parseJSON(xhr.responseText).error);
                    }, "html");
            });

            // 코드/비교 팝업 열기
            $(document).on("click", '#btnCompareGisungCode, #popCompareGisungCode', function () {
                let gisungId = $("#gisungId").val();
                const code = $(this).data("code");
                //console.log(gisungId);
                if (code != "") {
                    const params = {};
                    const callback = function (data) {
                        $('#gcodeListPopup').find('.popup-con').html(data);
                    };

                    $.nfx.ajaxGet(params, 'html', callback, `/gisung/gisungCodeList?id=${gisungId}&code=${code}`);
                }
            });

            // 코드/비교 검색 버튼 클릭
            $(document).on("click", '#searchGisungCode', function () {
                let gisungId = $("#gisungId").val();
                const code = $("#gcode").val();
                //console.log(gisungId);
                if (code != "") {
                    const params = {};
                    const callback = function (data) {
                        $('#gcodeListPopup').find('.popup-con').html(data);
                    };

                    $.nfx.ajaxGet(params, 'html', callback, `/gisung/gisungCodeList?id=${gisungId}&code=${code}`);
                }
            });

            // 검증/비교 선택 적용
            /**
            $.nfx.click('.btn-setting-sel', function () {
                if ($(".compare-list-item-checkbox:checkbox:checked").length > 0) {
                    if (confirm('선택한 공정값을 적용 하시겠습니까?')) {
                        o.gcodeUpdate($.nfx.form, 'setting_sel', null);
                    }
                } else {
                    alert('적용할 공정을 선택하십시오.');
                }
            });
             **/

            // 공정 선택 팝업 열기
            $(document).on("click", '#btnProcessSelect', function () {
                o.searchProcess.processItem = [];
                o.searchProcess.processItemTotal = [];
                o.searchProcess.searchProcessPopup = true;
                o.searchProcess.getProgressPopupData();
            });

            // 공정 검색 팝업 열기
            $(document).on("click", '#btnSerachProcess', function () {
                const params = {};
                const code = "";
                //console.log(gisungId + " --- " + code);
                const callback = function (data) {
                    $('#taskAddPopup').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/gisung/processItemSearchList?id=${gisungId}&code=${code}`);
            });

            $(document).on("click", '.btn-process-item-search', function () {
                o.searchProcess.processItem = [];
                o.searchProcess.processItemTotal = [];
                const code = $("#search_gcode").val();
                //console.log(gisungId + " --- " + code);
                o.getProgressItemSearchData(code);
            });

            // 전체엑셀생성
            $.nfx.click('#exportTotalXlsx', function () {
                reqGet("/gisungApi/exportGisungCostTotalXlsxPython/0",
                    function (data) {
                        if (!data.result) {
                            alert(data.message);
                        } else {
                            alert(data.message);
                            //toastr.success(data.message);
                            //location.reload();
                        }
                    },
                    function (xhr) {
                        alert(xhr.responseJSON.message);
                    }
                )
            });

            // 전체엑셀다운
            $.nfx.click('#exportTotalXlsxDown', function () {
                window.open("/commonApi/downloadFile/GISUNG_EXCEL_FILE/0");
            });

            // 선택 삭제
            $(document).on("click", '.btn-del-sel', function () {
                if ($(".list-item-checkbox:checkbox:checked").length > 0) {
                    if (confirm(selectGisungDelete)) {
                        o.deleteSelGisung()
                    }
                } else {
                    alert(deleteGisungSelect);
                }
            });
        },

        onClickBtnGisungPrcessAdd: function() {
            var matchedItems = [];

            $('.itemCheck:checked').each(function () {
                // data-no 속성을 사용하여 item_no 값을 가져옴
                var itemNo = $(this).data('no');
                if (itemNo) {
                    for (var i = 0; i < o.searchProcess.processItemTotal.length; i++) {
                        if (o.searchProcess.processItemTotal[i].processItemId === itemNo) {
                            matchedItems.push(o.searchProcess.processItemTotal[i]);
                        }
                    }
                }
            });
            //console.log(matchedItems);

            if (matchedItems.length > 0) {
                o.addGisungProcessItem(matchedItems);
            } else {
                alert("선택된 공정이 없습니다");
            }
        },

        setProgressItemCostDetailCount: function(index, schCount) {
            let progressCost = $.nfx.calculateMultipleZ(schCount, $('td#unitPrice:eq(' + index + ')').attr('data-value'));
            $("td#progressCost:eq("+index+")").attr('data-value', progressCost);
            $("td#progressCost:eq("+index+")").text($.nfx.numberWithCommas(progressCost));
            let sumProgressCount = $.nfx.calculateAdd($("td#paidProgressCount:eq("+index+")").attr('data-value'), schCount);
            $("td#sumProgressCount:eq("+index+")").text(parseFloat(sumProgressCount).toFixed(4));
            let sumProgressCost = $.nfx.calculateAdd($("td#paidCost:eq("+index+")").attr('data-value'), progressCost);
            $("td#sumProgressCost:eq("+index+")").attr('data-value', sumProgressCost);
            $("td#sumProgressCost:eq("+index+")").text($.nfx.numberWithCommas(sumProgressCost));
            let remindProgressCount = $.nfx.calculateMinus($("td#count:eq("+index+")").attr('data-value'), sumProgressCount);
            $("td#remindProgressCount:eq("+index+")").text(parseFloat(remindProgressCount).toFixed(4));
            let remindProgressCost = $.nfx.calculateMinus($("td#cost:eq("+index+")").attr('data-value'), sumProgressCost);
            $("td#remindProgressCost:eq("+index+")").attr('data-value', remindProgressCost);
            $("td#remindProgressCost:eq("+index+")").text($.nfx.numberWithCommas(remindProgressCost));
        },

        getProgressItemSearchData: function(code) {
            reqPostJSON("/gisungApi/getProcessItemCostSearchCode", o.getProcessItemSearchCondition(gisungId, code), function (data) {
                //document.getElementById("loader").style.display="none";
                //console.log(data);
                if (data.result) {
                    let content = '';
                    let no = 1;
                    o.searchProcess.processItem = JSON.parse(data.model);
                    o.searchProcess.setProcessItemTotal();
                    o.searchProcess.processItem.forEach((m, index) => {
                        let itemCheck = "itemCheckSearch" + index;
                        content += '<tr>';
                        content += '<td>';
                        content += '<div class="check-set">';
                        content += '<input type="checkbox" id="'+itemCheck+'" name="item_no[]" class="list-item-checkbox itemCheck" data-no="'+m.processItemId+'" value="'+m.processItemId+'">';
                        content += '<label for="'+itemCheck+'"></label>';
                        content += '</div>';
                        content += '</td>';
                        content += '<td>'+(index+1)+'</td>';
                        content += '<td class="txt-left"><b>'+m.taskName+'</b></td>';
                        content += '<td>'+m.phasingCode+'</td>';
                        content += '<td><button type="button" class="btn-xs btn-bo3 btnCostDetailSearch" data-modal="#costDetail" data-id="'+m.processItemId+'" data-task-name="'+m.taskName+'">'+$.nfx.numberWithCommasPoint(m.complexUnitPrice)+'</button></td>';
                        content += '<td>'+$.nfx.numberWithCommasPoint(m.taskCost)+'</td>';
                        content += '<td>'+$.nfx.numberWithCommasPoint(m.progressAmount)+' ('+m.progressRate+'%)</span></td>';
                        content += '<td>'+$.nfx.numberWithCommasPoint(m.paidCost)+' ('+$.nfx.numberWithCommasPoint(m.paidProgressRate*100)+'%)</td>';
                        content += '</tr>';
                        no = no + 1;
                    });

                    $('#taskAddPopup').find('.tb-process-item-search tbody').html(content);
                    $('#taskAddPopup').find('.board-list').css('display', 'block');
                }
            }, function (xhr) {
                alert($.parseJSON(xhr.responseJSON).error);
            });
        },

        getProcessItemSearchCondition: function (gisungId, code) {
            if ($("#schTaskName").val() != "") {
                code = $("#schTaskName").val();
            }
            return JSON.stringify(
                {
                    id: gisungId,
                    code: code,
                    taskName: "", //$("#schTaskName").val(),
                    isMinCost: $("#search_costmin").is(":checked")
                }
            );
        },

        addGisungProcessItem: function(taskList) {
            reqPostJSON('/gisungApi/postGisungProcessItem'
                , o.setSendData(taskList)
                , function (data) {
                    if (data.result) {
                        $("#taskAddPopup").hide();
                        reloadComponent("/gisung/jobSheetForm?id="+gisungId, "#divCardBody", "");
                        //window.location.href = '/gisung/gisungAggregationList';
                        alert(data.message);
                        //toastr.success(data.message);
                    } else {
                        alert(data.message);
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            );
        },

        setSendData: function(taskList) {
            return JSON.stringify({
                "gisungId": gisungId + "",
                "status": "WRITING",
                "data": o.getValues(taskList)
            });
        },

        setSearchGcodeSendData: function() {
            return JSON.stringify({
                "id": gisungId
            });
        },

        getValues: function(taskList) {
            let vals = [];
            for (let i = 0; i < taskList.length; i++) {
                let existProcessItem = false;
                for (let ii = 0; ii < $("input[name=processItemId]").length; ii++) {
                    if ($("input[name=processItemId]").eq(ii).val() === (taskList[i].processItemId + "")) {
                        existProcessItem = true;
                        break;
                    }
                }
                if (existProcessItem) {
                    continue;
                }

                vals.push({
                    phasingCode: taskList[i].phasingCode + "",
                    id: taskList[i].processItemId + "",
                });
            }
            return vals;
        },

        updateGisungAggregation: function(status) {
            reqPutJSON('/gisungApi/putGisungProcessItem'
                , o.setSendUpdateData(status)
                , function (data) {
                    if (data.result) {
                        if (status == "COMPLETE") {
                            window.location.href = '/gisung/gisungList';
                        } else {
                            alert(data.message);
                            reloadComponent("/gisung/jobSheetForm?id=" + gisungId, "#divCardBody", "");
                        }
                        //toastr.success(data.message);
                    }
                    else {
                        alert(data.message);
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            );
        },

        setSendUpdateData: function(status) {
            return JSON.stringify({
                "gisungId": gisungId + "",
                "status": status + "",
                "data": o.getUpdateValues(status)
            });
        },

        getUpdateValues: function(status) {
            let vals = [];
            $("div#divCardBody table").map(function (i, el) {
                const table = $(el).find('table[class="tbGisung"]');
                let id = $(el).find('input[name="id"]');
                let progressRate = $(el).find('input[name="progressRate"]');
                let cost = $(el).find('input[name="cost"]');
                let paidProgressRate = $(el).find('input[name="paidProgressRate"]');
                let paidCost = $(el).find('input[name="paidCost"]');
                for (let i=0; i<id.length; i++) {
                    if (status == "COMPLETE") {
                        vals.push({
                            progressRate: progressRate.eq(i).val() + "",
                            paidProgressRate: paidProgressRate.eq(i).val() + "",
                            cost: cost.eq(i).val() + "",
                            paidCost: paidCost.eq(i).val() + "",
                            //paidProgressRate: parseFloat(paidProgressRate.eq(i).val()) + parseFloat(progressRate.eq(i).val() * 0.01) + "",
                            //paidCost: parseFloat(cost.eq(i).val()) + parseFloat(paidCost.eq(i).val()) + "",
                            id: id.eq(i).val() + "",
                        })
                    } else {
                        vals.push({
                            progressRate: progressRate.eq(i).val() + "",
                            paidProgressRate: paidProgressRate.eq(i).val() + "",
                            cost: cost.eq(i).val() + "",
                            paidCost: paidCost.eq(i).val() + "",
                            //paidProgressRate: paidProgressRate.eq(i).val() + "",
                            //paidCost: paidCost.eq(i).val() + "",
                            id: id.eq(i).val() + "",
                        })
                    }
                }
            });
            return vals;
        },
        deleteGisung: function(gisungId) {
            reqDelete(`/gisungApi/deleteGisung?id=${gisungId}`,
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        o.resetAndSearch();
                    }
                },
                function (xhr) {
                    alert(xhr.responseJSON.message);
                }
            )
        },
        deleteSelGisung: function() {
            reqDeleteJSON('/gisungApi/deleteSelGisung', o.setSendDeleteData(),
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        o.resetAndSearch();
                    }
                }, function (xhr) {
                    alert(xhr.responseJSON.message);

                }
            )
        },
        setSendDeleteData: function () {
            let val = [];
            $(".list-item-checkbox:checkbox:checked").each(function () {
                val.push({
                    id: $(this).val() + "",
                });
            });

            return JSON.stringify(val);
        },
        deleteGisungProcessItem: function(id) {
            reqDelete(`/gisungApi/deleteGisungProcessItem?id=${id}`,
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        //toastr.success(data.message);
                        reloadComponent("/gisung/jobSheetForm?id="+gisungId, "#divCardBody", "");
                    }
                },
                function (xhr) {
                    alert(xhr.responseJSON.message);
                }
            )
        },
        resetAndSearch: function() {
            o.resetSearchCondition();
            o.reloadCardBody();
        },
        reloadCardBody: function() {
            reloadComponent("/gisung/gisungListCardBody", "#divCardBody", o.getSearchCondition());
        },
        getSearchCondition: function() {
            let param = "";
            param += `page=${parseInt($("#pageNo").val()) || 0}`;
            param += `&size=${parseInt($("#pageSize").val())}`;
            param += `&searchWorkId=${parseInt($("#searchWorkId").val())}`;
            param += `&searchType=${$("#searchType").val()}`;
            param += `&searchValue=${$("#searchValue").val()}`;
            param += `&searchUserId=${parseInt($("#searchUserId").val()) || 0}`;
            param += `&searchUserDisplayName=${$("#searchUserDisplayName").val()}`;
            param += `&startDateString=${$("#startDate").val()}`;
            param += `&endDateString=${$("#endDate").val()}`;
            param += `&SortProp=${$("#sortProp").val()}`;
            return param;
        },
        resetSearchCondition: function() {
            $("#searchWorkId").val(0);
            $("#searchType").val('none');
            $("#searchValue").val('');
            $("#searchUserId").val(0);
            $("#searchUserDisplayName").val('');
            $("#startDate").val('');
            $("#endDate").val('');
            $("#sortProp").val('');
            $("#pageNo").val(0);
            $("#pageSize").val(20);
        },

        gcodeUpdate: function (form, ptype, obj) {

        },

        searchProcess: {
            processItem: [],
            processItemTotal: [],
            processSearch: false,
            searchProcessPopup: false,

            init: function () {
                // 공정추가 팝업 검색
                $(document).on("click", '.btn-process-search', function () {
                    const taskName = $("#schTaskName").val();
                    let processTotalCnt = 0;
                    reqPostJSON("/costApi/getProcessItemCost", o.searchProcess.getProcessCostItemSearchCondition('0', '00000', 'ganttTaskType'), function (data) {
                        //document.getElementById("loader").style.display="none";
                        console.log(data);
                        if (data.result) {
                            o.searchProcess.processItem = JSON.parse(data.model);
                            o.searchProcess.setProcessItemTotal();
                            const content = o.searchProcess.setProgressCateSearch(taskName);

                            o.searchProcess.processSearch = true;

                            $('#taskAddPopup').find('.popup-con').html(content);

                            if (o.searchProcess.dataCateCnt.length > 0) {
                                o.searchProcess.dataCateCnt.forEach((m, index) => {
                                    $("#"+m.id+"_cnt").text("("+m.cnt+"건)");
                                    processTotalCnt += m.cnt;
                                });
                            }
                            $("#processTotalCnt").text(processTotalCnt);
                        }
                    }, function (xhr) {
                        alert($.parseJSON(xhr.responseJSON).error);
                    });
                });

                // 공정추가 팝업 열기
                $.nfx.click('#btnSearchProcess', function () {
                    console.log("공정추가 팝업 열기");
                    $(".bg-loading").show();
                    //if (!o.searchProcess.searchProcessPopup) {
                        o.searchProcess.searchProcessPopup = true;
                        o.searchProcess.getProgressPopupData();
                    //}
                });

                // 공정추가 팝업 초기화
                $(document).on("click", '.btn-process-search-reset', function () {
                    $(".bg-loading").show();
                    o.searchProcess.searchProcessPopup = false;
                    o.searchProcess.processSearch = false;
                    if (!o.searchProcess.searchProcessPopup) {
                        o.searchProcess.searchProcessPopup = true;
                        $("#schTaskName").val("");
                        o.searchProcess.getProgressPopupData();
                    }
                });

                // 공정추가 팝업 닫기
                $(document).on("click", '.btn-process-close', function () {
                    $(".close").click();
                });

                $(document).on("click", '.tree-nav ul > li > .menu-top > .menu-btn, .tree-nav ul > li > .menu-top > .menu-tit', function (e) {
                    let cateNo = $(this).closest('li').data("cate-no");
                    let cate = $(this).closest('li').data("cate");

                    if (!$(this).closest('li').hasClass('cateRoad')) {
                        if (cateNo) {
                            o.searchProcess.getProgressData(cateNo, cate);
                        }
                    }

                    /* 만약 클릭된 메뉴에 엑티브 클래스가 있으면 */
                    if ($(this).closest('li').hasClass('active')) {
                        /* 클릭된 메뉴의 엑티브를 없앤다 */
                        $(this).closest('li').removeClass('active');
                    } else {
                        /* 클릭된 메뉴의 형제의 엑티브를 없앤다 */
                        $(this).closest('a').siblings('.active').removeClass('active');

                        /* 클릭된 메뉴(지역)의 엑티브를 없앤다 */
                        $(this).closest('li').find('.active').removeClass('active');

                        /* 클릭된 메뉴의 엑티브를 만든다 */
                        $(this).closest('li').addClass('active');
                        $(this).closest('li').addClass('cateRoad');
                    }

                    /* 클릭된 메뉴 안에 다른 메뉴를 클릭하면 위에있는 메뉴가 같이 클릭되는데 그것을 막아준다 */
                    e.stopPropagation();
                });
            },

            getProcessCostItemSearchCondition: function (cateNo, upCode, ganttTaskType) {
                return JSON.stringify(
                    {
                        cateNo: cateNo,
                        upCode: upCode,
                        taskName: $("#schTaskName").val(),
                        ganttTaskType: ganttTaskType + ""
                    }
                );
            },

            getProgressPopupData: function() {
                reqPostJSON("/costApi/getProcessItemCost", o.searchProcess.getProcessCostItemSearchCondition(1, '00000', ''), function (data) {
                    //document.getElementById("loader").style.display="none";
                    //console.log(data);
                    if (data.result) {
                        let content = '';
                        content += '<div class="tree-nav mt-0">';
                        o.searchProcess.processItem = JSON.parse(data.model);
                        o.searchProcess.setProcessItemTotal();
                        const html = o.searchProcess.setProgressCate();
                        content += html + "</div>";
                        content += '<div class="btn-box sticky">';
                        content += '<div class="right-box">';
                        content += '<input type="text" name="schTaskName" id="schTaskName" placeholder="공정명을 입력하세요.">';
                        content += '<a href="#none" class="btn btn btn-process-search">검색</a>';
                        content += '<a href="#none" id="btnPrcessAdd" class="btn btn-color1">적용</a>';
                        content += '<a href="#none" class="btn btn-process-close">취소</a>';
                        content += '</div>';
                        content += '</div>';

                        $('#taskAddPopup').find('.popup-con').html(content);
                        $(".bg-loading").hide();
                    }
                }, function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                });
            },

            getProgressData: function (cateNo, cate) {    // 2depth 이상 호출
                console.log("setProgressCate2");
                let cate_arr = cate.split("|");
                let cate1 = cate_arr[0];
                let cate2 = cate_arr[1];
                let cate3 = cate_arr[2];
                let cate4 = cate_arr[3];
                let cate5 = cate_arr[4];
                let cate6 = cate_arr[5];
                let upCode = cate1;
                let cateCode = o.searchProcess.getProcessCateCode((cateNo-1), cate);
                let divCateId = "divCate_" + cateCode.replaceAll("|", "");
                if (cateNo == 2) {
                    upCode = cate1;
                } else if (cateNo == 3) {
                    upCode = cate2;
                } else if (cateNo == 4) {
                    upCode = cate3;
                } else if (cateNo == 5) {
                    upCode = cate4;
                } else if (cateNo == 6) {
                    upCode = cate5;
                }
                reqPostJSON("/costApi/getProcessItemCost", o.searchProcess.getProcessCostItemSearchCondition(cateNo, upCode, ''), function (data) {
                    if (data.result) {
                        o.searchProcess.processItem = JSON.parse(data.model);
                        o.searchProcess.setProcessItemTotal();
                        const html = o.searchProcess.setProgressCateSub(cateNo);

                        $('#taskAddPopup').find('#'+divCateId+' > ul').html(html);
                    }
                }, function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                });
            },

            setProgressCate: function() {   // 1depth
                const orignData = o.searchProcess.processItem;
                let html = "";
                let oldDataCate = "";
                orignData.forEach((m, index) => {
                    //console.log("cate1 : " + m.cate1 + ", cate2 : " + m.cate2 + ", cate3 : " + m.cate3 + ", cate4 : " + m.cate4 + ", cate5 : " + m.cate5 + ", cate6 : " + m.cate6);
                    let dataCate = m.cate1 + "|" + m.cate2.trim() + "|" + m.cate3.trim() + "|" + m.cate4.trim() + "|" + m.cate5.trim() + "|" + m.cate6.trim();
                    let itemCheckName = dataCate.replaceAll("|", "");
                    let itemCheck = "itemCheck_1_" +  + m.processItemId;
                    let cateCode = o.searchProcess.getProcessCateCode(1, dataCate);
                    let divCateId = "divCate_" + cateCode.replaceAll("|", "");
                    let title = m.cate1Name;
                    if (oldDataCate != cateCode) {
                        html += `<section class="depth0">
					<ul><li data-cate-no="2" data-cate="${dataCate}">`;
                        html += o.searchProcess.getProgressItemDivSub(1, itemCheck, itemCheckName, m, title);
                        html += `<section class="depth1" id="${divCateId}">
                        <ul>
                        </ul>
                        </section>
                    </li></ul>
				</section>`;
                    }
                    oldDataCate = cateCode;

                });

                let data = orignData;
                if (data.length > 0) {
                    data = data.map(function (item) {
                        return {
                            value: item.id,
                            label: item.name,
                            color: item.color,
                            progress: item.progress
                        };
                    });
                }
                return html;
            },

            setProgressCateSub: function(cateNo) {
                const orignData = o.searchProcess.processItem;
                let html = "";
                let oldDataCate = "";

                orignData.forEach((m, index) => {
                    //console.log("cate1 : " + m.cate1 + ", cate2 : " + m.cate2 + ", cate3 : " + m.cate3 + ", cate4 : " + m.cate4 + ", cate5 : " + m.cate5 + ", cate6 : " + m.cate6);
                    let dataCate = m.cate1 + "|" + m.cate2.trim() + "|" + m.cate3.trim() + "|" + m.cate4.trim() + "|" + m.cate5.trim() + "|" + m.cate6.trim();
                    let itemCheckName = "";
                    let title = m.cate2Name;
                    let cateCode = o.searchProcess.getProcessCateCode(cateNo, dataCate);
                    let cateNoId = (cateNo+1);
                    if (cateNo == 3) {
                        title = m.cate3Name;
                    } else if (cateNo == 4) {
                        title = m.cate4Name;
                    } else if (cateNo == 5) {
                        title = m.cate5Name;
                    } else if (cateNo == 6) {
                        title = m.taskName;
                    } else if (cateNo == 7) {
                        cateNoId = "";
                        title = m.taskName;
                    }
                    itemCheckName = dataCate.replaceAll("|", "");
                    let itemCheck = "itemCheck_"+cateNo+"_" +  + m.processItemId;
                    let divCateId = "divCate_" + cateCode.replaceAll("|", "");

                    if (oldDataCate != cateCode) {
                        html += `<li data-cate-no="${cateNoId}" data-cate="${dataCate}">`;
                        html += o.searchProcess.getProgressItemDivSub(cateNo, itemCheck, itemCheckName, m, title);
                        html += `<section class="depth${cateNo}" id="${divCateId}">
                            <ul>
                            </ul>
                        </section>
                        </li>`;
                    }

                    oldDataCate = cateCode;
                });

                let data = orignData;
                if (data.length > 0) {
                    data = data.map(function (item) {
                        return {
                            value: item.id,
                            label: item.name,
                            color: item.color,
                            progress: item.progress
                        };
                    });
                }
                return html;
            },

            getProgressItemDivSub: function (depth, itemCheck, itemCheckName, m, title) {
                let html = "";
                html += `<div class="menu-top">
                <button class="menu-btn"></button>`;
                if (depth <= 4) {
                    html += `<div class="menu-tit">
                    <span>${title}</span>
                    <span class="code"></span>
                    <span class="rate">${m.cateProgressRate}%</span>
                </div>
                <div class="check-set">
                    <input type="checkbox" id="${itemCheck}">
                    <label for="${itemCheck}"></label>
                </div>`;
                } else {
                    if (m.ganttTaskType == "TASK") {
                        html += `<div class="menu-tit">
                    <span>${title}</span>
                    <span class="code">(${m.phasingCode})</span>
                    <span class="rate">${m.progressRate}%</span>
                </div>
                <div class="check-set">
                    <input type="checkbox" id="${itemCheck}" class="itemCheck" name="item_no[]" data-no="${m.processItemId}" value="${m.processItemId}">
                    <label for="${itemCheck}"></label>
                </div>`;
                    } else {
                        html += `<div class="menu-tit">
                    <span>${title}</span>
                    <span class="code"></span>
                    <span class="rate">${m.cateProgressRate}%</span>
                </div>
                <div class="check-set">
                    <input type="checkbox" id="${itemCheck}" class="itemCheck" data-name="${itemCheckName}">
                    <label for="${itemCheck}"></label>
                </div>`;
                    }
                }
                html += '</div>';
                return html;
            },

            setProgressCateSearch: function(taskName) {   // 1depth
                const orignData = o.searchProcess.processItem;
                let html = "";
                let oldDataCate = "";
                let cateCnt = 0;
                o.searchProcess.dataCateCnt = [];
                html += `<div class="tree-nav-search">
                        <div class="tit-box">
                            <h2>총 <strong id="processTotalCnt" th:text="${orignData.length}">100</strong>건의 검색결과가 있습니다.</h2>
                        </div>`;
                orignData.forEach((m, index) => {
                    //console.log("cate1 : " + m.cate1 + ", cate2 : " + m.cate2 + ", cate3 : " + m.cate3 + ", cate4 : " + m.cate4 + ", cate5 : " + m.cate5 + ", cate6 : " + m.cate6);
                    let dataCate = m.cate1 + "|" + m.cate2.trim() + "|" + m.cate3.trim() + "|" + m.cate4.trim() + "|" + m.cate5.trim() + "|" + m.cate6.trim();
                    let itemCheckName = dataCate.replaceAll("|", "");
                    let itemCheck = "itemCheck_1_" +  + m.processItemId;
                    let cateCode = o.searchProcess.getProcessCateCode(1, dataCate);
                    let divCateId = "divCate_" + cateCode.replaceAll("|", "");
                    let title = m.cate1Name;
                    if (oldDataCate != m.cate1) {
                        if (oldDataCate != "") {
                            html += `</ul>
                                </section>`;
                            o.searchProcess.dataCateCnt.push({
                                id: oldDataCate,
                                cnt: cateCnt
                            });
                            cateCnt = 0;
                        }
                        html += `<section>
                                <div class="tit-box">
                                <h3>${title} <small id="${m.cate1}_cnt">(건)</small></h3>
                            </div>
                        <ul>`;
                    }
                    cateCnt = cateCnt + 1;
                    html += `<li>
                <span>${m.cate1Name}</span>
                <span>${m.cate2Name}</span>
                <span>${m.cate3Name}</span>
                <span>${m.cate4Name}</span>
                <span>${m.cate5Name}</span>
                <span>
                    <div class="menu-top">
                        <div class="menu-tit">
                            <span>${m.taskName}</span>
                            <span class="code">(${m.phasingCode})</span>
                            <span class="rate">${m.progressRate}%</span>
                        </div>
                        <div class="check-set">
                            <input type="checkbox" id="${itemCheck}" class="itemCheck" name="item_no[]" data-no="${m.processItemId}" value="${m.processItemId}">
                            <label for="${itemCheck}"></label>
                        </div>
                    </div>
                </span>
            </li>`;
                    oldDataCate = m.cate1;

                });

                o.searchProcess.dataCateCnt.push({
                    id: oldDataCate,
                    cnt: cateCnt
                });

                html += `</ul>
                                </section>
                        </div>
                        <div class="btn-box sticky">
                            <div class="right-box">
                                <input type="text" name="schTaskName" id="schTaskName" th:value="${taskName}" placeholder="공정명을 입력하세요.">
                                <a href="#none" class="btn btn btn-process-search">검색</a>
                                <a href="#none" id="btnPrcessAdd" class="btn btn-color1">적용</a>
                                <a href="#none" class="btn btn-color1 btn-process-search-reset">초기화</a>
                                <a href="#none" class="btn btn-process-close">취소</a>
                            </div>
                        </div>`;

                let data = orignData;
                if (data.length > 0) {
                    data = data.map(function (item) {
                        return {
                            value: item.id,
                            label: item.name,
                            color: item.color,
                            progress: item.progress
                        };
                    });
                }
                return html;
            },

            getProcessCateCode: function (cateNo, cate) {
                let cateCode = "";
                let cate_arr = cate.split("|");
                let cate1 = cate_arr[0];
                let cate2 = cate_arr[1];
                let cate3 = cate_arr[2];
                let cate4 = cate_arr[3];
                let cate5 = cate_arr[4];
                let cate6 = cate_arr[5];
                if (cateNo == 1) {
                    cateCode = cate1;
                } else if (cateNo == 2) {
                    cateCode = cate1 + "|" + cate2;
                } else if (cateNo == 3) {
                    cateCode = cate1 + "|" + cate2 + "|" + cate3;
                } else if (cateNo == 4) {
                    cateCode = cate1 + "|" + cate2 + "|" + cate3 + "|" + cate4;
                } else if (cateNo == 5) {
                    cateCode = cate1 + "|" + cate2 + "|" + cate3 + "|" + cate4 + "|" + cate5;
                } else if (cateNo == 6) {
                    cateCode = cate1 + "|" + cate2 + "|" + cate3 + "|" + cate4 + "|" + cate5 + "|" + cate6;
                }

                return cateCode;
            },

            setProcessItemTotal: function() {
                if (o.searchProcess.processItemTotal.length == 0) {
                    o.searchProcess.processItemTotal = o.searchProcess.processItem;
                }
                var obj1 = o.searchProcess.processItemTotal;
                var obj2 = o.searchProcess.processItem;

                // processItemId를 중복 체크하여 합치기
                var combinedArray = obj1.concat(obj2.reduce(function(result, item) {
                    if (!obj1.find(element => element.processItemId === item.processItemId)) {
                        result.push(item);
                    }
                    return result;
                }, []));

                // 결과를 다시 JSON 문자열로 변환하여 원래 형태로 되돌림
                var combinedString = JSON.stringify(combinedArray);

                // 최종 결과
                o.searchProcess.processItemTotal = combinedArray;
            }
        },
    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
            MAX_FILE_SIZE: 20 * 1024 * 1024,
            listCardBody: '/gisung/gisungListCardBody',
        });

        o.init();
        o.searchProcess.init();
    });

})(jQuery);