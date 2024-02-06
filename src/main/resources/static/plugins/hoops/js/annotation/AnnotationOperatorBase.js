/// <reference path="../../../typescript/hoops_web_viewer.d.ts"/>
var UserViewer;
(function (UserViewer) {
    var AnnotationOperatorBase = /** @class */ (function () {
        function AnnotationOperatorBase(viewer, annotationRegistry) {
            this._previousAnchorPlaneDragPoint = null;
            this._activeMarkup = null;
            this._viewer = viewer;
            this._annotationRegistry = annotationRegistry;
        }
        AnnotationOperatorBase.prototype._startDraggingAnnotation = function (annotation, downPosition) {
            this._activeMarkup = annotation;
            this._previousAnchorPlaneDragPoint = this._getDragPointOnAnchorPlane(downPosition);
        };
        AnnotationOperatorBase.prototype._selectAnnotation = function (selectPoint) {
            var markup = this._viewer.markupManager.pickMarkupItem(selectPoint);
            if (markup) {
                // If we picked an annotation start dragging it
                this._activeMarkup = markup;
                this._previousAnchorPlaneDragPoint = this._getDragPointOnAnchorPlane(selectPoint);
                return true;
            }
            else {
                return false;
            }
        };
        AnnotationOperatorBase.prototype.onMouseMove = function (event) {
            if (this._activeMarkup) {
                var currentAnchorPlaneDragPoint = this._getDragPointOnAnchorPlane(event.getPosition());
                var dragDelta = void 0;
                if (currentAnchorPlaneDragPoint !== null &&
                    this._previousAnchorPlaneDragPoint !== null) {
                    dragDelta = Communicator.Point3.subtract(currentAnchorPlaneDragPoint, this._previousAnchorPlaneDragPoint);
                }
                else {
                    dragDelta = Communicator.Point3.zero();
                }
                var newAnchorPos = this._activeMarkup.getTextBoxAnchor().add(dragDelta);
                this._activeMarkup.setTextBoxAnchor(newAnchorPos);
                this._previousAnchorPlaneDragPoint = currentAnchorPlaneDragPoint;
                this._viewer.markupManager.refreshMarkup();
                event.setHandled(true);
            }
        };
        AnnotationOperatorBase.prototype.onMouseUp = function (event) {
            event; // unreferenced
            this._activeMarkup = null;
            this._previousAnchorPlaneDragPoint = null;
        };
        AnnotationOperatorBase.prototype.onMouseDown = function (_event) { };
        AnnotationOperatorBase.prototype.onMousewheel = function (_event) { };
        AnnotationOperatorBase.prototype.onTouchStart = function (_event) { };
        AnnotationOperatorBase.prototype.onTouchMove = function (_event) { };
        AnnotationOperatorBase.prototype.onTouchEnd = function (_event) { };
        AnnotationOperatorBase.prototype.onKeyDown = function (_event) { };
        AnnotationOperatorBase.prototype.onKeyUp = function (_event) { };
        AnnotationOperatorBase.prototype.onDeactivate = function () { };
        AnnotationOperatorBase.prototype.onActivate = function () { };
        AnnotationOperatorBase.prototype.onViewOrientationChange = function () { };
        AnnotationOperatorBase.prototype.stopInteraction = function () { };
        AnnotationOperatorBase.prototype._getDragPointOnAnchorPlane = function (screenPoint) {
            if (this._activeMarkup === null) {
                return null;
            }
            var anchor = this._activeMarkup.getLeaderLineAnchor();
            var camera = this._viewer.view.getCamera();
            var normal = Communicator.Point3.subtract(camera.getPosition(), anchor).normalize();
            var anchorPlane = Communicator.Plane.createFromPointAndNormal(anchor, normal);
            var raycast = this._viewer.view.raycastFromPoint(screenPoint);
            if (raycast === null) {
                return null;
            }
            var intersectionPoint = Communicator.Point3.zero();
            if (anchorPlane.intersectsRay(raycast, intersectionPoint)) {
                return intersectionPoint;
            }
            else {
                return null;
            }
        };
        return AnnotationOperatorBase;
    }());
    UserViewer.AnnotationOperatorBase = AnnotationOperatorBase;
})(UserViewer || (UserViewer = {}));
