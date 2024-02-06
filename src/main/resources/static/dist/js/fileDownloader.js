
var globalDownLoadFileId ="";
var globalDownLoadFileUIType = "";

let executeFileDownloadModal = function (downLoadFileId, downLoadFileUIType){
  globalDownLoadFileId = downLoadFileId;
  globalDownLoadFileUIType = downLoadFileUIType;
  reqGet("/commonModal/fileDownload?id=" + globalDownLoadFileId + "&fileDownloadUIType=" + globalDownLoadFileUIType
      , function (data) {
        $('#modalDownloadFile').find('.popup-con').html(data);
          $('#modalDownloadFile').show();
      }
      , function (xhr) {
        alert($.parseJSON(xhr.responseText).error);
      }, "html");
  //modalShowAndDraggable('#modalDownloadFile');
}

$(document).on('show.bs.modal', '#modalDownloadFile', function(){
  reqGet("/commonModal/fileDownload?id=" + globalDownLoadFileId + "&fileDownloadUIType=" + globalDownLoadFileUIType
    , function (data) {
      $('#modalDownloadFile').find('.modal-body').html(data);
    }
    , function (xhr) {
      showErrorAlert("ALERT", $.parseJSON(xhr.responseText).error);
    }, "html");
});

$(document).on('hide.bs.modal', '#modalDownloadFile', function(){
  $('#modalDownloadFile').find('.modal-body').html(null);
  refreshTooltip();
});
