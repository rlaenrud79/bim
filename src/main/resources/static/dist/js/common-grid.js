let countChecked = function (grid, columnName) {
  let count = 0;
  grid.data.forEach(row => {
    if (row[columnName]) ++count;
  })
  return count;
}

let checkNumericColumn = function (col,arrayNumberColId) {
  let result = false;

  arrayNumberColId.forEach(colId => {
    if (colId == col.id) result = true;
  });

  return result;
}

let isRow = function (grid, columnName, value) {
  let result = false;
  grid.data.forEach(row => {
    if (row[columnName] ===  value) result = true;
  })

  return result;
}

let getEmptyRow = function(grid){
  return grid.data.forEach(row => {
    if(row.$emptyRow) return row;
  })
}

let getItem = function(grid, index){
  let rowIndex = 0;
  let item = {};
  if(grid.data.getLength() < index + 1) return item;
  grid.data.forEach(row => {
    if(index == rowIndex++) item = row;
  })

  return item;
}

let getFirstItem = function(grid){
  return getItem(grid,0);
}

let getLastItem = function(grid){
  if(grid.getLength() == 0) return {};
  return getItem(grid,grid.getLength() - 1);
}
//region state
let isSaveTargetRow = function (row) {
  return isNewRow(row) || isUpdateRow(row);
}

let isNewRow = function (row) {
  return row.rowState == "C";
}

let isUpdateRow = function (row) {
  return row.rowState == "U";
}

let isSavedRow = function (row) {
  return row.rowState == "R" || isUpdateRow(row);
}

let isEmptyRow = function (row) {
  return row.rowState == undefined || row.rowState == "";
}

let isNormalRow = function (row) {
  return !isEmptyRow(row);
}
//endregion

let calculateMultiple = function (val1, val2) {
  if (isNumeric(val1) && isNumeric(val2)) return parseInt(val1 * val2);
  return "";
}

let calculateMultipleZ = function (val1, val2) {
  if ((isNumeric(val1) || val1 == 0) && (isNumeric(val2) || val2 == 0)) return parseInt(val1 * val2);
  return "";
}

let calculateAdd = function (val1, val2) {
  if ((isNumeric(val1) || val1 == 0) && (isNumeric(val2) || val2 == 0)) return parseFloat(val1) + parseFloat(val2);
  return "";
}

let calculateMinus = function (val1, val2) {
  if ((isNumeric(val1) || val1 == 0) && (isNumeric(val2) || val2 == 0)) return parseFloat(val1) - parseFloat(val2);
  return "";
}