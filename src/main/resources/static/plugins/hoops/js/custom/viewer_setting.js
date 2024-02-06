$(function(){
// save my viewer settings
    $("#viewer-settings-ok-button").on("click", function () {
        let settings = {}
        settings["general"] = {};
        settings["general"]["projection-mode"] = $("#settings-projection-mode").val();
        settings["general"]["framerate"] = $("#settings-framerate").val();
        settings["general"]["hidden-line-opacity"] = $("#settings-hidden-line-opacity").val();
        settings["general"]["show-backfaces"] = $("#settings-show-backfaces").is(":checked")
        settings["general"]["show-capping-geometry"] = $("#settings-show-capping-geometry").is(":checked")
        settings["general"]["enable-face-line-selection"] = $("#settings-enable-face-line-selection").is(":checked")
        settings["general"]["select-scene-invisible"] = $("#settings-select-scene-invisible").is(":checked")
        settings["general"]["orbit-mode"] = $("#settings-orbit-mode").is(":checked")
        settings["effects"] = {};
        settings["effects"]["ambient-occlusion"] = $("#settings-ambient-occlusion").is(":checked")
        settings["effects"]["ambient-occlusion-radius"] = $("#settings-ambient-occlusion-radius").val();
        settings["effects"]["anti-aliasing"] = $("#settings-anti-aliasing").is(":checked")
        settings["effects"]["bloom-enabled"] = $("#settings-bloom-enabled").is(":checked")
        settings["effects"]["bloom-intensity"] = $("#settings-bloom-intensity").val();
        settings["effects"]["bloom-threshold"] = $("#settings-bloom-threshold").val();
        settings["effects"]["silhouette-enabled"] = $("#settings-silhouette-enabled").is(":checked")
        settings["effects"]["reflection-enabled"] = $("#settings-reflection-enabled").is(":checked")
        settings["effects"]["shadow-enabled"] = $("#settings-shadow-enabled").is(":checked")
        settings["effects"]["shadow-interactive"] = $("#settings-shadow-interactive").is(":checked")
        settings["effects"]["shadow-blur-samples"] = $("#settings-shadow-blur-samples").val();
        settings["axis"] = {};
        settings["axis"]["axis-triad"] = $("#settings-axis-triad").is(":checked")
        settings["axis"]["nav-cube"] = $("#settings-nav-cube").is(":checked")
        settings["pointcloud"] = {};
        settings["pointcloud"]["splat-rendering-enabled"] = $("#settings-splat-rendering-enabled").is(":checked")
        settings["pointcloud"]["splat-rendering-size"] = $("#settings-splat-rendering-size").val();
        settings["pointcloud"]["splat-rendering-point-size-unit"] = $("#settings-splat-rendering-point-size-unit").val();
        settings["pointcloud"]["eye-dome-lighting-enabled"] = $("#settings-eye-dome-lighting-enabled").is(":checked")
        settings["color"] = {};
        settings["color"]["background-top"] = $("#settings-background-top").val();
        settings["color"]["background-bottom"] = $("#settings-background-bottom").val();
        settings["color"]["capping-face-color"] = $("#settings-capping-face-color").val();
        settings["color"]["capping-line-color"] = $("#settings-capping-line-color").val();
        settings["color"]["selection-color-body"] = $("#settings-selection-color-body").val();
        settings["color"]["selection-color-face-line"] = $("#settings-selection-color-face-line").val();
        settings["color"]["measurement-color"] = $("#settings-measurement-color").val();
        settings["color"]["pmi-color"] = $("#settings-pmi-color").val();
        settings["color"]["pmi-enabled"] = $("#settings-pmi-enabled").is(":checked")
        settings["walkmode"] = {};
        settings["walkmode"]["walk-mode"] = $("#settings-walk-mode").val();
        settings["walkmode"]["walk-rotation"] = $("#settings-walk-rotation").val();
        settings["walkmode"]["walk-speed"] = $("#settings-walk-speed").val();
        settings["walkmode"]["walk-elevation"] = $("#settings-walk-elevation").val();
        settings["walkmode"]["walk-view-angle"] = $("#settings-walk-view-angle").val();
        settings["walkmode"]["mouse-look-enabled"] = $("#settings-mouse-look-enabled").is(":checked")
        settings["walkmode"]["mouse-look-speed"] = $("#settings-mouse-look-speed").val();
        settings["walkmode"]["bim-mode-enabled"] = $("#settings-bim-mode-enabled").is(":checked")
        settings["drawing"] = {};
        settings["drawing"]["drawing-background"] = $("#settings-drawing-background").val();
        settings["drawing"]["drawing-sheet"] = $("#settings-drawing-sheet").val();
        settings["drawing"]["drawing-sheet-shadow"] = $("#settings-drawing-sheet-shadow").val();
        settings["drawing"]["drawing-background-enabled"] = $("#settings-drawing-background-enabled").is(":checked")
        if ($("#settings-tab-label-floorplan").length > 0) {
            settings["floorplan"] = {};
            settings["floorplan"]["floorplan-active"] = $("#settings-floorplan-active").is(":checked")
            settings["floorplan"]["floorplan-track-camera"] = $("#settings-floorplan-track-camera").is(":checked")
            settings["floorplan"]["floorplan-orientation"] = $("#settings-floorplan-orientation").val();
            settings["floorplan"]["floorplan-auto-activate"] = $("#settings-floorplan-auto-activate").val();
            settings["floorplan"]["floorplan-feet-per-pixe"] = $("#settings-floorplan-feet-per-pixe").val();
            settings["floorplan"]["floorplan-zoom"] = $("#settings-floorplan-zoom").val();
            settings["floorplan"]["floorplan-background-opacity"] = $("#settings-floorplan-background-opacity").val();
            settings["floorplan"]["floorplan-border-opacity"] = $("#settings-floorplan-border-opacity").val();
            settings["floorplan"]["floorplan-avatar-opacity"] = $("#settings-floorplan-avatar-opacity").val();
            settings["floorplan"]["floorplan-background-color"] = $("#settings-floorplan-background-color").val();
            settings["floorplan"]["floorplan-border-color"] = $("#settings-floorplan-border-color").val();
            settings["floorplan"]["floorplan-avatar-color"] = $("#settings-floorplan-avatar-color").val();
            settings["floorplan"]["floorplan-avatar-outline-color"] = $("#settings-floorplan-avatar-outline-color").val();
        }
        let strSettings = JSON.stringify(settings);
        let userId = $("#userId").val();
        //console.log(strSettings);

        let param = {id: userId, viewerSetting: strSettings};
        reqPut("/account/viewerSetting"
            , param
            , (result) => {
                if(result == 'OK'){
                    console.log("viewerSetting is saved, successfully!");
                }
            }
            , function (xhr) {
                console.log("viewerSetting is failed.")
                showErrorAlert("ALERT", $.parseJSON(xhr.responseJSON).error);
            });

    });
});

