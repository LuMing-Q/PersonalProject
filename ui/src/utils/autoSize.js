/*
	自动计算元素宽度
*/
function autoSize (res) {
  const clientWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
  if (!clientWidth) return;

  const autoWidth = clientWidth / 1920;
  if (typeof res === 'string' && /px/i.test(res)) {
    return `${parseFloat(res) * autoWidth}px`;
  } else {
    return res * autoWidth;
  }
}

export default autoSize