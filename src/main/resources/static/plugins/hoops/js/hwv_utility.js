/**
  You can control the Hoops Web Viewer - Node's Opacity, Node's Selection
 */

let HwvUtility = {
  cwv                   : null, // HPViewPoint 생성 시, 메인페이지의 hwv를 cwv에 설정한다. hwv의 타입은 Communicator.WebViewer이다.
  _targetNodeIds        : [],
  nodeIdsFromName       : [],
  nodeIdsFromProperty   : [],
  nodeIdsFromExchangeIds: [],
  isGanttClicked: false,
  getNodeIdsFromName    : function (name) {
    if (!!!HwvUtility.cwv) return [];
    HwvUtility.nodeIdsFromName = [];
    let model = HwvUtility.cwv.model;
    let nodeId = model.getAbsoluteRootNode();
    HwvUtility._getNodeIdsFromName(nodeId, model, name);
    return HwvUtility.nodeIdsFromName;
  },
  /**
   * 노드의 이름을 통해서 nodeId를 배열로 취득한다.
   * @param {"Communicator.NodeId"} node
   * @param {"Communicator.Model"} model
   * @param {string} name
   */
  _getNodeIdsFromName       : function (nodeId, model, name) {
    let nodeName = model.getNodeName(nodeId);
    if (model.getNodeType(nodeId) == Communicator.NodeType.Body) {
      return;
    }
    if (nodeName != undefined && nodeName.toUpperCase().includes(name.toString().toUpperCase())) {
      HwvUtility.nodeIdsFromName.push(nodeId);
    }
    let children = model.getNodeChildren(nodeId);
    if (!children) {
      return;
    }
    for (let i = 0; i < children.length; i++) {
      HwvUtility._getNodeIdsFromName(children[i], model, name);
    }
  },
  getNodeNameFromId         : function (nodeId) {
    if (!!!HwvUtility.cwv) return "";
    let modelName = HwvUtility.cwv.model.getNodeName(nodeId);
    return modelName;
  },
  getNodeIdsFromExchangeIds : function (arrExchangeId) {
    HwvUtility.nodeIdsFromExchangeIds = [];
    let model = HwvUtility.cwv.model;
    let rootNodeId = model.getAbsoluteRootNode();
    HwvUtility._getNodeIdsFromExchangeIds(rootNodeId, model, arrExchangeId);
  },
  _getNodeIdsFromExchangeIds: function (nodeId, model, arrExchangeId) {
    let nodeExchangeId = model.getNodeExchangeId(nodeId);

    if (arrExchangeId.includes(nodeExchangeId)) {
      HwvUtility.nodeIdsFromExchangeIds.push(nodeId);
    }

    let children = model.getNodeChildren(nodeId);
    for (let i = 0; i < children.length; i++) {
      if (model.getNodeType(children[i]) === Communicator.NodeType.Body) {
        continue;
      }
      HwvUtility._getNodeIdsFromExchangeIds(children[i], model, arrExchangeId);
    }
  },
  /**
   * 노드의 프로퍼티 명을 통해서 해당하는 프로퍼티 명을 갖는 nodeId를 추출한다.
   */
  // property example: "Constraints/Base Constraint"
  getNodeIdsFromProperty: async function (searchProperty) {
    HwvUtility.nodeIdsFromProperty = [];
    let model = HwvUtility.cwv.model;
    let nodeIds = HwvUtility.getAllTargetNodeIds(model.getAbsoluteRootNode());
    for (let nodeId of nodeIds) {
      let nodeProperties = await model.getNodeProperties(nodeId);
      if (nodeProperties && Object.keys(nodeProperties).length > 0) {
        for (let _i = 0, _a = Object.keys(nodeProperties); _i < _a.length; _i++) {
          let propertyName = _a[_i];
          let propertyValue = nodeProperties[propertyName];
          if (propertyName === searchProperty) {
            HwvUtility.nodeIdsFromProperty.push({"nodeId": nodeId, "value": propertyValue});
          }
        }
      }
    }
    return HwvUtility.nodeIdsFromProperty;
  },
  getAllTargetNodeIds   : function (nodeId) {
    HwvUtility._targetNodeIds = [];
    HwvUtility._getAllTargetNodeIds(nodeId);
    return HwvUtility._targetNodeIds;
  },
  _getAllTargetNodeIds  : function (nodeId) {
    let model = HwvUtility.cwv.model;
    let type = model.getNodeType(nodeId);
    if (type === Communicator.NodeType.AssemblyNode || type === Communicator.NodeType.PartInstance
        || type === Communicator.NodeType.Part || type === Communicator.NodeType.BodyInstance) {
      HwvUtility._targetNodeIds.push(nodeId);
      let children = model.getNodeChildren(nodeId);
      for (let child of children) {
        HwvUtility._getAllTargetNodeIds(child);
      }
    }
  },
  hideRootNodeId        : function () {
    this.cwv.model.setNodesOpacity([HwvUtility.cwv.model.getAbsoluteRootNode()], 0.0);
  },
  showRootNodeId        : function () {
    this.cwv.model.setNodesOpacity([HwvUtility.cwv.model.getAbsoluteRootNode()], 1.0);
  },
  hideSelectedNodeIds   : function (arrNodeId) {
      this.setOpacitySelectedNodeIds(arrNodeId, 0.0);
  },
  showSelectedNodeIds   : function (arrNodeId) {
      this.setOpacitySelectedNodeIds(arrNodeId, 1.0);
  },
  /*
   * arrNodeId: array, opacity: number(0.0~1.0)
   */
  setOpacitySelectedNodeIds: function (arrNodeId, opacity) {
    if (typeof  arrNodeId === 'undefined') arrNodeId = this.nodeIdsFromExchangeIds;
    if (Array.isArray(arrNodeId)) {
      this.cwv.model.setNodesOpacity(arrNodeId, opacity);
    }
  },
  setFaceColorSelectedNodeIds: function (arrNodeId, color ) {
    if (typeof  arrNodeId === 'undefined') arrNodeId = this.nodeIdsFromExchangeIds;
    if (Array.isArray(arrNodeId)) {
      this.cwv.model.setNodesFaceColor(arrNodeId, color);
    }
  },
  unsetFaceColorSelectedNodeIds: function (arrNodeId ) {
    if (typeof  arrNodeId === 'undefined') arrNodeId = this.nodeIdsFromExchangeIds;
    if (Array.isArray(arrNodeId)) {
      this.cwv.model.unsetNodesFaceColor(arrNodeId);
    }
  },
  /* Set Nodes in the model be selected by exchange Ids
   * arrExchangeId: array, ex: ["교각기둥 - D = 2500mm.dffaf816e672bb7ff2202f068f39614da02a7c18", "교량받침 - 3500kN,H=150mm(양방향).dffaf816e672bb7ff2202f068f39614da02c1410"]
   */
  selectNodesFromExchangeIds: function(arrExchangeId){
	this.isGanttClicked = true;
    this.getNodeIdsFromExchangeIds(arrExchangeId);
    let targetNodeIds = this.nodeIdsFromExchangeIds;
	this.cwv.selectPart(); // clear the selected nodes and select first target node
    for( let i = 0; i < targetNodeIds.length; i++){
      this.cwv.getSelectionManager().selectNode(targetNodeIds[i], Communicator.SelectionMode.Add);
    }
    this.isGanttClicked = false;
  },
  getNodeExchangeIdByNodeId: function(nodeId){
    let nodeExchangeId = this.cwv.model.getNodeExchangeId(nodeId);
    return nodeExchangeId;
  },
  // 해당하는 NodeId의 Phasing Codes 를 취득
  getPhasingCodesByNodeId: async function(nodeId){
    let model = this.cwv.model;
    // 트리의 bodyInstance(6)을 선택한 경우 부모(PartInstance = 1)를 찾도록 한다.
    if(model.getNodeType(nodeId) === Communicator.NodeType.BodyInstance || model.getNodeType(nodeId) == Communicator.NodeType.Body){
      nodeId = model.getNodeParent(nodeId);
    }
    let result = await model.getNodeProperties(nodeId);

    // return (result["TYPE"] && result["TYPE"].indexOf('IFC') > -1) ? result["공정/Phasing Codes"] : result["Phasing/Phasing Codes"];
    return result["Phasing/Phasing Codes"];
  },
  // 선택된 node의 PhasingCodes 를 취득
  getSelectedPhasingCodes: async function(){
    const nodes = this.cwv.selectionManager.getResults();
    let phasingCodes = [];
	for(const node of nodes){
		phasingCodes.push(await HwvUtility.getPhasingCodesByNodeId(node.getNodeId()));
	};
    return phasingCodes;
  },
  // 해당하는 NodeId가 갖는 프로퍼티를 취득
   getPropertyByNodeId: async function(nodeId){
      let model = this.cwv.model;
      if(model.getNodeType(nodeId) === Communicator.NodeType.BodyInstance || model.getNodeType(nodeId) === Communicator.NodeType.Body){
        nodeId = model.getNodeParent(nodeId);
      }
      let result = await model.getNodeProperties(nodeId);
      return result;
    },
  // 해당하는 NodeId가 갖는 프로퍼티 명의 값을 취득
  getPropertyValueByNodeId: async function(nodeId, propertyName){
    let model = this.cwv.model;
    // 트리의 bodyInstance(6)을 선택한 경우 부모(PartInstance = 1)를 찾도록 한다.
    if(model.getNodeType(nodeId) === Communicator.NodeType.BodyInstance || model.getNodeType(nodeId) === Communicator.NodeType.Body){
      nodeId = model.getNodeParent(nodeId);
    }
    let result = await model.getNodeProperties(nodeId);
    return result[propertyName];
  },
  // 모델의 클릭 이벤트를 받아서 nodeId를 취득하고 그 nodeId에 해당하는 PhasingCodes 를 취득
  // ex) 메인 페이지의 hwv.setCallbacks({selectionArray: HwvUtility.getSelectedNodePhasingCodes});
  getSelectedNodePhasingCodes: async function(selectionEvents){
    for( const selectionEvent of selectionEvents){
      const selection = selectionEvent.getSelection();
      if (selection && selection.getSelectionType() !== Communicator.SelectionType.None) {
        let nodeId = selection.getNodeId();
        let phasingCodes = await HwvUtility.getPhasingCodesByNodeId(nodeId);
        return phasingCodes;
        // 또는 Phasing Codes 에 해당하는 Activity 를 찾아서 highlight
      }
    }
  },
  /**
   * nodeId의 마감재 값 및 Phasing Codes을 취득한다.
   * @param nodeId
   */
  getFinishingAndPhasingCodesByNodeId: async function(nodeId){
    let rtn = {"nodeId":-999, "name":"", "phasingCodes":"", "finishing":""};
    let model = this.cwv.model;
    // 트리의 bodyInstance(6)을 선택한 경우 부모(PartInstance = 1)를 찾도록 한다.
    if(model.getNodeType(nodeId) === Communicator.NodeType.BodyInstance || model.getNodeType(nodeId) === Communicator.NodeType.Body){
      nodeId = model.getNodeParent(nodeId);
    }
    rtn.nodeId = nodeId;
    rtn.name = model.getNodeName(nodeId);

    let finishingProperties = [];
    finishingProperties.push("Materials and Finishes/Structural Material");
    finishingProperties.push("Structural/Rebar Cover");
    finishingProperties.push("Structural/Rebar Cover - Bottom Face");
    finishingProperties.push("Structural/Rebar Cover - Other Faces");

    let phasingCodesProperty = "Phasing/Phasing Codes";

    let result = await model.getNodeProperties(nodeId);

    if( !result[phasingCodesProperty] ){
      //console.log ("PhasingCodes are NOT exist ") // nudefined, null, empty 체크
    }
    else{
      //console.log(phasingCodesProperty, result[phasingCodesProperty]);
      rtn.phasingCodes = result[phasingCodesProperty];
    }

    for(const k of finishingProperties){
      if( !result[k] ){ // nudefined, null, empty 체크
        //console.log ( k, result[k] );
      }
      else{
        rtn.finishing = result[k];
        break;
      }
    }
    return rtn;
  },
  highlightActivityByExchangeId: function (exchangeId){
    // Activity를 highlight 시키는 컨트롤러 함수를 Ajax로 호출
  }
}
