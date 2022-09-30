package fr.NVT.TopOneReacher.kernel.vplayer;


import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.Position;

public abstract class VPlayer {
	
	private Game game;
	private String name;
	private int id;


	protected VPlayer(Game game, String name) {
		this.game = game;
		this.name = name;
		this.id = this.game.createNewPlayer(this);
	}
	
	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
	
	//Call for each loop turn
	public abstract Position loop(Board board);

	
}
