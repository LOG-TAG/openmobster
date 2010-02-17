call mvn install:install-file -DgroupId=com.sun.jdmk -DartifactId=jmxtools -Dversion=1.2.1 -Dpackaging=jar -Dfile=jmxtools.jar

call mvn install:install-file -DgroupId=com.sun.jmx -DartifactId=jmxri -Dversion=1.2.1 -Dpackaging=jar -Dfile=jmxri.jar

call mvn install:install-file -DgroupId=net.rim -DartifactId=net_rim_api -Dversion=4.3.0 -Dpackaging=jar -Dfile=net_rim_api.jar

call mvn install:install-file -DgroupId=org.codehaus.mojo -DartifactId=exec-maven-plugin -Dversion=1.1.2-SNAPSHOT -Dpackaging=jar -Dfile=exec-maven-plugin-1.1.2-SNAPSHOT.jar