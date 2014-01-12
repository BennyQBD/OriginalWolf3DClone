package com.base.engine;

import java.util.ArrayList;

import javax.sound.midi.Sequence;

public class Game 
{
	private static final int STARTING_LEVEL = 1;
	private static Level level;
	private static Shader shader;
	
	private static int levelNum;
	private static ArrayList<Sequence> playlist = new ArrayList<Sequence>();
	private static int track;
	private static boolean isRunning;
	
	public Game()
	{
		shader = BasicShader.getInstance();
		Transform.setProjection(70f, Window.getWidth(), Window.getHeight(), 0.1f, 1000f);
		
		for(int i = 2; i <= 27; i++)
			playlist.add(ResourceLoader.loadMidi("WOLF" + i + ".mid"));
		
		track = STARTING_LEVEL - 1;
		levelNum = STARTING_LEVEL - 1;
		loadLevel(1);
		
		isRunning = true;
	}
	
	public void input()
	{
		level.input();
		
		if(Input.getKey(Input.KEY_1))
			System.exit(0);
		
//		if(Input.getKeyDown(Input.KEY_R))
//		{
//			AudioUtil.playMidi(playlist.get(track));
//		}
	}
	
	public void update()
	{
		if(isRunning)
		{
			level.update();
		}
	}
	
	public void render()
	{
		if(isRunning)
		{
			level.render();
		}
	}
	
	public static void updateShader(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material material)
	{
		shader.bind();
		shader.updateUniforms(worldMatrix, projectedMatrix, material);
	}
	
	public static void loadLevel(int offset)
	{	
		try
		{	
			int deadMonsters = 0;
			int totalMonsters = 0;
			boolean displayMonsters = false;
			
			if(level != null)
			{
				totalMonsters = level.getMonsters().size();
				
				for(Monster monster : level.getMonsters())
				{
					if(!monster.isAlive())
						deadMonsters++;
				}
				
				displayMonsters = true;
			}
			
			levelNum += offset;
			level = new Level(ResourceLoader.loadBitmap("level" + levelNum + ".png").flipX(), new Material(ResourceLoader.loadTexture("WolfCollection.png")));
		
			track += offset;
			
			AudioUtil.playMidi(playlist.get(track));
			
			while(track >= playlist.size())
				track -= playlist.size();
			
			System.out.println("=============================");
			System.out.println("Level " + levelNum + ": No Name");
			System.out.println("=============================");
			
			if(displayMonsters)
			{
				System.out.println("Killed " + deadMonsters + "/" + totalMonsters + " baddies: " + ((float)deadMonsters/(float)totalMonsters) * 100f + "%");
			}
		}
		catch(Exception ex)
		{
			isRunning = false;
			System.out.println("A winner is you!");
			AudioUtil.playMidi(null);
		}
		
	}
	
	public static Level getLevel()
	{
		return level;
	}
	
	public static Shader getShader()
	{
		return shader;
	}
	
	public static void setIsRunning(boolean value)
	{
		isRunning = value;
	}
}
