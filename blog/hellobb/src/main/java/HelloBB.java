import net.rim.device.api.ui.UiApplication;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;

/**
 * @author openmobster@gmail.com
 *
 */
public class HelloBB extends UiApplication
{
	public static void main(String[] args)
	{
		UiApplication app = new HelloBB();
		app.enterEventDispatcher();
	}
	
	public void activate()
	{
		this.pushScreen(new HelloBBScreen());
	}
	
	//create a new screen that extends MainScreen, which provides
	//default standard behavior for BlackBerry applications
	private static final class HelloBBScreen extends MainScreen
	{
	        private HelloBBScreen()
	        {
	                //invoke the MainScreen constructor
	                super();

	                //add a title to the screen
	                LabelField title = new LabelField("HelloBB", LabelField.ELLIPSIS
	                                | LabelField.USE_ALL_WIDTH);
	                setTitle(title);
	                
	               
	                //add the text "Hello Blackberry!!" to the screen
	                add(new RichTextField("Hello Blackberry!!"));
	        }

	        //override the onClose() method to display a dialog box to the user
	        //with "Goodbye!!" when the application is closed
	        public boolean onClose()
	        {
	            Dialog.alert("Good Bye!!");
	            
	            //exits the app cleanly
	            System.exit(0);
	            
	            return true;
	        }
	}
}
