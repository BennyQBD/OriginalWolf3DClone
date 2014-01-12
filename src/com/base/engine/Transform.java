package com.base.engine;

public class Transform
{
	private static Camera camera;
	
	private static float zNear;
	private static float zFar;
	private static float width;
	private static float height;
	private static float fov;
	
	private Vector3f position;
	private Vector3f rotation;
	private Vector3f scale;
	
	public Transform()
	{
		this(new Vector3f(0,0,0));
	}
	
	public Transform(Vector3f position)
	{
		this.position = position;
		this.rotation = new Vector3f(0,0,0);
		this.scale = new Vector3f(1,1,1);
	}
	
	public Matrix4f getTransformation()
	{
		Matrix4f translationMatrix = new Matrix4f().initTranslation(position.getX(), position.getY(), position.getZ());
		Matrix4f rotationMatrix = new Matrix4f().initRotation(rotation.getX(), rotation.getY(), rotation.getZ());
		Matrix4f scaleMatrix = new Matrix4f().initScale(scale.getX(), scale.getY(), scale.getZ());
		
		return translationMatrix.mul(rotationMatrix.mul(scaleMatrix));
	}
	
	public Matrix4f getPerspectiveTransformation()
	{	
		return getPerspectiveCameraMatrix().mul(getTransformation());
	}
	
	public Matrix4f getOrhographicTransformation()
	{
		return getOrthographicCameraMatrix().mul(getTransformation());
	}
	
	public static Matrix4f getOrthographicMatrix()
	{
		return new Matrix4f().initOrthographicProjection(-width/2, width/2, -height/2, height/2, zNear, zFar);
	}
	
	public static Matrix4f getOrthographicCameraMatrix()
	{
		Matrix4f cameraRotation = new Matrix4f().initCamera(camera.getForward(), camera.getUp());
		Matrix4f cameraTranslation = new Matrix4f().initTranslation(-camera.getPos().getX(), -camera.getPos().getY(), -camera.getPos().getZ());
		
		return getOrthographicMatrix().mul(cameraRotation.mul(cameraTranslation));
	}
	
	public static Matrix4f getPerspectiveCameraMatrix()
	{
		Matrix4f cameraRotation = new Matrix4f().initCamera(camera.getForward(), camera.getUp());
		Matrix4f cameraTranslation = new Matrix4f().initTranslation(-camera.getPos().getX(), -camera.getPos().getY(), -camera.getPos().getZ());
		
		return getPerspectiveMatrix().mul(cameraRotation.mul(cameraTranslation));
	}
	
	public static Matrix4f getPerspectiveMatrix()
	{
		return new Matrix4f().initPerspectiveProjection(fov, width, height, zNear, zFar);
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public static void setProjection(float fov, float width, float height, float zNear, float zFar)
	{
		Transform.fov = fov;
		Transform.width = width;
		Transform.height = height;
		Transform.zNear = zNear;
		Transform.zFar = zFar;
	}
	
	public void setPosition(Vector3f position)
	{
		this.position = position;
	}
	
	public void setPosition(float x, float y, float z)
	{
		this.position = new Vector3f(x, y, z);
	}

	public Vector3f getRotation()
	{
		return rotation;
	}

	public void setRotation(Vector3f rotation)
	{
		this.rotation = rotation;
	}
	
	public void setRotation(float x, float y, float z)
	{
		this.rotation = new Vector3f(x, y, z);
	}

	public Vector3f getScale()
	{
		return scale;
	}

	public void setScale(Vector3f scale)
	{
		this.scale = scale;
	}
	
	public void setScale(float x, float y, float z)
	{
		this.scale = new Vector3f(x, y, z);
	}
	
	public void setScale(float amt)
	{
		setScale(amt,amt,amt);
	}

	public static Camera getCamera()
	{
		return camera;
	}

	public static void setCamera(Camera camera)
	{
		Transform.camera = camera;
	}
}
