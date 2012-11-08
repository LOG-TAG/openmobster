									*************************************************************
									* OpenMobster - Mobile Cloud Platform
									*************************************************************
									
									
***********************									
Component Description:*
***********************
cloudServer - Contains the Cloud Server binaries. Supports hsqldb and mysql5

console - Contains the GWT/SmartGWT based "Management Console"

Android - Contains the binaries to be installed on the Android Platform.

iPhone - Contains the XCode projects used to compile and create a Showcase App. 'mobilecloudlib' is the static library implementing the OpenMobster infrastructure. 
'CloudManager' is the Showcase App.

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
------------

Install the "MobileCloud" binaries, (CloudManager.apk) on the actual device or device simulator.
Fire up the device browser and goto: http://<cloudserverip>/o/android/cloudmanager

Step 3:
-------------

Activate your device "real" or "simulator" using the "CloudManager" app.

Step 4:
-----------

Install the "sample applications" via the "CloudManager" Corporate App Store option


Step 5:
-----------

Welcome to the OpenMobster Community. Your are officially a "Mobster" ;)
