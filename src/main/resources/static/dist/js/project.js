let gridProcess;
// 인원/장비 선택 팝업시 현재 아이템 index 저장
let currentProcessItemIndex = 0;
let currentProcessItemType = 1;
let selectProcessItemForm;

function initGrid(task_name, select, phasing_code, progress_rate) {
  gridProcess = new dhx.TreeGrid("gridProcess", {
    columns: [
      {
        id: "processItemId",
        hidden: true,
        header: [{ text: "ID", rowspan: 2 }],
        editable: false
      },
      {
        id: "taskName",
        width: 470,
        editable: false,
        header: [{ text: task_name, align: "center" }],
        // header: [{ text: task_name, align: "center" }, { content: "inputFilter" }],
        mark: function (cell, data) {
          return "process-col-disabled";
        }
      },
      {
        id: "selected",
        width: 60,
        editable: true,
        header: [{ text: select, align: "center" }],
        type: "boolean"
      },
      {
        id: "phasingCode",
        width: 115,
        editable: false,
        header: [{ text: phasing_code, align: "center" }],
        // header: [{ text: phasing_code, align: "center" }, { content: "inputFilter" }],
        mark: function (cell, data) {
          return "process-col-disabled";
        }
      },
      {
        id: "progressRate",
        editable: false,
        header: [{ text: progress_rate, align: "center" }], type: "number", format: "#.00",
        template: function(i){ if (isNaN(i)){ return `0.00%`;	} else{	return `${i}%`;	}},
        mark: function (cell, data) {
          return "process-col-disabled";
        }
      },
    ],
    autowidth: true,
    // adjust      : true,
    autoEmptyRow: false,
    resizable: true,
    selection: true
  });
  gridProcess.events.on("AfterEditEnd", function (value, row, col) {
    if (col.id == "selected") {
      if (row.ganttTaskType === "PROJECT") {
        checkMyChildren(row, value);
      }
      if (value === true) {
        checkMySibling(row);
      }
    }
  });
}

const getProcessItemCost = function () {
  reqPostJSON("/costApi/getProcessItemCost", getSearchCondition(), function (data) {
    document.getElementById("loader").style.display="none";
    if (data.result) {
      const model = listToTreeItems(JSON.parse(data.model));
      gridProcess.data.parse(model);
    }
  }, function (xhr) {
    showErrorAlert("ALERT", $.parseJSON(xhr.responseJSON).error);
  });
}

function getProcessItemCostWithChildren(){
  reqPostJSON("/costApi/getProcessItemCostWithChildren"
      , getSearchCondition()
      , function (data) {
        document.getElementById("loader").style.display="none";
        if (data.result) {
          if(Object.keys(JSON.parse(data.model)).length === 0 ){
            showErrorAlert("ALERT", "데이터가 존재하지 않습니다");
            return ;
          }

          const model = listToTreeItems(JSON.parse(data.model));
          gridProcess.data.parse(model);
          //calculateTotalProgress();
        }
      }
      , function (xhr) {
        showErrorAlert("ALERT", $.parseJSON(xhr.responseJSON).error);
      });
}

const getSearchCondition = function () {
  return JSON.stringify(
      {
        taskName   : $("#taskName").val()
      }
  );
}

