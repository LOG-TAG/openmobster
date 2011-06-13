// This is a test harness for your module
// You should do something interesting in this harness 
// to test out the module and to provide instructions 
// to users on how to use it by example.


// open a single window
var window = Ti.UI.createWindow({
	backgroundColor:'white'
});
var label = Ti.UI.createLabel();
window.add(label);
window.open();

// TODO: write your module tests here
var titanium_module_iphone = require('org.openmobster.cloud');
Ti.API.info("module is => " + titanium_module_iphone);

Ti.API.info("1..............");
label.text = titanium_module_iphone.example();

Ti.API.info("2..............");
function print(oids)
{
   for(var i=0; i<oids.length; i++)
   {
                Ti.API.info(oids[i]);

                var title = sync.getValue(channel, oids[i], "title");
                var comment = sync.getValue(channel, oids[i], "comment");

                Ti.API.info("Title: "+title);
                Ti.API.info("Comment: "+comment);

                //ArrayLength
                var arrayLength = sync.arrayLength(channel,oids[i],"mockList");
                for(var j=0; j<arrayLength; j++)
                {
                   var local = sync.getValue(channel,oids[i],"mockList["+j+"]");
                   Ti.API.info("MockList["+j+"]: "+local);
                }

                Ti.API.info("--------------------------------------");
   }
}

Ti.API.info("3..............");
var sync = titanium_module_iphone.sync();

//ReadAll and GetValue
Ti.API.info("4..............");
var channel = 'webappsync_ticket_channel';
Ti.API.info("5..............");
var oids = sync.readAll(channel);
Ti.API.info("6..............");
if(oids != null)
{
        Ti.API.info("Oids:" +oids);
	oids = eval('(' + oids + ')');
        print(oids);

       //Delete an instance
      sync.deleteBean(channel,oids[0]);

      //Update
      sync.setValue(channel,oids[1],"title","title://updated");

        //Add
        var newOid = sync.newBean(channel);
        Ti.API.info("NewOID: "+newOid);
        sync.setNewBeanValue(channel,"title","newBean://title");
        sync.setNewBeanValue(channel,"comment","newBean://comment");
        sync.saveNewBean();
       

       Ti.API.info("************************************");

       //Display again
       oids = sync.readAll(channel);
       oids = eval('(' + oids + ')');
       print(oids);
}
