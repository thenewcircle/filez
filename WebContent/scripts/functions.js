function dump(obj, maxLevel, indent) {
	if (indent == null) indent = "";
	if (maxLevel == null) maxLevel = 0;
	if (indent.length > maxLevel) return "###";
	var strMessage = "\r\n";
	for (var p in obj) {
		strMessage += indent + p + ": ";
		try {
			if (obj[p] && typeof(obj[p]) == "object") {
				strMessage += dump(obj[p], indent + "\t");
			} else {
				strMessage += obj[p];
			}
		} catch (e) {
			strMessage += e.message;
		}
		strMessage += "\r\n";
	}
	return strMessage;
}
	    