function listToTreeItems(orignData) {
  let startDate = new Date("2999-12-31");
  let endDate = new Date("2000-01-01");
  let model = [], minDepth = 10, maxDepth = 0;
  orignData.forEach((m, index) => {
    m.chkboxShowModel = true;
    if (m.taskDepth && m.taskDepth > 0) {
      if (maxDepth < m.taskDepth) {
        maxDepth = m.taskDepth;
      }
      if (minDepth > m.taskDepth) {
        minDepth = m.taskDepth;
      }
    }
    if (m.ganttTaskType === "TASK") {
      m.totalTaskCost = parseInt(Number(m.taskCost) * 1.4890); // 제배율
      startDate = Math.min(new Date(m.startDate), startDate);
      endDate = Math.max(new Date(m.endDate), endDate);
    }

  });

  modelStartDate = startDate;
  modelEndDate = endDate;

  for (let depth = maxDepth; depth >= minDepth; depth--) {
    orignData.forEach(m => {
      if (depth === m.taskDepth) {
        if (m.ganttTaskType === "PROJECT") {
          m.items = [];
          m.progressRate = "";
          m.firstCount = "";
          m.duration = "";
          m.complexUnitPrice = "";
          m.startDate = "";
          m.endDate = "";

          m.children = [];
          let sumData = { taskCost: 0, paidCost: 0, duration: 0, progressDurationRate: 0, totalTaskCost: 0 };
          let typeProjectStartDate = new Date("2999-12-31");
          let typeProjectEndDate = new Date("2000-01-01");
          orignData.forEach(mm => {
            if (m.processItemId === mm.processParentItemId) {
              m.items.push(mm);
              m.children.push(mm.processItemId)
              sumData.taskCost += mm.taskCost;
              sumData.paidCost += mm.paidCost;
              sumData.duration += mm.duration;
              sumData.progressDurationRate += Number(mm.progressRate) * Number(mm.duration);
              sumData.totalTaskCost += Number(mm.totalTaskCost);
              typeProjectStartDate = Math.min(new Date(mm.startDate), typeProjectStartDate);
              typeProjectEndDate = Math.max(new Date(mm.endDate), typeProjectEndDate);
            }
          });
          m.taskCost = sumData.taskCost;
          m.paidCost = sumData.paidCost;
          m.duration = sumData.duration;
          m.totalTaskCost = sumData.totalTaskCost;
          m.progressRate = (sumData.progressDurationRate / sumData.duration).toFixed(4);

          if (!isNaN(typeProjectStartDate)) { m.startDate = new Date(typeProjectStartDate).toISOString().substr(0, 10); }
          if (!isNaN(typeProjectEndDate)) { m.endDate = new Date(typeProjectEndDate).toISOString().substr(0, 10); }
        }
        if (minDepth === m.taskDepth) {
          model.push(m);
        }
      }
    });
  }
  return model;
}

