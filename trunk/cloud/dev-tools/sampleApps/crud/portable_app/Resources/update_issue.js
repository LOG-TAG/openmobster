var win = Titanium.UI.currentWindow;

var oid;
Ti.UI.currentWindow.addEventListener('open',function(){
	oid = Titanium.App.Properties.getString('oid','');
	titleField.value = 'Title://Value';
	customerPicker.setSelectedRow(0,2,true);
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

var customerPicker = Titanium.UI.createPicker({
	top:105,
	left:30,
	width:'auto',
	height:'auto'
});
var data = [];
data[0]=Titanium.UI.createPickerRow({title:'Bananas'});
data[1]=Titanium.UI.createPickerRow({title:'Strawberries'});
data[2]=Titanium.UI.createPickerRow({title:'Mangos'});
data[3]=Titanium.UI.createPickerRow({title:'Grapes'});
customerPicker.add(data);

win.add(customerPicker);

//Specialist Picker
var specialistLabel = Titanium.UI.createLabel({
	color:'#fff',
	text:'Specialist',
	top:160,
	left:30,
	width:100,
	height:'auto'
});

win.add(specialistLabel);

var specialistPicker = Titanium.UI.createPicker({
	top:185,
	left:30,
	width:'auto',
	height:'auto'
});
var data = [];
data[0]=Titanium.UI.createPickerRow({title:'Bananas'});
data[1]=Titanium.UI.createPickerRow({title:'Strawberries'});
data[2]=Titanium.UI.createPickerRow({title:'Mangos'});
data[3]=Titanium.UI.createPickerRow({title:'Grapes'});
specialistPicker.add(data);

win.add(specialistPicker);


//Comments
var commentsLabel = Titanium.UI.createLabel({
	color:'#fff',
	text:'Comments',
	top:235,
	left:30,
	width:100,
	height:'auto'
});

win.add(commentsLabel);

//Comments TextArea
var comments = Titanium.UI.createTextArea({
    height:70,
    width:270,
    top:260,
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

var buttonView = Titanium.UI.createView({
	layout:'horizontal',
	height:'auto',
	top:330,
	width:100,
	left:100
	//backgroundColor:'#13386c'
});

var ok = Titanium.UI.createButton({
	title: 'OK'
});

var cancel = Titanium.UI.createButton({
    title:'Cancel'
});

buttonView.add(ok);
buttonView.add(cancel);
win.add(buttonView);

//Event Handling
cancel.addEventListener('click',function(e){
	Titanium.UI.currentWindow.close();
});