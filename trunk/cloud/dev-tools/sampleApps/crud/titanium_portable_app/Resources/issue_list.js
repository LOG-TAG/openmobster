var tableView = Titanium.UI.createTableView({});
var eventListenerAdded = 0;
function loadWindow()
{
	if(tableView != null)
	{
		tableView.setData([]);
	}
		
	//Cloud Module
	var cloudModule = require('org.openmobster.cloud');
	var sync = cloudModule.sync();
	if(sync == null)
	{
		return;
	}
	
	var channel = "crm_ticket_channel";
	var oids = sync.readAll(channel);
	
	if(oids != null && oids != '[]')
	{
		oids = eval('('+oids+')');
		
		var data = new Array(oids.length);
		for(var i=0; i<oids.length; i++)
		{
			var bean = sync.readById(channel,oids[i]);
			var title = bean.getValue("title");
			var rowData = {title:title, hasChild:false, oid:oids[i]};
			data[i] = rowData;
		}
		
		// create table view
		tableView.setData(data);
		
		// add table view to the window
		Titanium.UI.currentWindow.add(tableView);
		
		// create table view event listener
		if(eventListenerAdded > 0)
		{
			return;
		}
		eventListenerAdded++;
		tableView.addEventListener('click', function(e)
		{
			if(e.rowData.oid)
			{
				var issueDialog = Titanium.UI.createAlertDialog({
							title:"Issue",
							message:e.rowData.title
				});
				
				issueDialog.buttonNames = ['Update', 'Close','Delete'];
				issueDialog.cancel = 1;
				
				Titanium.App.Properties.setString('oid',e.rowData.oid);
				
				issueDialog.show();
				
				//Add an EventHandler
				issueDialog.addEventListener('click',function(e){
					var oid = Titanium.App.Properties.getString('oid','');
					if(e.index == 0)
					{
						var updateUrl;
						if (Titanium.Platform.name != 'iPhone OS')
						{
							updateUrl = "update_issue.js";
						}
						else
						{
							updateUrl = "iphone_update_issue.js"
						}
						var updateWindow = Titanium.UI.createWindow({
							url:updateUrl,
			    			titleImage:'images/appcelerator_small.png',
			    			title:'Update Issue'
						});
						Titanium.UI.currentTab.open(updateWindow,{animated:true});
					}
					else if(e.index == 2)
					{
						//Delete this row
						sync.deleteBean(channel,oid);
						loadWindow();
					}
				});
			}
		});
	}
	else
	{
		var notLoadedDialog = Titanium.UI.createAlertDialog({
							title:"Synchronizing...",
							message:"Data is not available yet"
		});	
		
		notLoadedDialog.show();
	}
}

if (Titanium.Platform.name != 'iPhone OS')
{
	Ti.Android.currentActivity.addEventListener('resume', function(e) {
		var tabGroup = Ti.UI.currentWindow.tabGroup;
		var tab = tabGroup.activeTab;
		if(tab.title == "Support Tickets")
		{
			loadWindow();
		}
	});
}
else
{
	Titanium.UI.currentWindow.addEventListener('open',function(){
		loadWindow();
	});
	
	Titanium.UI.currentWindow.addEventListener('focus',function(){
		var tabGroup = Ti.UI.currentWindow.tabGroup;
		var tab = tabGroup.activeTab;
		
		if(tab.title == "Support Tickets")
		{
			loadWindow();
		}
	});

	Ti.App.addEventListener('resume', function(e) {
		var tabGroup = Ti.UI.currentWindow.tabGroup;
		var tab = tabGroup.activeTab;
		
		if(tab.title == "Support Tickets")
		{
			loadWindow();
		}
	});
}