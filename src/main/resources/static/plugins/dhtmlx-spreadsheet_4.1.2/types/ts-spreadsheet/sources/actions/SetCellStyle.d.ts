import { IAction, IActionConfig } from "./../types";
export declare class SetCellStyle implements IAction {
    config: IActionConfig;
    private _page;
    constructor(config: IActionConfig);
    do(): void;
    undo(): void;
}
