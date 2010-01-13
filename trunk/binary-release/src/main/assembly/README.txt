									*************************************************************
									* OpenMobster - Mobile Cloud Platform (version 2.0-M1)      *
									*************************************************************
									
									
***********************									
Component Description:*
***********************
cloudServer - Cloud Server binaries. Supports hsqldb and mysql5

cloudConsole - A command line "Management Console"

mobileCloud - Binaries to be installed on the device. Supports Blackberry RIM OS version 4.3.0+
Note: Other platforms like Google Android, Apple iPhone, Palm WebOS, Symbian, and Windows Mobile are on the project roadmap

examples - A couple of examples to demonstrate some mobile programming concepts

docs - API docs and an App Developer Guide

src - Source Code of the entire project


*************************
Getting Started:        *
*************************
Each Component has detailed Installation instructions in README.txt files. Perform the component installations in the following order:

Step 1:
----------

Install and Run the cloudServer instance


Step 2:
-----------

Provision an account using the cloudConsole "Management Console". Some usage instructions are in cloudConsole/README.txt

Step 3:
------------

Install the "mobileCloud" binaries on the actual device or device simulator

Step 4:
-------------

Activate your device "real" or "simulator" using the "cloudManager" app. Use the account created in Step 2.

Step 5:
------------

Install the "examples" on the "cloudServer" instance

Step 6:
-----------

Install these "examples" via the "cloudManager" App Store

Step 7:
-----------

Welcome to the OpenMobster Community. Your are officially a "Mobster" ;)

**********************
Note:                *
**********************

RIM OS Binary Signing:
-------------------------
This 2.0-M1 release supports the Blackberry RIM OS : 4.3.0+ devices.

The supplied binaries are unsigned and will work perfectly end-to-end with the RIM Simulation Environment.

In order to work on an actual device, the binaries must be signed using the signing process mandated by RIM.

Further details about the signing process are available at: http://na.blackberry.com/eng/developers/javaappdev/codekeys.jsp

Future releases of OpenMobster will include signed binaries out-of-the-box so this step will not be necessary anymore.


*********************
Troubleshooting     *
*********************

