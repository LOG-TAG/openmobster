## Prepare Environment ##

1) Checkout the OpenMobster source code to your favorite working directory
```
  svn checkout http://openmobster.googlecode.com/svn/branches/2.4-SNAPSHOT openmobster-read-only
```

2) Install Google Web Toolkit (GWT - I'm using 2.4.0)
```
  Download from: http://code.google.com/webtoolkit/gettingstarted.html
  (Just unzip it and point to it with the GWT_HOME env variable outlined below)
```

3) Install the Android SDK
```
  Download from: http://developer.android.com/sdk/index.html
  Follow the installation instructions
```

4) Install Maven Build Tool
```
  Download from: http://archive.apache.org/dist/maven/binaries/apache-maven-3.0.4-bin.zip
  Extract the zip file into a directory you will designate as MAVEN_HOME
```

5) Add the following values to your Environment Variables
```
MAVEN_HOME=[Directory where you unzipped Maven]
ANDROID_HOME=[Directory where Android SDK is installed]
MAVEN_OPTS=-Xmx512m -XX:MaxPermSize=128m
GWT_HOME=[Directory where you installed GWT]

//Append these values to the PATH variable
[Directory where you unzipped Maven]/bin;[Directory where Android SDK is installed]/platform-tools
```

6) Check that maven was totally switched over (you should see something like the following)
```
  root@vworkz:~#  mvn -version
  Apache Maven 3.0.4 (r1232337; 2012-01-17 03:44:56-0500)
  Maven home: /usr/local/apache-maven-3.0.4
  Java version: 1.6.0_20, vendor: Sun Microsystems Inc.
  Java home: /usr/lib/jvm/java-6-openjdk/jre
  Default locale: en_US, platform encoding: UTF-8
  OS name: "linux", version: "2.6.32-33-server", arch: "amd64", family: "unix"
```

## The environment should now be ready to build OpenMobster! ##
1) OK, let's build the whole enchilada (grab a coffee)
```
  cd ~/openmobster-read-only/cloud
  mvn -DskipTests -Pbuild-all install
  (Build should be SUCCESSFUL)
```