package fr.NVT.TopOneReacher.kernel.utils;

public class Position {
	
	private static final int MINIMUM_VALIDATE_POSITION = 0;
	
	private final int x, y, z;
	
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
	
	//Check if position is valid with specific size.
	public static boolean validatePosition(Position pos, int width, int height, int depth) {
		if (pos != null)
			return pos.x >= MINIMUM_VALIDATE_POSITION && pos.y >= MINIMUM_VALIDATE_POSITION && pos.z >= MINIMUM_VALIDATE_POSITION && pos.x < width && pos.y < height && pos.z < depth;
		else
			return false;
	}
}
