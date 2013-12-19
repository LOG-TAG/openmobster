/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.showcase.app.camera;

import java.io.FileOutputStream;
import org.showcase.app.R;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * Controls the 'home' screen that is displayed when the App is first launched.
 * 
 * It displays the Showcase options in a 'ListView' 
 * 
 * @author openmobster@gmail.com
 */

public class PreviewScreen extends Activity{
	
	private byte[] picture;
	private Preview preview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_preview);
		
		show();
		
	}
	
	private void show()
	{
		//Setup the Preview screen
		this.preview = new Preview(this);
		FrameLayout frame = (FrameLayout)findViewById(R.id.preview);
		frame.addView(preview);
		
		final Button buttonClick = (Button)findViewById(R.id.buttonClick);
		
		final ShutterCallback shutterCallback = new ShutterCallback() 
		{
			public void onShutter() 
			{
			}
		};

		/** Handles data for raw picture */
		final PictureCallback rawCallback = new PictureCallback() 
		{
			public void onPictureTaken(byte[] data, Camera camera) 
			{
				System.out.println("onPictureTaken - raw");
			}
		};

		/** Handles data for jpeg picture */
		final PictureCallback jpegCallback = new PictureCallback() 
		{
			public void onPictureTaken(byte[] data, Camera camera) 
			{
				FileOutputStream outStream = null;
				try 
				{
					/*outStream = new FileOutputStream(String.format(
							"/sdcard/%d.jpg", System.currentTimeMillis()));
					outStream.write(data);
					outStream.close();
					System.out.println("onPictureTaken - wrote bytes: " + data.length);*/
					PreviewScreen.this.picture = data;
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				} 
			}
		};
		
		buttonClick.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Camera camera = PreviewScreen.this.preview.getCamera();
				camera.takePicture(shutterCallback, rawCallback,
						jpegCallback);
				Intent intent=new Intent(PreviewScreen.this,CameraMainScreen.class);
				startActivity(intent);
				finish();
				//NavigationContext.getInstance().back();
			}
		});
	}	
}