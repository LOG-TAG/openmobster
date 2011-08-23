var win = Titanium.UI.currentWindow;

var oid = Titanium.App.Properties.getString('oid','');
var cloudModule = require('org.openmobster.cloud');
var sync = cloudModule.sync();
var channel = "crm_ticket_channel";


Ti.UI.currentWindow.addEventListener('open',function(){	
	var bean = sync.readById(channel,oid);
	var title = bean.getValue("title");
	var customer = bean.getValue("customer");
	var specialist = bean.getValue("specialist");
	var comment = bean.getValue("comment");
	
	titleField.value = title;
	
	//find customer row
	var customerRow = 0;
	if(customer == "Apple")
	{
		customerRow = 0;
	}
	else if(customer == "Google")
	{
		customerRow = 1;
	}
	else if(customer == "Oracle")
	{
		customerRow = 2;
	}
	else if(customer == "Microsoft")
	{
		customerRow = 3;
	}
	customerPicker.setSelectedRow(0,customerRow,true);
	
	//Find specialist row
	var specialistRow = 0;
	if(specialist == "Steve J")
	{
		specialist = 0;
	}
	else if(specialist == "Eric S")
	{
		specialistRow = 1;
	}
	else if(specialist == "Larry E")
	{
		specialistRow = 2;
	}
	else if(specialist == "Steve B")
	{
		specialistRow = 3;
	}
	specialistPicker.setSelectedRow(0,specialistRow,true);
	
	//comment field
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

var customerPicker = Titanium.UI.createPicker({
	top:105,
	left:30,
	width:'auto',
	height:'auto'
});
var data = [];
data[0]=Titanium.UI.createPickerRow({title:'Apple'});
data[1]=Titanium.UI.createPickerRow({title:'Google'});
data[2]=Titanium.UI.createPickerRow({title:'Oracle'});
data[3]=Titanium.UI.createPickerRow({title:'Microsoft'});
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
data[0]=Titanium.UI.createPickerRow({title:'Steve J'});
data[1]=Titanium.UI.createPickerRow({title:'Eric S'});
data[2]=Titanium.UI.createPickerRow({title:'Larry E'});
data[3]=Titanium.UI.createPickerRow({title:'Steve B'});
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

ok.addEventListener('click', function(){

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
	
	var bean = sync.readById(channel,oid);
	bean.setValue("title",title);
	bean.setValue("customer",customer);
	bean.setValue("specialist",specialist);
	bean.setValue("comment",comment);
	bean.commit();
	
	Titanium.UI.currentWindow.close();
});