var SyncPlugin = function(){};

SyncPlugin.prototype.test = function(input,successCallback,errorCallback)
{
	return Cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'test', //Tell plugin, which action must be performed
    [input] //Passing a list of arguments to the Plugin
	);
};

SyncPlugin.prototype.readall = function(channel,successCallback,errorCallback)
{
	return Cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'readall', //Tell plugin, which action must be performed
    [channel] //Passing a list of arguments to the Plugin
	);
};

SyncPlugin.prototype.updateBean = function(channel,oid,jsonUpdate,successCallback,errorCallback)
{
	return Cordova.exec(
    successCallback, //Success callback
    errorCallback, //Failure callback
    'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
    'updateBean', //Tell plugin, which action must be performed
    [channel,oid,jsonUpdate] //Passing a list of arguments to the Plugin
	);
};

SyncPlugin.prototype.addNewBean = function(channel,jsonAdd,successCallback,errorCallback)
{
	return Cordova.exec(
                        successCallback, //Success callback
                        errorCallback, //Failure callback
                        'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                        'addNewBean', //Tell plugin, which action must be performed
                        [channel,jsonAdd] //Passing a list of arguments to the Plugin
                        );
};

SyncPlugin.prototype.deleteBean = function(channel,oid,successCallback,errorCallback)
{
	return Cordova.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'deleteBean', //Tell plugin, which action must be performed
                         [channel,oid] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.insertIntoArray = function(channel,oid,arrayProperty,value,successCallback,errorCallback)
{
	return Cordova.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'insertIntoArray', //Tell plugin, which action must be performed
                         [channel,oid,arrayProperty,value] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.arrayLength = function(channel,oid,arrayProperty,successCallback,errorCallback)
{
	return Cordova.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'arrayLength', //Tell plugin, which action must be performed
                         [channel,oid,arrayProperty] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.clearArray = function(channel,oid,fieldUri,successCallback,errorCallback)
{
	return Cordova.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'clearArray', //Tell plugin, which action must be performed
                         [channel,oid,fieldUri] //Passing a list of arguments to the Plugin
                         );
};

SyncPlugin.prototype.commit = function(successCallback,errorCallback)
{
	return Cordova.exec(
                         successCallback, //Success callback
                         errorCallback, //Failure callback
                         'SyncPlugin', //Tell PhoneGap to run 'HelloPlugin'
                         'commit', //Tell plugin, which action must be performed
                         [] //Passing a list of arguments to the Plugin
                         );
};


Cordova.addConstructor(function(){
	if(!window.plugins)
    {
        window.plugins = {};
    }
    window.plugins.sync = new SyncPlugin();
});