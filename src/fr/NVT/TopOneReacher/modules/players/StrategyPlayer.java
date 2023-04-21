package fr.NVT.TopOneReacher.modules.players;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.ressources.DirectionPosition;
import fr.NVT.TopOneReacher.modules.ressources.OptimumZone;
import fr.NVT.TopOneReacher.modules.ressources.PlayerUtils;
import fr.NVT.TopOneReacher.modules.ressources.Strategies;

public class StrategyPlayer extends VPlayer{

	private int nb_stroke = 0;
	private LinkedList<Position> next_strokes = new LinkedList<>();
	
	
	private boolean is_3D;
	private Board board;
	private LinkedHashMap<Integer, Integer> priorities;
	private HashMap<Integer, int[][]> strategies;
	private HashMap<Integer, int[][]> ennemies;
	
	public StrategyPlayer(Game game, String name) {
		super(game, name);
		this.is_3D = game.is3D();
		
		
		if (is_3D) {
			priorities = Strategies.priorities;
			strategies = Strategies.strategies;
			ennemies = Strategies.ennemies;
		} else {
			priorities = Strategies.priorities2D;
			strategies = Strategies.strategies2D;
			ennemies = Strategies.ennemies2D;
		}
	}
	
	private void evaluateDP(DirectionPosition dp, HashMap<DirectionPosition, OptimumZone> notes) {
		int id = board.getPawnAtPosition(dp.getPos());
		if (PlayerUtils.zoneIsOpen(board, id, dp)) {
			byte dp_byte = PlayerUtils.dpToByte(board, dp);
			if (dp_byte == -1) return;
			OptimumZone oz = OptimumZone.getOptimumZone(dp_byte);
			notes.put(dp, oz);
		}
	}
	
	private void evalutateDefensePosition(Position pos, HashMap<DirectionPosition, OptimumZone> notes) {
		int dir_max = 4;
		if (is_3D) 
			dir_max = 13;
		for (byte dir = 1; dir <= dir_max; dir++) {
			//On réalise l'evaluation des deux zones.
			evaluateDP(new DirectionPosition(pos, dir, (byte) 1), notes);
			evaluateDP(new DirectionPosition(pos, dir, (byte) 0), notes);
		}
	}
	
	private boolean evaluateDefense() {
		HashMap<DirectionPosition, OptimumZone> notes = new HashMap<DirectionPosition, OptimumZone>();
		
		//On évalue toutes les positions jouées
		for (int j = 0; j <= this.board.getLastPositionsSize(); j ++) {
			Position[] positions = board.getLastPositions(j);
			if (positions == null)
				break;
			else
				for(int i = 0; i < super.getGame().getNbPlayers(); i++) {
					//Récupération de la position
					Position evalpos = positions[i];
					//evaluation
					if (evalpos != null && j != super.getId())
						evalutateDefensePosition(evalpos, notes);
				}
		}
		
		DirectionPosition max_dp = null;
		int max_note = -1;
		for (DirectionPosition dp : notes.keySet()) {
			OptimumZone oz = notes.get(dp);
			int note = oz.getEnnemiePrioritie();
			if (max_dp == null || note > max_note) {
				max_note = note;
				max_dp = dp;
			}
		}
		
		if (max_note > 450) {
			//On calcule la position optimum
			OptimumZone oz = notes.get(max_dp);
			byte dp_byte = PlayerUtils.dpToByte(board, max_dp);
			boolean reversable = oz.getReversableValue() == dp_byte;
			byte rg;
			if (reversable) rg = (byte) (max_dp.getRgMax() - (oz.getOptimumDefense() - 1));
			else rg = (byte) (max_dp.getRgMin() + (oz.getOptimumDefense() - 1));
			
			
			next_strokes.addFirst(Board.getCheckPosition(max_dp.getDir(), max_dp.getPos(), rg));
			return true;
		}
		return false;
	}
	
	private boolean strategyIsAlwaysValid() {
		if (next_strokes.size() == 0) return false;
		for (Position pos : next_strokes)
			if (board.getPawnAtPosition(pos) != Board.PAWN_NONE)
				return false;
		return true;
	}
	

