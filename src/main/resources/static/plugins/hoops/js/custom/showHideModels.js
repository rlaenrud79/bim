let ShowHideModels = {
    prop:{
        checkState: []
    },
    init: function(){
        ShowHideModels._setUpModelSelectDialog();
        ShowHideModels._showHideModelsHtml();
        ShowHideModels._allCheck();
        ShowHideModels._checkApply();
        ShowHideModels._showWorks();
    },
    //모델 선택 다이얼로그 초기 설정
    _setUpModelSelectDialog: function(){
        $("#dialogSelectModels").dialog({
            autoOpen: false,
            position : {my: "right bottom", at: "right bottom", of: $("#viewerContainer-canvas-container")},
            width    : 500,
            maxHeight: 500
        });
    },
    // 모델 표시/비표시 다이얼로그 내용
    _showHideModelsHtml : function (){
        let html = "";
        html += "<table class='w-100 table table-sm table-bordered table-striped'>";
        html += "<thead>";
        html += "<tr><th><button class='.btn btn-primary' id='applyHideModels'>적용</button></th>";
        html += "<th colspan='2'>";
        html += "   <select class='custom-select' id='workName'>";
        html += "       <option value='0'>::공종::</option>";
        $.each(distinctWorkName, function(key, value){
            let selectKey = Number(key)+1;
            html += "<option value=" + selectKey + ">" + value + "</option>";
        });
        html += "   </select>";
        html += "</th></tr>";
        html += "<tr><th><input type='checkbox' id='allCheck'><label for='allCheck'/></th><th>공종</th><th>모델 명</th></tr>";
        html += "</thead>";
        html += "<tbody id='tbShowHideModels'>";
        $.each(models, function (key, value){
            html += "<tr><td><input class='modelShowHide' type='checkbox' id='chk_" + key + "' data-modelname='" + value + "' checked/></td>";
            html += "<td><label for='chk_" + key + "' style='cursor:pointer;'>" + workName[key] + "</label></td>";
            html += "<td><label for='chk_" + key + "' style='cursor:pointer;'>" + value + "</label></td></tr>";
            ShowHideModels.prop.checkState[key] = true;
        });
        html += "</body>";
        html += "</table>";
        $("#dialogSelectModels").html(html);
    },
    // 모델 표시/비표시 이벤트 정의
    _showHideByCheckbox: function(){
        $.each(models, function(key, value){
           if($("#chk_"+key).data("modelname") != undefined ){
               HwvUtility.cwv = hwv;
               let nodeIds = HwvUtility.getNodeIdsFromName( $("#chk_"+key).data("modelname"));
               let liId = "#visibility_part_" + nodeIds[0];

               ShowHideModels.prop.checkState[key] = $("#chk_"+key).is(":checked");

               if( $("#chk_"+key).is(":checked") ){
                   //hwv.model.setNodesOpacity( nodeIds, 1);
                   hwv.model.setNodesVisibility(nodeIds, true);
                   /*
                   if ( $(liId).hasClass("partHidden") ) {
                       $(liId).children("div").trigger("click");
                   }*/
               }
               else{
                   hwv.model.setNodesVisibility(nodeIds, false);
                   /*
                   if ( !$(liId).hasClass("partHidden") ) {
                       //hwv.model.setNodesOpacity(nodeIds, 0);
                       $(liId).children("div").trigger("click");
                   }*/
               }
           }
       });
    },
    _allCheck: function(){
        $("#dialogSelectModels").find("#allCheck").change(function(){
            executeAllCheck($("#dialogSelectModels"), $("#allCheck"), ".modelShowHide");
        });
    },
    _checkApply: function(){
        $("#dialogSelectModels").find("#applyHideModels").on("click", function(){
          ShowHideModels._showHideByCheckbox();
        });
    },
    _showWorks: function(){
        $("#dialogSelectModels").find("#workName").change(function(){
            $("#allCheck").prop("checked", false);
            $("#tbShowHideModels").empty();
            let html ="";
            if($("#workName").val() === "0"){
                $.each(models, function(key, value){
                    if(ShowHideModels.prop.checkState[key] === true){
                        html += "<tr><td><input class='modelShowHide' type='checkbox' id='chk_" + key + "' data-modelname='" + value + "' checked/></td>";
                    }
                    else{
                        html += "<tr><td><input class='modelShowHide' type='checkbox' id='chk_" + key + "' data-modelname='" + value + "'/></td>";
                    }
                    html += "<td><label for='chk_" + key + "' style='cursor:pointer;'>" + workName[key] + "</label></td>";
                    html += "<td><label for='chk_" + key + "' style='cursor:pointer;'>" + value + "</label></td></tr>";
                });
            }
            else{
                $.each(models, function(key, value){
                    if(workName[key] === distinctWorkName[ Number($("#workName").val()) - 1 ]){
                        if(ShowHideModels.prop.checkState[key] === true){
                            html += "<tr><td><input class='modelShowHide' type='checkbox' id='chk_" + key + "' data-modelname='" + value + "' checked/></td>";
                        }
                        else{
                            html += "<tr><td><input class='modelShowHide' type='checkbox' id='chk_" + key + "' data-modelname='" + value + "'/></td>";
                        }
                        html += "<td><label for='chk_" + key + "' style='cursor:pointer;'>" + workName[key] + "</label></td>";
                        html += "<td><label for='chk_" + key + "' style='cursor:pointer;'>" + value + "</label></td></tr>";
                    }
                });
            }
            $("#tbShowHideModels").append(html);
        });
    }

}