package fr.NVT.TopOneReacher.modules.players;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.ressources.PlayerUtils;

public class RandomPlayer extends VPlayer {

	public RandomPlayer(Game game, String name) {
		super(game, name);
	}

	@Override
	public Position loop(Board board) {
		int x = PlayerUtils.randomInt(0, board.getWidth());
		int y = PlayerUtils.randomInt(0, board.getHeight());
		int z = PlayerUtils.randomInt(0, board.getDepth());
		return new Position(x, y, z);
	}

}
