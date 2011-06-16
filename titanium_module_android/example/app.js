// This is a test harness for your module
// You should do something interesting in this harness 
// to test out the module and to provide instructions 
// to users on how to use it by example.


// open a single window
var window = Ti.UI.createWindow({
	backgroundColor:'white'
});
var label = Ti.UI.createLabel();
label.text = "Hello World!!";
window.add(label);


// TODO: write your module tests here
var cloudModule = require('org.openmobster.cloud');


var sync = cloudModule.sync();
var channel = "crm_ticket_channel";

//ReadAll
var oids = sync.readAll(channel);
oids = eval('('+oids+')');

if(oids != null)
{
	//List Beans
	listBeans(oids);
	
	//SetValue
	var updatedOid = sync.setValue(channel,oids[0],"title","title://updated");
	Ti.API.info("Updated: "+updatedOid);
	
	//Delete Bean
	var deletedOid = sync.deleteBean(channel,oids[1]);
	Ti.API.info("Deleted: "+deletedOid);
	
	//Add Bean
	sync.newBean(channel);
	sync.setNewBeanValue(channel, "title", "new://title");
	sync.setNewBeanValue(channel, "comment", "new://comment");
	sync.setNewBeanValue(channel, "customer", "new://customer");
	sync.setNewBeanValue(channel, "specialist", "new://specialist");
	var newBeanId = sync.saveNewBean();
	Ti.API.info("Added: " + newBeanId);
	
	oids = sync.readAll(channel);
	oids = eval('('+oids+')');
	listBeans(oids);
}

function listBeans(oids)
{
	Ti.API.info("----------------------------------------------------");
	
	for(var i=0; i<oids.length; i++)
	{
		//getValue
		var title = sync.getValue(channel,oids[i],"title");
		var comment = sync.getValue(channel,oids[i], "comment");
		var customer = sync.getValue(channel,oids[i], "customer");
		var specialist = sync.getValue(channel,oids[i], "specialist");
		
		Ti.API.info("*************************************");
		Ti.API.info("Oid: " + oids[i]);
		Ti.API.info("Title: " + title);
		Ti.API.info("Comment: " + comment);
		Ti.API.info("Customer:"+customer);
		Ti.API.info("Specialist:"+specialist);
	}	
	
	Ti.API.info("----------------------------------------------------");
}
		
window.open();