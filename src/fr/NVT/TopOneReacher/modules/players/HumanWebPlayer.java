package fr.NVT.TopOneReacher.modules.players;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.viewers.WebViewer;

public class HumanWebPlayer extends VPlayer {

	private Position next_pos = null;
	private WebViewer wv;
	
	public HumanWebPlayer(WebViewer wv, Game game, String name) {
		super(game, name);
		this.wv = wv;
	}

	@Override
	public Position loop(Board board) {
		this.next_pos = null;
		this.wv.requestHuman(super.getId());
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
