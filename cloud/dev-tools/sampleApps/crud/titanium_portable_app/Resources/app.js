
function loadApp()
{
	// this sets the background color of the master UIView (when there are no windows/tab groups on it)
	Titanium.UI.setBackgroundColor('#000');
	
	var tabGroup = Titanium.UI.createTabGroup(
		{
			barColor:'#336699'
		}
	);
	
	var window1Url;
	var window2Url;
	
	if (Titanium.Platform.name == 'iPhone OS')
	{
		window1Url = 'iphone_issue_list.js';
		window2Url = 'iphone_new_issue.js';
	}
	else
	{
		window1Url = 'issue_list.js';
		window2Url = 'new_issue.js';
	}
	
	//create the tab1 window
	var window1 = Titanium.UI.createWindow({
		url:window1Url,
	    titleImage:'images/appcelerator_small.png'
	});
	
	var tab1 = Titanium.UI.createTab({
		icon:'KS_nav_views.png',
	    title:'Support Tickets',
	    window:window1
	});
	
	//create the tab2 window
	var window2 = Titanium.UI.createWindow({
		url:window2Url,
	    titleImage:'images/appcelerator_small.png'
	});
	
	var tab2 = Titanium.UI.createTab({
		icon:'KS_nav_ui.png',
	    title:'Report an Issue',
	    window:window2
	});
	
	//add the tabs
	tabGroup.addTab(tab1);
	tabGroup.addTab(tab2);
	
	//active tab
	tabGroup.setActiveTab(0);
	
	
	//open tab group with a transition animation
	tabGroup.open({
		transition:Titanium.UI.iPhone.AnimationStyle.FLIP_FROM_LEFT
	});
}

Ti.Android.currentActivity.addEventListener('resume', function(e) 
{
	//Cloud Module
	var cloudModule = require('org.openmobster.cloud');
	var sync = cloudModule.sync();
	
	//If Module is ready to take invocations
	if(sync != null)
	{
		loadApp();
	}
});
