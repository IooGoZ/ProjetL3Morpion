package fr.NVT.TopOneReacher.modules.players;

import java.util.HashMap;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.ressources.DirectionPosition;
import fr.NVT.TopOneReacher.modules.ressources.OptimumZone;
import fr.NVT.TopOneReacher.modules.ressources.PlayerUtils;

public class TopTwoReacherPlayer extends VPlayer {
	
	private Board board;
	private HashMap<DirectionPosition, OptimumZone> notes;
	
	public TopTwoReacherPlayer(Game game, String name) {
		super(game, name);
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
			notes.put(dp, oz);
		}
	}
	
	
	@Override
	public Position loop(Board board) {
		this.board = board;
		this.notes = new HashMap<>();
		
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
			//On calcule la position optimum
			OptimumZone oz = notes.get(max_dp);
			byte dp_byte = dpToByte(max_dp);
			boolean reversable = oz.getReversableValue() == dp_byte;
			byte rg;
			if (reversable) rg = (byte) (max_dp.getRgMax() - (oz.getOptimum() - 1));
			else rg = (byte) (max_dp.getRgMin() + (oz.getOptimum() - 1));
			
			return board.getCheckPosition(max_dp.getDir(), max_dp.getPos(), rg);
		} else return getRandomPosition();
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