let byDbSetting = false;

function viewerBgColor(){
    let param = "id=" + $("#userId").val();
    reqGet("/account/viewerSetting?"+param
        ,(result) => {
            if( result != "") {
                let pattern = /"background-top":"rgb\((.*?)\).*"background-bottom":"rgb\((.*?)\)"/;
                let rtn = pattern.exec(result);
                if(!rtn || rtn.length < 2){
                    return false;
                }
                let topColor = rtn[1].replace(" ", "").split(",");
                let btmColor = rtn[2].replace(" ", "").split(",");
                hwv.view.setBackgroundColor(
                    new Communicator.Color(Number(topColor[0]), Number(topColor[1]), Number(topColor[2])),
                    new Communicator.Color(Number(btmColor[0]), Number(btmColor[1]), Number(btmColor[2]))
                );
            }
        }
        , function (xhr) {
            console.log("viewerSetting is failed.")
            showErrorAlert("ALERT", $.parseJSON(xhr.responseJSON).error);
        });
}

function setOrbitRotate() {
    let cameraOrbitOperator = hwv.getOperatorManager().getOperator(Communicator.OperatorId.Orbit);
    cameraOrbitOperator.setBimOrbitEnabled(true);
    // left클릭 customize를 위해 버튼설정 삭제, left클릭 기능은 마우스 휠버튼으로 대체
    // cameraOrbitOperator.setPrimaryButton(Communicator.Button.Left);
}

