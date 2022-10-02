package fr.NVT.TopOneReacher.modules.viewers;


import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.Main;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.Position;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.boardgame.VViewer;
import fr.NVT.TopOneReacher.modules.players.RandomPlayer;

public class ConsoleViewer extends VViewer {

	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 100;
	private static final int DEFAULT_DEPTH = 1;
	
	private static final int DEFAULT_GAME_ID = 0;
	
	private int width, height, depth;
	private Game game;
	
	private int[][][] fake_board;
	
	public ConsoleViewer(Main main, int width, int height, int depth) {
		super(main);
		this.game = new Game(this, DEFAULT_GAME_ID, width, height, depth);
		
		this.game.createNewPlayer(new RandomPlayer(game, "One"));
		this.game.createNewPlayer(new RandomPlayer(game, "Two"));
		
		this.game.run();
		
	}
	
	public ConsoleViewer(Main main) {
		this(main, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_DEPTH);
	}

	@Override
	public void initScreenBoard(int width, int height, int depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		
	}

	@Override
	public void resetScreen() {
		return;
	}

	@Override
	public void shownBoard(Board board) {
		fake_board = new int[this.width][this.height][this.depth];
		for (int z = 0; z < this.depth; z++) {
			for (int y = 0; y < this.height; y++) {
				for (int x = 0; x < this.width; x++) {
					fake_board[x][y][z] = board.getPawnAtPosition(new Position(x, y, z));
				}
			}
		}
		print();
	}

	@Override
	public void showPlayerPosition(VPlayer player, Position pos) {
		fake_board[pos.getX()][pos.getY()][pos.getZ()] = player.getId();
		print();
	}

	@Override
	public void showWinner(VPlayer player) {
		System.out.println(player.getName() + " a gagné la partie !");
	}

	@Override
	public void showPauseResume(boolean pr) {
		System.out.print("Pause : " + pr);
	}

	@Override
	public void exceptions(String message) {
		System.err.println(message);
		
	}
	
	private void print() {
		for (int z = 0; z < this.depth; z++) {
			System.out.println("z = " + z + "--------------");
			for (int y = 0; y < this.height; y++) {
				System.out.print("y = " + y + "----");
				for (int x = 0; x < this.width; x++) {
					int i = this.fake_board[x][y][z];
					System.out.print(" " + i + " ");
				}
				System.out.println();
			}
			System.out.println("-------------------");
		}
	}

}
