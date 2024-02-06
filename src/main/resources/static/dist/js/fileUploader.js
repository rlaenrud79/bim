// Creates a new file and add it to our list

function ui_multi_add_file(objectId, id, file)
{

    var template = $('#files-template').text();

    template = template.replace('%%filename%%', file.name);
    template = $(template);
    template.prop('id', 'uploaderFile' + id);
    template.data('file-id', id);

    $(objectId).find('li.empty').fadeOut(); // remove the 'no files yet'
    $(objectId).prepend(template);

    $('#files-template ')
}

// Updates a file progress, depending on the parameters it may animate it or change the color.
function ui_multi_update_file_progress(id, percent, color, active)
{
    color = (typeof color === 'undefined' ? false : color);
    active = (typeof active === 'undefined' ? true : active);

    var bar = $('#uploaderFile' + id).find('div.progress-bar');

    bar.width(percent + '%').attr('aria-valuenow', percent);
    bar.toggleClass('progress-bar-striped progress-bar-animated', active);

    if (percent === 0){
        bar.html('');
    } else {
        bar.html(percent + '%');
    }

    if (color !== false){
        bar.removeClass('bg-success bg-info bg-warning bg-danger');
        bar.addClass('bg-' + color);
    }
}

function makeSingleUploaderScript(objectId, funcExtraData, maxFileSize, extFilter, onNewFileErrorMessage, onFileSizeErrorMessage, onFileExtErrorMessage, funcCompleteUploadFile) {
    $(objectId).dmUploader({
        url: '/commonApi/uploadFile',
        method: 'post',
        extraData: funcExtraData,
        queue: true,
        auto: false,
        multiple: false,
        maxFileSize: maxFileSize,
        extFilter: extFilter,
        onInit: function () {

        },
        onDragEnter: function () {
            this.addClass('active');
        },
        onDragLeave: function () {
            this.removeClass('active');
        },
        onComplete: function () {
            funcCompleteUploadFile($(this));
        },
        onNewFile: function (id, file) {
            if ($(objectId + "s").find(".media").length >= 1) {
                alert(onNewFileErrorMessage);
                return false;
            }
            ui_multi_add_file(objectId+"s", id, file);
        },
        onBeforeUpload: function (id) {
            $(".file-cancel").hide();
            ui_multi_update_file_progress(id, 0, '', true);
        },
        onUploadProgress: function (id, percent) {
            ui_multi_update_file_progress(id, percent);
        },
        onUploadSuccess: function (id, data) {
            ui_multi_update_file_progress(id, 100, 'success', false);
        },
        onUploadError: function (id, xhr, status, message) {
            ui_multi_update_file_progress(id, 0, 'danger', false);
        },
        onFileSizeError: function (file) {
            alert(onFileSizeErrorMessage);
        },
        onFileExtError: function () {
            alert(onFileExtErrorMessage);
        }
    });

    let h3 = $(objectId + "_single_h3").html();
    $(objectId + "_single_h3").html(h3 + "<br/>" + extFilter);
}

function makeMultiUploaderScript(objectId, funcExtraData, maxFileSize, extFilter, onFileSizeErrorMessage, onFileExtErrorMessage, funcCompleteUploadFile) {
    $(objectId).dmUploader({
        url: '/commonApi/uploadFile',
        method: 'post',
        extraData: funcExtraData,
        queue: true,
        auto: false,
        multiple: true,
        maxFileSize: maxFileSize,
        extFilter: extFilter,
        onInit: function () {

        },
        onDragEnter: function () {
            this.addClass('active');
        },
        onDragLeave: function () {
            this.removeClass('active');
        },
        onComplete: function () {
            funcCompleteUploadFile($(this));
        },
        onNewFile: function (id, file) {
            ui_multi_add_file(objectId+"s", id, file);
        },
        onBeforeUpload: function (id) {
            $(".file-cancel").hide();
            ui_multi_update_file_progress(id, 0, '', true);
        },
        onUploadProgress: function (id, percent) {
            ui_multi_update_file_progress(id, percent);
        },
        onUploadSuccess: function (id, data) {
            ui_multi_update_file_progress(id, 100, 'success', false);
        },
        onUploadError: function (id, xhr, status, message) {
            ui_multi_update_file_progress(id, 0, 'danger', false);
        },
        onFileSizeError: function (file) {
            showErrorAlert("ALERT", onFileSizeErrorMessage);
        },
        onFileExtError: function () {
            showErrorAlert("ALERT", onFileExtErrorMessage);
        }
    });

    let h3 = $(objectId + "_multi_h3").html();
    $(objectId + "_multi_h3").html(h3 + "<br/>" + extFilter);
}

$(document).on("click", '.devo_file_list button', function () {
    if ($(this).parents(".devo_file_list").length <= 1) {
        $(this).parents("ul").find(".empty").show();
    }
    $(this).parents(".devo_file_list").remove();
});