function setSlider(taskList) {
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
    if (taskList[i].progressRate > 0 && taskList[i].duration > 0) {
      rate = taskList[i].progressRate / taskList[i].duration;
    } else {
      rate = 100 / taskList[i].duration;
    }
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
    textHtml += '<div id="jobSheetProcessItemList'+($(".task-title").length + i)+'">';
    textHtml += '<div class="period-table">';
    textHtml += '<div>기간:</div>';
    textHtml += '<div>' + taskList[i].startDate + ' ~ ' + taskList[i].endDate + '('+taskList[i].duration+' 일) <span class="exchange-id">' + exchangeId + '</span></div>';
    textHtml += '</div>';
    textHtml += '<div class="task-title">';
    textHtml += taskList[i].taskFullPath;
    textHtml += '<i class="fas fa-times-circle ml-2" style="cursor: pointer;" onclick="removeSlider(this)"></i>';
    textHtml += '</div>';
    textHtml += '<div class="task-element">';
    textHtml += '<div class="task-rate">';
    textHtml += '<div>진행률: (일 평균)</div>';
    textHtml += '<div class="task-rate-num">' + rate.toFixed(2) + ' (%)</div>';
    textHtml += '</div>';
    textHtml += '  <input type="number" min="0" max="100" step="0.01" class="js-range-slider" name="js-range-slider" value="' + taskList[i].progressRate + '" />';
    textHtml += '  <div class="input-group">';
    textHtml += '   <input id="processItemId" name="processItemId" type="hidden" value="' + taskList[i].processItemId + '">';
    textHtml += '   <input id="processItemCost" name="processItemCost" type="hidden" value="' + taskList[i].taskCost + '">';
    textHtml += '   <input id="phasingCode" name="phasingCode" type="hidden" value="' + taskList[i].phasingCode + '">';
    textHtml += '   <input id="taskFullPath" name="taskFullPath" type="hidden" value="' + taskList[i].taskFullPath + '">';
    textHtml += '   <input id="exchangeId" name="exchangeId" type="hidden" value="' + exchangeId + '">';
    textHtml += '   <div class="task-detail">';
    textHtml += '     <table class="task-table">';
    textHtml += '       <tr>';
    textHtml += '         <th colspan=3>실시량(원)</th><th colspan=3>진도(%)</th>';
    textHtml += '       </tr>';
    textHtml += '       <tr>';
    textHtml += '         <th>전일누계</th><th>금일</th><th>누계</th>';
    textHtml += '         <th>전일진도</th><th>금일진도</th><th>누계</th>';
    textHtml += '       </tr>';
    textHtml += '       <tr>';
    textHtml += '         <td><input id="beforeProgressAmount" name="beforeProgressAmount" type="number" style="" class="form-control" value="' + taskList[i].progressAmount + '" readonly></td>';
    textHtml += '         <td><input id="todayProgressAmount" name="todayProgressAmount" type="number" min="0" step="1" maxlength="10" style="" class="form-control" value="0" readonly></td>';
    textHtml += '         <td><input id="afterProgressAmount" name="afterProgressAmount" type="number" style="" class="form-control" value="' + taskList[i].progressAmount + '" readonly></td>';
    textHtml += '         <td><input id="beforeProgressRate" name="beforeProgressRate" type="number" style="" class="form-control" value="' + taskList[i].progressRate + '" readonly></td>';
    textHtml += '         <td><input id="todayProgressRate" name="todayProgressRate" type="number" min="0" max="100" step="0.01" style="" class="form-control" value="0" ></td>';
    textHtml += '         <td><input id="afterProgressRate" name="afterProgressRate" type="number" style="" class="form-control" value="' + taskList[i].progressRate + '" readonly></td>';
    textHtml += '       </tr>';
    textHtml += '     </table>';
    textHtml += '   </div>';
    textHtml += ' </div>';

    textHtml += '<div class="task-img-upload">';
    textHtml += '<div id="jobSheetSnapShotList'+($(".task-title").length + i)+'" class="up-file-list img">';
    textHtml += '  <img src="/dist/img/no_image.png" className="" alt="" id="jobSheetSnapShotImg'+($(".task-title").length + i)+'">';
    textHtml += '</div>';
    textHtml += '<input type="hidden" name="mySnapShotId" value="0">';
    textHtml += '<button type="button" class="btn bg-gradient-primary" data-toggle="modal" data-target="#mySnapShotShare" name="btnSelectSnapShot">';
    textHtml += '   <span th:text="#{project.job_sheet_add.btn_snap_shot}">스냅샷 선택</span>';
    textHtml += '</button>';
    textHtml += '</div>';




    textHtml += '</div>';
    textHtml += '     <div class="task-option-add">';
    textHtml += '       <div class="task-option">';
    textHtml += '       <div class="title">인원 추가</div>';
    textHtml += '         <button type="button" class="btn btn-primary" name="btnSelectWorker" style="margin-bottom: 4px;">추가</button>';
    textHtml += '         <table name="tblProcessWorker" class="table-process-item">';
    textHtml += '         </table>';
    textHtml += '       </div>';
    textHtml += '       <div class="task-option">';
    textHtml += '       <div class="title">장비 추가</div>';
    textHtml += '         <button type="button" class="btn btn-primary" name="btnSelectDevice" style="margin-bottom: 4px;">추가</button>';
    textHtml += '         <table name="tblProcessDevice" class="table-process-item">';
    textHtml += '         </table>';
    textHtml += '       </div>';
    textHtml += '     </div>';
    textHtml += '<div class="task-previous">';
    //textHtml += '<button type="button" class="btn btn-primary" name="">PRINT</button>';
    textHtml += '<button type="button" class="btn btn-primary btnAddPrevJobSheetItem" data-process-item-id="'+taskList[i].processItemId+'"  data-item-bun="'+($(".task-title").length + i)+'" name="" style="margin-left: 5px">이전내역 불러오기</button>';
    textHtml += '</div>';
    textHtml += '</div></div>';
  }
  $taskList.append(textHtml);

  setTimeout(drawSlider, 100);
  $("#modalSearchProcess").modal("hide");
}

