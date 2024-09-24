function echartsFit (res) {
	let clientWidth =
		window.innerWidth ||
		document.documentElement.clientWidth ||
		document.body.clientWidth;
	if (!clientWidth) return;
	let isString = false;

	let fontSize = clientWidth / 1920;
	if (typeof res === 'string' && (res.indexOf('px') !== -1 || res.indexOf('PX') !== -1)) {
		isString = true;
		const len = res.length - 2;
		res = res.substr(0, len);
	}
	return isString ? `${res * fontSize}px` : res * fontSize;
}

export default echartsFit

