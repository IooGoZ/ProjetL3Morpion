package fr.NVT.TopOneReacher.modules.players;

import java.util.HashMap;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.Position;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.modules.ressources.DirectionPosition;
import fr.NVT.TopOneReacher.modules.ressources.IntegerTabUtils;
import fr.NVT.TopOneReacher.modules.ressources.PlayerUtils;

public class TeachersPlayer extends VPlayer {
	
	private Board board;
	private HashMap<DirectionPosition, Object> notes = new HashMap<>();
	
	public TeachersPlayer(Game game, String name) {
		super(game, name);
	}

	
	private Position calculateBestPosition(DirectionPosition dp) {
		short rg_min, rg_max;
		if (dp.getZone() == 1) {
			rg_min = -4;
			rg_max = 0;
		} else {
			rg_min = 0;
			rg_max = 4;
		}
		for (int i = rg_max; i >= rg_min; i++) {
			Position check_pos = this.board.getCheckPosition(dp.getDir(), dp.getPos(), i);
			int pawn = this.board.getPawnAtPosition(check_pos);
			if (pawn == Board.PAWN_NONE) return check_pos;
		}
		return null;
	}
	
	
	private void evalutatePosition(Position pos) {
		int dir_max = 4;
		if (this.board.getDepth() != 1) 
			dir_max = 13;
		
		for (byte dir = 1; dir <= dir_max; dir++) {
			evaluateZonedDirection(new DirectionPosition(pos, dir, (byte) 1));
			evaluateZonedDirection(new DirectionPosition(pos, dir, (byte) 0));
		}
	}
	
	private void evaluateZonedDirection(DirectionPosition dp) {
		short rg_min, rg_max;
		
		if (dp.getZone() == 1) {
			rg_min = -4;
			rg_max = 0;
		} else {
			rg_min = 0;
			rg_max = 4;
		}
		
		if (dp.getPos() == null)  {
			notes.put(dp, -1);
			return;
		}
		int id = this.board.getPawnAtPosition(dp.getPos());
		if (!PlayerUtils.zoneIsOpen(this.board, id, dp.getPos(), dp.getDir(), rg_min, rg_max)) {
			//Defense
			defend(dp, rg_min, rg_max, id);
		} else {
			attack(dp, rg_min, rg_max, id);
		}
	}
	
	
	private void defend(DirectionPosition dp, short rg_min, short rg_max, int id) {
		int tab[] = new int[super.getGame().getNbPlayers()];
		
		for (int i = rg_min; i <= rg_max; i++) {
			Position check_pos = this.board.getCheckPosition(dp.getDir(), dp.getPos(), i);
			int pawn = this.board.getPawnAtPosition(check_pos);
			if (pawn > Board.PAWN_NONE) tab[pawn-1]++;
		}
		int max_id = IntegerTabUtils.maxValueId(tab)+1;
		if (PlayerUtils.zoneIsOpen(this.board, max_id, dp.getPos(), dp.getDir(), rg_min, rg_max)) {
			int note = (int) Math.pow(5, tab[max_id-1]);
			notes.put(dp, note);
		} else
			notes.put(dp, -1);
	}
	
	private void attack(DirectionPosition dp, short rg_min, short rg_max, int id) {
		int val = 0;
		for (int i = rg_min; i <= rg_max; i++) {
			Position check_pos = this.board.getCheckPosition(dp.getDir(), dp.getPos(), i);
			int pawn = this.board.getPawnAtPosition(check_pos);
			if (pawn == id) val++;
		}
		int note = (int) Math.pow(10, val);
		notes.put(dp, note);
	}
	
	
	@Override
	public Position loop(Board board) {
		this.board = board;
		
		Position[] positions = board.getLastPositions();
		
		for(int i = 0; i < super.getGame().getNbPlayers(); i++) {
			Position pos = positions[i];
			evalutatePosition(pos);
		}
		
		DirectionPosition max_dp = null;
		int max_note = -1;
		
		
		for (DirectionPosition dp : notes.keySet()) {
			int note = (int) notes.get(dp);
			if (max_dp == null || note > max_note) {
				max_note = note;
				max_dp = dp;
			}
		}
		
		if (max_dp.getPos() == null) {
			int x = PlayerUtils.randomInt(0, board.getWidth());
			int y = PlayerUtils.randomInt(0, board.getHeight());
			int z = PlayerUtils.randomInt(0, board.getDepth());
			return new Position(x, y, z);
		}
		
		Position pos = calculateBestPosition(max_dp);
		
		return pos;
	}

}
