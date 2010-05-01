----Real Device TestSuites-------------------------------------------------------------------------------------------------------------------------------------------------------------------
mobileCloud/test-suite/cloudtests:
* mvn install
* Device Runtime and Moblet Runtime run as "separate" applications
* Install "mobileCloud" and "cloudtests"
* Server Needed
* Actual Device testing is fine
----Module-Level TestSuites-----------------------------------------------------------------------------------------------------------------------------------------------
mobileCloud/test-suite/apitests:
* mvn install
* Device Runtime and Moblet Runtime run as "separate" applications
* Install "mobileCloud" and "apitests"
* Server needed

mobileCloud/test-suite/sync-tests:
* mvn install
* Device Runtime and Moblet Runtime run "inside" the same application
* Install "sync-tests"
* Server needed
* *Do Not test on actual device* since Moblet co-located with the Cloud runtime is a simulator only setup for fast testsuite execution

mobileCloud/test-suite/bus-tests:
* mvn install
* Device Runtime and Moblet Runtime run as "separate" applications
* Install "mobileCloud" and "bus-tests"
* Server needed
* Testing on the actual device is not necessary for this one. cloudtests and apitests have plenty of InterApp communication using the Bus
-----Low-Level TestSuites--------------------------------------------------------------------------------------------------------------------------------------------
common
* Does not need Device Runtime or Moblet Runtime
* Does not need Server
* Install database-testsuite-app1 and database-testsuite-app2
* Start Network, Finish Execution, Stop Network
* *Do Not test on actual device* since it depends on Network Events which is a hack. This set of tests will deprecated in later releases
--------------------------------------------------------------------------------------------------------------------------------------------------------------------
For the testsuite in general, make sure the server side component is running and listening to the default port of 1502.