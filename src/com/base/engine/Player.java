package com.base.engine;

import java.util.Random;

import javax.sound.sampled.Clip;

public class Player
{	
	private static final float GUN_SIZE = 0.10f;
	private static final float GUN_OFFSET = -0.1325f;
	private static final float GUN_FIRE_SIZE = 0.4f;
	private static final float GUN_FIRE_OFFSET_X = -0.00375f;
	private static final float GUN_FIRE_OFFSET = 0.07125f;
	private static final float GUN_FIRE_ANIMATIONTIME = 0.05f;
	
	private static final float MOUSE_SENSITIVITY = 0.3f;
	private static final float MAX_LOOK_ANGLE = 30f;
	private static final float MIN_LOOK_ANGLE = -17f;
	private static final float MOVE_SPEED = 5f;
	private static final float PLAYER_WIDTH = 0.2f;
	private static final float DAMAGE_MIN = 20f;
	private static final float DAMAGE_RANGE = 40f;
	
	private static final Vector2f centerPosition = new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
	private static final Vector3f zeroVector = new Vector3f(0,0,0);
	private static final Clip gunNoise = ResourceLoader.loadAudio("DSPISTOL.wav");
	private static final Clip painNoise = ResourceLoader.loadAudio("DSPLPAIN.wav");
	private static final Clip deathNoise = ResourceLoader.loadAudio("DSPLDETH.wav");
	
	private static Mesh gunMesh;
	private static Material gunMaterial;
	private static Material gunFireMaterial;
	private static Transform gunTransform;
	
	private Camera playerCamera;
	private Random rand;
	private Vector3f movementVector;
	
	private static boolean mouseLocked;
	private double gunFireTime;
	private float width;
	private int health;

	public Player(Vector3f position)
	{
		if(gunMesh == null)
		{
			gunMesh = new Mesh();
			
			float sizeY = GUN_SIZE;
			float sizeX = (float)((double)sizeY / (1.0379746835443037974683544303797 * 2.0));
			
			float offsetX = 0.05f;
			float offsetY = 0.01f;
			
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
			
			gunMesh.addVertices(verts, indices);
		}
		
		if(gunMaterial == null)
		{
			gunMaterial = new Material(ResourceLoader.loadTexture("PISGB0.png"));
		}
		
		if(gunFireMaterial == null)
		{
			gunFireMaterial = new Material(ResourceLoader.loadTexture("PISFA0.png"));
		}
		
		playerCamera = new Camera(position);
		health = 100;
		
		if(gunTransform == null)
		{
			gunTransform = new Transform(playerCamera.getPos());
		}
		
		gunFireTime = 0;
		mouseLocked = true;
		Input.setMousePosition(centerPosition);
		Input.setCursor(false);
		movementVector = zeroVector;
		width = PLAYER_WIDTH;
		rand = new Random();
	}
	
	private float upAngle = 0;
	
	public void input()
	{
		//showGunFire = false;
		
		if(Input.getKeyDown(Input.KEY_E))
			Game.getLevel().openDoors(Transform.getCamera().getPos(), true);
		
		if(Input.getKey(Input.KEY_ESCAPE))
		{
			Input.setCursor(true);
			mouseLocked = false;
		}
		if(Input.getMouseDown(0))
		{
			if(!mouseLocked)
			{
				Input.setMousePosition(centerPosition);
				Input.setCursor(false);
				mouseLocked = true;
			}
			else
			{
				Vector2f shootDirection = playerCamera.getForward().getXZ().normalized();
				
				Vector2f lineStart = playerCamera.getPos().getXZ();
				Vector2f lineEnd = lineStart.add(shootDirection.mul(1000.0f));
				
				Game.getLevel().checkIntersections(lineStart, lineEnd, true);
				AudioUtil.playAudio(gunNoise, 0);
				gunFireTime = (double)Time.getTime()/Time.SECOND;
			}
		}
		
		movementVector = zeroVector;
		
		if(Input.getKey(Input.KEY_W))
			movementVector = movementVector.add(playerCamera.getForward());
		if(Input.getKey(Input.KEY_S))
			movementVector = movementVector.sub(playerCamera.getForward());
		if(Input.getKey(Input.KEY_A))
			movementVector = movementVector.add(playerCamera.getLeft());
		if(Input.getKey(Input.KEY_D))
			movementVector = movementVector.add(playerCamera.getRight());
		
		if(mouseLocked)
		{
			Vector2f deltaPos = Input.getMousePosition().sub(centerPosition);
			
			boolean rotY = deltaPos.getX() != 0;
			boolean rotX = deltaPos.getY() != 0;
			
			if(rotY)
				playerCamera.rotateY(deltaPos.getX() * MOUSE_SENSITIVITY);
			if(rotX)
			{
				float amt = -deltaPos.getY() * MOUSE_SENSITIVITY;
				if(amt + upAngle > -MIN_LOOK_ANGLE)
				{
					playerCamera.rotateX(-MIN_LOOK_ANGLE - upAngle);
					upAngle = -MIN_LOOK_ANGLE;
				}
				else if(amt + upAngle < -MAX_LOOK_ANGLE)
				{
					playerCamera.rotateX(-MAX_LOOK_ANGLE - upAngle);
					upAngle = -MAX_LOOK_ANGLE;
				}
				else
				{
					playerCamera.rotateX(amt);
					upAngle += amt;
				}
			}
				
			if(rotY || rotX)
				Input.setMousePosition(new Vector2f(Window.getWidth()/2, Window.getHeight()/2));
		}
	}
	
