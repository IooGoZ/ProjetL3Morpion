package fr.NVT.TopOneReacher.kernel.utils;

import fr.NVT.TopOneReacher.kernel.boardgame.Position;

public class TwoDimensionsPositionStack {

	private final int maxLentgth;
	
	private Position[][] stack;
	private int[] stacksLength;
	
	
	public TwoDimensionsPositionStack(int maxIndex, int maxLentgth) {
		this.maxLentgth = maxLentgth;
		this.stack = new Position[maxIndex][maxLentgth];
		this.stacksLength = new int[this.maxLentgth];
		
		for (int i = 0; i < maxIndex; i++)
			this.stacksLength[i] = 0;
	}
	
	public boolean add(int index, Position value) {
		int len = stacksLength[index];
		if (len != maxLentgth) {
			this.stack[index][len] = value;
			this.stacksLength[index]++;
			return true;
		}
		return false;
	}
	
	public Position getLastPosition(int index) {
		return this.stack[index][this.stacksLength[index]-1];
	}
	
	public Position getPosition(int indexX, int indexY) {
		return this.stack[indexX][this.stacksLength[indexX]-1-indexY];
	}

}
