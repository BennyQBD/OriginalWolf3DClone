package com.base.engine;

import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.Clip;

public class Level
{
	private static final float SPOT_WIDTH = 1;
	private static final float SPOT_LENGTH = 1;
	private static final float LEVEL_HEIGHT = 1;
	
	private static final float NUM_TEX_X = 4f;
	private static final float NUM_TEX_Y = 4f;
	
	private static final Clip misuseNoise = ResourceLoader.loadAudio("DSOOF.wav");
	
	private static ArrayList<Medkit> removeList;
	private static Player player;
	
	private ArrayList<Vector3f> exitPoints;
	private ArrayList<Integer> exitOffsets;
	private ArrayList<Vector2f> collisionPosStart;
	private ArrayList<Vector2f> collisionPosEnd;
	
	private ArrayList<Door> doors;
	
	private ArrayList<Monster> monsters;
	private ArrayList<Medkit> medkits;
	
	private Mesh geometry;
	private Bitmap level;
	private Material material;
	private Transform transform;
	
	public Level(Bitmap bitmap, Material material)
	{
		Level.removeList = new ArrayList<Medkit>();
		
		this.level = bitmap;
		this.geometry = new Mesh();
		this.doors = new ArrayList<Door>();
		this.exitOffsets = new ArrayList<Integer>();
		this.exitPoints = new ArrayList<Vector3f>();
		this.monsters = new ArrayList<Monster>();
		this.medkits = new ArrayList<Medkit>();
		this.material = material;
		this.transform = new Transform();
		this.collisionPosStart = new ArrayList<Vector2f>();
		this.collisionPosEnd = new ArrayList<Vector2f>();
		
		generateLevel();
		
		Transform.setCamera(player.getCamera());
	}
	
	Random rand = new Random();
	
