## Introduction ##
In the spirit of confidence building when using a new technology, lets start with seeing a sample app in action. We will call it the CRUD (Create/Read/Update/Delete) App.


---

## System Requirements ##

#### Cloud Server ####

  * Java SE JDK v6.0
  * JBoss AS version 5.1.0.GA


#### Android Device ####

  * API Level 11 and higher
  * 2 Devices

---


## Installation ##

#### Download OpenMobster ####

  * Download [OpenMobster Binary Distribution](https://code.google.com/p/openmobster/downloads/list)
  * Unzip the distribution
  * Check the Cloud Server is found under **cloudServer/hsqldb** and **cloudServer/mysql5**
  * Check that the CRUD Sample App is found under: **Samples/CRUD.apk**

#### Install Cloud Server ####

  * **Step 1:** Download and install JBoss-5.1.0.GA from [here](http://www.jboss.org/jbossas/downloads/)
  * **Step 2:** Copy **cloudServer/hsqdb/openmobster** to the JBoss AS server directory. **cloudServer/hsqldb/openmobster** is a pre-configured/optimized instance for the OpenMobster Cloud Server
  * **Step 3:** Start the JBoss AS instance with the OpenMobster binary installed using: **run -c openmobster -b "a real IP address"**
Note: A real IP address like "192.168.0.1" or something like that is needed for the mobile device to be able to connect to the server. Even in the case of a simulation environment, the device's browser does not connect to a loopback address like "localhost" or "127.0.0.1"
  * **Step 5:** Verify the Cloud Server installation by typing in: http://{cloudserverIp}/o

#### Install CRUD App on 2 devices ####

  * **Step 1:** Locate the CRUD App in the OpenMobster distribution under: **Samples/CRUD.apk**
  * **Step 2:** Install the CRUD App on the device connected to your computer using: **adb install -r CRUD.apk**
  * **Step 3:** Repeat Step 2 on the other device as well


---


## App Activation ##

For security purposes, every OpenMobster based App must be first activated successfully with the Cloud Server. When the CRUD App is started on each device, it brings up the App Activation Dialog. You must provide the following information:

  * **Server:** The IP or Domain name of the Cloud Server
  * **Port:** Port of the Cloud Server. **[Default: 1502]**
  * **Email:** The email address you want to associate to uniquely identify you with the system
  * **Password:** A password to be used for authentication with the system

In case of two devices, you will do an activation on both devices. You can use the same email address on both devices, or you can input a different email address for each device. Either way, your data will be replicated across both devices.


---


## CRUD App Features ##

Here is a list of what to expect from the CRUD App functionality

  * **Enteprise Data Sync:** On the Cloud Side, data stored in a HSQDLB database is mobilized and synced with the App. All the CRUD (Create/Read/Update/Delete) operations on the App data is synced back to the Cloud database.
  * **Enteprise Data Push:** In case of multi-device access, data changes made on one device are automatically and silently pushed to other devices.
  * **Offline Workflow Support:** The App Data is fully available even when a network connection is not available between the device and the Cloud Server. All the CRUD (Create/Read/Update/Delete) functions are available when the device is offline. In case, the device is disconnected, the Sync Engine queues all data changes to be synchronized back with the Cloud database as soon as a network connection is established with the Cloud Server.


---


## CRUD App Help ##

  * The data was pushed from one device to another, but the new data is not showing up?
> > This is a screen refresh issue. The App does not refresh the screen automatically to keep the coding simple to follow. You can refresh your screen manually from the Option in the App Menu.

  * How do I 'Create' a new ticket?
> > The "New Ticket" option is available in the App Menu.

  * How do I 'Update' a ticket?
> > The "Update" option is available when you click the ticket to be updated in the list

  * How do I 'Delete' a ticket?
> > The "Delete" option is available when you click the ticket to be deleted from the list