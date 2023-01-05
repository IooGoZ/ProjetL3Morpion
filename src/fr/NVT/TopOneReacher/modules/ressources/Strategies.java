package fr.NVT.TopOneReacher.modules.ressources;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;

public enum Strategies {
	
	
	OPTIMUM_ZONE(null, new int[][] {{4, 0}, {2, 0}, {1, 0}, {3, 0}}, 1),
	FIVE_CROSS_DIAGONAL(null, new int[][] {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}}, 2),
	FIVE_CROSS(null, new int[][] {{1, 0}, {-1, 0}, {0, -1}, {0, 1}}, 2);
	
	private int[][] ennemie, strategy;
	private int prioritie;
	
	private Strategies(int [][] ennemie, int[][] strategy, int prioritie) {
		this.ennemie = ennemie;
		this.strategy = strategy;
		this.prioritie = prioritie;
	}
	
	public static final HashMap<Integer, int[][]> strategies = new HashMap<>();
	public static final HashMap<Integer, int[][]> ennemies = new HashMap<>();
	public static final LinkedHashMap<Integer, Integer> priorities = new LinkedHashMap<>();
	
	static {
		HashMap<Integer, Integer> temp_priorities = new HashMap<>();
		int current_id = 0;
		for (Strategies strat : EnumSet.allOf(Strategies.class))
        {
			for (byte fst = 1; fst <= 13; fst++)
				for (byte scnd = 4; scnd <= 9; scnd++) {
					int[][] res = new int[strat.strategy.length][];
					for (int i = 0; i < strat.strategy.length; i++) {
						res[i] = rotatePos(fst, scnd, strat.strategy[i]);
					}
					strategies.put(current_id, res);
					
					if (strat.ennemie != null) {
						res = new int[strat.ennemie.length][];
						for (int i = 0; i < strat.ennemie.length; i++) {
							res[i] = rotatePos(fst, scnd, strat.ennemie[i]);
						}
						ennemies.put(current_id, res);
					} else ennemies.put(current_id, null);
					temp_priorities.put(current_id, strat.prioritie);
					current_id++;
				}
        }
		
		while (temp_priorities.size() > 0) {
			int max_prioritie = -1;
			Integer max_key = null;
			for (Integer key : temp_priorities.keySet()) {
				int p = temp_priorities.get(key);
				if (p >= max_prioritie) {
					max_prioritie = p;
					max_key = key;
				}
			}
			priorities.put(max_key, max_prioritie);
			temp_priorities.remove(max_key);
		}
			
	}
	
	private static int[] rotateAroundAxe(byte dir, int[] pos, int rg) {
		int x = pos[0];
		int y = pos[1];
		int z = pos[2];
		switch(dir) {
		//2D & 3D
		case 1 : //90
			return new int[] {x-rg, y, z};
		case 2 : //90
			return new int[] {x, y-rg, z};
		case 3 : //90
			return new int[] {x, y, z-rg};
		case 4 : //45
			return new int[] {x-rg, y-rg, z};
		case 5 : //45
			return new int[] {x+rg, y-rg, z};
		case 6 : //45
			return new int[] {x, y+rg, z-rg};
		case 7 : //45
			return new int[] {x, y-rg, z-rg};
		case 8 : //45
			return new int[] {x+rg, y, z-rg};
		case 9 : //45
			return new int[] {x-rg, y, z-rg};
		case 10 : //45*45
			return new int[] {x-rg, y-rg, z-rg};
		case 11 : //45*45
			return new int[] {x-rg, y-rg, z+rg};
		case 12 : //45*45
			return new int[] {x-rg, y+rg, z-rg};
		case 13 : //45*45
			return new int[] {x-rg, y+rg, z+rg};
		default : //45*45
			return null;
		}
	}
	
	private static int[] rotatePos(byte fstAxe, byte scndAxe, int[] pos) {
		return rotateAroundAxe(scndAxe, rotateAroundAxe(fstAxe, new int[] {0, 0, 0}, pos[0]), pos[1]);
	}
	
	
	

}
