									*************************************************************
									* OpenMobster - Mobile Cloud Platform (version 2.2-SNAPSHOT)*
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

Install the "MobileCloud" binaries, (CloudManager.apk) on the actual device or device simulator.
Fire up the device browser and goto: http://<cloudserverip>/o/android/cloudmanager

Step 4:
-------------

Activate your device "real" or "simulator" using the "CloudManager" app. Use the account created in Step 2.

Step 5:
-----------

Install the "sample applications" via the "CloudManager" Corporate App Store option


Step 6:
-----------

Welcome to the OpenMobster Community. Your are officially a "Mobster" ;)
