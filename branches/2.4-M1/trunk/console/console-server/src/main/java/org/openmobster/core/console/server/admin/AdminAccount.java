/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.console.server.admin;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 
 * @author openmobster@gmail.com
 */
public class AdminAccount 
{
	private long id; //database uid
	private String username;
	private String password;
	private Set<AccountAttribute> attributes;
	
	public AdminAccount()
	{
		this.attributes = new HashSet<AccountAttribute>();
	}
	
	public AdminAccount(String username, String password)
	{
		this.username = username;
		this.password = password;
		this.attributes = new HashSet<AccountAttribute>();
	}

	public long getId() 
	{
		return id;
	}

	public void setId(long id) 
	{
		this.id = id;
	}

	public String getUsername() 
	{
		return username;
	}

	public void setUsername(String username) 
	{
		this.username = username;
	}

	public String getPassword() 
	{
		return password;
	}

	public void setPassword(String password) 
	{
		this.password = password;
	}
	
	public Set<AccountAttribute> getAttributes() 
	{
		return attributes;
	}

	public void setAttributes(Set<AccountAttribute> attributes) 
	{
		this.attributes = attributes;
	}
	//---------------------------------------------------------------------------------------------
	public boolean isActive()
	{
		boolean isActive = false;
		
		AccountAttribute active = this.readAttribute("active");
		if(active != null && active.getValue().equals(Boolean.TRUE.toString()))
		{
			isActive = true;
		}
		
		return isActive;
	}
	
	public void activate()
	{
		AccountAttribute active = this.readAttribute("active");
		if(active == null)
		{
			active = new AccountAttribute("active", Boolean.TRUE.toString());
			this.addAttribute(active);
		}
		else
		{
			active.setValue(Boolean.TRUE.toString());
			this.updateAttribute(active);
		}
	}
	
	public void deactivate()
	{
		AccountAttribute active = this.readAttribute("active");
		if(active == null)
		{
			active = new AccountAttribute("active", Boolean.FALSE.toString());
			this.addAttribute(active);
		}
		else
		{
			active.setValue(Boolean.FALSE.toString());
			this.updateAttribute(active);
		}
	}
	
	public void setFirstName(String firstName)
	{
		AccountAttribute attribute = this.readAttribute("first_name");
		if(attribute != null)
		{
			attribute.setValue(firstName);
		}
		else
		{
			AccountAttribute newAttr = new AccountAttribute("first_name",firstName);
			this.addAttribute(newAttr);
		}
	}
	
	public String getFirstName()
	{
		AccountAttribute attribute = this.readAttribute("first_name");
		if(attribute != null)
		{
			return attribute.getValue();
		}
		return null;
	}
	
	public void setLastName(String lastName)
	{
		AccountAttribute attribute = this.readAttribute("last_name");
		if(attribute != null)
		{
			attribute.setValue(lastName);
		}
		else
		{
			AccountAttribute newAttr = new AccountAttribute("last_name",lastName);
			this.addAttribute(newAttr);
		}
	}
	
	public String getLastName()
	{
		AccountAttribute attribute = this.readAttribute("last_name");
		if(attribute != null)
		{
			return attribute.getValue();
		}
		return null;
	}
	//-----------------------------------------------------------------------------------
	public void addAttribute(AccountAttribute attribute)
	{				
		this.getAttributes().add(attribute);
	}
	
	
	public void removeAttribute(AccountAttribute attribute)
	{		
		AccountAttribute cour = this.find(attribute);
		if(cour != null)
		{
			this.getAttributes().remove(cour);
		}
	}
	
	
	public void updateAttribute(AccountAttribute attribute)
	{		
		AccountAttribute cour = this.find(attribute);
		if(cour != null)
		{
			cour.setName(attribute.getName());
			cour.setValue(attribute.getValue());
		}
		else
		{
			this.addAttribute(attribute);
		}
	}
	
	
	public AccountAttribute readAttribute(String name)
	{		
		return this.find(new AccountAttribute(name, null));
	}
	
	
	private AccountAttribute find(AccountAttribute attribute)
	{
		AccountAttribute cour = null;
	
		Set<AccountAttribute> attributes = this.getAttributes();
		for(AccountAttribute loop: attributes)
		{
			if(loop.getName().equals(attribute.getName()))
			{
				return loop;
			}
		}
		
		return cour;
	}
	//---------------xml rendition-------------------------------------------------------------------------
	public String toXml()
	{
		StringBuilder xmlBuffer = new StringBuilder();
		
		xmlBuffer.append("<admin-account>\n");
		xmlBuffer.append("<username>"+this.username+"</username>\n");
		if(this.attributes != null && !this.attributes.isEmpty())
		{
			xmlBuffer.append("<attributes>\n");
			for(AccountAttribute attribute:this.attributes)
			{
				xmlBuffer.append("<attribute name='"+attribute.getName()+"' value='"+attribute.getValue()+"'></attribute>\n");
			}
			xmlBuffer.append("</attributes>\n");
		}
		xmlBuffer.append("</admin-account>\n");
		
		return xmlBuffer.toString();
	}
	
	public static String toXml(List<AdminAccount> accounts)
	{
		StringBuilder xmlBuffer = new StringBuilder();
		
		if(accounts != null && !accounts.isEmpty())
		{
			xmlBuffer.append("<accounts>\n");
			
			for(AdminAccount account:accounts)
			{
				xmlBuffer.append(account.toXml());
			}
			
			xmlBuffer.append("</accounts>\n");
		}
		
		return xmlBuffer.toString();
	}
	
	public static AdminAccount toObject(String xml)
	{
		try
		{
			AdminAccount adminAccount = new AdminAccount();
			
			Document document = DocumentBuilderFactory.newInstance().
			newDocumentBuilder().parse(xml);
			
			Element account = (Element)document.getElementsByTagName("admin-account").item(0);
			adminAccount = toAccount(account);
			
			return adminAccount;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static List<AdminAccount> toList(String xml)
	{
		try
		{
			List<AdminAccount> accounts = new ArrayList<AdminAccount>();
			
			Document document = DocumentBuilderFactory.newInstance().
			newDocumentBuilder().parse(xml);
			
			NodeList nodes = document.getElementsByTagName("admin-account");
			if(nodes != null)
			{
				int length = nodes.getLength();
				for(int i=0; i<length; i++)
				{
					Element node = (Element)nodes.item(i);
					accounts.add(toAccount(node));
				}
			}
			
			return accounts;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private static AdminAccount toAccount(Element node)
	{
		AdminAccount adminAccount = new AdminAccount();
		
		Element username = (Element)node.getElementsByTagName("username").item(0);
		adminAccount.setUsername(username.getFirstChild().getNodeValue());
		
		NodeList attrNodes = node.getElementsByTagName("attribute");
		if(attrNodes != null && attrNodes.getLength()>0)
		{
			int length = attrNodes.getLength();
			for(int i=0; i<length; i++)
			{
				Element local = (Element)attrNodes.item(i);
				String name = local.getAttribute("name");
				String value = local.getAttribute("value");
				AccountAttribute accountAttribute = new AccountAttribute(name,value);
				adminAccount.addAttribute(accountAttribute);
			}
		}
		return adminAccount;
	}
}