$(document).on("change", 'input[name=todayProgressRate]', function () {
  let i = $('input[name="todayProgressRate"]').index(this);
  const beforeRate = parseFloat($('input[name=beforeProgressRate]').eq(i).val());
  let todayRate = parseFloat(this.value);
  let val = beforeRate + todayRate;
  if (val > 100) {
    todayRate = 100 - beforeRate;
    this.value = todayRate;
  }
  $('input[name=afterProgressRate]').eq(i).val(beforeRate + todayRate)
  $('input[name=afterProgressRate]').eq(i).change();

  // 실시량(원)
  const cost = parseFloat($('input[name=processItemCost]').eq(i).val());
  const before = parseInt($('input[name=beforeProgressAmount]').eq(i).val());
  const today = parseInt(cost * (todayRate/100));
  $('input[name=todayProgressAmount]').eq(i).val(today)
  $('input[name=afterProgressAmount]').eq(i).val(before + today)
});

$('button[name=btnSnapShotList]').on("click", function () {
  popupMySnapShot("snapShotList");
});

$('button[name=btnSearchProcess]').on("click", function () {
  $("#txtLoader").text("검색중입니다.");
  document.getElementById("loader").style.display="block";
  getProcessItemCostWithChildren();
});

$this.find("#btnReset").on("click", function () {
  $this.find("#taskName").val("");
  // document.getElementById("txtLoader").text="초기화중입니다.";
  $("#txtLoader").text("초기화중입니다.");
  document.getElementById("loader").style.display="block";
  getProcessItemCost();
});

$(document).on("click", 'button[name=btnSelectWorker]', function () {
  let i = $('button[name="btnSelectWorker"]').index(this);
  currentProcessItemIndex = i;
  popupSelectProcessWorker();
});
$(document).on("click", 'button[name=btnSelectDevice]', function () {
  let i = $('button[name="btnSelectDevice"]').index(this);
  currentProcessItemIndex = i;
  popupSelectProcessDevice()
});
$(document).on("click", 'button[name=btnSelectSnapShot]', function () {
  let i = $('button[name="btnSelectSnapShot"]').index(this);
  currentProcessItemIndex = i;
  popupMySnapShot("jobSheetAdd");
});

function drawSlider() {
  for (let i = 0; i < $("input[name=js-range-slider]").length; i++) {
    $("input[name=js-range-slider]").eq(i).ionRangeSlider({
      type: 'single',
      min: 0.00,
      max: 100.00,
      step: 0.01,
      postfix: " %",
      to_fixed: true,
      from_fixed: true,
      onChange: function (data) {
        $('input[name=afterProgressRate]').eq(i).val(data.from);
      },
    });
    $('input[name=todayProgressAmount]').eq(i).on("change", function () {
      const before = parseInt($('input[name=beforeProgressAmount]').eq(i).val());
      const today = parseInt(this.value);
      $('input[name=afterProgressAmount]').eq(i).val(before + today);
    });
    $('input[name=afterProgressRate]').eq(i).on("change", function () {
      const v = parseInt(this.value);
      if (v < 0) {
        this.value = 0;
      } else if (v > 100) {
        this.value = 100;
      }
      if ($('input[name=js-range-slider]').eq(i).data("ionRangeSlider")) {
        $('input[name=js-range-slider]').eq(i).data("ionRangeSlider").update({
          from: $('input[name=afterProgressRate]').eq(i).val(),
        });
      } else {
        //$('input[name=js-range-slider]').eq(i).ionRangeSlider({
          // 새로운 ionRangeSlider 설정
        //});
      }

    });
    /**
     $('input[name=todayProgressRate]').eq(i).on("change", function () {
      const beforeRate = parseFloat($('input[name=beforeProgressRate]').eq(i).val());
      let todayRate = parseFloat(this.value);
      let val = beforeRate + todayRate;
      if (val > 100) {
        todayRate = 100 - beforeRate;
        this.value = todayRate;
      }
      $('input[name=afterProgressRate]').eq(i).val(beforeRate + todayRate)
      $('input[name=afterProgressRate]').eq(i).change();

      // 실시량(원)
      const cost = parseFloat($('input[name=processItemCost]').eq(i).val());
      const before = parseInt($('input[name=beforeProgressAmount]').eq(i).val());
      const today = parseInt(cost * (todayRate/100));
      $('input[name=todayProgressAmount]').eq(i).val(today)
      $('input[name=afterProgressAmount]').eq(i).val(before + today)
    });
     **/
  }
}

