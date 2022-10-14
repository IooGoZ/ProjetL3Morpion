package fr.NVT.TopOneReacher.modules.players;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.utils.Position;

public class TestPlayer extends VPlayer{
	
	private Position pos = new Position(9, 9, 0);
	private int rg = -5;

	public TestPlayer(Game game, String name) {
		super(game, name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Position loop(Board board) {
		if (rg > 4) System.exit(0);
		rg++;
		return board.getCheckPosition((byte) 4, pos, (short) rg);
	}

	
}
