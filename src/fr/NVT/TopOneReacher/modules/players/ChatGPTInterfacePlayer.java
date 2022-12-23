package fr.NVT.TopOneReacher.modules.players;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.utils.Position;

public class ChatGPTInterfacePlayer extends VPlayer {
	
	public ChatGPTInterfacePlayer(Game game, String name) {
		super(game, name);
	}

	@Override
	public Position loop(Board board) {
		
		int[][][] clonedBoard = new int[board.getWidth()][board.getHeight()][board.getDepth()];
		for (int x = 0; x < board.getWidth(); x++)
			for (int y = 0; y < board.getHeight(); y++)
				for (int z = 0; z < board.getDepth(); z++)
					clonedBoard[x][y][z] = board.getPawnAtPosition(new Position(x, y, z));
		
		ChatGPTPlayer pl = new ChatGPTPlayer(clonedBoard);
		int[] pos = pl.getBestMove(getId());
		
		return new Position(pos[0], pos[1], pos[2]);
	}

}
