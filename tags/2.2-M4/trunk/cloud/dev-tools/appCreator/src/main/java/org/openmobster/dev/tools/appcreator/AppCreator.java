/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.dev.tools.appcreator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

/**
 * @author openmobster@gmail.com
 *
 */
public class AppCreator
{
	public AppCreator()
	{
		
	}
	
	public void start() throws Exception
	{
		System.out.println("OpenMobster Mobile Cloud Platform - App Creator");
		
		this.collectDeveloperInfo();
	}
	
	public static void main(String[] args) throws Exception
	{	
		AppCreator appCreator = new AppCreator();
		appCreator.start();
	}
	
	private Properties loadConfiguration() throws Exception
	{
		Properties config = new Properties();
		InputStream is = null;
		try
		{
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration.properties");
			config.load(is);
			return config;
		}
		finally
		{
			if(is != null)
			{
				is.close();
			}
		}
	}
	
	private void collectDeveloperInfo() throws Exception
	{
		//Read in the configuration
		Properties config = this.loadConfiguration();
		Map<String, String> userValues = new HashMap<String, String>();
		userValues.put("version.android.api.value", config.getProperty("version.android.api"));
		
		String projectName = "MyApp"; //user with default
		String groupId = "com.myapp"; //user with default
		String projectUrl = "http://www.myapp.com"; //user with default
		String openMobsterVersion = config.getProperty("openmobster.version"); //user with default
		String projectVersion = "1.0"; //user with default
		
		boolean sampleApp = true; //by default
		
		
		projectName = this.askUser("Project Name", projectName);
		String[] split = projectName.split(" ");
		projectName = "";
		for(String token: split)
		{
			projectName += token;
		}
		
		groupId = this.askUser("Group Id", groupId);
		projectVersion = this.askUser("Project Version", projectVersion);
		projectUrl = this.askUser("Project Url", projectUrl);
		openMobsterVersion = this.askUser("Version of the OpenMobster Platform", openMobsterVersion);
		
		//Decide if sample workspace or sketelon (hello world) workspace
		String sampleCode = this.askUser("Generate Sample Code", "yes/no [default:yes]");
		if(sampleCode.trim().length() == 0 || sampleCode.contains("yes"))
		{
			sampleApp = true;
		}
		else
		{
			sampleApp = false;
		}
		
		//Ask user to select the mobile platforms of interest
		List<String> supportedPlatforms = new ArrayList<String>();
		String platforms = config.getProperty("platforms");
		String[] platformTokens = platforms.split(",");
		for(String token:platformTokens)
		{
			String selection = this.askUser("Support Mobile Platform: "+token, "yes");
			if(selection != null && selection.trim().equalsIgnoreCase("yes"))
			{
				supportedPlatforms.add(token.toLowerCase());
				
				if(token.equalsIgnoreCase("android"))
				{
					String androidApi = this.askUser("Android API Version", config.getProperty("version.android.api"));
					userValues.put("version.android.api.value", androidApi);
				}
			}
		}
		
		String rimosAppGroupId = groupId+".rimos.app"; //derived 
		String rimosAppName = projectName; //derived
		String cloudAppName = projectName+"-cloud"; //derived
		String mobletGroupId = groupId + ".moblet";
		String mobletName = projectName;
		String mobletArtifactId = mobletName.toLowerCase();
		String androidAppGroupId = groupId+".android.app"; //derived
		String androidAppName = projectName; //derived
		int androidAppVersionCode = 1; //derived
		try
		{
			androidAppVersionCode = Integer.parseInt(projectVersion.substring(0, 1)); //derived
		}
		catch(Exception e)
		{
			androidAppVersionCode = 1;
		}
		
		
		userValues.put("project.name", projectName);
		userValues.put("appCreator.project.name", projectName+"-parent");
		userValues.put("appCreator.project.groupId", groupId);
		userValues.put("appCreator.project.version", projectVersion);
		userValues.put("appCreator.project.url", projectUrl);
		userValues.put("appCreator.openmobster.version", openMobsterVersion);
		userValues.put("appCreator.project.artifactId", projectName.toLowerCase());
		
		userValues.put("appCreator.rimos.app.groupId", rimosAppGroupId);
		userValues.put("appCreator.rimos.app.name", rimosAppName);
		userValues.put("appCreator.rimos.app.artifactId", rimosAppName.toLowerCase());		
		
		userValues.put("appCreator.cloud.app.groupId", groupId+".cloud.app");
		userValues.put("appCreator.cloud.app.name", cloudAppName);
		userValues.put("appCreator.cloud.app.artifactId", cloudAppName.toLowerCase());
		
		userValues.put("appCreator.moblet.groupId", mobletGroupId);
		userValues.put("appCreator.moblet.name", mobletName);
		userValues.put("appCreator.moblet.artifactId", mobletArtifactId);
		
		userValues.put("appCreator.android.app.groupId", androidAppGroupId);
		userValues.put("appCreator.android.app.name", androidAppName);
		userValues.put("appCreator.android.app.artifactId", androidAppName.toLowerCase());
		userValues.put("appCreator.android.app.versionCode", ""+androidAppVersionCode);
		
		userValues.put("openmobster.version.value", openMobsterVersion);
		
		File projectDir = null;
		if(sampleApp)
		{
			projectDir = this.generateWorkspace(supportedPlatforms,userValues);
		}
		else
		{
			SkeletonWorkspace skeleton = new SkeletonWorkspace();
			projectDir = skeleton.generateWorkspace(supportedPlatforms,userValues);
		}
		
		//Show output
		System.out.println("----------------------------------------------------");
		System.out.println("Project Name: "+projectName);
		System.out.println("Project Version: "+projectVersion);
		System.out.println("OpenMobster Platform: "+openMobsterVersion);		
		System.out.println("Created at: "+projectDir.getAbsolutePath());
	}
	