function popupMySnapShot(type) {
  modalShowAndDraggable('#mySnapShotShare');
  const $sortedSelector = $("#jobSheetSnapShotList"+currentProcessItemIndex).find("button.btn");
  let existSnapShotIds = "";

  $sortedSelector.each(function (index, item) {
    if (existSnapShotIds === "") existSnapShotIds = $(item).data('snap-shot-id');
    else existSnapShotIds += "," + $(item).data('snap-shot-id');
  });

  //reqGet('/commonModal/mySnapShotShare?type=jobSheetAdd&id='+processItemNo+'&existSnapShotIds=${existSnapShotIds}', function (data) {
  //  $('#mySnapShotShare').find('.modal-body').html(data);
  //});
  reqGet(`/commonModal/mySnapShotShare?type=${type}&id=${currentProcessItemIndex}&existSnapShotIds=${existSnapShotIds}`, function (data) {
    $('#mySnapShotShare').find('.modal-body').html(data);
  });
}

function popupSelectProcessWorker() {
  const data = [
    "도장공",
    "보통인부",
    "미장공",
    "비계공",
    "방수공",
    "보링공",
    "신호수",
    "용접공",
    "작업반장",
    "조력공",
    "전기공",
    "철근공",
    "철공",
    "철판공",
    "철골공",
    "착암공",
    "콘크리트공",
    "특별인부",
    "타일공",
    "포설공",
    "포장공",
    "화약취급공",
    "할석공",
    "형틀목공",
  ];
  popupSelectProcessItem(data, 1);
}

function popupSelectProcessDevice() {
  const data = [
    "고소작업차",
    "굴삭기(B/H) / 0.2m3",
    "굴삭기(B/H) / 0.4m3",
    "굴삭기(B/H) / 0.6m3",
    "굴삭기(B/H) / 0.7m3",
    "굴삭기(B/H) / 0.8m3",
    "굴삭기(B/H) / 1.0 M3 이상",
    "그라우팅믹서/펌프",
    "건설용 리프트 (인화물용)",
    "덤프트럭 / 15TON",
    "덤프트럭 / 24TON",
    "로더",
    "롤러(진동롤러)",
    "롤러(크롤러롤러)",
    "래머80 KG",
    "모터그레이더",
    "보오링기계",
    "발전기",
    "불도저",
    "살수차",
    "숏크리트머신",
    "아스팔트 페이퍼(피니셔)",
    "오거드릴",
    "이동식 임목파쇄기",
    "양족식 롤러",
    "압쇄기(펄버라이저)",
    "지게차",
    "점보드릴",
    "차징카",
    "콘크리트 피니셔(포장용)",
    "콘크리트롤러페이버 / 12.0 M",
    "콘크리트 믹서트럭 / 6.0M3",
    "콘크리트 펌프차",
    "크로울러드릴",
    "크레인(무한궤도) / 70TON 이하",
    "크레인(무한궤도) / 70TON 초과",
    "크레인(타이어) / 200 TON 이하",
    "크레인(타이어) / 200 TON 초과",
    "카고트럭",
    "타워크레인",
    "트럭탑제형 크레인 / 5 ~ 10 TON",
    "플레이트 콤팩터 / 1.5 TON",
    "항타기",
    "기타",
  ];
  popupSelectProcessItem(data, 2);
}

