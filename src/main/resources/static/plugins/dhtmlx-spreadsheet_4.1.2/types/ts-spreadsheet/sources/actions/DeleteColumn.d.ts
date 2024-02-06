import { IAction, IActionConfig } from "./../types";
export declare class DeleteColumn implements IAction {
    config: IActionConfig;
    private _col;
    private _cells;
    private _store;
    private _activeSheetName;
    constructor(config: IActionConfig);
    do(): void;
    undo(): void;
}