	private String askUser(String message, String defaultValue) throws Exception
	{
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		System.out.print(message+"["+defaultValue+"]: ");
		
		String userInput = bf.readLine();
		if(userInput != null && userInput.trim().length()>0)
		{
			return userInput;
		}
		
		return defaultValue;		
	}
	
	private File generateWorkspace(List<String> supportedPlatforms,Map<String, String> userValues) throws Exception
	{
		String projectName = userValues.get("project.name");
		
		//Start the project tree
		File projectDir = new File("workspace"+File.separator+projectName.toLowerCase());
		if(!projectDir.exists())
		{
			projectDir.mkdirs();
		}
		else
		{
			int counter = 2;
			while(projectDir.exists())
			{
				projectDir = new File("workspace"+File.separator+projectName.toLowerCase()+"_"+(counter++)+"_0");				
			}
			projectDir.mkdirs();
		}
		
		//Create the app-rimos tree
		if(supportedPlatforms.contains("blackberry"))
		{
			File command = new File(projectDir, "app-rimos/src/main/java/com/offlineApp/rimos/app/command"); 
			File screen = new File(projectDir, "app-rimos/src/main/java/com/offlineApp/rimos/app/screen");
			File appIcon = new File(projectDir, "app-rimos/src/main/resources/moblet-app/icon");
			File assemble = new File(projectDir, "app-rimos/src/assemble");
			command.mkdirs();
			screen.mkdirs();
			appIcon.mkdirs();
			assemble.mkdirs();
		}
		
		//Create the cloud tree
		File src = new File(projectDir, "cloud/src/main/java/com/offlineApp/cloud/rpc");
		File sync_src = new File(projectDir, "cloud/src/main/java/com/offlineApp/cloud/sync");
		File srcRes = new File(projectDir, "cloud/src/main/resources/META-INF");
		File test = new File(projectDir, "cloud/src/test/java/com/offlineApp/cloud/rpc");
		File sync_test = new File(projectDir, "cloud/src/test/java/com/offlineApp/cloud/sync");
		File testRes = new File(projectDir, "cloud/src/test/resources/META-INF");
		src.mkdirs();
		srcRes.mkdirs();
		test.mkdirs();
		testRes.mkdirs();
		sync_src.mkdirs();
		sync_test.mkdirs();
		
		//Create the moblet tree
		File moblet = new File(projectDir, "moblet/src/assemble");
		File moblet_res = new File(projectDir, "moblet/src/main/resources/META-INF");
		moblet.mkdirs();
		moblet_res.mkdirs();
		
		//Create the app-android tree
		if(supportedPlatforms.contains("android"))
		{
			File android_command = new File(projectDir, "app-android/src/main/java/com/offlineApp/android/app/command"); 
			File android_screen = new File(projectDir, "app-android/src/main/java/com/offlineApp/android/app/screen");
			File drawable_hdpi = new File(projectDir, "app-android/res/drawable-hdpi");
			File drawable_ldpi = new File(projectDir, "app-android/res/drawable-ldpi");
			File drawable_mdpi = new File(projectDir, "app-android/res/drawable-mdpi");
			File layout = new File(projectDir, "app-android/res/layout");
			File values = new File(projectDir, "app-android/res/values");
			File android_appicon = new File(projectDir, "app-android/src/main/resources/moblet-app/icon");
			android_command.mkdirs();
			android_screen.mkdirs();
			android_appicon.mkdirs();
			drawable_hdpi.mkdirs();
			drawable_ldpi.mkdirs();
			drawable_mdpi.mkdirs();
			layout.mkdirs();
			values.mkdirs();
		}
		
		//Copy project-level files
		this.generateProject(supportedPlatforms,projectDir, userValues);
		
		if(supportedPlatforms.contains("blackberry"))
		{
			this.generateRimOsApp(new File(projectDir, "app-rimos"), userValues);
		}
		
		this.generateCloudApp(new File(projectDir, "cloud"), userValues);
		this.generateMoblet(supportedPlatforms,new File(projectDir, "moblet"), userValues);
		
		if(supportedPlatforms.contains("android"))
		{
			this.generateAndroidOsApp(new File(projectDir,"app-android"), userValues);
		}
		
		return projectDir;
	}
	
