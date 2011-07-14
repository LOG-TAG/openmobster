var win = Titanium.UI.currentWindow;

var cloudModule = require('org.openmobster.cloud');
var sync = cloudModule.sync();
var rpc = cloudModule.rpc();
var channel = "crm_ticket_channel";

var response = rpc.invoke('/async/load/spinners','{}');
response = eval('(' + response + ')');


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
var customerLength = response.customers.length;
var customerData = [];
for(var i=0; i<customerLength; i++)
{
	customerData[i] = Titanium.UI.createPickerRow({title:response.customers[i]});
}
customerPicker.add(customerData);

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
var specialistLength = response.specialists.length;
var specialistData = [];
for(var i=0; i<specialistLength; i++)
{
	specialistData[i] = Titanium.UI.createPickerRow({title:response.specialists[i]});
}
specialistPicker.add(specialistData);

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
ok.addEventListener('click',function(e){
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
	
	var tabGroup = Ti.UI.currentWindow.tabGroup;
	tabGroup.setActiveTab(0);
});

cancel.addEventListener('click',function(e){
	var tabGroup = Ti.UI.currentWindow.tabGroup;
	tabGroup.setActiveTab(0);
});