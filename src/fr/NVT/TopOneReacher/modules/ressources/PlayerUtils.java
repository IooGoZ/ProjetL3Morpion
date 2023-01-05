package fr.NVT.TopOneReacher.modules.ressources;

import java.util.ArrayList;
import java.util.List;

import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.utils.Position;

public abstract class PlayerUtils {
	
	
	//Check if zone is playable
	public static boolean zoneIsOpen(Board board, int player_id, DirectionPosition dp) {
		for (short i = dp.getRgMin(); i <= dp.getRgMin(); i++) {
			Position check_pos = Board.getCheckPosition(dp.getDir(), dp.getPos(), i);
			int pawn = board.getPawnAtPosition(check_pos);
			if (pawn != Board.PAWN_NONE && pawn != player_id) return false;
		}
		return true;
	}
	
	public static int getPlayerInZone(Board board, DirectionPosition dp) {
		for (short i = dp.getRgMin(); i <= dp.getRgMax(); i++) {
			Position check_pos = Board.getCheckPosition(dp.getDir(), dp.getPos(), i);
			int pawn = board.getPawnAtPosition(check_pos);
			if (pawn != Board.PAWN_NONE) return pawn;
		}
		return Board.PAWN_NONE;
	}
	
	//Return random int
	public static int randomInt(int min, int max) {
		int range = max - min;
		int rand = (int)(Math.random() * range) + min;
		return rand;
	}
	
	public static Position getRandomPosition(Board board) {
		List<Position> poss = new ArrayList<>();
		for (int x = 0; x < board.getWidth(); x++)
			for (int y = 0; y < board.getHeight(); y++)
				for (int z = 0; z < board.getDepth(); z++) {
					Position pos = new Position(x, y, z);
					if (board.getPawnAtPosition(pos)==Board.PAWN_NONE)
						poss.add(pos);
				}
					
		if (poss.size()==0) return null;
		
		return poss.get(randomInt(0, poss.size()));
	}
	
	public static byte dpToByte(Board board, DirectionPosition dp) {
		//Definition de la zone d'analyse
		short rg_min = dp.getRgMin(), rg_max = dp.getRgMax();
		String bin = "";
		int id = board.getPawnAtPosition(dp.getPos());
		for (short i = rg_min; i <= rg_max; i++) {
			Position check_pos = Board.getCheckPosition(dp.getDir(), dp.getPos(), i);
			int pawn = board.getPawnAtPosition(check_pos);
			if (pawn != Board.PAWN_NONE && pawn != id) return -1;
			else if (pawn == id) bin += "1";
			else bin += "0";
		}
		return Byte.parseByte(bin, 2);
	}
	
}
