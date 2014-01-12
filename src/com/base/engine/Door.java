package com.base.engine;

import javax.sound.sampled.Clip;

public class Door
{
	private static final Clip openNoise = ResourceLoader.loadAudio("DSDOROPN.wav");
	private static final Clip closeNoise = ResourceLoader.loadAudio("DSDORCLS.wav");
	
	private static Mesh door;
	
	private Material material;
	private Transform transform;
	private Vector3f closedPos;
	private Vector3f openPos;
	
	private boolean opening;
	private boolean closing;
	private boolean open;
	
	private double startTime;
	private double openTime;
	private double startCloseTime;
	private double closeTime;
	
	public Door(Transform transform, Material material, Vector3f openPosition)
	{
		this.transform = transform;
		this.openPos = openPosition;
		this.closedPos = transform.getPosition();
		this.material = material;
		
		opening = false;
		closing = false;
		open = false;
		startTime = 0;
		openTime = 0;
		closeTime = 0;
		startCloseTime = 0;
		
		if(door == null)
		{
			door = new Mesh();
			
			Vertex[] doorVerts = new Vertex[]{new Vertex(new Vector3f(0,0,0), new Vector2f(0.5f,1)),
					  new Vertex(new Vector3f(0,1,0), new Vector2f(0.5f,0.75f)),
					  new Vertex(new Vector3f(1,1,0), new Vector2f(0.75f,0.75f)),
					  new Vertex(new Vector3f(1,0,0), new Vector2f(0.75f,1)),
					  
					  new Vertex(new Vector3f(0,0,0), new Vector2f(0.73f,1)),
					  new Vertex(new Vector3f(0,1,0), new Vector2f(0.73f,0.75f)),
					  new Vertex(new Vector3f(0,1,0.125f), new Vector2f(0.75f,0.75f)),
					  new Vertex(new Vector3f(0,0,0.125f), new Vector2f(0.75f,1)),

					  new Vertex(new Vector3f(0,0,0.125f), new Vector2f(0.5f,1)),
					  new Vertex(new Vector3f(0,1,0.125f), new Vector2f(0.5f,0.75f)),
					  new Vertex(new Vector3f(1,1,0.125f), new Vector2f(0.75f,0.75f)),
					  new Vertex(new Vector3f(1,0,0.125f), new Vector2f(0.75f,1)),
					  
					  new Vertex(new Vector3f(1,0,0), new Vector2f(0.73f,1)),
					  new Vertex(new Vector3f(1,1,0), new Vector2f(0.73f,0.75f)),
					  new Vertex(new Vector3f(1,1,0.125f), new Vector2f(0.75f,0.75f)),
					  new Vertex(new Vector3f(1,0,0.125f), new Vector2f(0.75f,1))};
			
			int[] doorIndices = new int[] {0,1,2,
				   0,2,3,
				   6,5,4,
				   7,6,4,
				   10,9,8,
				   11,10,8,
				   12,13,14,
				   12,14,15};

			door.addVertices(doorVerts, doorIndices);
		}
	}

	public void open(float time, float delay)
	{
		if(opening || open)
			return;
		
		startTime = (double)Time.getTime()/(double)Time.SECOND;
		openTime = startTime + (double)time;
		startCloseTime = openTime + (double)delay;
		closeTime = startCloseTime + (double)time;
		
		opening = true;
		closing = false;
		AudioUtil.playAudio(openNoise, transform.getPosition().sub(Transform.getCamera().getPos()).length());
	}
	
	public void update()
	{
		if(opening)
		{
			double time = (double)Time.getTime()/(double)Time.SECOND;
			
			if(time < openTime)
			{
				double lerpFactor = (time - startTime)/(openTime - startTime);
				
				transform.setPosition(openPos.lerp(closedPos, (float)lerpFactor));
			}
			else if(time > openTime && time < startCloseTime)
			{
				transform.setPosition(openPos);
				open = true;
			}
			else if(time > startCloseTime && time < closeTime)
			{
				if(!closing)
					AudioUtil.playAudio(closeNoise, transform.getPosition().sub(Transform.getCamera().getPos()).length());
				
				closing = true;
				open = false;
				double lerpFactor = (time - startCloseTime)/(closeTime - startCloseTime);
				
				transform.setPosition(closedPos.lerp(openPos, (float)lerpFactor));
			}
			else
			{
				closing = true;
				opening = false;
				open = false;
			}
		}
		else
			transform.setPosition(closedPos);
	}
	
	public void render()
	{
		Game.updateShader(transform.getTransformation(), transform.getPerspectiveTransformation(), material);
		door.draw();
	}
	
	public boolean isOpen()
	{
		return open;
	}
	
	public Transform getTransform()
	{
		return transform;
	}
	
	public Vector2f getSize()
	{
		if(transform.getRotation().getY() == 0)
			return new Vector2f(1, 0.125f);
		else
			return new Vector2f(0.125f, 1);
	}
	
//	public Vector2f getPosXZ()
//	{
//		return new Vector2f(transform.getPosition().getX(), transform.getPosition().getZ());
//	}
}
