package fr.NVT.TopOneReacher.modules.players;

import java.util.HashMap;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.ressources.DirectionPosition;
import fr.NVT.TopOneReacher.modules.ressources.OptimumZone;
import fr.NVT.TopOneReacher.modules.ressources.PlayerUtils;

public class ReverseTTRPlayer extends VPlayer {

	private HashMap<Position, Integer> attackHeatMap;
	private HashMap<DirectionPosition, OptimumZone> defensePriorities;
	private Board board;
	
	public ReverseTTRPlayer(Game game, String name) {
		super(game, name);
	}
	
	private void addToHeatMap(HashMap<Position, Integer> heatMap, Position dp, int val) {
		if (heatMap.containsKey(dp) ) {
			int newval = heatMap.get(dp) + val;
			heatMap.remove(dp);
			heatMap.put(dp, newval);
		} else heatMap.put(dp, val);
	}
	
	private byte dpToByte(DirectionPosition dp) {
		//Definition de la zone d'analyse
		short rg_min = dp.getRgMin(), rg_max = dp.getRgMax();
		
		
		String bin = "";
		int id = this.board.getPawnAtPosition(dp.getPos());
		for (short i = rg_min; i <= rg_max; i++) {
			Position check_pos = this.board.getCheckPosition(dp.getDir(), dp.getPos(), i);
			int pawn = this.board.getPawnAtPosition(check_pos);
			if (pawn != Board.PAWN_NONE && pawn != id) return -1;
			else if (pawn == id) bin += "1";
			else bin += "0";
		}
		return Byte.parseByte(bin, 2);
	}
	
	private void evaluatePosition(Position pos) {
		//Si la position est null, sa note n'existe pas
		if (pos == null)  {
			return;
		}
		
		//2D / 3D
		int dir_max = 4;
		if (this.board.getDepth() != 1) 
			dir_max = 13;
		
		
		//Pour l'ensemble des directions autour de la position
		for (byte dir = 1; dir <= dir_max; dir++) {
			//On réalise l'evaluation des deux zones.
			evaluateDP(new DirectionPosition(pos, dir, (byte) 1));
			evaluateDP(new DirectionPosition(pos, dir, (byte) 0));
		}
	}

	private void evaluateDP(DirectionPosition dp) {
		int id = board.getPawnAtPosition(dp.getPos());
		if (PlayerUtils.zoneIsOpen(board, id, dp.getPos(), dp.getDir(), dp.getRgMin(), dp.getRgMax())) {
			
			byte dp_byte = dpToByte(dp);
			if (dp_byte == -1) return;
			OptimumZone oz = OptimumZone.getOptimumZone(dp_byte);
			boolean reversable = oz.getReversableValue() == dp_byte;
			
			if (id == super.getId()) {
				short rg;
				if (reversable) rg = (byte) (dp.getRgMax() - (oz.getOptimumAttack() - 1));
				else rg = (byte) (dp.getRgMin() + (oz.getOptimumAttack() - 1));
				Position optimum_pos = board.getCheckPosition(dp.getDir(), dp.getPos(), rg);
				
				for (short i = dp.getRgMin(); i <= dp.getRgMax(); i++) {
					Position pos = board.getCheckPosition(dp.getDir(), dp.getPos(), i);
					if (pos != optimum_pos && board.getPawnAtPosition(pos) == Board.PAWN_NONE)
						addToHeatMap(this.attackHeatMap, pos, oz.getCurrentPrioritie());
						
					else if (board.getPawnAtPosition(pos) == Board.PAWN_NONE);
						addToHeatMap(this.attackHeatMap, pos, (int)(oz.getCurrentPrioritie()*0.3));
				}
			} else {
				OptimumZone curr_oz = this.defensePriorities.getOrDefault(dp, null);
				if (curr_oz == null)
					this.defensePriorities.put(dp, oz);
				else if (curr_oz.getEnnemiePrioritie() < oz.getEnnemiePrioritie())
					this.defensePriorities.replace(dp, oz);
			}
		}
	}

	@Override
	public Position loop(Board board) {
		
		this.attackHeatMap = new HashMap<Position, Integer>();
		this.defensePriorities = new HashMap<DirectionPosition, OptimumZone>();
		this.board = board;
		
		for (int x = 0; x < board.getWidth(); x++)
			for (int y = 0; y < board.getHeight(); y++)
				for (int z = 0; z < board.getDepth(); z++) {
					Position pos = new Position(x, y, z);
					evaluatePosition(pos);
				}
		
		Position rd_pos = getRandomPosition();
		
		Position attack_stroke = rd_pos;
		DirectionPosition defense_stroke = null;
		int attack_note = -1;
		int defense_note = -1;
		
		for (Position pos : this.attackHeatMap.keySet()) {
			int note = this.attackHeatMap.get(pos);
			if (note > attack_note) {
				attack_note = note;
				attack_stroke = pos;
			}
				
		}
		for (DirectionPosition dp : this.defensePriorities.keySet()) {
			OptimumZone oz = this.defensePriorities.get(dp);
			if (oz.getEnnemiePrioritie() > defense_note) {
				defense_note = oz.getEnnemiePrioritie();
				defense_stroke = dp;
			}
				
		}
		
		if (defense_note > 450) {
			OptimumZone oz = this.defensePriorities.get(defense_stroke);
			byte dp_byte = dpToByte(defense_stroke);
			boolean reversable = oz.getReversableValue() == dp_byte;
			byte rg;
			if (reversable) rg = (byte) (defense_stroke.getRgMax() - (oz.getOptimumDefense() - 1));
			else rg = (byte) (defense_stroke.getRgMin() + (oz.getOptimumDefense() - 1));
			Position pos = board.getCheckPosition(defense_stroke.getDir(), defense_stroke.getPos(), rg);
			if (board.getPawnAtPosition(pos) != Board.PAWN_NONE) {
				return attack_stroke;
			}
			return pos;
		}
		else return attack_stroke;
	}

	//Retourne une position random inoccuppée 
	private Position getRandomPosition() {
		int x, y, z;
		Position pos;
		do {
			x = PlayerUtils.randomInt(0, board.getWidth());
			y = PlayerUtils.randomInt(0, board.getHeight());
			z = PlayerUtils.randomInt(0, board.getDepth());
			pos = new Position(x, y, z);
		} while(this.board.getPawnAtPosition(pos)!=Board.PAWN_NONE);
		return new Position(x, y, z);
	}

	

}
