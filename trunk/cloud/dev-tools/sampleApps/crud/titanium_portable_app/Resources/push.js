// open a single window
var window = Titanium.UI.currentWindow;

var cloudModule = require('org.openmobster.cloud');
var sync = cloudModule.sync();
var rpc = cloudModule.rpc();

var commandBar = Titanium.UI.createButtonBar({
    labels:['Initiate Push'],
    backgroundColor:'#336699',
    top:150,
    style:Titanium.UI.iPhone.SystemButtonStyle.BAR,
    height:25,
    width:200
});
window.add(commandBar);

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


commandBar.addEventListener('click',function(){
    invokePushNotification();
});


window.open();