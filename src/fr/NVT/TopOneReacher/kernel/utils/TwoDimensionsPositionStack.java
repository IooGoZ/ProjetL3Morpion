package fr.NVT.TopOneReacher.kernel.utils;

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
		int len = this.stacksLength[index-shift];
		if (len==0) return null;
		return this.stack[index-shift][len-1];
	}
	
	public Position getPosition(int indexX, int indexY) {
		int len = this.stacksLength[indexX-shift];
		if (len==0 || indexY > (len-1)) return null;
		return this.stack[indexX-shift][len-1-indexY];
	}

}
