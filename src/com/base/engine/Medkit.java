package com.base.engine;

import javax.sound.sampled.Clip;

public class Medkit
{
	private static final float PICKUP_THRESHHOLD = 0.75f;
	private static final int HEAL_AMOUNT = 25;
	
	private static final Clip pickupNoise = ResourceLoader.loadAudio("DSITEMUP.wav");
	
	private static Mesh mesh;
	private static Material material;
	
	private Transform transform;
	
	public Medkit(Transform transform)
	{
		if(mesh == null)
		{
			mesh = new Mesh();
			
			float sizeY = 0.4f;
			float sizeX = (float)((double)sizeY / (0.67857142857142857142857142857143 * 4.0));
			
			float offsetX = 0.05f;
			float offsetY = 0.00f;
			
			float texMinX = -offsetX;
			float texMaxX = -1 - offsetX;
			float texMinY = -offsetY;
			float texMaxY = 1 - offsetY;
			
			Vertex[] verts = new Vertex[]{new Vertex(new Vector3f(-sizeX,0,0), new Vector2f(texMaxX,texMaxY)),
										  new Vertex(new Vector3f(-sizeX,sizeY,0), new Vector2f(texMaxX,texMinY)),
										  new Vertex(new Vector3f(sizeX,sizeY,0), new Vector2f(texMinX,texMinY)),
										  new Vertex(new Vector3f(sizeX,0,0), new Vector2f(texMinX,texMaxY))};
			
			int[] indices = new int[] {0,1,2,
					   				   0,2,3};
			
			mesh.addVertices(verts, indices);
		}
		
		if(material == null)
		{
			material = new Material(ResourceLoader.loadTexture("MEDIA0.png"));
		}
		
		this.transform = transform;
	}
	
	public void update()
	{
		Vector3f playerDistance = transform.getPosition().sub(Transform.getCamera().getPos());
		
		Vector3f orientation = playerDistance.normalized();
		float distance = playerDistance.length();
		
		float angle = (float)Math.toDegrees(Math.atan(orientation.getZ()/orientation.getX()));
		
		if(orientation.getX() > 0)
			angle = 180 + angle;
		
		transform.setRotation(0,angle + 90,0);
		
		if(distance < PICKUP_THRESHHOLD && Level.getPlayer().getHealth() < 100)
		{
			Level.getPlayer().damage(-HEAL_AMOUNT);
			Level.removeMedkit(this);
			AudioUtil.playAudio(pickupNoise, 0);
		}
	}
	
	public void render()
	{
		Game.updateShader(transform.getTransformation(), transform.getPerspectiveTransformation(), material);
		mesh.draw();
	}
}
