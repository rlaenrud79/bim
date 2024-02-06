/**
 * ================ 모델에서 오브젝트 선택했을 때 ===================
 * 1. nodeid를 통해서 상위를 검색하여 phasingCodes가 있는 노드를 찾는다.
 * 2. 그 노드의 객체 이름과 재료를 찾는다.
 * 3. 데이터 베이스에서 나머지 요소들을 찾는다.
 * 4. 다이얼로그 html에 그 값들을 설정한다.
 * 5. 다이얼로그를 표시한다.
 * 6. 필요 시 유저 설정을 통해서 오브젝트 프로퍼티 윈도우의 표시 여부를 설정할 수 있도록 한다.
 */

let NodePropertyPopup = {
    prop:{
        nodeId: -999,
        phasingCodes: '',
        name: '',
        finishing: '',
        path: '',
        procItemId : '',
        procName : '',
        procPeriod : '',
        procRate : 0,
        costNum : 0,
        costUnit : '',
        complexUnitPrice : 0,
        sumSiteContractEnd: 0,
        sumSiteContractExpropriation: 0,
        sumSiteContractUncompensated: 0
    },
    init: function(){
        // create dialog html
        NodePropertyPopup._appendDialogTemplate();
        // setup dialog
        NodePropertyPopup._enableDialogAction();

    },
    showNodePropertyPopup: function (selectionEvents,companyRoleType){
        NodePropertyPopup._clearProperty(); // 객체 정보 용
        for (const selectionEvent of selectionEvents){
            const selection = selectionEvent.getSelection();
            if (selection && selection.getSelectionType() !== Communicator.SelectionType.None){
                let nodeId = selection.getNodeId();
                if(hwv.model.getNodeType(nodeId) === Communicator.NodeType.BodyInstance
                    || hwv.model.getNodeType(nodeId) === Communicator.NodeType.Body){
                    nodeId = hwv.model.getNodeParent(nodeId);
                }
                NodePropertyPopup._getHwvProperty(nodeId).then(()=>{
                    if( NodePropertyPopup.prop.phasingCodes.toUpperCase().startsWith("YJ_") ){ // 용지 정보
                        hwv.model.getNodeProperties(nodeId).then((properties)=>{
                            let tbl = NodePropertyPopup._landInformation(properties);
                            $("#dlgObjectProperty").parent().find("span").html("용지 정보");
                            $("#dlgObjectProperty").html(tbl);
                            $("#dlgObjectProperty").dialog("open");
                        });
                    }else {
                        if( NodePropertyPopup.prop.phasingCodes.trim().length > 0) { // 2022.10.04 phasingCodes 유무 체크
                            let tbl = NodePropertyPopup._objInformation(companyRoleType);
                            $("#dlgObjectProperty").parent().find("span").html("객체 정보");
                            $("#dlgObjectProperty").html(tbl);
                            NodePropertyPopup._getCostProperties(NodePropertyPopup.prop.phasingCodes)
                                .then(function (resolve) {
                                    NodePropertyPopup._findCostDetail();
                                    NodePropertyPopup._findWorkerDeviceDetail();
                                    NodePropertyPopup._popoverInit();
                                });
                        }
                    }
                })

            }
        }
    },
    _landInformation: function(prop){

        let contractHtml = "";
        let contractStatus =  (prop["Data/계약완료"] == undefined)?"":prop["Data/계약완료"];
        contractStatus = contractStatus.replace(/\s/gi, "")
        if( contractStatus === "보상완료"){
            contractHtml = '<span style="color:blue">' + contractStatus + '</span>';
        }else if(contractStatus === "수용재결") {
            contractHtml = '<span style="color:orange">' + contractStatus + '</span>';
        }else if(contractStatus === "미보상"){
            contractHtml = '<span style="color:red">' + contractStatus + '</span>';
        }else{
            contractHtml = '<span>' + contractStatus + '</span>';
        }

        let area = (prop["Data/면적"] == undefined)?"":prop["Data/면적"];
        let sp = area.split(".");
        let first = "";
        if ($.isNumeric(sp[0].trim())){ first = parseInt(sp[0].trim()).toLocaleString('ko-KR') } else {first = sp[0];}
        area = first + ( (sp[1] == undefined)? "": sp[1].replace(/\d/gi, "") );

        let estimated = (prop["Data/감정평가금액"] == undefined)? "":prop["Data/감정평가금액"];
        if ($.isNumeric(estimated.trim())){ estimated = parseInt(estimated.trim()).toLocaleString('ko-KR') }

        NodePropertyPopup._getSiteContractSum();

        let tbl = '';
        tbl += '    <table class="w-100 table table-sm table-bordered table-striped">';
        tbl += '        <tr><th class="w-25 text-center">구분</th><th class="text-center">상세정보</th></tr>';
        tbl += '        <tr><td>필지번호</td><td>' + ( (prop["Data/필지번호"] == undefined)?"":prop["Data/필지번호"] ) +'</td></tr>';
        tbl += '        <tr><td>소재지</td><td>' + ( (prop["Data/소재지"] == undefined)?"":prop["Data/소재지"] ) +'</td></tr>';
        tbl += '        <tr><td>소유주</td><td>' + ( (prop["Data/소유주"] == undefined)?"":prop["Data/소유주"] ) +'</td></tr>';
        tbl += '        <tr><td>면적</td><td>' + area +'</td></tr>';
        tbl += '        <tr><td>감정평가금액</td><td>' + estimated +'</td></tr>';
        tbl += '        <tr><td>계약현황</td><td>' + contractHtml +'</td></tr>';
        tbl += '        <tr><th colspan="2">전체 합계</th></tr>';
        tbl += '        <tr><td>보상완료</td><td>' + NodePropertyPopup.prop.sumSiteContractEnd +'</td></tr>';
        tbl += '        <tr><td>수용재결</td><td>' + NodePropertyPopup.prop.sumSiteContractExpropriation +'</td></tr>';
        tbl += '        <tr><td>미보상</td><td>' + NodePropertyPopup.prop.sumSiteContractUncompensated +'</td></tr>';
        tbl += '    </table>';
        return tbl;
    },
    _objInformation: function(companyRoleType){
        let tbl = '';
        tbl += '    <table class="w-100 table table-sm table-bordered"  id="tbObjectProperty">';
        tbl += '        <thead><tr><th class="w-25 text-center" colspan="2">구분</th><th class="text-center" colspan="2">데이터</th></tr></thead>';
        tbl += '        <tbody>';
        tbl += '        <tr><td rowspan="3">공사</td><td>객체이름</td><td id="propName" colspan="2"> value1 </td></tr>';
        tbl += '        <tr>                        <td>작업분류</td><td id="propPath" colspan="2"> value2 </td></tr>';
        tbl += '        <tr>                        <td>재료</td><td id="propFinishing"> value3 </td><td width="40"><button id="workerDevicePopover" class="btn btn-primary workerDevicePopover" data-toggle="popover">+</button></td></tr>';
        tbl += '        <tr class="bg-light"><td rowspan="3">공정</td><td>공정명</td><td id="propProcName" colspan="2"> value4 </td></tr>';
        tbl += '        <tr class="bg-light">                        <td>기간</td><td id="propProcPeriod" colspan="2"> value5 </td></tr>';
        tbl += '        <tr class="bg-light">                        <td>진행률</td><td id="propProcRate" colspan="2"> value6 </td></tr>';
        if(companyRoleType !== 'PARTNER'){
            tbl += '        <tr><td rowspan="4">기성</td><td>대표수량</td><td id="propCostNum" colspan="2"> value7 </td></tr>';
            tbl += '        <tr>                        <td>복합단가</td><td id="propComplexUnitPrice"> value8 </td><td width="40"><button id="costPopover" class="btn btn-primary costPopover" data-toggle="popover">+</button></td></tr>';
            tbl += '        <tr>                        <td>공사비용</td><td id="propCostConst" colspan="2"> value9 </td></tr>';
            tbl += '        <tr>                        <td>기성률</td><td id="propPaidProgressRate" colspan="2"> value10 </td></tr>';
        }
        tbl += '        </tbody>';
        tbl += '    </table>';
        tbl += '    <div>';
        tbl += '      <div class="row form-group">';
        tbl += '        <div class="col-sm-4"><input type="text" class="form-control" id="fileTitle" name="fileTitle" placeholder="파일 제목을 입력하세요" /></div>';
        tbl += '        <div class="col-sm-6 mt-1"><input type="file" class="form-control-file" id="objFile" name="objFile"/></div>';
        tbl += '        <div class="col-sm-2"><button id="btnAddFile" class="btn btn-primary"><i class="fas fa-upload"></i>&nbsp;<span>파일 추가</span></button></div>';
        tbl += '      </div>';
        tbl += '      <table class="w-100 table table-sm table-striped" id="addedFiles"></table>'
        tbl += '    </div>';
        return tbl;
    },
    _clearProperty: function(){
        NodePropertyPopup.prop.nodeId= -999;
        NodePropertyPopup.prop.phasingCodes= '';
        NodePropertyPopup.prop.name= '';
        NodePropertyPopup.prop.finishing= '';
        NodePropertyPopup.prop.path= '';
        NodePropertyPopup.prop.procName = '';
        NodePropertyPopup.prop.procPeriod = '';
        NodePropertyPopup.prop.procRate = 0;
        NodePropertyPopup.prop.costNum = 0;
        NodePropertyPopup.prop.costUnit = '';
        NodePropertyPopup.prop.complexUnitPrice = 0;
        NodePropertyPopup.prop.paidProgressRate = 0;
        $(".popover").popover('dispose');
    },
    _getFinishingAndPhasingCodes: async function (nodeId){
        HwvUtility.cwv = hwv;
        let rtn = await HwvUtility.getFinishingAndPhasingCodesByNodeId(nodeId);
        return rtn;
    },
    _getHwvProperty: async function(nodeId){
        // body가 선택되었을 경우, 상위 오브젝트를 검색
        let result = await NodePropertyPopup._getFinishingAndPhasingCodes(nodeId);
        NodePropertyPopup.prop.nodeId = result["nodeId"];
        NodePropertyPopup.prop.phasingCodes = $.trim(result["phasingCodes"]);
        NodePropertyPopup.prop.name = result["name"];
        NodePropertyPopup.prop.finishing = result["finishing"];
    },
    _getCostProperties: function (phasingCodes){
        return new Promise(function(resolve, reject){
            let searchCondition = {workId:0, phasingCode:NodePropertyPopup.prop.phasingCodes, taskName:"", startDate:"", endDate:""}
            reqPostJSON(
                "/costApi/getProcessItemCost"
                ,JSON.stringify(searchCondition)
                ,function (data){
                    const model = JSON.parse(data.model);
                    if ( model.length > 0) {
                        NodePropertyPopup.prop.procItemId = model[0].processItemId;
                        NodePropertyPopup.prop.path = model[0].taskFullPath;
                        NodePropertyPopup.prop.procName = model[0].taskName;
                        NodePropertyPopup.prop.procPeriod = model[0].startDate + ' ~ ' + model[0].endDate;
                        NodePropertyPopup.prop.procRate = model[0].progressRate + '%';
                        NodePropertyPopup.prop.costNum = model[0].firstCount;
                        NodePropertyPopup.prop.costUnit = model[0].firstCountUnit;
                        NodePropertyPopup.prop.complexUnitPrice = model[0].complexUnitPrice;
                        NodePropertyPopup.prop.paidProgressRate = (model[0].paidProgressRate*100) + '%';
                    }
                    // show dialog
                    NodePropertyPopup._showPropertiesDialog();
                    resolve(true)
                }
                ,function (xhr){
                    console.log($.parseJSON(xhr.responseJSON).error);
                    resolve(true)
                });
        })
    },
    // 먼저 오브젝트 정보 표시용 다이얼로그를 body 에 추가해 둔다
    _appendDialogTemplate : function(){
        if( $("body").find("#dlgObjectProperty").length === 0 ){
            let dlg = '';
            dlg += '<div id="dlgObjectProperty" title="객체 정보">';
            dlg += '    <table class="w-100 table table-sm table-bordered">';
            dlg += '    </table>';
            dlg += '</div>';
            $("body").append(dlg);
        }
    },
    _showPropertiesDialog: function(){
        let $dlg = $("body").find("#dlgObjectProperty");
        $dlg.find("#propName").text("");
        $dlg.find("#propPath").text("");
        $dlg.find("#propFinishing").text("");
        $dlg.find("#propProcName").text("");
        $dlg.find("#propProcPeriod").text("");
        $dlg.find("#propProcRate").text("");
        $dlg.find("#propCostNum").text("");
        $dlg.find("#propComplexUnitPrice").text("");
        $dlg.find("#propCostConst").text("");

        $dlg.find("#propName").text(NodePropertyPopup.prop.name);
        $dlg.find("#propPath").text(NodePropertyPopup.prop.path);
        $dlg.find("#propFinishing").text(NodePropertyPopup.prop.finishing);
        $dlg.find("#propProcName").text(NodePropertyPopup.prop.procName);
        $dlg.find("#propProcPeriod").text(NodePropertyPopup.prop.procPeriod);
        $dlg.find("#propProcRate").text(NodePropertyPopup.prop.procRate);
        $dlg.find("#propCostNum").text(NodePropertyPopup.prop.costNum + NodePropertyPopup.prop.costUnit);
        $dlg.find("#propComplexUnitPrice").text((NodePropertyPopup.prop.complexUnitPrice).toLocaleString('ko-KR'));
        $dlg.find("#propPaidProgressRate").text(NodePropertyPopup.prop.paidProgressRate);
        let costConstruction = 0;
        if(isNumeric(NodePropertyPopup.prop.costNum) && isNumeric(NodePropertyPopup.prop.complexUnitPrice)){
            costConstruction = parseInt(NodePropertyPopup.prop.costNum * NodePropertyPopup.prop.complexUnitPrice).toLocaleString('ko-KR'); // 세자리 콤마
        }
        $dlg.find("#propCostConst").text(costConstruction);

        // 업로드된 파일 정보를 표시한다.
        $.ajax({
            url: "/modelingViewerApi/phasingCodeFile/" + NodePropertyPopup.prop.phasingCodes
            , type: "GET"
            , success: function(m){
                // console.log(m);
                for( let i = 0; i < m.length; i++){
                    let appendTr = '';
                    appendTr += '<tr><td class="w-40">'+ m[i].fileTitle + '</td><td class="w-50 badge badge-pill download-attach-file"><a href="/modelingViewerApi/phasingCodeFileDownload/' + m[i].id + '">' + m[i].originFileName +  '</a></td>';
                    appendTr += '<td><button class="deleteFile btn bg-gradient-danger" data-id="' + m[i].id + '"><i class="fas fa-trash"></i> <span>DELETE<span></button></td></tr>';
                    $dlg.find("#addedFiles").append(appendTr);
                }
                $dlg.dialog("open");
            }
            ,error: function (jqXHR)
            {
                $dlg.dialog("open");
                console.log(jqXHR.responseText);
            }
        });
        /*
        reqGetSync("/modelingViewerApi/phasingCodeFile/" + NodePropertyPopup.prop.phasingCodes
            , (obj) =>{
                let m = JSON.parse(obj.model);
                for( let i = 0; i < m.length; i++){
                    let appendTr = '';
                    appendTr += '<tr><td class="w-30">'+ m[i].fileTitle + '</td><td class="w-50"><a href="#" data-id="' + m[i].id + '">' + m[i].originFileName +  '</a></td>';
                    appendTr += '<td><button class="deleteFile btn bg-gradient-danger" data-fileid="' + m[i].id + '"><i class="fas fa-trash"></i> <span>DELETE<span></button></td></tr>';
                    $dlg.find("#addedFiles").append(appendTr);
                }
            }
            ,()=>{}, ()=> {}, "json" );
        */
        //$dlg.dialog("open");

        // 파일 업로드 버튼 클릭
        $dlg.find("#btnAddFile").click(function(){
            let phasingCode = NodePropertyPopup.prop.phasingCodes;
            let fileTitle = $dlg.find("#fileTitle").val();
            let uploadFile = $dlg.find("#objFile")[0].files[0];
            if( fileTitle.trim() == "" ) {
                showErrorAlert("ALERT", "파일 제목을 입력하세요.");
                return false;
            }
            if ( uploadFile == undefined ) {
                showErrorAlert("ALERT", "파일을 선택하세요.");
                return false;
            }
            let form = new FormData();
            form.append("phasingCode", phasingCode)
            form.append( "fileTitle", fileTitle);
            form.append( "file", uploadFile );
            form.append("fileUploadUIType", "PHASING_CODE_FILE");
            jQuery.ajax({
                url : "/modelingViewerApi/phasingCodeFile"
                , type : "POST"
                , processData : false
                , contentType : false
                , data : form
                , success:function(response) {
                    // 파일 정보를 저장하고 #addedFiles 에 관련 정보를 추가한다.
                    let appendTr = '';
                    appendTr += '<tr><td class="w-40">'+ response.fileTitle + '</td><td class="w-50 badge badge-pill download-attach-file"><a href="/modelingViewerApi/phasingCodeFileDownload/' + response.id + '">' + response.originFileName +  '</a></td>';
                    appendTr += '<td><button class="deleteFile btn bg-gradient-danger" data-id="' + response.id + '"><i class="fas fa-trash"></i> <span>DELETE<span></button></td></tr>';
                    $dlg.find("#addedFiles").append(appendTr);
                    showErrorAlert("INFO", "파일을 저장했습니다.")
                    //console.log(response);
                }
                ,error: function (jqXHR)
                {
                    console.log(jqXHR.responseText);
                }
            });
        });
        $dlg.find("#addedFiles").on("click", ".deleteFile", function(){
            let id = $(this).data("id");
            showConfirm("파일 삭제", "파일을 삭제하시겠습니까?", NodePropertyPopup._deleteFile.bind(this));
        });
        //NodePropertyPopup._clearProperty();
    },
    _deleteFile: function (){
        const _this = $(this);
        const id = _this.data('id');

        reqPost('/modelingViewerApi/deletePhasingCodeFile'
            , { "id": id }
            , function (data) {
                if (!data.result) {
                    showErrorAlert("ALERT", data.message);
                } else {
                    _this.parent().parent().remove(); //remove tr
                }
            }
            , function (xhr) {
                showErrorAlert("ALERT", $.parseJSON(xhr.responseJSON).error);
            });
    },
    _enableDialogAction : function(){
        // setup dialog
        $("body").find("#dlgObjectProperty").dialog({
            autoOpen: false,
            position : {my: "center", at: "center", of: window},
            show     : {effect: "blind", duration: 100},
            hide     : {effect: "explode", duration: 100},
            width    : 800,
            maxHeight: 700,
            close    : function(event, ui){
                keyEventWhich = null; // modelingViewer.html에서 설정 취소 남는 것을 해소
                $("#costPopover").popover('dispose'); // 열려있는 popover 끄기
            }
        });
    },
    _getSiteContractSum: function (){
        let rtn = {};
        reqGetSync("/modelingViewerApi/nodeProperty/siteContract"
            , (obj) =>{
                let m = JSON.parse(obj.model);
                m.filter(function(o){
                    // 용지정보 > 계약현황(계약완료 > 보상완료) 변경
                    if ( o["siteStatus"] === "보상완료" ){
                        NodePropertyPopup.prop.sumSiteContractEnd = NodePropertyPopup._toThreeComma( o["siteSum"] );
                    }else if( o["siteStatus"] === "미보상" ){
                        NodePropertyPopup.prop.sumSiteContractUncompensated = NodePropertyPopup._toThreeComma( o["siteSum"] );
                    }else if( o["siteStatus"] === "수용재결" ){
                        NodePropertyPopup.prop.sumSiteContractExpropriation = NodePropertyPopup._toThreeComma( o["siteSum"] );
                    }
                })
            }
            ,()=>{}, ()=> {}, "json" );
    },
    _toThreeComma(source){
        let rtn = source;
        if ($.isNumeric(source.trim())){
            rtn = parseInt(source.trim()).toLocaleString('ko-KR')
        }
        return rtn;
    },
    _findCostDetail: function(){
        reqGet("/costApi/getProcessItemCostDetail/"+NodePropertyPopup.prop.procItemId+"/R", function (data) {
            if (data.result) {

                let model = JSON.parse(data.model);
                $("#costPopover").data("model", model);

                $('#costPopover').popover({
                    placement   : "right",
                    html        : true,
                    title       : "복합단가 상세",
                    container   : "body",
                    content     : function(){
                        let model = $("#costPopover").data("model");
                        if(model.length>0){
                            $("#dlgObjectProperty").parent().css("width","1100px");
                        }
                        else{
                            $("#dlgObjectProperty").parent().css("width","700px");
                        }

                        let dom = document.createElement("div");
                        dom.innerHTML = NodePropertyPopup._getCostDetail(model, NodePropertyPopup.prop.procItemId);
                        return dom;
                    },
                });
            }
        });
    },
    _getCostDetail: function(model, procItemId){
        let modelHtml = '';
        modelHtml += '<table class="tablePopover" border="1" width="100" id="tablePopover">';
        modelHtml += '  <thead>';
        modelHtml += '      <tr>';
        modelHtml += '          <td colspan="7" class="text-right">';
        modelHtml += "              <button class='btn bg-gradient-info exportXlsx' id='exportXlsx' data-procitemid='" + procItemId + "' data-model='" + JSON.stringify(model) + "'>"; // json에 쌍따옴표로 인하여 model이 data에 정상적으로 들어가지 않아 해당 라인만 쌍따옴표로 작성
        modelHtml += '                <i class="fas fa-file-excel"></i><span th:text="#{cost.index.page.print_xlsx}">xlsx 출력</span>';
        modelHtml += '             </button>';
        modelHtml += '          </td>'
        modelHtml += '      </tr>';
        modelHtml += '      <tr>';
        modelHtml += '          <td colspan="2" class="text-center">상세공종</td>';
        modelHtml += '          <td colspan="4" class="text-center">수량</td>';
        modelHtml += '          <td class="text-center">공사비</td>';
        modelHtml += '      </tr>';
        modelHtml += '      <tr>';
        modelHtml += '          <td class="text-center">코드</td>';
        modelHtml += '          <td class="text-center">명</td>';
        modelHtml += '          <td class="text-center">대표</td>';
        modelHtml += '          <td class="text-center">ⓐ값</td>';
        modelHtml += '          <td class="text-center">단위</td>';
        modelHtml += '          <td class="text-center">ⓑ단가</td>';
        modelHtml += '          <td class="text-center">ⓐ * ⓑ</td>';
        modelHtml += '      </tr>';
        modelHtml += '  </thead>';
        modelHtml += '  <tbody>';
        if(model){
            let sum = 0;
            let haveFirstCount = false;
            let firstCountValue = 0;
            let isZero = false;
            for(let i=0 ; i<model.length ; i++){
                modelHtml += '      <tr>';

                // 코드
                modelHtml += '          <td class="text-center">'+model[i].code+'</td>';

                // 명
                modelHtml += '          <td class="text-center">'+model[i].name+'</td>';

                // 대표
                if(model[i].isFirst){
                    modelHtml += '          <td class="text-center"><input type="checkbox" checked disabled></td>';
                    haveFirstCount = true;
                    firstCountValue = model[i].count
                }
                else{
                    modelHtml += '          <td class="text-center"><input type="checkbox" disabled></td>';
                }

                // ⓐ값
                modelHtml += '          <td class="text-right">'+model[i].count.toLocaleString()+'</td>';
                if(model[i].count === 0){
                    isZero = true;
                }

                // 단위
                modelHtml += '          <td class="text-center">'+model[i].unit+'</td>';

                // ⓑ단가
                modelHtml += '          <td class="text-right">'+model[i].unitPrice.toLocaleString()+'</td>';
                if(model[i].unitPrice === 0){
                    isZero = true;
                }

                // 공사비(ⓐ*ⓑ)
                modelHtml += '          <td class="text-right">'+Math.floor(model[i].cost).toLocaleString()+'</td>';
                modelHtml += '      </tr>';
                sum += model[i].cost;
            }
            modelHtml += '      <tr>';
            modelHtml += '          <td class="text-center">집계</td>';
            modelHtml += '          <td class="text-right">'+model.length+'</td>';
            if(haveFirstCount){
                if(isZero){
                    modelHtml += '          <td colspan="4"  class="text-center">값 또는 단가가 0인 항목이 있습니다.</td>';
                }
                else{
                    let numberResult=Math.floor(sum/firstCountValue);
                    modelHtml += '          <td colspan="4" class="text-right">' + numberResult.toLocaleString() + ' = ' + Math.floor(sum).toLocaleString() + ' / ' + firstCountValue.toLocaleString() + '</td>';
                }
            }
            else{
                modelHtml += '          <td colspan="4" class="text-center">대표가 없습니다.</td>';
            }
            modelHtml += '          <td class="text-right">'+ Math.floor(sum).toLocaleString() +'</td>';

            modelHtml += '      </tr>';
        }
        modelHtml += '  </tbody>';
        modelHtml += '</table>';
        return modelHtml;
    },
    _clickExportXlsx: function(){
        $(".tablePopover").each(function(index, item){
            $(this).on("click", ".exportXlsx", function(){
                let model = $(this).data("model");
                let procItemId = $(this).data("procitemid");
                if(model.length > 0){
                    let param =  "procItemId=" + procItemId;
                    window.open("/commonModal/selectModelCostDetailExcel/" + procItemId + "/R")
                }
                else{
                    showErrorAlert("ALERT", "데이터가 없습니다.");
                }
            });
        });
    },
    _findWorkerDeviceDetail: function(){
        reqGet("/costApi/getProcessItemWorkerDeviceDetail/"+NodePropertyPopup.prop.procItemId, function (data) {
            if (data.result) {

                let model = JSON.parse(data.model);
                $("#workerDevicePopover").data("model", model);

                $('#workerDevicePopover').popover({
                    placement   : "right",
                    html        : true,
                    title       : "인원 장비 상세",
                    container   : "body",
                    content     : function(){
                        let model = $("#workerDevicePopover").data("model");
                        if(model.length>0){
                            $("#dlgObjectProperty").parent().css("width","1100px");
                        }
                        else{
                            $("#dlgObjectProperty").parent().css("width","700px");
                        }

                        let dom = document.createElement("div");
                        dom.innerHTML = NodePropertyPopup._getWorkerDeviceDetail(model, NodePropertyPopup.prop.procItemId);
                        return dom;
                    },
                });
            }
        });
    },
    _getWorkerDeviceDetail: function(model, procItemId){
        let modelHtml = '';
        let ctypeFlag = true;
        modelHtml += '<table class="tablePopover" border="1" width="100" id="tablePopover">';
        modelHtml += '  <thead>';
        modelHtml += '      <tr>';
        modelHtml += '          <td colspan="4" class="text-right">';
        modelHtml += "              <button class='btn bg-gradient-info exportWorkerDeviceXlsx' id='exportWorkerDeviceXlsx' data-procitemid='" + procItemId + "' data-model='" + JSON.stringify(model) + "'>"; // json에 쌍따옴표로 인하여 model이 data에 정상적으로 들어가지 않아 해당 라인만 쌍따옴표로 작성
        modelHtml += '                <i class="fas fa-file-excel"></i><span th:text="#{cost.index.page.print_xlsx}">xlsx 출력</span>';
        modelHtml += '             </button>';
        modelHtml += '          </td>'
        modelHtml += '      </tr>';
        modelHtml += '      <tr>';
        modelHtml += '          <td colspan="4" class="text-center">인원(인)</td>';
        modelHtml += '      </tr>';
        modelHtml += '  </thead>';
        modelHtml += '  <tbody>';
        modelHtml += '      <tr>';
        if(model){
            for(let i=0 ; i<model.length ; i++){
                if (i % 2 == 0) {
                    modelHtml += '      </tr>';
                    modelHtml += '      <tr>';
                }
                if (ctypeFlag && model[i].ctype == "device") {
                    modelHtml += '      </tr>';
                    modelHtml += '      <tr>';
                    modelHtml += '          <td colspan="4" class="text-center">장비(대)</td>';
                    modelHtml += '      </tr>';
                    modelHtml += '      <tr>';
                    ctypeFlag = false;
                }

                // 인원명
                modelHtml += '          <td class="text-center">'+model[i].name +'</td>';

                // 인원누계
                modelHtml += '          <td class="text-center">'+model[i].amount+'</td>';
            }
            if (model.length % 2 == 1) {
                modelHtml += '          <td class="text-center"></td>';
            }
            modelHtml += '      </tr>';
        }
        modelHtml += '  </tbody>';
        modelHtml += '</table>';
        return modelHtml;
    },
    _clickExportWorkerDeviceXlsx: function(){
        $(".tablePopover").each(function(index, item){
            $(this).on("click", ".exportWorkerDeviceXlsx", function(){
                let model = $(this).data("model");
                let procItemId = $(this).data("procitemid");
                if(model.length > 0){
                    let param =  "procItemId=" + procItemId;
                    window.open("/commonModal/selectModelWorkerDeviceDetailExcel/" + procItemId + "/R")
                }
                else{
                    showErrorAlert("ALERT", "데이터가 없습니다.");
                }
            });
        });
    },
    _popoverInit: function(){
        $("#costPopover").on("click", function(){
            $("#costPopover").popover("show");
            $(".popover-body").css({"max-height":"700px", "overflow-y":"auto"});
            NodePropertyPopup._clickExportXlsx();
            $(this).toggleClass('on');
        });

        $("#workerDevicePopover").on("click", function(){
            $("#workerDevicePopover").popover("show");
            $(".popover-body").css({"max-height":"500px", "overflow-y":"auto"});
            NodePropertyPopup._clickExportWorkerDeviceXlsx();
            $(this).toggleClass('on');
        });
    }

}