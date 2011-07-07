// open a single window
var window = Ti.UI.createWindow({
	backgroundColor:'white'
});

var cloudModule = require('org.openmobster.cloud');
var sync = cloudModule.sync();
var channel = "crm_ticket_channel";
var rpc = cloudModule.rpc();

var button = Titanium.UI.createButton({title:'Run'});
window.add(button);


function listBeans(oids)
{
	Ti.API.info("----------------------------------------------------");
	
	for(var i=0; i<oids.length; i++)
	{
		var bean = sync.readById(channel,oids[i]);
		
		//getValue
		var oid = bean.oid();
		var title = bean.getValue("title");
		var comment = bean.getValue("comment");
		var customer = bean.getValue("customer");
		var specialist = bean.getValue("specialist");
		
		Ti.API.info("*************************************");
		Ti.API.info("Oid: " + oid);
		Ti.API.info("Title: " + title);
		Ti.API.info("Comment: " + comment);
		Ti.API.info("Customer:"+customer);
		Ti.API.info("Specialist:"+specialist);
	}	
	
	Ti.API.info("----------------------------------------------------");
}

function printNewBean(bean)
{
   Ti.API.info("Printing New Bean");
   Ti.API.info("-------------------------");

   var oid = bean.oid();
   var title = bean.getValue("title");
   var comment = bean.getValue("comment");
   var customer = bean.getValue("customer");
   var specialist = bean.getValue("specialist");
		
   Ti.API.info("*************************************");
   Ti.API.info("OID: "+oid);
   Ti.API.info("Title: " + title);
   Ti.API.info("Comment: " + comment);
   Ti.API.info("Customer:"+customer);
   Ti.API.info("Specialist:"+specialist);
   Ti.API.info("-------------------------");
}

function invokeRPC()
{
	var payload = "{'name':'John', 'lastname':'Doe', 'customers':['Apple','Google','Microsoft','Oracle']}";
	var response = rpc.invoke("/titanium/module/tester", payload);
	
	Ti.API.info(response);
	
	//Process the json response
	response = eval('(' + response + ')');
	
	Ti.API.info("***************************************");
	Ti.API.info("Status: "+response.status);
	Ti.API.info("Status Message: "+response.statusMsg);
	Ti.API.info("Name: "+response.name);
	Ti.API.info("Last Name:" +response.lastname);
	Ti.API.info("Customer Count: "+response.customers.length);
	Ti.API.info("Specialists Count: "+response.specialists.length);
	
	var customerLength = response.customers.length;
	for(var i=0; i<customerLength; i++)
	{
		var local = response.customers[i];
		Ti.API.info("Customer: "+local);
	}
	
	Ti.API.info("***************************************");
}

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


button.addEventListener('click',function(){
    /*var oids = sync.readAll(channel);
    oids = eval('('+oids+')');
    if(oids != null && oids.length > 0)
    {
		listBeans(oids);
	
		//SetValue
		var updateBean = sync.readById(channel,oids[0]);
		updateBean.setValue("title", "updated://title");
		updateBean.setValue("customer", "updated://customer");
		updateBean.setValue("specialist","updated://specialist");
		updateBean.setValue("comment", "updated://comment");
		updateBean.commit();
	
		//Delete Bean
		var deletedOid = sync.deleteBean(channel,oids[1]);
		Ti.API.info("Deleted: "+deletedOid);
	
		//Add Bean
		var newBean = sync.newBean(channel);
		newBean.setValue("title", "new://title");
		newBean.setValue("comment", "new://comment");
		newBean.setValue("customer", "new://customer");
		newBean.setValue("specialist", "new://specialist");
	    var newBeanId = newBean.commit();
		printNewBean(newBean);
		
		oids = sync.readAll(channel);
		oids = eval('('+oids+')');
		listBeans(oids);
    }
    else
    {
		Ti.API.info("Channel is not booted yet");
    }
    
    //Testing RPC
    invokeRPC();*/
    
    invokePushNotification();
});


window.open();