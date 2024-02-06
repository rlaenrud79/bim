var dataset = [
    {cell: "A1", value: "Formula name", css: "header"},
    {cell: "B1", value: "Formula example", css: "header"},
    {cell: "C1", value: "Data for formula", css: "header"},
    {cell: "D1", value: "Data for formula", css: "header"},

    {cell: "A2", value: "ABS", css:"header"},
    {cell: "B2", value: "=ABS(C2)"},
    {cell: "C2", value: "-5"},

    {cell: "A3", value: "AVERAGE", css:"header"},
    {cell: "B3", value: `=AVERAGE(C3;D3)`},
    {cell: "C3", value: "5"},
    {cell: "D3", value: "15"},

    {cell: "A4", value: "COUNT", css:"header"},
    {cell: "B4", value: `=COUNT(C4;D4)`},
    {cell: "C4", value: "5"},
    {cell: "D4", value: "some text"},
    
    {cell: "A5", value: "COUNTA", css:"header"},
    {cell: "B5", value: `=COUNTA(C5;D5)`},
    {cell: "C5", value: "5"},
    {cell: "D5", value: "some text"},
    
    {cell: "A6", value: "COUNTBLANK", css:"header"},
    {cell: "B6", value: `=COUNTBLANK(C6;D6)`},
    {cell: "C6", value: "0"},
    {cell: "D6", value: ""},
    
    {cell: "A7", value: "EVEN", css:"header"},
    {cell: "B7", value: `=EVEN(C7)`},
    {cell: "C7", value: "5.7"},
    
    {cell: "A8", value: "INT", css:"header"},
    {cell: "B8", value: `=INT(C8)`},
    {cell: "C8", value: "5.7"},
   
    {cell: "A9", value: "MAX", css:"header"},
    {cell: "B9", value: `=MAX(C9;D9)`},
    {cell: "C9", value: "5"},
    {cell: "D9", value: "15"},
   
    {cell: "A10", value: "MIN", css:"header"},
    {cell: "B10", value: `=MIN(C10;D10)`},
    {cell: "C10", value: "5"},
    {cell: "D10", value: "15"},

    {cell: "A11", value: "ODD", css:"header"},
    {cell: "B11", value: `=ODD(C11)`},
    {cell: "C11", value: "5"},
    
    {cell: "A12", value: "PI", css:"header"},
    {cell: "B12", value: `=PI()`},
    
    {cell: "A13", value: "POWER", css:"header"},
    {cell: "B13", value: `=POWER(C13;D13)`},
    {cell: "C13", value: "5"},
    {cell: "D13", value: "2"},

    {cell: "A14", value: "PRODUCT", css:"header"},
    {cell: "B14", value: `=PRODUCT(C14;D14)`},
    {cell: "C14", value: "5"},
    {cell: "D14", value: "2"},
    
    {cell: "A15", value: "QUOTIENT", css:"header"},
    {cell: "B15", value: `=QUOTIENT(C15;D15)`},
    {cell: "C15", value: "5"},
    {cell: "D15", value: "2"},

    {cell: "A16", value: "RAND", css:"header"},
    {cell: "B16", value: `=RAND()`},
   
   
    {cell: "A17", value: "ROUND", css:"header"},
    {cell: "B17", value: `=ROUND(C17;D17)`},
    {cell: "C17", value: "5.73758"},
    {cell: "D17", value: "2"},
    
    {cell: "A18", value: "ROUNDDOWN", css:"header"},
    {cell: "B18", value: `=ROUNDDOWN(C18;D18)`},
    {cell: "C18", value: "5.73758"},
    {cell: "D18", value: "2"},
    
    {cell: "A19", value: "ROUNDUP", css:"header"},
    {cell: "B19", value: `=ROUNDUP(C19;D19)`},
    {cell: "C19", value: "5.73158"},
    {cell: "D19", value: "2"},
    
    {cell: "A20", value: "SQRT", css:"header"},
    {cell: "B20", value: `=SQRT(C20)`},
    {cell: "C20", value: "5"},
    
    {cell: "A21", value: "STDEVP", css:"header"},
    {cell: "B21", value: `=STDEVP(C21;D21)`},
    {cell: "C21", value: "5"},
    {cell: "D21", value: "2"},
    
    {cell: "A22", value: "SUM", css:"header"},
    {cell: "B22", value: `=SUM(C22;D22)`},
    {cell: "C22", value: "5"},
    {cell: "D22", value: "2"},
    
    {cell: "A23", value: "SUMPRODUCT", css:"header"},
    {cell: "B23", value: `=SUMPRODUCT(C21:C23;D21:D23)`},
    {cell: "C23", value: "5"},
    {cell: "D23", value: "2"},
    
    {cell: "A24", value: "SUMSQ", css:"header"},
    {cell: "B24", value: `=SUMSQ(C24;D24)`},
    {cell: "C24", value: "5"},
    {cell: "D24", value: "2"},
    
    {cell: "A25", value: "TRUNC", css:"header"},
    {cell: "B25", value: `=TRUNC(C25)`},
    {cell: "C25", value: "5.64"},

    {cell: "A26", value: "VARP", css:"header"},
    {cell: "B26", value: `=VARP(C26;D26)`},
    {cell: "C26", value: "5"},
    {cell: "D26", value: "2"},

    {cell: "A27", value: "CONCATENATE", css:"header"},
    {cell: "B27", value: `=CONCATENATE(C27;D27)`},
    {cell: "C27", value: "some"},
    {cell: "D27", value: "text"},

    {cell: "A28", value: "LEFT", css:"header"},
    {cell: "B28", value: `=LEFT(C28; 2)`},
    {cell: "C28", value: "some"},

    {cell: "A29", value: "LEN", css:"header"},
    {cell: "B29", value: `=LEN(C29)`},
    {cell: "C29", value: "some"},

    {cell: "A30", value: "LOWER", css:"header"},
    {cell: "B30", value: `=LOWER(C30)`},
    {cell: "C30", value: "SOME"},

    {cell: "A31", value: "MID", css:"header"},
    {cell: "B31", value: `=MID(C31; 2; 2)`},
    {cell: "C31", value: "some"},

    {cell: "A32", value: "PROPER", css:"header"},
    {cell: "B32", value: `=PROPER(C32)`},
    {cell: "C32", value: "some"},

    {cell: "A33", value: "RIGHT", css:"header"},
    {cell: "B33", value: `=RIGHT(C33; 2)`},
    {cell: "C33", value: "some"},

    {cell: "A34", value: "TRIM", css:"header"},
    {cell: "B34", value: `=TRIM(C34)`},
    {cell: "C34", value: "    some"},

    {cell: "A35", value: "UPPER", css:"header"},
    {cell: "B35", value: `=UPPER(C35)`},
    {cell: "C35", value: "some"},
];

var formulas = {
	data: dataset,
	styles: {
		header: {
            fontWeight: "bold",
            background: "rgb(3 155 229)",
            color: "rgb(255 255 255)",
		},
	},
};
