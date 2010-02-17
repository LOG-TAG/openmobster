/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
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
		if(System.getenv("RIM_JDE_HOME") == null)
		{
			System.out.println("RIM_JDE_HOME environment variable must be set");
			System.exit(0);
		}
			
		AppCreator appCreator = new AppCreator();
		appCreator.start();
	}
	
	private void collectDeveloperInfo() throws Exception
	{
		String projectName = "MyApp"; //user with default
		String groupId = "com.myapp"; //user with default
		String projectUrl = "http://www.myapp.com"; //user with default
		String openMobsterVersion = "2.0-snapshot"; //user with default
		String projectVersion = "1.0-snapshot"; //user with default
		
		String rimosJdeHome = System.getenv("RIM_JDE_HOME").replaceAll("\\\\", "/");
		String rimosSimulatorHome = !(rimosJdeHome.endsWith("/"))?rimosJdeHome+"/simulator":rimosJdeHome+"simulator"; //user with default
		
		
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
		rimosJdeHome = this.askUser("RIM OS JDE_Home", rimosJdeHome);
		rimosSimulatorHome = this.askUser("RIM OS Simulator_Home", rimosSimulatorHome);
		
		String rimosAppGroupId = groupId+".rimos.app"; //derived 
		String rimosAppName = projectName; //derived
		String cloudAppName = projectName+"-cloud"; //derived
		
		Map<String, String> userValues = new HashMap<String, String>();
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
		userValues.put("appCreator.rimos.jde.home", rimosJdeHome);
		userValues.put("appCreator.rimos.simulator.home", rimosSimulatorHome);
		
		userValues.put("appCreator.cloud.app.groupId", groupId+".cloud.app");
		userValues.put("appCreator.cloud.app.name", cloudAppName);
		userValues.put("appCreator.cloud.app.artifactId", cloudAppName.toLowerCase());
		
		File projectDir = this.generateWorkspace(userValues);
		
		//Show output
		System.out.println("----------------------------------------------------");
		System.out.println("Project Name: "+projectName);
		System.out.println("Project Version: "+projectVersion);
		System.out.println("OpenMobster Platform: "+openMobsterVersion);
		System.out.println("RIM OS JDE_HOME: "+rimosJdeHome);
		System.out.println("RIM OS SIMULATOR_HOME: "+rimosSimulatorHome);
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
	
	private File generateWorkspace(Map<String, String> userValues) throws Exception
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
		File command = new File(projectDir, "app-rimos/src/main/java/org/openmobster/core/examples/rpc/command"); 
		File screen = new File(projectDir, "app-rimos/src/main/java/org/openmobster/core/examples/rpc/screen");
		File resMetaInf = new File(projectDir, "app-rimos/src/main/resources/META-INF");
		File appIcon = new File(projectDir, "app-rimos/src/main/resources/moblet-app/icon");
		command.mkdirs();
		screen.mkdirs();
		resMetaInf.mkdirs();
		appIcon.mkdirs();
		
		//Create the cloud tree
		File src = new File(projectDir, "cloud/src/main/java/org/openmobster/core/examples/rpc");
		File srcRes = new File(projectDir, "cloud/src/main/resources/META-INF");
		File test = new File(projectDir, "cloud/src/test/java/org/openmobster/core/examples/rpc");
		File testRes = new File(projectDir, "cloud/src/test/resources/META-INF");
		src.mkdirs();
		srcRes.mkdirs();
		test.mkdirs();
		testRes.mkdirs();
		
		//Copy project-level files
		this.generateProject(projectDir, userValues);
		this.generateRimOsApp(new File(projectDir, "app-rimos"), userValues);
		this.generateCloudApp(new File(projectDir, "cloud"), userValues);
		
		return projectDir;
	}
	
	private void generateProject(File directory,Map<String, String> userValues) throws Exception
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
		
		File parentPomFile = new File(directory, "pom.xml");
		this.generateFile(parentPomFile, parentPom);
		
		//FIXME: .classpath for Eclipse 
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
		
		pom = pom.replaceAll("<appCreator.rimos.jde.home>", 
				userValues.get("appCreator.rimos.jde.home"));
		
		pom = pom.replaceAll("<appCreator.rimos.simulator.home>", 
				userValues.get("appCreator.rimos.simulator.home"));
		
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
		
		//devcloud.rapc
		this.generateFile(new File(directory, "devcloud.rapc"), 
		this.readTemplateResource("/template/app-rimos/devcloud.rapc"));
		
		//activation.properties
		this.generateFile(new File(directory, "activation.properties"), 
		this.readTemplateResource("/template/app-rimos/activation.properties"));
		
		//moblet-apps
		String mobletApps = this.readTemplateResource("/template/app-rimos/src/main/resources/META-INF/moblet-apps.xml");
		mobletApps = mobletApps.replaceAll("<appCreator.rimos.app.name>", 
				userValues.get("appCreator.rimos.app.name"));
		
		File mobletAppsFile = new File(directory, "src/main/resources/META-INF/moblet-apps.xml");
		this.generateFile(mobletAppsFile, mobletApps);
		
		//moblet-app
		this.generateFile(new File(directory, "src/main/resources/moblet-app/icon/icon.png"), 
		this.readTemplateBinaryResource("/template/app-rimos/src/main/resources/moblet-app/icon/icon.png"));
		
		this.generateFile(new File(directory, "src/main/resources/moblet-app/localize.properties"), 
		this.readTemplateResource("/template/app-rimos/src/main/resources/moblet-app/localize.properties"));
		this.generateFile(new File(directory, "src/main/resources/moblet-app/localize_en_GB.properties"), 
		this.readTemplateResource("/template/app-rimos/src/main/resources/moblet-app/localize_en_GB.properties"));
		this.generateFile(new File(directory, "src/main/resources/moblet-app/moblet-app.xml"), 
		this.readTemplateResource("/template/app-rimos/src/main/resources/moblet-app/moblet-app.xml"));
		
		this.generateFile(new File(directory, "src/main/java/org/openmobster/core/examples/rpc/command/DemoMobileRPC.java"), 
		this.readTemplateResource("/template/app-rimos/src/main/java/org/openmobster/core/examples/rpc/command/DemoMobileRPC.java"));
		
		this.generateFile(new File(directory, "src/main/java/org/openmobster/core/examples/rpc/screen/HomeScreen.java"), 
		this.readTemplateResource("/template/app-rimos/src/main/java/org/openmobster/core/examples/rpc/screen/HomeScreen.java"));		
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
		this.generateFile(new File(directory, "src/main/java/org/openmobster/core/examples/rpc/DemoMobileBeanService.java"), 
		this.readTemplateResource("/template/cloud/src/main/java/org/openmobster/core/examples/rpc/DemoMobileBeanService.java"));
		
		//openmobster-config.xml
		this.generateFile(new File(directory, "src/main/resources/META-INF/openmobster-config.xml"), 
		this.readTemplateResource("/template/cloud/src/main/resources/META-INF/openmobster-config.xml"));
		
		//TestDemoRPC
		this.generateFile(new File(directory, "src/test/java/org/openmobster/core/examples/rpc/TestDemoRPC.java"), 
		this.readTemplateResource("/template/cloud/src/test/java/org/openmobster/core/examples/rpc/TestDemoRPC.java"));
		
		//openmobster-config.xml
		this.generateFile(new File(directory, "src/test/resources/META-INF/openmobster-config.xml"), 
		this.readTemplateResource("/template/cloud/src/test/resources/META-INF/openmobster-config.xml"));
		
		//log4j.properties
		this.generateFile(new File(directory, "src/test/resources/log4j.properties"), 
		this.readTemplateResource("/template/cloud/src/test/resources/log4j.properties"));
	}
	
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