	private String generateProjectProperties(Map<String,String> userValues) throws Exception
	{
		String propertyXml = this.readTemplateResource("/template/properties.xml");
		
		propertyXml = propertyXml.replaceAll("<openmobster.version.value>", userValues.get("openmobster.version.value"));
		propertyXml = propertyXml.replaceAll("<version.android.api.value>", userValues.get("version.android.api.value"));
		
		return propertyXml;
	}
	
	private String generateProjectModules(List<String> supportedPlatforms) throws Exception
	{
		if(supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/modules.all.xml");
		}
		else if(supportedPlatforms.contains("android") && !supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/modules.android.xml");
		}
		else if(!supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/modules.blackberry.xml");
		}
		
		return null;
	}
	
	private String generateSystemDependencies(List<String> supportedPlatforms) throws Exception
	{
		if(supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/dependencies.all.xml");
		}
		else if(supportedPlatforms.contains("android") && !supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/dependencies.android.xml");
		}
		else if(!supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/dependencies.blackberry.xml");
		}
		
		return null;
	}
	
	private void generateProject(List<String> supportedPlatforms,File directory,Map<String, String> userValues) throws Exception
	{
		String parentPom = this.readTemplateResource("/template/pom.xml");
		
		parentPom = parentPom.replaceAll("<appCreator.project.name>", 
		userValues.get("appCreator.project.name"));
		
		parentPom = parentPom.replaceAll("<appCreator.project.groupId>", 
				userValues.get("appCreator.project.groupId"));
		
		parentPom = parentPom.replaceAll("<appCreator.project.url>", 
				userValues.get("appCreator.project.url"));
		
		parentPom = parentPom.replaceAll("<appCreator.openmobster.version>", 
				userValues.get("appCreator.openmobster.version"));
		
		parentPom = parentPom.replaceAll("<appCreator.project.artifactId>", 
				userValues.get("appCreator.project.artifactId"));
		
		parentPom = parentPom.replaceAll("<appCreator.project.version>", 
				userValues.get("appCreator.project.version"));
		
		String propertyXml = this.generateProjectProperties(userValues);
		parentPom = parentPom.replaceAll("<appCreator.properties>", propertyXml);
		
		String moduleFragment = this.generateProjectModules(supportedPlatforms);
		if(moduleFragment != null)
		{
			parentPom = parentPom.replaceAll("<appCreator.modules>", moduleFragment);
		}
		
		String dependencyFragment = this.generateSystemDependencies(supportedPlatforms);
		if(dependencyFragment != null)
		{
			parentPom = parentPom.replaceAll("<appCreator.platform.dependencies>", dependencyFragment);
		}
		
		File parentPomFile = new File(directory, "pom.xml");
		this.generateFile(parentPomFile, parentPom);
		
		//.classpath for Eclipse 
		this.generateFile(new File(directory, ".classpath"), 
		this.readTemplateResource("/template/.classpath"));
		
		//README.txt file to help developer
		this.generateFile(new File(directory, "README.txt"), 
		this.readTemplateResource("/template/README.txt"));
	}
	
