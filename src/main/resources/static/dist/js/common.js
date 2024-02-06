const $this = $(document);

$.widget.bridge('uibutton', $.ui.button)

function showErrorAlert(title, message) {
  eModal.alert(message, title);
}

function showConfirm(title, message, okFunction, cancelFunction) {
  eModal.confirm(message, title)
  .then(
    function () {
      if (typeof (okFunction) != 'undefined') {
        okFunction();
      }
    },
    function () {
      if (typeof (cancelFunction) != 'undefined') {
        cancelFunction();
      }
    }
  );
}

function executeAllCheck($scope, obj, targetClass) {
  let allChecked = $(obj).is(":checked");
  checkAllOrNoting($scope, targetClass, allChecked);
}

function checkAllOrNoting($scope, targetClass, isAll) {
  $scope.find("input" + targetClass).each(function () {
    if (isAll) this.checked = true;
    else this.checked = false;
  });
}

function isChecked($scope) {
  let checkedCnt = 0
  $scope.find("input.itemCheck, input.itemCheckSearch").each(function (index, item) {
    if ($(item).is(":checked")) checkedCnt++;
  });

  if (checkedCnt == 0) return false;
  return true;
}

function getCheckedValues($scope, isReverse) {
  let values = "";
  let sortedSelector = $scope.find("input.itemCheck");

  if (isReverse) sortedSelector = $(sortedSelector.get().reverse());

  sortedSelector.each(function (index, item) {
    if ($(item).is(":checked")) {
      if (values == "") values = $(item).val();
      else values += "," + $(item).val();
    }
  });

  return values;
}

function controlDisplayOption($scope, arrayString) {

  if (arrayString == undefined || arrayString == "") return;

  if ((arrayString + "").indexOf(",") > 0) {
    arrayString.split(",").forEach(function (item) {
      $scope.find("#d-ctrl-" + item).addClass("d-flex");
    });
  } else $scope.find("#d-ctrl-" + arrayString).addClass("d-flex");
}

function setCheckedValues($scope, arrayString) {

  if (arrayString == undefined || arrayString == "") return;

  if ((arrayString + "").indexOf(",") > 0) {
    arrayString.split(",").forEach(function (item) {
      $scope.find("input.itemCheck[value=" + item + "]").attr("checked", "checked");
    });
  } else $scope.find("input.itemCheck[value=" + arrayString + "]").attr("checked", "checked");
}

const getData = (thisElement, closestElement, dataName) => {
  return $(thisElement).closest(closestElement).data(dataName);
}

jQuery.fn.serializeObject = function () {
  let obj = null;
  try {
    if (this[0].tagName && this[0].tagName.toUpperCase() == "FORM") {
      let arr = this.serializeArray();
      if (arr) {
        obj = {};
        jQuery.each(arr, function () {
          obj[this.name] = this.value;
        });
      }
    }
  } catch (e) {
    alert(e.message);
  } finally {
  }

  return obj;
};

function checkTelNo(regexPattern, val) {
  let telNoRegex = regexPattern;
  return telNoRegex.test(val);
}

function checkMobileNo(regexPattern, val) {
  let mobileRegex = regexPattern;
  return mobileRegex.test(val);
}

jQuery.each(["put", "delete"], function (i, method) {
  jQuery[method] = function (url, data, callback, type) {
    if (jQuery.isFunction(data)) {
      type = type || callback;
      callback = data;
      data = undefined;
    }

    return jQuery.ajax({
      url        : url,
      contentType: "application/json; charset=utf-8",
      type       : method,
      dataType   : type,
      data       : data,
      success    : callback,
      error      : function (xhr) {
        showErrorAlert("ALERT", xhr.responseJSON.message);
      }
    });
  };
});

function post(url, data, fnSuccess) {
  $.ajax({
    type       : 'post',
    url        : url,
    contentType: 'application/json; charset=utf-8',
    data       : data,
    dataType   : 'json',
    success    : fnSuccess,
    error      : function (xhr) {
      showErrorAlert("ALERT", xhr.responseJSON.message);
    }
  });
}

function validateEmail(email) {
  const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}

function isBlank(text) {
  if (text.search(/\s/) != -1) return true;
  return false;
}

function isNumeric(value)
{
  return !(isNaN(value) || value == '');
}

