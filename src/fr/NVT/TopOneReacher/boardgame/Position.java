package fr.NVT.TopOneReacher.boardgame;

public class Position {

	private int x;
	private int y;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getDistance(Position pos) {
		int dx = Math.max(pos.getX(), this.x) - Math.min(pos.getX(), this.x);
		int dy = Math.max(pos.getY(), this.y) - Math.min(pos.getY(), this.y);
		
		return (dx * dx) + (dy * dy);
	}
	
	public static boolean validatePosition(int x, int y, int width, int height) {
		return x >= 0 && y >= 0 && x < width && y < height;
	}
	
	public static boolean validatePosition(Position pos, int width, int height) {
		return pos.x >= 0 && pos.y >= 0 && pos.x < width && pos.y < height;
	}
}
