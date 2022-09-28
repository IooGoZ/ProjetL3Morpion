package fr.NVT.TopOneReacher.viewer;


import java.util.List;

import fr.NVT.TopOneReacher.Game;
import fr.NVT.TopOneReacher.boardgame.Board;
import fr.NVT.TopOneReacher.boardgame.Position;
import fr.NVT.TopOneReacher.vplayer.VPlayer;

public abstract class VViewer {
	
	private Game game;
	
	//Display
	public abstract void initScreenBoard(int width, int height, int depth);
	
	public abstract void resetScreen();
	
	public abstract void shownBoard(Board board);
	
	public abstract void showPlayerPosition(VPlayer player, Position pos);
	
	public abstract void hidePlayerPosition(VPlayer player, Position pos);
	
	public abstract void showWinner(VPlayer player);
	
	//pr = false = pause //pr = true = resume
	public abstract void showPauseResume(boolean pr);
	
	public abstract void exceptions(String message);
	
	
	//Controller
	protected void createNewGame(int width, int height, int depth, List<VPlayer> players) {
		Board board = new Board(width, height, depth, players.size());
		this.game = new Game(board, players);
	};
	
	protected void setDelay(double sec) {
		this.game.setVirtualDelay(sec);
	}
	
	protected void runGame() {
		this.game.run();
	}
	
	protected void pauseResume() {
		this.game.pauseResume();
	};
}
