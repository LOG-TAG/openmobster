**Related Links:** [iPhone Sync App](iPhoneSyncApp.md)


---


### Introduction ###

This tutorial is to guide you through the process of running the iPhone Sample App with the OpenMobster Cloud Server.

### Getting the Showcase App ###

  * You can find this App under the **SampleApps** directory under the OpenMobster distribution

### Running the Cloud Server ###

  * Go To _cloud_ directory: **cd cloud**

  * Run the Cloud Server: **mvn -PrunCloud integration-test**

### Compiling the mobilecloudlib Xcode Project ###

  * Open the mobilecloudlib xcode project by opening: **mobilecloudlib/mobilecloudlib.xcodeproj**

  * Build the project in Xcode

### Build/Run the SampleApp ###

  * Open the SampleApp project by opening: iphone/SampleApp/SampleApp.xcodeproj

  * Build/Run the project in Xcode

  * The iPhone Simulator must start and launch the App

### Notes ###

If the App launches with a Device Activation screen go ahead with the Activation wizard. The values to use are:

  * **Login:** blah2@gmail.com

  * **Password:** blahblah2

  * **IP :** IP address of Cloud Server. Usually a 192.168.1.x adrress. **localhost** will not work

  * **Port:** 1502 (default)

For security purposes, the device must provisioned before using the Cloud services.


---


**Related Links:** [iPhone Sync App](iPhoneSyncApp.md)