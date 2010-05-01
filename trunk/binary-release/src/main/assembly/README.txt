									*************************************************************
									* OpenMobster - Mobile Cloud Platform (version 2.0-snapshot)*
									*************************************************************
									
									
***********************									
Component Description:*
***********************
cloudServer - Contains the Cloud Server binaries. Supports hsqldb and mysql5

cloudConsole - Contains a command line "Management Console"

mobileCloud - Contains the binaries to be installed on the device. Supports Blackberry RIM OS version 4.3.0+
Note: Other platforms like Google Android, Apple iPhone, Palm WebOS, Symbian, and Windows Mobile are on the project roadmap

AppCreator - A Maven-based Mobile App Development Tool along with some sample apps

docs - App Developer Guide and API documentation

src - Source Code of the entire project 


*************************
Quick Start:            *
*************************
Each Component has detailed Installation instructions in README.txt files. Perform the component installations in the following order:

Step 1:
----------

Install and Run the "cloudServer" instance on the JBoss AS 5.1.0.GA server. Details are in: "cloudServer/README.txt"


Step 2:
-----------

Provision an account using the cloudConsole "Management Console". Details are in: "cloudConsole/README.txt"

Step 3:
------------

Install the "MobileCloud" binaries, (CloudManager and MobileCloud) on the actual device or device simulator. Details are in: "mobileCloud/README.txt"

Step 4:
-------------

Activate your device "real" or "simulator" using the "CloudManager" app. Use the account created in Step 2.

Step 5:
-----------

Install the "sample applications" via the "CloudManager" App Store


Step 6:
-----------

Welcome to the OpenMobster Community. Your are officially a "Mobster" ;)


**********************
Note:                *
**********************

RIM OS Binary Signing:
-------------------------
This 2.0-snapshot release supports the Blackberry RIM OS : 4.3.0+ devices.

Blackberry has a selected set of APIs which require "signing" the binary (.cod) before installing it on the device. The OpenMobster
binaries makes use of these APIs, and hence requires "signing" before installing it on the device. For more information about the
"signing" process: http://na.blackberry.com/eng/developers/javaappdev/codekeys.jsp
	   		 	
Code Signing is not required for installing/testing Apps in a simulation environment. In fact all the OpenMobster binaries have been
thoroughly tested on a 4.3.0 OS simulator. All tests have passed without any errors, warnings, or crashes.   		 	
	   		
As of 2.0-M3 community release, all OpenMobster Blackberry binaries have been signed and tested on an actual BlackBerry device. 
The device used was the BlackBerry Curve 8330 running on the Sprint CDMA network.


*********************
Troubleshooting     *
*********************