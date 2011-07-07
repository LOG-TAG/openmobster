
// open a single window
var window = Ti.UI.createWindow({
	backgroundColor:'white'
});

var cloudModule = require('org.openmobster.cloud');
var sync = cloudModule.sync();
var rpc = cloudModule.rpc();

var button = Titanium.UI.createButton({title:'Run'});
window.add(button);


function invokePushNotification()
{
	Ti.API.info("********Push Invocation***********");
	
	var response = rpc.invoke("/listen/push", "{}");
	
	//Process the json response
	response = eval('(' + response + ')');
	
	Ti.API.info("***************************************");
	Ti.API.info("Status: "+response.status);
	Ti.API.info("Status Message: "+response.statusMsg);
	
	//rpc.showNotification();
	
	Ti.API.info("****************************************");
}


button.addEventListener('click',function(){
    invokePushNotification();
});


window.open();