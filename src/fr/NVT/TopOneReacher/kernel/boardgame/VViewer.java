package fr.NVT.TopOneReacher.kernel.boardgame;

import java.util.ArrayList;
import java.util.List;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.Main;
import fr.NVT.TopOneReacher.kernel.utils.Position;

public abstract class VViewer {
	
	//Constants
	protected static final int DEFAULT_CURRENT_GAME_ID = -1;
	
	
	//Variables
	private List<Game> games = new ArrayList<>();
	public final Main main;
	
	private int current_game_id;
	
	
	protected VViewer (Main main) {
		this.main = main;
		this.current_game_id = DEFAULT_CURRENT_GAME_ID;
	}
	
		//Check if game can be displayed
	protected boolean GameCanDisplayed(Game game) {
		return game.getGameId() == this.current_game_id;
	}
	
	//Return game with this id
	protected Game getGame(int id) {
		if (id >= this.games.size()) return null;
		return this.games.get(id);
	}
	
	//Display
	
		//Init display with size
	public abstract void initScreenBoard(int width, int height, int depth);
		
		//Reset Screen
	public abstract void resetScreen();
	
		//Show full board
	public abstract void shownBoard(Board board);
	
		//Show specific player position
	public abstract void showPlayerPosition(VPlayer player, Position pos);
	
		//Show the winner
	public abstract void showWinner(VPlayer player);
	
		//pr = false = pause //pr = true = resume
	public abstract void showPauseResume(boolean pr);
		
		//Show exceptions
	public abstract void exceptions(String message);
	
	
	//Controller
	
		//Create game with his size
	protected int createNewGame(int width, int height, int depth) {
		int id = this.games.size();
		this.games.add(id, new Game(this, id, width, height, depth));
		this.current_game_id = id;
		return id;
	}
	
		//Remove game from the list (Not forget to release memory)
	protected void removeGame(int id) {
		this.games.remove(id);
	}
	
		//Set delay on specific game
	protected void setDelay(int id, double sec) {
		this.games.get(id).setVirtualDelay(sec);
	}
	
		//Run specific game
	protected void runGame(int id) {
		Thread t = new Thread(this.games.get(id));
		t.start();

	}
	
		//Change the state of pause
	protected void pauseResume(int id) {
		this.games.get(id).pauseResume();
	};
	
		//Change the game can be display
	protected boolean changeGameDisplayed(int id) {
		if (this.current_game_id >= this.games.size()) return false;
		this.current_game_id = id;
		return true;
	}
	
	protected int getCurrentGameId() {
		return current_game_id ;
	}
}
