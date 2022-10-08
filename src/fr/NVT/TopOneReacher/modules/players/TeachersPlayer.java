package fr.NVT.TopOneReacher.modules.players;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.Position;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;

public class TeachersPlayer extends VPlayer {

	protected TeachersPlayer(Game game, String name) {
		super(game, name);
	}

	private int evaluateDirection(short dir, short zone) {
		int rg_min, rg_max;
		
		if (zone == 1) {
			rg_min = -4;
			rg_max = 0;
		} else {
			rg_min = 0;
			rg_max = 4;
		}
		
		return -1;
	}
	
	@Override
	public Position loop(Board board) {
		
		return null;
	}

}
