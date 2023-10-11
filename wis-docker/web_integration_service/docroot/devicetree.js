/**
 * 
 */
var deleteListener=function(udi) {
	var divForUDI=document.getElementById(udi);
	divForUDI.parentNode.removeChild(divForUDI);
}

var addListener=function(udi) {
	var divForUDI=document.createElement("div");
	divForUDI.id=udi;
	var rootToAddTo=document.getElementById("devices_root");
	rootToAddTo.appendChild(divForUDI);

	var deviceInfo=deviceList.get(udi);

	var deviceHTML="<b>Manufacturer:</b>&nbsp;"+deviceInfo.manufacturer+"<b>Model:</b>&nbsp;"+deviceInfo.model;
	var asciiImageStr=btoa(String.fromCharCode.apply(null, new Uint8Array(deviceInfo.icon.image)));
	//console.log("imagesrc is "+asciiImageStr);
	var imgSrcStr="src=\"data:image/png;base64,"+asciiImageStr+"\">";
	deviceHTML=deviceHTML+"<br/><img id=\"icon_"+udi+"\"" + imgSrcStr;
	
	
	//document.getElementById("ItemPreview").src = "data:image/png;base64," + btoa(String.fromCharCode.apply(null, new Uint8Array([137,80,78,71,13,10,26,10,0,...])));
	
	divForUDI.innerHTML=deviceHTML;
}

function startUpdatesLoop() {
	deviceList.addSetListener( addListener );
	deviceList.addDeleteListener( deleteListener );
	updateDeviceList();
}