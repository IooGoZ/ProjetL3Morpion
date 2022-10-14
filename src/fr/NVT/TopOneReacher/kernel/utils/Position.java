package fr.NVT.TopOneReacher.kernel.utils;

public class Position {

	private int x;
	private int y;
	private int z;
	
	public Position(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public int getDistance(Position pos) {
		int dx = Math.max(pos.getX(), this.x) - Math.min(pos.getX(), this.x);
		int dy = Math.max(pos.getY(), this.y) - Math.min(pos.getY(), this.y);
		int dz = Math.max(pos.getZ(), this.z) - Math.min(pos.getZ(), this.z);
		
		return (dx * dx) + (dy * dy) + (dz * dz);
	}
	
	public static boolean validatePosition(int x, int y, int z, int width, int height, int depth) {
		return x >= 0 && y >= 0 && z >= 0 && x < width && y < height && z < depth;
	}
	
	public static boolean validatePosition(Position pos, int width, int height, int depth) {
		if (pos != null)
			return pos.x >= 0 && pos.y >= 0 && pos.z >= 0 && pos.x < width && pos.y < height && pos.z < depth;
		else
			return false;
	}
}