	private String generateMobletPom(List<String> supportedPlatforms) throws Exception
	{
		if(supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/moblet/pom.all.xml");
		}
		else if(supportedPlatforms.contains("android") && !supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/moblet/pom.android.xml");
		}
		else if(!supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/moblet/pom.blackberry.xml");
		}
		
		return null;
	}
	
	private String generateMobletApps(List<String> supportedPlatforms) throws Exception
	{
		if(supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/moblet/src/main/resources/META-INF/moblet-apps.all.xml");
		}
		else if(supportedPlatforms.contains("android") && !supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/moblet/src/main/resources/META-INF/moblet-apps.android.xml");
		}
		else if(!supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/template/moblet/src/main/resources/META-INF/moblet-apps.blackberry.xml");
		}
		
		return null;
	}
	
	private void generateMoblet(List<String> supportedPlatforms,File directory,Map<String, String> userValues) throws Exception
	{
		String pom = this.generateMobletPom(supportedPlatforms);
		
		pom = pom.replaceAll("<appCreator.project.version>", 
				userValues.get("appCreator.project.version"));
		
		pom = pom.replaceAll("<appCreator.project.groupId>", 
				userValues.get("appCreator.project.groupId"));
		
		pom = pom.replaceAll("<appCreator.project.artifactId>", 
				userValues.get("appCreator.project.artifactId"));
		
		pom = pom.replaceAll("<appCreator.moblet.groupId>", 
				userValues.get("appCreator.moblet.groupId"));
		
		pom = pom.replaceAll("<appCreator.moblet.name>", 
				userValues.get("appCreator.moblet.name"));
		
		pom = pom.replaceAll("<appCreator.moblet.artifactId>", 
				userValues.get("appCreator.moblet.artifactId"));
		
		pom = pom.replaceAll("<appCreator.cloud.app.groupId>", 
				userValues.get("appCreator.cloud.app.groupId"));
		
		pom = pom.replaceAll("<appCreator.cloud.app.artifactId>", 
				userValues.get("appCreator.cloud.app.artifactId"));
		
		pom = pom.replaceAll("<appCreator.rimos.app.groupId>", 
				userValues.get("appCreator.rimos.app.groupId"));
		
		pom = pom.replaceAll("<appCreator.rimos.app.artifactId>", 
				userValues.get("appCreator.rimos.app.artifactId"));
		
		pom = pom.replaceAll("<appCreator.android.app.groupId>", 
				userValues.get("appCreator.android.app.groupId"));
		
		pom = pom.replaceAll("<appCreator.android.app.artifactId>", 
				userValues.get("appCreator.android.app.artifactId"));
		
		File pomFile = new File(directory, "pom.xml");
		this.generateFile(pomFile, pom);
		
		//src/resources/META-INF/moblet-apps.xml
		String mobletApps = this.generateMobletApps(supportedPlatforms);
		mobletApps = mobletApps.replaceAll("<appCreator.rimos.app.name>", 
				userValues.get("appCreator.rimos.app.name"));
		
		mobletApps = mobletApps.replaceAll("<appCreator.android.app.name>", 
				userValues.get("appCreator.android.app.name"));
		
		mobletApps = mobletApps.replaceAll("<appCreator.android.app.artifactId>", 
				userValues.get("appCreator.android.app.artifactId"));
		
		mobletApps = mobletApps.replaceAll("<appCreator.project.version>", 
				userValues.get("appCreator.project.version"));
		
		File mobletAppsFile = new File(directory, "src/main/resources/META-INF/moblet-apps.xml");
		this.generateFile(mobletAppsFile, mobletApps);
		
		
		//src/assemble/moblet.xml
		this.generateFile(new File(directory, "src/assemble/moblet.xml"), 
				this.readTemplateResource("/template/moblet/src/assemble/moblet.xml"));
	}
	
