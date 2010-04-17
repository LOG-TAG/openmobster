package org.bus.testdrive;

import org.bus.testdrive.module.bus.Bus;

import net.rim.device.api.system.Application;

/**
 * @author openmobster@gmail
 * 
 */
public class RemoteBus extends Application
{
	public RemoteBus()
	{
		try
		{								
			Bus bus = Bus.getInstance();
			if(!bus.isActive())
			{
				Bus.getInstance().start();
				Bus.getInstance().register(new RemoteInvocationHandler());
			}
			
			Sensor sensor = new Sensor();
	    	this.addRadioListener(sensor);
	    	this.addSystemListener(sensor);
		}
		catch(Exception e)
		{
			System.out.println("-----------------------------------------------");
			System.out.println("Exception: "+e.getMessage());
			System.out.println("-----------------------------------------------");
		}
	}
	
	public static void main(String[] args)
	{
		Application app = new RemoteBus();
		app.enterEventDispatcher();
	}
}
