### System Requirements ###

  * Java SE, JDK 6

  * Apache Maven 2.2.1

  * XCode with iPhone4 SDK

  * SVN client to checkout the source code

### Getting the source code ###

  * svn co https://openmobster.googlecode.com/svn/trunk

### Building the source code ###

  * cd cloud

  * mvn -DskipTests -Pbuild-all install

### Open iPhone XCode projects ###

  * Open **mobilecloudlib** by opening **iphone/mobilecloudlib/mobilecloudlib.xcodeproj**

  * Open **CloudManager** by opening **iphone/CloudManager/CloudManager.xcodeproj**

  * On the above projects, do a **Clean All Targets**, and **Build All**

### Start the Cloud Server ###

  * cd cloud/dev-tools/sampleApps/showcase/cloud

  * mvn -PrunCloud integration-test

### Start the Showcase App on iPhone Simulator ###

  * **Build and Run** on the Cloud Manager App from XCode

### What to Expect? ###

When the App is launched, it should first launch with the **Provisiong Dialog". This is needed, as it will provision the device with the Cloud Server before the Showcase is accessible. Here are the values that should be used**

  * Email : blah2@gmail.com
  * Password: blahblah2
  * Cloud IP: IP address where the Cloud Server is running
  * Cloud Port: 1502 (Default Port, should be pre-populated)

Once the provisiong is done, the CRUD Showcase is self-explanatory