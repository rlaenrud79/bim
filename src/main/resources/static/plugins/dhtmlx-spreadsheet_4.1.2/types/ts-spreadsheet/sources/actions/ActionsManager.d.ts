import { Actions, IActionsManager, IExecuteConfig } from "../types";
import { AddColumn } from "./AddColumn";
import { AddRow } from "./AddRow";
import { AddSheet } from "./AddSheet";
import { DeleteColumn } from "./DeleteColumn";
import { DeleteRow } from "./DeleteRow";
import { DeleteSheet } from "./DeleteSheet";
import { GroupAction } from "./GroupAction";
import { GroupColAction } from "./GroupColAction";
import { GroupRowAction } from "./GroupRowAction";
import { LockCell } from "./LockCell";
import { RemoveCellStyles } from "./RemoveCellStyles";
import { RenameSheet } from "./RenameSheet";
import { SetCellFormat } from "./SetCellFormat";
import { SetCellStyle } from "./SetCellStyle";
import { SetCellValue } from "./SetCellValue";
export declare const actions: {
    setCellStyle: typeof SetCellStyle;
    setCellValue: typeof SetCellValue;
    setCellFormat: typeof SetCellFormat;
    removeCellStyles: typeof RemoveCellStyles;
    lockCell: typeof LockCell;
    deleteRow: typeof DeleteRow;
    addRow: typeof AddRow;
    deleteColumn: typeof DeleteColumn;
    addColumn: typeof AddColumn;
    groupAction: typeof GroupAction;
    groupRowAction: typeof GroupRowAction;
    groupColAction: typeof GroupColAction;
    addSheet: typeof AddSheet;
    deleteSheet: typeof DeleteSheet;
    renameSheet: typeof RenameSheet;
};
export declare class ActionsManager implements IActionsManager {
    private _actions;
    private _redoActions;
    private _config;
    private _dataStore;
    private _sheets;
    private _redoSheets;
    constructor(config: any);
    execute(command: Actions | IExecuteConfig[], config?: IExecuteConfig): void;
    undo(): void;
    redo(): void;
}
