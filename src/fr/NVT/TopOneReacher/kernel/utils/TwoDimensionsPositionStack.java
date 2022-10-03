package fr.NVT.TopOneReacher.kernel.utils;

import fr.NVT.TopOneReacher.kernel.boardgame.Position;

public class TwoDimensionsPositionStack {

	
	private static final int DEFAULT_VALUE_STACK_LENGHT = 0;
	
	private final int maxLentgth;
	
	private Position[][] stack;
	private int[] stacksLength;
	
	private final int shift;
	
	public TwoDimensionsPositionStack(int maxIndex, int maxLentgth, int shift) {
		this.maxLentgth = maxLentgth;
		this.stack = new Position[maxIndex][maxLentgth];
		this.stacksLength = new int[this.maxLentgth];
		this.shift = shift;
		
		for (int i = 0; i < maxIndex; i++)
			this.stacksLength[i] = DEFAULT_VALUE_STACK_LENGHT;
	}
	
	public boolean add(int index, Position value) {
		int len = stacksLength[index-shift];
		if (len < maxLentgth) {
			this.stack[index-shift][len] = value;
			this.stacksLength[index-shift]++;
			return true;
		}
		return false;
	}
	
	public Position getLastPosition(int index) {
		return this.stack[index-shift][this.stacksLength[index-shift]-1];
	}
	
	public Position getPosition(int indexX, int indexY) {
		return this.stack[indexX-shift][this.stacksLength[indexX-shift]-1-indexY];
	}

}
