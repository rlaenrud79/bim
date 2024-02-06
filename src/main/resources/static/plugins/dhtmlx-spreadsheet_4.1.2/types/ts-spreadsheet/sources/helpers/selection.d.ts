import { Store } from "../../../muon";
export declare function getCellsDiff(firstCell: string, lastCell: string): {
    x: number;
    y: number;
    isLargerByX: boolean;
};
export declare function getLastCopyingCell(firstCell: string, lastCell: string): string;
export declare function getProgressionStep(progression: any[]): number;
export declare function getAutoFilledCells(values: any[], focused: string, selected: string, math: Store): {
    cells: string;
    value: any[];
};
