package fr.NVT.TopOneReacher.modules.ressources;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;

public enum Strategies {

	OPTIMUM_ZONE(null, new int[][] { { 4, 0 }, { 2, 0 }, { 1, 0 }, { 3, 0 } }, 1),
	FIVE_CROSS(null, new int[][] { { 0, 2 }, { 2, 0 }, { 2, 2 }, { 1, 1 }, { -1, -1 }, { 3, 3 } }, 2),
	DOUBLE_ALIGN_ONE(null, new int[][] { { -2, -1 }, { 0, -2 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 0, 2 } }, 3),
	DOUBLE_ALIGN_TWO(null, new int[][] { { -2, -1 }, { 0, -2 }, { -1, 0 }, { 0, 1 }, { -3, -2 }, { 1, 2 } }, 3);

	private int[][] ennemie, strategy;
	private int prioritie;

	private Strategies(int[][] ennemie, int[][] strategy, int prioritie) {
		this.ennemie = ennemie;
		this.strategy = strategy;
		this.prioritie = prioritie;
	}

	public static final HashMap<Integer, int[][]> strategies = new HashMap<>();
	public static final HashMap<Integer, int[][]> ennemies = new HashMap<>();
	public static final LinkedHashMap<Integer, Integer> priorities = new LinkedHashMap<>();
	
	public static final HashMap<Integer, int[][]> strategies2D = new HashMap<>();
	public static final HashMap<Integer, int[][]> ennemies2D = new HashMap<>();
	public static final LinkedHashMap<Integer, Integer> priorities2D = new LinkedHashMap<>();

	static {
		HashMap<Integer, Integer> temp_priorities = new HashMap<>();
		int current_id = 0;
		for (Strategies strat : EnumSet.allOf(Strategies.class)) {
			for (byte fst = 1; fst <= 13; fst++)
				for (byte scnd = 0; scnd < 4; scnd++) {
					int[][] res = new int[strat.strategy.length][];
					for (int i = 0; i < strat.strategy.length; i++) {
						res[i] = rotatePos(fst, scnd, strat.strategy[i]);
					}
					strategies.put(current_id, res);
					if ((fst == 1 || fst == 2 || fst == 4 || fst == 5) && scnd == 0)
						strategies2D.put(current_id, res);

					if (strat.ennemie != null) {
						res = new int[strat.ennemie.length][];
						for (int i = 0; i < strat.ennemie.length; i++) {
							res[i] = rotatePos(fst, scnd, strat.ennemie[i]);
						}
						ennemies.put(current_id, res);
						if ((fst == 1 || fst == 2 || fst == 4 || fst == 5) && scnd == 0)
							ennemies2D.put(current_id, res);
					} else {
						ennemies.put(current_id, null);
						if ((fst == 1 || fst == 2 || fst == 4 || fst == 5) && scnd == 0)
							ennemies2D.put(current_id, null);
					}
					temp_priorities.put(current_id, strat.prioritie);
					current_id++;
				}
		}

		while (temp_priorities.size() > 0) {
			int max_prioritie = -1;
			Integer max_key = null;
			for (Integer key : temp_priorities.keySet()) {
				int p = temp_priorities.get(key);
				if (p > max_prioritie) {
					max_prioritie = p;
					max_key = key;
				}
			}
			priorities.put(max_key, max_prioritie);
			if (strategies2D.containsKey(max_key))
				priorities2D.put(max_key, max_prioritie);
			temp_priorities.remove(max_key);
		}
		
	}
	
	private static int[] rotateAroundAxe(byte dir, int[] pos, int rg) {
		int x = pos[0];
		int y = pos[1];
		int z = pos[2];
		switch (dir) {
		// 2D & 3D
		case 1: // 90 - 2, 3, 6, 7
			return new int[] { x - rg, y, z };
		case 2: // 90 - 1, 3, 8, 9
			return new int[] { x, y - rg, z };
		case 3: // 90 - 1, 2, 4, 5
			return new int[] { x, y, z - rg };
		case 4: // 45 - 3, 4, 12, 13
			return new int[] { x - rg, y - rg, z };
		case 5: // 45 - 3, 5, 10, 11
			return new int[] { x + rg, y - rg, z };
		case 6: // 45 - 1, 7, 10, 13
			return new int[] { x, y + rg, z - rg };
		case 7: // 45 - 1, 6, 11, 12
			return new int[] { x, y - rg, z - rg };
		case 8: // 45 - 2, 9, 10, 12
			return new int[] { x + rg, y, z - rg };
		case 9: // 45 - 2, 8, 11, 13
			return new int[] { x - rg, y, z - rg };
		case 10: // 45*45 - 4, 11, 12, 13
			return new int[] { x - rg, y - rg, z - rg };
		case 11: // 45*45 - 4, 10, 12, 13
			return new int[] { x - rg, y - rg, z + rg };
		case 12: // 45*45 - 5, 13, 10, 11
			return new int[] { x - rg, y + rg, z - rg };
		case 13: // 45*45 - 5, 12, 10, 11
			return new int[] { x - rg, y + rg, z + rg };
		default:
			return null;
		}
	}

	private static int[] rotatePos(byte fstAxe, byte scndAxe, int[] pos) {
		int[] scnd = null;
		switch (fstAxe) {
		case 0:
			return new int[] { pos[0], pos[1], 0 };
		case 1: // 90 - 2, 3, 6, 7
			scnd = new int[] { 2, 3, 6, 7 };
			break;
		case 2: // 90 - 1, 3, 8, 9
			scnd = new int[] { 1, 3, 8, 9 };
			break;
		case 3: // 90 - 1, 2, 4, 5
			scnd = new int[] { 1, 2, 4, 5 };
			break;
		case 4: // 45 - 3, 4, 12, 13
			scnd = new int[] { 5, 3, 12, 13 };
			break;
		case 5: // 45 - 3, 5, 10, 11
			scnd = new int[] { 4, 3, 10, 11 };
			break;
		case 6: // 45 - 1, 7, 10, 13
			scnd = new int[] { 1, 7, 10, 13 };
			break;
		case 7: // 45 - 1, 6, 11, 12
			scnd = new int[] { 1, 6, 11, 12 };
			break;
		case 8: // 45 - 2, 9, 10, 12
			scnd = new int[] { 2, 9, 10, 12 };
			break;
		case 9: // 45 - 2, 8, 11, 13
			scnd = new int[] { 2, 8, 11, 13 };
			break;
		case 10: // 45*45 - 4, 11, 12, 13
			scnd = new int[] { 4, 11, 12, 13 };
			break;
		case 11: // 45*45 - 4, 10, 12, 13
			scnd = new int[] { 4, 10, 12, 13 };
			break;
		case 12: // 45*45 - 5, 13, 10, 11
			scnd = new int[] { 5, 13, 10, 11 };
			break;
		case 13: // 45*45 - 5, 12, 10, 11
			scnd = new int[] { 5, 12, 10, 11 };
			break;
		default:
			scnd = null;
		}
		
		int[] firstRotate = rotateAroundAxe(fstAxe, new int[] {0, 0, 0}, pos[0]);
		int[] secondRotate = rotateAroundAxe((byte) scnd[scndAxe], firstRotate, pos[1]);
		return secondRotate;
	}

}
