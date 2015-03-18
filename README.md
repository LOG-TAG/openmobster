# openmobster
Automatically exported from code.google.com/p/openmobster


http://code.google.com/p/openmobster/

![alt tag](http://openmobster.googlecode.com/svn/wiki/content/app-developer-guide/html/images/architecture.png)


Introduction

OpenMobster is an open source platform for integrating Mobile Apps (on-device apps) with Cloud services. Its goal is to provide the infrastructure for the Mobile App developer to productively build apps that consume information that resides in the Cloud. It provides seamless application state/data synchronization, Push notifications, consistent server-side API/frameworks to expose Cloud services, and consistent device-side API/frameworks to productively develop native mobile apps across multiple platforms.
What services are provided by the platform?
Enterprise Sync Platform

Cloud data is made available inside an App's local storage. This allows the App to function seamlessly in both online as well as offline modes. The data is automatically synchronized with the Cloud service based on local state changes. These state changes are auto detected and does not require any device-side sync-related programming on the part of the developer.
Push Notifications

App state changes are proactively pushed to an App from the Cloud server. The Push mechanism uses pure network/socket based approach instead of clunky methodologies like sending sms alerts or email alerts. The Push notifications happen inside the App's execution environment.

=====================================================
 GetStartedLatest  
OpenMobster, Mobile Backend as a Service Platform
Updated Dec 27, 2013 by openmobs...@gmail.com
System Requirements

    Java SE JDK v6.0 

    Apache Maven 3.0.4 

    Android SDK 

    OS can be Mac OSX, Windows, or Linux. (Tested on Mac OSX, and Linux) 

Mobile Platforms Supported

    Android (API Level 11 and higher) 

    iOS (version 5 and higher) 

    Windows 8 (On the roadmap. SDK not yet implemented) 

Enviroment Variables Setup

ANDROID_HOME=[Directory where Android SDK is installed]

//Append this value to the PATH variable
[Directory where Android SDK is installed]/platform-tools

Download

Latest Download
Build OpenMobster Source Code

Build Instructions
CRUD Sample App in Action

Instructions
AppCreator

Start writing your own App

 CRUDSampleApp  
Running the CRUD Sample App
Updated Dec 26, 2013 by openmobs...@gmail.com
Introduction

In the spirit of confidence building when using a new technology, lets start with seeing a sample app in action. We will call it the CRUD (Create/Read/Update/Delete) App.
System Requirements
Cloud Server

    Java SE JDK v6.0
    JBoss AS version 5.1.0.GA 

Android Device

    API Level 11 and higher
    2 Devices 

Installation
Download OpenMobster

    Download OpenMobster Binary Distribution
    Unzip the distribution
    Check the Cloud Server is found under cloudServer/hsqldb and cloudServer/mysql5
    Check that the CRUD Sample App is found under: Samples/CRUD.apk 

Install Cloud Server

    Step 1: Download and install JBoss-5.1.0.GA from here
    Step 2: Copy cloudServer/hsqdb/openmobster to the JBoss AS server directory. cloudServer/hsqldb/openmobster is a pre-configured/optimized instance for the OpenMobster Cloud Server
    Step 3: Start the JBoss AS instance with the OpenMobster binary installed using: run -c openmobster -b "a real IP address" 

Note: A real IP address like "192.168.0.1" or something like that is needed for the mobile device to be able to connect to the server. Even in the case of a simulation environment, the device's browser does not connect to a loopback address like "localhost" or "127.0.0.1"

    Step 5: Verify the Cloud Server installation by typing in: http://{cloudserverIp}/o 

Install CRUD App on 2 devices

    Step 1: Locate the CRUD App in the OpenMobster distribution under: Samples/CRUD.apk
    Step 2: Install the CRUD App on the device connected to your computer using: adb install -r CRUD.apk
    Step 3: Repeat Step 2 on the other device as well 

App Activation

For security purposes, every OpenMobster based App must be first activated successfully with the Cloud Server. When the CRUD App is started on each device, it brings up the App Activation Dialog. You must provide the following information:

    Server: The IP or Domain name of the Cloud Server
    Port: Port of the Cloud Server. [Default: 1502]
    Email: The email address you want to associate to uniquely identify you with the system
    Password: A password to be used for authentication with the system 

In case of two devices, you will do an activation on both devices. You can use the same email address on both devices, or you can input a different email address for each device. Either way, your data will be replicated across both devices.
CRUD App Features

Here is a list of what to expect from the CRUD App functionality

    Enteprise Data Sync: On the Cloud Side, data stored in a HSQDLB database is mobilized and synced with the App. All the CRUD (Create/Read/Update/Delete) operations on the App data is synced back to the Cloud database.
    Enteprise Data Push: In case of multi-device access, data changes made on one device are automatically and silently pushed to other devices.
    Offline Workflow Support: The App Data is fully available even when a network connection is not available between the device and the Cloud Server. All the CRUD (Create/Read/Update/Delete) functions are available when the device is offline. In case, the device is disconnected, the Sync Engine queues all data changes to be synchronized back with the Cloud database as soon as a network connection is established with the Cloud Server. 

CRUD App Help

    The data was pushed from one device to another, but the new data is not showing up?

        This is a screen refresh issue. The App does not refresh the screen automatically to keep the coding simple to follow. You can refresh your screen manually from the Option in the App Menu. 

    How do I 'Create' a new ticket?

        The "New Ticket" option is available in the App Menu. 

    How do I 'Update' a ticket?

        The "Update" option is available when you click the ticket to be updated in the list 

    How do I 'Delete' a ticket?

        The "Delete" option is available when you click the ticket to be deleted from the list 



Simple Mobile RPC (Remote Procedure Call)

Exposes your server-side coarse grained business services. These services are invoked via a simple RPC mechanism without any low-level programming like http-client code, client side REST library, etc on the part of the App developer. There is a RPC API that is used for making these calls.
Management Console

A Management Console is provided to administrate the Cloud Server. It provides security, and account provisioning features. Over time the Management Console will carry device management features like remote wipe, remote tracking, remote lock-down, etc.
What server-side frameworks are provided?
Channel Framework

A Channel serves as a gateway for integrating on-device model/data objects with the server-side backend storage systems such as relational databases, content repositories, or Enterprise systems like CRMs, ERPs etc. It provides a simple CRUD (Create, Read, Update, and Delete) interface to expose the backend data. The Channel is specifically designed such that the Developer does not have to worry about any low-level state management, synchronization, or other mobile-oriented issues. The idea is to keep a Channel a purely data-oriented component.
Service Framework

The Service Framework is used to expose coarse grained business processes to the Mobile App.
What device-side frameworks are provided?
Mobile Data Framework

The Mobile Data Framework provides Cloud data-oriented services like seamless offline data synchronization, Push notifications, and simple RPC (Remote Procedure Call) mechanism.
What mobile platforms are supported?

The project is evolving at a rapid pace. The platform supports Android (API Level 11 and above), and iOS (version 5 and higher) More details can be found on our project roadmap.
Ready to write your first Mobile Cloud App?

==============================================================================================

 BuildOpenMobster  
Building OpenMobster from Source Code
Updated Dec 26, 2013 by openmobs...@gmail.com
Prepare Environment

1) Checkout the OpenMobster source code to your favorite working directory

  svn checkout http://openmobster.googlecode.com/svn/branches/2.4-SNAPSHOT openmobster-read-only

