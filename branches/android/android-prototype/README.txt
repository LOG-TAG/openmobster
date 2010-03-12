Maven Dependency Issue
-------------------------------------------------------------------------------------------------
When project is first checked out, it is necessary to install the necessary maven dependencies
into your local maven repository

For this, run installDependencies.sh before performing a maven build

These are dependencies that cannot be distributed via a direct download from a remote maven repository.

See here for details: http://forum.java.sun.com/thread.jspa?threadID=5269944


Build Help
-----------------------------------------------------------------------------------------------
Full Build including testsuite:
mvn clean install


Full Build skip testsuite:
mvn -Dmaven.test.skip clean install

Just run the testsuite:
See, TESTSUITE-HELP.txt

Build All including the Device-Side Moblet Stack:
mvn -PmobileCloud clean install