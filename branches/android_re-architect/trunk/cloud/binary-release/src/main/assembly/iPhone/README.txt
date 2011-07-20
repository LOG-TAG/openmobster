									*************************************************************
									* OpenMobster - Mobile Cloud Platform (version 2.2-SNAPSHOT)*
									*************************************************************
									
									
mobilecloudlib: Contains the XCode project for OpenMobster infrastructure. This is a static library to be linked with Apps wanting to use OpenMobster services.

CloudManager: Contains the XCode project for the Showcase App. It demonstrates sync and rpc functionality in addition to others.

ActivationApp: Contains the XCode project for a Management App. It is not required to install this app to be functional, but it helps to make sure the Device is activated
with the OpenMobster cloud

showcase: Contains the Cloud side server to use with the Showcase App.
____________________________________________________________________________________________________________________________________________________________________

How to use the Showcase App?
________________________________

Step 1: Open the 'mobilecloudlib' XCode project and Build it.

Step 2: Open the 'CloudManager' XCode project and Build it.

Step 3: Start the Showcase App Cloud Server.
mvn -PrunCloud integration-test

Step 4: Build/Run the 'CloudManager' App. It should startup in a Simulator.

You will need to Activate your device with the Cloud before the App can be used. This is just a one time activation.
