									*************************************************************
									* OpenMobster - Mobile Cloud Platform (version 2.0-snapshot)*
									*************************************************************
									
**************************									
Quick Usage Instructions:*
**************************

Start the Management Console:
------------------------------
Start the 'Management Console': Execute openmobster.sh or openmobster.bat located under cloudConsole/bin

Setup 'Root' User:
-------------------------------
If this the first time being used, use the configure command:
configure -a <cloudServer Ip Address> -po 1502 -u root -p <whatever root password you can remember>

Note: This is the user who is allowed to use the <emphasis role="bold">Management Console</emphasis>, not to be confused with other accounts that will be created.
The accounts that will be created are the actual users that will connect to the "Cloud Server" via their Mobile Apps loaded on their respective devices.

Login to the Cloud Server as 'Root' for Management functions:
--------------------------------------------------------------
startadmin -u root -p <whatever password was setup>


Setup a 'User' account used by Mobile Apps:
--------------------------------------------
register -u <email of the user being registered> -p <whatever password that you want to associate with this account>


********
Notes: *
********

* To get general help type 'help' at the command line

* To get help related to a specific command, use: command -h
For example: startadmin -h