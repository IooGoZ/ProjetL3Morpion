package fr.NVT.TopOneReacher.modules.ressources;

import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.utils.Position;

public abstract class PlayerUtils {
	
	
	//Check if zone is playable
	public static boolean zoneIsOpen(Board board, int player_id, Position pos, byte dir, short rg_min, short rg_max) {
		for (short i = rg_min; i <= rg_max; i++) {
			Position check_pos = board.getCheckPosition(dir, pos, i);
			int pawn = board.getPawnAtPosition(check_pos);
			if (pawn != Board.PAWN_NONE && pawn != player_id) return false;
		}
		return true;
	}
	
	//Return random int
	public static int randomInt(int min, int max) {
		int range = max - min;
		int rand = (int)(Math.random() * range) + min;
		return rand;
	}
	
	
}
