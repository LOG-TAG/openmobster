## Start Writing your Own App ##

In your binary distribution, go to the directory **AppCreator**. Inside the directory, use a tool called the _appcreator.bat_ (Windows), _appcreator.sh_ (Linux and Mac) to generate a skeleton project.

```
appcreator.bat
```

This will generate a skeleton for the Mobile App. Each generated project has the following modules:

  * **cloud:** Contains the src for the Cloud-side components. Java code is located under **src/main/java**, and configuration is located under **src/main/resources**.

  * **app-android:** Contains the src for the Android App. Java code is located under **src**, Configuration is located under **src/openmobster-app.xml**. Besides the OpenMobster component setup, the Android SDK specific setup is located under **AndroidManifest.xml**. This is an Eclipse ADT project and can be smoothly imported into your Eclipse Workspace.


---


## app-android ##

  * **Step 1:** Import the Android Project stored under "app-android" into your Eclipse workspace. Make sure you select, **Import > Existing Android Code into Workspace**. Rest of the instructions should be self-explanatory

  * **Step 2:** Run As Android Application from Eclipse. This will compile and install the Android App on the connected device or simulator.


---


## cloud ##

On the Cloud-side of things, there is a fully functional Standalone "Development Mode" Cloud Server provided that you can run right inside your Maven environment. Here are some of the Maven commands that are used during development

  * Build All including testsuite execution
```
          mvn clean install
```

  * Command to run the standalone "Development Mode" Cloud Server
```
         mvn -PrunCloud integration-test
```

  * Command to run the standalone "Development Mode" Cloud Server in **debug mode**
```
         mvn -PdebugCloud integration-test
```

Eclipse Project Setup

  * Import the Cloud project stored under "cloud" into your Eclipse workspace. Make sure you select, **Import > Maven > Existing Maven Projects**

  * In case, you do not have the Eclipse Maven Plugin installed, you can open the "cloud" project as a regular Java Project. But, before you do that, you execute the following command from the commandline:
```
       mvn eclipse:eclipse
```


---


## JBoss AS Deployment ##

Once your **cloud** jar file is tested end-to-end in the Maven based Cloud Server, you must deploy the jar file into a JBoss 5.1.0.GA App Server. The deployment is quite simple. You just copy the **cloud** jar file from the **cloud/target** folder into the **JBOSS\_HOME/server/openmobster/deploy** folder.