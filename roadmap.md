**Related Links:** [Get Started](GetStarted.md)


---


Timelines are difficult to anticipate, but here is a rolling list of features that we would like to deliver. This should give some vision about the direction of the project. As the project progresses, this roadmap is bound to be adjusted.

### Release 2.0 ###

#### Device Side ####
  * Blackberry OS version 4.3.0 and above

  * Google Android (baseline version not decided yet). Have been testdriving the 2.0 version of the platform

  * Connection Module
    * Socket Management
    * Server Side Push Notifications

  * Bus Module
    * Inter-App communication

  * Sync Module
    * Seamless App State/Data Synchronization
    * Support for all sync modes two-way, oneway-server, oneway-device, bootsync

  * MobileObject Module
    * Data Serialization/Deserialization
    * Object Storage
    * Object Query Facilities
    * Support a generic "MobileBean" that mirrors its corresponding POJO/Domain object on the Cloud

  * Storage Module
    * Abstracts on-device storage services

  * Moblet Data Framework

  * Moblet MVC Framework

  * CloudManager App

#### Cloud Side ####
  * Synchronization Engine

  * Comet-Style Push Engine

  * Security Services
    * Device Authorization
    * User Account Provisioning
    * Device Provisioning

  * App Store Engine

  * A Command Line Management Console

  * Channel Development Framework

  * Service Development Framework


---


### Release 2.2 - (Release Candidate 1 just released) ###

#### Device Side ####
  * iPhone Support

#### Cloud Side ####

  * A GWT-based Management Console

  * A Javascript API/Bridge for the Sync and the RPC (Remote Procedure Call) services. This will help integrate with Apps written in html,css, and Javascript

  * Push based programming via a cross platform API. Same API for pushing to both Android and iPhone devices.

  * Integration with Apple Push Notification Service

  * Sync Channel Encryption

  * Integration with Appcelerator to develop cross platform Cloud Apps using html, css, and javascript

  * Location Based Programming Framework

  * Remote Wipe and Remote Lock from the Management Console

  * A Device to Device Push Framework. Allows for two way communication between two mobile devices from your App.

  * Integration with PhoneGap to build Hybrid Cloud Apps


---


### Release 2.4 (Production Quality) ###
  * Windows Phone 8 Support
  * Support for Hybrid Native Apps based on HTML5
  * Expand PhoneGap Support to more modules in addition to the already supported Sync Plugin
  * Upgrade to JBoss 7
  * Clustering
  * BlackBerry 10 Support
  * Remove all dependency on the CloudManager App. This App is not even needed to be installed on the device. This has been transitioned into a DeviceManagement App for doing Remote Lock and Remote Wipe
  * More iOS Sample Apps based on Push Notifications
  * Integrate SQLCipher to provide an Encrypted Database Option for Android
  * Make conflict resolution more robust especially by getting the user involved
  * Expose a REST-API for the Push service. This will allow applications not installed within the JBoss service to use the Push Notification service
  * Improve the Android Enterprise App Store Workflow
  * [Security Integration Framework](https://code.google.com/p/openmobster/issues/detail?id=161&can=1&q=label%3A2.4-M4)
  * Develop the SalesForce.com SDK. This will provide the infrastructure needed to mobilize an Enterprise's SalesForce.com data to their Mobile Apps. The SDK will be provided Free and Open Source


---


### Release 2.4-M4 tasks (Planning Stage) ###
  * Support for Horizontal Scaling via integration with the AWS Elastic Load Balancer
  * Add Support for Streaming Binary Content to the Sync Engine
  * Upgrade to JBoss 7
  * More iOS7 based Sample Apps with Push Notifications
  * Integrate SQLCipher to provide an Encrypted Database Option for Android
  * Develop the SalesForce.com SDK. This will provide the infrastructure needed to mobilize an Enterprise's SalesForce.com data to their Mobile Apps. The SDK will be provided Free and Open Source


---


### Release 2.4-M5 tasks (Planning Stage) ###

  * Add Support for Windows Phone 8 SDK


---

**Related Links:** [Get Started](GetStarted.md)