function numberWithCommas(value) {
  return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

const cloneObj = obj => JSON.parse(JSON.stringify(obj))

function sleep(ms) {
  const wakeUpTime = Date.now() + ms;
  while (Date.now() < wakeUpTime) {}
}

function isContainCharacter(text, isNumber, isEnglish, isSpecial) {
  let numberCount = text.search(/[0-9]/g);
  let englishCount = text.search(/[a-z]/ig);
  let specialCount = text.search(/[`~!@@#$%^&*|₩₩₩'₩";:₩/?]/gi);

  if (isNumber && isEnglish && isSpecial) {
    if (numberCount < 0 || englishCount < 0 || specialCount < 0) return false;
    else return true;
  }

  if (isNumber && isEnglish) {
    if (numberCount < 0 || englishCount < 0) return false;
    else return true;
  }

  if (isNumber && isSpecial) {
    if (numberCount < 0 || specialCount < 0) return false;
    else return true;
  }

  if (isEnglish && isSpecial) {
    if (englishCount < 0 || specialCount < 0) return false;
    else return true;
  }

  if (isNumber) {
    if (numberCount < 0) return false;
    else return true;
  }

  if (isEnglish) {
    if (englishCount < 0) return false;
    else return true;
  }

  if (isSpecial) {
    if (specialCount < 0) return false;
    else return true;
  }

  return false;
}

function toStringByFormatting(source, delimiter = '-'){
  const year = source.getFullYear();
  const month = leftPad(source.getMonth() + 1);
  const day = leftPad(source.getDate());

  return [year, month, day].join(delimiter);
}

function leftPad(value) { if (value >= 10) { return value; } return `0${value}`; }

function calculateDate(ymd, unit, value){

  let date = getDateFormat(ymd);
  if(unit=="day") date.setDate( date.getDate() + value);
  if(unit=="month") date.setDate( date.getMonth() + value);

  let month = (date.getMonth()+1);
  let day = date.getDate();

  if(month < 10) month = "0"+month;
  if(day < 10) day = "0"+day;

  return date.getFullYear()+"-"+month+"-"+day;
}

function getDateFormat(ymd) {
  let yyyy, mm, dd;

  if (ymd.length == 8) {
    yyyy = ymd.substr(0, 4);
    mm = ymd.substr(4, 2)
    dd = ymd.substr(6, 2)
  }

  if (ymd.length == 10) {
    yyyy = ymd.substr(0, 4);
    mm = ymd.substr(5, 2)
    dd = ymd.substr(8, 2)

    return new Date(yyyy, mm - 1, dd);
  }

}

const datepickerFormat = {
  dateFormat : 'yy-mm-dd',
  showOn     : "button",
  buttonImage: "/dist/img/calendar-alt-regular.svg"
};

let hideTooltip = function (element) {
  // element.tooltips({hide: true});
  $('.tooltip').remove();
}

let refreshTooltip = function () {
  if ($('[data-toggle="tooltip"]').length === 0) return false;
  $('[data-toggle="tooltip"]').tooltip();
}

function convertFirstCharterToUpperCase(text)
{
  if(text.length < 2) return text;
  return text.charAt(0).toUpperCase()+ text.slice(1)
}

let modalShowAndDraggable = function(obj){
  $(obj).modal('show');
  $(obj).draggable({'cancel':'.modal-body'});
}

refreshTooltip();

function applyNoAsideLayout () {
  // 프로젝트웍스 logo 삭제
  $(".nav-brand .nav-toggle").remove();
  $("body .wrapper aside").hide();
  // 중앙 content 영역 좌측 margin 0으로 설정
  $('.content-wrapper').css('margin-left',0);
  // 하단 footer 영역 좌측 margin 0으로 설정
  $('.main-footer').css('margin-left',0);

  // article 영역 클래스 변경(width=100%)
  $('article').removeClass('main-content-area').addClass('main-content-area-employer');
}

function setCookie(cookieName, cookieValue, cookieExpire, cookiePath, cookieDomain, cookieSecure){
  var cookieText=encodeURIComponent(cookieName)+'='+encodeURIComponent(cookieValue);
  cookieText+=(cookieExpire ? '; EXPIRES='+cookieExpire.toGMTString() : '');
  cookieText+=(cookiePath ? '; PATH='+cookiePath : '');
  cookieText+=(cookieDomain ? '; DOMAIN='+cookieDomain : '');
  cookieText+=(cookieSecure ? '; SECURE' : '');
  document.cookie=cookieText;
}

function getCookie(cookieName){
  var cookieValue=null;
  if(document.cookie){
    var array=document.cookie.split((encodeURIComponent(cookieName)+'='));
    if(array.length >= 2){
      var arraySub=array[1].split(';');
      cookieValue=decodeURIComponent(arraySub[0]);
    }
  }
  return cookieValue;
}

function deleteCookie(cookieName){
  var temp = getCookie(cookieName);
  if(temp){
    setCookie(cookieName,temp,(new Date(1)));
  }
}
