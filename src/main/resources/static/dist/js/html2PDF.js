window.jsPDF = window.jspdf.jsPDF;

// reference: http://html2canvas.hertzen.com/documentation.html
const html2PDF = (function () {
  let renderedImg = [];
  const contWidth = 200;
  const padding = 5;

  function download(fileName, selector, beforeFn, afterFn) {
    executeFunction(beforeFn);

    setTimeout(function() {
      _createPDF(fileName, selector, afterFn);
    }, 100)
  }

  function executeFunction(fn) {
    if (typeof (fn) != 'undefined') {
      fn();
    }
  }

  function _createPDF(fileName, selector, afterFn) {
    const selectNodeList = document.querySelectorAll(selector);
    if (selectNodeList.length === 0) {
      executeFunction(afterFn);
      return false;
    }

    let deferreds = [];
    const doc = new jsPDF("p", "mm", "a4");

    for (let i = 0; i < selectNodeList.length; i++) {
      const deferred = $.Deferred();
      deferreds.push(deferred.promise());
      _generateCanvas(i, doc, deferred, selectNodeList[i]);
    }

    $.when.apply($, deferreds).then(function () {
      const sorted = renderedImg.sort(function (a, b) {
        return a.num < b.num ? -1 : 1;
      });
      let curHeight = padding;

      for (let i = 0; i < sorted.length; i++) {
        const sortedHeight = sorted[i].height;
        const sortedImage = sorted[i].image;

        if (curHeight + sortedHeight > 297 - padding * 2) {
          doc.addPage();
          curHeight = padding;
          doc.addImage(sortedImage, 'jpeg', padding, curHeight, contWidth, sortedHeight);
          curHeight += sortedHeight;
        } else {
          doc.addImage(sortedImage, 'jpeg', padding, curHeight, contWidth, sortedHeight);
          curHeight += sortedHeight;
        }
      }

      doc.save(`${fileName ? fileName : 'download'}.pdf`);

      curHeight = padding;
      renderedImg = [];

      setTimeout(function() {
        executeFunction(afterFn)
      }, 100)
    });
  }

  function _generateCanvas(i, doc, deferred, curList) {
    const pdfWidth = $(curList).outerWidth() * 0.2645;
    const pdfHeight = $(curList).outerHeight() * 0.2645;
    const heightCalc = contWidth * pdfHeight / pdfWidth;
    html2canvas(curList).then(
      function (canvas) {
        const img = canvas.toDataURL('image/jpeg', 1.0);
        renderedImg.push({num: i, image: img, height: heightCalc});
        deferred.resolve();
      }
    );
  }

  return {
    download
  }
})();
