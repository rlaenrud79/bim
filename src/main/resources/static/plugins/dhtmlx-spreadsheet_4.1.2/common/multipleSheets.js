var dataset = {
	styles: {
        red_text: { color: "#E94633" },
		center_bold: {
			"font-weight": "bold",
			"justify-content": "center",
			"text-align": "center",
		},
        bold: {
            "font-weight": "bold",
        },
		text_end: {
			"justify-content": "flex-end",
			"text-align": "right",
		},
        blue_background: {
            background: "#B3E5FC"
        }
	},
	sheets: [
		{
			name: "Report",
			data: [
				{
					cell: "B1",
					css: "text_end bold",
					format: "General",
					value: "Average",
				},
				{
					cell: "C1",
					css: "text_end bold",
					format: "General",
					value: "Product's Share",
				},
				{
					cell: "D1",
					css: "text_end bold",
					format: "General",
					value: "Min",
				},
				{
					cell: "E1",
					css: "text_end bold",
					format: "General",
					value: "Max",
				},
				{
					cell: "F1",
					css: "text_end bold",
					format: "General",
					value: "NET",
				},
				{
                    cell: "A2",
                    css: "bold",
					format: "General",
					value: "Drone",
				},
				{
                    cell: "B2",
                    css: "text_end",
					format: "currency",
					value: "=AVERAGE(Income!B2:M2)",
				},
				{
					cell: "C2",
					format: "percent",
                    css: "text_end",
					value: "=Income!B2/Income!B12",
				},
				{
					cell: "D2",
					format: "currency",
                    css: "text_end",
					value: "=MIN(Income!B2:M2)",
				},
				{
					cell: "E2",
					format: "currency",
                    css: "text_end",
					value: "=MAX(Income!B2:M2)",
				},
				{
					cell: "F2",
					format: "currency",
                    css: "text_end",
					value: "=Income!N2-Expenses!E2",
				},
				{
					cell: "A3",
					format: "General",
                    css: "bold",
					value: "VR Glasses",
				},
				{
					cell: "B3",
					format: "currency",
                    css: "text_end",
					value: "=AVERAGE(Income!B3:M3)",
				},
				{
					cell: "C3",
					format: "percent",
                    css: "text_end",
					value: "=Income!B3/Income!B12",
				},
				{
					cell: "D3",
					format: "currency",
                    css: "text_end",
					value: "=MIN(Income!B3:M3)",
				},
				{
					cell: "E3",
					format: "currency",
                    css: "text_end",
					value: "=MAX(Income!B3:M3)",
				},
				{
					cell: "F3",
					format: "currency",
                    css: "text_end",
					value: "=Income!N3-Expenses!E3",
				},
				{
					cell: "A4",
					format: "General",
                    css: "bold",
					value: "Voice Remote Control",
				},
				{
					cell: "B4",
					format: "currency",
                    css: "text_end",
					value: "=AVERAGE(Income!B4:M4)",
				},
				{
					cell: "C4",
					format: "percent",
                    css: "text_end",
					value: "=Income!B4/Income!B12",
				},
				{
					cell: "D4",
					format: "currency",
                    css: "text_end",
					value: "=MIN(Income!B4:M4)",
				},
				{
					cell: "E4",
					format: "currency",
                    css: "text_end",
					value: "=MAX(Income!B4:M4)",
				},
				{
					cell: "F4",
					format: "currency",
                    css: "text_end",
					value: "=Income!N4-Expenses!E4",
				},
				{
					cell: "A5",
					format: "General",
                    css: "bold",
					value: "Touch Projector",
				},
				{
					cell: "B5",
					format: "currency",
                    css: "text_end",
					value: "=AVERAGE(Income!B5:M5)",
				},
				{
					cell: "C5",
					format: "percent",
                    css: "text_end",
					value: "=Income!B5/Income!B12",
				},
				{
					cell: "D5",
					format: "currency",
                    css: "text_end",
					value: "=MIN(Income!B5:M5)",
				},
				{
					cell: "E5",
					format: "currency",
                    css: "text_end",
                    value: "=MAX(Income!B5:M5)",
				},
				{
					cell: "F5",
					format: "currency",
                    css: "red_text text_end",
					value: "=Income!N5-Expenses!E5",
				},
				{
					cell: "A6",
					format: "General",
                    css: "bold",
					value: "Smart Band",
				},
				{
					cell: "B6",
					format: "currency",
                    css: "text_end",
					value: "=AVERAGE(Income!B6:M6)",
				},
				{
					cell: "C6",
					format: "percent",
                    css: "text_end",
					value: "=Income!B6/Income!B12",
				},
				{
					cell: "D6",
					format: "currency",
                    css: "text_end",
					value: "=MIN(Income!B6:M6)",
				},
				{
					cell: "E6",
					format: "currency",
                    css: "text_end",
					value: "=MAX(Income!B6:M6)",
				},
				{
					cell: "F6",
					format: "currency",
                    css: "text_end",
					value: "=Income!N6-Expenses!E6",
				},
				{
					cell: "A7",
					format: "General",
                    css: "bold",
					value: "Video Doorbell",
				},
				{
					cell: "B7",
					format: "currency",
                    css: "text_end",
					value: "=AVERAGE(Income!B7:M7)",
				},
				{
					cell: "C7",
					format: "percent",
                    css: "text_end",
					value: "=Income!B7/Income!B12",
				},
				{
					cell: "D7",
					format: "currency",
                    css: "text_end",
					value: "=MIN(Income!B7:M7)",
				},
				{
					cell: "E7",
					format: "currency",
                    css: "text_end",
					value: "=MAX(Income!B7:M7)",
				},
				{
					cell: "F7",
					format: "currency",
                    css: "text_end",
					value: "=Income!N7-Expenses!E7",
				},
				{
					cell: "A8",
					format: "General",
                    css: "bold",
					value: "Smart TV",
				},
				{
					cell: "B8",
                    css: "text_end",
					format: "currency",
					value: "=AVERAGE(Income!B8:M8)",
				},
				{
					cell: "C8",
					format: "percent",
                    css: "text_end",
					value: "=Income!B8/Income!B12",
				},
				{
					cell: "D8",
					format: "currency",
                    css: "text_end",
					value: "=MIN(Income!B8:M8)",
				},
				{
					cell: "E8",
					format: "currency",
                    css: "text_end",
					value: "=MAX(Income!B8:M8)",
				},
				{
					cell: "F8",
					format: "currency",
                    css: "text_end",
					value: "=Income!N8-Expenses!E8",
				},
				{
					cell: "A9",
					format: "General",
                    css: "bold",
					value: "Robot Vacuum",
				},
				{
					cell: "B9",
					format: "currency",
                    css: "text_end",
					value: "=AVERAGE(Income!B9:M9)",
				},
				{
					cell: "C9",
					format: "percent",
                    css: "text_end",
					value: "=Income!B9/Income!B12",
				},
				{
					cell: "D9",
					format: "currency",
                    css: "text_end",
					value: "=MIN(Income!B9:M9)",
				},
				{
					cell: "E9",
					format: "currency",
                    css: "text_end",
					value: "=MAX(Income!B9:M9)",
				},
				{
					cell: "F9",
					format: "currency",
                    css: "text_end",
					value: "=Income!N9-Expenses!E9",
				},
				{
					cell: "A10",
					format: "General",
                    css: "bold",
					value: "Air Purifier",
				},
				{
					cell: "B10",
					format: "currency",
                    css: "text_end",
					value: "=AVERAGE(Income!B10:M10)",
				},
				{
					cell: "C10",
					format: "percent",
                    css: "text_end",
					value: "=Income!B10/Income!B12",
				},
				{
					cell: "D10",
					format: "currency",
                    css: "text_end",
					value: "=MIN(Income!B10:M10)",
				},
				{
					cell: "E10",
					format: "currency",
                    css: "text_end",
					value: "=MAX(Income!B10:M10)",
				},
				{
					cell: "F10",
					format: "currency",
                    css: "text_end",
					value: "=Income!N10-Expenses!E10",
				},
				{
					cell: "A11",
					format: "General",
                    css: "bold",
					value: "Baby Monitor",
				},
				{
					cell: "B11",
					format: "currency",
                    css: "text_end",
					value: "=AVERAGE(Income!B11:M11)",
				},
				{
					cell: "C11",
					format: "percent",
                    css: "text_end",
					value: "=Income!B11/Income!B12",
				},
				{
					cell: "D11",
					format: "currency",
                    css: "text_end",
					value: "=MIN(Income!B11:M11)",
				},
				{
					cell: "E11",
					format: "currency",
                    css: "text_end",
					value: "=MAX(Income!B11:M11)",
				},
				{
					cell: "F11",
					format: "currency",
                    css: "text_end",
					value: "=Income!N11-Expenses!E11",
				},
				{
					cell: "A12",
					format: "General",
					value: "Total" , 
                    css: "bold blue_background",
				},
				{
					cell: "B12",
                    css: "blue blue_background",
				},
				{
					cell: "C12",
                    css: "bold blue_background",
				},
				{
					cell: "D12",
                    css: "bold blue_background",
				},
				{
					cell: "E12",
                    css: "bold blue_background",
				},
				{
					cell: "F12",
					css: "green_text",
					format: "currency",
                    css: "text_end bold blue_background",
					value: "=Income!N12-Expenses!E12",
				},
			],
		},
		{
			name: "Income",
			data: [
				{
					cell: "B1",
					css: "text_end bold",
					format: "General",
					value: "Jan",
				},
				{
					cell: "C1",
					css: "text_end bold",
					format: "General",
					value: "Feb",
				},
				{
					cell: "D1",
					css: "text_end bold",
					format: "General",
					value: "Mar",
				},
				{
					cell: "E1",
					css: "text_end bold",
					format: "General",
					value: "Apr",
				},
				{
					cell: "F1",
					css: "text_end bold",
					format: "General",
					value: "May",
				},
				{
					cell: "G1",
					css: "text_end bold",
					format: "General",
					value: "Jun",
				},
				{
					cell: "H1",
					css: "text_end bold",
					format: "General",
					value: "Jul",
				},
				{
					cell: "I1",
					css: "text_end bold",
					format: "General",
					value: "Aug",
				},
				{
					cell: "J1",
					css: "text_end bold",
					format: "General",
					value: "Sep",
				},
				{
					cell: "K1",
					css: "text_end bold",
					format: "General",
					value: "Oct",
				},

				{
					cell: "L1",
					css: "text_end bold",
					format: "General",
					value: "Nov",
				},

				{
					cell: "M1",
					css: "text_end bold",
					format: "General",
					value: "Dec",
				},

				{
					cell: "N1",
					css: "text_end bold",
					format: "General",
					value: "Total" , 
				},

				{
					cell: "A2",
					format: "General",
                    css: "bold",
					value: "Drone",
				},

				{
					cell: "B2",
					css: "text_end",
					format: "currency",
					value: 500000,
				},

				{
					cell: "C2",
					css: "text_end",
					format: "currency",
					value: 200000,
				},

				{
					cell: "D2",
					css: "text_end",
					format: "currency",
					value: 700000,
				},

				{
					cell: "E2",
					css: "text_end",
					format: "currency",
					value: 750000,
				},

				{
					cell: "F2",
					css: "text_end",
					format: "currency",
					value: 600000,
				},

				{
					cell: "G2",
					css: "text_end",
					format: "currency",
					value: 800000,
				},

				{
					cell: "H2",
					css: "text_end",
					format: "currency",
					value: 500000,
				},

				{
					cell: "I2",
					css: "text_end",
					format: "currency",
					value: 100000,
				},

				{
					cell: "J2",
					css: "text_end",
					format: "currency",
					value: 750000,
				},

				{
					cell: "K2",
					css: "text_end",
					format: "currency",
					value: 900000,
				},

				{
					cell: "L2",
					css: "text_end",
					format: "currency",
					value: 200000,
				},

				{
					cell: "M2",
					css: "text_end",
					format: "currency",
					value: 950000,
				},

				{
					cell: "N2",
					css: "text_end",
					format: "currency",
					value: "=SUM(B2:M2)",
				},

				{
					cell: "A3",
                    css: "bold",
					format: "General",
					value: "VR Glasses",
				},

				{
					cell: "B3",
					css: "text_end",
					format: "currency",
					value: 80000,
				},

				{
					cell: "C3",
					css: "text_end",
					format: "currency",
					value: 75000,
				},

				{
					cell: "D3",
					css: "text_end",
					format: "currency",
					value: 90000,
				},

				{
					cell: "E3",
					css: "text_end",
					format: "currency",
					value: 100000,
				},

				{
					cell: "F3",
					css: "text_end",
					format: "currency",
					value: 95000,
				},

				{
					cell: "G3",
					css: "text_end",
					format: "currency",
					value: 105000,
				},

				{
					cell: "H3",
					css: "text_end",
					format: "currency",
					value: 102500,
				},

				{
					cell: "I3",
					css: "text_end",
					format: "currency",
					value: 75000,
				},

				{
					cell: "J3",
					css: "text_end",
					format: "currency",
					value: 85000,
				},

				{
					cell: "K3",
					css: "text_end",
					format: "currency",
					value: 90000,
				},

				{
					cell: "L3",
					css: "text_end",
					format: "currency",
					value: 120000,
				},

				{
					cell: "M3",
					css: "text_end",
					format: "currency",
					value: 112500,
				},

				{
					cell: "N3",
					css: "text_end",
					format: "currency",
					value: "=SUM(B3:M3)",
				},

				{
					cell: "A4",
                    css: "bold",
					format: "General",
					value: "Voice Remote Control",
				},

				{
					cell: "B4",
					css: "text_end",
					format: "currency",
					value: 20000,
				},

				{
					cell: "C4",
					css: "text_end",
					format: "currency",
					value: 25000,
				},

				{
					cell: "D4",
					css: "text_end",
					format: "currency",
					value: 26500,
				},

				{
					cell: "E4",
					css: "text_end",
					format: "currency",
					value: 25750,
				},

				{
					cell: "F4",
					css: "text_end",
					format: "currency",
					value: 23500,
				},

				{
					cell: "G4",
					css: "text_end",
					format: "currency",
					value: 21000,
				},

				{
					cell: "H4",
					css: "text_end",
					format: "currency",
					value: 21500,
				},

				{
					cell: "I4",
					css: "text_end",
					format: "currency",
					value: 19800,
				},

				{
					cell: "J4",
					css: "text_end",
					format: "currency",
					value: 21500,
				},

				{
					cell: "K4",
					css: "text_end",
					format: "currency",
					value: 25000,
				},

				{
					cell: "L4",
					css: "text_end",
					format: "currency",
					value: 27500,
				},

				{
					cell: "M4",
					css: "text_end",
					format: "currency",
					value: 26000,
				},

				{
					cell: "N4",
					css: "text_end",
					format: "currency",
					value: "=SUM(B4:M4)",
				},

				{
					cell: "A5",
                    css: "bold",
					format: "General",
					value: "Touch Projector",
				},

				{
					cell: "B5",
					css: "text_end",
					format: "currency",
					value: "=7000",
				},

				{
					cell: "C5",
					css: "text_end",
					format: "currency",
					value: 9250,
				},

				{
					cell: "D5",
					css: "text_end",
					format: "currency",
					value: 8000,
				},

				{
					cell: "E5",
					css: "text_end",
					format: "currency",
					value: 9000,
				},

				{
					cell: "F5",
					css: "text_end",
					format: "currency",
					value: 10000,
				},

				{
					cell: "G5",
					css: "text_end",
					format: "currency",
					value: 8500,
				},

				{
					cell: "H5",
					css: "text_end",
					format: "currency",
					value: 9000,
				},

				{
					cell: "I5",
					css: "text_end",
					format: "currency",
					value: 7200,
				},

				{
					cell: "J5",
					css: "text_end",
					format: "currency",
					value: 9500,
				},

				{
					cell: "K5",
					css: "text_end",
					format: "currency",
					value: 10000,
				},

				{
					cell: "L5",
					css: "text_end",
					format: "currency",
					value: 11000,
				},

				{
					cell: "M5",
					css: "text_end",
					format: "currency",
					value: 9000,
				},

				{
					cell: "N5",
					css: "text_end",
					format: "currency",
					value: "=SUM(B5:M5)",
				},

				{
					cell: "A6",
                    css: "bold",
					format: "General",
					value: "Smart Band",
				},

				{
					cell: "B6",
					css: "text_end",
					format: "currency",
					value: 50000,
				},

				{
					cell: "C6",
					css: "text_end",
					format: "currency",
					value: 52000,
				},

				{
					cell: "D6",
					css: "text_end",
					format: "currency",
					value: 53000,
				},

				{
					cell: "E6",
					css: "text_end",
					format: "currency",
					value: 53500,
				},

				{
					cell: "F6",
					css: "text_end",
					format: "currency",
					value: 52500,
				},

				{
					cell: "G6",
					css: "text_end",
					format: "currency",
					value: 51000,
				},

				{
					cell: "H6",
					css: "text_end",
					format: "currency",
					value: 50000,
				},

				{
					cell: "I6",
					css: "text_end",
					format: "currency",
					value: 49000,
				},

				{
					cell: "J6",
					css: "text_end",
					format: "currency",
					value: 51000,
				},

				{
					cell: "K6",
					css: "text_end",
					format: "currency",
					value: 52500,
				},

				{
					cell: "L6",
					css: "text_end",
					format: "currency",
					value: 56000,
				},

				{
					cell: "M6",
					css: "text_end",
					format: "currency",
					value: 53000,
				},

				{
					cell: "N6",
					css: "text_end",
					format: "currency",
					value: "=SUM(B6:M6)",
				},

				{
					cell: "A7",
                    css: "bold",
					format: "General",
					value: "Video Doorbell",
				},

				{
					cell: "B7",
					css: "text_end",
					format: "currency",
					value: 18000,
				},

				{
					cell: "C7",
					css: "text_end",
					format: "currency",
					value: 19500,
				},

				{
					cell: "D7",
					css: "text_end",
					format: "currency",
					value: 20500,
				},

				{
					cell: "E7",
					css: "text_end",
					format: "currency",
					value: 20250,
				},

				{
					cell: "F7",
					css: "text_end",
					format: "currency",
					value: 20500,
				},

				{
					cell: "G7",
					css: "text_end",
					format: "currency",
					value: 19000,
				},

				{
					cell: "H7",
					css: "text_end",
					format: "currency",
					value: 18000,
				},

				{
					cell: "I7",
					css: "text_end",
					format: "currency",
					value: 16500,
				},

				{
					cell: "J7",
					css: "text_end",
					format: "currency",
					value: 17000,
				},

				{
					cell: "K7",
					css: "text_end",
					format: "currency",
					value: 18500,
				},

				{
					cell: "L7",
					css: "text_end",
					format: "currency",
					value: 19000,
				},

				{
					cell: "M7",
					css: "text_end",
					format: "currency",
					value: 18500,
				},

				{
					cell: "N7",
					css: "text_end",
					format: "currency",
					value: "=SUM(B7:M7)",
				},

				{
					cell: "A8",
                    css: "bold",
					format: "General",
					value: "Smart TV",
				},

				{
					cell: "B8",
					css: "text_end",
					format: "currency",
					value: 310000,
				},

				{
					cell: "C8",
					css: "text_end",
					format: "currency",
					value: 295000,
				},
				{
					cell: "D8",
					css: "text_end",
					format: "currency",
					value: 315000,
				},
				{
					cell: "E8",
					css: "text_end",
					format: "currency",
					value: 320000,
				},
				{
					cell: "F8",
					css: "text_end",
					format: "currency",
					value: 295000,
				},
				{
					cell: "G8",
					css: "text_end",
					format: "currency",
					value: 280000,
				},
				{
					cell: "H8",
					css: "text_end",
					format: "currency",
					value: 290000,
				},
				{
					cell: "I8",
					css: "text_end",
					format: "currency",
					value: 285000,
				},
				{
					cell: "J8",
					css: "text_end",
					format: "currency",
					value: 300000,
				},
				{
					cell: "K8",
					css: "text_end",
					format: "currency",
					value: 315000,
				},
				{
					cell: "L8",
					css: "text_end",
					format: "currency",
					value: 340000,
				},
				{
					cell: "M8",
					css: "text_end",
					format: "currency",
					value: 335000,
				},
				{
					cell: "N8",
					css: "text_end",
					format: "currency",
					value: "=SUM(B8:M8)",
				},
				{
					cell: "A9",
                    css: "bold",
					format: "General",
					value: "Robot Vacuum",
				},
				{
					cell: "B9",
					css: "text_end",
					format: "currency",
					value: 50000,
				},
				{
					cell: "C9",
					css: "text_end",
					format: "currency",
					value: 55000,
				},
				{
					cell: "D9",
					css: "text_end",
					format: "currency",
					value: 57500,
				},
				{
					cell: "E9",
					css: "text_end",
					format: "currency",
					value: 60000,
				},
				{
					cell: "F9",
					css: "text_end",
					format: "currency",
					value: 62500,
				},
				{
					cell: "G9",
					css: "text_end",
					format: "currency",
					value: 57500,
				},
				{
					cell: "H9",
					css: "text_end",
					format: "currency",
					value: 55000,
				},
				{
					cell: "I9",
					css: "text_end",
					format: "currency",
					value: 50000,
				},
				{
					cell: "J9",
					css: "text_end",
					format: "currency",
					value: 57500,
				},
				{
					cell: "K9",
					css: "text_end",
					format: "currency",
					value: 60000,
				},
				{
					cell: "L9",
					css: "text_end",
					format: "currency",
					value: 62500,
				},
				{
					cell: "M9",
					css: "text_end",
					format: "currency",
					value: 65000,
				},
				{
					cell: "N9",
					css: "text_end",
					format: "currency",
					value: "=SUM(B9:M9)",
				},
				{
					cell: "A10",
                    css: "bold",
					format: "General",
					value: "Air Purifier",
				},
				{
					cell: "B10",
					css: "text_end",
					format: "currency",
					value: 15000,
				},
				{
					cell: "C10",
					css: "text_end",
					format: "currency",
					value: 20000,
				},
				{
					cell: "D10",
					css: "text_end",
					format: "currency",
					value: 25000,
				},
				{
					cell: "E10",
					css: "text_end",
					format: "currency",
					value: 30000,
				},
				{
					cell: "F10",
					css: "text_end",
					format: "currency",
					value: 25000,
				},
				{
					cell: "G10",
					css: "text_end",
					format: "currency",
					value: 27500,
				},
				{
					cell: "H10",
					css: "text_end",
					format: "currency",
					value: 25000,
				},
				{
					cell: "I10",
					css: "text_end",
					format: "currency",
					value: 20000,
				},
				{
					cell: "J10",
					css: "text_end",
					format: "currency",
					value: 25000,
				},
				{
					cell: "K10",
					css: "text_end",
					format: "currency",
					value: 30000,
				},
				{
					cell: "L10",
					css: "text_end",
					format: "currency",
					value: 27500,
				},
				{
					cell: "M10",
					css: "text_end",
					format: "currency",
					value: 30000,
				},
				{
					cell: "N10",
					css: "text_end",
					format: "currency",
					value: "=SUM(B10:M10)",
				},
				{
					cell: "A11",
                    css: "bold",
					format: "General",
					value: "Baby Monitor",
				},
				{
					cell: "B11",
					css: "text_end",
					format: "currency",
					value: 25000,
				},
				{
					cell: "C11",
					css: "text_end",
					format: "currency",
					value: 27000,
				},
				{
					cell: "D11",
					css: "text_end",
					format: "currency",
					value: 27500,
				},
				{
					cell: "E11",
					css: "text_end",
					format: "currency",
					value: 26500,
				},
				{
					cell: "F11",
					css: "text_end",
					format: "currency",
					value: 28000,
				},
				{
					cell: "G11",
					css: "text_end",
					format: "currency",
					value: 29000,
				},
				{
					cell: "H11",
					css: "text_end",
					format: "currency",
					value: 29500,
				},
				{
					cell: "I11",
					css: "text_end",
					format: "currency",
					value: 25000,
				},
				{
					cell: "J11",
					css: "text_end",
					format: "currency",
					value: 26000,
				},
				{
					cell: "K11",
					css: "text_end",
					format: "currency",
					value: 27000,
				},
				{
					cell: "L11",
					css: "text_end",
					format: "currency",
					value: 27500,
				},
				{
					cell: "M11",
					css: "text_end",
					format: "currency",
					value: 25000,
				},
				{
					cell: "N11",
					css: "text_end",
					format: "currency",
					value: "=SUM(B11:M11)",
				},
				{
					cell: "A12",
					format: "General",
					value: "Total" , 
                    css: "bold",
				},
				{
					cell: "B12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(B2:B11)",
				},
				{
					cell: "C12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(C2:C11)",
				},
				{
					cell: "D12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(D2:D11)",
				},
				{
					cell: "E12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(E2:E11)",
				},
				{
					cell: "F12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(F2:F11)",
				},
				{
					cell: "G12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(G2:G11)",
				},
				{
					cell: "H12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(H2:H11)",
				},
				{
					cell: "I12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(I2:I11)",
				},
				{
					cell: "J12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(J2:J11)",
				},
				{
					cell: "K12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(K2:K11)",
				},
				{
					cell: "L12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(L2:L11)",
				},
				{
					cell: "M12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(M2:M11)",
				},
				{
					cell: "N12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(N2:N11)",
					length: 167,
				},
			],
		},
		{
			name: "Expenses",
			data: [
				{
					cell: "B1",
					css: "text_end bold",
					format: "General",
					value: "PPC Campaigns",
				},
				{
					cell: "C1",
					css: "text_end bold",
					format: "General",
					value: "Sponsored Articles",
				},
				{ cell: "D1", css: "text_end bold", format: "General", value: "SMM" },
				{ cell: "E1", format: "General", value: "Total" , css: "bold text_end" },
				{
					cell: "A2",
                    css: "bold",
					format: "General",
					value: "Drone",
				},
				{ cell: "B2", css: "text_end", format: "currency", value: 150000 },
				{ cell: "C2", css: "text_end", format: "currency", value: 25000 },
				{ cell: "D2", css: "text_end", format: "currency", value: 150000 },
				{
					cell: "E2",
					css: "text_end",
					format: "currency",
					value: "=SUM(B2:D2)",
				},
				{
					cell: "A3",
                    css: "bold",
					format: "General",
					value: "VR Glasses",
				},

				{
					cell: "B3",
					css: "text_end",
					format: "currency",
					value: 40000,
				},
				{
					cell: "C3",
					css: "text_end",
					format: "currency",
					value: 0,
				},
				{
					cell: "D3",
					css: "text_end",
					format: "currency",
					value: 15000,
				},
				{
					cell: "E3",
					css: "text_end",
					format: "currency",
					value: "=SUM(B3:D3)",
				},
				{
					cell: "A4",
					format: "General",
                    css: "bold",
					value: "Voice Remote, Control",
				},
				{
					cell: "B4",
					css: "text_end",
					format: "currency",
					value: 25000,
				},
				{
					cell: "C4",
					css: "text_end",
					format: "currency",
					value: 5000,
				},
				{
					cell: "D4",
					css: "text_end",
					format: "currency",
					value: 5000,
				},
				{
					cell: "E4",
					css: "text_end",
					format: "currency",
					value: "=SUM(B4:D4)",
				},
				{
					cell: "A5",
					format: "General",
                    css: "bold",
					value: "Touch Projector",
				},
				{
					cell: "B5",
					css: "text_end",
					format: "currency",
					value: 105500,
				},
				{
					cell: "C5",
					css: "text_end",
					format: "currency",
					value: 3500,
				},
				{
					cell: "D5",
					css: "text_end",
					format: "currency",
					value: 3000,
				},
				{
					cell: "E5",
					css: "text_end",
					format: "currency",
					value: "=SUM(B5:D5)",
				},
				{
					cell: "A6",
					format: "General",
                    css: "bold",
					value: "Smart Band",
				},
				{
					cell: "B6",
					css: "text_end",
					format: "currency",
					value: 50000,
				},
				{
					cell: "C6",
					css: "text_end",
					format: "currency",
					value: 0,
				},
				{
					cell: "D6",
					css: "text_end",
					format: "currency",
					value: 5000,
				},
				{
					cell: "E6",
					css: "text_end",
					format: "currency",
					value: "=SUM(B6:D6)",
				},
				{
					cell: "A7",
					format: "General",
                    css: "bold",
					value: "Video Doorbell",
				},
				{
					cell: "B7",
					css: "text_end",
					format: "currency",
					value: 5000,
				},
				{
					cell: "C7",
					css: "text_end",
					format: "currency",
					value: 5000,
				},
				{
					cell: "D7",
					css: "text_end",
					format: "currency",
					value: 500,
				},
				{
					cell: "E7",
					css: "text_end",
					format: "currency",
					value: "=SUM(B7:D7)",
				},
				{
					cell: "A8",
					format: "General",
                    css: "bold",
					value: "Smart TV",
				},
				{
					cell: "B8",
					css: "text_end",
					format: "currency",
					value: 50000,
				},
				{
					cell: "C8",
					css: "text_end",
					format: "currency",
					value: 30000,
				},
				{
					cell: "D8",
					css: "text_end",
					format: "currency",
					value: 10000,
				},
				{
					cell: "E8",
					css: "text_end",
					format: "currency",
					value: "=SUM(B8:D8)",
				},
				{
					cell: "A9",
					format: "General",
                    css: "bold",
					value: "Robot Vacuum",
				},
				{
					cell: "B9",
					css: "text_end",
					format: "currency",
					value: 50000,
				},
				{
					cell: "C9",
					css: "text_end",
					format: "currency",
					value: 0,
				},
				{
					cell: "D9",
					css: "text_end",
					format: "currency",
					value: 5000,
				},
				{
					cell: "E9",
					css: "text_end",
					format: "currency",
					value: "=SUM(B9:D9)",
				},
				{
					cell: "A10",
					format: "General",
                    css: "bold",
					value: "Air Purifier",
				},
				{
					cell: "B10",
					css: "text_end",
					format: "currency",
					value: 30000,
				},
				{
					cell: "C10",
					css: "text_end",
					format: "currency",
					value: 10000,
				},
				{
					cell: "D10",
					css: "text_end",
					format: "currency",
					value: 50000,
				},
				{
					cell: "E10",
					css: "text_end",
					format: "currency",
					value: "=SUM(B10:D10)",
				},
				{
					cell: "A11",
					format: "General",
                    css: "bold",
					value: "Baby Monitor",
				},
				{
					cell: "B11",
					css: "text_end",
					format: "currency",
					value: 80000,
				},
				{
					cell: "C11",
					css: "text_end",
					format: "currency",
					value: 5000,
				},
				{
					cell: "D11",
					css: "text_end",
					format: "currency",
					value: 2500,
				},
				{
					cell: "E11",
					css: "text_end",
					format: "currency",
					value: "=SUM(B11:D11)",
				},
				{
					cell: "A12",
					format: "General",
					value: "Total" , 
                    css: "bold",
				},
				{
					cell: "B12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(B2:B11)",
				},
				{
					cell: "C12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(C2:C11)",
				},
				{
					cell: "D12",
					css: "text_end bold",
					format: "currency",
					value: "=SUM(D2:D11)",
				},
				{
					cell: "E12",
					css: "bold text_end",
					format: "currency",
					value: "=SUM(E2:E11)",
				},
			],
		},
	],
};
