package fr.NVT.TopOneReacher.kernel.boardgame;


import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.utils.Position;

public abstract class VPlayer {
	
	//Game manage player
	private final Game game;
	
	//name and id
	private final String name;
	private final int id;


	//Constructors
	protected VPlayer(Game game, String name) {
		this.game = game;
		this.name = name;
		this.id = this.game.createNewPlayer(this);
	}
	
	//Return name
	public String getName() {
		return name;
	}

	//Return player ID
	public int getId() {
		return id;
	}
	
	//Return game
	protected Game getGame() {
		return game;
	}
	
	//Call for each loop turn
	public abstract Position loop(Board board);

	
}
