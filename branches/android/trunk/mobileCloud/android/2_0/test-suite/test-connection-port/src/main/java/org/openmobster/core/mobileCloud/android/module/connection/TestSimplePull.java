/**
 * 
 */
package org.openmobster.core.mobileCloud.android.module.connection;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

/**
 * @author openmobster
 *
 */
public class TestSimplePull extends Test
{
	public void runTest()
	{
		try
		{
			this.testSimplePull();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private void testSimplePull() throws Exception
	{
		NetSession session = null;
		try
		{
			session = NetworkConnector.getInstance().openSession(false);	
			
			String sessionInitPayload = "processorid=/testdrive/pull";
			
			String data = session.sendTwoWay(sessionInitPayload);
			if(data.indexOf("status=200")!=-1)
			{
				String stream = "<pull><caller name='android'/></pull>";
				
				String response = session.sendPayloadTwoWay(stream);
				
				System.out.println("InvocationResponse........................");
				System.out.println("Response="+response);
			}
			else
			{
				System.out.println("Status="+data);
				throw new RuntimeException("Invocation Failed.........");
			}
		}
		finally
		{
			if(session != null)
			{
				try{session.close();}catch(Exception ioe){}
			}
		}
	}
}
