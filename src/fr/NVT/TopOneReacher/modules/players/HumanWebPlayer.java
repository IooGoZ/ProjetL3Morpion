package fr.NVT.TopOneReacher.modules.players;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.utils.Position;

public class HumanWebPlayer extends VPlayer {

	private Position next_pos = null;
	
	public HumanWebPlayer(Game game, String name) {
		super(game, name);
	}

	@Override
	public Position loop(Board board) {
		this.next_pos = null;
		while(this.next_pos == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
		Position res = this.next_pos;
		return res;
	}

	public void setNextPosition(Position pos) {
		this.next_pos = pos;
	}
}
