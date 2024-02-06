/// <reference path="hoops_web_viewer.d.ts" />
/// <reference types="jquery" />
declare namespace Example {
    class AnnotationMarkup extends Communicator.Markup.MarkupItem {
        protected _viewer: Communicator.WebViewer;
        private _leaderAnchor;
        private _textBoxAnchor;
        private _leaderLine;
        private _textBox;
        private _nodeId;
        private _showAsColor;
        constructor(viewer: Communicator.WebViewer, nodeId: number, anchorPoint: Communicator.Point3, label: string);
        draw(): void;
        hit(point: Communicator.Point2): boolean;
        setShowAsColor(showAsColor: boolean): void;
        getShowAsColor(): boolean;
        getNodeId(): number;
        getLeaderLineAnchor(): Communicator.Point3;
        getTextBoxAnchor(): Communicator.Point3;
        setTextBoxAnchor(newAnchorPoint: Communicator.Point3): void;
        setLabel(label: string): void;
        getLabel(): string;
    }
    class AnnotationRegistry {
        private _viewer;
        private _pulseManager;
        private _table;
        private _annotationMap;
        constructor(viewer: Communicator.WebViewer, pulseManager: PulseManager);
        getAnnotation(markupHandle: string): AnnotationMarkup | undefined;
        export(): string;
        addAnnotation(markupHandle: string, annotation: AnnotationMarkup): void;
        private _onPulseChange;
        private _renameAnnotation;
        private _deleteAnnotation;
    }
    class AnnotationOperator implements Communicator.Operator.Operator {
        protected _viewer: Communicator.WebViewer;
        protected _annotationRegistry: AnnotationRegistry;
        private _previousAnchorPlaneDragPoint;
        private _activeMarkup;
        private _previousNodeId;
        constructor(viewer: Communicator.WebViewer, annotationRegistry: AnnotationRegistry);
        onMouseDown(event: Communicator.Event.MouseInputEvent): Promise<void>;
        onMouseMove(event: Communicator.Event.MouseInputEvent): Promise<void>;
        onMouseUp(_event: Communicator.Event.MouseInputEvent): void;
        protected _startDraggingAnnotation(annotation: AnnotationMarkup, downPosition: Communicator.Point2): void;
        protected _selectAnnotation(selectPoint: Communicator.Point2): boolean;
        onDeactivate(): void;
        private _getDragPointOnAnchorPlane;
    }
}
declare namespace Communicator.Ui.Context {
    class ContextMenuItem {
        action: () => Promise<void>;
        element: HTMLDivElement;
        constructor(action: () => Promise<void>, element: HTMLDivElement);
        setEnabled(enabled: boolean): void;
        setText(text: string): void;
        show(): void;
        hide(): void;
    }
    interface ContextItemMap {
        reset: ContextMenuItem;
        visibility: ContextMenuItem;
        isolate: ContextMenuItem;
        zoom: ContextMenuItem;
        transparent: ContextMenuItem;
        setColor: ContextMenuItem;
        modifyColor: ContextMenuItem;
    }
    class ContextMenu {
        protected readonly _viewer: WebViewer;
        protected readonly _isolateZoomHelper: IsolateZoomHelper;
        private readonly _containerId;
        private readonly _menuElement;
        private readonly _contextLayer;
        private readonly _colorPicker;
        private _contextItemMap;
        private _activeItemId;
        private _activeLayerName;
        private _activeType;
        private _separatorCount;
        protected _position: Point3 | null;
        protected _modifiers: KeyModifiers;
        constructor(menuClass: string, containerId: HtmlId, viewer: WebViewer, isolateZoomHelper: IsolateZoomHelper, colorPicker: ColorPicker);
        _getContextItemMap(): ContextItemMap;
        private _onNewModel;
        private _isMenuItemEnabled;
        private _isMenuItemVisible;
        private _isColorSet;
        private _updateMenuItems;
        setActiveLayerName(layerName: LayerName): Promise<void>;
        setActiveType(genericType: GenericType): Promise<void>;
        setActiveItemId(activeItemId: NodeId | null): Promise<void>;
        showElements(position: Point2): void;
        protected _onContextLayerClick(event: JQuery.MouseDownEvent): void;
        hide(): void;
        action(action: keyof ContextItemMap): Promise<void>;
        private _doMenuClick;
        private _createMenuElement;
        private _createContextLayer;
        private _initElements;
        private _isMenuItemExecutable;
        private _createDefaultMenuItems;
        getContextItemIds(includeSelected: boolean, includeClicked: boolean, includeRoot?: boolean): NodeId[];
        appendItem(itemId: keyof ContextItemMap, label: string, action: () => Promise<void>): ContextMenuItem;
        appendSeparator(): void;
        private _isItemVisible;
        private _isLayerVisibile;
        private _isTypeVisible;
    }
}
declare namespace Communicator.Ui.CuttingPlane {
    const enum Status {
        Hidden = 0,
        Visible = 1,
        Inverted = 2
    }
    class Info {
        plane: Plane | null;
        referenceGeometry: Point3[] | null;
        status: Status;
        updateReferenceGeometry: boolean;
    }
    namespace ControllerUtils {
        interface IController {
            init: () => Promise<void>;
            update: () => Promise<void>;
            refresh: () => Promise<void>;
            clear: () => Promise<void>;
        }
        type EventType = "init" | "update" | "refresh" | "clear";
        type StateName = "not initialized" | "outdated" | "updating" | "up to date" | "update triggered";
        interface ControllerState {
            name: StateName;
            controller: IController;
        }
        type ControllerStateReducer = (state: ControllerState, event: EventType) => ControllerState;
    }
}
declare namespace Communicator.Ui.CuttingPlane.ControllerUtils {
    /**
     * @class PlaneInfoManager encapsulates the the handling of the planes' information
     */
    class PlaneInfoManager {
        /**
         * @member _planeInfoMap a map of plane information for each CuttingSection
         */
        private readonly _planeInfoMap;
        /**
         * Get the plane Information for CuttingSectionIndex.X
         * @returns the plane Information for CuttingSectionIndex.X
         */
        get X(): Info;
        /**
         * Get the plane Information for CuttingSectionIndex.Y
         * @returns the plane Information for CuttingSectionIndex.Y
         */
        get Y(): Info;
        /**
         * Get the plane Information for CuttingSectionIndex.Z
         * @returns the plane Information for CuttingSectionIndex.Z
         */
        get Z(): Info;
        /**
         * Get the plane Information for CuttingSectionIndex.Face
         * @returns the plane Information for CuttingSectionIndex.Face
         */
        get Face(): Info;
        /**
         * Check whether a cutting plane is hidden or not for a given section
         * @param  {CuttingSectionIndex} sectionIndex the section index of the plane to check
         * @returns true if the cutting plane is hidden, false otherwise.
         */
        isHidden(sectionIndex: CuttingSectionIndex): boolean;
        /**
         * Get the plane information for a given cutting section
         *
         * If the info does not exist in the map it creates it
         *
         * @param  {CuttingSectionIndex} sectionIndex the section index of the info to get
         * @returns the plane information for the given section
         */
        get(sectionIndex: CuttingSectionIndex): Info;
        /**
         * Resets a plane's information
         *
         * Sets plane and referenceGeometry to null
         *
         * @param  {CuttingSectionIndex} sectionIndex
         */
        reset(sectionIndex: CuttingSectionIndex): void;
        /**
         * Deletes the plane's information for a given section in the map
         *
         * @note it will be recreated if get is called with the same section index
         *
         * @param  {CuttingSectionIndex} sectionIndex the section index of the plane's information to delete
         */
        delete(sectionIndex: CuttingSectionIndex): void;
        /**
         * Deletes all planes' information
         *
         * @note they will be recreated if get is called with each of their section index
         */
        clear(): void;
        /**
         * Update all planes' info
         *
         * @todo check if this is still necessary after refactoring or if something more elegant is feasible
         */
        update(): void;
        /**
         * Get the status of a plane given the plain and its section index
         * @param  {CuttingSectionIndex} sectionIndex the section index of the plane
         * @param  {Plane} plane the plane to get its status
         * @returns {Status} the status of the plane
         */
        static getCuttingStatus(sectionIndex: CuttingSectionIndex, plane: Plane): Status;
        /**
         * Get the section index for a given plane based on its normal
         * @param  {Plane} plane the plane to get the section for
         * @returns the section index of the given plane
         */
        static getPlaneSectionIndex(plane: Plane): CuttingSectionIndex;
    }
}
declare namespace Communicator.Ui.CuttingPlane.ControllerUtils {
    /**
     * @class CuttingSectionManager encapsulate the access to the cutting manager sections.
     */
    class CuttingSectionManager {
        /**
         * @member _useIndividualCuttingSections a boolean representing whether the cutting section is done on a single plane or not
         */
        private _useIndividualCuttingSections;
        /**
         * @member _cuttingManager The CuttingManager of the WebViewer where the cutting planes are drawn
         */
        private _cuttingManager;
        /**
         * @property useIndividualCuttingSections getter
         * @returns  a boolean representing whether the cutting section is done on a single plane or not
         */
        get useIndividualCuttingSections(): boolean;
        /**
         * @property useIndividualCuttingSections setter
         *
         * @todo check if this can be remove and internalize the handling of this value or make it public
         *
         * @param  {boolean} value
         */
        set useIndividualCuttingSections(value: boolean);
        /**
         * @property X get the CuttingSection for CuttingSectionIndex.X
         * @returns The CuttingSection for CuttingSectionIndex.X
         */
        get X(): CuttingSection;
        /**
         * @property Y get the CuttingSection for CuttingSectionIndex.Y
         * @returns The CuttingSection for CuttingSectionIndex.Y
         */
        get Y(): CuttingSection;
        /**
         * @property Z get the CuttingSection for CuttingSectionIndex.Z
         * @returns The CuttingSection for CuttingSectionIndex.Z
         */
        get Z(): CuttingSection;
        /**
         * @property Face get the CuttingSection for CuttingSectionIndex.Face
         * @returns The CuttingSection for CuttingSectionIndex.Face
         */
        get Face(): CuttingSection;
        /**
         * @property activeStates get an array of boolean representing whether a plane in
         * [CuttingSectionIndex.X, CuttingSectionIndex.Y, CuttingSectionIndex.Z, CuttingSectionIndex.Face]
         * is active
         * @returns boolean
         */
        get activeStates(): [boolean, boolean, boolean, boolean];
        /**
         * @property planes get planes in either CuttingSectionIndex.X CuttingSection if its count is greater than 1
         * or every first plane of every section in
         * [CuttingSectionIndex.X, CuttingSectionIndex.Y, CuttingSectionIndex.Z, CuttingSectionIndex.Face]
         *
         * @todo check whether the X part is not something that must be modified due to refactoring (support use
         * individual on other section index)
         *
         * @returns an array of four cells containing either a plane or null.
         */
        get planes(): (Plane | null)[];
        /**
         * @property referenceGeometry get the reference geometry in either CuttingSectionIndex.X CuttingSection if its count
         * is greater than 1 or every first reference geometry of every section in
         * [CuttingSectionIndex.X, CuttingSectionIndex.Y, CuttingSectionIndex.Z, CuttingSectionIndex.Face]
         *
         * @todo check whether the X part is not something that must be modified due to refactoring (support use
         * individual on other section index)
         *
         * @returns an array of four cells containing either a plane or null.
         */
        get referenceGeometry(): (Point3[] | null)[];
        /**
         * Get a Cutting Section given an index
         * @param sectionIndex The index of the cutting section
         * @returns The appropriate cutting section for this index from the cutting manager
         */
        get(sectionIndex: CuttingSectionIndex): CuttingSection;
        /**
         * Get the total number of planes for all sections.
         * @returns the total number of planes for all sections.
         */
        getCount(): number;
        /**
         * Initialize the component and store a reference to the CuttingManager of the WebViewer
         * @param viewer The viewer where the cutting planes to manage are
         */
        init(viewer: WebViewer): void;
        /**
         * Check whether a section is active or not.
         *
         * @throws Error if the CuttingSectionManager has not been initialized yet
         * @param  {CuttingSectionIndex} sectionIndex
         * @returns a boolean that represent whether a section is active or not
         */
        isActive(sectionIndex: CuttingSectionIndex): boolean;
        /**
         * Activate a plane if the section count is greater than 0
         * @param  {CuttingSectionIndex} sectionIndex the index of the plane to activate
         *
         * @todo improve this API, a function cannot silently fail like that at least return a boolean
         *
         * @returns Promise a promise returning when the plane has been activated or not
         */
        activate(sectionIndex: CuttingSectionIndex): Promise<void>;
        /**
         * Activate all planes if the section count is greater than 0 depending on activeStates
         * if activeStates is undefined every planes are activated (if possible)
         * if activeStates is defined
         *  - activeStates[0] triggers activation of CuttingSectionIndex.X
         *  - activeStates[1] triggers activation of CuttingSectionIndex.Y
         *  - activeStates[2] triggers activation of CuttingSectionIndex.Z
         *  - activeStates[3] triggers activation of CuttingSectionIndex.Face
         *
         * @param  {activeStates} activeStates
         *
         * @todo improve this API, a function cannot silently fail like that at least return a boolean array
         * @todo improve this API, use named fields for activeStates
         *
         * @returns Promise a promise returning when the plane has been activated or not
         */
        activateAll(activeStates?: [boolean, boolean, boolean, boolean]): Promise<void>;
        /**
         * Deactivate a plane
         * @param  {CuttingSectionIndex} sectionIndex the index of the plane to deactivate
         *
         * @todo improve this API, a function cannot silently fail like that at least return a boolean
         *
         * @returns Promise a promise returning when the plane has been activated or not
         */
        deactivate(sectionIndex: CuttingSectionIndex): Promise<void>;
        /**
         * Reset useIndividualCuttingSections to true
         *
         * @todo check whether it is still useful after the refactoring
         *
         */
        reset(): void;
        /**
         * Get the section index based on whether useIndividualCuttingSections is set or not.
         * Return CuttingSectionIndex.X (0) if useIndividualCuttingSections is set, sectionIndex otherwise.
         *
         * @todo maybe we can improve this API by aliasing the CuttingSectionIndex.X (0) with something like CuttingSectionIndividual = 0
         *
         * @param sectionIndex the index of the desired section
         * @returns CuttingSectionIndex.X (0) if useIndividualCuttingSections is set, sectionIndex otherwise
         */
        getCuttingSectionIndex(sectionIndex: CuttingSectionIndex): CuttingSectionIndex;
        /**
         * Clear a CuttingSection given its index
         * @param  {CuttingSectionIndex} sectionIndex the index of the section to clear
         * @returns a promise resolved with void on completion
         */
        clear(sectionIndex: CuttingSectionIndex): Promise<void>;
        /**
         * Clear a CuttingSection given its index
         * @param  {CuttingSectionIndex} sectionIndex the index of the section to clear
         * @returns a promise resolved with void on completion
         */
        clearAll(): Promise<void>;
        /**
         * get the index of a plane given section index
         * @param  {CuttingSectionIndex} sectionIndex the index of the section of the plane
         * @param  {Point3} normal? an optional normal used if sectionIndex === CuttingSectionIndex.Face
         * @returns the index of the plane according to the sec or -1 if not found.
         */
        getPlaneIndex(sectionIndex: CuttingSectionIndex, normal?: Point3): number;
        /**
         * Get the plane and the reference geometry for a given section index
         * @param  {CuttingSectionIndex} sectionIndex the section index of the data to get
         * @param  {Point3} normal? an optional normal used if sectionIndex === CuttingSectionIndex.Face
         * @returns {plane: Plane | null; referenceGeometry: Point3[] | null} an Object containing the
         * plane or null as plane and the reference geometry or null as referenceGeometry
         */
        getPlaneAndGeometry(sectionIndex: CuttingSectionIndex, normal?: Point3): {
            plane: Plane | null;
            referenceGeometry: Point3[] | null;
        };
        /**
         * Get the reference geometry for a given section index
         *
         * @param  {CuttingSectionIndex} sectionIndex the section index of the reference geometry to get
         * @param  {Box} boundingBox the bounding box of the model
         * @returns {Point3[]} the reference geometry of the section
         */
        getReferenceGeometry(sectionIndex: CuttingSectionIndex, boundingBox: Box): Point3[];
        /**
         * Add a plane to a section
         * @param  {CuttingSectionIndex} sectionIndex the section where the plane should be added
         * @param  {Plane} plane the plane to add.
         * @param  {Point[]|null} referenceGeometry the reference geometry of the plane or null
         * @returns Promise a promise completed with undefined when its done
         */
        addPlane(sectionIndex: CuttingSectionIndex, plane: Plane, referenceGeometry: Point3[] | null): Promise<void>;
    }
}
declare namespace Communicator.Ui.CuttingPlane.ControllerUtils {
    /**
     * @class BoundingManager this is a buffer class that holds the model bounding
     * box and update it if needed
     */
    class BoundingManager {
        /**
         * @member _modelBounding Copy of the bounding box model used as a buffer value yo allow synchronous access
         */
        private _modelBounding;
        /**
         * @property @readonly box the latest bounding box
         */
        get box(): Box;
        /**
         * Initialize the bounding box with the current model bounding box value
         * @param viewer the WebViewer of the cutting planes
         */
        init(viewer: WebViewer): Promise<void>;
        /**
         * update the bounding box if the model bounding box is different than the current one
         * @param viewer the WebViewer of the cutting planes
         * @returns true if the bounding boxed changed, false otherwise
         */
        update(viewer: WebViewer): Promise<boolean>;
    }
}
declare namespace Communicator.Ui.CuttingPlane.ControllerUtils {
    /**
     * @class FaceSelectionManager encapsulates Face Selection for cutting planes
     */
    class FaceSelectionManager {
        /**
         * @member _faceSelection the current face selection item if there is one or null;
         */
        private _faceSelection;
        /**
         * Set the face selection item or reset it to null
         * @param  {Selection.FaceSelectionItem|null=null} item
         */
        reset(item?: Selection.FaceSelectionItem | null): void;
        /**
         * @property  isSet is true if the current face selection item is not null
         * @returns true if the current face selection item is not null, false otherwise
         */
        get isSet(): boolean;
        /**
         * @property normal is the normal of the current face selection entity if any
         * @returns the normal of the current face selection entity if any, undefined otherwise
         */
        get normal(): Point3 | undefined;
        /**
         * @property position is the position of the current face selection entity if any
         * @returns the position of the current face selection entity if any, undefined otherwise
         */
        get position(): Point3 | undefined;
        /**
         * Get the reference geometry for the face section.
         * @param  {WebViewer} viewer the WebViewer were the cutting planes are displayed
         * @param  {Box} boundingBox the model bounding box
         * @returns the reference geometry for the face section
         */
        getReferenceGeometry(viewer: WebViewer, boundingBox: Box): Point3[];
    }
}
declare namespace Communicator.Ui.CuttingPlane.ControllerUtils {
    /**
     * @class StateMachine Helper Class to create a Controller StateMachine given a Controller
     */
    class StateMachine extends Util.StateMachine<ControllerState, EventType> {
        constructor(controller: IController);
    }
    /**
     * The default reducer of the Controller.
     *
     * The default behavior is to begin as 'not initialized'
     *  - If it receives an init action
     *    - it set its state to 'updating'
     *    - it initializes the controller
     *    - If an update action is received during the initialization it sets its states to 'update triggered'.
     *    - when the initialization is done
     *      - if the current state is 'updating' it is set it to 'outdated'
     *      - if the current state is 'update triggered' it calls the default reducer with the action 'update'
     *
     *  - If it receives an update action
     *    - if the current state is 'not initialized' it throws an error
     *    - if the current state is 'update triggered' it does nothing
     *    - if the current state is 'updating' it set the state to 'update triggered' and stops
     *    - it set its state to 'updating'
     *    - it updates the controller
     *    - If an update action is received during the initialization it sets its states to 'update triggered'.
     *    - when the initialization is done
     *      - if the current state is 'updating' it is set it to 'up to date'
     *      - if the current state is 'update triggered' it calls the default reducer with the action 'update'
     *
     *  - If it receives a clear action
     *    - if the current state is 'not initialized' it throws an error
     *    - it set its state to 'updating'
     *    - it clears the controller
     *    - If an update action is received during the initialization it sets its states to 'update triggered'.
     *    - when the initialization is done
     *      - if the current state is 'updating' it is set it to 'up to date'
     *      - if the current state is 'update triggered' it calls the default reducer with the action 'update'
     *
     * @note COM-3280 showed that several calls to setVisibility will trigger calls of
     * Communicator.Ui.CuttingPlane.Controller._updateBoundingBox and it interferes with the model visibility
     * and creates bugs.
     * To avoid collisions between the successive setVisibility and the updating of the Controller the actions
     * are delayed using @function delayCall.
     *
     * @see delayCall
     *
     * {@link https://techsoft3d.atlassian.net/browse/COM-3280}
     *
     * @param  {ControllerState} state the current state of the controller's state machine
     * @param  {Util.StateMachine.Action<EventType>} action the action that triggered the transition
     * @param  {EventType} action.name the name of the action
     * @param  {undefined} action.payload the payload of the action
     *
     * @note The action's payload is always undefined as the Controller does not use it
     *
     * @returns State
     */
    function defaultReducer(state: ControllerState, { name, payload }: Util.StateMachine.Action<EventType>): ControllerState;
}
declare namespace Communicator.Ui.CuttingPlane {
    class Controller implements ControllerUtils.IController {
        protected readonly _viewer: WebViewer;
        protected readonly _stateMachine: ControllerUtils.StateMachine;
        protected readonly _planeInfoMgr: ControllerUtils.PlaneInfoManager;
        protected readonly _cuttingSectionsMgr: ControllerUtils.CuttingSectionManager;
        protected readonly _modelBoundingMgr: ControllerUtils.BoundingManager;
        protected readonly _faceSelectionMgr: ControllerUtils.FaceSelectionManager;
        protected _showReferenceGeometry: boolean;
        protected readonly _pendingFuncs: {
            visibility?: () => Promise<void>;
            inverted?: () => Promise<void>;
        };
        constructor(viewer: WebViewer);
        init(): Promise<void>;
        update(): Promise<void>;
        refresh(): Promise<void>;
        clear(): Promise<void>;
        get individualCuttingSectionEnabled(): boolean;
        getPlaneStatus(sectionIndex: CuttingSectionIndex): Status;
        onSectionsChanged(): Promise<void>;
        getReferenceGeometryEnabled(): boolean;
        private _updateBoundingBox;
        private _resetCuttingData;
        resetCuttingPlanes(): Promise<void>;
        private _initSection;
        private _triggerPendingFuncs;
        toggle(sectionIndex: CuttingSectionIndex): Promise<void>;
        getCount(): number;
        setCuttingPlaneVisibility(visibility: boolean, sectionIndex: CuttingSectionIndex): Promise<void>;
        setCuttingPlaneInverted(sectionIndex: CuttingSectionIndex): Promise<void>;
        toggleReferenceGeometry(): Promise<void>;
        refreshPlaneGeometry(): Promise<void>;
        toggleCuttingMode(): Promise<void>;
        private _setSection;
        private _restorePlane;
        private _restorePlanes;
        private _storePlanes;
        private _generateReferenceGeometry;
    }
}
declare namespace Communicator.Ui {
    /**
     * @deprecated CuttingPlaneStatus is deprecated in favor of CuttingPlane.Status
     */
    type CuttingPlaneStatus = CuttingPlane.Status;
    /**
     * @deprecated CuttingPlaneInfo is deprecated in favor of CuttingPlane.Info
     */
    class CuttingPlaneInfo extends CuttingPlane.Info {
    }
    /**
     * @deprecated CuttingPlaneController is deprecated in favor of CuttingPlane.Controller
     */
    class CuttingPlaneController extends CuttingPlane.Controller {
        /**
         * @deprecated getPlaneInfo is deprecated in favor of CuttingPlane.Controller.individualCuttingSectionEnabled
         * @returns CuttingPlane.Controller.individualCuttingSectionEnabled
         */
        getIndividualCuttingSectionEnabled(): boolean;
        /**
         * @deprecated getPlaneInfo is deprecated in favor of CuttingPlane.Controller.getPlaneStatus
         * @param sectionIndex the section index of the plane you want
         * @returns the plane info of the plane in section sectionIndex
         */
        getPlaneInfo(sectionIndex: CuttingSectionIndex): CuttingPlaneInfo | undefined;
    }
}
declare namespace Communicator {
    /**
     * @hidden
     * Removes any ids from the array if they are not contained in the current sheet.
     * @param nodeIds [[NodeId]] array.
     */
    function _filterActiveSheetNodeIds(viewer: WebViewer, nodeIds: NodeId[]): void;
    class IsolateZoomHelper {
        private readonly _viewer;
        private readonly _noteTextManager;
        private _camera;
        private _deselectOnIsolate;
        private _deselectOnZoom;
        private _isolateStatus;
        constructor(viewer: WebViewer);
        private _setCamera;
        setDeselectOnIsolate(deselect: boolean): void;
        getIsolateStatus(): boolean;
        isolateNodes(nodeIds: NodeId[], initiallyHiddenStayHidden?: boolean | null): Promise<void>;
        fitNodes(nodeIds: NodeId[]): Promise<void>;
        showAll(): Promise<void>;
        private _updatePinVisibility;
    }
}
declare namespace Example {
    class PulseManager {
        private _viewer;
        private _previousTime?;
        private _pulseInfoMap;
        private _defaultColor1;
        private _defaultColor2;
        private _defaultPulseTime;
        constructor(viewer: Communicator.WebViewer);
        start(): void;
        deletePulse(id: number): void;
        add(id: number, color1: Communicator.Color, color2: Communicator.Color, duration: number): void;
        update(): void;
        getDefaultColor1(): Communicator.Color;
        getDefaultColor2(): Communicator.Color;
        getDefaultPulseTime(): number;
    }
}
declare namespace Communicator.Ui {
    class RightClickContextMenu extends Context.ContextMenu {
        constructor(containerId: HtmlId, viewer: WebViewer, isolateZoomHelper: IsolateZoomHelper, colorPicker: ColorPicker);
        private _initEvents;
        private _getSelectionNoTransItem;
        doContext(position: Point2): Promise<void>;
        protected _onContextLayerClick(event: JQuery.MouseDownEvent): Promise<void>;
    }
}
declare namespace Communicator.Ui {
    class StreamingIndicator {
        private readonly _viewer;
        private readonly _container;
        private readonly _bottomLeftOffset;
        private readonly _opacity;
        private _spinnerImageUrl;
        private readonly _spinnerSize;
        constructor(elementId: HtmlId, viewer: WebViewer);
        show(): void;
        hide(): void;
        setBottomLeftOffset(point: Point2): void;
        getBottomLeftOffset(): Point2;
        setSpinnerImage(spinnerUrl: string, size: Point2): void;
        private _initContainer;
        private _onStreamingActivated;
        private _onStreamingDeactivated;
    }
}
declare namespace Communicator.Ui {
    class UiDialog {
        private readonly _containerId;
        private readonly _textDiv;
        private readonly _windowElement;
        private readonly _headerDiv;
        constructor(containerId: HtmlId);
        private static _createWindowElement;
        private static _createHeaderDiv;
        private static _createTextDiv;
        private _initElements;
        protected _onOkButtonClick(): void;
        show(): void;
        hide(): void;
        setText(text: string): void;
        setTitle(title: string): void;
    }
}
declare namespace Communicator.Ui {
    class TimeoutWarningDialog extends UiDialog {
        private readonly _viewer;
        constructor(containerId: HtmlId, viewer: WebViewer);
        private _onTimeoutWarning;
        protected _onOkButtonClick(): void;
        private _onTimeout;
    }
}
declare namespace Communicator.Ui {
    class Toolbar {
        private readonly _viewer;
        private readonly _noteTextManager;
        private readonly _cuttingPlaneController;
        private readonly _viewerSettings;
        private readonly _toolbarSelector;
        private readonly _screenElementSelector;
        private readonly _cuttingPlaneXSelector;
        private readonly _cuttingPlaneYSelector;
        private readonly _cuttingPlaneZSelector;
        private readonly _cuttingPlaneFaceSelector;
        private readonly _cuttingPlaneVisibilitySelector;
        private readonly _cuttingPlaneGroupToggle;
        private readonly _cuttingPlaneResetSelector;
        private readonly _selectedClass;
        private readonly _disabledClass;
        private readonly _invertedClass;
        private readonly _submenuHeightOffset;
        private readonly _viewOrientationDuration;
        private _activeSubmenu;
        private readonly _actionsNullary;
        private readonly _actionsBoolean;
        private _isInitialized;
        private readonly _screenConfiguration;
        constructor(viewer: WebViewer, cuttingPlaneController: CuttingPlane.Controller, screenConfiguration?: ScreenConfiguration);
        init(): void;
        /** @hidden */
        _getViewerSettings(): Desktop.ViewerSettings;
        disableSubmenuItem(item: Object | string): void;
        enableSubmenuItem(item: Object | string): void;
        setCorrespondingButtonForSubmenuItem(value: string): void;
        private _mouseEnterItem;
        private _mouseLeaveItem;
        show(): void;
        hide(): void;
        private _initSliders;
        private _mouseEnter;
        private _mouseLeave;
        reposition(): void;
        private _processButtonClick;
        private _toggleMenuTool;
        private _startModal;
        private _alignMenuToTool;
        private _showSubmenu;
        private _hideActiveSubmenu;
        private _activateSubmenuItem;
        private _submenuIconClick;
        private _initIcons;
        private _removeNonApplicableIcons;
        setSubmenuEnabled(buttonId: HtmlId, enabled: boolean): void;
        private _performNullaryAction;
        private _performBooleanAction;
        private _renderModeClick;
        private _initSnapshot;
        _doSnapshot(): Promise<void>;
        private _setRedlineOperator;
        private _initActions;
        private _onExplosionSlider;
        private _explosionButtonClick;
        private _settingsButtonClick;
        updateEdgeFaceButton(): void;
        private _cuttingPlaneButtonClick;
        private _getAxis;
        private _updateCuttingPlaneIcons;
        private _updateCuttingPlaneIcon;
        private _orientToFace;
    }
}
declare namespace Communicator.Ui {
    class ColorPicker {
        private readonly _viewer;
        private readonly _colorPickerId;
        private readonly _colorPickerHeaderId;
        private readonly _colorPickerFooterId;
        private readonly _colorPickerOkId;
        private readonly _colorPickerCancelId;
        private readonly _colorPickerApplyId;
        private readonly _colorPickerInputId;
        private readonly _colorPickerActiveColorId;
        private readonly _colorPickerActiveColorLabelId;
        private readonly _colorPickerActiveColorSwatchId;
        private readonly _colorPicker;
        private _color;
        constructor(viewer: WebViewer, containerId: HtmlId);
        private _createColorPickerWindow;
        private _initElements;
        private _updateColor;
        show(): void;
        hide(): void;
        getColor(): Color;
    }
}
declare namespace Communicator.Ui.Desktop {
    type AnyViewTree = ModelTree | CadViewTree | SheetsTree | ConfigurationsTree | LayersTree | FiltersTree | TypesTree | BCFTree | RelationshipsTree;
    enum Tree {
        Model = 0,
        CadView = 1,
        Sheets = 2,
        Configurations = 3,
        Layers = 4,
        Filters = 5,
        Types = 6,
        BCF = 7,
        Relationships = 8
    }
    class ModelBrowser {
        private _elementId;
        private _containerId;
        private _viewer;
        private _isolateZoomHelper;
        private _colorPicker;
        private _contextMenu;
        private _canvasSize;
        private _treeMap;
        private _scrollTreeMap;
        private _elementIdMap;
        private _relationshipTree;
        private _header;
        private _content;
        private _minimizeButton;
        private _modelBrowserTabs;
        private _propertyWindow;
        private _treePropertyContainer;
        private _relationshipsWindow;
        private _browserWindow;
        private _browserWindowMargin;
        private _scrollRefreshTimer;
        private _scrollRefreshTimestamp;
        private _scrollRefreshInterval;
        private _minimized;
        private _modelHasRelationships;
        constructor(elementId: HtmlId, containerId: HtmlId, viewer: WebViewer, isolateZoomHelper: IsolateZoomHelper, colorPicker: ColorPicker);
        private _computeRelationshipTreeVisibility;
        private _initEvents;
        private _registerScrollRefreshCallbacks;
        private _refreshBrowserScroll;
        private _setPropertyWindowVisibility;
        private _updateRelationshipsTreeVisibility;
        private _setRelationshipsWindowVisibility;
        private _setTreeVisibility;
        /** @hidden */
        _showTree(activeTreeType: Tree): void;
        _getContextMenu(): ModelBrowserContextMenu;
        _addTree(elementId: HtmlId, treeType: Tree): void;
        private _createBrowserWindow;
        private _createDiv;
        private _createHeader;
        private _createIScrollWrapper;
        private _createBrowserTab;
        private _initializeIScroll;
        private _createRelationshipTree;
        private _createPropertyWindow;
        private _onMinimizeButtonClick;
        _maximizeModelBrowser(): void;
        private _minimizeModelBrowser;
        private onResize;
        private onResizeElement;
        private _onSlide;
        private _onModelStructureParsingBegin;
        private _onModelStructureLoadBegin;
        private _onAssemblyTreeReady;
        freeze(freeze: boolean): void;
        enablePartSelection(enable: boolean): void;
        updateSelection(items: Selection.NodeSelectionItem[] | null): void;
        /** @hidden */
        _getTree(tree: Tree.Model): ModelTree;
        /** @hidden */
        _getTree(tree: Tree.CadView): CadViewTree;
        /** @hidden */
        _getTree(tree: Tree.Sheets): SheetsTree;
        /** @hidden */
        _getTree(tree: Tree.Configurations): ConfigurationsTree;
        /** @hidden */
        _getTree(tree: Tree.Layers): LayersTree;
        /** @hidden */
        _getTree(tree: Tree.Filters): FiltersTree;
        /** @hidden */
        _getTree(tree: Tree.Types): TypesTree;
        /** @hidden */
        _getTree(tree: Tree.BCF): BCFTree;
        /** @hidden */
        _getTree(tree: Tree.Relationships): RelationshipsTree;
        /** @hidden */
        _getTree(tree: Tree): RelationshipsTree;
    }
}
declare namespace Communicator.Ui.Desktop {
    interface UiConfig {
        /** The ID of the div element to use for the UI. */
        containerId?: HtmlId;
        /** Specifies what UI style to use. Default value is [[ScreenConfiguration.Desktop]] */
        screenConfiguration?: ScreenConfiguration;
        /** Specifies whether the model browser is shown. Default is true. */
        showModelBrowser?: boolean;
        /** Specifies whether the toolbar is shown. Default is true. */
        showToolbar?: boolean;
    }
    class DesktopUi {
        private static _defaultBackgroundColor;
        private static _defaultPartSelectionColor;
        private static _defaultPartSelectionOutlineColor;
        private static _defaultXRayColor;
        private _viewer;
        private _modelBrowser;
        private _toolbar;
        private _contextMenu;
        private _isolateZoomHelper;
        private _cuttingPlaneController;
        private _colorPicker;
        /** The `ModelType` derived from the current model. */
        private _modelType;
        /** The `ModelType` for which the UI is configured. */
        private _uiModelType;
        private _suppressMissingModelDialog;
        private readonly _params;
        private _getWithDefault;
        /**
         * Creates a new Web Viewer instance. You must pass in a **containerId** key with the ID of an element.
         * The system will create any required elements inside the supplied container.
         *
         * @param inputParams object containing key-value pairs for UI options.
         */
        constructor(viewer: WebViewer, inputParams: UiConfig);
        private _determineModelType;
        private _isBim;
        private _configureUi;
        private _configureToolbar;
        private _configureModelBrowser;
        private _onSceneReady;
        setDeselectOnIsolate(deselect: boolean): void;
        freezeModelBrowser(freeze: boolean): void;
        enableModelBrowserPartSelection(enable: boolean): void;
        /** @hidden */
        _getContextMenu(): RightClickContextMenu;
        /** @hidden */
        _getModelBrowser(): ModelBrowser | null;
        /** @hidden */
        _getToolbar(): Toolbar | null;
    }
}
declare namespace Communicator.Ui.Desktop {
    class ModelBrowserContextMenu extends Context.ContextMenu {
        private readonly _treeMap;
        constructor(containerId: HtmlId, viewer: WebViewer, treeMap: Map<Tree, ViewTree | BCFTree>, isolateZoomHelper: IsolateZoomHelper, colorPicker: ColorPicker);
        private _initEvents;
        private _registerContextMenuCallback;
        private _onTreeContext;
        protected _onContextLayerClick(_event: JQuery.MouseDownEvent): void;
    }
}
declare namespace Communicator.Ui.Desktop {
    class PropertyWindow {
        private readonly _viewer;
        private readonly _propertyWindow;
        private _assemblyTreeReadyOccurred;
        private _incrementalSelectionActive;
        constructor(viewer: WebViewer);
        private _update;
        private _onModelStructureReady;
        private _createRow;
        private _onPartSelection;
    }
}
declare namespace Communicator.Ui {
    /**
     * The default duration in milliseconds of UI transitions such as model tree
     * expansion or scrolling to selected nodes.
     */
    let DefaultUiTransitionDuration: number;
    function colorFromRgbString(rgbStr: string): Color;
    function rgbStringFromColor(color: Color | null | undefined): string;
    function cssHexStringFromColor(color: Color): string;
    function getValueAsString(id: HtmlId): string;
    function centerWindow(htmlId: HtmlId, canvasSize: Point2): void;
}
declare namespace Communicator.Ui.Desktop {
    enum SettingTab {
        General = 0,
        Walk = 1,
        Drawing = 2,
        Floorplan = 3
    }
    class ViewerSettings {
        private readonly _viewer;
        private readonly _viewerSettingsSelector;
        private _versionInfo;
        private readonly _axisTriad;
        private readonly _navCube;
        private _splatRenderingEnabled;
        private _splatRenderingSize;
        private _splatRenderingPointSizeUnit;
        private _floorplanActive;
        private _honorSceneVisibility;
        private _walkSpeedUnits;
        private readonly _generalTabLabelId;
        private readonly _walkTabLabelId;
        private readonly _drawingTabLabelId;
        private readonly _floorplanTabLabelId;
        private readonly _generalTabId;
        private readonly _walkTabId;
        private readonly _drawingTabId;
        private readonly _floorplanTabId;
        private readonly _walkKeyIdsMap;
        constructor(viewer: WebViewer);
        show(): Promise<void>;
        hide(): void;
        private _scaleForMobile;
        private _initElements;
        /** @hidden */
        _switchTab(tab: SettingTab): void;
        private _updateSettings;
        private _applySettings;
        private _applyWalkKeyText;
        private _applyWalkSettings;
        private _updateKeyboardWalkModeStyle;
        private _updateWalkSpeedUnits;
        private _updateWalkSettingsHelper;
        private _updateWalkSettings;
        private _updateDrawingSettings;
        private _applyDrawingSettings;
        private _updateFloorplanSettings;
        private _applyFloorplanSettings;
        private _updateEnabledStyle;
    }
}
declare namespace Communicator.Ui {
    type TreeCallbackName = "addChild" | "loadChildren" | "collapse" | "context" | "expand" | "selectItem" | "clickItemButton";
    class ViewTree {
        protected readonly _tree: Control.TreeControl;
        protected readonly _viewer: WebViewer;
        protected readonly _internalId: HtmlId;
        static readonly separator = "_";
        static readonly visibilityPrefix = "visibility";
        protected readonly _maxNodeChildrenSize = 300;
        constructor(viewer: WebViewer, elementId: HtmlId, iScroll: IScroll | null);
        getElementId(): HtmlId;
        registerCallback(name: "addChild", func: () => void): void;
        registerCallback(name: "collapse", func: (id: HtmlId) => void): void;
        registerCallback(name: "context", func: (id: HtmlId, position: Point2) => void): void;
        registerCallback(name: "expand", func: (id: HtmlId) => void): void;
        registerCallback(name: "loadChildren", func: (id: HtmlId) => void): void;
        registerCallback(name: "selectItem", func: (id: HtmlId, mode: SelectionMode) => void): void;
        protected _splitHtmlId(htmlId: HtmlId): [string, string];
        protected _splitHtmlIdParts(htmlId: HtmlId, separator: string): [string, string];
        protected hideTab(): void;
        protected showTab(): void;
        /** @hidden */
        _getTreeControl(): Control.TreeControl;
    }
}
declare namespace Communicator.Ui {
    class BCFTree {
        private readonly _viewer;
        private readonly _elementId;
        private readonly _listRoot;
        private readonly _bcfDataList;
        private readonly _scroll;
        private _idCount;
        private _viewpointIdMap;
        private _bcfIdMap;
        private _topicGuidMap;
        private _topicTitleGuidMap;
        private _topicCommentsGuidMap;
        private _commentGuidMap;
        constructor(viewer: WebViewer, elementId: HtmlId, iScroll: IScroll | null);
        hideTab(): void;
        showTab(): void;
        getElementId(): HtmlId;
        private _refreshScroll;
        private _showBCFData;
        private _events;
        private _addBCFComment;
        private _addSnapshot;
        /** @hidden */
        _removeBcf(bcfId: number): void;
        private _buildRemoveBCF;
        /** @hidden */
        _addBcf(bcfName: BCFName): Promise<BCFData>;
        private _buildAddBCF;
        private _buildOpenBCF;
        /** @hidden */
        _addTopic(bcfData: BCFData, topicTitle: string): Promise<BCFTopic>;
        private _buildAddTopic;
        private _initEvents;
        private _buildBCFNode;
        private _buildDiv;
        private _buildEditDiv;
        private _buildImage;
        private _buildDeleteComment;
        private _buildEditComment;
        private _buildComment;
        /** @hidden */
        _addComment(bcfTopic: BCFTopic, text: string): Promise<BCFComment>;
        /** @hidden */
        _deleteComment(bcfTopic: BCFTopic, bcfComment: BCFComment, commentElementId: HtmlId): void;
        _setCommentText(bcfComment: BCFComment, text: string): void;
        private _buildAddComment;
        private _buildTopicData;
        private _formatDate;
        /** @hidden */
        _deleteTopic(bcfData: BCFData, bcfTopic: BCFTopic): boolean;
        private _buildDeleteTopic;
        /** @hidden */
        _setTopicTitle(bcfTopic: BCFTopic, topicTitle: string): void;
        private _buildTopic;
        private _buildSelectOption;
        private _appendBCF;
        /** @hidden */
        _getBcfHtmlId(id: number): HtmlId | null;
        private _removeBCF;
        private _getViewpointFromComment;
        private _getId;
        private _getSelectId;
        private _onTreeSelectItem;
        private _getViewpoint;
    }
}
declare namespace Communicator.Ui {
    class CadViewTree extends ViewTree {
        private readonly _annotationViewsString;
        private readonly _annotationViewsLabel;
        private _viewFolderCreated;
        private _lastSelectedhtmlId;
        private _cadViewIds;
        constructor(viewer: WebViewer, elementId: HtmlId, iScroll: IScroll | null);
        private _initEvents;
        private _modelSwitched;
        private _updateCadViews;
        private _addCadViews;
        private _allowView;
        private _createCadViewNodes;
        private _onTreeSelectItem;
        private _cadViewId;
    }
    /** @deprecated Use [[CadViewTree]] instead. */
    type CADViewTree = CadViewTree;
    /** @deprecated Use [[CadViewTree]] instead. */
    const CADViewTree: typeof CadViewTree;
}
declare namespace Communicator.Ui {
    class ConfigurationsTree extends ViewTree {
        constructor(viewer: WebViewer, elementId: HtmlId, iScroll: IScroll | null);
        private _initEvents;
        private _modelSwitched;
        private _onNewModel;
        private _createConfigurationNodes;
        private _onTreeSelectItem;
        private _configurationsId;
    }
}
declare namespace Communicator.Ui {
    class FiltersTree extends ViewTree {
        constructor(viewer: WebViewer, elementId: HtmlId, iScroll: IScroll | null);
        private _initEvents;
        private _onTreeSelectItem;
        private _setFilter;
        private _onNewModel;
        getFilterId(id: NodeId): HtmlId;
    }
}
declare namespace Communicator.Ui {
    class LayersTree extends ViewTree {
        static readonly layerPrefix = "layer";
        static readonly layerPartPrefix = "layerpart";
        static readonly layerPartContainerPrefix = "layerpartcontainer";
        private _layerNames;
        private _layerParts;
        private static _idCount;
        private static _layerIdMap;
        private static _idLayerMap;
        private static _layerPartIdMap;
        private static _idLayerPartMap;
        private static _layerContainersMap;
        constructor(viewer: WebViewer, elementId: HtmlId, iScroll: IScroll | null);
        private _initEvents;
        private _onTreeSelectItem;
        private _selectLayerPart;
        private _selectLayer;
        private _onNewModel;
        private _loadNodeChildren;
        private _addLayerParts;
        private _addLayerPartContainers;
        _expandPart(nodeId: NodeId): void;
        private static _createLayerId;
        private static _createContainerId;
        private static _createPartId;
        /**
         * Takes a layer [[HtmlId]] and returns the name of the corresponding layer.
         * @param layerId
         */
        static getLayerName(layerId: HtmlId): LayerName | null;
        /**
         * Takes a layerName and returns a corresponding layer [[HtmlId]].
         * @param layerName
         */
        static getLayerId(layerName: LayerName): HtmlId | null;
        /**
         * Takes a layerPart [[HtmlId]] and returns the corresponding [[NodeId]].
         * @param layerPartId
         */
        static getPartId(layerPartId: HtmlId): NodeId | null;
        /**
         * Takes a [[NodeId]] and returns the corresponding layerPart [[HtmlId]].
         * @param nodeId
         */
        static getLayerPartId(nodeId: NodeId): HtmlId | null;
    }
}
declare namespace Communicator.Ui {
    class ModelTree extends ViewTree {
        private _lastModelRoot;
        private _startedWithoutModelStructure;
        private _partSelectionEnabled;
        private _currentSheetId;
        private _measurementFolderId;
        private _updateVisibilityStateTimer;
        private _updateSelectionTimer;
        constructor(viewer: WebViewer, elementId: HtmlId, iScroll: IScroll | null);
        freezeExpansion(freeze: boolean): void;
        modelStructurePresent(): boolean;
        enablePartSelection(enable: boolean): void;
        private _initEvents;
        private _refreshModelTree;
        private _reset;
        private _onNewModel;
        private _createMarkupViewFolderIfNecessary;
        private _createMeasurementFolderIfNecessary;
        private _parentChildrenLoaded;
        private _onSubtreeLoaded;
        private _onSubtreeDeleted;
        private _buildTreePathForNode;
        private _expandCorrectContainerForNodeId;
        private _expandPmiFolder;
        private _isInsideContainer;
        private _isInsidePmiFolder;
        _expandPart(nodeId: NodeId): void;
        private _onPartSelection;
        private _createContainerNodes;
        private _loadAssemblyNodeChildren;
        private _loadContainerChildren;
        private _processNodeChildren;
        private _loadNodeChildren;
        private _onTreeSelectItem;
        private _onContainerClick;
        private _onNewView;
        private _refreshMarkupViews;
        private _addMarkupView;
        private _onNewMeasurement;
        private _onDeleteMeasurement;
        private _updateMeasurementsFolderVisibility;
        private _measurementId;
        private _partId;
        private _pmiFolderId;
        private _viewId;
        private _containerId;
        private _splitContainerId;
        updateSelection(items: Selection.NodeSelectionItem[] | null): void;
    }
}
declare namespace Communicator.Ui {
    type RelationshipTypeName = string;
    type RelationshipName = string;
    interface RelationshipInfo {
        name: string;
        nodeId: BimId;
        processed: boolean;
    }
    class RelationshipsTree extends ViewTree {
        static readonly RelationshipPrefix = "relships";
        static readonly RelationshipTypePrefix = "relshipsType";
        static readonly RelationshipPartPrefix = "relshipsPart";
        private static _idCount;
        private static _nameIdMap;
        private static _idNameMap;
        private _currentNodeId;
        private _currentBimNodeId;
        constructor(viewer: WebViewer, elementId: HtmlId, iScroll: IScroll | null);
        private _initEvents;
        private _onTreeSelectItem;
        private _translateTypeRelationshipToString;
        private _translateStringTypeToRelationshipType;
        private static _createIdNode;
        private static _createIdType;
        /**
         * Takes a relation [[HtmlId]] and returns the name of the corresponding relation.
         * @param relationId
         */
        static getRelationshipTypeName(relationshipTypeId: HtmlId): RelationshipTypeName | null;
        /**
         * Takes a relationName and returns a corresponding relation [[HtmlId]].
         * @param relationName
         */
        static getRelationshipTypeId(relationshipTypeName: RelationshipTypeName): HtmlId | null;
        private _onNewModel;
        private _update;
        private _loadNodeChildren;
        private _addRelationships;
        private _onclickItemButton;
        private _onSelectRelationships;
    }
}
declare namespace Communicator.Ui {
    class SheetsTree extends ViewTree {
        private _currentSheetId;
        private readonly _3dSheetId;
        constructor(viewer: WebViewer, elementId: HtmlId, iScroll: IScroll | null);
        private _initEvents;
        private _setCurrentSheetId;
        private _onNewModel;
        private _onSheetActivated;
        private _onSheetDeactivated;
        private _onTreeSelectItem;
        private _sheetTreeId;
    }
}
declare namespace Communicator.Ui.Control {
    type ItemType = "modelroot" | "assembly" | "body" | "part" | "container" | "view" | "viewfolder" | "measurement" | "configurations" | "sheet" | "layer" | "filter";
    class VisibilityControl {
        private readonly _viewer;
        private readonly _fullHiddenParentIds;
        private readonly _partialHiddenParentIds;
        private _assemblyTreeReadyOccurred;
        constructor(viewer: WebViewer);
        private _clearStyles;
        private _applyStyles;
        updateModelTreeVisibilityState(): void;
        private _getVisibilityItem;
        private _addVisibilityHiddenClass;
        private _removeVisibilityHiddenClass;
    }
    class TreeControl {
        private readonly _elementId;
        private readonly _listRoot;
        private readonly _partVisibilityRoot;
        private readonly _separator;
        private _$lastNonSelectionItem?;
        private _lastItemId;
        private readonly _selectedPartItems;
        private readonly _futureHighlightIds;
        private readonly _futureMixedIds;
        private readonly _selectedItemsParentIds;
        private _selectedLayers;
        private readonly _mixedItemsLayer;
        private _selectedTypes;
        private _futureMixedTypesIds;
        private _mixedTypes;
        private _visibilityControl;
        private readonly _callbacks;
        private readonly _childrenLoaded;
        private readonly _loadedNodes;
        private _touchTimer;
        private _freezeExpansion;
        private _scrollTimer;
        private _selectionLabelHighlightTimer;
        private readonly _viewer;
        private readonly _treeScroll;
        private _createVisibilityItems;
        constructor(elementId: HtmlId, viewer: WebViewer, separator: string, treeScroll: IScroll | null);
        setCreateVisibilityItems(createVisibilityItems: boolean): void;
        getElementId(): HtmlId;
        getRoot(): HTMLElement;
        getPartVisibilityRoot(): HTMLElement;
        getVisibilityControl(): VisibilityControl;
        registerCallback(name: "addChild", func: () => void): void;
        registerCallback(name: "collapse", func: (id: HtmlId) => void): void;
        registerCallback(name: "context", func: (id: HtmlId, position: Point2) => void): void;
        registerCallback(name: "expand", func: (id: HtmlId) => void): void;
        registerCallback(name: "loadChildren", func: (id: HtmlId) => void): void;
        registerCallback(name: "selectItem", func: (id: HtmlId, mode: SelectionMode) => void): void;
        registerCallback(name: TreeCallbackName, func: Function): void;
        registerCallback(name: "clickItemButton", func: (id: HtmlId) => void): void;
        private _triggerCallback;
        deleteNode(htmlId: HtmlId): void;
        private _getTaggedId;
        addChild(name: string | null, htmlId: HtmlId, parent: HtmlId, itemType: ItemType, hasChildren: boolean, treeType: Desktop.Tree, accessible?: boolean, ignoreLoaded?: boolean): HTMLElement | null;
        private _addVisibilityToggleChild;
        private _buildPartVisibilityNode;
        freezeExpansion(freeze: boolean): void;
        updateSelection(items: (Event.NodeSelectionEvent | Selection.NodeSelectionItem)[] | null): void;
        collapseAllChildren(elementId: HtmlId): void;
        private _expandChildren;
        expandChildren(htmlId: HtmlId): void;
        _expandVisibilityChildren(htmlId: HtmlId): void;
        collapseChildren(htmlId: HtmlId): void;
        private _collapseVisibilityChildren;
        private _buildNode;
        childrenAreLoaded(htmlId: HtmlId): boolean;
        preloadChildrenIfNecessary(htmlId: HtmlId): void;
        private _processExpandClick;
        /** @hidden */
        _collapseListItem(htmlId: HtmlId): void;
        /** @hidden */
        _expandListItem(htmlId: HtmlId): void;
        selectItem(htmlId: HtmlId | null, triggerEvent?: boolean): void;
        highlightItem(htmlId: HtmlId | null, triggerEvent?: boolean): void;
        private _getListItem;
        private _updateNonSelectionHighlight;
        private _doUnfreezeSelection;
        /** @hidden */
        _doSelection(htmlId: HtmlId | null, triggerEvent?: boolean): void;
        _doHighlight(htmlId: HtmlId | null, triggerEvent?: boolean): void;
        _doSelectIfcItem(htmlId: HtmlId | null, triggerEvent?: boolean): void;
        /**
         * Increased by automated tests for more consistent behavior.
         * @hidden
         */
        static _ScrollToItemDelayMs: number;
        private _scrollToItem;
        private _parseTaggedId;
        private _parseNodeId;
        private _parseUuid;
        private _parseMeasurementId;
        private _parseVisibilityLayerName;
        private _parseVisibilityLayerNodeId;
        private _updateLayerTreeSelectionHighlight;
        private _addMixedTypeClass;
        private _updateTypesTreeSelectionHighlight;
        private _updateTreeSelectionHighlight;
        private _updateParentIdList;
        private _updateMixedLayers;
        private _updateMixedTypes;
        private _processLabelContext;
        private _processLabelClick;
        private _processLabelRSClick;
        private _processLabelRSClickButton;
        appendTopLevelElement(name: string | null, htmlId: HtmlId, itemType: ItemType, hasChildren: boolean, loadChildren?: boolean, markChildrenLoaded?: boolean): HTMLElement;
        insertNodeAfter(name: string | null, htmlId: HtmlId, itemType: string, element: HTMLElement, hasChildren: boolean): HTMLElement;
        /** @hidden */
        _insertNodeAfter(name: string | null, htmlId: HtmlId, itemType: ItemType, element: HTMLElement, hasChildren: boolean): HTMLElement;
        clear(): void;
        expandInitialNodes(htmlId: HtmlId): void;
        /** @hidden */
        _processVisibilityClick(htmlId: HtmlId): Promise<void>;
        private _processPartVisibilityClick;
        _processPartVisibility(nodeId: NodeId): Promise<void>;
        private _processMeasurementVisibilityClick;
        private _processTypesVisibilityClick;
        _processTypesVisibility(type: GenericType): Promise<void>;
        private _processTypesPartVisibilityClick;
        _processTypesPartVisibility(nodeId: NodeId): Promise<void>;
        updateTypesVisibilityIcons(): void;
        private _processLayerVisibilityClick;
        private _processLayerPartVisibilityClick;
        updateLayersVisibilityIcons(): void;
        updateMeasurementVisibilityIcons(): void;
        private _init;
        private _getChildItemsFromModelTreeItem;
    }
}
declare namespace Communicator.Ui {
    class TypesTree extends ViewTree {
        private _ifcNodesMap;
        private _containerMap;
        constructor(viewer: WebViewer, elementId: HtmlId, iScroll: IScroll | null);
        private _initEvents;
        private _onTreeSelectItem;
        private _loadNodeChildren;
        private _loadGenericTypeChildren;
        private _loadContainerChildren;
        private _addChildPart;
        private _createContainerNodes;
        private _selectIfcComponent;
        private _onNewModel;
        static getComponentPartId(id: NodeId): HtmlId;
        static getGenericTypeId(genericType: GenericType): HtmlId;
        private static getContainerId;
    }
}
