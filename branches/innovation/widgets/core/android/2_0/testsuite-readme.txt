Test Synchronization Module

This TestModule combines the moblet-runtime and mobileCloud-runtime into a single App

* mobileCloud/android/2_0/test-suite/test-sync-port: 
mvn package com.jayway.maven.plugins.android.generation2:maven-android-plugin:deploy


* Server Needed: device-agent-simulator : 
mvn -Pdevice-testsuite test

--------------------------------------------------------------------------------------------------------
Test MobileObject Module

* mobileCloud/android/2_0/test-suite/test-mobileObject-port: 
mvn package com.jayway.maven.plugins.android.generation2:maven-android-plugin:deploy

* Install CloudManager: mobileCloud/android/2_0/cloudManager: 
mvn package com.jayway.maven.plugins.android.generation2:maven-android-plugin:deploy

* Server *Not* Needed

--------------------------------------------------------------------------------------------------------
Test Connection Module

* mobileCloud/android/2_0/test-suite/test-connection-port: 
mvn package com.jayway.maven.plugins.android.generation2:maven-android-plugin:deploy

* Install CloudManager: mobileCloud/android/2_0/cloudManager: 
mvn package com.jayway.maven.plugins.android.generation2:maven-android-plugin:deploy

* Server Needed: testdrive-server : 
mvn -Prun test

---------------------------------------------------------------------------------------------------------
Test Common Module

* mobileCloud/android/2_0/test-suite/test-common-port: 
mvn package com.jayway.maven.plugins.android.generation2:maven-android-plugin:deploy

* Install CloudManager: mobileCloud/android/2_0/cloudManager: 
mvn package com.jayway.maven.plugins.android.generation2:maven-android-plugin:deploy

* Server *Not* Needed

---------------------------------------------------------------------------------------------------------
Test Bus Module

* mobileCloud/android/2_0/test-suite/test-bus-port: 
mvn package com.jayway.maven.plugins.android.generation2:maven-android-plugin:deploy

* Install CloudManager: mobileCloud/android/2_0/cloudManager: 
mvn package com.jayway.maven.plugins.android.generation2:maven-android-plugin:deploy

* Server *Not* Needed

---------------------------------------------------------------------------------------------------------
Test API (Full Integration Test)

* mobileCloud/android/2_0/test-suite/test-api-port: 
mvn package com.jayway.maven.plugins.android.generation2:maven-android-plugin:deploy

* Install CloudManager: mobileCloud/android/2_0/cloudManager: 
mvn package com.jayway.maven.plugins.android.generation2:maven-android-plugin:deploy

* Server Needed: device-agent-simulator : 
mvn -Pdevice-testsuite test


