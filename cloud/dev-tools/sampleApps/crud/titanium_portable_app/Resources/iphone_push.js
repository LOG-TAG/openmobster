// open a single window
var window = Titanium.UI.currentWindow;

var cloudModule = require('org.openmobster.cloud');
var sync = cloudModule.sync();
var rpc = cloudModule.rpc();
var push = cloudModule.push();

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
	Ti.Network.registerForPushNotifications({
  types: [
    Ti.Network.NOTIFICATION_TYPE_BADGE,
    Ti.Network.NOTIFICATION_TYPE_ALERT,
    Ti.Network.NOTIFICATION_TYPE_SOUND
  ],
  success:function(e){
    var deviceToken = e.deviceToken;
    
    push.registerDeviceToken(deviceToken);
    
    Ti.API.info('successfully registered for apple device token with '+e.deviceToken);
    var a = Ti.UI.createAlertDialog({
      title:'DeviceToken',
      message:deviceToken
    });
    a.show();
    
    //Register this App with the Cloud so that it can receive Push Notifications
  },
  error:function(e) {
    Ti.API.warn("push notifications disabled: "+e);
    var a = Ti.UI.createAlertDialog({
      title:'Error',
      message:e
    });
    a.show();
  },
  callback:function(e) {
    var a = Ti.UI.createAlertDialog({
      title:'New Message',
      message:e.data.alert
    });
    a.show();
  }
});
}


commandBar.addEventListener('click',function(){
    invokePushNotification();
});


window.open();