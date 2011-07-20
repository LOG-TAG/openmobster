On Windows:
---------------------------------------------------------------------------------------
Make sure "RIM_JDE_HOME" environment variable is set to the home directory where the RIM JDE is installed.
For instance, when using the eclipse JDE plugin, it could look something like: C:\projects\eclipse\plugins\net.rim.eide.componentpack4.5.0_4.5.0.16\components


Build Help
-----------------------------------------------------------------------------------------------
Full Build including testsuite:
mvn clean install


Full Build skip testsuite:
mvn -Dmaven.test.skip clean install

Just run the testsuite:
See, TESTSUITE-HELP.txt

Build All including the Mobile Stack:
mvn -PmobileCloud clean install