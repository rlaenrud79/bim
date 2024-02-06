(function ($) {
    var o = {

        //processItem: [],
        //processItemTotal: [],
        currentProcessItemIndex: 0,
        currentProcessItemType: 1,
        jobSheetPreview: "N",
        jobSheetProcessItemId: 0,
        previewFileExtList: ['png', 'jpg', 'jpeg', 'gif', 'jpeg', 'svg', 'pdf'],
        //processSearch: false,
        //searchProcessPopup: false,

        init: function () {

            $(document).on("click", '.btn-del-sel', function () {
                if ($(".list-item-checkbox:checkbox:checked").length > 0) {
                    if (confirm(selectDeleteJobSheet)) {
                        o.deleteSelJobSheetList();
                    }
                } else {
                    alert(deleteJobSheetSelect);
                }
            });

            // 참조자 창 열기
            $.nfx.click('.openReferenceModal', function () {
                const referenceCount = $(this).data('reference-count');
                if (referenceCount === 0) return false;

                const params = {};
                const param = `jobSheetId=${$(this).data('job-sheet-id')}`;

                const callback = function (data) {
                    $('#modalJobSheetReferences').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/projectModal/jobSheetReferenceList?${param}`);
            });

            // 사용자 검색 모달창 열기
            $.nfx.click('#searchUserDisplayName, #btnSearchUser', function () {
                if ($("#searchUserType").val() == "none") {
                    alert(confirmSearchUser);
                } else {
                    let param = "formElementIdForUserId=searchUserValue";
                    param += "&formElementIdForUserName=searchUserDisplayName";
                    param += "&formElementIdForModal=modalSearchSingleUser";
                    param += "&searchUserFilter=N";
                    const params = {};

                    const callback = function (data) {
                        $('#modalSearchSingleUser').find('.popup-con').html(data);
                        $('#modalSearchSingleUser').show();
                    };

                    $.nfx.ajaxGet(params, 'html', callback, `/commonModal/searchSingleUser?${param}`);
                }
            });

            $.nfx.click('#btnSearchJobSheet', function () {
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
            });

            $(document).on("click", ".sort", function () {
                if ($(this).hasClass("up")) {
                    $("#sortProp").val($(this).data('sort') + "ASC");
                    $(this).removeClass("up").addClass("down");
                } else if ($(this).hasClass("down")) {
                    $("#sortProp").val($(this).data('sort') + "DESC");
                    $(this).removeClass("down").addClass("up");
                } else {
                    $("#sortProp").val($(this).data('sort') + "ASC");
                    $(this).removeClass("up").addClass("down");
                }
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
            });

            $.nfx.on('change', '#pageSize', function () {
                $("#pageNo").val(0);
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
            });

            $(document).on("click", '#btnPagePrevious, a[id^="btnPageNo_"], #btnPageNext', function () {
                $("#pageNo").val($(this).data("page-no"));
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
            });

            $.nfx.click('#btnResetSearchCondition', function () {
                o.resetSearchCondition();
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
            });

            // 작업일지 삭제 버튼
            $(document).on("click", '#listBtnDelete', function () {
                if (confirm(confirmDeleteJobSheet)) {
                    o.deleteJobSheetList($(this));
                }
            });

            $.nfx.click('#btnDelete', function () {
                if (confirm(confirmDeleteJobSheet)) {
                    o.deleteJobSheet($(this));
                }
            });

            // 작업일지 복사 버튼
            $.nfx.click('#btnCopy', function () {
                const jobSheetId = $(this).data('job-sheet-id');
                window.location.href = `/project/jobSheetCopy?id=${jobSheetId}`;
            });

            // 작업일지 등록 버튼
            $.nfx.click('#btnAdd', function () {
                location.href = "/project/jobSheetAdd?id=";
            });

            $.nfx.click('#btnSearchGrantor', function () {
                var modal = $(this).data('modal');
                $(modal).attr('tabindex', 0).show().focus();
                let params = "formElementIdForUserId=jobSheetGrantorId";
                params += "&formElementIdForModal=modalSearchSingleUser";
                params += "&searchUserFilter=Y";

                reqGet(`/commonModal/searchSingleUser?${params}`, function (data) {
                    //console.log(data);
                    $('#modalSearchSingleUser').find('.popup-con').html(data);
                });

                //o.modalShowAndDraggable('#modalSearchJobSheetGrantor');
            });

            $(document).on("click", 'a[id^=\'mBtnSelect_\']', function () {
                reloadComponent("/common/userInfoAtField", "#divUserInfoAtField", o.setJobSheetGrantorSendData());

                $("a[id^='mBtnSelect_']").off("click");
                $("#pSelectJobSheetGrantor").hide();
                //$("#modalSearchJobSheetGrantor").find('.modal-body').html('');

                $('.savedGrantor').hide();
                $('#divUserInfoAtField').show();
            });

            $.nfx.click('#btnSearchReference', function () {
                var modal = $(this).data('modal');
                $(modal).attr('tabindex', 0).show().focus();
                let params = "formElementIdForUserIds=jobSheetReferencesIds";
                params += "&formElementIdForModal=taskRefereePopup";
                params += "&searchUserFilter=Y";
                if (isObject(jobSheetId) && jobSheetId > 0) {
                    params += "&referenceSelected=Y";
                    params += "&jobSheetId=" + jobSheetId;
                } else {
                    params += "&referenceSelected=N";
                }
                params += "&fixId=" + $("#jobSheetReferencesIds").val();

                reqGet(`/commonModal/searchMultiUser?${params}`, function (data) {
                    $('#taskRefereePopup').find('.popup-con').html(data);
                });
            });

            $(document).on("click", '.btn-process-add', function (e) {
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
                console.log(matchedItems);
                o.setSlider(matchedItems);
            });

            $(document).on("click", '.itemCheck', function (e) {
                const menuName = $(this).attr("data-name");
                $("." + menuName).prop("checked", ($(this).prop("checked")));
            });

            // 스냅샷 팝업 열기
            $(document).on("click", '.btnSelectSnapShot', function (e) {
                let i = $('.btnSelectSnapShot').index(this);
                o.currentProcessItemIndex = i;
                o.popupMySnapShot("jobSheetAdd");
            });

            // 금일진도 수정
            $(document).on("change", 'input[name=todayProgressRate]', function () {
                let i = $('input[name="todayProgressRate"]').index(this);
                //console.log("beforeProgressRate " + $('td[id=beforeProgressRate]').eq(i).text());
                let beforeRate = parseFloat($('td[id=beforeProgressRate]').eq(i).text());
                let todayRate = parseFloat(this.value);
                let val = beforeRate + todayRate;
                if (val > 100) {
                    todayRate = 100 - beforeRate;
                    this.value = todayRate;
                }
                //console.log("todayProgressRate " + this.value + "---" + i + "---" + beforeRate + "---" + todayRate);
                $('td[id=afterProgressRate]').eq(i).text(beforeRate + todayRate)
                $('td[id=afterProgressRate]').eq(i).change();

                // 실시량(원)
                const cost = parseFloat($('input[name=processItemCost]').eq(i).val().replaceAll(",", ""));
                const before = parseInt($('td[id=beforeProgressAmount]').eq(i).text().replaceAll(",", ""));
                let today = parseInt(cost * (todayRate / 100));
                $('td[id=todayProgressAmount]').eq(i).text(numberWithCommas(today))
                $('td[id=afterProgressAmount]').eq(i).text(numberWithCommas(before + today))
            });


            // 공정추가 삭제
            $(document).on("click", '.btnRemoveSlider', function (e) {
                o.removeSlider(this);
            });

            // 인원관리 추가
            $(document).on("click", '.btnSelectWorker', function () {
                let i = $('.btnSelectWorker').index(this);
                o.currentProcessItemIndex = i;
                console.log("popupSelectProcessWorker");
                o.popupSelectProcessWorker();
            });

            // 장비관리 추가
            $(document).on("click", '.btnSelectDevice', function () {
                let i = $('.btnSelectDevice').index(this);
                o.currentProcessItemIndex = i;
                console.log("popupSelectProcessDevice");
                o.popupSelectProcessDevice();
            });

            /*공정추가 토글*/
            $(document).on("click", '.add-task .toggle-btn', function () {
                $(this).closest('.add-task').toggleClass('on');
            });
            $(document).on("click", '.add-task-control .all-open', function () {
                $('.add-task').addClass('on');
                $('.add-task .toggle-btn').addClass('on');
            });
            $(document).on("click", '.add-task-control .all-close', function () {
                $('.add-task').removeClass('on');
                $('.add-task .toggle-btn').removeClass('on');
            });

            /*작업일지 토글*/
            $('.form-toggle-btn').on('click',function(){
                $(this).closest('.form-toggle').toggleClass('on');
            });

            // 아이콘추가 버튼 클릭
            $.nfx.click('.addImageIcon', function () {
                var image = $(this).attr('data-image');
                var id = $(this).attr('data-id');
                if (image) {
                    o.insertImageAtCursorPosition(image, id);
                }
            });

            $.nfx.on("change", "input[name='file']", function () {
                if(!($("input[name='file']")[0].files[0].size < $.nfx.MAX_FILE_SIZE)) { // 10 MB (this size is in bytes)
                    //Prevent default and display error
                    alert("이미지는 10MB 이하의 파일만 업로드 가능합니다.");
                    return;
                }

                o.readURLS(this);
            });

            // 공사일지 임시저장
            $.nfx.click('#btnWriteJobSheet', function (e) {
                o.addJobSheet("WRITING");
            });

            // 공사일지 상신
            $.nfx.click('#btnAddJobSheet', function (e) {
                o.addJobSheet("GOING");
            });

            // 공정 인쇄하기
            $.nfx.click('.btnJobProcessItemPRINT', function (e) {
                o.jobSheetProcessItemId = $(this).attr("data-process-item-id");
                reqGet(`/project/printJobSheetProcessItem?id=${o.jobSheetProcessItemId}`, function (data) {
                    $('#modalPrintJobSheetProcessItem').find('.popup-con').html(data);
                });
            });

            // 인원관리 보기 팝업
            $.nfx.click('.btnWorkerTable', function () {
                o.jobSheetProcessItemId = $(this).attr("data-process-item-id");
                console.log("popupSelectProcessWorker");
                o.popupProcessWorkerTable();
            });

            // 장비관리 보기 팝업
            $.nfx.click('.btnDeviceTable', function () {
                o.jobSheetProcessItemId = $(this).attr("data-process-item-id");
                console.log("popupSelectProcessDevice");
                o.popupProcessDeviceTable();
            });

            // 작업일지 인쇄하기
            $.nfx.click('#btnDownloadPRINT', function (e) {
                //const jobSheetId = $("#jobSheetId").val();
                reqGet(`/project/printJobSheetDetail?id=${jobSheetId}&preview=`, function (data) {
                    $('#modalPrintJobSheetDetail').find('.popup-con').html(data);
                });
            });

            // 목록보기
            $.nfx.click('#btnMoveListPage', function () {
                o.moveListPage(true);
            });

            // 수정 페이지 이동
            $.nfx.click('#btnMoveEditPage', function () {
                window.location.href = `/project/jobSheetAdd?id=${jobSheetId}`;
            });

            // 미리보기 시작
            $.nfx.click('#btnPreview', function () {
                o.makePreview();
            });

            // 이전내역 전체 불러오기
            $.nfx.click('#btnPrevJopSheet', function () {
                window.location.href = `/project/prevJobSheet`;
            });

            $.nfx.on("change", "#allCheck", function () {
                executeAllCheck($(this), $(this), ".itemCheck");
            });

            // 공정 이전내역 불러오기
            $(document).on("click", '.btnAddPrevJobSheetItem', function () {
                let itemBun = $(this).attr("data-item-bun");
                let processItemId = $(this).attr("data-process-item-id");
                reqGet('/project/addPrevJobSheetItem?itemBun='+itemBun+'&id='+processItemId+'&jobSheetId=0&jobSheetProcessItemId=0', function (data) {
                    if ($.trim(data)) {
                        $('#modalAddPrevJobSheetItem').find('.popup-con').html(data);
                        $('#modalAddPrevJobSheetItem').attr('tabindex', 0).show().focus();
                    } else {
                        alert("등록된 공정이 없습니다.");
                    }
                });
            });

            // 첨부파일 다운로드
            $.nfx.click('.download-attach-file', function (e) {
                e.preventDefault();

                const fileExt = $(this).data('file-ext');
                const filePath = $(this).data('file-path');

                if (o.previewFileExtList.includes(fileExt.toLowerCase())) {
                    window.open(filePath);
                } else {
                    executeFileDownloadModal($(this).data("file-id"), "JOB_SHEET_FILE");
                }
            });

            // 첨부파일 삭제
            $.nfx.click('.btnDeleteFile', function (e) {
                if (confirm(jobSheetConfirmDeleteJobSheetFile)) {
                    o.deleteJobSheetFile($(this));
                }
            });

            // 전체열기
            $.nfx.click('.btn-open-all', function () {
                $(".add-task").addClass("on");
            });

            // 전체닫기
            $.nfx.click('.btn-close-all', function () {
                $(".add-task").removeClass("on");
            });

            // 반려 팝업
            $.nfx.click('#openDenyJobSheetModal', function () {
                reqGet(`/projectModal/denyJobSheet?id=${jobSheetGrantorId}`, function (data) {
                    $('#modalDenyJobSheet').find('.popup-con').html(data);
                });
            });

            // 승인 팝업
            $.nfx.click('#openApproveJobSheetModal', function () {
                reqGet(`/projectModal/approveJobSheet?id=${jobSheetGrantorId}`, function (data) {
                    $('#modalApproveJobSheet').find('.popup-con').html(data);
                });
            });
        },

        modalShowAndDraggable: function (obj) {
            $(obj).modal('show');
            $(obj).draggable({'cancel': '.modal-body'});
        },

        getSearchCondition: function () {
            let param = "";
            param += `page=${parseInt($("#pageNo").val()) || 0}`;
            param += `&size=${parseInt($("#pageSize").val())}`;
            param += `&searchDivisionType=${$("#searchDivisionType").val()}`;
            param += `&searchDivisionValue=${$("#searchDivisionValue").val()}`;
            param += `&searchUserType=${$("#searchUserType").val()}`;
            param += `&searchUserValue=${$("#searchUserValue").val()}`;
            param += `&searchUserDisplayName=${$("#searchUserDisplayName").val()}`
            param += `&startDateString=${$("#startDate").val()}`;
            param += `&endDateString=${$("#endDate").val()}`;
            param += `&SortProp=${$("#sortProp").val()}`;
            return param;
        },

        resetSearchCondition: function () {
            $("#searchDivisionType").val('none');
            $("#searchDivisionValue").val('');
            $("#searchUserType").val('none');
            $("#searchUserValue").val(0);
            $("#searchUserDisplayName").val('');
            $("#startDate").val('');
            $("#endDate").val('');
            $("#sortProp").val('');
            $("#pageNo").val(0);
            $("#pageSize").val(20);
        },

        getSearchConditionList: function () {
            let param = "";
            param += `page=${parseInt($("#pageNo").val()) || 0}`;
            param += `&size=${parseInt($("#pageSize").val())}`;
            param += `&searchDivisionType=${$("#searchDivisionType").val()}`;
            param += `&searchDivisionValue=${$("#searchDivisionValue").val()}`;
            param += `&searchUserType=${$("#searchUserType").val()}`;
            param += `&searchUserValue=${$("#searchUserValue").val()}`;
            param += `&searchUserDisplayName=${$("#searchUserDisplayName").val()}`
            param += `&startDateString=${$("#startDate").val()}`;
            param += `&endDateString=${$("#endDate").val()}`;
            param += `&SortProp=${$("#sortProp").val()}`;
            return param;
        },

        deleteJobSheetList: function (obj) {
            reqPut('/projectApi/deleteJobSheet', {"id": obj.data('job-sheet-id')},
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                            reloadComponent("/project/jobSheetListCardBody", "#divCardBody", o.getSearchCondition());
                    }
                }, function (xhr) {
                    alert(xhr.responseJSON.message);

                });
        },

        deleteSelJobSheetList: function (obj) {
            reqDeleteJSON('/projectApi/deleteSelJobSheet', o.setSendDeleteData(),
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        reloadComponent("/project/jobSheetListCardBody", "#divCardBody", o.getSearchCondition());
                    }
                }, function (xhr) {
                    alert(xhr.responseJSON.message);

                });
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

        setJobSheetGrantorSendData: function () {
            return `&userId=${$("#jobSheetGrantorId").val()}`;
        },

        setSlider: function (taskList) {
            const $taskList = $("#taskList");

            let textHtml = '';
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
                let rate = 0;
                if (parseInt(taskList[i].progressRate) > 0 && parseInt(taskList[i].duration) > 0) {
                    rate = taskList[i].progressRate / taskList[i].duration;
                } else {
                    if (parseInt(taskList[i].duration) > 0) {
                        rate = 100 / taskList[i].duration;
                    }
                }
                console.log("rate " + rate + " taskList[i].progressRate " + taskList[i].progressRate + " taskList[i].duration " + taskList[i].duration);
                var exchangeId_arr = taskList[i].exchangeId.split(":");
                var exchangeId_arr2 = [];
                var exchangeId = "";
                if (exchangeId_arr.length > 0) {
                    exchangeId_arr2 = exchangeId_arr[0].split(".");
                    //exchangeId = exchangeId_arr2[0];
                    if (exchangeId_arr2.length > 1) {
                        exchangeId = exchangeId_arr2[0] + "." + exchangeId_arr2[1];
                    } else {
                        exchangeId = exchangeId_arr[0];
                    }
                } else {
                    exchangeId_arr2 = taskList[i].exchangeId.split(".");
                    //exchangeId = exchangeId_arr2[0];
                    if (exchangeId_arr2.length > 1) {
                        exchangeId = exchangeId_arr2[0] + "." + exchangeId_arr2[1];
                    } else {
                        exchangeId = taskList[i];
                    }
                }
                if (i == 0) {
                    textHtml += '<section class="add-task on" id="jobSheetProcessItemList' + ($(".task-title").length + i) + '">';
                } else {
                    textHtml += '<section class="add-task" id="jobSheetProcessItemList' + ($(".task-title").length + i) + '">';
                }
                textHtml += '<div class="flex">';
                textHtml += '<div class="tit-box flex">';
                textHtml += '<div class="img id="jobSheetSnapShotList' + ($(".task-title").length + i) + '"">';
                textHtml += '  <img src="/dist/img/no_img.jpg" className="" alt="" id="jobSheetSnapShotImg' + ($(".task-title").length + i) + '">';
                textHtml += '</div>';
                textHtml += '<div class="tb-cate">';
                textHtml += '<span>' + taskList[i].cate3Name + '</span>';
                textHtml += '<span>' + taskList[i].cate4Name + '</span>';
                textHtml += '<span>' + taskList[i].taskName + '</span>';
                textHtml += '</div>';
                textHtml += '</div>';
                textHtml += '<div class="task-rate flex">';
                textHtml += '<p>진행률 : <strong>' + rate.toFixed(2) + '%</strong> (일 평균)</p>';
                textHtml += '<div class="btn-box">';
                textHtml += '<div id="jobSheetSnapShotList'+($(".task-title").length + i)+'" class="up-file-list img" style="display:none">';
                textHtml += '  <img src="/dist/img/no_image.png" className="" alt="" id="jobSheetSnapShotImg'+($(".task-title").length + i)+'">';
                textHtml += '</div>';
                textHtml += '<input type="hidden" name="mySnapShotId" value="0">';
                textHtml += '<a href="#none" class="btn-xs btnAddPrevJobSheetItem" data-process-item-id="' + taskList[i].processItemId + '"  data-item-bun="' + ($(".task-title").length + i) + '">이전내역 불러오기</a>';
                textHtml += '<a href="#none" class="btn-xs pop-open-btn btnSelectSnapShot" data-modal="#mySnapShotShare">스냅샷설정</a>';
                textHtml += '<a href="#none" class="btn-xs btnRemoveSlider">삭제</a>';
                textHtml += '</div>';
                textHtml += '<button type="button" class="toggle-btn"></button>';
                textHtml += '</div>';
                textHtml += '</div>';
                textHtml += '<div class="task-con">';
                textHtml += '   <input id="processItemId" name="processItemId" type="hidden" value="' + taskList[i].processItemId + '">';
                textHtml += '   <input id="processItemCost" name="processItemCost" type="hidden" value="' + taskList[i].taskCost + '">';
                textHtml += '   <input id="phasingCode" name="phasingCode" type="hidden" value="' + taskList[i].phasingCode + '">';
                textHtml += '   <input id="taskFullPath" name="taskFullPath" type="hidden" value="' + taskList[i].taskFullPath + '">';
                textHtml += '   <input id="exchangeId" name="exchangeId" type="hidden" value="' + exchangeId + '">';
                textHtml += '<div class="grid">';
                textHtml += '<div class="col4">';
                textHtml += '<div class="table-wrap">';
                textHtml += '<table class="table">';
                textHtml += '<tbody>';
                textHtml += '<tr>';
                textHtml += '<th colspan="3">실시량(원)</th>';
                textHtml += '<th colspan="3">진도(%)</th>';
                textHtml += '</tr>';
                textHtml += '<tr>';
                textHtml += '<td>전일누계</td>';
                textHtml += '<td>금일누계</td>';
                textHtml += '<td>누계</td>';
                textHtml += '<td>전일진도</td>';
                textHtml += '<td>금일진도</td>';
                textHtml += '<td>누계</td>';
                textHtml += '</tr>';
                textHtml += '<tr>';
                textHtml += '<td id="beforeProgressAmount" name="beforeProgressAmount">' + numberWithCommas(taskList[i].progressAmount) + '</td>';
                textHtml += '<td id="todayProgressAmount" name="todayProgressAmount">0</td>';
                textHtml += '<td id="afterProgressAmount" name="afterProgressAmount">' + numberWithCommas(taskList[i].progressAmount) + '</td>';
                textHtml += '<td id="beforeProgressRate" name="beforeProgressRate">' + taskList[i].progressRate + '</td>';
                textHtml += '<td><input type="number" id="todayProgressRate" name="todayProgressRate" min="0" max="100" step="0.01" class="input-box-xs" value="0"></td>';
                textHtml += '<td id="afterProgressRate" name="afterProgressRate">' + taskList[i].progressRate + '</td>';
                textHtml += '</tr>';
                textHtml += '</tbody>';
                textHtml += '</table>';
                textHtml += '</div>';
                textHtml += '</div>';
                textHtml += '<div class="col2">';
                textHtml += '<div class="add-job grid task-option-add">';
                textHtml += '<div class="fm-section col2">';
                textHtml += '<div class="fm-tit">';
                textHtml += '<strong><i class="ico-require"></i>인원관리</strong>';
                textHtml += '</div>';
                textHtml += '<div class="fm-txt">';
                textHtml += '<a href="#none" class="btn btn-color4 pop-open-btn btnSelectWorker" data-modal="#selectWorkList">추가</a>';
                textHtml += '<p class="col6 fm-msg tblProcessWorker"></p>';
                textHtml += '</div>';
                textHtml += '</div>';
                textHtml += '<div class="fm-section col2">';
                textHtml += '<div class="fm-tit">';
                textHtml += '<strong><i class="ico-require"></i>장비관리</strong>';
                textHtml += '</div>';
                textHtml += '<div class="fm-txt">';
                textHtml += '<a href="#none" class="btn btn-color3 pop-open-btn btnSelectDevice" data-modal="#selectDeviceList">추가</a>';
                textHtml += '<p class="col6 fm-msg tblProcessDevice"></p>';
                textHtml += '</div>';
                textHtml += '</div>';
                textHtml += '</div>';
                textHtml += '</div>';
                textHtml += '</div>';
                textHtml += '</div>';
                textHtml += '</section>';

            }
            $taskList.append(textHtml);
            $(".process_close").click();
            //$('.scroll-wrap').overlayScrollbars({});

            //$("#taskAddPopup").hide();
        },

        popupMySnapShot: function (type) {
            //o.modalShowAndDraggable('#mySnapShotShare');
            const $sortedSelector = $("#jobSheetSnapShotList" + o.currentProcessItemIndex).find("a.btn-xs");
            let existSnapShotIds = "";

            $sortedSelector.each(function (index, item) {
                if (existSnapShotIds === "") existSnapShotIds = $(item).data('snap-shot-id');
                else existSnapShotIds += "," + $(item).data('snap-shot-id');
            });

            //reqGet('/commonModal/mySnapShotShare?type=jobSheetAdd&id='+processItemNo+'&existSnapShotIds=${existSnapShotIds}', function (data) {
            //  $('#mySnapShotShare').find('.modal-body').html(data);
            //});
            reqGet(`/commonModal/mySnapShotShare?type=${type}&id=${o.currentProcessItemIndex}&existSnapShotIds=${existSnapShotIds}`, function (data) {
                $('#mySnapShotShare').find('.popup-con').html(data);
            });
        },

        removeSlider: function (obj) {
            var element = obj.parentElement.parentElement.parentElement.parentElement; // 삭제할 요소
            // 요소 삭제
            element.remove();
        },

        popupSelectProcessWorker: function () {
            let params = "formElementIdForModal=selectWorkList";
            params += "&id=" + o.currentProcessItemIndex;
            reqGet(`/projectModal/selectWorkerList?${params}`, function (data) {
                //console.log(data);
                $('#selectWorkList').find('.popup-con').html(data);
            });
        },

        popupSelectProcessDevice: function () {
            let params = "formElementIdForModal=selectDeviceList";
            params += "&id=" + o.currentProcessItemIndex;
            reqGet(`/projectModal/selectDeviceList?${params}`, function (data) {
                //console.log(data);
                $('#selectDeviceList').find('.popup-con').html(data);
            });
        },

        popupProcessWorkerTable: function () {
            let params = "formElementIdForModal=taskMemberPopup";
            params += "&jobSheetId=" + o.jobSheetProcessItemId;
            reqGet(`/projectModal/jobSheetProcessItemWorkerTable?${params}`, function (data) {
                //console.log(data);
                $('#taskMemberPopup').find('.popup-con').html(data);
            });
        },

        popupProcessDeviceTable: function () {
            let params = "formElementIdForModal=taskMachinePopup";
            params += "&jobSheetId=" + o.jobSheetProcessItemId;
            reqGet(`/projectModal/jobSheetProcessItemDeviceTable?${params}`, function (data) {
                //console.log(data);
                $('#taskMachinePopup').find('.popup-con').html(data);
            });
        },

        insertImageAtCursorPosition: function(imageUrl, id) {
            var editor = $('#' + id)[0];
            var selection = window.getSelection();

            if (selection.rangeCount > 0) {
                var range = selection.getRangeAt(0);
                var imgElement = $('<img>').attr('src', imageUrl).attr('alt', '이미지');

                // 특정 div 내에서만 적용
                if (!editor.contains(range.startContainer)) {
                    return;
                }

                // 텍스트 컨텐츠로 이루어진 노드인지 확인
                var isTextNode = range.startContainer.nodeType === Node.TEXT_NODE;

                // 삽입될 위치가 이미지 태그 내부라면 부모 노드로 이동
                var parentNode = range.startContainer.parentNode;
                while (parentNode && parentNode.tagName !== 'DIV') {
                    if (parentNode.tagName === 'IMG') {
                        range.setStartBefore(parentNode);
                        range.setEndAfter(parentNode);
                        break;
                    }
                    parentNode = parentNode.parentNode;
                }

                // 이미지 삽입
                if (isTextNode) {
                    // 텍스트 노드인 경우 분리하여 이미지 삽입
                    var startText = range.startContainer.textContent.slice(0, range.startOffset);
                    var endText = range.startContainer.textContent.slice(range.startOffset);
                    var beforeNode = document.createTextNode(startText);
                    var afterNode = document.createTextNode(endText);

                    range.deleteContents();
                    //range.insertNode(beforeNode);
                    range.collapse(false);
                    range.insertNode(imgElement[0]);
                    //range.insertNode(afterNode);
                    range.collapse(false);
                } else {
                    // 텍스트 노드가 아니라면 이미지 태그 뒤에 삽입
                    range.insertNode(imgElement[0]);
                    range.collapse(false);
                }

                // 커서 이동
                selection.removeAllRanges();
                selection.addRange(range);
                editor.focus();
            }
        },

        getFileCount: function () {
            var max = 0;
            $('.btn-file-del').each(function () {
                no = parseInt($(this).attr('data-no'), 10);
                max = (max < no) ? no : max;
            });

            return max;
        },

        readURLS: function(obj) {
            var form = $.nfx.form;
            var html = "";
            var callback = function (data) {
                switch (data.result) {
                    case "OK":
                        var no = o.getFileCount() + 1;
                        html = '<li><div class="upload-docu"><input type="hidden" name="uploads[]" value="'+ data.file + '#' + data.file_logical + '#">\n' +
                            '<span>'+data.file_logical+'</span>\n' +
                            '<a href="#none" class="btn-delete-ico btn-file-del" data-no="'+no+'" data-idx="" data-filename="'+ data.file +'"><span class="hidden">삭제</span></a>\n' +
                            '</div></li>';
                        $(".div-upload").append(html);
                        break;

                    case "CANCEL":
                        alert(data.msg);
                        break;
                }
                $(obj).val("");
            };
            if (obj.files && obj.files[0]) {
                $.nfx.ajaxFile(new FormData(form), 'json', callback, $.nfx.proc);
            }
        },

        addJobSheet: function(status) {
            o.jobSheetPreview = "N";
            let jobSheetIdVal = $("#jobSheetId").val();
            var date1 = new Date(maxReportDate);
            var date2 = new Date($("#reportDate").val());
            if ($("#reportDate").val() === "") {
                alert(jobSheetAddAlertReportDate);
                return false;
            }

            if (date1 >= date2) {
                alert(jobSheetAddAlertMaxReportDate);
                return false;
            }

            if ($("#title").val() === "") {
                alert(jobSheetAddAlertTitle);
                return false;
            }

            if ($("#jobSheetGrantorId").val() === "" || $("#jobSheetGrantorId").val() === "0") {
                alert(jobSheetAddAlertGrantor);
                return false;
            }

            if ($("#contents").html() === "") {
                alert(jobSheetAddAlertJobSheet);
                return false;
            }

            if (jobSheetIdVal > 0) {
                reqPutJSON('/projectApi/putJobSheet', o.setSendData(status, jobSheetIdVal),
                    function (data) {
                        if (!data.result) {
                            alert(data.message);
                        } else {
                            alert(data.message);
                            startFileUpload();
                            //o.moveListPage(false);
                        }
                    },
                    function (xhr) {
                        alert(xhr.responseJSON.message);
                    }
                );
            } else {
                reqPostJSON('/projectApi/postJobSheet', o.setSendData(status, ''),
                    function (data) {
                        if (!data.result) {
                            alert(data.message);
                        } else {
                            alert(data.message);
                            jobSheetId = data.returnId;
                            startFileUpload();
                        }
                    },
                    function (xhr) {
                        alert(xhr.responseJSON.message);

                    }
                );
                // 등록 후에는 저장된 쿠키 삭제
                //deleteCookie('JobPhasingCodes');
            }
        },

        setSendData: function(status, id) {
            const result = {
                "title": $("#title").val(),
                "contents": $("#contents").html(), //CommonEditor.getInputData(),
                "reportDate": $("#reportDate").val(),
                "location": $("#location").val(),
                "temperatureMax": $("#temperatureMax").val(),
                "temperatureMin": $("#temperatureMin").val(),
                "rainfallAmount": $("#rainfallAmount").val(),
                "snowfallAmount": $("#snowfallAmount").val(),
                "grantorId": $("#jobSheetGrantorId").val(),
                "referencesIds": o.getJobSheetReferencesIds(),
                "mySnapShotIds": id ? o.getSnapShotIds() : o.getMySnapShotIds(),
                "processItemIds": o.getInputValues("processItemId"),
                "phasingCodes": o.getInputValues("phasingCode"),
                "taskFullPaths": o.getInputValues("taskFullPath"),
                "beforeProgressRates": o.getTdValues("beforeProgressRate"),
                "afterProgressRates": o.getTdValues("afterProgressRate"),
                "todayProgressRates": o.getInputValues("todayProgressRate"),
                "beforeProgressAmounts": o.getTdValues("beforeProgressAmount"),
                "afterProgressAmounts": o.getTdValues("afterProgressAmount"),
                "todayProgressAmounts": o.getTdValues("todayProgressAmount"),
                "processItemWorkers": o.getProcessItemValues("tblProcessWorker"),
                "processItemDevices": o.getProcessItemValues("tblProcessDevice"),
                "status": status,
                // 전일공사현황 추가(2023.03.06 이동근)
                //"beforeContents": beforeContentsEditor.getInputData(),
                "todayContents": $("#today_contents").html(),
                "exchangeIds": o.getInputValues("exchangeId"),
                //"mySnapShotIds": getInputValues ("mySnapShotId"),

            }
            if (id) {
                result.id = id
            }
            return JSON.stringify(result);
        },

        makePreview: function() {
            let jobSheetId = $("#jobSheetId").val();
            var date1 = new Date(maxReportDate);
            var date2 = new Date($("#reportDate").val());

            if ($("#reportDate").val() === "") {
                alert(jobSheetAddAlertReportDate);
                return false;
            }

            if (date1 >= date2) {
                alert(jobSheetAddAlertMaxReportDate);
                return false;
            }

            if ($("#title").val() === "") {
                alert(jobSheetAddAlertTitle);
                return false;
            }

            if ($("#jobSheetGrantorId").val() === "" || $("#jobSheetGrantorId").val() === "0") {
                alert(jobSheetAddAlertGrantor);
                return false;
            }

            if ($("#contents").html() === "") {
                alert(jobSheetAddAlertJobSheet);
                return false;
            }

            let status = "WRITING";
            if (jobSheetId > 0) {
                reqPutJSON('/projectApi/putJobSheet', o.setSendData(status, jobSheetId),
                    function (data) {
                        if (!data.result) {
                            alert(data.message);
                        } else {
                            //toastr.success(data.message);
                            o.jobSheetPreview = "Y";
                            //startFileUpload();
                            reqGet('/project/printJobSheetDetail?id='+jobSheetId+'&preview=Y', function (data) {
                                $('#modalPrintJobSheetDetail').find('.popup-con').html(data);
                                $('#modalPrintJobSheetDetail').attr('tabindex',0).show().focus();
                                o.moveListPage(false);
                            });
                        }
                    },
                    function (xhr) {
                        alert(xhr.responseJSON.message);
                    }
                );
            } else {
                reqPostJSON('/projectApi/postJobSheet', o.setSendData(status, ''),
                    function (data) {
                        if (!data.result) {
                            alert(data.message);
                        } else {
                            //toastr.success(data.message);
                            jobSheetId = data.returnId;
                            console.log("jobSheetId2 : " + jobSheetId);
                            o.jobSheetPreview = "Y";
                            $("#jobSheetId").val(jobSheetId);
                            //o.moveListPage(false);
                            //startFileUpload();
                            reqGet('/project/printJobSheetDetail?id='+jobSheetId+'&preview=Y', function (data) {
                                $('#modalPrintJobSheetDetail').find('.popup-con').html(data);
                                $('#modalPrintJobSheetDetail').attr('tabindex',0).show().focus();
                                o.moveListPage(false);
                            });
                        }
                    },
                    function (xhr) {
                        alert(xhr.responseJSON.message);

                    }
                );
                // 등록 후에는 저장된 쿠키 삭제
                //deleteCookie('JobPhasingCodes');
            }
            // 등록 후에는 저장된 쿠키 삭제
            //deleteCookie('JobPhasingCodes');
        },

        getJobSheetReferencesIds: function() {
            if ($("#jobSheetReferencesIds").val().length === 0) return [];
            return $("#jobSheetReferencesIds").val().split(',').map(Number);
        },

        getMySnapShotIds: function() {
            const $snapShotIds = $('button[name="btnSnapShotId"]');
            if ($snapShotIds.length === 0) return [];

            let snapShotIds = [];
            $snapShotIds.each(function (idx, item) {
                snapShotIds.push($(item).data('snap-shot-id'));
            });
            return snapShotIds;
        },

        getSnapShotIds: function() {
            if ($('.savedSnapShot').length > 0) {
                return [];
            }
            return o.getMySnapShotIds();
        },

        getInputValues: function(nm) {
            let vals = [];
            $("input[name=" + nm + "]").map(function (i, el) {
                vals.push(el.value + "");
            });
            return vals;
        },

        getTdValues: function(nm) {
            let vals = [];
            $("#taskList .add-task").each(function(index, element) {
                // 현재 섹션 내의 beforeProgressAmount 값을 가져와 배열에 추가
                let nmVal = $(element).find("#" + nm).text().replaceAll(",", "");
                vals.push(nmVal + "");
            });
            //$("#" + nm).map(function (i, el) {
            //    vals.push($(el).text() + "");
            //});
            return vals;
        },

        getProcessItemValues: function(name) {
            let vals = [];
            $("div.task-option-add").map(function (i, el) {
                const table = $(el).find('p.' + name);
                let names = [];
                let ids = [];
                let values = [];
                if (name == "tblProcessWorker") {
                    names = table.find('input[name=progressWorkerName]');
                    ids = table.find('input[name=progressWorkerId]');
                    values = table.find('input[name=progressWorkerValue]');
                } else if (name == "tblProcessDevice") {
                    names = table.find('input[name=progressDeviceName]');
                    ids = table.find('input[name=progressDeviceId]');
                    values = table.find('input[name=progressDeviceValue]');
                }
                const val = []
                for (let i=0; i<names.length; i++) {
                    val.push({
                        name: names.eq(i).val() + "",
                        id: ids.eq(i).val() + "",
                        value: values.eq(i).val() + "",
                    })
                }
                vals.push(val);
            });
            return vals;
        },

        deleteJobSheet: function() {
            reqPut('/projectApi/deleteJobSheet', {"id": jobSheetId},
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        alert(data.message);
                        o.moveListPage(true);
                    }
                },
                function (xhr) {
                    alert(xhr.responseJSON.message);
                }
            );
        },

        moveListPage: function(condition) {
            if (o.jobSheetPreview == "Y") {
                o.jobSheetPreview = "N";
            } else {
                window.location.href = `/project/jobSheetList?keepCondition=${condition}`;
            }
        },

        deleteJobSheetFile: function(obj) {
            const fileId = obj.data('file-id');

            reqDelete(`/projectApi/deleteJobSheetFile?id=${fileId}`,
                function (data) {
                    if (!data.result) {
                        alert(data.message);
                    } else {
                        obj.parent().remove();
                    }
                },
                function (xhr) {
                    alert(xhr.responseJSON.message);
                }
            );
        },

        searchProcess: {
            processItem: [],
            processItemTotal: [],
            processSearch: false,
            searchProcessPopup: false,
            dataCateCnt: [],

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

                // 공정추가 팝업 검색 리스트
                $(document).on("click", '.btn-process-search-list', function () {
                    let params = "taskName=" + $("#schTaskName").val();
                    reqGet(`/projectModal/processItemSearchList?${params}`, function (data) {
                        //console.log(data);
                        $('#taskAddPopup').find('.popup-con').html(data);
                    });
                });

                // 공정추가 팝업 열기
                $.nfx.click('#btnSearchProcess', function () {
                    console.log("공정추가 팝업 열기");
                    if (!o.searchProcess.searchProcessPopup) {
                        o.searchProcess.searchProcessPopup = true;
                        o.searchProcess.getProgressPopupData();
                    }
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

                $(document).on("click", '.tree-nav ul > li > .menu-top > .menu-btn, .tree-nav ul > li > .menu-top > .menu-tit', function (e) {
                    console.log("메뉴 클릭됨");
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
                /**
                 let params = "cate2=000";
                 reqGet(`/processModal/searchProcess?${params}`, function (data) {
                        $('#taskAddPopup').find('.popup-inner').html(data);
                    });
                 **/
                reqPostJSON("/costApi/getProcessItemCost", o.searchProcess.getProcessCostItemSearchCondition(1, '00000', ''), function (data) {
                    //document.getElementById("loader").style.display="none";
                    console.log(data);
                    if (data.result) {
                        let content =
                            '<div class="tree-nav">';
                        o.searchProcess.processItem = JSON.parse(data.model);
                        o.searchProcess.setProcessItemTotal();
                        const html = o.searchProcess.setProgressCate();
                        content += html + "</div>" +
                            '<div class="btn-box sticky">\n' +
                            '\t\t\t\t<div class="right-box">\n' +
                            '\t\t\t\t\t<input type="text" name="schTaskName" id="schTaskName" placeholder="공정명을 입력하세요.">\n' +
                            '\t\t\t\t\t<a href="#none" class="btn btn btn-process-search">검색</a>\n' +
                            '\t\t\t\t\t<a href="#none" class="btn btn-color2 btn-process-search-reset">초기화</a>\n' +
                            '\t\t\t\t\t<a href="#none" class="btn btn-color1 btn-process-add">공정추가</a>\n' +
                            '\t\t\t\t</div>\n' +
                            '\t\t\t</div><!--//btn-box-->\n'

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
                    //document.getElementById("loader").style.display="none";
                    console.log(data);
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
                    console.log("cate1 : " + m.cate1 + ", cate2 : " + m.cate2 + ", cate3 : " + m.cate3 + ", cate4 : " + m.cate4 + ", cate5 : " + m.cate5 + ", cate6 : " + m.cate6);
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
                    console.log("cate1 : " + m.cate1 + ", cate2 : " + m.cate2 + ", cate3 : " + m.cate3 + ", cate4 : " + m.cate4 + ", cate5 : " + m.cate5 + ", cate6 : " + m.cate6);
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
                    <span class="rate">${numberWithCommas(m.cateProgressRate)}</span>
                </div>
                <div class="check-set">
                    <!--<input type="checkbox" id="${itemCheck}">
                    <label for="${itemCheck}"></label>-->
                </div>`;
                } else {
                    if (m.ganttTaskType == "TASK") {
                        html += `<div class="menu-tit">
                    <span>${title}</span>
                    <span class="code">(${m.phasingCode})</span>
                    <span class="rate">${numberWithCommas(m.cost)}</span>
                </div>
                <div class="check-set">
                    <input type="checkbox" id="${itemCheck}" class="itemCheck" name="item_no[]" data-no="${m.processItemId}" value="${m.processItemId}">
                    <label for="${itemCheck}"></label>
                </div>`;
                    } else {
                        html += `<div class="menu-tit">
                    <span>${title}</span>
                    <span class="code"></span>
                    <span class="rate">${numberWithCommas(m.cateProgressRate)}</span>
                </div>
                <div class="check-set">
                    <!--<input type="checkbox" id="${itemCheck}" class="itemCheck" data-name="${itemCheckName}">
                    <label for="${itemCheck}"></label>-->
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
                                <a href="#none" class="btn btn-color2 btn-process-search-reset">초기화</a>
                                <a href="#none" class="btn btn-color1 btn-process-add">공정추가</a>
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
            MAX_FILE_SIZE: 10 * 1024 * 1024,
            listCardBody: '/project/jobSheetListCardBody',
        });

        o.init();
        o.searchProcess.init();
    });

})(jQuery);