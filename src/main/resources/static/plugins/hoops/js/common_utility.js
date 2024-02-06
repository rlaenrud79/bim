let ComUtility = {
    getDiffSeconds: function(startMilliSec, endMilliSec){
        let diffSec = parseInt((endMilliSec - startMilliSec)/1000);
        let diffMin = parseInt(diffSec / 60);
        let remainder = diffSec % 60;
        return diffMin + ":" + remainder;
    }
}