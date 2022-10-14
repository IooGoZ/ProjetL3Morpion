package fr.NVT.TopOneReacher.kernel;

import java.util.ArrayList;
import java.util.List;

import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.boardgame.VViewer;
import fr.NVT.TopOneReacher.kernel.utils.GameState;
import fr.NVT.TopOneReacher.kernel.utils.Position;

public class Game extends Thread {
	
	private static final double DEFAULT_DELAY = -1.0d;
	private static final double SEC_TO_MILLIS = 1000d;
	
	private static final int LIMIT_PLAYER_TRY = 3;
	
	private List<VPlayer> players = new ArrayList<>();
	
	private final VViewer viewer;
	private final int game_id;
	private Board board;
	private final int width, height, depth;

	private GameState state;

	private long delay;
	
	
	public Game(VViewer viewer, int id, int width, int height, int depth) {
		this.viewer = viewer;
		this.game_id = id;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.state = GameState.LOADING;
	}

	@Override
	public void run() {
		super.run();
		this.board = new Board(this, this.width, this.height, this.depth, players.size());
		this.state = GameState.INGAME;
		this.viewer.initScreenBoard(this.width, this.height, this.depth);
		this.viewer.shownBoard(board);
		this.looper();
	}
	
	private void looper() {
		while(this.state == GameState.INGAME || this.state == GameState.PAUSED) {
			if (this.state == GameState.INGAME) loopContent();
		}
	}
	
	private void delay() {
		if (this.delay != DEFAULT_DELAY)
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	private void loopContent() {
		for (VPlayer pl : this.players) {
			boolean valid_stroke;
			Position pos;
			int i = 0;
			do {
				pos = pl.loop(this.board);
				valid_stroke = this.board.playPosition(pl, pos);
				i++;
				if (i > LIMIT_PLAYER_TRY) {
					this.viewer.main.exceptions(pl.getName() + " a depasser sa limite d'essaie.");
				}
			} while (!valid_stroke);
			if (this.state == GameState.FINISHED) return;
			else {
				this.viewer.showPlayerPosition(pl, pos);
				delay();
			}
		}
	}
	
	
	public void killGame() {
		this.state = GameState.FINISHED;
	}
	
	public void pauseResume() {
		if (this.state == GameState.INGAME) this.state = GameState.PAUSED;
		else this.state = GameState.INGAME;
	}

	public void setVirtualDelay(double sec) {
		this.delay = (long) (sec * SEC_TO_MILLIS);
	}

	public int createNewPlayer(VPlayer vPlayer) {
		int id = players.size();
		players.add(id, vPlayer);
		return id + 1;
	}

	public GameState getGameState() {
		return this.state;
	}

	public int getGameId() {
		return this.game_id;
	}
	
	public int getNbPlayers() {
		return players.size();
	}

	public void setWinner(VPlayer player, Position pos) {
		this.state = GameState.FINISHED;
		this.viewer.showPlayerPosition(player, pos);
		this.viewer.showWinner(player);
	}
	
}
