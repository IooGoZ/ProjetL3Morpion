package fr.NVT.TopOneReacher.modules.players;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.viewers.WebViewer;

public class HumanWebPlayer extends VPlayer {

	private WebViewer viewer;
	
	public HumanWebPlayer(WebViewer viewer, Game game, String name) {
		super(game, name);
		this.viewer = viewer;
	}

	@Override
	public Position loop(Board board) {
		return viewer.getPlayerPosition(this, board);
	}

}
