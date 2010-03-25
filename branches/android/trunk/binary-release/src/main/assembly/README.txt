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

The supplied binaries are unsigned and will work perfectly end-to-end with the RIM Simulation Environment.

In order to work on an actual device, the binaries must be signed using the signing process mandated by RIM.

Further details about the signing process are available at: http://na.blackberry.com/eng/developers/javaappdev/codekeys.jsp

Future releases of OpenMobster will include signed binaries out-of-the-box so this step will not be necessary anymore.


*********************
Troubleshooting     *
*********************