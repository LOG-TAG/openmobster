var win = Titanium.UI.currentWindow;

var cloudModule = require('org.openmobster.cloud');
var sync = cloudModule.sync();
var channel = "crm_ticket_channel";

//
//  Title Field
//
var titleLabel = Titanium.UI.createLabel({
	color:'#fff',
	text:'Title',
	top:10,
	left:30,
	width:100,
	height:'auto'
});

win.add(titleLabel);

var titleField = Titanium.UI.createTextField({
	height:35,
	top:35,
	left:30,
	width:250,
	borderStyle:Titanium.UI.INPUT_BORDERSTYLE_ROUNDED
});

win.add(titleField);

//Customer Picker
var customerLabel = Titanium.UI.createLabel({
	color:'#fff',
	text:'Customer',
	top:80,
	left:30,
	width:100,
	height:'auto'
});

win.add(customerLabel);

var transformPicker = Titanium.UI.create2DMatrix().scale(0.5);
var customerPicker = Titanium.UI.createPicker({
	top:50,
	left:-8,
	width:150,
	height:5,
	transform:transformPicker
});
var data = [];
data[0]=Titanium.UI.createPickerRow({title:'Apple'});
data[1]=Titanium.UI.createPickerRow({title:'Google'});
data[2]=Titanium.UI.createPickerRow({title:'Oracle'});
data[3]=Titanium.UI.createPickerRow({title:'Microsoft'});
customerPicker.add(data);
customerPicker.selectionIndicator = true;

win.add(customerPicker);

//Specialist Picker
var specialistLabel = Titanium.UI.createLabel({
	color:'#fff',
	text:'Specialist',
	top:80,
	left:175,
	width:100,
	height:'auto'
});

win.add(specialistLabel);

var specialistPicker = Titanium.UI.createPicker({
	top:50,
	left:142,
	width:150,
	height:25,
	transform:transformPicker
});
var data = [];
data[0]=Titanium.UI.createPickerRow({title:'Steve J'});
data[1]=Titanium.UI.createPickerRow({title:'Eric S'});
data[2]=Titanium.UI.createPickerRow({title:'Larry E'});
data[3]=Titanium.UI.createPickerRow({title:'Steve B'});
specialistPicker.add(data);
specialistPicker.selectionIndicator = true;

win.add(specialistPicker);


//Comments
var commentsLabel = Titanium.UI.createLabel({
	color:'#fff',
	text:'Comments',
	top:225,
	left:30,
	width:100,
	height:'auto'
});

win.add(commentsLabel);

//Comments TextArea
var comments = Titanium.UI.createTextArea({
    height:70,
    width:270,
    top:250,
    left:30,
    font:{fontSize:20,fontFamily:'Marker Felt', fontWeight:'bold'},
    color:'#888',
    textAlign:'left',
    appearance:Titanium.UI.KEYBOARD_APPEARANCE_ALERT,   
    keyboardType:Titanium.UI.KEYBOARD_NUMBERS_PUNCTUATION,
    returnKeyType:Titanium.UI.RETURNKEY_EMERGENCY_CALL,
    borderWidth:2,
    borderColor:'#bbb',
    borderRadius:5
});

win.add(comments);


var commandBar = Titanium.UI.createButtonBar({
    labels:['OK', 'Cancel'],
    backgroundColor:'#336699',
    top:330,
    style:Titanium.UI.iPhone.SystemButtonStyle.BAR,
    height:25,
    width:200
});
win.add(commandBar);

commandBar.addEventListener('click', function(e){
	if(e.index == 0)
	{
		//OK
		var title = titleField.value;
		var customer = customerPicker.getSelectedRow(0).title;
		var specialist = specialistPicker.getSelectedRow(0).title;
		var comment = comments.value;
		
		//Data Validation
		if((title == null || title == '') || (comment == null || comment == ''))
		{
			var validationDialog = Titanium.UI.createAlertDialog({
								title:"Error",
								message:"All fields are required"
			});
			validationDialog.buttonNames = ['Close'];
			validationDialog.show();
			
			return;
		}
		
		//Save the New ticket into the channel
		var newBean = sync.newBean(channel);
		newBean.setValue("title",title);
		newBean.setValue("customer", customer);
		newBean.setValue("specialist", specialist);
		newBean.setValue("comment", comment);
		newBean.commit();
	}
	else
	{
		//Cancel
	}
	var tabGroup = Ti.UI.currentWindow.tabGroup;
	tabGroup.setActiveTab(0);
});