	public void input()
	{
		if(Input.getMouseDown(0))
			for(Monster monster : monsters)
				if(Math.abs(monster.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < 1.5f)
					monster.damage((int)(30f + rand.nextFloat() * 50f));
		
		player.input();
	}
	
	public void update()
	{
		player.update();
		
		for(Door door : doors)
			door.update();
		
		for(Monster monster : monsters)
			monster.update();
		
		for(Medkit medkit : medkits)
			medkit.update();
		
		for(Medkit medkit : removeList)
			medkits.remove(medkit);
		
		removeList.clear();
	}
	
	public void render()
	{
		Game.updateShader(transform.getTransformation(), transform.getPerspectiveTransformation(), material);
		geometry.draw();
		
		for(Door door : doors)
			door.render();
		
		if(monsters.size() > 0)
			sortMonsters(0, monsters.size() - 1);
		
		for(Monster monster : monsters)
			monster.render();
		
		for(Medkit medkit : medkits)
			medkit.render();
		
		player.render();
	}

	private void sortMonsters(int low, int high) 
	{
		int i = low;
		int j = high;
		
		Monster pivot = monsters.get(low + (high-low)/2);
		float pivotDistance = pivot.getTransform().getPosition().sub(Transform.getCamera().getPos()).length();

		while (i <= j) 
		{
			while (monsters.get(i).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() > pivotDistance) 
			{
		        i++;
		    }
			while (monsters.get(j).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() < pivotDistance) 
		    {
				j--;
		    }

			if (i <= j) 
			{
				Monster temp = monsters.get(i);
				
				monsters.set(i, monsters.get(j));
				monsters.set(j, temp);

				i++;
				j--;
			}
		}

		if (low < j)
			sortMonsters(low, j);
		if (i < high)
			sortMonsters(i, high);
	}
	
	public void openDoors(Vector3f position, boolean playSound)
	{
		boolean worked = false;
		
		for(Door door : doors)
		{
			if(Math.abs(door.getTransform().getPosition().sub(position).length()) < 1.5f)
			{
				worked = true;
				door.open(0.5f, 3f);
			}
		}
		
		if(playSound)
		{
			for(int i = 0; i < exitPoints.size(); i ++)
			{
				if(Math.abs(exitPoints.get(i).sub(position).length()) < 1f)
				{
					Game.loadLevel(exitOffsets.get(i));
				}
			}
		}
		
		if(!worked && playSound)
			AudioUtil.playAudio(misuseNoise, 0);
	}
	
	public Vector3f checkCollisions(Vector3f oldPos, Vector3f newPos, float objectWidth, float objectLength)
	{
		Vector2f collisionVector = new Vector2f(1,1);
		Vector3f movementVector = newPos.sub(oldPos);
		
		if(movementVector.length() > 0)
		{
			Vector2f blockSize = new Vector2f(SPOT_WIDTH, SPOT_LENGTH);
			Vector2f objectSize = new Vector2f(objectWidth, objectLength);
			
			Vector2f oldPos2 = new Vector2f(oldPos.getX(), oldPos.getZ());
			Vector2f newPos2 = new Vector2f(newPos.getX(), newPos.getZ());
			
			for(int i = 0; i < level.getWidth(); i++)
				for(int j = 0; j < level.getHeight(); j++)
					if((level.getPixel(i, j) & 0xFFFFFF) == 0) // If it's a black (wall) pixel
						collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, blockSize.mul(new Vector2f(i,j)), blockSize));
			
			for(Door door : doors)
				collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, door.getTransform().getPosition().getXZ(), door.getSize()));
		}
		
		return new Vector3f(collisionVector.getX(),0,collisionVector.getY());
	}
	
	public Vector2f checkIntersections(Vector2f lineStart, Vector2f lineEnd, boolean hurtMonsters)
	{
		Vector2f nearestIntersect = null;
		
		for(int i = 0; i < collisionPosStart.size(); i++)
		{
			Vector2f collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, collisionPosStart.get(i), collisionPosEnd.get(i));
			
			if(collision != null && (nearestIntersect == null || 
					nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length()))
				nearestIntersect = collision;
		}
		
		for(Door door : doors)
		{
			Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, door.getTransform().getPosition().getXZ(), door.getSize());
		
			if(collision != null && (nearestIntersect == null || 
					nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length()))
				nearestIntersect = collision;
		}
		
		if(hurtMonsters)
		{
			Vector2f monsterIntersect = null;
			Monster nearestMonster = null;
			
			for(Monster monster : monsters)
			{
				Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, monster.getTransform().getPosition().getXZ(), monster.getSize());
				
				if(collision != null && (monsterIntersect == null || 
						monsterIntersect.sub(lineStart).length() > collision.sub(lineStart).length()))
				{
					monsterIntersect = collision;
					nearestMonster = monster;
				}
			}
			
			if(monsterIntersect != null && (nearestIntersect == null || 
					nearestIntersect.sub(lineStart).length() > monsterIntersect.sub(lineStart).length()))
					nearestMonster.damage(player.getDamage());
		}
		
		return nearestIntersect;
	}
		
	private void generateLevel()
	{
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		for(int i = 1; i < level.getWidth() - 1; i++)
		{
			for(int j = 1; j < level.getHeight() - 1; j++)
			{
				if((level.getPixel(i, j) & 0xFFFFFF) != 0) // If it isn't a black (wall) pixel
				{
					if((level.getPixel(i, j) & 0x0000FF) == 16)
					{
						Transform doorTransform = new Transform();
						
						boolean xDoor = (level.getPixel(i, j - 1) & 0xFFFFFF) == 0 && (level.getPixel(i, j + 1) & 0xFFFFFF) == 0;
						boolean yDoor = (level.getPixel(i - 1, j) & 0xFFFFFF) == 0 && (level.getPixel(i + 1, j) & 0xFFFFFF) == 0;
						
						if((yDoor && xDoor) || !(yDoor || xDoor))
						{
							System.err.println("Level Generation Error at (" + i + ", " + j + "): Doors must be between two solid walls.");
							new Exception().printStackTrace();
							System.exit(1);
						}
						
						if(yDoor)
						{
							doorTransform.setPosition(i, 0, j + SPOT_LENGTH / 2);
							doors.add(new Door(doorTransform, material, doorTransform.getPosition().add(new Vector3f(-0.9f,0,0))));
						}
						else if(xDoor)
						{
							doorTransform.setPosition(i + SPOT_LENGTH / 2, 0, j);
							doorTransform.setRotation(0,90,0);
							doors.add(new Door(doorTransform, material, doorTransform.getPosition().add(new Vector3f(0,0,-0.9f))));
						}
					}
					else if((level.getPixel(i, j) & 0x0000FF) == 128)
						monsters.add(new Monster(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
					else if((level.getPixel(i, j) & 0x0000FF) == 1)
						player = new Player(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0.4375f, (j + 0.5f) * SPOT_LENGTH));
					else if((level.getPixel(i, j) & 0x0000FF) == 192)
						medkits.add(new Medkit(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.125f, (j + 0.5f) * SPOT_LENGTH))));
					else if((level.getPixel(i, j) & 0x0000FF) < 128 && (level.getPixel(i, j) & 0x0000FF) > 96)
					{
						int offset = (level.getPixel(i, j) & 0x0000FF) - 96;
						exitPoints.add(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0f, (j + 0.5f) * SPOT_LENGTH));
						exitOffsets.add(offset);
					}
					
					int texX = ((level.getPixel(i,j) & 0x00FF00) >> 8) / 16;
					int texY = texX % 4;
					texX /= 4;
					
					float XHigher = 1f - texX/NUM_TEX_X;
					float XLower = XHigher - 1/NUM_TEX_X;
					float YHigher = 1f - texY/NUM_TEX_Y;
					float YLower = YHigher - 1/NUM_TEX_Y;
					
					//Generate Floor
					indices.add(vertices.size() + 2);
					indices.add(vertices.size() + 1);
					indices.add(vertices.size() + 0);
					indices.add(vertices.size() + 3);
					indices.add(vertices.size() + 2);
					indices.add(vertices.size() + 0);
					
					vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, j * SPOT_LENGTH), new Vector2f(XLower,YLower)));
					vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, j * SPOT_LENGTH), new Vector2f(XHigher,YLower)));
					vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new Vector2f(XHigher,YHigher)));
					vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new Vector2f(XLower,YHigher)));
					
					//Generate Ceiling
					indices.add(vertices.size() + 0);
					indices.add(vertices.size() + 1);
					indices.add(vertices.size() + 2);
					indices.add(vertices.size() + 0);
					indices.add(vertices.size() + 2);
					indices.add(vertices.size() + 3);
					
					vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(XLower,YLower)));
					vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(XHigher,YLower)));
					vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(XHigher,YHigher)));
					vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(XLower,YHigher)));
					
					texX = ((level.getPixel(i,j) & 0xFF0000) >> 16) / 16;
					texY = texX % 4;
					texX /= 4;
					
					XHigher = 1f - texX/NUM_TEX_X;
					XLower = XHigher - 1/NUM_TEX_X;
					YHigher = 1f - texY/NUM_TEX_Y;
					YLower = YHigher - 1/NUM_TEX_Y;
					
					//Generate Walls
					if((level.getPixel(i, j - 1) & 0xFFFFFF) == 0)
					{
						collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, j * SPOT_LENGTH));
						collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));
						
						indices.add(vertices.size() + 0);
						indices.add(vertices.size() + 1);
						indices.add(vertices.size() + 2);
						indices.add(vertices.size() + 0);
						indices.add(vertices.size() + 2);
						indices.add(vertices.size() + 3);
						
						vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, j * SPOT_LENGTH), new Vector2f(XHigher,YHigher)));
						vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, j * SPOT_LENGTH), new Vector2f(XLower,YHigher)));
						vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(XLower,YLower)));
						vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(XHigher,YLower)));
					}
					if((level.getPixel(i, j + 1) & 0xFFFFFF) == 0)
					{
						collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
						collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
						
						indices.add(vertices.size() + 2);
						indices.add(vertices.size() + 1);
						indices.add(vertices.size() + 0);
						indices.add(vertices.size() + 3);
						indices.add(vertices.size() + 2);
						indices.add(vertices.size() + 0);
						
						vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new Vector2f(XHigher,YHigher)));
						vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new Vector2f(XLower,YHigher)));
						vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(XLower,YLower)));
						vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(XHigher,YLower)));
					}
					if((level.getPixel(i - 1, j) & 0xFFFFFF) == 0)
					{
						collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, j * SPOT_LENGTH));
						collisionPosEnd.add(new Vector2f(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
						
						indices.add(vertices.size() + 2);
						indices.add(vertices.size() + 1);
						indices.add(vertices.size() + 0);
						indices.add(vertices.size() + 3);
						indices.add(vertices.size() + 2);
						indices.add(vertices.size() + 0);
						
						vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, j * SPOT_LENGTH), new Vector2f(XHigher,YHigher)));
						vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new Vector2f(XLower,YHigher)));
						vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(XLower,YLower)));
						vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(XHigher,YLower)));
					}
					if((level.getPixel(i + 1, j) & 0xFFFFFF) == 0)
					{
						collisionPosStart.add(new Vector2f((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));
						collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
						
						indices.add(vertices.size() + 0);
						indices.add(vertices.size() + 1);
						indices.add(vertices.size() + 2);
						indices.add(vertices.size() + 0);
						indices.add(vertices.size() + 2);
						indices.add(vertices.size() + 3);
						
						vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, j * SPOT_LENGTH), new Vector2f(XHigher,YHigher)));
						vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new Vector2f(XLower,YHigher)));
						vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(XLower,YLower)));
						vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(XHigher,YLower)));
					}
				}
			}
		}
		
		Vertex[] vertArray = new Vertex[vertices.size()];
		Integer[] intArray = new Integer[indices.size()];
		
		vertices.toArray(vertArray);
		indices.toArray(intArray);
		
		geometry.addVertices(vertArray, Util.toIntArray(intArray));
	}
	
	public ArrayList<Monster> getMonsters()
	{
		return monsters;
	}
	
	public static Player getPlayer()
	{
		return player;
	}
	
	public static void removeMedkit(Medkit medkit)
	{
		removeList.add(medkit);
	}
}