// s.j.h : 마우스 휠에 의한 줌을 할 경우 객체에 가까이 갈 경우에 속도를 줄이도록 설정함
function setMouseWheelAdjustCameraTarget() {
    let zoomOperator = hwv.getOperatorManager().getOperator(Communicator.OperatorId.Zoom);
    zoomOperator.setMouseWheelAdjustCameraTarget(true);
}

// s.j.h Ctrl 키에 의해서 객체 선택에 대해 Toggle 할 수 있음
function setSingleEntityToggleModeEnabled(val) {
    hwv.selectionManager.setSingleEntityToggleModeEnabled(val);
}

function viewerSettingInit(){
    let param = "id=" + $("#userId").val();
    reqGet("/account/viewerSetting?"+param
        , (result) => {
            //console.log("getting viewerSetting is success!", result);
            viewerSettingByDb(result);
        }
        , function (xhr) {
            console.log("viewerSetting is failed.")
            showErrorAlert("ALERT", $.parseJSON(xhr.responseJSON).error);
        });
}
function viewerSettingByDb(strViewerSetting) {
    if(!strViewerSetting || strViewerSetting === ""){
        return false;
    }
    // apply viewer setting by session from database
    let viewerSetting = JSON.parse(strViewerSetting);
    //console.log("viewerSetting from db JSON:", viewerSetting);
    if (Object.keys(viewerSetting).length > 0 && !byDbSetting) {
        $("#settings-projection-mode").val(viewerSetting.general["projection-mode"]);
        $("#settings-framerate").val(viewerSetting.general["framerate"]);
        $("#settings-hidden-line-opacity").val(viewerSetting.general["hidden-line-opacity"]);
        $("#settings-show-backfaces").prop('checked', viewerSetting.general["show-backfaces"]);
        $("#settings-show-capping-geometry").prop('checked', viewerSetting.general["show-capping-geometry"]);
        $("#settings-enable-face-line-selection").prop('checked', viewerSetting.general["enable-face-line-selection"]);
        $("#settings-select-scene-invisible").prop('checked', viewerSetting.general["select-scene-invisible"]);
        $("#settings-orbit-mode").prop('checked', viewerSetting.general["orbit-mode"]);
        $("#settings-ambient-occlusion").prop('checked', viewerSetting.effects["ambient-occlusion"]);
        $("#settings-ambient-occlusion-radius").val(viewerSetting.effects["ambient-occlusion-radius"]);
        $("#settings-anti-aliasing").prop('checked', viewerSetting.effects["anti-aliasing"]);
        $("#settings-bloom-enabled").prop('checked', viewerSetting.effects["bloom-enabled"]);
        $("#settings-bloom-intensity").val(viewerSetting.effects["bloom-intensity"]);
        $("#settings-bloom-threshold").val(viewerSetting.effects["bloom-threshold"]);
        $("#settings-silhouette-enabled").prop('checked', viewerSetting.effects["silhouette-enabled"]);
        $("#settings-reflection-enabled").prop('checked', viewerSetting.effects["reflection-enabled"]);
        $("#settings-shadow-enabled").prop('checked', viewerSetting.effects["shadow-enabled"]);
        $("#settings-shadow-interactive").prop('checked', viewerSetting.effects["shadow-interactive"]);
        $("#settings-shadow-blur-samples").val(viewerSetting.effects["shadow-blur-samples"]);
        $("#settings-axis-triad").prop('checked', viewerSetting.axis["axis-triad"]);
        $("#settings-nav-cube").prop('checked', viewerSetting.axis["nav-cube"]);
        $("#settings-splat-rendering-enabled").prop('checked', viewerSetting.pointcloud["splat-rendering-enabled"]);
        $("#settings-splat-rendering-size").val(viewerSetting.pointcloud["splat-rendering-size"]);
        $("#settings-splat-rendering-point-size-unit").val(viewerSetting.pointcloud["splat-rendering-point-size-unit"]);
        $("#settings-eye-dome-lighting-enabled").prop('checked', viewerSetting.pointcloud["eye-dome-lighting-enabled"]);
        $("#settings-background-top").val(viewerSetting.color["background-top"]);
        $("#settings-background-bottom").val(viewerSetting.color["background-bottom"]);
        $("#settings-capping-face-color").val(viewerSetting.color["capping-face-color"]);
        $("#settings-capping-line-color").val(viewerSetting.color["capping-line-color"]);
        $("#settings-selection-color-body").val(viewerSetting.color["selection-color-body"]);
        $("#settings-selection-color-face-line").val(viewerSetting.color["selection-color-face-line"]);
        $("#settings-measurement-color").val(viewerSetting.color["measurement-color"]);
        $("#settings-pmi-color").val(viewerSetting.color["pmi-color"]);
        $("#settings-pmi-enabled").prop('checked', viewerSetting.color["pmi-enabled"]);

        $("#settings-walk-mode").val(viewerSetting.walkmode["walk-mode"]);
        $("#settings-walk-rotation").val(viewerSetting.walkmode["walk-rotation"]);
        $("#settings-walk-speed").val(viewerSetting.walkmode["walk-speed"]);
        $("#settings-walk-elevation").val(viewerSetting.walkmode["walk-elevation"]);
        $("#settings-walk-view-angle").val(viewerSetting.walkmode["walk-view-angle"]);
        $("#settings-mouse-look-enabled").prop('checked', viewerSetting.walkmode["mouse-look-enabled"]);
        $("#settings-mouse-look-speed").val(viewerSetting.walkmode["mouse-look-speed"]);
        $("#settings-bim-mode-enabled").prop('checked', viewerSetting.walkmode["bim-mode-enabled"]);
        $("#settings-drawing-background").val(viewerSetting.drawing["drawing-background"]);
        $("#settings-drawing-sheet").val(viewerSetting.drawing["drawing-sheet"]);
        $("#settings-drawing-sheet-shadow").val(viewerSetting.drawing["drawing-sheet-shadow"]);
        $("#settings-drawing-background-enabled").prop('checked', viewerSetting.drawing["drawing-background-enabled"]);
        if (viewerSetting.floorplan) {
            $("#settings-floorplan-active").prop('checked', viewerSetting.floorplan["floorplan-active"]);
            $("#settings-floorplan-track-camera").prop('checked', viewerSetting.floorplan["floorplan-track-camera"]);
            $("#settings-floorplan-orientation").val(viewerSetting.floorplan["floorplan-orientation"]);
            $("#settings-floorplan-auto-activate").val(viewerSetting.floorplan["floorplan-auto-activate"]);
            $("#settings-floorplan-feet-per-pixe").val(viewerSetting.floorplan["floorplan-feet-per-pixe"]);
            $("#settings-floorplan-zoom").val(viewerSetting.floorplan["floorplan-zoom"]);
            $("#settings-floorplan-background-opacity").val(viewerSetting.floorplan["floorplan-background-opacity"]);
            $("#settings-floorplan-border-opacity").val(viewerSetting.floorplan["floorplan-border-opacity"]);
            $("#settings-floorplan-avatar-opacity").val(viewerSetting.floorplan["floorplan-avatar-opacity"]);
            $("#settings-floorplan-background-color").val(viewerSetting.floorplan["floorplan-background-color"]);
            $("#settings-floorplan-border-color").val(viewerSetting.floorplan["floorplan-border-color"]);
            $("#settings-floorplan-avatar-color").val(viewerSetting.floorplan["floorplan-avatar-color"]);
            $("#settings-floorplan-avatar-outline-color").val(viewerSetting.floorplan["floorplan-avatar-outline-color"]);
        }

        $("#viewer-settings-apply-button").trigger("click");

        _updateEnabledStyle("settings-pmi-enabled", ["settings-pmi-color-style"], ["settings-pmi-color"], $("#settings-pmi-enabled").prop("checked"));
        _updateEnabledStyle("settings-splat-rendering-enabled", ["settings-splat-enabled-style"], ["settings-splat-rendering-size", "settings-splat-rendering-point-size-unit"], $("#settings-splat-rendering-enabled").prop("checked"));
        _updateEnabledStyle("settings-mouse-look-enabled", ["settings-mouse-look-style"], ["settings-mouse-look-speed"], $("#settings-mouse-look-enabled").prop("checked"));
        _updateEnabledStyle("settings-bim-mode-enabled", [], [], $("#settings-bim-mode-enabled").prop("checked"));
        _updateEnabledStyle("settings-bloom-enabled", ["settings-bloom-style"], ["settings-bloom-intensity", "settings-bloom-threshold"], $("#settings-bloom-enabled").prop("checked"));
        _updateEnabledStyle("settings-shadow-enabled", ["settings-shadow-style"], ["settings-shadow-blur-samples", "settings-shadow-interactive"], $("#settings-shadow-enabled").prop("checked"));
        _updateEnabledStyle("settings-silhouette-enabled", [], [], $("#settings-silhouette-enabled").prop("checked"));
        _updateEnabledStyle(null, ["walk-mouse-look-text", "settings-mouse-look-style", "walk-navigation-keys"], ["settings-mouse-look-enabled", "settings-mouse-look-speed"], Number(viewerSetting.walkmode["walk-mode"]) === 1);
        _updateEnabledStyle("settings-mouse-look-enabled", ["settings-mouse-look-style"], ["settings-mouse-look-speed"], Number(viewerSetting.walkmode["walk-mode"]) === 1 && $("#settings-mouse-look-enabled").prop("checked"));

        if (viewerSetting.floorplan) {
            _updateEnabledStyle(null, ["settings-floorplan-zoom-style"], ["settings-floorplan-zoom"], $("#settings-floorplan-track-camera").prop("checked"));
            _updateEnabledStyle(null, ["settings-floorplan-feet-per-pixel-style"], ["settings-floorplan-feet-per-pixel"], $("#settings-floorplan-track-camera").prop("checked"));
        }

        byDbSetting = false;
    } // end of if
}

