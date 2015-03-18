#### Introduction ####
This is a simple tutorial to learn the RPC (Remote Procedure Call) service
in OpenMobster.

#### Step 1: Developing the Server Side Component ####
The server side consists of creating a service bean. A service bean is an implementation of the _org.openmobster.server.api.service.MobileServiceBean_ interface.
```
@ServiceInfo(uri="/asyncserviceapp/getlist")
public class GetList implements MobileServiceBean
```

The _org.openmobster.server.api.service.ServiceInfo_ annotation creates a unique way to identify the service bean. This **uri** is used on the device side to make an invocation on the service bean.
```
public Response invoke(Request request) 
	{	
		Response response = new Response();
		
		//Get a list emails
		List<EmailBean> mockBeans = EmailBean.generateMockBeans();
		
		//Create a list of subjects to be returned for display
		List<String> subjects = new ArrayList<String>();
		for(EmailBean local:mockBeans)
		{
			subjects.add("id="+local.getOid()+":subject="+local.getSubject());
		}
		
		//Set the information in the response object
		response.setListAttribute("subjects", subjects);
		
		return response;
	}
```

#### Step 2: Register the service bean with the system ####
Register this component with **META-INF/openmobster-config.xml** file.
```
<?xml version="1.0" encoding="UTF-8"?>

<deployment xmlns="urn:jboss:bean-deployer:2.0">
   <bean name="/asyncserviceapp/getlist" class="org.async.service.app.cloud.app.GetList">
   		<depends>services://MobileObjectMonitor</depends>
   		<depends>services://MobileServiceMonitor</depends>
   </bean>
   
   <bean name="/asyncserviceapp/getdetails" class="org.async.service.app.cloud.app.GetDetails">
   		<depends>services://MobileObjectMonitor</depends>
   		<depends>services://MobileServiceMonitor</depends>
   </bean>
</deployment>
```

#### Step 3: Invoke the service bean via the RPC service ####
```
Request request = new Request("/asyncserviceapp/getdetails");	
			request.setAttribute("oid", email.getOid());
			
			Response response = new MobileService().invoke(request);
			
			//Process the Cloud Response
			String oid = response.getAttribute("oid");
			String from = response.getAttribute("from");
			String to = response.getAttribute("to");
			String subject = response.getAttribute("subject");
			String date = response.getAttribute("date");

```

Create a _org.openmobster.core.mobileCloud.api.service.Request_ object and populate it with attributes to be sent during the RPC call.

Make the invocation via _org.openmobster.core.mobileCloud.api.service.MobileService_.

Process the _org.openmobster.core.mobileCloud.api.service.Response_ received from making the RPC call

#### Download this tutorial ####
  * [Download](http://openmobster.googlecode.com/svn/samples/asyncserviceapp.zip)

  * Install the Android App from the **app-android** dir: **mvn -Phot-deploy install**

  * Run the Cloud from the **cloud** directory: **mvn -PrunCloud integration-test**

Note: You must Activate with the Cloud using the DevCloud App installed via the **mvn -Phot-deploy install** command.