package fr.NVT.TopOneReacher.kernel.boardgame;

import java.util.ArrayList;
import java.util.List;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.Main;
import fr.NVT.TopOneReacher.kernel.utils.Position;

public abstract class VViewer {
	
	private static final int DEFAULT_CURRENT_GAME_ID = -1;
	
	
	private List<Game> games = new ArrayList<>();
	public final Main main;
	
	private int current_game_id;
	
	
	//Logic
	protected VViewer (Main main) {
		this.main = main;
		this.current_game_id = DEFAULT_CURRENT_GAME_ID;
	}
	
	protected boolean gameCanDisplayed(Game game) {
		return game.getGameId() == this.current_game_id;
	}
	
	protected Game getGame(int id) {
		if (id >= this.games.size()) return null;
		return this.games.get(id);
	}
	
	//Display
	public abstract void initScreenBoard(int width, int height, int depth);
	
	public abstract void resetScreen();
	
	public abstract void shownBoard(Board board);
	
	public abstract void showPlayerPosition(VPlayer player, Position pos);
	
	public abstract void showWinner(VPlayer player);
	
	//pr = false = pause //pr = true = resume
	public abstract void showPauseResume(boolean pr);
	
	public abstract void exceptions(String message);
	
	
	//Controller
	protected int createNewGame(int width, int height, int depth) {
		int id = this.games.size();
		this.games.add(id, new Game(this, id, width, height, depth));
		return id;
	};
	
	protected void setDelay(int id, double sec) {
		this.games.get(id).setVirtualDelay(sec);
	}
	
	protected void runGame(int id) {
		this.games.get(id).run();

	}
	
	protected void pauseResume(int id) {
		this.games.get(id).pauseResume();
	};
	
	protected boolean changeGameDisplayed(int id) {
		if (this.current_game_id >= this.games.size()) return false;
		this.current_game_id = id;
		return true;
	}
}