	@Override
	public Position loop(Board board) {
		this.board = board;
		if (nb_stroke == 0 && super.getId() == 1) {
			int x = board.getWidth()/2 + PlayerUtils.randomInt(-3, 3);
			int y = board.getHeight()/2 + PlayerUtils.randomInt(-3, 3);
			int z = board.getDepth()/2 + PlayerUtils.randomInt(-3, 3);
			next_strokes.addFirst(new Position(x, y, z));
		} else if (!evaluateDefense() && !strategyIsAlwaysValid()) {
			next_strokes.clear();
			//On évalue une nouvelle strategie
			Position[] poss = evaluateStrategies();
			for (int i = poss.length-1; i >= 0; i--)
				if (board.getPawnAtPosition(poss[i]) == Board.PAWN_NONE)
					next_strokes.addFirst(poss[i]);
		}
		
		nb_stroke++;
		if (next_strokes.size() != 0) {
			Position pos = next_strokes.getFirst();
			next_strokes.removeFirst();
			return pos;
		}
		return PlayerUtils.getRandomPosition(board);
	}
	
	private Position convertRelativePos(Position pos, int[] pt) {
		return new Position(pos.getX()-pt[0], pos.getY()-pt[1], pos.getZ()-pt[2]);
	}

	private int evaluateDefenseStrategy(int [][] ennemie, Position pos) {
		int res = 0;
		for (int[] pt : ennemie) {
			Position tmp_pos = convertRelativePos(pos, pt);
			int bd = board.getPawnAtPosition(tmp_pos);
			if (bd != Board.PAWN_NONE && bd != super.getId() && bd != Board.DEFAULT_OUT_PAWN)
				res++;
			else return -1;
		}
		return res * 100 / ennemie.length;
	}
	
	private int evaluateStrategy(int[][] strategy, Position pos) {
		int res = 0;
		for (int[] pt : strategy) {
			Position tmp_pos = convertRelativePos(pos, pt);
			int bd = board.getPawnAtPosition(tmp_pos);
			if (bd == super.getId())
				res++;
			else if (bd != Board.PAWN_NONE)
				return -1;
		}	
		return res * 100 / strategy.length;
	}
	
	
	private Position[] evaluateStrategies() {
		
		HashMap<Integer, Integer> saving = new HashMap<>();
		HashMap<Integer, Position> savingPos = new HashMap<>();
		
		for (int i = 0; i < board.getLastPositionsSize(); i++) {
			Position[] positions = board.getLastPositions(i);
			if (positions == null)
				break;
			
			for (int pid = 0 ; pid < positions.length; pid++) {
				Position pos = positions[pid];
				if (pos == null)
					continue;
				if (pid == super.getId()-1) {
					for (Integer id : priorities.keySet()) {
						int [][] ennemie = ennemies.get(id);
						if (ennemie == null) {
							int note = evaluateStrategy(strategies.get(id), pos);
							if (note != -1) {
								saving.put(id, note);
								savingPos.put(id, pos);
								break;
							}
						}
					}
				} else {
					for (Integer id : priorities.keySet()) {
						int [][] ennemie = ennemies.get(id);
						if (ennemie != null) {
							int note = evaluateDefenseStrategy(ennemies.get(id), pos);
							if (note != -1) {
								saving.put(id, note);
								savingPos.put(id, pos);
								break;
							}
						}
					}
				}
				
			}
			
			
		}
		
		if (saving.size() > 0) {
			Integer id = 0;
			int note = -1;
			for (Integer curr_id : saving.keySet()) {
				if (saving.get(curr_id)>note) {
					id = curr_id;
					saving.get(curr_id);
				}
			}
			
			int[][] strat = strategies.get(id);
			Position[] res = new Position[strat.length];
			for (int j = 0; j < strat.length; j++) {
				res[j] = convertRelativePos(savingPos.get(id), strat[j]);
			}
			return res;
		}
	
		return new Position[] {PlayerUtils.getRandomPosition(board)};
	}
}
