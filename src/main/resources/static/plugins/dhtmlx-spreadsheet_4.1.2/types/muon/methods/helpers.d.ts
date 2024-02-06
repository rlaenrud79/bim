import { mathArgument, maybeNumber, cellValue } from "../types";
export declare function dayOfTheYear(date: Date): number;
export declare function getJsDateFromExcel(excelDate: number): Date;
export declare function getExcelDateFromJs(jsDate: Date): number;
export declare function _to_number(v: string | number): maybeNumber;
export declare function _combine(data: mathArgument[]): cellValue[];
export declare function _isEmpty(text: cellValue): boolean;
