package fr.NVT.TopOneReacher.kernel;

import java.util.ArrayList;
import java.util.List;

import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.boardgame.VViewer;
import fr.NVT.TopOneReacher.kernel.utils.GameState;
import fr.NVT.TopOneReacher.kernel.utils.Position;

public class Game implements Runnable {
	
	//Constants
	private static final double DEFAULT_DELAY = -1.0d;
	private static final double SEC_TO_MILLIS = 1000d;
	
	private static final int LIMIT_PLAYER_TRY = 3;
	
	private static final boolean ENABLE_DISPLAY = true;
	
	
	//Players list
	private List<VPlayer> players = new ArrayList<>();
	
	//Viewer used by the game
	private final VViewer viewer;
	
	private final int game_id;
	private Board board;
	private final int width, height, depth;

	private GameState state;

	private long delay;
	
	private int winner;
	
	
	public Game(VViewer viewer, int id, int width, int height, int depth) {
		this.viewer = viewer;
		this.game_id = id;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.state = GameState.LOADING;
	}

	
	//Run the game
	@Override
	public void run() {
		
		//Initialization of play board
		this.board = new Board(this, this.width, this.height, this.depth, players.size());
		
		//Change state
		this.state = GameState.INGAME;
		
		//Initialize viewer
		this.viewer.initScreenBoard(this.width, this.height, this.depth);
		
		//Run the loop
		this.looper();
	}
	
	//Manage the loop
	private void looper() {
		while(this.state == GameState.INGAME || this.state == GameState.PAUSED) {
			//If game isn't paused, it play
			if (this.state == GameState.INGAME) loopContent();
		}
	}
	
	//Wait a delay
	private void delay() {
		if (this.delay != DEFAULT_DELAY)
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	//Content of play loop
	private void loopContent() {
		//For all players of the game
		for (VPlayer pl : this.players) {
			boolean valid_stroke;
			Position pos;
			int i = 0;
			
			//Player can try to play LIMIT_PLAYER_TRY times.
			do {
				//Request player positions
				pos = pl.loop(this.board);
				
				//Check the position
				valid_stroke = this.board.playPosition(pl, pos);
				i++;
				
				//Check the limit of try
				if (i > LIMIT_PLAYER_TRY) {
					this.viewer.main.exceptions(pl.getName() + " a depasser sa limite d'essaie.");
				}
			} while (!valid_stroke);
			
			//Security to prevent bad display
			if (this.state == GameState.FINISHED) return;
			else {
				if (ENABLE_DISPLAY) this.viewer.showPlayerPosition(pl, pos);
				delay();
			}
		}
	}
	
	//Stop the game loop
	public void killGame() {
		this.state = GameState.FINISHED;
	}
	
	//Pause or resume the game
	public void pauseResume() {
		if (this.state == GameState.INGAME) this.state = GameState.PAUSED;
		else this.state = GameState.INGAME;
	}

	//Set delay between each stroke
	public void setVirtualDelay(double sec) {
		this.delay = (long) (sec * SEC_TO_MILLIS);
	}

	//Add new player
	public int createNewPlayer(VPlayer vPlayer) {
		int id = players.size();
		players.add(id, vPlayer);
		return id + 1;
	}

	//Return game state
	public GameState getGameState() {
		return this.state;
	}

	//Return game ID
	public int getGameId() {
		return this.game_id;
	}
	
	//Return the number of player
	public int getNbPlayers() {
		return players.size();
	}

	//Define the winner and make end to the game
	public void setWinner(VPlayer player, Position pos) {
		if (player != null) {
			this.winner = player.getId();
			this.state = GameState.FINISHED;
			if (ENABLE_DISPLAY) {
				this.viewer.showPlayerPosition(player, pos);
				this.viewer.showWinner(player);
			}
		} else if (ENABLE_DISPLAY) {
			this.viewer.showWinner(null);
		}
		
	}
	
	public List<VPlayer> getPlayersList() {
		return players;
	}
	
	//Return the winner (null if undefined)
	public int getWinner() {
		return this.winner;
	}
	
}
