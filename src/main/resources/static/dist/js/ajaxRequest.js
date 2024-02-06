// wrap jquery-ajax
function reqGet(url, doneFn, failFn, alwaysFn, dataType) {
    ajaxRequest(getSettings('get', url, undefined, false, dataType, true), doneFn, failFn, alwaysFn);
}

function reqGetSync(url, doneFn, failFn, alwaysFn, dataType) {
    ajaxRequest(getSettings('get', url, undefined, false, dataType, false), doneFn, failFn, alwaysFn);
}

function reqDelete(url, doneFn, failFn, alwaysFn, dataType) {
    ajaxRequest(getSettings('delete', url, undefined, false, dataType, true), doneFn, failFn, alwaysFn);
}

function reqDeleteJSON(url, data, doneFn, failFn, alwaysFn, dataType) {
    ajaxRequest(getSettings('delete', url, data, true, dataType, true), doneFn, failFn, alwaysFn);
}

function reqPost(url, data, doneFn, failFn, alwaysFn) {
    ajaxRequest(getSettings('post', url, data, false, "", true), doneFn, failFn, alwaysFn);
}

function reqPostJSON(url, data, doneFn, failFn, alwaysFn) {
    ajaxRequest(getSettings('post', url, data, true, "", true), doneFn, failFn, alwaysFn);
}

function reqPut(url, data, doneFn, failFn, alwaysFn) {
    ajaxRequest(getSettings('put', url, data, false, "", true), doneFn, failFn, alwaysFn);
}

function reqPutJSON(url, data, doneFn, failFn, alwaysFn) {
    ajaxRequest(getSettings('put', url, data, true, "", true), doneFn, failFn, alwaysFn);
}

function getSettings(type, url, data, isJSON, dataType, async) {
    const defaultSettings = {
        type, url, data, async
    }
    if (isJSON) {
        defaultSettings.contentType = 'application/json; charset=utf-8';
    }
    if (dataType !== "") {
        defaultSettings.dataType = dataType;
    }
    return defaultSettings;
}

let _cnt900 = 0, _cnt405 = 0;
function ajaxRequest(settings, doneFn, failFn, alwaysFn) {
    $.ajax(settings)
    .done(function (data, textStatus, xhr) {
        if ($.isFunction(doneFn)) {
            doneFn(data, textStatus, xhr);
        }
    })
    .fail(function (xhr, textStatus, errorThrown) {
        if (xhr.responseJSON.status === 900 && _cnt900 == 0) {
            _cnt900++;
            alert(xhr.responseJSON.message);
            window.location.href = "/logout";
        }
        if (xhr.responseJSON.status === 405 && _cnt405 == 0) {
            _cnt405++;
            alert("Session time out!! please login again!!");
            window.location.href = "/logout";
        }
        if ($.isFunction(failFn)) {
            failFn(xhr, textStatus, errorThrown);
        }
    })
    .always(function (data, textStatus, xhr) {
        if ($.isFunction(alwaysFn)) {
            alwaysFn(data, textStatus, xhr);
        }
    });
}