package fr.NVT.TopOneReacher.modules.viewers;

import fr.NVT.TopOneReacher.kernel.Main;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.boardgame.VViewer;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.ressources.WebSocketServer;

public class WebViewer extends VViewer {
	
	private WebSocketServer ws_server; 
	
	public WebViewer(Main main) {
		super(main);
		ws_server = new WebSocketServer();
		ws_server.open();
	}

	@Override
	public void initScreenBoard(int width, int height, int depth) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetScreen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void shownBoard(Board board) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showPlayerPosition(VPlayer player, Position pos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showWinner(VPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showPauseResume(boolean pr) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exceptions(String message) {
		// TODO Auto-generated method stub

	}

}
