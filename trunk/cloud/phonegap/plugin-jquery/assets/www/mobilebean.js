var MobileBeanPlugin = function(){};

MobileBeanPlugin.prototype.readall = function(channel,successCallback,errorCallback)
{
	return PhoneGap.exec(
		successCallback, //Success callback
		errorCallback, //Failure callback
		'MobileBeanPlugin', //Tell PhoneGap to run 'HelloPlugin'
		'readall', //Tell plugin, which action must be performed
		[channel] //Passing a list of arguments to the Plugin
	);
};

MobileBeanPlugin.prototype.value = function(channel,oid,fieldUri,successCallback,errorCallback)
{
	return PhoneGap.exec(
		successCallback, //Success callback
		errorCallback, //Failure callback
		'MobileBeanPlugin', //Tell PhoneGap to run 'HelloPlugin'
		'value', //Tell plugin, which action must be performed
		[channel,oid,fieldUri] //Passing a list of arguments to the Plugin
	);
};

MobileBeanPlugin.prototype.test = function(tag,successCallback,errorCallback)
{
	return PhoneGap.exec(
		successCallback, //Success callback
		errorCallback, //Failure callback
		'MobileBeanPlugin', //Tell PhoneGap to run 'HelloPlugin'
		'test', //Tell plugin, which action must be performed
		[tag] //Passing a list of arguments to the Plugin
	);
};

MobileBeanPlugin.prototype.insertIntoArray = function(channel,oid,arrayProperty,value,successCallback,errorCallback)
{
	return PhoneGap.exec(
		successCallback, //Success callback
		errorCallback, //Failure callback
		'MobileBeanPlugin', //Tell PhoneGap to run 'HelloPlugin'
		'insertIntoArray', //Tell plugin, which action must be performed
		[channel,oid,arrayProperty,value] //Passing a list of arguments to the Plugin
	);
};

MobileBeanPlugin.prototype.arrayLength = function(channel,oid,arrayProperty,successCallback,errorCallback)
{
	return PhoneGap.exec(
		successCallback, //Success callback
		errorCallback, //Failure callback
		'MobileBeanPlugin', //Tell PhoneGap to run 'HelloPlugin'
		'arrayLength', //Tell plugin, which action must be performed
		[channel,oid,arrayProperty] //Passing a list of arguments to the Plugin
	);
};

MobileBeanPlugin.prototype.addNewBean = function(channel,successCallback,errorCallback)
{
	return PhoneGap.exec(
		successCallback, //Success callback
		errorCallback, //Failure callback
		'MobileBeanPlugin', //Tell PhoneGap to run 'HelloPlugin'
		'addNewBean', //Tell plugin, which action must be performed
		[channel] //Passing a list of arguments to the Plugin
	);
};

MobileBeanPlugin.prototype.updateBean = function(channel,oid,fieldUri,value,successCallback,errorCallback)
{
	return PhoneGap.exec(
		successCallback, //Success callback
		errorCallback, //Failure callback
		'MobileBeanPlugin', //Tell PhoneGap to run 'HelloPlugin'
		'updateBean', //Tell plugin, which action must be performed
		[channel,oid,fieldUri,value] //Passing a list of arguments to the Plugin
	);
};

MobileBeanPlugin.prototype.deleteBean = function(channel,oid,successCallback,errorCallback)
{
	return PhoneGap.exec(
		successCallback, //Success callback
		errorCallback, //Failure callback
		'MobileBeanPlugin', //Tell PhoneGap to run 'HelloPlugin'
		'deleteBean', //Tell plugin, which action must be performed
		[channel,oid] //Passing a list of arguments to the Plugin
	);
};

MobileBeanPlugin.prototype.commit = function(successCallback,errorCallback)
{
	return PhoneGap.exec(
		successCallback, //Success callback
		errorCallback, //Failure callback
		'MobileBeanPlugin', //Tell PhoneGap to run 'HelloPlugin'
		'commit', //Tell plugin, which action must be performed
		[] //Passing a list of arguments to the Plugin
	);
};

PhoneGap.addConstructor(function(){
			PhoneGap.addPlugin("mobileBeanPlugin",new MobileBeanPlugin());
		});