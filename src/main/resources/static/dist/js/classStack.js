class Stack {
  constructor() {
    this.arr = [];
    this.top = 0;
  }

  size() {
    return this.top;
  }

  push(item) {
    this.arr[this.top++] = item;
  }

  pop() {
    if (this.top <= 0) return null;
    this.top--;
    return this.arr.pop();
  }

  getAll() {
    let returnString = "";
    for (let idx = 0; idx < this.arr.length; idx++) {
      returnString += this.arr[idx];
      if ((this.arr.length - 1) > idx) returnString += " > ";
    }
    return returnString;
  }
}