2) Install Google Web Toolkit (GWT - I'm using 2.4.0)

  Download from: http://code.google.com/webtoolkit/gettingstarted.html
  (Just unzip it and point to it with the GWT_HOME env variable outlined below)

3) Install the Android SDK

  Download from: http://developer.android.com/sdk/index.html
  Follow the installation instructions

4) Install Maven Build Tool

  Download from: http://archive.apache.org/dist/maven/binaries/apache-maven-3.0.4-bin.zip
  Extract the zip file into a directory you will designate as MAVEN_HOME

5) Add the following values to your Environment Variables

MAVEN_HOME=[Directory where you unzipped Maven]
ANDROID_HOME=[Directory where Android SDK is installed]
MAVEN_OPTS=-Xmx512m -XX:MaxPermSize=128m
GWT_HOME=[Directory where you installed GWT]

//Append these values to the PATH variable
[Directory where you unzipped Maven]/bin;[Directory where Android SDK is installed]/platform-tools

6) Check that maven was totally switched over (you should see something like the following)

  root@vworkz:~#  mvn -version
  Apache Maven 3.0.4 (r1232337; 2012-01-17 03:44:56-0500)
  Maven home: /usr/local/apache-maven-3.0.4
  Java version: 1.6.0_20, vendor: Sun Microsystems Inc.
  Java home: /usr/lib/jvm/java-6-openjdk/jre
  Default locale: en_US, platform encoding: UTF-8
  OS name: "linux", version: "2.6.32-33-server", arch: "amd64", family: "unix"

The environment should now be ready to build OpenMobster!

1) OK, let's build the whole enchilada (grab a coffee)

  cd ~/openmobster-read-only/cloud
  mvn -DskipTests -Pbuild-all install
  (Build should be SUCCESSFUL)


