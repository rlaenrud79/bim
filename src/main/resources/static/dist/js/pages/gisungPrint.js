(function ($) {
    var o = {
        processItemTotal: [],
        processSearch: false,
        searchProcessPopup: false,

        init: function () {

            // 기성 인쇄 탭
            $(document).on("click", '.btn-tab', function () {
                var documentNo = $(this).data("document-no");
                var id = $(this).data("gisung-id");
                location.href = "/gisung/gisungPrintForm?id=" + id + "&no=" + documentNo;
            });

            // 사용자 검색 모달창 열기
            $.nfx.click('#searchUserDisplayName, #btnSearchUser', function () {
                let param = "formElementIdForUserId=searchUserId";
                param += "&formElementIdForUserName=searchUserDisplayName";
                param += "&formElementIdForModal=modalSearchSingleUser";
                param += "&searchUserFilter=N";
                const params = {};

                const callback = function (data) {
                    $('#modalSearchSingleUser').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/commonModal/searchSingleUser?${param}`);
            });

            $.nfx.on('change', '#pageSize', function () {
                $("#pageNo").val(0);
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
            });

            $(document).on("click", '#btnPagePrevious, a[id^="btnPageNo_"], #btnPageNext', function () {
                $("#pageNo").val($(this).data("page-no"));
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
            });

            // search
            $.nfx.click('#btnSearchGisung', function () {
                o.reloadCardBody();
            });

            // reset
            $.nfx.click('#btnResetSearchCondition', function () {
                o.resetAndSearch();
            });

            // 기성 문서 저장
            $.nfx.click('#btnWriteGisungItem', function () {
                //console.log(setSendData());
                let gisungCoverId = $("input[name=gisungCoverId]").val();
                let gisungTableId = $("input[name=gisungTableId]").val();

                if (documentNo == 9) {      // 표지 등록, 수정
                    if ($("#title").val() === "") {
                        alert("표지 제목을 입력해 주세요.");
                        return;
                    }
                    if (gisungCoverId == "" || gisungCoverId == "0") {
                        if (confirm(confirmGisungCoverAddGisung)) {
                            o.addGisungCover();
                        }
                    } else {
                        if (confirm(confirmGisungCoverModifyGisung)) {
                            o.updateGisungCover();
                        }
                    }
                } else if (documentNo == 10) {  // 목록 등록, 수정
                    if ($("#contents").val() === "") {
                        alert("목록 내용을 입력해 주세요.");
                        return;
                    }
                    if (gisungTableId == "" || gisungTableId == "0") {
                        if (confirm(confirmGisungTableAddGisung)) {
                            o.addGisungTable();
                        }
                    } else {
                        if (confirm(confirmGisungTableModifyGisung)) {
                            o.updateGisungTable();
                        }
                    }
                } else {
                    if (gisungItemId == "" || gisungItemId == "0") {
                        if (confirm(confirmAddGisung)) {
                            o.addGisungItem();
                        }
                    } else {
                        if (confirm(confirmModifyGisung)) {
                            o.updateGisungItem();
                        }
                    }
                }
            });

            // 기성 문서 인쇄
            $.nfx.click('.btn-download-print', function (e) {
                if (documentNo == 9) {
                    var gisungCoverId = $("input[name=gisungCoverId]").val();
                    if (gisungCoverId != "" && gisungCoverId != "0") {
                        o.openPringGisungItem();
                    } else {
                        alert('인쇄할 표지를 선택해 주세요.');
                    }
                } else if (documentNo == 10) {
                    var gisungTableId = $("input[name=gisungTableId]").val();
                    if (gisungTableId != "" && gisungTableId != "0") {
                        o.openPringGisungItem();
                    } else {
                        alert('인쇄할 목록을 선택해 주세요.');
                    }
                } else {
                    o.openPringGisungItem();
                }
            });

            $.nfx.click('.btn-contract-manager-add', function (e) {
                const url = `/gisungApi/getGisungContractManagers`;
                var html = "<tr>";
                reqGet(url, function (data) {
                    var parsedData = JSON.parse(data.model)
                    if (parsedData.length > 0) {
                        for (var i = 0; i < parsedData.length; i++) {
                            var item = parsedData[i];
                            if (item.stampPath == null || item.stampPath == "") {
                                item.stampPath = '/dist/img/no_user_photo.png';
                            }
                            if (i > 0 && i % 2 == 0) {
                                html += '</tr>\n';
                                html += '<tr>\n';
                            }
                            if (i % 2 == 0) {
                                html += '<td><input type="hidden" name="company" value="' + item.company + '">' + item.company + '</td>' +
                                    '    <td><input type="hidden" name="name" value="' + item.damName + '">' + item.damName + '</td>';

                                if (item.stampPath == null || item.stampPath == "") {
                                    html += '<td><input type="hidden" name="stampPath" value=""></td>';
                                } else {
                                    html += '<td><input type="hidden" name="stampPath" value="' + item.stampPath + '"><img src="' + item.stampPath + '" style="width:25px;"></td>';
                                }
                            } else {
                                html += '<td><input type="hidden" name="company2" value="' + item.company + '">' + item.company + '</td>' +
                                    '    <td><input type="hidden" name="name2" value="' + item.damName + '">' + item.damName + '</td>';

                                if (item.stampPath == null || item.stampPath == "") {
                                    html += '<td><input type="hidden" name="stampPath2" value=""></td>';
                                } else {
                                    html += '<td><input type="hidden" name="stampPath2" value="' + item.stampPath + '"><img src="' + item.stampPath + '" style="width:25px;"></td>';
                                }
                            }
                        }
                        if (i % 2 == 1) {
                            html += '<td><input type="hidden" name="company2" value=""></td>' +
                                '    <td><input type="hidden" name="name2" value=""></td>';

                            html += '<td><input type="hidden" name="stampPath2" value=""></td>';
                        }
                        html += '</tr>';
                        $("#tbContractor").html(html);
                    }
                }, function (xhr) {
                    alert(xhr.responseJSON.message);
                });
            });

            $(document).on("click", '.btn-addfile-add', function (e) {
                var html = "";
                html += '<dd>\n' +
                    '    <input type="text" name="filename">' +
                    '    <div class="btn-control">' +
                    '         <a href="#none" class="btn-add btn-addfile-add" title="추가"></a>' +
                    '         <a href="#none" class="btn-remove btn-addfile-del" title="삭제"></a>' +
                    '    </div>\n' +
                    '</dd>';
                $("#tbAddfile").append(html);
            });

            $(document).on("click", '.btn-addfile-del', function (e) {
                $(this).closest('dd').remove();
            });

            $(document).on("click", '.btn-addfile2-add', function (e) {
                var html = "";
                html += '<tr>\n' +
                    '    <td><input type="text" name="filename"></td>' +
                    '    <td><input type="text" name="filename_etc"></td>' +
                    '    <td><div class="btn-control">' +
                    '         <a href="#none" class="btn-add btn-addfile2-add" title="추가"></a>' +
                    '         <a href="#none" class="btn-remove btn-addfile2-del" title="삭제"></a>' +
                    '    </div></td>\n' +
                    '</tr>';
                $("#tbAddfile").append(html);
            });

            $(document).on("click", '.btn-addfile2-del', function (e) {
                $(this).closest('tr').remove();
            });

            $.nfx.click('#btnReportAdd', function () {
                const params = {};
                const callback = function (data) {
                    $('#modalAddGisungReport').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/gisung/addGisungReport?id=${gisungId}`);
            });

            $.nfx.click('.openUpdateModal', function () {
                const params = {};
                const callback = function (data) {
                    $('#modalUpdateGisungReport').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/gisung/updateGisungReport?id=${$(this).data('gisung-report-id')}`);
            });

            $.nfx.click('#btnDownloadPRINT', function () {
                if (documentNo == 9) {
                    var gisungCoverId = $("input[name=gisungCoverId]").val();
                    if (gisungCoverId != "" && gisungCoverId != "0") {
                        //modalShowAndDraggable('#modalPrintGisungItem');
                        var gisungCoverId = $("input[name=gisungCoverId]").val();
                        reqGet(`/gisung/modalGisungItem?id=${gisungId}&no=${documentNo}&coverTableId=${gisungCoverId}`, function (data) {
                            $('#modalPrintGisungItem').find('.popup-con').html(data);
                            $('#modalPrintGisungItem').show();
                        });
                    } else {
                        alert('인쇄할 표지를 선택해 주세요.');
                    }
                } else if (documentNo == 10) {
                    var gisungTableId = $("input[name=gisungTableId]").val();
                    if (gisungTableId != "" && gisungTableId != "0") {
                        //modalShowAndDraggable('#modalPrintGisungItem');
                        reqGet(`/gisung/modalGisungItem?id=${gisungId}&no=${documentNo}&coverTableId=${gisungTableId}`, function (data) {
                            $('#modalPrintGisungItem').find('.popup-con').html(data);
                            $('#modalPrintGisungItem').show();
                        });
                    } else {
                        alert('인쇄할 목록을 선택해 주세요.');
                    }
                } else {
                    //modalShowAndDraggable('#modalPrintGisungItem');
                    reqGet(`/gisung/modalGisungItem?id=${gisungId}&no=${documentNo}&coverTableId=0`, function (data) {
                        $('#modalPrintGisungItem').find('.popup-con').html(data);
                    });
                }
            });

            // tab menu
            $(document).on('click', '.btn-document-tab', function (e) {
                var tabNo = $(this).index();
                if (tabNo == 0) {
                    $(".tb-tab .btn-document-tab").removeClass("on");
                    $(".tb-tab .btn-document-tab").eq(0).addClass("on");
                    $("#tbTab1").show();
                    $("#tbTab2").hide();
                } else {
                    $(".tb-tab .btn-document-tab").removeClass("on");
                    $(".tb-tab .btn-document-tab").eq(1).addClass("on");
                    $("#tbTab1").hide();
                    $("#tbTab2").show();
                }
            });

            $(document).on('click', '.btnCoverModify', function() {
                console.log("btnCoverModify1");
                o.getGisungCover($(this).data('id'));
            });

            $(document).on('click', '.btnCoverDelete', function() {
                if (confirm(confirmGisungCoverDeleteDocument)) {
                    o.deleteGisungCover($(this));
                }
            });

            $(document).on('click', '.btnTableModify', function() {
                console.log("btnCoverModify1Table");
                o.getGisungTable($(this).data('id'));
            });

            $(document).on('click', '.btnTableDelete', function() {
                if (confirm(confirmGisungTableDeleteDocument)) {
                    o.deleteGisungTable($(this));
                }
            });

            $(document).on('click', '.download-attach-file', function(e) {
                e.preventDefault();
                executeFileDownloadModal($(this).data("file-id"), $(this).data("file-upload-type"));
            });

            $(document).on('click', '#btnDeleteGisungReport', function() {
                if (confirm(confirmGisungReportDelete)) {
                    o.deleteGisungReport($(this));
                }
            });
        },

        openPringGisungItem: function() {
            console.log("documentNo : " + documentNo);
            if (documentNo == 9) {
                var gisungCoverId = $("input[name=gisungCoverId]").val();
                reqGet(`/gisung/modalGisungItem?id=${gisungId}&no=${documentNo}&coverTableId=${gisungCoverId}`, function (data) {
                    $('#modalPrintGisungItem').find('.popup-con').html(data);
                });
            } else if (documentNo == 10) {
                var gisungTableId = $("input[name=gisungTableId]").val();
                reqGet(`/gisung/modalGisungItem?id=${gisungId}&no=${documentNo}&coverTableId=${gisungTableId}`, function (data) {
                    $('#modalPrintGisungItem').find('.popup-con').html(data);
                });
            } else {
                reqGet(`/gisung/modalGisungItem?id=${gisungId}&no=${documentNo}&coverTableId=0`, function (data) {
                    $('#modalPrintGisungItem').find('.popup-con').html(data);
                });
            }
        },

        addGisungItem: function() {
            reqPostJSON('/gisungApi/postGisungItem'
                , o.setSendData()
                , function (data) {
                    if (data.result) {
                        location.href = "/gisung/gisungPrintList?id="+data.returnId;
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

        updateGisungItem: function() {
            reqPutJSON('/gisungApi/putGisungItem'
                , o.setSendData()
                , function (data) {
                    if (data.result) {
                        location.href = "/gisung/gisungPrintList?id="+data.returnId;
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

        /**
         * 표지
         */
        getGisungCover: function(id) {
            console.log("btnCoverModify" + id);
            reqGet('/gisungApi/getGisungCover?id='+id,
                function (data) {
                    if (data.result) {
                        const model = JSON.parse(data.model);
                        $("#gisungCoverId").val(model.id);
                        $("#title").val(model.title);
                        $("#sub_title").val(model.subTitle);
                        $("#date").val(model.date);
                        $("#project_name").val(model.projectName);
                        console.log(model);
                    }
                },
                function (xhr) {
                    alert(xhr.responseJSON.message);
                }
            )
        },

        deleteGisungCover: function(obj) {
            reqDelete(`/gisungApi/deleteGisungCover?id=${obj.data('id')}`,
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        //toastr.success(data.message);
                        location.href = "/gisung/gisungPrintForm?id="+gisungId+"&no="+documentNo;
                    }
                },
                function (xhr) {
                    alert(xhr.responseJSON.message);
                }
            )
        },

        addGisungCover: function() {
            reqPostJSON('/gisungApi/postGisungCover'
                , o.setSendData()
                , function (data) {
                    if (data.result) {
                        //reloadComponent("/gisung/gisungListCardBody", "#divCardBody", "");
                        location.href = "/gisung/gisungPrintForm?id="+gisungId+"&no="+documentNo;
                        alert(data.message);
                    } else {
                        alert(data.message);
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            );
        },

        updateGisungCover: function() {
            reqPutJSON('/gisungApi/putGisungCover'
                , o.setSendData()
                , function (data) {
                    if (data.result) {
                        location.href = "/gisung/gisungPrintForm?id="+gisungId+"&no="+documentNo;
                    } else {
                        alert(data.message);
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            );
        },
        /**
         * 표지 끝
         */

        /**
         * 목록
         */
        getGisungTable: function(id) {  // 목록
            console.log("btnCoverModifyTable" + id);
            reqGet('/gisungApi/getGisungTable?id='+id,
                function (data) {
                    if (data.result) {
                        const model = JSON.parse(data.model);
                        $("#gisungTableId").val(model.id);
                        var contents = model.contents;
                        if (contents != "") {
                            var contentsArr = contents.split("▥");
                            const table = $("#tbAddTable");
                            let inputContents = table.find('input[name="contents"]');
                            for (var i = 0; i < contentsArr.length; i++) {
                                inputContents.eq(i).val(contentsArr[i]);
                            }
                        }
                        console.log(model.contents);
                    }
                },
                function (xhr) {
                    alert(xhr.responseJSON.message);
                }
            )
        },

        deleteGisungTable: function(obj) {
            reqDelete(`/gisungApi/deleteGisungTable?id=${obj.data('id')}`,
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        location.href = "/gisung/gisungPrintForm?id="+gisungId+"&no="+documentNo;
                    }
                },
                function (xhr) {
                    alert(xhr.responseJSON.message);
                }
            )
        },

        addGisungTable: function() {
            reqPostJSON('/gisungApi/postGisungTable'
                , o.setSendData()
                , function (data) {
                    if (data.result) {
                        location.href = "/gisung/gisungPrintForm?id="+gisungId+"&no="+documentNo;
                        //toastr.success(data.message);
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            );
        },

        updateGisungTable: function() {
            reqPutJSON('/gisungApi/putGisungTable'
                , o.setSendData()
                , function (data) {
                    if (data.result) {
                        location.href = "/gisung/gisungPrintForm?id="+gisungId+"&no="+documentNo;
                        //toastr.success(data.message);
                    }
                }
                , function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                }
            );
        },

        deleteGisungReport: function(obj) {
            reqDelete(`/gisungReportApi/deleteGisungReport?id=${obj.data('gisung-report-id')}`,
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        location.href = "/gisung/gisungPrintForm?id="+gisungId+"&no="+documentNo;
                    }
                },
                function (xhr) {
                    alert(xhr.responseJSON.message);
                }
            )
        },
        /**
         * 목록 끝
         */

        getSearchCondition: function () {
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

        resetAndSearch: function() {
            o.resetSearchCondition();
            o.reloadCardBody();
        },

        reloadCardBody: function () {
            reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
        },

        setSendData: function() {
            console.log("setSendData documentNo : " + documentNo);
            if (documentNo == 1) {
                return JSON.stringify({
                    "id": gisungItemId,
                    "title": $("#title").val(),
                    "item01": $("#item01").val().replaceAll(",", "") + "",
                    "item02": $("#item02").val().replaceAll(",", "") + "",
                    "item03": $("#item03_year").val() + "-" + o.addZero($("#item03_month").val(), 2) + "-" + o.addZero($("#item03_day").val(), 2),
                    "item04": $("#item04_year").val() + "-" + o.addZero($("#item04_month").val(), 2) + "-" + o.addZero($("#item04_day").val(), 2),
                    "item05": $("#item05_year").val() + "-" + o.addZero($("#item05_month").val(), 2) + "-" + o.addZero($("#item05_day").val(), 2),
                    "item06": $("#item06_year").val() + "-" + o.addZero($("#item06_month").val(), 2) + "-" + o.addZero($("#item06_day").val(), 2),
                    "item07": $("#item07").val(),
                    "item08": $("#item08_year").val() + "-" + o.addZero($("#item08_month").val(), 2) + "-" + o.addZero($("#item08_day").val(), 2),
                    "item09": $("#item09").val(),
                    "item10": $("#item10").val(),
                    "item11": $("#item11").val(),
                    "item12": $("#item12").val(),
                    "item13": $("#item13").val(),
                    "contractorData": o.getContractorValues2(),
                    "filenameList": o.getAddFilenameValues(),
                    "data": [],
                    "gisungId": gisungId,
                    "documentNo": documentNo
                });
            } else if (documentNo == 2) {
                return JSON.stringify({
                    "id": gisungItemId,
                    "title": $("#title").val(),
                    "item01": $("#item01_year").val() + "-" + o.addZero($("#item01_month").val(), 2) + "-" + o.addZero($("#item01_day").val(), 2),
                    "item02": $("#item02").val(),
                    "item03": $("#item03").val().replaceAll(",", "") + "",
                    "item04": $("#item04_year").val() + "-" + o.addZero($("#item04_month").val(), 2) + "-" + o.addZero($("#item04_day").val(), 2),
                    "item05": $("#item05_year").val() + "-" + o.addZero($("#item05_month").val(), 2) + "-" + o.addZero($("#item05_day").val(), 2),
                    "item06": $("#item06_year").val() + "-" + o.addZero($("#item06_month").val(), 2) + "-" + o.addZero($("#item06_day").val(), 2),
                    "item07": $("#item07_year").val() + "-" + o.addZero($("#item07_month").val(), 2) + "-" + o.addZero($("#item07_day").val(), 2),
                    "item08": $("#item08_year").val() + "-" + o.addZero($("#item08_month").val(), 2) + "-" + o.addZero($("#item08_day").val(), 2),
                    "item09": $("#item09").val(),
                    "item10": $("#item10").val(),
                    "item11": $("#item11").val(),
                    "item12": $("#item12").val(),
                    "item13": $("#item13").val(),
                    "contractorData": o.getContractorValues2(),
                    "filenameList": o.getAddFilenameValues2(),
                    "data": [],
                    "gisungId": gisungId,
                    "documentNo": documentNo
                });
            } else if (documentNo == 3) {
                return JSON.stringify({
                    "id": gisungItemId,
                    "title": $("#title").val(),
                    "item01": $("#item01").val().replaceAll(",", "") + "",
                    "item02": $("#item02").val().replaceAll(",", "") + "",
                    "item03": $("#item03_year").val() + "-" + o.addZero($("#item03_month").val(), 2) + "-" + o.addZero($("#item03_day").val(), 2),
                    "item04": $("#item04_year").val() + "-" + o.addZero($("#item04_month").val(), 2) + "-" + o.addZero($("#item04_day").val(), 2),
                    "item05": $("#item05_year").val() + "-" + o.addZero($("#item05_month").val(), 2) + "-" + o.addZero($("#item05_day").val(), 2),
                    "item06": $("#item06_year").val() + "-" + o.addZero($("#item06_month").val(), 2) + "-" + o.addZero($("#item06_day").val(), 2),
                    "item07": $("#item07").val(),
                    "item08": $("#item08").val(),
                    "item09": $("#item09_year").val() + "-" + o.addZero($("#item09_month").val(), 2) + "-" + o.addZero($("#item09_day").val(), 2),
                    "item10": $("#item10").val(),
                    "item11": $("#item11").val(),
                    "item12": $("#item12").val(),
                    "item13": $("#item13").val(),
                    "contractorData": o.getContractorValues2(),
                    "filenameList": o.getAddFilenameValues(),
                    "data": [],
                    "gisungId": gisungId,
                    "documentNo": documentNo
                });
            } else if (documentNo == 4) {
                return JSON.stringify({
                    "id": gisungItemId,
                    "title": $("#title").val(),
                    "item01": $("#item01_year").val() + "-" + o.addZero($("#item01_month").val(), 2) + "-" + o.addZero($("#item01_day").val(), 2),
                    "item02": $("#item02").val().replaceAll(",", "") + "",
                    "item03": $("#item03").val(),
                    "item04": $("#item04_year").val() + "-" + o.addZero($("#item04_month").val(), 2) + "-" + o.addZero($("#item04_day").val(), 2),
                    "item05": $("#item05_year").val() + "-" + o.addZero($("#item05_month").val(), 2) + "-" + o.addZero($("#item05_day").val(), 2),
                    "item06": $("#item06_year").val() + "-" + o.addZero($("#item06_month").val(), 2) + "-" + o.addZero($("#item06_day").val(), 2),
                    "item07": $("#item07").val().replaceAll(",", "") + "",
                    "item08": $("#item08").val().replaceAll(",", "") + "",
                    "item09": $("#item09").val(),
                    "item10": $("#item10").val(),
                    "item11": $("#item11_year").val() + "-" + o.addZero($("#item11_month").val(), 2) + "-" + o.addZero($("#item11_day").val(), 2),
                    "item12": $("#item12").val(),
                    "item13": $("#item13").val(),
                    "contractorData": o.getContractorValues2(),
                    "filenameList": o.getAddFilenameValues2(),
                    "data": [],
                    "gisungId": gisungId,
                    "documentNo": documentNo
                });
            } else if (documentNo == 5 || documentNo == 6) {
                return JSON.stringify({
                    "id": gisungItemId,
                    "title": $("#title").val(),
                    "item01": $("#item01").val(),
                    "item02": $("#item02").val(),
                    "item03": $("#item03").val(),
                    "item04": $("#item04").val(),
                    "item05": $("#item05").val(),
                    "item06": $("#item06").val(),
                    "item07": $("#item07").val(),
                    "item08": $("#item08").val(),
                    "item09": $("#item09").val(),
                    "item10": $("#item10").val(),
                    "item11": $("#item11").val(),
                    "item12": $("#item12").val(),
                    "item13": $("#item13").val(),
                    "contractorData": [],
                    "filenameList": "",
                    "data": o.getItemValues(),
                    "gisungId": gisungId,
                    "documentNo": documentNo
                });
            } else if (documentNo == 9) {
                let gisungCoverId = $("input[name=gisungCoverId]").val();
                return JSON.stringify({
                    "id": gisungCoverId,
                    "title": $("#title").val(),
                    "subTitle": $("#sub_title").val(),
                    "date": $("#date").val(),
                    "projectName": $("#project_name").val(),
                    "gisungId": gisungId
                });
            } else if (documentNo == 10) {
                let gisungTableId = $("input[name=gisungTableId]").val();
                return JSON.stringify({
                    "id": gisungTableId,
                    "contents": o.getAddTableValues(),
                    "gisungId": gisungId
                });
            } else {
                return JSON.stringify({
                    "id": gisungItemId,
                    "title": $("#title").val(),
                    "item01": $("#item01").val(),
                    "item02": $("#item02").val(),
                    "item03": $("#item03").val(),
                    "item04": $("#item04").val(),
                    "item05": $("#item05").val(),
                    "item06": $("#item06").val(),
                    "item07": $("#item07").val(),
                    "item08": $("#item08").val(),
                    "item09": $("#item09").val(),
                    "item10": $("#item10").val(),
                    "item11": $("#item11").val(),
                    "item12": $("#item12").val(),
                    "item13": $("#item13").val(),
                    "contractorData": [],
                    "filenameList": "",
                    "data": [],
                    "gisungId": gisungId,
                    "documentNo": documentNo
                });
            }
        },

        addZero: function(str, max) {
            if (str.length < max) {
                for (let i = 0; i < max - str.length; i++) {
                    str = "0" + str;
                }
            }
            return str;
        },

        getItemValues: function() {   // 집계표 데이터 세팅
            const val = [];
            $("div.card-body").map(function (i, el) {
                const table = $(el).find('table[name="tblGisung1"]');
                const names = table.find('input[name="itemDetail01"]');
                let itemDetail01 = table.find('input[name="itemDetail01"]');
                let itemDetail02 = table.find('input[name="itemDetail02"]');
                let itemDetail03 = table.find('input[name="itemDetail03"]');
                let itemDetail04 = table.find('input[name="itemDetail04"]');
                let itemDetail05 = table.find('input[name="itemDetail05"]');
                let itemDetail06 = table.find('input[name="itemDetail06"]');
                let netCheck = table.find('input[name="netCheck"]');
                let gtype = table.find('input[name="gtype"]');
                for (let i=0; i<itemDetail01.length; i++) {
                    val.push({
                        itemDetail01: itemDetail01.eq(i).val() + "",
                        itemDetail02: $.nfx.uncomma(itemDetail02.eq(i).val())+ "",
                        itemDetail03: $.nfx.uncomma(itemDetail03.eq(i).val()) + "",
                        itemDetail04: $.nfx.uncomma(itemDetail04.eq(i).val()) + "",
                        itemDetail05: $.nfx.uncomma(itemDetail05.eq(i).val()) + "",
                        itemDetail06: $.nfx.uncomma(itemDetail06.eq(i).val()) + "",
                        netCheck: netCheck.eq(i).val() + "",
                        gtype: gtype.eq(i).val() + "",
                    });
                }
                const table2 = $(el).find('table[name="tblGisung2"]');
                itemDetail01 = table2.find('input[name="itemDetail01"]');
                itemDetail02 = table2.find('input[name="itemDetail02"]');
                itemDetail03 = table2.find('input[name="itemDetail03"]');
                itemDetail04 = table2.find('input[name="itemDetail04"]');
                itemDetail05 = table2.find('input[name="itemDetail05"]');
                itemDetail06 = table2.find('input[name="itemDetail06"]');
                netCheck = table2.find('input[name="netCheck"]');
                gtype = table2.find('input[name="gtype"]');
                for (let i=0; i<gtype.length; i++) {
                    val.push({
                        itemDetail01: itemDetail01.eq(i).val() + "",
                        itemDetail02: $.nfx.uncomma(itemDetail02.eq(i).val()) + "",
                        itemDetail03: $.nfx.uncomma(itemDetail03.eq(i).val()) + "",
                        itemDetail04: $.nfx.uncomma(itemDetail04.eq(i).val()) + "",
                        itemDetail05: $.nfx.uncomma(itemDetail05.eq(i).val()) + "",
                        itemDetail06: $.nfx.uncomma(itemDetail06.eq(i).val()) + "",
                        netCheck: netCheck.eq(i).val() + "",
                        gtype: gtype.eq(i).val() + "",
                    });
                }
            });
            return val;
        },

        setCalculateCost: function(obj) {
            //$("div.card-body").map(function (i, el) {
            const table = $('#tbTab1');
            //const table = $(el).find('table[name="tblGisung1"]');
            let itemDetail01 = table.find('input[name="itemDetail01"]');
            let itemDetail02 = table.find('input[name="itemDetail02"]');
            let itemDetail03 = table.find('input[name="itemDetail03"]');
            let itemDetail04 = table.find('input[name="itemDetail04"]');
            let itemDetail05 = table.find('input[name="itemDetail05"]');
            let itemDetail06 = table.find('input[name="itemDetail06"]');
            var sumWorkAmount = 0;
            var sumWorkCostPrevPaidCost = 0;
            var sumWorkCostPaidCost = 0;
            var sumWorkCostTotalPaidCost = 0;
            var totalCost1 = 0;
            var totalPrevCost1 = 0;
            var totalTodayCost1 = 0;
            var totalSumCost1 = 0;
            var totalCost2 = 0;
            var totalPrevCost2 = 0;
            var totalTodayCost2 = 0;
            var totalSumCost2 = 0;
            var totalCost3 = 0;
            var totalPrevCost3 = 0;
            var totalTodayCost3 = 0;
            var totalSumCost3 = 0;
            var totalCostVat = 0;
            var totalPrevCostVat = 0;
            var totalTodayCostVat = 0;
            var totalSumCostVat = 0;
            var totalCost4 = 0;
            var totalPrevCost4 = 0;
            var totalTodayCost4 = 0;
            var totalSumCost4 = 0;
            var totalCost5 = 0;
            var totalPrevCost5 = 0;
            var totalTodayCost5 = 0;
            var totalSumCost5 = 0;
            let gtype = table.find('input[name="gtype"]');
            for (let i=0; i<itemDetail01.length; i++) {
                var gtypeVal = gtype.eq(i).val();
                var itemPrice02 = $.nfx.uncomma(itemDetail02.eq(i).val());
                var itemPrice03 = $.nfx.uncomma(itemDetail03.eq(i).val());
                var itemPrice04 = $.nfx.uncomma(itemDetail04.eq(i).val());
                var itemPrice05 = $.nfx.uncomma(itemDetail05.eq(i).val());
                if (gtypeVal == "0") {
                    if (i == 0) {
                        sumWorkAmount = $.nfx.uncomma(itemPrice02);
                        sumWorkCostPrevPaidCost = $.nfx.uncomma(itemPrice03);
                        sumWorkCostPaidCost = $.nfx.uncomma(itemPrice04);
                        itemPrice05 = $.nfx.uncomma(itemPrice03) + $.nfx.uncomma(itemPrice04);
                        sumWorkCostTotalPaidCost = $.nfx.uncomma(itemPrice05);
                        itemDetail02.eq(i).val($.nfx.numberWithCommas(itemPrice02));
                        itemDetail03.eq(i).val($.nfx.numberWithCommas(itemPrice03));
                        itemDetail04.eq(i).val($.nfx.numberWithCommas(itemPrice04));
                        itemDetail05.eq(i).val($.nfx.numberWithCommas(itemPrice05));
                    }
                } else if (gtypeVal == "1") {
                    itemPrice05 = $.nfx.uncomma(itemPrice03) + $.nfx.uncomma(itemPrice04);
                    totalCost1 = $.nfx.uncomma(totalCost1) + $.nfx.uncomma(itemPrice02);
                    totalPrevCost1 = $.nfx.uncomma(totalPrevCost1) + $.nfx.uncomma(itemPrice03);
                    totalTodayCost1 = $.nfx.uncomma(totalTodayCost1) + $.nfx.uncomma(itemPrice04);
                    totalSumCost1 = $.nfx.uncomma(totalSumCost1) + $.nfx.uncomma(itemPrice05);
                    itemDetail02.eq(i).val($.nfx.numberWithCommas(itemPrice02));
                    itemDetail03.eq(i).val($.nfx.numberWithCommas(itemPrice03));
                    itemDetail04.eq(i).val($.nfx.numberWithCommas(itemPrice04));
                    itemDetail05.eq(i).val($.nfx.numberWithCommas(itemPrice05));
                } else if (gtypeVal == "A") {
                    totalCost1 = $.nfx.uncomma(sumWorkAmount) + $.nfx.uncomma(totalCost1);
                    totalPrevCost1 = $.nfx.uncomma(sumWorkCostPrevPaidCost) + $.nfx.uncomma(totalPrevCost1);
                    totalTodayCost1 = $.nfx.uncomma(sumWorkCostPaidCost) + $.nfx.uncomma(totalTodayCost1)
                    totalSumCost1 = $.nfx.uncomma(totalPrevCost1) + $.nfx.uncomma(totalTodayCost1);
                    itemPrice05 = totalSumCost1;
                    itemDetail02.eq(i).val($.nfx.numberWithCommas(totalCost1));
                    itemDetail03.eq(i).val($.nfx.numberWithCommas(totalPrevCost1));
                    itemDetail04.eq(i).val($.nfx.numberWithCommas(totalTodayCost1));
                    itemDetail05.eq(i).val($.nfx.numberWithCommas(itemPrice05));
                } else if (gtypeVal == "2") {
                    itemPrice05 = $.nfx.uncomma(itemPrice03) + $.nfx.uncomma(itemPrice04);
                    totalCost2 = $.nfx.uncomma(totalCost2) + $.nfx.uncomma(itemPrice02);
                    totalPrevCost2 = $.nfx.uncomma(totalPrevCost2) + $.nfx.uncomma(itemPrice03);
                    totalTodayCost2 = $.nfx.uncomma(totalTodayCost2) + $.nfx.uncomma(itemPrice04);
                    totalSumCost2 = $.nfx.uncomma(totalSumCost2) + $.nfx.uncomma(itemPrice05);
                    itemDetail02.eq(i).val($.nfx.numberWithCommas(itemPrice02));
                    itemDetail03.eq(i).val($.nfx.numberWithCommas(itemPrice03));
                    itemDetail04.eq(i).val($.nfx.numberWithCommas(itemPrice04));
                    itemDetail05.eq(i).val($.nfx.numberWithCommas(itemPrice05));
                } else if (gtypeVal == "B") {
                    totalCost2 = $.nfx.uncomma(totalCost1) + $.nfx.uncomma(totalCost2);
                    totalPrevCost2 = $.nfx.uncomma(totalPrevCost1) + $.nfx.uncomma(totalPrevCost2);
                    totalTodayCost2 = $.nfx.uncomma(totalTodayCost1) + $.nfx.uncomma(totalTodayCost2)
                    totalSumCost2 = $.nfx.uncomma(totalPrevCost2) + $.nfx.uncomma(totalTodayCost2);
                    itemPrice05 = totalSumCost2;
                    itemDetail02.eq(i).val($.nfx.numberWithCommas(totalCost2));
                    itemDetail03.eq(i).val($.nfx.numberWithCommas(totalPrevCost2));
                    itemDetail04.eq(i).val($.nfx.numberWithCommas(totalTodayCost2));
                    itemDetail05.eq(i).val($.nfx.numberWithCommas(itemPrice05));
                }
                if (itemPrice02 > 0) {
                    itemDetail06.eq(i).val((($.nfx.uncomma(itemPrice05) / $.nfx.uncomma(itemPrice02)) * 100).toFixed(2));
                } else {
                    itemDetail06.eq(i).val(0.00);
                }

            }
            const table2 = $('#tbTab2');
            //const table2 = $(el).find('table[name="tblGisung2"]');
            itemDetail01 = table2.find('input[name="itemDetail01"]');
            itemDetail02 = table2.find('input[name="itemDetail02"]');
            itemDetail03 = table2.find('input[name="itemDetail03"]');
            itemDetail04 = table2.find('input[name="itemDetail04"]');
            itemDetail05 = table2.find('input[name="itemDetail05"]');
            itemDetail06 = table2.find('input[name="itemDetail06"]');
            gtype = table2.find('input[name="gtype"]');
            for (let i=0; i<gtype.length; i++) {
                var gtypeVal = gtype.eq(i).val();
                var itemPrice02 = $.nfx.uncomma(itemDetail02.eq(i).val());
                var itemPrice03 = $.nfx.uncomma(itemDetail03.eq(i).val());
                var itemPrice04 = $.nfx.uncomma(itemDetail04.eq(i).val());
                var itemPrice05 = $.nfx.uncomma(itemDetail05.eq(i).val());
                if (gtypeVal == "3") {
                    itemPrice05 = $.nfx.uncomma(itemPrice03) + $.nfx.uncomma(itemPrice04);
                    totalCost3 = $.nfx.uncomma(totalCost3) + $.nfx.uncomma(itemPrice02);
                    totalPrevCost3 = $.nfx.uncomma(totalPrevCost3) + $.nfx.uncomma(itemPrice03);
                    totalTodayCost3 = $.nfx.uncomma(totalTodayCost3) + $.nfx.uncomma(itemPrice04);
                    totalSumCost3 = $.nfx.uncomma(totalSumCost3) + $.nfx.uncomma(itemPrice05);
                    itemDetail02.eq(i).val($.nfx.numberWithCommas(itemPrice02));
                    itemDetail03.eq(i).val($.nfx.numberWithCommas(itemPrice03));
                    itemDetail04.eq(i).val($.nfx.numberWithCommas(itemPrice04));
                    itemDetail05.eq(i).val($.nfx.numberWithCommas(itemPrice05));
                } else if (gtypeVal == "C") {
                    totalCost3 = $.nfx.uncomma(totalCost2) + $.nfx.uncomma(totalCost3);
                    totalPrevCost3 = $.nfx.uncomma(totalPrevCost2) + $.nfx.uncomma(totalPrevCost3);
                    totalTodayCost3 = $.nfx.uncomma(totalTodayCost2) + $.nfx.uncomma(totalTodayCost3);
                    totalSumCost3 = $.nfx.uncomma(totalPrevCost3) + $.nfx.uncomma(totalTodayCost3);
                    itemPrice05 = totalSumCost3;
                    itemDetail02.eq(i).val($.nfx.numberWithCommas(totalCost3));
                    itemDetail03.eq(i).val($.nfx.numberWithCommas(totalPrevCost3));
                    itemDetail04.eq(i).val($.nfx.numberWithCommas(totalTodayCost3));
                    itemDetail05.eq(i).val($.nfx.numberWithCommas(itemPrice05));
                } else if (gtypeVal == "D") {
                    totalCostVat = parseInt($.nfx.uncomma(totalCost3) * 0.1);
                    totalPrevCostVat = parseInt($.nfx.uncomma(totalPrevCost3) * 0.1);
                    totalTodayCostVat = parseInt($.nfx.uncomma(totalTodayCost3) * 0.1);
                    totalSumCostVat = $.nfx.uncomma(totalPrevCostVat) + $.nfx.uncomma(totalTodayCostVat);
                    itemPrice05 = $.nfx.uncomma(totalSumCostVat);
                    itemDetail02.eq(i).val($.nfx.numberWithCommas(totalCostVat));
                    itemDetail03.eq(i).val($.nfx.numberWithCommas(totalPrevCostVat));
                    itemDetail04.eq(i).val($.nfx.numberWithCommas(totalTodayCostVat));
                    itemDetail05.eq(i).val($.nfx.numberWithCommas(itemPrice05));
                } else if (gtypeVal == "E") {
                    totalCost4 = $.nfx.uncomma(totalCost3) + $.nfx.uncomma(totalCostVat);
                    totalPrevCost4 = $.nfx.uncomma(totalPrevCost3) + $.nfx.uncomma(totalPrevCostVat);
                    totalTodayCost4 = $.nfx.uncomma(totalTodayCost3) + $.nfx.uncomma(totalTodayCostVat);
                    totalSumCost4 = $.nfx.uncomma(totalPrevCost4) + $.nfx.uncomma(totalTodayCost4);
                    itemPrice05 = $.nfx.uncomma(totalSumCost4);
                    itemDetail02.eq(i).val($.nfx.numberWithCommas(totalCost4));
                    itemDetail03.eq(i).val($.nfx.numberWithCommas(totalPrevCost4));
                    itemDetail04.eq(i).val($.nfx.numberWithCommas(totalTodayCost4));
                    itemDetail05.eq(i).val($.nfx.numberWithCommas(itemPrice05));
                } else if (gtypeVal == "4") {
                    itemPrice05 = $.nfx.uncomma(itemPrice03) + $.nfx.uncomma(itemPrice04);
                    totalCost5 = $.nfx.uncomma(totalCost5) + $.nfx.uncomma(itemPrice02);
                    totalPrevCost5 = $.nfx.uncomma(totalPrevCost5) + $.nfx.uncomma(itemPrice03);
                    totalTodayCost5 = $.nfx.uncomma(totalTodayCost5) + $.nfx.uncomma(itemPrice04);
                    totalSumCost5 = $.nfx.uncomma(totalSumCost5) + $.nfx.uncomma(itemPrice05);
                    itemDetail02.eq(i).val($.nfx.numberWithCommas(itemPrice02));
                    itemDetail03.eq(i).val($.nfx.numberWithCommas(itemPrice03));
                    itemDetail04.eq(i).val($.nfx.numberWithCommas(itemPrice04));
                    itemDetail05.eq(i).val($.nfx.numberWithCommas(itemPrice05));
                } else if (gtypeVal == "F") {
                    totalCost5 = $.nfx.uncomma(totalCost4) + $.nfx.uncomma(totalCost5);
                    totalPrevCost5 = $.nfx.uncomma(totalPrevCost4) + $.nfx.uncomma(totalPrevCost5);
                    totalTodayCost5 = $.nfx.uncomma(totalTodayCost4) + $.nfx.uncomma(totalTodayCost5);
                    totalSumCost5 = $.nfx.uncomma(totalPrevCost5) + $.nfx.uncomma(totalTodayCost5);
                    itemPrice05 = $.nfx.uncomma(totalSumCost5);
                    itemDetail02.eq(i).val($.nfx.numberWithCommas(totalCost5));
                    itemDetail03.eq(i).val($.nfx.numberWithCommas(totalPrevCost5));
                    itemDetail04.eq(i).val($.nfx.numberWithCommas(totalTodayCost5));
                    itemDetail05.eq(i).val($.nfx.numberWithCommas(itemPrice05));
                }
                if (itemPrice02 > 0) {
                    itemDetail06.eq(i).val((($.nfx.uncomma(itemPrice05) / $.nfx.uncomma(itemPrice02)) * 100).toFixed(2));
                } else {
                    itemDetail06.eq(i).val(0.00);
                }
            }
            //});
        },

        getContractorValues: function() { // 계약자 데이터 세팅
            const val = [];
            $("div.card-body").map(function (i, el) {
                const table = $(el).find('table[id="tbContractor"]');
                let company = table.find('input[name="company"]');
                let staff = table.find('input[name="staff"]');
                let name = table.find('input[name="name"]');
                let stampPath = table.find('input[name="stampPath"]');
                for (let i=0; i<company.length; i++) {
                    val.push({
                        company: company.eq(i).val() + "",
                        staff: staff.eq(i).val() + "",
                        name: name.eq(i).val() + "",
                        stampPath: stampPath.eq(i).val() + "",
                    });
                }
            });
            return val;
        },

        getContractorValues2: function() { // 계약자 데이터 세팅
            const val = [];
            console.log("getContractorValues2");
            $("div.card-body").map(function (i, el) {
                const table = $(el).find('table[id="tbContractor"]');
                let company = table.find('input[name="company"]');
                let name = table.find('input[name="name"]');
                let stampPath = table.find('input[name="stampPath"]');
                let company2 = table.find('input[name="company2"]');
                let name2 = table.find('input[name="name2"]');
                let stampPath2 = table.find('input[name="stampPath2"]');
                console.log("company.length " + company.length);
                for (let i=0; i<company.length; i++) {
                    val.push({
                        company: company.eq(i).val() + "",
                        name: name.eq(i).val() + "",
                        stampPath: stampPath.eq(i).val() + "",
                        company2: company2.eq(i).val() + "",
                        name2: name2.eq(i).val() + "",
                        stampPath2: stampPath2.eq(i).val() + "",
                    });
                }
            });
            return val;
        },

        getAddFilenameValues: function() {  // 첨부파일 데이터 세팅
            let filename = "";
            //$("div.card-body").map(function (i, el) {
            const table = $('#tbAddfile'); //const table = $(el).find('dl[id="tbAddfile"]');
            let inputfilename = table.find('input[name="filename"]');
            for (let i = 0; i < inputfilename.length; i++) {
                if (i > 0) {
                    filename += "▥";
                }
                filename += inputfilename.eq(i).val();
            }
            //});
            return filename;
        },

        getAddFilenameValues2: function() {
            let filename = "";
            //$("div.card-body").map(function (i, el) {
            const table = $('#tbAddfile');
            let inputfilename = table.find('input[name="filename"]');
            let inputfilenameEtc = table.find('input[name="filename_etc"]');
            for (let i = 0; i < inputfilename.length; i++) {
                if (i > 0) {
                    filename += "▥";
                }
                filename += inputfilename.eq(i).val() + "||" + inputfilenameEtc.eq(i).val();
            }
            //});
            return filename;
        },

        getAddTableValues: function() { // 목록 데이터 세팅
            let contents = "";
            $("div.card-body").map(function (i, el) {
                const table = $(el).find('ul[id="tbAddTable"]');
                let inputContents = table.find('input[name="contents"]');
                for (let i = 0; i < inputContents.length; i++) {
                    if (i > 0) {
                        contents += "▥";
                    }
                    contents += inputContents.eq(i).val();
                }
            });
            return contents;
        },
    };

    $( document ).ready(function() {
        $.extend($.nfx, {
            form: document.FRM,
            MAX_FILE_SIZE: 20 * 1024 * 1024,
            listCardBody: '/gisung/gisungPrintListCardBody',
        });

        o.init();


        /*우측 버튼 클릭시 active*/
        function getTextAfterNoFromURL() {
            const fullPath = window.location.href;
            const noIndex = fullPath.indexOf('no=');

            // 'no='을 찾았으면 그 뒤의 문자열을 반환, 찾지 못했으면 null 반환
            return noIndex !== -1 ? fullPath.slice(noIndex + 3) : null;
        }
        const textAfterNo = getTextAfterNoFromURL();
        $('button[data-document-no="' + textAfterNo + '"]').addClass('on');
        console.log(textAfterNo)


    });

})(jQuery);