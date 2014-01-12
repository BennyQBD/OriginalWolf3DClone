package com.base.engine;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Window 
{
	public static void createWindow(int width, int height, String title)
	{
		Display.setTitle(title);
		try 
		{
			Display.setDisplayMode(new DisplayMode(width, height));
//			DisplayMode displayMode = null;
//	        DisplayMode[] modes = Display.getAvailableDisplayModes();
//
//	         for (int i = 0; i < modes.length; i++)
//	         {
//	             if (modes[i].getWidth() == width
//	             && modes[i].getHeight() == height
//	             && modes[i].isFullscreenCapable())
//	               {
//	                    displayMode = modes[i];
//	               }
//	         }
//			Display.setFullscreen(true);
			//Display.setVSyncEnabled(true);
			Display.create();
			Keyboard.create();
			Mouse.create();
		} 
		catch (LWJGLException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void render()
	{
		Display.update();
	}
	
	public static void dispose()
	{
		Display.destroy();
		Keyboard.destroy();
		Mouse.destroy();
	}
	
	public static boolean isCloseRequested()
	{
		return Display.isCloseRequested();
	}
	
	public static int getWidth()
	{
		return Display.getDisplayMode().getWidth();
	}
	
	public static int getHeight()
	{
		return Display.getDisplayMode().getHeight();
	}
	
	public static String getTitle()
	{
		return Display.getTitle();
	}
}
