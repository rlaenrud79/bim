import { IAction, IActionConfig } from "./../types";
export declare class AddColumn implements IAction {
    config: IActionConfig;
    private _index;
    private _store;
    activeSheetName: string;
    constructor(config: IActionConfig);
    do(): void;
    undo(): void;
}
