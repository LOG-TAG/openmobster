									*************************************************************
									* OpenMobster - Mobile Cloud Platform (version 2.2-SNAPSHOT)*
									*************************************************************
									
									
***********************									
Component Description:*
***********************
src - The source code for the PhoneGap Plugins and Sample Apps

bin - The binary artifacts for integrating OpenMobster service with PhoneGap App

Samples - Code samples. Includes both the PhoneGap App and the Cloud runtime to run the Apps end-to-end.



*************************
Running the Samples     *
*************************

JQuery/PhoneGap/OpenMobster based Offline App
----------------------------------------------

Step 1: Before you run the App, make sure the CloudManager App is installed and Activated with the Cloud.
Go to the Android/core-runtime directory at the top of the distribution
Install the CloudManager App on the device: adb install -r CloudManager.apk

Once, the Cloud Manager App is installed, use the "Activate" function to activate your device with the Cloud. For more details please see:
http://code.google.com/p/openmobster/wiki/WhatNext


Step 2: Install the "JQueryOfflineApp.apk"
Install the App on the device: adb install -r JQueryOfflineApp.apk

Step 3: Run the Cloud Server
Go to plugin-jquery-cloud directory
Run the Cloud Server: mvn -PrunCloud integration-test