	private void generateRimOsApp(File directory, Map<String, String> userValues) throws Exception
	{
		//app pom
		String pom = this.readTemplateResource("/template/app-rimos/pom.xml");
		
		pom = pom.replaceAll("<appCreator.project.version>", 
				userValues.get("appCreator.project.version"));
		
		pom = pom.replaceAll("<appCreator.project.groupId>", 
				userValues.get("appCreator.project.groupId"));
		
		pom = pom.replaceAll("<appCreator.project.artifactId>", 
				userValues.get("appCreator.project.artifactId"));
		
		pom = pom.replaceAll("<appCreator.rimos.app.groupId>", 
				userValues.get("appCreator.rimos.app.groupId"));
		
		pom = pom.replaceAll("<appCreator.rimos.app.name>", 
				userValues.get("appCreator.rimos.app.name"));
		
		pom = pom.replaceAll("<appCreator.rimos.app.artifactId>", 
				userValues.get("appCreator.rimos.app.artifactId"));
		
		
		File pomFile = new File(directory, "pom.xml");
		this.generateFile(pomFile, pom);
		
		//app rapc
		String appRapc = this.readTemplateResource("/template/app-rimos/app.rapc");
		
		appRapc = appRapc.replaceAll("<appCreator.rimos.app.name>", 
				userValues.get("appCreator.rimos.app.name"));
		
		appRapc = appRapc.replaceAll("<appCreator.rimos.app.artifactId>", 
				userValues.get("appCreator.rimos.app.artifactId"));
		
		File rapcFile = new File(directory, "app.rapc");
		this.generateFile(rapcFile, appRapc);
		
		//app.alx
		String appAlx = this.readTemplateResource("/template/app-rimos/app.alx");
		
		appAlx = appAlx.replaceAll("<appCreator.rimos.app.name>", 
				userValues.get("appCreator.rimos.app.name"));
		
		File alxFile = new File(directory, "app.alx");
		this.generateFile(alxFile, appAlx);
		
		//devcloud.rapc
		this.generateFile(new File(directory, "devcloud.rapc"), 
		this.readTemplateResource("/template/app-rimos/devcloud.rapc"));
		
		//activation.properties
		this.generateFile(new File(directory, "activation.properties"), 
		this.readTemplateResource("/template/app-rimos/activation.properties"));
		
		
		//moblet-app
		this.generateFile(new File(directory, "src/main/resources/moblet-app/icon/icon.png"), 
		this.readTemplateBinaryResource("/template/app-rimos/src/main/resources/moblet-app/icon/icon.png"));
		
		this.generateFile(new File(directory, "src/main/resources/moblet-app/localize.properties"), 
		this.readTemplateResource("/template/app-rimos/src/main/resources/moblet-app/localize.properties"));
		this.generateFile(new File(directory, "src/main/resources/moblet-app/localize_en_GB.properties"), 
		this.readTemplateResource("/template/app-rimos/src/main/resources/moblet-app/localize_en_GB.properties"));
		this.generateFile(new File(directory, "src/main/resources/moblet-app/moblet-app.xml"), 
		this.readTemplateResource("/template/app-rimos/src/main/resources/moblet-app/moblet-app.xml"));
		
		//src/main/java
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/rimos/app/command/DemoDetails.java"),
		this.readTemplateBinaryResource("/template/app-rimos/src/main/java/com/offlineApp/rimos/app/command/DemoDetails.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/rimos/app/command/DemoMobileRPC.java"),
		this.readTemplateBinaryResource("/template/app-rimos/src/main/java/com/offlineApp/rimos/app/command/DemoMobileRPC.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/rimos/app/command/PushTrigger.java"),
		this.readTemplateBinaryResource("/template/app-rimos/src/main/java/com/offlineApp/rimos/app/command/PushTrigger.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/rimos/app/command/ResetChannel.java"),
		this.readTemplateBinaryResource("/template/app-rimos/src/main/java/com/offlineApp/rimos/app/command/ResetChannel.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/rimos/app/command/PushHandler.java"),
				this.readTemplateBinaryResource("/template/app-rimos/src/main/java/com/offlineApp/rimos/app/command/PushHandler.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/rimos/app/screen/HomeScreen.java"),
		this.readTemplateBinaryResource("/template/app-rimos/src/main/java/com/offlineApp/rimos/app/screen/HomeScreen.java"));
		
