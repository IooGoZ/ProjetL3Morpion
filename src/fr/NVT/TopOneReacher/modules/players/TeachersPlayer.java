package fr.NVT.TopOneReacher.modules.players;

import java.util.HashMap;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.ressources.DirectionPosition;
import fr.NVT.TopOneReacher.modules.ressources.PlayerUtils;

public class TeachersPlayer extends VPlayer {
	
	private Board board;
	private HashMap<DirectionPosition, Object> notes;
	
	public TeachersPlayer(Game game, String name) {
		super(game, name);
	}

	
	private Position calculateBestPosition(DirectionPosition dp) {
		short rg_min = dp.getRgMin(), rg_max = dp.getRgMax();
		if (dp.getZone() != 1) {
			for (short i = rg_min; i <= rg_max; i--) {
				Position check_pos = this.board.getCheckPosition(dp.getDir(), dp.getPos(), i);
				int pawn = this.board.getPawnAtPosition(check_pos);
				if (pawn == Board.PAWN_NONE) return check_pos;
			}
		} else {
			for (short i = rg_max; i >= rg_min; i++) {
				Position check_pos = this.board.getCheckPosition(dp.getDir(), dp.getPos(), i);
				int pawn = this.board.getPawnAtPosition(check_pos);
				if (pawn == Board.PAWN_NONE) return check_pos;
			}
		}
		
		return null;
	}
	
	//Evalue l'ensemble des coups autour d'une position
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
			evaluateZonedDirection(new DirectionPosition(pos, dir, (byte) 1));
			evaluateZonedDirection(new DirectionPosition(pos, dir, (byte) 0));
		}
	}
	
	//Evaluation d'une zone
	private void evaluateZonedDirection(DirectionPosition dp) {
		
		//Definition de la zone d'analyse
		short rg_min = dp.getRgMin(), rg_max = dp.getRgMax();
		
		//On récupère le joueur à analyser
		int id = this.board.getPawnAtPosition(dp.getPos());
		//Si la zone est ouverte, On évalue une stratégie
		evalutateDP(dp, rg_min, rg_max, id);
		
	}
	
	private void evalutateDP(DirectionPosition dp, short rg_min, short rg_max, int id) {
		
		//On compte le nombre de pion
		int val = 0;
		int ennemies_val = 0;
		
		for (short i = rg_min; i <= rg_max; i++) {
			Position check_pos = this.board.getCheckPosition(dp.getDir(), dp.getPos(), i);
			int pawn = this.board.getPawnAtPosition(check_pos);
			
			if (pawn == id) val++;
			else if (pawn != Board.PAWN_NONE) ennemies_val++;
		}
		
		int note;
		
		//Si on evalue le joueur courant
		if (super.getId() == id)
				note = (1 - ennemies_val) * (int) Math.pow(10, (double) val);
		//Si on evalue un joueur ennemie
		else note = (5 - ennemies_val) * (int)  Math.pow(10, (double) (val-1));
		notes.put(dp, note);
	}
	
	
	@Override
	public Position loop(Board board) {
		this.board = board;
		this.notes = new HashMap<>();
		
		//Définition de la valeur résultat
		Position res = null;
		
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
		
		//Tri et calcule de position
		do {
			//On récupère la meilleure note de la liste
			for (DirectionPosition dp : notes.keySet()) {
				int note = (int) notes.get(dp);
				if (max_dp == null || note > max_note) {
					max_note = note;
					max_dp = dp;
				}
			}
			
			//On calcule la position optimum
			res = calculateBestPosition(max_dp);
			
			notes.remove(max_dp);
			max_dp = null;
			max_note = -1;
			
			
			//Si la position est null, on recommence avec la n-ieme meilleure
		} while(res == null && notes.size() > 0);
		
		//Si malgré tout la position n'est pas toruvé on renvoie une position aléatoire
		if (res == null) return getRandomPosition();
		
		//sinon résultat
		else return res;
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