function sleepPromise(ms) {
    return new Promise((r) => setTimeout(r, ms));
}

let _updateEnabledStyle = function(a, d, c, k) {
    null !== a && $("#" + a).prop("checked", k);
    if (k)
        for (var b = 0; b < d.length; b++)
            a = d[b],
                $("#" + a).removeClass("grayed-out");
    else
        for (b = 0; b < d.length; b++)
            a = d[b],
                $("#" + a).addClass("grayed-out");
    for (d = 0; d < c.length; d++)
        $("#" + c[d]).prop("disabled", !k)
}

function chgColorProcessStatus(exchangeIds, setDate) {
    let completeIds = [];
    let incompleteIds = [];

    // 공정기간 상태에 따른 exchange_id를 완료/미완료로 구분하여 저장
    for (let i = 0; i < allTaskExchangeIds.length; i++) {
        let jsonData = allTaskExchangeIds[i];
        // 공정 시작일이 2024-01-01 이전 공정만
        if (moment(jsonData.startDate).isBefore("2024-01-01")) {
            // TODO 조건에 맞는 공정 필터
            if (moment(jsonData.endDate).isBefore(setDate)) {
                completeIds = completeIds.concat(jsonData.exchangeIds);
            } else if (moment(jsonData.endDate).isSameOrAfter(setDate)) {
                incompleteIds = incompleteIds.concat(jsonData.exchangeIds);
            }
        }
    }

    // 전체 모델 색상을 회색으로
    HwvUtility.getNodeIdsFromExchangeIds(allIds);
    let allNodeId = HwvUtility.nodeIdsFromExchangeIds;
    HwvUtility.unsetFaceColorSelectedNodeIds(allNodeId);
    hwv.model.setNodesFaceColor(allNodeId, new Communicator.Color(Number(224), Number(224), Number(224)));

    // 금일 날짜 기준으로 완료된 공정 -> color.blue()
    if (completeIds.length > 0) {
        HwvUtility.getNodeIdsFromExchangeIds(completeIds);
        let completeNodeId = HwvUtility.nodeIdsFromExchangeIds;
        hwv.model.setNodesFaceColor(completeNodeId, Communicator.Color.blue());
    }

    // 금일 날짜 기준으로 미완료된 공정 -> color(255,112,32)
    if (incompleteIds.length > 0) {
        HwvUtility.getNodeIdsFromExchangeIds(incompleteIds);
        let incompleteNodeId = HwvUtility.nodeIdsFromExchangeIds;
        hwv.model.setNodesFaceColor(incompleteNodeId, new Communicator.Color(Number(255), Number(112), Number(32))); // 주황색
    }
}
