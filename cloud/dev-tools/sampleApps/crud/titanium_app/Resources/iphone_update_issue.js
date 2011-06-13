var cloudModule = require('org.openmobster.cloud');
var sync = cloudModule.sync();
var channel = "crm_ticket_channel";
var oid = Titanium.App.Properties.getString('oid','');
var win = Titanium.UI.currentWindow;

win.addEventListener('open',function(){
	var title = sync.getValue(channel,oid,"title");
	var comment = sync.getValue(channel,oid,"comment");
	var customer = sync.getValue(channel,oid,"customer");
	var specialist = sync.getValue(channel,oid,"specialist");
	
	Ti.API.info("****************************");
	Ti.API.info("Title: "+title);
	Ti.API.info("Comment: "+comment);
	Ti.API.info("Customer: "+customer);
	Ti.API.info("Specialist: "+specialist);
	Ti.API.info("****************************");
	
	titleField.value = title;
	comments.value = comment;
});

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
data[0]=Titanium.UI.createPickerRow({title:'Bananas'});
data[1]=Titanium.UI.createPickerRow({title:'Strawberries'});
data[2]=Titanium.UI.createPickerRow({title:'Mangos'});
data[3]=Titanium.UI.createPickerRow({title:'Grapes'});
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
data[0]=Titanium.UI.createPickerRow({title:'Bananas'});
data[1]=Titanium.UI.createPickerRow({title:'Strawberries'});
data[2]=Titanium.UI.createPickerRow({title:'Mangos'});
data[3]=Titanium.UI.createPickerRow({title:'Grapes'});
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
	}
	else
	{
		//Cancel
	}
	Titanium.UI.currentWindow.close();
});