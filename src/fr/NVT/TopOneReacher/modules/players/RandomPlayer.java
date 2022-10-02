package fr.NVT.TopOneReacher.modules.players;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.Position;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;

public class RandomPlayer extends VPlayer {

	public RandomPlayer(Game game, String name) {
		super(game, name);
	}
	
	private int randomInt(int min, int max) {
		int range = max - min;
		int rand = (int)(Math.random() * range) + min;
		return rand;
	}

	@Override
	public Position loop(Board board) {
		int x = randomInt(0, board.getWidth());
		int y = randomInt(0, board.getHeight());
		int z = randomInt(0, board.getDepth());
		return new Position(x, y, z);
	}

}
