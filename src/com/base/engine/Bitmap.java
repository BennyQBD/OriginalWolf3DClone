package com.base.engine;

public class Bitmap 
{
	private final int width;
	private final int height;
	private int[] pixels;
	
	public Bitmap(int width, int height)
	{
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
	}
	
	public void draw(Bitmap render, int xOffset, int yOffset)
	{
		for(int y = 0; y <  render.height; y++)
		{
			int yPix = y + yOffset;
			
			if (yPix < 0 || yPix >= height) 
				continue;
			
			for(int x = 0; x <  render.width; x++)
			{
				int xPix = x + xOffset;
				
				if (xPix < 0 || xPix >= width) 
					continue;
				
				int alpha =  render.pixels[x + y * render.width];
				
				pixels[xPix + yPix * width] = alpha;
			}
		}
	}

	public Bitmap flipX()
	{
		int[] temp = new int[pixels.length];
		
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				temp[i + j * width] = pixels[(width - i - 1) + j * width];
				//temp.setPixel(i, j, level.getPixel(level.getWidth() - i - 1, j));
			}
		}
		
		pixels = temp;
		
		return this;
	}
	
	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int[] getPixels()
	{
		return pixels;
	}
	
	public int getPixel(int x, int y)
	{
		return pixels[x + y * width];
	}
	
	public void setPixel(int x, int y, int value)
	{
		pixels[x + y * width] = value;
	}
}
