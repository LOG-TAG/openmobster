/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.showcase.app.camera;

import java.io.IOException;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.app.Activity;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * @author openmobster@gmail.com
 */

public class Preview extends SurfaceView implements SurfaceHolder.Callback
{
	private SurfaceHolder mHolder;
	private Camera camera;

	public Preview(Activity activity)
	{
		super(activity);

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	public Camera getCamera()
	{
		return this.camera;
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		camera = Camera.open();
		try
		{
			camera.setPreviewDisplay(holder);

			/*camera.setPreviewCallback(new PreviewCallback() 
			{

				public void onPreviewFrame(byte[] data, Camera arg1)
				{
					FileOutputStream outStream = null;
					try
					{
						outStream = new FileOutputStream(String.format(
								"/sdcard/%d.jpg", System.currentTimeMillis()));
						outStream.write(data);
						outStream.close();
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					} 
					Preview.this.invalidate();
				}
			});*/
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
	{
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(w, h);
		camera.setParameters(parameters);
		camera.startPreview();
	}
	
	public void draw(Canvas canvas)
	{
		super.draw(canvas);
		Paint p = new Paint(Color.RED);
		canvas.drawText("PREVIEW", canvas.getWidth() / 2,
		canvas.getHeight() / 2, p);
	}
}
