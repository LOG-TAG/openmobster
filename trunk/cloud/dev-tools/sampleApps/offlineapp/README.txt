									*************************************************************
									* OpenMobster - Mobile Cloud Platform
									*************************************************************

********************									
Project Layout:    *
********************									

Each generated project has the following 4 maven modules:

* app-android - Contains the App for the Android OS - version 2.0 and higher

* app-rimos - Contains the App for the Blackberry OS - version 4.3.0 and higher

* cloud - Contains the "OpenMobster Cloud Server" based artifacts which will be deployed on the server side

* moblet - Represents a "OpenMobster Moblet" which combines both the device side and server side artifacts into one single
artifact. The moblet is deployed as a simple jar file into the "OpenMobster Cloud Server". When the moblet is deployed into the Cloud
server it is registered with the built-in App store. Once registered with the App Store, this moblet can be easily downloaded, installed, and
managed on the actual device via the "App Store" functionality under the "Cloud Manager" app 


****************************									
Helpful Development Tips:  *
****************************

Build All the artifacts:
------------------------------
mvn install

This command builds all the artifacts. 


Android Device/Emulator Deployment
------------------------------------
mvn -Phot-deploy install

This installs the Offline App as well as the 'development mode' CloudManager App

This 'development mode' CloudManager App improves developer productivity by automating the manual provisioning 
processes by automatically provisioning a Cloud account under the name: "blah2@gmail.com".

Note: This is a strict development stage only optimization and should not be used in a production setting.



**********************************************									
Standalone "Development Mode" Cloud Server:  *
**********************************************
On the Cloud-side of things, there is a fully functional Standalone "Development Mode" Cloud Server provided 
that you can run right from inside your Maven environment.

Command to run the standalone "Development Mode" Cloud Server:
---------------------------------------------------------------
mvn -PrunCloud integration-test


Command to run the standalone "Development Mode" Cloud Server in *debug mode*:
-------------------------------------------------------------------------------
mvn -PdebugCloud integration-test
