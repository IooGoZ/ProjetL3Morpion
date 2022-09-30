package fr.NVT.TopOneReacher.kernel;

import java.util.ArrayList;
import java.util.List;

import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.utils.GameState;
import fr.NVT.TopOneReacher.kernel.viewer.VViewer;
import fr.NVT.TopOneReacher.kernel.vplayer.VPlayer;

public class Game extends Thread {
	
	private static final double DEFAULT_DELAY = -1.0d;
	private static final double SEC_TO_MILLIS = 1000d;
	
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
		this.looper();
	}
	
	private void looper() {
		while(this.state == GameState.INGAME || this.state == GameState.PAUSED) {
			if (this.state == GameState.INGAME) loopContent();
			if (this.delay != DEFAULT_DELAY)
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}
	
	private void loopContent() {
		// TODO Auto-generated method stub
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
		players.add(vPlayer);
		return id;
	}

	public GameState getGameState() {
		return this.state;
	}

	public int getGameId() {
		return this.game_id;
	}
	
}
