import { IHandlers } from "../../ts-common/types";
import { IEventSystem } from "../../ts-common/events";
import { IGrid } from "../../ts-grid";
import { IToolbar } from "../../ts-toolbar";
import { ContextMenu, Menu } from "../../ts-menu";
import { EditLine } from "./EditLine";
import { Exporter } from "./Exporter";
import { IFormula, DataPage, DataStore } from "../../muon";
import { DataCollection } from "../../ts-data";
import { IKeyManager } from "../../ts-common/newKeyManager";
export interface ISpreadsheetConfig {
    toolbarBlocks?: ToolbarBlocks[];
    rowsCount?: number;
    colsCount?: number;
    editLine?: boolean;
    menu?: boolean;
    readonly?: boolean;
    autoFormat?: boolean;
    leftSplit?: number;
    multiSheets?: boolean;
    hotkeys?: IHandlers;
    importModulePath?: string;
    exportModulePath?: string;
    formats?: IFormats[];
}
export declare type formatAliases = "common" | "number" | "currency" | "percent" | "text";
export interface IFormats {
    id: formatAliases | string;
    mask: string;
    name?: string;
    example?: string;
}
export declare type ToolbarBlocks = "default" | "undo" | "colors" | "columns" | "rows" | "cells" | "lock" | "align" | "decoration" | "clear" | "help" | "format" | "file";
export interface IStylesList {
    [key: string]: string;
}
export interface ISpreadsheet {
    selection: ISelection;
    events: IEventSystem<SpreadsheetEvents>;
    config: ISpreadsheetConfig;
    toolbar: IToolbar;
    menu: Menu;
    contextMenu: ContextMenu;
    container: HTMLElement;
    export: Exporter;
    keyManager: IKeyManager;
    getFormula: (cell: string) => string | string[];
    getValue: (cell: string) => any | any[];
    setValue: (cell: string, value: any | any[]) => void;
    getStyle: (cell: string) => IStylesList | IStylesList[];
    setStyle: (cell: string, styles: string | string[] | IStylesList | IStylesList[]) => void;
    getFormat: (cell: string) => string | string[];
    setFormat: (cell: string, format: string | string[]) => void;
    getCellIndex: (cell1: string, cell2: string) => ICell;
    lock: (cell: string) => void;
    unlock: (cell: string) => void;
    isLocked: (cell: string) => boolean;
    deleteColumn: (cell: string) => void;
    deleteRow: (cell: string) => void;
    addColumn: (cell: string) => void;
    addRow: (cell: string) => void;
    load: (url: string, type?: FileFormat) => Promise<any>;
    parse: (data: any) => void;
    serialize: () => any[] | IDataWithStyles;
    eachCell: (cb: (cellName: string, cellValue: any) => any, range?: string) => void;
    removeSheet: (id: string) => void;
    addSheet: (name?: string) => string;
    getSheets: () => ISheet[];
    getActiveSheet: () => ISheet;
    undo(): void;
    redo(): void;
    startEdit(cell?: string, initialValue?: string): any;
    endEdit(withoutSave?: boolean): void;
}
export interface ISelection {
    setSelectedCell(cell: string): any;
    getSelectedCell(): string;
    setFocusedCell(cell: string): any;
    getFocusedCell(): string;
}
export interface ISheet {
    name: string;
    id: string;
}
export interface IDataWithStyles {
    data?: ICellInfo[];
    styles: {
        [key: string]: any;
    };
    formats?: IFormats[];
    columns?: {
        [key: string]: {
            width: string | number;
        };
    };
    sheets?: ISheetData[];
}
export interface ISheetData {
    data: ICellInfo[];
    name?: string;
    id?: string;
}
export declare enum SpreadsheetEvents {
    beforeValueChange = "beforeValueChange",
    afterValueChange = "afterValueChange",
    beforeStyleChange = "beforeStyleChange",
    afterStyleChange = "afterStyleChange",
    beforeFormatChange = "beforeFormatChange",
    afterFormatChange = "afterFormatChange",
    beforeSelectionSet = "beforeSelectionSet",
    afterSelectionSet = "afterSelectionSet",
    beforeRowAdd = "beforeRowAdd",
    afterRowAdd = "afterRowAdd",
    beforeRowDelete = "beforeRowDelete",
    afterRowDelete = "afterRowDelete",
    beforeColumnAdd = "beforeColumnAdd",
    afterColumnAdd = "afterColumnAdd",
    beforeColumnDelete = "beforeColumnDelete",
    afterColumnDelete = "afterColumnDelete",
    beforeFocusSet = "beforeFocusSet",
    afterFocusSet = "afterFocusSet",
    beforeEditStart = "beforeEditStart",
    afterEditStart = "afterEditStart",
    beforeEditEnd = "beforeEditEnd",
    afterEditEnd = "afterEditEnd",
    groupFill = "groupFill",
    beforeSheetAdd = "beforeSheetAdd",
    afterSheetAdd = "afterSheetAdd",
    beforeSheetRemove = "beforeSheetRemove",
    afterSheetRemove = "afterSheetRemove",
    beforeSheetRename = "beforeSheetRename",
    afterSheetRename = "afterSheetRename",
    beforeSheetChange = "beforeSheetChange",
    afterSheetChange = "afterSheetChange",
    editLineInput = "editLineInput",
    editLineFocus = "editLineFocus",
    editLineBlur = "editLineBlur",
    cellInput = "cellInput",
    gridRedraw = "gridRedraw"
}
export interface IAction {
    do(...args: any[]): any;
    undo(): void;
}
export interface IExecuteConfig {
    row?: number;
    col?: number;
    target?: any;
    val?: any;
    prev?: any;
    action?: Actions;
    groupAction?: Actions;
    cell?: string;
    dataStore: DataStore;
    sheets?: DataCollection;
    page?: DataPage;
}
export interface IActionConfig extends IExecuteConfig {
    spreadsheet?: ISpreadsheet;
    grid?: IGrid;
    editLine?: EditLine;
    activePage: DataPage;
}
export declare enum Actions {
    setCellStyle = "setCellStyle",
    setCellValue = "setCellValue",
    setCellFormat = "setCellFormat",
    removeCellStyles = "removeCellStyles",
    lockCell = "lockCell",
    deleteRow = "deleteRow",
    addRow = "addRow",
    deleteColumn = "deleteColumn",
    addColumn = "addColumn",
    groupAction = "groupAction",
    groupRowAction = "groupRowAction",
    groupColAction = "groupColAction",
    addSheet = "addSheet",
    deleteSheet = "deleteSheet",
    renameSheet = "renameSheet"
}
export interface IActionsManager {
    execute(commandsPack: IExecuteConfig[]): void;
    execute(command: Actions, config?: IExecuteConfig): void;
    undo(): void;
    redo(): void;
}
export declare type CellFormats = string;
export declare type CellTypes = "string" | "number";
export interface ICellInfo {
    cell: string;
    value: string;
    css?: string;
    format?: CellFormats;
}
export interface ICellMeta {
    locked?: boolean;
    edited?: boolean;
    selected?: boolean;
    css?: string;
    editorValue?: string;
    nextValue?: any;
    format?: CellFormats;
    type?: CellTypes;
    [key: string]: any;
}
export interface IRow {
    id: string;
}
export interface ICol {
    id: string;
    $edit?: any;
    header?: any[];
}
export interface ICell {
    row: string;
    col: string;
}
export interface IRange {
    start: ICell;
    end: ICell;
}
export interface ICellIndex {
    row: number;
    col: number;
}
export interface IRangeIndex {
    start: ICellIndex;
    end: ICellIndex;
}
export interface IBufferManager {
    store: (operation: IBufferStruct["operation"], col?: number, row?: number) => void;
    paste: () => void;
    getStruct: () => IBufferStruct;
}
export interface IBufferStruct {
    value: string | string[];
    styles: IStylesList | IStylesList[];
    cell: string;
    math: IFormula[];
    cells: string;
    formats: string[] | string;
    inserted: boolean;
    operation: "cut" | "copy" | "";
}
export declare type FileFormat = "json" | "csv" | "xlsx";