function popupSelectProcessItem(data, type) {
  currentProcessItemType = type;
  modalShowAndDraggable('#modalSelectProcessItem');
  const modalTitle = currentProcessItemType === 2 ? "장비 선택": "인원 선택";
  const btnTitle = currentProcessItemType === 2 ? "장비추가": "인원추가";
  $("#modalSelectProcessItem #modalLongTitle").text(modalTitle);
  $("#modalSelectProcessItem #btnPrcessItemAdd").text(btnTitle);
  const rows = []
  let row;
  data.forEach((e, i) => {
    if (i % 3 === 0) {
      row = {
        align: "start",
        padding: "0 0 0 40px",
        cols: []
      }
      rows.push(row)
    }
    row.cols.push({
      id: 'ProcessItem' + i,
      type: "checkbox",
      text: e,
      value: e,
      labelWidth: "160px",
    })
  })
  if (selectProcessItemForm) {
    selectProcessItemForm.destructor();
  }
  selectProcessItemForm = new dhx.Form("selectProcessItemForm", {
    rows
  });
}
function onClickBtnProcessItemAdd() {
  const formValue = selectProcessItemForm.getValue()
  const values = [];
  for(key in formValue) {
    if (formValue[key] !== "") {
      values.push(formValue[key])
    }
  }
  applySelectProcessItem(values);

  $("#modalSelectProcessItem").modal("hide");
}
function applySelectProcessItem(values) {
  const target = currentProcessItemType === 2 ? "tblProcessDevice" : "tblProcessWorker";
  const selector = "table[name=" + target + "]";
  const table = $(selector).eq(currentProcessItemIndex);
  let html = "";
  for (let value of values) {
    let isExist = false;
    table.find("[name=progressItemName]").each(function() {
      if (this.value === value) {
        isExist = true;
      }
    })
    if (isExist) { continue; }
    html += '<tr>';
    html += '  <td><input name="progressItemName" type="hidden" value="' + value + '">' + value + '</td>';
    html += '  <td><input name="progressItemValue" type="number" min="0" step="1" maxlength="10" style="width: 75px;" class="form-control" value="0" ></td>';
    html += '  <td><button class="btnDel" onclick="onClickDeleteProgressItem(this);">X</button></td>';
    html += '</tr>';
  }
  table.append(html);
}

function onClickDeleteProgressItem(self) {
  // button > td > tr
  self.parentNode.parentNode.remove();
}

function checkMyChildren(row, value) {
  gridProcess._filterData.forEach(item => {
    if (item.processParentItemId === row.processItemId) {
      item.selected = value;
      if (item.ganttTaskType === "PROJECT") {
        checkMyChildren(item, value);
      }
    }
  });
}
function checkMySibling(row) {
  let checkSibling = true;
  let parentRow = "";

  for (let i = 0; i < gridProcess._filterData.length; i++) {
    if (gridProcess._filterData[i].processItemId === row.processParentItemId) {
      parentRow = gridProcess._filterData[i];
      break;
    }
  }

  if (parentRow === "") {
    return;
  } else {
    gridProcess._filterData.forEach(item => {
      if (parentRow.processItemId === item.processParentItemId) {
        if (item.selected === false) {
          checkSibling = false;
        }
      }
    });

    if (checkSibling === true) {
      for (let i = 0; i < gridProcess._filterData.length; i++) {
        if (gridProcess._filterData[i].processItemId === parentRow.processItemId) {
          gridProcess._filterData[i].selected = true;
          break;
        }
      }
    }
    //부모를 기준으로 다시 재귀
    checkMySibling(parentRow);
  }
}

function removeSlider(obj) {
  var element = obj.parentElement.parentElement; // 삭제할 요소
  var $sliderInput = $(element).find('input[name=js-range-slider]');

  if ($sliderInput.data("ionRangeSlider")) {
    // ionRangeSlider 인스턴스가 존재하는 경우
    //console.log("ionRangeSlider 인스턴스가 존재합니다.");

    // ionRangeSlider 인스턴스를 가져온 뒤 삭제
    var sliderInstance = $sliderInput.data("ionRangeSlider");
    sliderInstance.destroy();
  }

  // 요소 삭제
  element.remove();
  drawSlider();
}

