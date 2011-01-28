//Function declaration
function loadList()
{
	var channel = "offlineapp_demochannel";
	var beans = mobileBean.readAll(channel); //accesses the MobileBean service via the Javascript bridge
	beans = (""+beans).split(","); //converts the list of MobileBean oids into an array
	var length = beans.length;
	var listArray = new Array(length);
	for(var i=0; i<length; i++)
	{
		var oid = beans[i];
		var demoString = mobileBean.getValue(channel,oid,"demoString"); //extracts the value of the field named 'demoString'. See: com.offlineApp.cloud.sync.DemoBean
		listArray[i] = {title:demoString,id:oid};
	}
	
	var list = new joMenu(listArray); //populates the Menu with the information extracted from the MobileBean instances
	return list;
}

//This is all HTML5 GUI code based on the Jo HTML5 Mobile App Framework: http://joapp.com/

//initialize jo
jo.load();

//sets up the toolbar
var toolbar = new joToolbar("");
var nav = new joNavbar();
var stack = new joStackScroller();
var flexCol = new joFlexcol([nav,stack]);

var container = new joContainer([flexCol,toolbar]);
container.setStyle({position: "absolute", top: "0", left: "0", bottom: "0", right: "0"});

var scn = new joScreen(container);
nav.setStack(stack);

//Create the menu
var list = loadList();
var menu = new joCard([list]);
menu.setTitle("HTML5 Offline App");

//Event Handling
list.selectEvent.subscribe(function(oid) 
{
	//Shows details about the selected MobileBean in an 'Alert' dialog box
	var channel = "offlineapp_demochannel";
	var demoString = mobileBean.getValue(channel,oid,"demoString");
	
	//Iterates through an indexed/array property called 'demoArray'. See: com.offlineApp.cloud.sync.DemoBean
	var details = "";
	var demoArrayLength = mobileBean.arrayLength(channel,oid,"demoArray");
	for(var i=0; i<demoArrayLength; i++)
	{
		var local = mobileBean.getValue(channel,oid,"demoArray["+i+"]");
		details += local+",";
	}
	
	scn.alert(demoString, details, 
	function() { list.deselect(); });
}, this);


stack.push(menu);