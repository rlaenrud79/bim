function reloadComponent(url, id, param) {
    if (url === "/common/chattingLayer"){
        let checkTryChattingLogInTime = sessionStorage.getItem("tryChattingLogInTime");
        let storageTime = new Date(checkTryChattingLogInTime);

        let timeDifference = (new Date().getTime()- storageTime.getTime()) / 1000; // 1초단위로 만들기 위해 1000을 나눔

        if ( (checkTryChattingLogInTime === null)||(timeDifference >= 60) ){
            reqGet(param === "" ? url : `${url}?${param}`, function (data) {
                sessionStorage.setItem("chattingAlertData", data);
                sessionStorage.setItem("tryChattingLogInTime", new Date());
                $(id).replaceWith(data);
                refreshTooltip();
            });
        }
        else{
            let lastChattingAlertData = sessionStorage.getItem("chattingAlertData");
            $(id).replaceWith(lastChattingAlertData);
            refreshTooltip();
        }
    }
    else{
        reqGet(param === "" ? url : `${url}?${param}`, function (data) {
            $(id).replaceWith(data);
            refreshTooltip();
        });
    }
}

function includeComponent(url, param, target) {
  reqGet(param === "" ? url : `${url}?${param}`, function (data) {
    $(target).html(data);
  });
}

function reloadComponentByJson(url, id, jsonParam, successCallbackFn, errorCallbackFn) {
  reqPostJSON(url, jsonParam, function (data) {
    $(id).replaceWith(data);
    refreshTooltip();
    if ($.isFunction(successCallbackFn)) {
      successCallbackFn();
    }
  }, function (xhr) {
    if ($.isFunction(errorCallbackFn)) {
      errorCallbackFn();
    }
  });
}

function reloadComponentTabActive(url, id, param, successCallbackFn) {
  reqGet(param === "" ? url : `${url}?${param}`, function (data) {
    $(id).replaceWith(data);
    $(id).addClass("active");
    refreshTooltip();
    if ($.isFunction(successCallbackFn)) {
      successCallbackFn();
    }
  });
}