		//Add assembly
		String bin_xml = this.readTemplateResource("/template/app-rimos/src/assemble/bin.xml");
		
		bin_xml = bin_xml.replaceAll("<appCreator.rimos.app.groupId>", 
				userValues.get("appCreator.rimos.app.groupId"));
		
		bin_xml = bin_xml.replaceAll("<appCreator.rimos.app.artifactId>", 
				userValues.get("appCreator.rimos.app.artifactId"));
		
		File binXmlFile = new File(directory, "src/assemble/bin.xml");
		this.generateFile(binXmlFile, bin_xml);
	}
	
	private void generateAndroidOsApp(File directory, Map<String, String> userValues) throws Exception
	{
		//app pom
		String pom = this.readTemplateResource("/template/app-android/pom.xml");
		
		pom = pom.replaceAll("<appCreator.project.version>", 
				userValues.get("appCreator.project.version"));
		
		pom = pom.replaceAll("<appCreator.project.groupId>", 
				userValues.get("appCreator.project.groupId"));
		
		pom = pom.replaceAll("<appCreator.project.artifactId>", 
				userValues.get("appCreator.project.artifactId"));
		
		pom = pom.replaceAll("<appCreator.android.app.groupId>", 
				userValues.get("appCreator.android.app.groupId"));
		
		pom = pom.replaceAll("<appCreator.android.app.name>", 
				userValues.get("appCreator.android.app.name"));
		
		pom = pom.replaceAll("<appCreator.android.app.artifactId>", 
				userValues.get("appCreator.android.app.artifactId"));
		
		String appGroupPath = userValues.get("appCreator.android.app.groupId").replace('.', '/');
		pom = pom.replaceAll("<appCreator.android.app.groupId.path>", appGroupPath);
		
		File pomFile = new File(directory, "pom.xml");
		this.generateFile(pomFile, pom);
		
		//Android Manifest
		String androidManifest = this.readTemplateResource("/template/app-android/AndroidManifest.xml");
		
		androidManifest = androidManifest.replaceAll("<appCreator.android.app.groupId>", 
				userValues.get("appCreator.android.app.groupId"));
		androidManifest = androidManifest.replaceAll("<appCreator.project.version>", 
				userValues.get("appCreator.project.version"));
		androidManifest = androidManifest.replaceAll("<appCreator.android.app.versionCode>", 
				userValues.get("appCreator.android.app.versionCode"));
		
		File androidManifestFile = new File(directory, "AndroidManifest.xml");
		this.generateFile(androidManifestFile, androidManifest);
		
		//Adding files verbatim
		this.generateFile(new File(directory, "local.properties"),
		this.readTemplateBinaryResource("/template/app-android/local.properties"));
		
		this.generateFile(new File(directory, "default.properties"),
		this.readTemplateBinaryResource("/template/app-android/default.properties"));
		
		//res folder
		this.generateFile(new File(directory, "res/drawable-hdpi/icon.png"),
		this.readTemplateBinaryResource("/template/app-android/res/drawable-hdpi/icon.png"));
		
		this.generateFile(new File(directory, "res/drawable-hdpi/push.png"),
		this.readTemplateBinaryResource("/template/app-android/res/drawable-hdpi/push.png"));
		
		this.generateFile(new File(directory, "res/drawable-ldpi/icon.png"),
		this.readTemplateBinaryResource("/template/app-android/res/drawable-ldpi/icon.png"));
		
		this.generateFile(new File(directory, "res/drawable-ldpi/push.png"),
		this.readTemplateBinaryResource("/template/app-android/res/drawable-ldpi/push.png"));
		
		this.generateFile(new File(directory, "res/drawable-mdpi/icon.png"),
		this.readTemplateBinaryResource("/template/app-android/res/drawable-mdpi/icon.png"));
		
		this.generateFile(new File(directory, "res/drawable-mdpi/push.png"),
		this.readTemplateBinaryResource("/template/app-android/res/drawable-mdpi/push.png"));
		
		this.generateFile(new File(directory, "res/layout/home.xml"),
		this.readTemplateBinaryResource("/template/app-android/res/layout/home.xml"));
		
		this.generateFile(new File(directory, "res/values/strings.xml"),
		this.readTemplateBinaryResource("/template/app-android/res/values/strings.xml"));
		
		//src/main/resources
		this.generateFile(new File(directory, "src/main/resources/moblet-app/icon/icon.png"),
		this.readTemplateBinaryResource("/template/app-android/src/main/resources/moblet-app/icon/icon.png"));
		
		this.generateFile(new File(directory, "src/main/resources/moblet-app/localize_en_GB.properties"),
		this.readTemplateBinaryResource("/template/app-android/src/main/resources/moblet-app/localize_en_GB.properties"));
		
		this.generateFile(new File(directory, "src/main/resources/moblet-app/localize.properties"),
		this.readTemplateBinaryResource("/template/app-android/src/main/resources/moblet-app/localize.properties"));
		
		this.generateFile(new File(directory, "src/main/resources/moblet-app/moblet-app.xml"),
		this.readTemplateBinaryResource("/template/app-android/src/main/resources/moblet-app/moblet-app.xml"));
		
		//src/main/java
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/android/app/command/DemoDetails.java"),
		this.readTemplateBinaryResource("/template/app-android/src/main/java/com/offlineApp/android/app/command/DemoDetails.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/android/app/command/DemoMobileRPC.java"),
		this.readTemplateBinaryResource("/template/app-android/src/main/java/com/offlineApp/android/app/command/DemoMobileRPC.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/android/app/command/PushTrigger.java"),
		this.readTemplateBinaryResource("/template/app-android/src/main/java/com/offlineApp/android/app/command/PushTrigger.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/android/app/command/ResetChannel.java"),
		this.readTemplateBinaryResource("/template/app-android/src/main/java/com/offlineApp/android/app/command/ResetChannel.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/android/app/screen/HomeScreen.java"),
		this.readTemplateBinaryResource("/template/app-android/src/main/java/com/offlineApp/android/app/screen/HomeScreen.java"));
	}
	
	private void generateCloudApp(File directory, Map<String, String> userValues) throws Exception
	{
		String pom = this.readTemplateResource("/template/cloud/pom.xml");
		
		pom = pom.replaceAll("<appCreator.project.version>", 
				userValues.get("appCreator.project.version"));
		
		pom = pom.replaceAll("<appCreator.project.groupId>", 
				userValues.get("appCreator.project.groupId"));
		
		pom = pom.replaceAll("<appCreator.project.artifactId>", 
				userValues.get("appCreator.project.artifactId"));
		
		pom = pom.replaceAll("<appCreator.cloud.app.groupId>", 
				userValues.get("appCreator.cloud.app.groupId"));
		
		pom = pom.replaceAll("<appCreator.cloud.app.name>", 
				userValues.get("appCreator.cloud.app.name"));
		
		pom = pom.replaceAll("<appCreator.cloud.app.artifactId>", 
				userValues.get("appCreator.cloud.app.artifactId"));
	
		File pomFile = new File(directory, "pom.xml");
		this.generateFile(pomFile, pom);
		
		//DemoMobileBeanService
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/cloud/rpc/DemoMobileBeanService.java"), 
		this.readTemplateResource("/template/cloud/src/main/java/com/offlineApp/cloud/rpc/DemoMobileBeanService.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/cloud/sync/DemoBean.java"), 
		this.readTemplateResource("/template/cloud/src/main/java/com/offlineApp/cloud/sync/DemoBean.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/cloud/sync/DemoChannel.java"), 
				this.readTemplateResource("/template/cloud/src/main/java/com/offlineApp/cloud/sync/DemoChannel.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/cloud/sync/DemoDataRepository.java"), 
				this.readTemplateResource("/template/cloud/src/main/java/com/offlineApp/cloud/sync/DemoDataRepository.java"));
		
		this.generateFile(new File(directory, "src/main/java/com/offlineApp/cloud/sync/PushTriggerService.java"), 
				this.readTemplateResource("/template/cloud/src/main/java/com/offlineApp/cloud/sync/PushTriggerService.java"));
		
		//openmobster-config.xml
		this.generateFile(new File(directory, "src/main/resources/META-INF/openmobster-config.xml"), 
		this.readTemplateResource("/template/cloud/src/main/resources/META-INF/openmobster-config.xml"));
		
		//TestDemoRPC
		this.generateFile(new File(directory, "src/test/java/com/offlineApp/cloud/rpc/TestDemoRPC.java"), 
		this.readTemplateResource("/template/cloud/src/test/java/com/offlineApp/cloud/rpc/TestDemoRPC.java"));
		
		this.generateFile(new File(directory, "src/test/java/com/offlineApp/cloud/sync/TestDemoChannel.java"), 
				this.readTemplateResource("/template/cloud/src/test/java/com/offlineApp/cloud/sync/TestDemoChannel.java"));
		
		//openmobster-config.xml
		this.generateFile(new File(directory, "src/test/resources/META-INF/openmobster-config.xml"), 
		this.readTemplateResource("/template/cloud/src/test/resources/META-INF/openmobster-config.xml"));
		
		//log4j.properties
		this.generateFile(new File(directory, "src/test/resources/log4j.properties"), 
		this.readTemplateResource("/template/cloud/src/test/resources/log4j.properties"));
	}
	//-----------------------------------------------------------------------------------------------------------------------
	private void generateFile(File file, String content) throws Exception
	{
		FileOutputStream fos = new FileOutputStream(file);
		try
		{
			fos.write(content.getBytes());
			fos.flush();
		}
		finally
		{
			if(fos != null)
			{
				fos.close();
			}
		}
	}
	
	private void generateFile(File file, byte[] content) throws Exception
	{
		FileOutputStream fos = new FileOutputStream(file);
		try
		{
			fos.write(content);
			fos.flush();
		}
		finally
		{
			if(fos != null)
			{
				fos.close();
			}
		}
	}
	
	private String readTemplateResource(String resourceLocation) throws Exception
	{	
		InputStream resourceStream = AppCreator.class.getResourceAsStream(resourceLocation);
		if(resourceStream != null)
		{
			return new String(IOUtilities.readBytes(resourceStream));
		}
		
		return null;
	}
	
	private byte[] readTemplateBinaryResource(String resourceLocation) throws Exception
	{	
		InputStream resourceStream = AppCreator.class.getResourceAsStream(resourceLocation);
		if(resourceStream != null)
		{
			return IOUtilities.readBytes(resourceStream);
		}
		
		return null;
	}
}
