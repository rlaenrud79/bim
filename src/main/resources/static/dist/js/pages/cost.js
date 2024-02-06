(function ($) {
    var o = {

        init: function () {
            // 복합단가 팝업 열기
            $(document).on("click", '.btnCostPopup', function () {
                const title = $(this).data("title");
                const id = $(this).data("id");
                if (id != "") {
                    const params = {};
                    const callback = function (data) {
                        $('#modalCostPopup').find('.popup-tit').html(title);
                        $('#modalCostPopup').find('.popup-con').html(data);
                    };

                    $.nfx.ajaxGet(params, 'html', callback, `/costModal/processItemCost/${id}`);
                }

            });

            $(document).on("click", ".btnWork, .btnSubWork", function () {
                $(".bg-loading").show();
                $("#phasingCode").val("");
                $("#taskName").val("");
                $("#startDate").val("");
                $("#endDate").val("");
                $("#workId").val($(this).data("work-id"));
                $("#subWorkId").val($(this).data("sub-work-id"));
                o.searchProcess.getProcessInfo();
            });

            $(document).on("click", "#btnModelViewer", function () {
                let processItemId = $(this).data("id");
                window.open("/contents/modelingViewByProcessItemId/" + processItemId);
            });

            $(document).on("click", ".btnCostDetail", function () {
                const id = $(this).data("id");
                if (id != "") {
                    const params = {};
                    const callback = function (data) {
                        $('#modalCostDetailPopup').find('.popup-con').html(data);
                    };

                    $.nfx.ajaxGet(params, 'html', callback, `/costModal/costDetail/${id}`);
                }
            });

            $(document).on("change", "#bookmarks", function () {
                let id = $(this).val();

                if (id == "0") {
                    $("#processItemBookmarkCostDetail").html("");
                    return;
                }

                const params = {};
                const callback = function (data) {
                    $('#processItemBookmarkCostDetail').html(data);
                    $(".dual-arr").addClass("on");
                };

                $.nfx.ajaxGet(params, 'html', callback, `/costModal/getProcessItemCostDetail/${id}/C`);

                /**
                reqGet(`/costModal/getProcessItemCostDetail/${id}/C`, function (data) {
                    if (data.result) {
                        let model = JSON.parse(data.model);
                        //gridCostTemplate.data.parse(model);
                        //calculateComplexUnitPrice(gridCostTemplate);
                        //countCheckedBookmarkCostDetail();
                        //$("#processItemBookmarkCostDetail").removeClass("d-none");
                    }
                },
                function (xhr) {
                    alert(xhr.responseJSON.message);
                });
                 **/
            });

            // 엑셀다운
            $.nfx.click('#exportXlsx', function () {
                window.open("/costApi/exportCostXlsx/");
            });

            // 리스트 업로드
            $.nfx.click('#btnUploadCostDetailFile', function () {
                const params = {};
                const callback = function (data) {
                    $('#listExcelUpload').find('.popup-con').html(data);
                };

                $.nfx.ajaxGet(params, 'html', callback, `/cost/addListExcelUpload`);
            });

            $(document).on("click", ".btnPaidCost", function () {
                const id = $(this).data("id");
                if (id != "") {
                    const params = {};
                    const callback = function (data) {
                        $('#paidCost').find('.popup-con').html(data);
                    };

                    $.nfx.ajaxGet(params, 'html', callback, `/costModal/paidCost/${id}`);
                }
            });

            $(document).on("click", "#pageTree", function () {
                location.href = `/cost/index`;
                $("#pageMode").val("tree");
            });

            $(document).on("click", "#pageTable", function () {
                location.href = `/cost/indexTable`;
                $("#pageMode").val("table");
            });

            $(document).on("click", ".btnTableWork, .btnTableSubWork", function () {
                $("#workId").val($(this).data("work-id"));
                $("#subWorkId").val($(this).data("sub-work-id"));
                reloadComponent($.nfx.listCardBody, "#divCardBody", o.getSearchCondition());
                o.getSubWorkList();
            });
        },

        setWorkTabSub: function(orignData) {
            let html = "";
            orignData.forEach((m, index) => {
                html += '<li><button type="button" class="btnSubWork" data-work-id="'+m.upId+'" data-sub-work-id="'+m.id+'">'+m.workNameLocale+'</button></li>';
                console.log("workId : " + m.id + ", workName : " + m.workNameLocale);
            });
            $('#workTabSub').html(html);
            $('#workTabSub').show();
        },

        getSearchCondition: function () {
            let param = "";
            param += `cateNo=0`;
            param += `&upCode=`;
            param += `&phasingCode=${$("#phasingCode").val()}`;
            param += `&taskName=${$("#taskName").val()}`;
            param += `&workId=${$("#workId").val()}`;
            param += `&startDate=${$("#startDate").val()}`;
            param += `&endDate=${$("#endDate").val()}`
            param += `&ganttTaskType=`;
            param += `&pageMode=${$("#pageMode").val()}`;
            return param;
        },

        getSubWorkList: function () {
            reqPostJSON("/costApi/getSubWorkList", o.searchProcess.getProcessCostItemSearchCondition('0', '', ''), function (data) {
                //document.getElementById("loader").style.display="none";
                //console.log(data);
                if (data.result) {
                    o.setWorkTabSub(JSON.parse(data.model));
                }
            }, function (xhr) {
                alert($.parseJSON(xhr.responseJSON).error);
            });
        },

        searchProcess: {
            processItem: [],
            processItemTotal: [],
            processSearch: false,
            searchProcessPopup: false,
            dataCateCnt: [],

            init: function () {
                o.searchProcess.getProcessInfo();

                // 공정 검색
                $(document).on("click", '#btnSearch', function () {
                    $(".bg-loading").show();
                    $("#workId").val(0);
                    let processTotalCnt = 0;
                    reqPostJSON("/costApi/getProcessItemCost", o.searchProcess.getProcessCostItemSearchCondition('0', '', 'TASK'), function (data) {
                        //document.getElementById("loader").style.display="none";
                        //console.log(data);
                        if (data.result) {
                            o.searchProcess.processItem = JSON.parse(data.model);
                            o.searchProcess.setProcessItemTotal();
                            const content = o.searchProcess.setProgressCateSearch();

                            o.searchProcess.processSearch = true;

                            $(".bg-loading").hide();
                            $('#totalPaidCost').html(numberWithCommas(data.value) + "원");
                            $('.tree-body').html(content);

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

                // 공정추가 팝업 초기화
                $(document).on("click", '#btnReset', function () {
                    $("#workId").val(0);
                    $("#phasingCode").val("");
                    $("#taskName").val("");
                    $("#startDate").val("");
                    $("#endDate").val("");
                    o.searchProcess.getProcessInfo();
                });

                $(document).on("click", '.tree-nav ul > li > .menu-top > .menu-btn, .tree-nav ul > li > .menu-top > .menu-tit', function (e) {
                    //console.log("메뉴 클릭됨");
                    let cateNo = $(this).closest('li').data("cate-no");
                    let cate = $(this).closest('li').data("cate");

                    if (!$(this).closest('li').hasClass('cateRoad')) {
                        if (cateNo) {
                            //o.searchProcess.getProgressData(cateNo, cate);
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

            getProcessInfo: function () {
                reqPostJSON("/costApi/getProcessItemCost", o.searchProcess.getProcessCostItemSearchCondition('0', '', ''), function (data) {
                    //document.getElementById("loader").style.display="none";
                    //console.log(data);
                    if (data.result) {
                        console.log(JSON.parse(data.work));
                        o.setWorkTabSub(JSON.parse(data.work));
                        o.searchProcess.processItem = JSON.parse(data.model);
                        o.searchProcess.setProcessItemTotal();
                        const content = o.searchProcess.setProgressCate();

                        o.searchProcess.processSearch = true;

                        $('#totalPaidCost').html(numberWithCommas(data.value) + "원");
                        $('.tree-body').html('<div class="tree-nav tree-open">' + content + '</div>');
                        $(".bg-loading").hide();
                    }
                }, function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                });
            },

            getProcessCostItemSearchCondition: function (cateNo, upCode, ganttTaskType) {
                return JSON.stringify(
                    {
                        cateNo: cateNo,
                        upCode: upCode,
                        phasingCode: $("#phasingCode").val(),
                        taskName: $("#taskName").val(),
                        workId: $("#workId").val(),
                        subWorkId: $("#subWorkId").val(),
                        startDate: $("#startDate").val(),
                        endDate: $("#endDate").val(),
                        ganttTaskType: ganttTaskType + "",
                        pageMode: $("#pageMode").val()
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
                    //console.log(data);
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
                    }
                }, function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                });
            },

            getProgressData: function (cateNo, cate) {    // 2depth 이상 호출
                //console.log("setProgressCate2");
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
                    //console.log(data);
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
                let cateNo = 0;
                let oldCateNo = "";
                let cate1Cnt = 0;
                let cate2Cnt = 0;
                let cate3Cnt = 0;
                let cate4Cnt = 0;
                let cate5Cnt = 0;
                let cate6Cnt = 0;
                orignData.forEach((m, index) => {
                    //console.log("cate1 : " + m.cate1 + ", cate2 : " + m.cate2 + ", cate3 : " + m.cate3 + ", cate4 : " + m.cate4 + ", cate5 : " + m.cate5 + ", cate6 : " + m.cate6);
                    let dataCate = m.cate1 + "|" + m.cate2.trim() + "|" + m.cate3.trim() + "|" + m.cate4.trim() + "|" + m.cate5.trim() + "|" + m.cate6.trim();
                    let itemCheckName = dataCate.replaceAll("|", "");
                    let itemCheck = "itemCheck_1_" +  + m.processItemId;
                    let cateCode = o.searchProcess.getProcessCateCode(1, dataCate);
                    let divCateId = "divCate_" + cateCode.replaceAll("|", "");
                    let title = m.cate1Name;
                    if (m.cate2.trim() == "" && m.cate3.trim() == "" && m.cate4.trim() == "" && m.cate5.trim() == "" && m.cate6.trim() == "") {
                        cateNo = 1;
                        if (oldCateNo != "" && oldCateNo > cateNo) {
                            html += `</li></ul>
                        </section></li></ul>
                        </section></li></ul>
                        </section></li></ul>
                        </section></li></ul>
                        </section>`;
                            cate2Cnt = 0;
                            cate3Cnt = 0;
                            cate4Cnt = 0;
                            cate5Cnt = 0;
                            cate6Cnt = 0;
                        }
                        if (oldCateNo != cateNo) {
                            html += `<section class="depth0">
					        <ul>`;
                        }
					    html += `<li class="active" data-cate-no="2" data-cate="${dataCate}">`;
                        html += o.searchProcess.getProgressItemDivSub(cateNo, itemCheck, itemCheckName, m, title);

                    }
                    if (m.cate2.trim() != "" && m.cate3.trim() == "" && m.cate4.trim() == "" && m.cate5.trim() == "" && m.cate6.trim() == "") {
                        cateNo = 2;
                        if (oldCateNo > cateNo) {
                            if (cate2Cnt == 0) {
                            html += `</li></ul>
                        </section>`;
                            } else {
                                html += `</ul>
                        </section></li></ul>
                        </section></li></ul>
                        </section>`;
                            }
                            cate3Cnt = 0;
                            cate4Cnt = 0;
                            cate5Cnt = 0;
                            cate6Cnt = 0;
                        }
                        //console.log("cate2Cnt : " + cate2Cnt + ", m.cate2Name : " + m.cate2Name);
                        cateCode = o.searchProcess.getProcessCateCode(cateNo, dataCate);
                        divCateId = "divCate_" + cateCode.replaceAll("|", "");
                        if (oldCateNo != cateNo && cate2Cnt == 0) {
                            html += `<section class="depth1" id="${divCateId}">
					        <ul>`;
                            cate2Cnt = parseInt(cate2Cnt) + 1;
                        } else {
                            html += `</li>`;
                        }

                        let title2 = m.cate2Name;
                        let cateNoId = (cateNo+1);
                        let itemCheck = "itemCheck_"+cateNo+"_" +  + m.processItemId;
                        html += `<li class="active" data-cate-no="${cateNoId}" data-cate="${dataCate}">`;
                        html += o.searchProcess.getProgressItemDivSub(cateNo, itemCheck, itemCheckName, m, title2);
                    }

                    if (m.cate2.trim() != "" && m.cate3.trim() != "" && m.cate4.trim() == "" && m.cate5.trim() == "" && m.cate6.trim() == "") {
                        cateNo = 3;
                        if (oldCateNo > cateNo) {
                            if (cate3Cnt == 0) {
                            html += `</li></ul>
                        </section></li></ul>
                        </section>`;
                            } else {
                                html += `</ul>
                        </section></li></ul>
                        </section>`;
                            }
                            cate4Cnt = 0;
                            cate5Cnt = 0;
                            cate6Cnt = 0;
                        }
                        //console.log("cate3Cnt : " + cate3Cnt + ", m.cate3Name : " + m.cate3Name);
                        cateCode = o.searchProcess.getProcessCateCode(cateNo, dataCate);
                        divCateId = "divCate_" + cateCode.replaceAll("|", "");
                        if (oldCateNo != cateNo && cate3Cnt == 0) {
                            html += `<section class="depth2" id="${divCateId}">
					        <ul>`;
                            cate3Cnt = cate3Cnt + 1;
                        } else {
                            html += `</li>`;
                        }

                        let title3 = m.cate3Name;
                        let cateNoId = (cateNo+1);
                        let itemCheck = "itemCheck_"+cateNo+"_" +  + m.processItemId;
                        html += `<li class="active" data-cate-no="${cateNoId}" data-cate="${dataCate}">`;
                        html += o.searchProcess.getProgressItemDivSub(cateNo, itemCheck, itemCheckName, m, title3);
                    }

                    if (m.cate2.trim() != "" && m.cate3.trim() != "" && m.cate4.trim() != "" && m.cate5.trim() == "" && m.cate6.trim() == "") {
                        cateNo = 4;
                        if (oldCateNo > cateNo) {
                            if (cate4Cnt == 0) {
                                html += `</li></ul>
                        </section></li></ul>
                        </section></li></ul>
                        </section>`;
                            } else {
                                html += `</ul>
                        </section>`;
                            }
                            cate5Cnt = 0;
                            cate6Cnt = 0;
                        }
                        cateCode = o.searchProcess.getProcessCateCode(cateNo, dataCate);
                        divCateId = "divCate_" + cateCode.replaceAll("|", "");
                        if (oldCateNo != cateNo && cate4Cnt == 0) {
                            html += `<section class="depth3" id="${divCateId}">
					        <ul>`;
                            cate4Cnt = cate4Cnt + 1;
                        } else {
                            html += `</li>`;
                        }

                        let title4 = m.cate4Name;
                        let cateNoId = (cateNo+1);
                        let itemCheck = "itemCheck_"+cateNo+"_" +  + m.processItemId;
                        html += `<li class="active" data-cate-no="${cateNoId}" data-cate="${dataCate}">`;
                        html += o.searchProcess.getProgressItemDivSub(cateNo, itemCheck, itemCheckName, m, title4);
                    }

                    if (m.cate2.trim() != "" && m.cate3.trim() != "" && m.cate4.trim() != "" && m.cate5.trim() != "" && m.cate6.trim() == "") {
                        cateNo = 5;
                        if (oldCateNo > cateNo) {
                            if (cate5Cnt == 0) {
                                html += `</li></ul>
                        </section></li></ul>
                        </section></li></ul>
                        </section></li></ul>
                        </section>`;
                            }
                            cate6Cnt = 0;
                        }
                        cateCode = o.searchProcess.getProcessCateCode(cateNo, dataCate);
                        divCateId = "divCate_" + cateCode.replaceAll("|", "");
                        if (oldCateNo != cateNo && cate5Cnt == 0) {
                            html += `<section class="depth4" id="${divCateId}">
					        <ul>`;
                            cate5Cnt = cate5Cnt + 1;
                        } else {
                            html += `</li>`;
                        }

                        let title5 = m.cate5Name;
                        let cateNoId = (cateNo+1);
                        let itemCheck = "itemCheck_"+cateNo+"_" +  + m.processItemId;
                        html += `<li class="active" data-cate-no="${cateNoId}" data-cate="${dataCate}">`;
                        html += o.searchProcess.getProgressItemDivSub(cateNo, itemCheck, itemCheckName, m, title5);
                    }

                    if (m.cate2.trim() != "" && m.cate3.trim() != "" && m.cate4.trim() != "" && m.cate5.trim() != "" && m.cate6.trim() != "") {
                        cateNo = 6;
                        if (oldCateNo > cateNo) {
                            html += `</li></ul>
                        </section></li></ul>
                        </section></li></ul>
                        </section></li></ul>
                        </section></li></ul>
                        </section>`;
                        }
                        cateCode = o.searchProcess.getProcessCateCode(cateNo, dataCate);
                        divCateId = "divCate_" + cateCode.replaceAll("|", "");
                        if (oldCateNo != cateNo && cate6Cnt == 0) {
                            html += `<section class="depth5" id="${divCateId}">
					        <ul>`;
                            cate6Cnt = cate6Cnt + 1;
                        } else {
                            html += `</li>`;
                        }

                        let title6 = m.taskName;
                        let cateNoId = (cateNo+1);
                        let itemCheck = "itemCheck_"+cateNo+"_" +  + m.processItemId;
                        html += `<li class="active" data-cate-no="${cateNoId}" data-cate="${dataCate}">`;
                        html += o.searchProcess.getProgressItemDivSub(cateNo, itemCheck, itemCheckName, m, title6);
                    }
                    oldDataCate = cateCode;
                    oldCateNo = cateNo;

                });

                    html += `</li></ul>
                        </section>
                    </li></ul>
				</section></li></ul>
				</section>`;

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
                    <!--<span class="rate">${m.cateProgressRate}%</span>-->
                </div>
                <div class="btn-box">
                </div>`;
                } else {
                    if (m.ganttTaskType == "TASK") {
                        html += `<div class="menu-tit">
                    <span>${title}</span>
                    <span class="code">(${m.phasingCode})</span>
                    <span class="rate">${m.progressRate}%</span>
                </div>
                <div class="btn-box">
                    <a href="#none" class="btn-view-ico pop-open-btn btnCostPopup" data-modal="#modalCostPopup" title="자세히보기" data-id="${m.processItemId}" data-title="${title}"></a>
                </div>`;
                    } else {
                        html += `<div class="menu-tit">
                    <span>${title}</span>
                    <span class="code"></span>
                    <!--<span class="rate">${m.cateProgressRate}%</span>-->
                </div>
                <div class="btn-box">
                    
                </div>`;
                    }
                }
                html += '</div>';
                return html;
            },

            setProgressCateSearch: function() {   // 1depth
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
                        <div class="btn-box">
                            <a href="#none" class="btn-view-ico pop-open-btn btnCostPopup" data-modal="#modalCostPopup" title="자세히보기" data-id="${m.processItemId}" data-title="${m.taskName}"></a>
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
            listCardBody: '/cost/jobSheetListCardBody',
        });

        o.init();
        o.searchProcess.init();
    });

})(jQuery);