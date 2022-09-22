package fr.NVT.TopOneReacher.vplayer;


import fr.NVT.TopOneReacher.Main;
import fr.NVT.TopOneReacher.boardgame.Board;
import fr.NVT.TopOneReacher.boardgame.Pawn;
import fr.NVT.TopOneReacher.boardgame.Position;

public interface VPlayer {
	
	public boolean init(Main main, Pawn pawn, int index);
	
	public Position loop(Board board);
}
