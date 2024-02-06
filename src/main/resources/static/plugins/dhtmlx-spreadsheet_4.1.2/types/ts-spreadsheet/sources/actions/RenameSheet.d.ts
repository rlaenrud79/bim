import { DataPage } from "../../../muon/dist/types/data/page";
import { IAction, IActionConfig } from "../types";
export declare class RenameSheet implements IAction {
    config: IActionConfig;
    page: DataPage;
    constructor(config: IActionConfig);
    do(): void;
    undo(): void;
}
