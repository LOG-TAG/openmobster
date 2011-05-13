/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.dev.tools.appcreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class SkeletonWorkspace
{
	public File generateWorkspace(List<String> supportedPlatforms,Map<String, String> userValues) throws Exception
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
		
		//Create the cloud tree
		File src = new File(projectDir, "cloud/src/main/java");
		File srcRes = new File(projectDir, "cloud/src/main/resources/META-INF");
		src.mkdirs();
		srcRes.mkdirs();
		
		//Create the moblet tree
		File moblet = new File(projectDir, "moblet/src/assemble");
		File moblet_res = new File(projectDir, "moblet/src/main/resources/META-INF");
		moblet.mkdirs();
		moblet_res.mkdirs();
		
		this.generateCloudApp(new File(projectDir, "cloud"), userValues);
		this.generateMoblet(supportedPlatforms,new File(projectDir, "moblet"), userValues);
		
		if(supportedPlatforms.contains("android"))
		{
			File main = new File(projectDir, "app-android/src/main/java/org/openmobster/app"); 
			File drawable_hdpi = new File(projectDir, "app-android/res/drawable-hdpi");
			File drawable_ldpi = new File(projectDir, "app-android/res/drawable-ldpi");
			File drawable_mdpi = new File(projectDir, "app-android/res/drawable-mdpi");
			File layout = new File(projectDir, "app-android/res/layout");
			File values = new File(projectDir, "app-android/res/values");
			main.mkdirs();
			drawable_hdpi.mkdirs();
			drawable_ldpi.mkdirs();
			drawable_mdpi.mkdirs();
			layout.mkdirs();
			values.mkdirs();
		}
		
		this.generateProject(supportedPlatforms,projectDir, userValues);
		this.generateAndroidOsApp(new File(projectDir,"app-android"), userValues);
		
		return projectDir;
	}
	
	private void generateProject(List<String> supportedPlatforms,File directory,Map<String, String> userValues) throws Exception
	{
		String parentPom = this.readTemplateResource("/skeleton/pom.xml");
		
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
		this.readTemplateResource("/skeleton/.classpath"));
		
		//README.txt file to help developer
		this.generateFile(new File(directory, "README.txt"), 
		this.readTemplateResource("/skeleton/README.txt"));
	}
	
	private void generateAndroidOsApp(File directory, Map<String, String> userValues) throws Exception
	{
		//app pom
		String pom = this.readTemplateResource("/skeleton/app-android/pom.xml");
		
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
		String androidManifest = this.readTemplateResource("/skeleton/app-android/AndroidManifest.xml");
		
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
		this.readTemplateBinaryResource("/skeleton/app-android/local.properties"));
		
		this.generateFile(new File(directory, "default.properties"),
		this.readTemplateBinaryResource("/skeleton/app-android/default.properties"));
		
		//res folder
		this.generateFile(new File(directory, "res/drawable-hdpi/icon.png"),
		this.readTemplateBinaryResource("/skeleton/app-android/res/drawable-hdpi/icon.png"));
		
		this.generateFile(new File(directory, "res/drawable-hdpi/push.png"),
		this.readTemplateBinaryResource("/skeleton/app-android/res/drawable-hdpi/push.png"));
		
		this.generateFile(new File(directory, "res/drawable-ldpi/icon.png"),
		this.readTemplateBinaryResource("/skeleton/app-android/res/drawable-ldpi/icon.png"));
		
		this.generateFile(new File(directory, "res/drawable-ldpi/icon.png"),
		this.readTemplateBinaryResource("/skeleton/app-android/res/drawable-mdpi/push.png"));
		
		this.generateFile(new File(directory, "res/drawable-mdpi/icon.png"),
		this.readTemplateBinaryResource("/skeleton/app-android/res/drawable-mdpi/icon.png"));
		
		this.generateFile(new File(directory, "res/drawable-mdpi/push.png"),
		this.readTemplateBinaryResource("/skeleton/app-android/res/drawable-mdpi/push.png"));
		
		this.generateFile(new File(directory, "res/layout/main.xml"),
		this.readTemplateBinaryResource("/skeleton/app-android/res/layout/main.xml"));
		
		this.generateFile(new File(directory, "res/values/strings.xml"),
		this.readTemplateBinaryResource("/skeleton/app-android/res/values/strings.xml"));
		
		//src/main/java
		this.generateFile(new File(directory, "src/main/java/org/openmobster/app/MainActivity.java"),
		this.readTemplateBinaryResource("/skeleton/app-android/src/main/java/org/openmobster/app/MainActivity.java"));
	}
	
	private void generateCloudApp(File directory, Map<String, String> userValues) throws Exception
	{
		String pom = this.readTemplateResource("/skeleton/cloud/pom.xml");
		
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
	}
	
	private String generateMobletPom(List<String> supportedPlatforms) throws Exception
	{
		if(supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/skeleton/moblet/pom.all.xml");
		}
		else if(supportedPlatforms.contains("android") && !supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/skeleton/moblet/pom.android.xml");
		}
		else if(!supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/skeleton/moblet/pom.blackberry.xml");
		}
		
		return null;
	}
	
	private String generateMobletApps(List<String> supportedPlatforms) throws Exception
	{
		if(supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/skeleton/moblet/src/main/resources/META-INF/moblet-apps.all.xml");
		}
		else if(supportedPlatforms.contains("android") && !supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/skeleton/moblet/src/main/resources/META-INF/moblet-apps.android.xml");
		}
		else if(!supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/skeleton/moblet/src/main/resources/META-INF/moblet-apps.blackberry.xml");
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
				this.readTemplateResource("/skeleton/moblet/src/assemble/moblet.xml"));
	}
	//-----------------------------------------------------------------------------------------------------------------------
	private String generateProjectProperties(Map<String,String> userValues) throws Exception
	{
		String propertyXml = this.readTemplateResource("/skeleton/properties.xml");
		
		propertyXml = propertyXml.replaceAll("<openmobster.version.value>", userValues.get("openmobster.version.value"));
		propertyXml = propertyXml.replaceAll("<version.android.api.value>", userValues.get("version.android.api.value"));
		
		return propertyXml;
	}
	
	private String generateProjectModules(List<String> supportedPlatforms) throws Exception
	{
		if(supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/skeleton/modules.all.xml");
		}
		else if(supportedPlatforms.contains("android") && !supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/skeleton/modules.android.xml");
		}
		else if(!supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/skeleton/modules.blackberry.xml");
		}
		
		return null;
	}
	
	private String generateSystemDependencies(List<String> supportedPlatforms) throws Exception
	{
		if(supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/skeleton/dependencies.all.xml");
		}
		else if(supportedPlatforms.contains("android") && !supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/skeleton/dependencies.android.xml");
		}
		else if(!supportedPlatforms.contains("android") && supportedPlatforms.contains("blackberry"))
		{
			return this.readTemplateResource("/skeleton/dependencies.blackberry.xml");
		}
		
		return null;
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
