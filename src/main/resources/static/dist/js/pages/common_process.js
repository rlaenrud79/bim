$(document).ready(function() {
    var commonProcess = (
        {
            processItem: [],
            processItemTotal: [],
            processSearch: false,
            searchProcessPopup: false,

            init: function () {
                // 공정추가 팝업 검색
                $(document).on("click", '.btn-process-search', function () {
                    reqPostJSON("/costApi/getProcessItemCost", commonProcess.getProcessCostItemSearchCondition('0', '', '000', '', '', '', '', ''), function (data) {
                        //document.getElementById("loader").style.display="none";
                        console.log(data);
                        if (data.result) {
                            commonProcess.processItem = JSON.parse(data.model);
                            commonProcess.setProcessItemTotal();
                            const content = commonProcess.setProgressCate();

                            commonProcess.processSearch = true;

                            $('#taskAddPopup').find('.tree-nav').html(content);
                        }
                    }, function (xhr) {
                        alert($.parseJSON(xhr.responseJSON).error);
                    });
                });

                // 공정추가 팝업 열기
                commonProcess.addClickEventListener('#btnSearchProcess', function () {
                    if (!commonProcess.searchProcessPopup) {
                        commonProcess.searchProcessPopup = true;
                        commonProcess.getProgressPopupData();
                    }
                });

                // 공정추가 팝업 초기화
                $(document).on("click", '.btn-process-search-reset', function () {
                    commonProcess.searchProcessPopup = false;
                    commonProcess.processSearch = false;
                    if (!commonProcess.searchProcessPopup) {
                        commonProcess.searchProcessPopup = true;
                        $("#schTaskName").val("");
                        commonProcess.getProgressPopupData();
                    }
                });

                $(document).on("click", '.tree-nav ul > li > .menu-top > .menu-btn, .tree-nav ul > li > .menu-top > .menu-tit', function (e) {
                    console.log("메뉴 클릭됨");
                    let cateNo = $(this).closest('li').data("cate-no");
                    let cate = $(this).closest('li').data("cate");

                    if (!$(this).closest('li').hasClass('cateRoad')) {
                        if (cateNo) {
                            commonProcess.getProgressData(cateNo, cate);
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

            addClickEventListener: function (id, func) {
                if ($(id).length) {
                    $(id).off();
                    $(id).click(func);
                }
            },

            addEventListener: function (event_name, id, func) {
                if($(id).length) {
                    $(id).off();
                    $(id).on(event_name, func);
                }
            },

            getProcessCostItemSearchCondition: function (cateNo, cate1, cate2, cate3, cate4, cate5, cate6, cate7) {
                return JSON.stringify(
                    {
                        cateNo: cateNo,
                        cate1: cate1,
                        cate2: cate2,
                        cate3: cate3,
                        cate4: cate4,
                        cate5: cate5,
                        cate6: cate6,
                        cate7: cate7,
                        taskName: $("#schTaskName").val()
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
                reqPostJSON("/costApi/getProcessItemCost", commonProcess.getProcessCostItemSearchCondition(1, '', '000', '', '', '', '', ''), function (data) {
                    //document.getElementById("loader").style.display="none";
                    console.log(data);
                    if (data.result) {
                        let content = '<div class="board-search">\n' +
                            '\t\t\t\t<div class="search-box">\n' +
                            '\t\t\t\t\t<input type="text" name="schTaskName" id="schTaskName" placeholder="공정명을 입력하세요.">\n' +
                            '\t\t\t\t\t<a href="#none" class="btn btn-color1 btn-process-search">검색</a>\n' +
                            '\t\t\t\t\t<a href="#none" class="btn btn-color1 btn-process-search-reset">초기화</a>\n' +
                            '\t\t\t\t\t<a href="#none" class="btn btn-color1 btn-process-add">공정추가</a>\n' +
                            '\t\t\t\t</div>\n' +
                            '\t\t\t</div><!--//board-search-->\n' +
                            '<div class="tree-nav">';
                        commonProcess.processItem = JSON.parse(data.model);
                        commonProcess.setProcessItemTotal();
                        const html = commonProcess.setProgressCate();
                        content += html + "</div><button type=\"button\" class=\"close close-btn\">팝업닫기</button>";

                        $('#taskAddPopup').find('.popup-con').html(content);
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
                let cate7 = cate_arr[6];
                let cateCode = commonProcess.getProcessCateCode((cateNo-1), cate);
                let divCateId = "divCate_" + cateCode.replaceAll("|", "");
                if (cateNo == 2) {
                    cate2 = "";
                } else if (cateNo == 3) {
                    cate3 = "";
                } else if (cateNo == 4) {
                    cate4 = "";
                } else if (cateNo == 5) {
                    cate5 = "";
                } else if (cateNo == 6) {
                    cate6 = "";
                } else if (cateNo == 7) {
                    cate7 = "";
                }
                reqPostJSON("/costApi/getProcessItemCost", commonProcess.getProcessCostItemSearchCondition(cateNo, cate1, cate2, cate3, cate4, cate5, cate6, cate7), function (data) {
                    //document.getElementById("loader").style.display="none";
                    console.log(data);
                    if (data.result) {
                        commonProcess.processItem = JSON.parse(data.model);
                        commonProcess.setProcessItemTotal();
                        const html = commonProcess.setProgressCateSub(cateNo);

                        $('#taskAddPopup').find('#'+divCateId+' > ul').html(html);
                    }
                }, function (xhr) {
                    alert($.parseJSON(xhr.responseJSON).error);
                });
            },

            setProgressCate: function() {   // 1depth
                const orignData = commonProcess.processItem;
                let html = "";
                let oldDataCate = "";
                orignData.forEach((m, index) => {
                    console.log("cate1 : " + m.cate1 + ", cate2 : " + m.cate2 + ", cate3 : " + m.cate3 + ", cate4 : " + m.cate4 + ", cate5 : " + m.cate5 + ", cate6 : " + m.cate6 + ", cate7 : " + m.cate7);
                    let dataCate = m.cate1 + "|" + m.cate2 + "|" + m.cate3 + "|" + m.cate4 + "|" + m.cate5 + "|" + m.cate6 + "|" + m.cate7;
                    let itemCheckName = dataCate.replaceAll("|", "");
                    let itemCheck = "itemCheck_1_" +  + m.processItemId;
                    let cateCode = commonProcess.getProcessCateCode(1, dataCate);
                    let divCateId = "divCate_" + cateCode.replaceAll("|", "");
                    let title = m.cate1Name;
                    if (oldDataCate != cateCode) {
                        html += `<section class="depth0">
					<ul><li data-cate-no="2" data-cate="${dataCate}">`;
                        html += commonProcess.getProgressItemDivSub(1, itemCheck, itemCheckName, m, title);
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
                const orignData = commonProcess.processItem;
                let html = "";
                let oldDataCate = "";

                orignData.forEach((m, index) => {
                    console.log("cate1 : " + m.cate1 + ", cate2 : " + m.cate2 + ", cate3 : " + m.cate3 + ", cate4 : " + m.cate4 + ", cate5 : " + m.cate5 + ", cate6 : " + m.cate6 + ", cate7 : " + m.cate7);
                    let dataCate = m.cate1 + "|" + m.cate2 + "|" + m.cate3 + "|" + m.cate4 + "|" + m.cate5 + "|" + m.cate6 + "|" + m.cate7;
                    let itemCheckName = "";
                    let title = m.cate2Name;
                    let cateCode = commonProcess.getProcessCateCode(cateNo, dataCate);
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
                        html += commonProcess.getProgressItemDivSub(cateNo, itemCheck, itemCheckName, m, title);
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

            getProcessCateCode: function (cateNo, cate) {
                let cateCode = "";
                let cate_arr = cate.split("|");
                let cate1 = cate_arr[0];
                let cate2 = cate_arr[1];
                let cate3 = cate_arr[2];
                let cate4 = cate_arr[3];
                let cate5 = cate_arr[4];
                let cate6 = cate_arr[5];
                let cate7 = cate_arr[6];
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
                } else if (cateNo == 7) {
                    cateCode = cate1 + "|" + cate2 + "|" + cate3 + "|" + cate4 + "|" + cate5 + "|" + cate6 + "|" + cate7;
                }

                return cateCode;
            },

            setProcessItemTotal: function() {
                if (commonProcess.processItemTotal.length == 0) {
                    commonProcess.processItemTotal = commonProcess.processItem;
                }
                var obj1 = commonProcess.processItemTotal;
                var obj2 = commonProcess.processItem;

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
                commonProcess.processItemTotal = combinedArray;
            },

            getData: function(url, params, dataType, callback){
                $.ajax({
                    url: url,
                    dataType: dataType,
                    data: params,
                    type: "post",
                    processData: false,
                    contentType: false,
                    success: function(data){
                        callback(data);
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                        alert("["+xhr.status+"] 다시 확인해 주십시오.");
                    }
                });
            }
        }
    );

    commonProcess.init();
});
