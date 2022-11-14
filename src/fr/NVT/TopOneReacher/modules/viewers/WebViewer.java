package fr.NVT.TopOneReacher.modules.viewers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.Main;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.boardgame.VViewer;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.players.HumanWebPlayer;
import fr.NVT.TopOneReacher.modules.players.TeachersPlayer;
import fr.NVT.TopOneReacher.modules.players.TopTwoReacherPlayer;
import fr.NVT.TopOneReacher.modules.ressources.WebSocketParser;
import fr.NVT.TopOneReacher.modules.ressources.WebSocketServer;

public class WebViewer extends VViewer {
	
	private WebSocketServer ws_server; 
	
	int last_width, last_height, last_depth;
	
	private HashMap<Integer, Position> player2lstpos = new HashMap<>();
	private HashMap<Integer, Position> player2pos = new HashMap<>();
	
	public WebViewer(Main main) {
		super(main);
		ws_server = new WebSocketServer(this);
		ws_server.open();
	}
	
	
	//Parse Call----------------
	
	public int createNewGame(int width, int height, int depth) {
		int id = super.createNewGame(width, height, depth);
		Game game = super.getGame(id);
		
		new HumanWebPlayer(this, game, "HumanWebPlayer Two");
		new TopTwoReacherPlayer(game, "TopTwoReacher One");
		
		System.out.println("Game is create !");
		return id;
	}

	public void setDelay(double delay) {
		if (checkCurrentGame())
			super.setDelay(super.getCurrentGameId(), delay);
		
	}
	
	public void runGame() {
		if (checkCurrentGame())
			super.runGame(super.getCurrentGameId());
			System.out.println("Game is run !");
	}
	
	
	public void pauseResume() {
		if (checkCurrentGame())
			super.pauseResume(super.getCurrentGameId());
	}
	
	//--------------------------
	
	@Override
	public void initScreenBoard(int width, int height, int depth) {
		this.last_width = width;
		this.last_height = height;
		this.last_depth = depth;
		WebSocketParser.unparserInitBoardLengths(ws_server, width, height, depth);
	}

	@Override
	public void resetScreen() {
		WebSocketParser.unparserInitBoardLengths(ws_server, this.last_width, this.last_height, this.last_depth);
	}

	@Override
	public void shownBoard(Board board) {
		List<Position> positions = new ArrayList<>();
		for (int z = 0; z < board.getDepth(); z++) {
			for (int y = 0; y < board.getHeight(); y++) {
				for (int x = 0; x < board.getWidth(); x++) {
					Position pos = new Position(x, y, z);
					if (board.getPawnAtPosition(pos) != Board.PAWN_NONE) 
						positions.add(pos);
				}
			}
		}
		//WebSocketParser.unparserShowBoard(ws_server, (Position[]) positions.toArray());
	}

	@Override
	public void showPlayerPosition(VPlayer player, Position pos) {
		WebSocketParser.unparserPlayerPos(ws_server, player.getId(), pos);
	}

	@Override
	public void showWinner(VPlayer player) {
		WebSocketParser.unparserShowWinner(ws_server, player.getName());
	}

	@Override
	public void showPauseResume(boolean pr) {
		WebSocketParser.unparserPauseResume(ws_server, pr);

	}

	@Override
	public void exceptions(String message) {
		WebSocketParser.unparserException(ws_server, message);
	}

	private boolean checkCurrentGame() {
		return super.getCurrentGameId() != VViewer.DEFAULT_CURRENT_GAME_ID;
	}

	public void setPlayerPosition(int playerId, Position pos) {
		player2pos.replace(playerId, pos);
	}

	public Position getPlayerPosition(HumanWebPlayer humanWebPlayer, Board board) {
		System.out.println(humanWebPlayer.getId());
		Position res;
		do {
			//exceptions("Joues");
			Position lstpos = player2lstpos.getOrDefault(humanWebPlayer.getId(), null);
			res = player2pos.get(humanWebPlayer.getId());
			while(res==null || lstpos.equals(res));
			player2lstpos.replace(humanWebPlayer.getId(), res);
		} while(board.getPawnAtPosition(res) != Board.PAWN_NONE);
		return null;
	}
}