	public void update()
	{
		
		float movAmt = (float)(MOVE_SPEED * Time.getDelta());
		
		movementVector.setY(0);
		
		Vector3f oldPos = Transform.getCamera().getPos();
		Vector3f newPos = oldPos.add(movementVector.normalized().mul(movAmt));
		
		Vector3f collisionVector = Game.getLevel().checkCollisions(oldPos, newPos, width, width);
		
		movementVector = movementVector.normalized().mul(collisionVector);
			
		if(movementVector.length() > 0)
			playerCamera.move(movementVector, movAmt);
		
		//Gun movement
		gunTransform.setScale(1,1,1);
		gunTransform.setPosition(playerCamera.getPos().add(playerCamera.getForward().normalized().mul(0.105f)));
		gunTransform.getPosition().setY(gunTransform.getPosition().getY() + GUN_OFFSET);
		
		Vector3f playerDistance = gunTransform.getPosition().sub(Transform.getCamera().getPos());
		
		Vector3f orientation = playerDistance.normalized();
		
		float angle = (float)Math.toDegrees(Math.atan(orientation.getZ()/orientation.getX()));
		
		if(orientation.getX() > 0)
			angle = 180 + angle;
		
		gunTransform.setRotation(0,angle + 90,0);
	}
	
	public void render()
	{
		Game.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunMaterial);
		gunMesh.draw();
		
		if((double)Time.getTime()/Time.SECOND < gunFireTime + GUN_FIRE_ANIMATIONTIME)
		{
			gunTransform.setPosition(playerCamera.getPos().add(playerCamera.getForward().normalized().mul(0.101f)));
			gunTransform.setPosition(gunTransform.getPosition().add(playerCamera.getLeft().normalized().mul(GUN_FIRE_OFFSET_X)));
			gunTransform.getPosition().setY(gunTransform.getPosition().getY() + GUN_OFFSET + GUN_FIRE_OFFSET);
			
			gunTransform.setScale(GUN_FIRE_SIZE);
			
			Game.updateShader(gunTransform.getTransformation(), gunTransform.getPerspectiveTransformation(), gunFireMaterial);
			gunMesh.draw();
		}
	}
	
	public void damage(int amt)
	{
		health -= amt;
		
		if(health > 100)
			health = 100;
		
		if(health <= 0)
		{
			AudioUtil.playAudio(deathNoise, 0);
			System.out.println("Game Over!");
			Game.setIsRunning(false);
		}
		else
		{
			if(amt > 0)
				AudioUtil.playAudio(painNoise, 0);
			System.out.println(health + "/100");
		}
	}
	
	public Camera getCamera()
	{
		return playerCamera;
	}

	public void setCamera(Camera playerCamera)
	{
		this.playerCamera = playerCamera;
	}
	
	public Vector2f getSize()
	{
		return new Vector2f(PLAYER_WIDTH, PLAYER_WIDTH);
	}
	
	public int getDamage()
	{
		return (int)(DAMAGE_MIN + rand.nextFloat() * DAMAGE_RANGE);
	}
	
	public int getHealth()
	{
		return health;
	}
}
