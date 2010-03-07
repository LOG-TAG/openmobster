----Real Device TestSuites-------------------------------------------------------------------------------------------------------------------------------------------------------------------
mobileCloud/test-suite/cloudtests:
* mvn install
* Device Runtime and Moblet Runtime run as "separate" applications
* Install "mobileCloud" and "cloudtests"
* Server Needed
----Module-Level TestSuites-----------------------------------------------------------------------------------------------------------------------------------------------
mobileCloud/test-suite/sync-tests:
* mvn install
* Device Runtime and Moblet Runtime run "inside" the same application
* Install "sync-tests"
* Server needed

mobileCloud/test-suite/apitests:
* mvn install
* Device Runtime and Moblet Runtime run as "separate" applications
* Install "mobileCloud" and "apitests"
* Server needed


mobileCloud/test-suite/bus-tests:
* mvn install
* Device Runtime and Moblet Runtime run as "separate" applications
* Install "mobileCloud" and "bus-tests"
* Server needed
-----Low-Level TestSuites--------------------------------------------------------------------------------------------------------------------------------------------
common
* Does not need Device Runtime or Moblet Runtime
* Does not need Server
* Install database-testsuite-app1 and database-testsuite-app2
* Start Network, Finish Execution, Stop Network
--------------------------------------------------------------------------------------------------------------------------------------------------------------------
For the testsuite in general, make sure the server side component is running and listening to the default port of 1502.