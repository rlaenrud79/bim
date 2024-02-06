import { ISpreadsheet } from "../types";
export declare class XlsxProxy {
    private _xlsxWorker;
    private _spreadsheet;
    constructor(spreadsheet: ISpreadsheet);
    load(url?: string): Promise<any>;
    private _getXlsxWorker;
    private _getFile;
}
