let UserProperty = {
    _modelIds: '',
    _modelId: '',
    _assemblyId: '',
    _enum_crud:{"CREATE":0, "READ":1, "UPDATE":2, "DELETE":3},
    _crud: 0,
    /**
     * 표시되는 모델이 하나일 경우에만 유저 프로퍼티 추가 인터페이스 활성화, called by modelStructureReady
     */
    init: function (){
        UserProperty._modelIds = modelIds;
        //console.log('modelIds in init:', modelIds); // ex: '123,456,789'
        let arrModelIds = UserProperty._modelIds.split(',');
        if( arrModelIds.length === 1){
            UserProperty._modelId = arrModelIds[0];
            // create dialog html
            UserProperty._appendDialogTemplate();
            // open dialog and enable save button click event
            UserProperty._enableDialogAction();
        }
    },
    /**
     * 유저가 선택한 노드를 통해서 유저 프로퍼티 추가 버튼을 표시, called by selectionArray
     * @param selectionEvent: 유저의 선택 이벤트
     */
    enableAddProperty : function(selectionEvents){
        if( UserProperty._modelIds.split(',').length !== 1){
            return false;
        }
        for (const selectionEvent of selectionEvents){
            const selection = selectionEvent.getSelection();
            //console.log('SelectionType:',selection.getSelectionType());
            if (selection && selection.getSelectionType() !== Communicator.SelectionType.None){
                let nodeId = selection.getNodeId();
                let nodeType = hwv.model.getNodeType(nodeId);
                //console.log('NodeType:', nodeType);
                let exchangeId = HwvUtility.getNodeExchangeIdByNodeId(nodeId);
                let assemblyId = UserProperty._getNodeAssemblyId(UserProperty._modelId, exchangeId);
                UserProperty._assemblyId = assemblyId;
                if( nodeType === Communicator.NodeType.Body || nodeType === Communicator.NodeType.BodyInstance ){
                    // NOTHING TO DO
                }else{
                    if( $("#modelTreeTab").hasClass("browser-tab-selected") ){
                        if( $("body").find("#addProperty").length > 0 ) {
                            $("body").find("#addProperty").remove();
                        }
                        setTimeout(
                            function(){
                                if( $("#propertyWindow").find("#propertyDiv_Value").text() === 'Value' ){
                                    let btnAdd = '';
                                    btnAdd += ' <i id="addProperty"'
                                           + ' data-modelid="' + UserProperty._modelId + '"'
                                           + ' data-assemblyid="' + assemblyId + '"'
                                           + ' data-exchangeid="' + exchangeId + '"'
                                           + ' style="cursor:pointer;color:#4A8AF4;" class="fas fa-plus-square"></i>';
                                    $("#propertyWindow").find("#propertyDiv_Value").append(btnAdd);
                                    UserProperty._getUserProperty(assemblyId);
                                }
                            }, 100
                        ); // end of setTimeout
                    }
                }
            }
        }
    },
    /**
     * 선택한 노드의 hoops assembly id를 취득
     * @param modelId 데이터베이스 modelId
     * @param exchangeId hoops의 exchage id
     * @returns {*} hoops assembly id
     * @private
     */
    _getNodeAssemblyId : function (modelId, exchangeId){
        //console.log("modelId:",modelId, "exchangeId:",exchangeId);
        let assemblyId;
        reqGetSync("/modelingViewerApi/assemblyId/" + exchangeId
            ,function(data){
                //console.log("data", data);
                assemblyId = data.id;
            }
            , function(xhr){
                showErrorAlert("ALERT", $.parseJSON(JSON.stringify(xhr.responseJSON)).error);
            },function(){}, "json")
        return assemblyId;
    },
    _appendDialogTemplate : function(){
        if( $("body").find("#dlg_add_property").length === 0 ){
            let dlg = '';
            dlg += '<div id="dlg_add_property" title="Add Property">';
            dlg += '    <input type="hidden" id="txtAttributeId" />';
            dlg += '    <table class="w-100">';
            dlg += '        <tr>';
            dlg += '            <td><label for="txtProperty" class="mb-0 mr-2">Property:</label></td>';
            dlg += '            <td><input type="text" id="txtAttributeName" class="d-inline-block form-control w-75"/></td>';
            dlg += '        </tr>';
            dlg += '        <tr>';
            dlg += '            <td><label for="txtValue" class="mb-0 mr-2">Value:</label></td>';
            dlg += '            <td><input type="text" id="txtAttributeValue" class="d-inline-block form-control w-75"/></td>';
            dlg += '        </tr>';
            dlg += '        <tr><td colspan="2" style="text-align: center"><button id="btnAddProperty" class="btn bg-gradient-primary ml-2">Save</button></td></tr>';
            dlg += '    </table>';
            dlg += '</div>';
            $("body").append(dlg);
        }
    },
    _enableDialogAction : function(){
        // setup dialog
        $("body").find("#dlg_add_property").dialog({
            autoOpen: false,
            position : {my: "center", at: "center", of: window},
            show     : {effect: "blind", duration: 100},
            hide     : {effect: "explode", duration: 100},
            width    : 500,
            maxHeight: 500
        });
        // open dialog for insert
        $("body").on("click", "#addProperty", function(){
            UserProperty._crud = UserProperty._enum_crud.CREATE;
            $("body").find("#dlg_add_property").dialog("open");
            $("body").find("#dlg_add_property").find("#btnAddProperty").html("Save");
            $("body").find("#dlg_add_property").find("#txtAttributeName").attr("disabled", false);
        });
        // open dialog for update
        $("body").on("click", ".attribut-update", function(){
            UserProperty._crud = UserProperty._enum_crud.UPDATE;
            let attributeid = $(this).data("attributeid");
            let attributename = $(this).data("attributename");
            let attributevalue = $(this).data("attributevalue");

            let $dlg = $("body").find("#dlg_add_property");
            $dlg.find("#txtAttributeId").val(attributeid);
            $dlg.find("#txtAttributeName").val(attributename);
            $dlg.find("#txtAttributeName").attr("disabled", true);
            $dlg.find("#txtAttributeValue").val(attributevalue);
            $dlg.find("#btnAddProperty").html("Update");
            $dlg.dialog("open");
        });
        // save & update button from popup dialog
        $("#dlg_add_property").on("click", "#btnAddProperty", function(){
            let reqData = {"modelingAssembly":{"id":0}, "attributeName":"", "attributeType":"String", "attributeValue":"", "userAttr":true};
            reqData.modelingAssembly.id = $("#addProperty").data("assemblyid");
            reqData.attributeName = $("#txtAttributeName").val();
            reqData.attributeValue = $("#txtAttributeValue").val();

            if( UserProperty._validateInput($("#txtAttributeName"), $("#txtAttributeValue")) == false ){
                return false;
            }

            if(UserProperty._crud === UserProperty._enum_crud.CREATE){
                // 이미 존재하는 프로퍼티가 있으면 삽입하지 않는다.
                let rtn = UserProperty._getCntProperty(reqData.modelingAssembly.id, reqData.attributeName);
                if( rtn.result == false ){
                    showErrorAlert("ALERT", rtn.message);
                    return false;
                }
                UserProperty._actionForIns(reqData);
            }else if(UserProperty._crud === UserProperty._enum_crud.UPDATE){
                reqData.id = $("#txtAttributeId").val(); // update용 id
                UserProperty._actionForUpd(reqData);
            }

        }); // end of btnAddPropery click event
    },
    _validateInput: function($attrName, $attrValue){
        if( $attrName.val().trim() == ""  ) {
            let msgCode = "system.modeling_service.error_input_property";
            reqGetSync("/commonApi/translate/"+msgCode,
                (rtn)=>{
                    showErrorAlert("ALERT", rtn.message);
                    $attrName.focus();
                    return false;
                })
            return false;
        }
        if( $attrValue.val().trim() == "" ) {
            let msgCode = "system.modeling_service.error_input_value";
            reqGetSync("/commonApi/translate/"+msgCode,
                (rtn)=>{
                    showErrorAlert("ALERT", rtn.message);
                    $attrValue.focus();
                    return false;
                })
            return false;
        }
        return true;
    },
    /**
     * 유저가 추가한 프로퍼티를 표시, 복수 모델일 경우 R, 단수 모델일 경우 CUD
     * @param modelingAssemblyId hoops assembly id
     * @private
     */
    _getUserProperty: function (modelingAssemblyId){
        reqGet("/modelingViewerApi/userProperty/" + modelingAssemblyId
            ,function(data){
                //console.log('modelingAssemblyId:',modelingAssemblyId,'data:', data);
                $("#propertyWindow").find(".tr_userproperty").remove();
                for( let i = 0; i < data.length; i++){
                    let dat = ' data-attributeid=' + data[i].id + ' data-assemblyid=' + data[i].modelingAssembly.id
                             +' data-attributename="' + data[i].attributeName + '" data-attributevalue="' + data[i].attributeValue + '"';
                    let upd = '<i style="cursor: pointer; color:#17A05D;;" class="fas fa-edit attribut-update" aria-hidden="true" ' + dat + '></i>';
                    let del = '<i style="cursor: pointer; color:#DD5347;" class="fas fa-trash attribute-delete" aria-hidden="true" ' + dat + '></i>';
                    let html = '<tr class="tr_userproperty">'
                              +'<td>' + data[i].attributeName + '</td>'
                              +'<td>' + data[i].attributeValue + ' <span>' + upd + '</span> <span>'+ del + '</span></td>'
                              +'</tr>';
                    $("#propertyTable #propertyDiv_Name").parent().after(html);
                }
                if(data.length > 0 ) {
                    UserProperty._actionForDel();
                }
            }
            , function(xhr){
                showErrorAlert("ALERT", $.parseJSON(JSON.stringify(xhr.responseJSON)).error);
            },function(){}, "json")
    },
    _actionForIns(insData){
        reqPostJSON("/modelingViewerApi/userProperty"
            , JSON.stringify(insData)
            , (data) => {
                //console.log('data:', data);
                if (data.result) {
                    toastr.success(data.message);
                    UserProperty._getUserProperty(insData.modelingAssembly.id );
                } else {
                    toastr.error(data.message);
                }
            }
            , function (xhr) {
                showErrorAlert("ALERT", $.parseJSON(JSON.stringify(xhr.responseJSON)).error);
            });
    },
    _actionForUpd(updData){
        //console.log('updData:', updData);
        reqPutJSON("/modelingViewerApi/userProperty"
        , JSON.stringify(updData)
        , (data) => {
            if(data.result){
                toastr.success(data.message);
                UserProperty._getUserProperty(updData.modelingAssembly.id);
            }
        }
        , (xhr)=>{
            showErrorAlert("ALERT", $.parseJSON(JSON.stringify(xhr.responseJSON)).error);
        }, ()=>{});
    },
    _actionForDel: function(){
        $(".attribute-delete").click(function (){
            let attrId = 0;
            attrId = $(this).data("attributeid");
            //console.log("for delete:", attrId);
            reqDelete("/modelingViewerApi/userProperty/"+ attrId
                , (data) => {
                    //console.log('rtnData:', data);
                    if (data.result) {
                        toastr.success(data.message);
                        UserProperty._getUserProperty(UserProperty._assemblyId);
                    } else {
                        toastr.error(data.message);
                    }
                }
                , function (xhr) {
                    showErrorAlert("ALERT", xhr.responseJSON.error);
                }, ()=>{}, 'json');
        });
    },
    _getCntProperty: function (assemblyId, property){
        let rtn = {};
        reqGetSync("/modelingViewerApi/userProperty/" + assemblyId + "/" + property
            , (data) =>{
                rtn = data;
            }
            ,()=>{}, ()=> {}, "json" );
        return rtn;
    }
}
