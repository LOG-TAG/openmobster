									*************************************************************
									* OpenMobster - Mobile Cloud Platform (version 2.0-snapshot)*
									*************************************************************

********************									
Project Layout:    *
********************									

Each generated project has the following three maven modules:

* app-rimos - Contains the device side application code for the Blackberry OS - version 4.3.0 and higher

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

This command builds all the 3 artifacts. 


**********************************************									
Developer Productivity Improvement:          *
**********************************************
To improve development productivity "mvn package or mvn install" command installs the required "OpenMobster MobileCloud" 
into the specified Blackeberry Simulator. This binary improves developer productivity by automating the manual provisioning 
processes by provisioning a Cloud account under the name: "blah2@gmail.com".

Note: This is a strict development stage only optimization and should not be used in a real world setting.

The location of the simulator is specified in the "RIM_JDE_HOME" environment variable.



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
