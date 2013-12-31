/**
 * Copyright (c) {2003,2010} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cloud.console.client.ui;

import org.openmobster.core.cloud.console.client.common.Constants;
import org.openmobster.core.cloud.console.client.ui.accounts.AccountsScreen;
import org.openmobster.core.cloud.console.client.ui.accounts.DeviceScreenLoader;
import org.openmobster.core.cloud.console.client.ui.apn.PushAppLoader;
import org.openmobster.core.cloud.console.client.ui.user.AdminScreenLoader;
import org.openmobster.core.cloud.console.client.ui.user.ApproveAdminScreen;

import com.smartgwt.client.types.SortArrow;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.LeafClickEvent;
import com.smartgwt.client.widgets.tree.events.LeafClickHandler;


/**
 *
 * @author openmobster@gmail.com
 */
public final class NavigationController 
{
	private static NavigationController singleton;
	
	private VLayout layout;
	private Tree navigationTree;
	
	private NavigationController()
	{
		
	}
	
	public static NavigationController getInstance()
	{
		if(NavigationController.singleton == null)
		{
			NavigationController.singleton = new NavigationController();
		}
		return NavigationController.singleton;
	}
	
	public void start()
	{
		this.layout = new VLayout();
        this.layout.setHeight100();
        this.layout.setWidth(185);
        this.layout.setShowResizeBar(true);
        
        this.layout.addMember(this.getSideNavigation());
	}
	
	public void stop()
	{
		
	}
	
	public Tree getNavigationTree() 
	{
		return navigationTree;
	}

	public void setNavigationTree(Tree navigationTree) 
	{
		this.navigationTree = navigationTree;
	}

	public Canvas render()
	{
		return this.layout;
	}
	
	private TreeGrid getSideNavigation()
	{
		TreeGrid treeGrid = new TreeGrid();
		
		treeGrid.setWidth100();
		treeGrid.setHeight100();
		treeGrid.setCustomIconProperty("icon");
		treeGrid.setAnimateFolderTime(100);
		treeGrid.setAnimateFolders(true);
		treeGrid.setAnimateFolderSpeed(1000);
		treeGrid.setNodeIcon("silk/application_view_list.png");
		treeGrid.setShowSortArrow(SortArrow.CORNER);
		treeGrid.setShowAllRecords(true);
		treeGrid.setLoadDataOnDemand(false);
		treeGrid.setCanSort(false);
		
		TreeGridField field = new TreeGridField();
        field.setCanFilter(true);
        field.setName("name");
        field.setTitle("<b>Console</b>");
        treeGrid.setFields(field);
        
        this.navigationTree = new Tree();
        this.navigationTree.setModelType(TreeModelType.PARENT);
        this.navigationTree.setNameProperty("name");
        this.navigationTree.setIdField("id");
        this.navigationTree.setParentIdField("parentId");
        this.navigationTree.setOpenProperty("isOpen");
        this.navigationTree.setRootValue("root");
        
        this.navigationTree.setData(adminNodes);
        
        treeGrid.setData(this.navigationTree);
        
        treeGrid.addLeafClickHandler(new NavBarHandler());
		
		return treeGrid;
	}
	
	public static TreeNode createNavigationNode(String name, String id, String parentId, String icon, String thumbnailIcon, boolean enabled)
	{
		TreeNode treeNode = new TreeNode();
		
		if (enabled) 
		{
            treeNode.setName(name);
        } 
		else 
		{
            treeNode.setName("<span style='color:808080'>" + name + "</span>");
        }
		
		treeNode.setIcon(icon);
		if(thumbnailIcon != null)
		{
			treeNode.setAttribute("thumbnail",thumbnailIcon);
		}
		
		treeNode.setID(id);
		treeNode.setParentID(parentId);
		
		return treeNode;
	}
	
	public static TreeNode createNavigationNode(String name,String id, String parentId, String icon, String thumbnailIcon, boolean enabled,TreeNode[] children)
	{
		TreeNode treeNode = createNavigationNode(name, id, parentId,icon,thumbnailIcon,enabled);
		
		treeNode.setChildren(children);
		
		return treeNode;
	}
	
	public static TreeNode[] manageAccounts = new TreeNode[]{
		createNavigationNode("Devices", "devices", "manage_devices", "openmobster/devices-32.png", "openmobster/devices-128.png", true),
		createNavigationNode("Administrators", "admin_approvals", "manage_accounts", "openmobster/admin-32.png", "openmobster/admin-128.png", true),
	};
	
	public static TreeNode[] iphone = new TreeNode[]{
		createNavigationNode("Push Setup", "id_push_setup", "push_setup", "openmobster/push-32.png", "openmobster/push-128.png", true),
	};
	
	public static TreeNode[] adminNodes = new TreeNode[]{
		createNavigationNode("Account Management","manage_accounts","root", "silk/house.png", null, true, manageAccounts),
		createNavigationNode("iPhone","iphone_setup","root", "silk/house.png", null, true, iphone),
	};
	
	public static TreeNode[] all = new TreeNode[]{
		createNavigationNode("Devices", "devices", "manage_devices", "openmobster/devices-32.png", "openmobster/devices-128.png", true),
		createNavigationNode("Administrators", "admin_approvals", "manage_accounts", "openmobster/admin-32.png", "openmobster/admin-128.png", true),
		createNavigationNode("Push Setup", "id_push_setup", "push_setup", "openmobster/push-32.png", "openmobster/push-128.png", true),
	};
	
	private static class NavBarHandler implements LeafClickHandler 
	{
	    @Override
	    public void onLeafClick(LeafClickEvent event) 
	    {
	        TreeNode node = event.getLeaf();
	        
	        String id = node.getAttribute("id");
	        if(id.equals("devices"))
	        {
	        	DeviceScreenLoader.load();
	        }
	        else if(id.equals("admin_approvals"))
	        {
	            AdminScreenLoader.load(id);
	        }
	        else if(id.equals("id_push_setup"))
	        {
	        	PushAppLoader.load(Constants.push_app);
	        }
	    }
	}
}
