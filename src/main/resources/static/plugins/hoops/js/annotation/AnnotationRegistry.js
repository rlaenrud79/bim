/// <reference path="../../../typescript/hoops_web_viewer.d.ts"/>
var UserViewer;
(function (UserViewer) {
    var AnnotationRegistry = /** @class */ (function () {
        function AnnotationRegistry(viewer) {
            this._annotationMap = {};
            this._viewer = viewer;
            this._makeDialog();
            this._table = document.getElementById("tblCoordinate");
        }
        AnnotationRegistry.prototype.addAnnotation = function (markupHandle, annotation) {
            $("body").find("#dlgCoordinates").dialog("open");
            var _this = this;
            this._annotationMap[markupHandle] = annotation;
            var tr = document.createElement("tr");
            tr.id = markupHandle;
            //var handletd = document.createElement("td");
            //handletd.innerText = markupHandle;
            //tr.appendChild(handletd);
            var nametd = document.createElement("td");
            nametd.id = markupHandle + "-name";
            nametd.innerText = annotation.getLabel();
            tr.appendChild(nametd);
            var actionstd = document.createElement("td");
            //var renameButton = document.createElement("button");
            //renameButton.innerText = "Rename";
            //renameButton.onclick = function () {
            //    _this._renameAnnotation(markupHandle);
            //};
            //actionstd.appendChild(renameButton);
            var deleteButton = document.createElement("span");
            deleteButton.innerHTML = '<button type="button" class="btn btn-primary btn-sm">delete</button>';
            deleteButton.onclick = function () {
                _this._deleteAnnotation(markupHandle);
            };
            actionstd.appendChild(deleteButton);
            tr.appendChild(actionstd);
            this._table.appendChild(tr);
        };
        AnnotationRegistry.prototype._makeDialog = function (){
            let dlg = '';
            dlg += '<div id="dlgCoordinates" title="Coordinate">';
            dlg += '    <table class="table table-responsive-sm" id="tblCoordinate">';
            dlg += '        <tr>';
            dlg += '            <th>Coordinate</th>';
            dlg += '            <th>delete</th>';
            dlg += '        </tr>';
            dlg += '    </table>';
            dlg += '</div>';
            if( $("body").find("#dlgCoordinates").length == 0){
                $("body").append(dlg);
            }
            $("body").find("#dlgCoordinates").dialog({
                autoOpen: false,
                position : {my: "center", at: "right", of: window},
                show     : {effect: "blind", duration: 100},
                hide     : {effect: "explode", duration: 100},
                width    : 300,
                maxHeight: 500
            });
        };
        AnnotationRegistry.prototype._renameAnnotation = function (markupHandle) {
            var annotation = this._annotationMap[markupHandle];
            var newMarkupName = prompt("Enter a new name for " + annotation.getLabel(), annotation.getLabel());
            if (newMarkupName !== null) {
                annotation.setLabel(newMarkupName);
                this._viewer.markupManager.refreshMarkup();
                document.getElementById(markupHandle + "-name").innerText = newMarkupName;
            }
        };
        AnnotationRegistry.prototype._deleteAnnotation = function (markupHandle) {
            this._viewer.markupManager.unregisterMarkup(markupHandle);
            delete this._annotationMap[markupHandle];
            let element = document.getElementById(markupHandle);
            let tblElement = element.parentElement;
            tblElement.removeChild(element);
        };
        return AnnotationRegistry;
    }());
    UserViewer.AnnotationRegistry = AnnotationRegistry;
})(UserViewer || (UserViewer = {}));
