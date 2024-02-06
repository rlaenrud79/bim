/** @hidden */
var Communicator;
(function (Communicator) {
    var Operator;
    (function (Operator) {
        var Common;
        (function (Common) {
            function worldPointToScreenPoint(viewer, worldPosition) {
                const wp4 = new Communicator.Point4(worldPosition.x, worldPosition.y, worldPosition.z, 1);
                const sp4 = new Communicator.Point4(0, 0, 0, 0);
                viewer.view.getFullCameraMatrix().transform4(wp4, sp4);
                const invW = 1 / sp4.w;
                const screenPosition = new Communicator.Point2(sp4.x * invW, sp4.y * invW);
                const dims = viewer.model.getClientDimensions();
                const w = dims[0];
                const h = dims[1];
                screenPosition.x = 0.5 * w * (screenPosition.x + 1);
                screenPosition.y = 0.5 * h * (screenPosition.y + 1);
                screenPosition.x = Math.max(0, Math.min(screenPosition.x, w));
                screenPosition.y = h - Math.max(0, Math.min(screenPosition.y, h));
                return screenPosition;
            }
            Common.worldPointToScreenPoint = worldPointToScreenPoint;
            class SelectionPoints {
                constructor(worldPosition, screenPosition, selectionItem) {
                    this.worldPosition = worldPosition;
                    this.screenPosition = screenPosition;
                    this.selectionItem = selectionItem;
                }
            }
            Common.SelectionPoints = SelectionPoints;
            class CursorMarkup extends Communicator.Markup.Measure.MeasureMarkup {
                constructor(viewer) {
                    super(viewer);
                    this._cursorSprite = new Communicator.Markup.Shape.Circle();
                    this._name = "CursorMarkup";
                    const measurementColor = viewer.measureManager.getMeasurementColor();
                    this._cursorSprite.setFillColor(measurementColor);
                    this._cursorSprite.setStrokeColor(measurementColor);
                    this._markupId = viewer.markupManager.registerMarkup(this);
                }
                draw() {
                    const renderer = this._viewer.markupManager.getRenderer();
                    renderer.drawCircle(this._cursorSprite);
                }
                enable(enable) {
                    this._cursorSprite.setRadius(enable ? 2.5 : 0);
                }
                isEnabled() {
                    return this._cursorSprite.getRadius() > 0;
                }
                setPosition(point) {
                    this._cursorSprite.setCenter(point);
                }
                destroy() {
                    this._viewer.markupManager.unregisterMarkup(this._markupId);
                }
            }
            class PointCursor {
                constructor(viewer) {
                    this._cursorMarkup = null;
                    this._updateCursorSpriteAction = new Communicator.Util.CurrentAction(true);
                    this._viewer = viewer;
                    this.snappingConfig = {
                        enabled: true,
                        preferVertices: true,
                    };
                }
                async getSelectionCursorPoints(mousePosition, useSnapping, previousPickPoint) {
                    const config = new Communicator.PickConfig(useSnapping ? Communicator.SelectionMask.All : Communicator.SelectionMask.Face);
                    const selectionItem = await this._viewer.view.pickFromPoint(mousePosition, config);
                    if (selectionItem.overlayIndex() !== 0) {
                        return null;
                    }
                    let worldPosition = selectionItem.getPosition();
                    let screenPosition = mousePosition;
                    if (this.snappingConfig.enabled) {
                        const lineEntity = selectionItem.getLineEntity();
                        const pointEntity = selectionItem.getPointEntity();
                        if (lineEntity || pointEntity) {
                            let worldSnapPosition = null;
                            if (lineEntity !== null) {
                                worldSnapPosition = this._getLineSnapPoint(lineEntity, useSnapping, previousPickPoint);
                            }
                            else if (pointEntity !== null) {
                                worldSnapPosition = pointEntity.getPosition();
                            }
                            if (worldSnapPosition !== null) {
                                worldPosition = worldSnapPosition;
                                screenPosition = worldPointToScreenPoint(this._viewer, worldPosition);
                            }
                        }
                    }
                    return new SelectionPoints(worldPosition, screenPosition, selectionItem);
                }
                updateCursorSprite(mousePosition, useSnapping, firstSelectedPoint) {
                    this._updateCursorSpriteAction.set(() => {
                        return this._updateCursorSpriteImpl(mousePosition, useSnapping, firstSelectedPoint);
                    });
                }
                async _updateCursorSpriteImpl(mousePosition, useSnapping, firstSelectedPoint) {
                    if (this._cursorMarkup !== null) {
                        if (useSnapping) {
                            const selection = await this.getSelectionCursorPoints(mousePosition, useSnapping, firstSelectedPoint);
                            if (selection !== null) {
                                this._cursorMarkup.setPosition(selection.screenPosition);
                                this.activateCursorSprite(true);
                            }
                            else {
                                this.activateCursorSprite(false);
                            }
                        }
                        else {
                            this._cursorMarkup.setPosition(mousePosition);
                        }
                    }
                    //this._draw(); // TODO: this belongs in the calling operator
                }
                draw() {
                    if (this._cursorMarkup !== null)
                        this._cursorMarkup.draw();
                }
                activateCursorSprite(enable) {
                    if (this._cursorMarkup !== null) {
                        this._cursorMarkup.enable(enable);
                    }
                }
                /**
                 * Finds the best point to use for the given lineEntity given the snapping behavior and settings.
                 */
                _getLineSnapPoint(lineEntity, useSnapping, firstSelectedPoint) {
                    // Always favor vertex snapping if it's viable
                    const bestVertexPosition = this.snappingConfig.preferVertices
                        ? lineEntity.getBestVertex()
                        : null;
                    if (bestVertexPosition !== null) {
                        return bestVertexPosition;
                    }
                    // The currently selected position from the line entity will be correct unless we want to find
                    // the snap-based center of the second-point line
                    const selectedPosition = lineEntity.getPosition();
                    if (!useSnapping || firstSelectedPoint == null) {
                        return selectedPosition;
                    }
                    // Getting here means we're snapping and selecting the second point. We need to support either
                    //   1) Snapping to the closest point on the line from our original point if we're *near* that closest point OR
                    //   2) Just snapping to the line if we're not near the closest point.
                    // Thus, we need to figure out the closest point to make that decision. But first we have to figure out
                    // which line segment from the set in the selection contains the current selection point. (It would be
                    // nice if the selection information indicated that segment, but that's a deeper change into
                    // the selection code and really needs it's own JIRA card)
                    const points = lineEntity.getPoints();
                    const onLineEpsilon = 1.0e-10;
                    const firstSegmentVertexIndex = (() => {
                        for (let i = 0; i < points.length - 1; i++) {
                            if (Communicator.Util.isPointOnLineSegment(points[i], points[i + 1], selectedPosition, onLineEpsilon)) {
                                return i;
                            }
                        }
                        // Punt if we didn't find a match and just use the first segment. Should never happen
                        // unless maybe we have a bad epsilon
                        return 0;
                    })();
                    // Find closest point on that selected line segment from our first selection point
                    const p0 = points[firstSegmentVertexIndex];
                    const p1 = points[firstSegmentVertexIndex + 1];
                    // Avoid throwing an error when line points are missing, i.e. capping geometry
                    if (p0 === undefined || p1 === undefined) {
                        return selectedPosition;
                    }
                    const closestLinePoint = Communicator.Util.closestPointFromPointToSegment(p0, p1, firstSelectedPoint);
                    // Determine if we are within the acceptable tolerance of the closest point on the second line
                    const closestScreenPoint = worldPointToScreenPoint(this._viewer, closestLinePoint);
                    const selectedScreenPoint = worldPointToScreenPoint(this._viewer, selectedPosition);
                    const pixelDistanceSq = Communicator.Point2.subtract(closestScreenPoint, selectedScreenPoint).squaredLength();
                    const pickTolerance = this._viewer.selectionManager.getPickTolerance();
                    const toleranceSq = pickTolerance * pickTolerance;
                    return pixelDistanceSq <= toleranceSq ? closestLinePoint : selectedPosition;
                }
                onOperatorActivate() {
                    this._cursorMarkup = new CursorMarkup(this._viewer);
                    // For 2D drawings, make the background sheet selectable while we're active so
                    // the user can point-to-point measure anything on the drawing
                    //
                    // TODO: Known issue here if hwv.switchToModel() is called while we're active, and the
                    // switch is from a 3D to 2D, the background won't be selectable. But there are other issues
                    // with the measurement operator already when switching models... fix them then. COM-2021
                    if (this._viewer.sheetManager.isDrawingSheetActive()) {
                        this._viewer.sheetManager.setBackgroundSelectionEnabled(true);
                    }
                }
                onOperatorDeactivate() {
                    if (this._cursorMarkup !== null) {
                        this._cursorMarkup.destroy();
                        this._cursorMarkup = null;
                    }
                    // Restore the no-selection behavior for 2D drawings when we're done. Note that if a model-switch to 3D
                    // happened while we're active, the background selection will be disabled by the sheet manager so it's
                    // fine that it won't get called here.
                    if (this._viewer.sheetManager.isDrawingSheetActive()) {
                        this._viewer.sheetManager.setBackgroundSelectionEnabled(false);
                    }
                }
            }
            Common.PointCursor = PointCursor;
        })(Common = Operator.Common || (Operator.Common = {}));
    })(Operator = Communicator.Operator || (Communicator.Operator = {}));
})(Communicator || (Communicator = {}));
