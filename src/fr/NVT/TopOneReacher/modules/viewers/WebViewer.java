package fr.NVT.TopOneReacher.modules.viewers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.Main;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.boardgame.VViewer;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.players.ChatGPTInterfacePlayer;
import fr.NVT.TopOneReacher.modules.players.HumanWebPlayer;
import fr.NVT.TopOneReacher.modules.players.RandomPlayer;
import fr.NVT.TopOneReacher.modules.players.ReverseTTRPlayer;
import fr.NVT.TopOneReacher.modules.players.TeachersPlayer;
import fr.NVT.TopOneReacher.modules.players.TTRPlayer;
import fr.NVT.TopOneReacher.modules.ressources.WebSocketParser;
import fr.NVT.TopOneReacher.modules.ressources.WebSocketServer;

public class WebViewer extends VViewer {
	
	private WebSocketServer ws_server;
	private final WebSocketParser ws_parser = new WebSocketParser(this);
	
	int last_width, last_height, last_depth;
	
	public WebViewer(Main main) {
		super(main);
		try {
			ws_server = new WebSocketServer(this);
			ws_server.open();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	//Parse Call----------------
	
	public int createNewGame(int width, int height, int depth) {
		int id = super.createNewGame(width, height, depth);
		Game game = super.getGame(id);
		
		new ReverseTTRPlayer(game, "ReverseTopTwoReacherPlayer");
		new TTRPlayer(game, "TopTwoReacherPlayer");
		
		
//		new TopTwoReacherPlayer(game, "TopTwoReacher Two");
//		new TopTwoReacherPlayer(game, "TopTwoReacher Three");
//		new TopTwoReacherPlayer(game, "TopTwoReacher Four");
//		new TopTwoReacherPlayer(game, "TopTwoReacher Five");
//		new TopTwoReacherPlayer(game, "TopTwoReacher Six");
//		new TopTwoReacherPlayer(game, "TopTwoReacher Seven");
		
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
		if (player != null)
			WebSocketParser.unparserShowWinner(ws_server, player.getName());
		else WebSocketParser.unparserShowWinner(ws_server, "Nobody");
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

	private HumanWebPlayer getHumanPlayer(int id) {
		for (VPlayer pl : getGame(getCurrentGameId()).getPlayersList()) {
			if (pl instanceof HumanWebPlayer && pl.getId() == id) return (HumanWebPlayer) pl;
		}
		return null;
	}
	
	public void playPosition(int id, Position pos) {
		HumanWebPlayer pl = getHumanPlayer(id);
		if (pl == null) {
			exceptions("Bad player ID");
			return;
		}
		pl.setNextPosition(pos);
	}
	
	public WebSocketParser getParser() {
		return ws_parser;
	}


	public void requestHuman(int id) {
		WebSocketParser.unparserRequestHuman(ws_server, id);
		
	}
}
