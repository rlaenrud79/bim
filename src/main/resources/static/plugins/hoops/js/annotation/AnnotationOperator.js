
var UserViewer;
(function (UserViewer) {
	class AnnotationOperator extends UserViewer.AnnotationOperatorBase {
		
		constructor(viewer, annotationRegistry) {
		    super(viewer, annotationRegistry);
            this._annotationCount = 1;
			this._cursor = new Communicator.Operator.Common.PointCursor(this._viewer);
			this._cameraInteractionActive = false;
			this._viewer.setCallbacks({
                beginInteraction: () => {
                   this._onBeginInteraction();
                },
                endInteraction: () => {
                    this._onEndInteraction();
                },
            });
        }
		_onBeginInteraction() {
            this._cameraInteractionActive = true;
            this._cursor.activateCursorSprite(false);
        }
        _onEndInteraction()  {
            this._cameraInteractionActive = false;
        }
			
		
		onMouseMove(event) {
            super.onMouseMove(event);
		  
            if (!this._cameraInteractionActive) {
                const mousePosition = event.getPosition();
                this._cursor.updateCursorSprite(mousePosition, true, null);
            }  
            this._cursor.draw();
			this._viewer.markupManager.refreshMarkup();
		};
		
		onActivate() {
            this._cursor.onOperatorActivate();
        };
            /** @hidden */
        onDeactivate() {
            this._cursor.onOperatorDeactivate();
        };
		
        onMouseDown(event) {
            var _this = this;
            var downPosition = event.getPosition();
            if (!this._selectAnnotation(downPosition)) {
                var config = new Communicator.PickConfig(Communicator.SelectionMask.Face);
                this._viewer.view.pickFromPoint(downPosition, config).then(function (selectionItem) {
                    var selectionPosition = selectionItem.getPosition();
                    if (selectionPosition) {
						const mousePosition = event.getPosition();
						const selection = _this._cursor.getSelectionCursorPoints(mousePosition, true, null).then(function (selection ) {
							const annotationPos = selection.worldPosition.copy();
                            let xPos = annotationPos.x;
                            let yPos = annotationPos.y;
                            let zPos = annotationPos.z;
                            if( g_offset ){
                                if( g_offset.x ){ xPos = annotationPos.x + g_offset.x;}
                                if( g_offset.y ){ yPos = annotationPos.y + g_offset.y;}
                                if( g_offset.z ){ zPos = annotationPos.z + g_offset.z;}
                            }

							let annotationtext = " x: " + xPos.toFixed(3)+ " \n y: " + yPos.toFixed(3)  + " \n z: " + zPos.toFixed(3);
							let annotationMarkup = new UserViewer.AnnotationMarkup(_this._viewer, annotationPos, annotationtext + _this._annotationCount++);
							let markupHandle = _this._viewer.markupManager.registerMarkup(annotationMarkup);
							_this._annotationRegistry.addAnnotation(markupHandle, annotationMarkup);
							_this._startDraggingAnnotation(annotationMarkup, downPosition);
						});
                    }
                });
            }
        }
		
    }
    UserViewer.AnnotationOperator = AnnotationOperator;
})(UserViewer || (UserViewer = {}));
