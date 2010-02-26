									*************************************************************
									* OpenMobster - Mobile Cloud Platform (version 2.0-snapshot)*
									*************************************************************
									
									
***********									
Overview  *
***********


This component consists of Cloud infrastructure runtimes that must be installed on each device, before the device can integrate with
the "Cloud Server". Think of them as bootstrap binaries.

Each mobile platform will have its corresponding set of artifacts that must be first installed on the device. 


The current version consists of Blackberry RIM OS 4.3.0+ support

*************************
Blackberry installation *
*************************

Over-the-Air (OTA installation)
--------------------------------------
If you have a Cloud Server running and accessible from the Internet, you can simply download these binaries from the built-in browser
of the device.

Download the following and follow the instructions provided by the device:

* http://<cloudServer IP>:<cloudServer port>/o/apps/rimos/430/MobileCloud.jad

* http://<cloudServer IP>:<cloudServer port>/o/apps/rimos/430/CloudManager.jad


Desktop Sync Installation
-----------------------------------

If you want to install it via desktop synchronization, just install the following 'alx' files.

* blackberry-4.3.0/bin/MobileCloud/MobileCloud.alx
* blackberry-4.3.0/bin/CloudManager/CloudManager.alx


Device Activation
-----------------------------------

Step 1: Launch the 'CloudManager' app.

Step 2: Select the 'Activate' function.

Step 3: Follow the wizard and provide the appropriate values

* Server : IP Address of the Cloud Server
* Email : The user id that was used to register the user via the Management Console
* Password: The password that was used to register the user via the Management Console



Device-Side Installation of the Sample Applications:
------------------------------------------------------

Using your "CloudManager App", install the deployed Moblets/Apps using the "App Store" function 



Using the "App Store" function on a Device Simulator:
--------------------------------------------------------

Note: This applies only in a simulator setup. The browser on the Simulator needs an "MDS" service to connect to the internet.
This is provided within the RIM SDK under the "RIM_JDE_HOME/MDS" directory. In a real world scenario, the MDS service is provided seamlessly
by your carrier along with your data plan

The following things should be kept in mind on a Simulator:

* The OpenMobster Cloud Server must be bound to a real IP address and not 127.0.0.1 or localhost. You can do this with the following
JBoss AS command:
run.bat -c openmobster -b 192.168.1.107

* The MD5 service should be running. This is done by running the RIM_JDE_HOME/MDS/run.bat

* When you activate your device using the "CloudManager App", input 192.168.1.107 as the IP address of the cloud server

Note: 192.168.1.107 is used as an example. Your would be something else. You can find it by "ipconfig" command on the DOS prompt.




********************
Important Note:    *
********************
RIM OS Binary Signing:
-------------------------
This 2.0-snapshot release supports the Blackberry RIM OS : 4.3.0+ devices.

Blackberry has a selected set of APIs which require "signing" the binary (.cod) before installing it on the device. The OpenMobster
binaries makes use of these APIs, and hence requires "signing" before installing it on the device. For more information about the
"signing" process: http://na.blackberry.com/eng/developers/javaappdev/codekeys.jsp
	   		 	
Code Signing is not required for installing/testing Apps in a simulation environment. In fact all the OpenMobster binaries have been
thoroughly tested on a 4.3.0 OS simulator. All tests have passed without any errors, warnings, or crashes.   		 	
	   		
At the time of this first community release, the OpenMobster Blackberry binaries have not been signed or tested on an actual device.
However, the developer can sign and distribute these binaries along with their Apps, if they
possess their own signing key. In the future, the binaries should come signed with the keys issued by RIM to OpenMobster Community 
	   		