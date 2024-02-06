import { IHandlers } from "../../ts-common/types";
import { VNode } from "../../ts-common/dom";
import { IEventSystem } from "../../ts-common/events";
import { View } from "../../ts-common/view";
import { TreeCollection, IDataEventsHandlersMap } from "../../ts-data";
import { DataEvents, IItem, IGroups, NavigationBarEvents, INavbarEventHandlersMap, INavbarConfig } from "./types";
export declare abstract class Navbar<T extends IItem = IItem> extends View {
    data: TreeCollection<T>;
    events: IEventSystem<DataEvents | NavigationBarEvents, IDataEventsHandlersMap & INavbarEventHandlersMap>;
    config: INavbarConfig;
    protected _vpopups: VNode;
    protected _activeMenu: string;
    protected _activePosition: {
        left: number;
        right: number;
        top: number;
        bottom: number;
        zIndex?: number;
    };
    protected _isContextMenu: boolean;
    protected _handlers: IHandlers;
    protected _currentRoot: string;
    protected _factory: (item: T, asMenuItem?: boolean) => any;
    protected _groups: IGroups;
    private _isActive;
    private _popupActive;
    private _currentTimeout;
    private _documentClick;
    private _documentHaveListener;
    private _rootItem;
    private _activeParents;
    private _keyManager;
    constructor(element?: string | HTMLElement, config?: any);
    paint(): void;
    disable(ids: string | string[]): void;
    enable(ids: string | string[]): void;
    isDisabled(id: string): boolean;
    show(ids: string | string[]): void;
    hide(ids: string | string[]): void;
    destructor(): void;
    select(id: string, unselect?: boolean): void;
    unselect(id?: string): void;
    isSelected(id: string): boolean;
    getSelected(): string[];
    protected abstract _getFactory(): (item: T, asMenuItem?: boolean) => any;
    protected _customHandlers(): {};
    protected _close(e: MouseEvent): void;
    protected _init(): void;
    protected _initHandlers(): void;
    protected _initEvents(): void;
    protected _getMode(item: T, root: string, _active?: boolean): "bottom" | "right";
    protected _drawMenuItems(id: string, asMenuItem?: boolean): any[];
    protected _setRoot(_id: string): void;
    protected _getParents(id: any, root: any): string[];
    protected _listenOuterClick(): void;
    protected _customInitEvents(): void;
    private _drawPopups;
    private _onMenuItemClick;
    private _activeItemChange;
    private _resetHotkeys;
    private _setProp;
}
