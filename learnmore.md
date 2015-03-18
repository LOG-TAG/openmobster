**Related Links:** [Get Started](GetStartedLatest.md)

### Introduction ###
**OpenMobster** is an open source platform for integrating Mobile Apps (on-device apps) with _Cloud_ services. Its goal is to provide the infrastructure for the Mobile App developer
to productively build apps that consume information that resides in the Cloud. It provides seamless application state/data synchronization, Push notifications,
consistent server-side API/frameworks to expose Cloud services, and consistent device-side API/frameworks to productively develop native mobile apps across multiple platforms.


---


### What services are provided by the platform? ###

#### Enterprise Sync Platform ####

Cloud data is made available inside an App's _local storage_. This allows the App
to function seamlessly in both _online_ as well as _offline_ modes. The data is automatically synchronized with the Cloud service based on local state changes. These state changes are auto detected and does not require any device-side sync-related programming on the part of the developer.


#### Push Notifications ####

App state changes are proactively pushed to an App from the Cloud server. The _Push_ mechanism uses pure network/socket based approach instead of clunky methodologies like sending sms alerts or email alerts. The Push notifications happen inside the App's execution environment.


#### Simple Mobile RPC (Remote Procedure Call) ####

Exposes your server-side coarse grained business services. These services are invoked via a simple RPC mechanism without any low-level programming like http-client code, client side REST library, etc on the part of the App developer. There is a RPC API that is used for making these calls.


#### Management Console ####

A _Management Console_ is provided to administrate the Cloud Server. It provides security, and account provisioning features. Over time the _Management Console_ will carry device management features like remote wipe, remote tracking, remote lock-down, etc.


---


### What server-side frameworks are provided? ###

#### Channel Framework ####

A **Channel** serves as a gateway for integrating on-device model/data objects with the server-side backend storage systems such as relational databases, content repositories, or Enterprise systems like CRMs, ERPs etc. It provides a simple CRUD (Create, Read, Update, and Delete) interface to expose the backend data. The Channel is specifically designed such that the Developer does not have to worry about any low-level state management, synchronization, or other mobile-oriented issues. The idea is to keep a Channel a purely data-oriented component.

#### Service Framework ####

The **Service Framework** is used to expose coarse grained business processes to the Mobile App.


---


### What device-side frameworks are provided? ###

#### Mobile Data Framework ####

The **Mobile Data Framework** provides Cloud data-oriented services like seamless offline data synchronization, Push notifications, and simple RPC (Remote Procedure Call) mechanism.


---


### What mobile platforms are supported? ###

The project is evolving at a rapid pace. The platform supports Android (API Level 11 and above), and iOS (version 5 and higher)
More details can be found on our [project roadmap](roadmap.md).


---


### Ready to write your first Mobile Cloud App? ###
[Download](http://code.google.com/p/openmobster/downloads/list) the latest stable release.