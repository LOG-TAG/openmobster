									*************************************************************
									* OpenMobster - Mobile Cloud Platform (version 2.2-SNAPSHOT)*
									*************************************************************
									
								
titanium_module_android - Contains the source code for the Android Titanium Module

titanium_module_ios - Contains the source code for the iPhone Titanium Module

org.openmobster.cloud-android-0.1.zip - Binary/Deployable artifact of the Android Titanium Module

org.openmobster.cloud-iphone-0.1.zip - Binary/Deployable artifact of the iPhone Titanium Module

crud - A Titanium App that demonstrates the usage of these modules

crud/titanium_portable_app - The Titanium project. This must be imported into the Titanium Developer

crud/cloud - The Cloud Server to see the App in action end-to-end


Android Module Installation
----------------------------
cp 'org.openmobster.cloud-android-0.1.zip' into 'crud/titanium_portable_app'


iPhone Module Installation
---------------------------
unzip 'org.openmobster.cloud-iphone-0.1.zip' into '/Library/Application\ Support\Titanium'


Running the Cloud Server
---------------------------
cd crud/cloud

mvn -PrunCloud integration-test