function setSendData(status, id) {
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
    "referencesIds": getJobSheetReferencesIds(),
    "mySnapShotIds": id ? getSnapShotIds() : getMySnapShotIds(),
    "processItemIds": getInputValues ("processItemId"),
    "phasingCodes": getInputValues ("phasingCode"),
    "taskFullPaths": getInputValues ("taskFullPath"),
    "beforeProgressRates": getInputValues ("beforeProgressRate"),
    "afterProgressRates": getInputValues ("afterProgressRate"),
    "todayProgressRates": getInputValues ("todayProgressRate"),
    "beforeProgressAmounts": getInputValues ("beforeProgressAmount"),
    "afterProgressAmounts": getInputValues ("afterProgressAmount"),
    "todayProgressAmounts": getInputValues ("todayProgressAmount"),
    "processItemWorkers": getProcessItemValues("tblProcessWorker"),
    "processItemDevices": getProcessItemValues("tblProcessDevice"),
    "status": status,
    // 전일공사현황 추가(2023.03.06 이동근)
    "beforeContents": beforeContentsEditor.getInputData(),
    "todayContents": $("#today_contents").html(),
    "exchangeIds": getInputValues ("exchangeId"),
    //"mySnapShotIds": getInputValues ("mySnapShotId"),

  }
  if (id) {
    result.id = id
  }
  return JSON.stringify(result);
}

function getJobSheetReferencesIds() {
  if ($("#jobSheetReferencesIds").val().length === 0) return [];
  return $("#jobSheetReferencesIds").val().split(',').map(Number);
}

function getMySnapShotIds() {
  const $snapShotIds = $('button[name="btnSnapShotId"]');
  if ($snapShotIds.length === 0) return [];

  let snapShotIds = [];
  $snapShotIds.each(function (idx, item) {
    snapShotIds.push($(item).data('snap-shot-id'));
  });
  return snapShotIds;
}

function getSnapShotIds() {
  if ($('.savedSnapShot').length > 0) {
    return [];
  }
  return getMySnapShotIds();
}

function getInputValues (nm) {
  let vals = [];
  $("input[name=" + nm + "]").map(function (i, el) {
    vals.push(el.value + "");
  });
  return vals;
}

function getProcessItemValues(name) {
  let vals = [];
  $("div.task-option-add").map(function (i, el) {
    const table = $(el).find('table[name="' + name + '"]');
    const names = table.find('input[name=progressItemName]');
    const values = table.find('input[name=progressItemValue]');
    const val = []
    for (let i=0; i<names.length; i++) {
      val.push({
        name: names.eq(i).val() + "",
        value: values.eq(i).val() + "",
      })
    }
    vals.push(val);
  });
  return vals;
}

// 공정추가 버튼 클릭
function onClickBtnPrcessAdd() {
  const selectedData = gridProcess._filterData.filter(e => e.selected && !!e.phasingCode);
  if (selectedData.length > 0) {
    setSlider(selectedData);
  } else {
    showErrorAlert("ALERT", "선택된 공정이 없습니다");
  }
}


function onClickBtnPrcessAddNew() {
  const selectedData = gridProcess._filterData.filter(e => e.selected && !!e.phasingCode);
  //console.log(selectedData);
  let vals = [];
  if (selectedData.length > 0) {
    //const val = []
    for (let i = 0; i < selectedData.length; i++) {
      vals.push({
        processItemId: selectedData[i].processItemId + "",
      })
    }
    //vals.push(val);
    //console.log(JSON.stringify(vals));
    reqPostJSON("/projectApi/getJobSheetPrev", JSON.stringify(vals), function (data) {
      //console.log(data.jobSheetProcessItem);
      if (data.jobSheetProcessItem.length > 0) {

      }
    }, function (xhr) {
      showErrorAlert("ALERT", $.parseJSON(xhr.responseJSON).error);
    });
    setSlider(selectedData);
  } else {
    showErrorAlert("ALERT", "선택된 공정이 없습니다");
  }
}