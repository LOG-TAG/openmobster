// open a single window
var window = Titanium.UI.currentWindow;

var cloudModule = require('org.openmobster.cloud');
var sync = cloudModule.sync();
var rpc = cloudModule.rpc();

var push = Titanium.UI.createButton({
	title: 'Initiate Push'
});
window.add(push);

function invokePushNotification()
{
	Ti.API.info("********Push Invocation***********");
	
	var response = rpc.invoke("/listen/push", "{}");
	
	//Process the json response
	response = eval('(' + response + ')');
	
	Ti.API.info("***************************************");
	Ti.API.info("Status: "+response.status);
	Ti.API.info("Status Message: "+response.statusMsg);
	
	Ti.API.info("****************************************");
}


push.addEventListener('click',function(){
    invokePushNotification();
});
