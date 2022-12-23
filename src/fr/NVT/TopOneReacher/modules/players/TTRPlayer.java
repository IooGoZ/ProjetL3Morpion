package fr.NVT.TopOneReacher.modules.players;

import java.util.HashMap;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.ressources.DirectionPosition;
import fr.NVT.TopOneReacher.modules.ressources.OptimumZone;
import fr.NVT.TopOneReacher.modules.ressources.PlayerUtils;

public class TTRPlayer extends VPlayer {
	
	private Board board;
	private HashMap<DirectionPosition, OptimumZone> notes;
	private HashMap<DirectionPosition, Integer> heatMap;
	
	public TTRPlayer(Game game, String name) {
		super(game, name);
	}

	
	private void addToHeatMap(DirectionPosition dp, int val) {
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
	
	private void evalutatePosition(Position pos) {
		
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
	
	//Evaluation d'une zone
	private void evaluateDP(DirectionPosition dp) {
		int id = board.getPawnAtPosition(dp.getPos());
		if (PlayerUtils.zoneIsOpen(board, id, dp.getPos(), dp.getDir(), dp.getRgMin(), dp.getRgMax())) {
			byte dp_byte = dpToByte(dp);
			if (dp_byte == -1) return;
			OptimumZone oz = OptimumZone.getOptimumZone(dp_byte);
			boolean reversable = oz.getReversableValue() == dp_byte;
			notes.put(dp, oz);
			if (id == super.getId()) {
				short rg;
				if (reversable) rg = (byte) (dp.getRgMax() - (oz.getOptimumAttack() - 1));
				else rg = (byte) (dp.getRgMin() + (oz.getOptimumAttack() - 1));
				Position optimum_pos = board.getCheckPosition(dp.getDir(), dp.getPos(), rg);
				
				addToHeatMap(dp, 75);
				
				for (short i = dp.getRgMin(); i <= dp.getRgMax(); i++) {
					Position pos = board.getCheckPosition(dp.getDir(), dp.getPos(), i);
					if (pos != optimum_pos && board.getPawnAtPosition(pos) == Board.PAWN_NONE)
						addToHeatMap(dp, oz.getCurrentPrioritie());
				}
			}
		}
	}
	
	
	@Override
	public Position loop(Board board) {
		this.board = board;
		this.notes = new HashMap<>();
		this.heatMap = new HashMap<>();
		
		//Récupération de l'ensemble des positions jouées par les joueurs
		Position[] positions = board.getLastPositions(0);
		
		//Si la liste est vide (=1er tour) -> position random
		if (positions == null)
			return getRandomPosition();
		
		//On évalue toutes les positions jouées
		for (int j = 1; j <= this.board.getLastPositionsSize(); j ++) {
			if (positions == null)
				break;
			for(int i = 0; i < super.getGame().getNbPlayers(); i++) {
				//Récupération de la position
				Position evalpos = positions[i];
				//evaluation
				evalutatePosition(evalpos);
			}
			//Actualisation de la valeur boucle
			positions = board.getLastPositions(j);
		}
		
		//Préparation du tri
		DirectionPosition max_dp = null;
		int max_note = -1;
		
		//On récupère la meilleure note de la liste
		for (DirectionPosition dp : notes.keySet()) {
			OptimumZone oz = notes.get(dp);
			int note;
			if (board.getPawnAtPosition(dp.getPos()) == super.getId())
				note = oz.getCurrentPrioritie();
			else note = oz.getEnnemiePrioritie();
			if (max_dp == null || note > max_note) {
				max_note = note;
				max_dp = dp;
			}
		}
		
		if (notes.size() != 0) {
			int id = board.getPawnAtPosition(max_dp.getPos());
			if (max_note > 450) {
				//On calcule la position optimum
				OptimumZone oz = notes.get(max_dp);
				byte dp_byte = dpToByte(max_dp);
				boolean reversable = oz.getReversableValue() == dp_byte;
				byte rg;
				if (id == super.getId()) {
					if (reversable) rg = (byte) (max_dp.getRgMax() - (oz.getOptimumAttack() - 1));
					else rg = (byte) (max_dp.getRgMin() + (oz.getOptimumAttack() - 1));
				} else {
					if (reversable) rg = (byte) (max_dp.getRgMax() - (oz.getOptimumDefense() - 1));
					else rg = (byte) (max_dp.getRgMin() + (oz.getOptimumDefense() - 1));
				}
				
				
				return board.getCheckPosition(max_dp.getDir(), max_dp.getPos(), rg);
			} else if (heatMap.size() != 0) {
				max_dp = null;
				max_note = -1;
				
				for (DirectionPosition dp : heatMap.keySet()) {
					int note = heatMap.get(dp);
					if (max_dp == null || note > max_note) {
						max_note = note;
						max_dp = dp;
					}
				}
				
				//On calcule la position optimum
				OptimumZone oz = notes.get(max_dp);
				byte dp_byte = dpToByte(max_dp);
				boolean reversable = oz.getReversableValue() == dp_byte;
				byte rg;
				if (id == super.getId()) {
					if (reversable) rg = (byte) (max_dp.getRgMax() - (oz.getOptimumAttack() - 1));
					else rg = (byte) (max_dp.getRgMin() + (oz.getOptimumAttack() - 1));
				} else {
					if (reversable) rg = (byte) (max_dp.getRgMax() - (oz.getOptimumDefense() - 1));
					else rg = (byte) (max_dp.getRgMin() + (oz.getOptimumDefense() - 1));
				}
				
				return board.getCheckPosition(max_dp.getDir(), max_dp.getPos(), rg);
				
			}
		}
		return getRandomPosition();
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
