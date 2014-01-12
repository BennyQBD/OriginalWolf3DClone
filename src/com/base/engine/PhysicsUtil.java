package com.base.engine;

public class PhysicsUtil
{
//	public static Vector2f rectCollide(Vector2f oldPos, Vector2f newPos, Vector2f size1, Vector2f pos2, Vector2f size2)
//	{
//		Vector2f result = new Vector2f(1,1);
//		
//		if(!(newPos.getX() + size1.getX() < pos2.getX() || 
//			newPos.getX() - size1.getX() > (pos2.getX()/size2.getX() + size2.getX()) * size2.getX() ||
//			oldPos.getY() + size1.getY() < pos2.getY() || 
//			oldPos.getY() - size1.getY() > (pos2.getY()/size2.getY() + size2.getY()) * size2.getY()))
//				result.setX(0);
//		
//		if(!(oldPos.getX() + size1.getX() < pos2.getX() || 
//			oldPos.getX() - size1.getX() > (pos2.getX()/size2.getX() + size2.getX())  * size2.getX() ||
//			newPos.getY() + size1.getY() < pos2.getY() || 
//			newPos.getY() - size1.getY() > (pos2.getY()/size2.getY() + size2.getY()) * size2.getY()))
//				result.setY(0);
//			
//		return result;
//	}
	
	public static Vector2f rectCollide(Vector2f oldPos, Vector2f newPos, Vector2f size1, Vector2f pos2, Vector2f size2)
	{
		Vector2f result = new Vector2f(1,1);
		
		if(!(newPos.getX() + size1.getX() < pos2.getX() || 
			newPos.getX() - size1.getX() > pos2.getX() + (size2.getX() * size2.getX()) ||
			oldPos.getY() + size1.getY() < pos2.getY() || 
			oldPos.getY() - size1.getY() > pos2.getY() + (size2.getY() * size2.getY())))
				result.setX(0);
		
		if(!(oldPos.getX() + size1.getX() < pos2.getX() || 
			oldPos.getX() - size1.getX() > pos2.getX() + (size2.getX()  * size2.getX()) ||
			newPos.getY() + size1.getY() < pos2.getY() || 
			newPos.getY() - size1.getY() > pos2.getY() + (size2.getY() * size2.getY())))
				result.setY(0);
			
		return result;
	}
	
	public static Vector2f lineIntersect(Vector2f a1, Vector2f a2, Vector2f b1, Vector2f b2)
	{
		Vector2f line1 = a2.sub(a1);
		Vector2f line2 = b2.sub(b1);
		
		float cross = line1.cross(line2);
		
		Vector2f pointDistance = b1.sub(a1);
		
		if(cross == 0)
			return null;
		
		float crossFactor1 = pointDistance.cross(line2)/cross;
		float crossFactor2 = pointDistance.cross(line1)/cross;
		
		if(0.0f < crossFactor1 && crossFactor1 < 1.0f && 0.0f < crossFactor2 && crossFactor2 < 1.0f)
			return a1.add(line1.mul(crossFactor1));
		
		return null;
	}
	
//	Vector2f collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, wallBottom, wallBottom.add(wallX));
//	
//	if(collision != null && (nearestIntersect == null || 
//			nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length()))
//		nearestIntersect = collision;
//	
//	collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, wallBottom, wallBottom.add(wallY));
//	
//	if(collision != null && (nearestIntersect == null || 
//			nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length()))
//		nearestIntersect = collision;
//	
//	collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, wallBottom.add(wallX), wallTop);
//	
//	if(collision != null && (nearestIntersect == null || 
//			nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length()))
//		nearestIntersect = collision;
//	
//	collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, wallBottom.add(wallY), wallTop);
//	
//	if(collision != null && (nearestIntersect == null || 
//			nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length()))
//		nearestIntersect = collision;
	
	public static Vector2f lineIntersectRect(Vector2f lineStart, Vector2f lineEnd, Vector2f rectStart, Vector2f rectSize)
	{
		Vector2f result = null;
		
		Vector2f collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, rectStart, new Vector2f(rectStart.getX() + rectSize.getX(), rectStart.getY()));
		
		if(collision != null && (result == null || 
				result.sub(lineStart).length() > collision.sub(lineStart).length()))
			result = collision;
		
		collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, rectStart, new Vector2f(rectStart.getX(), rectStart.getY() + rectSize.getY()));
		
		if(collision != null && (result == null || 
				result.sub(lineStart).length() > collision.sub(lineStart).length()))
			result = collision;
		
		collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, new Vector2f(rectStart.getX() + rectSize.getX(), rectStart.getY()), rectStart.add(rectSize));
		
		if(collision != null && (result == null || 
				result.sub(lineStart).length() > collision.sub(lineStart).length()))
			result = collision;
		
		collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, new Vector2f(rectStart.getX(), rectStart.getY() + rectSize.getY()), rectStart.add(rectSize));
		
		if(collision != null && (result == null || 
				result.sub(lineStart).length() > collision.sub(lineStart).length()))
			result = collision;
		
		return result;
	}
}
