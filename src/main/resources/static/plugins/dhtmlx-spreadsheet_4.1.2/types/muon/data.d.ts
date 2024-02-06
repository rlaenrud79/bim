import { IValue, cellValue } from "./types";
import { Store } from "./store";
declare type Page = {
    page: DataPage;
    store: Store;
};
export declare class DataPage {
    data: IValue[][];
    store: Store;
    constructor();
    setCellAt(r: number, c: number, value: IValue): void;
    setValueAt(r: number, c: number, value: cellValue): void;
    getCellAt(r: number, c: number, force?: boolean): IValue;
    getValueAt(r: number, c: number, formula?: boolean): cellValue;
}
export declare class DataStore {
    private _pages;
    constructor();
    addPage(name: string): Page;
    getPage(name: string): Page;
}
export {};
