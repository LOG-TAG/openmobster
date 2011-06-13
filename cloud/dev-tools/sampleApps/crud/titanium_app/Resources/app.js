// this sets the background color of the master UIView (when there are no windows/tab groups on it)
Titanium.UI.setBackgroundColor('#000');

var tabGroup = Titanium.UI.createTabGroup(
	{
		barColor:'#336699'
	}
);

//create the tab1 window
var window1 = Titanium.UI.createWindow({
	url:'iphone_issue_list.js',
    titleImage:'images/appcelerator_small.png'
});

var tab1 = Titanium.UI.createTab({
	icon:'KS_nav_views.png',
    title:'Support Tickets',
    window:window1
});

//create the tab2 window
var window2 = Titanium.UI.createWindow({
	url:'iphone_new_issue.js',
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
