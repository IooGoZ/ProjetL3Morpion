package fr.NVT.TopOneReacher.modules.viewers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.Main;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.boardgame.VViewer;
import fr.NVT.TopOneReacher.kernel.utils.GameState;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.players.ReverseTTRPlayer;
import fr.NVT.TopOneReacher.modules.players.TTRPlayer;
import fr.NVT.TopOneReacher.modules.players.TeachersPlayer;


	//ConsoleViewer is basic display system to run tests
public class ConsoleViewer extends VViewer {

	//Constants
	private static final int DEFAULT_WIDTH = 10;
	private static final int DEFAULT_HEIGHT = 10;
	private static final int DEFAULT_DEPTH = 1;
	
	private static final int NB_OF_GAMES = 1000;
	
	private static final String OUT_FILE_NAME = "morpion_out.txt";
	
	//Variables
	private int width, height, depth;
	
	private int[][][] fake_board;
	
	private PrintStream x_train;
	private PrintStream y_train;
	
	
	//Constructors
	public ConsoleViewer(Main main, int width, int height, int depth) {
		super(main);
		
		this.width = width;
		this.height = height;
		this.depth = depth;
		
		/*try {
			this.x_train = new PrintStream(new File("x_train.txt"));
			this.y_train = new PrintStream(new File("y_train.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		this.x_train = System.out;
		this.y_train = null;
		
		
		//Store players stats
		int pl2winners[] = null;
		String pl2names[] = null;
		int nb_players = -1;
		
		
		
		//Loop to create games
		for (int game_index = 0; game_index < NB_OF_GAMES; game_index++) {
			System.out.println(String.format("%10d", game_index) + " / " + NB_OF_GAMES);
			//Create game
			int game_id =  super.createNewGame(width, height, depth);
			Game game = super.getGame(game_id);
			
			resetScreen();
			
			//Init players
			initPlayers(game);
			
			//Create players stats tab
			if (pl2winners == null) {
				nb_players = game.getNbPlayers();
				pl2winners = new int[nb_players];
				pl2names = new String[nb_players];
				for (VPlayer pl : game.getPlayersList()) {
					pl2names[pl.getId()-1] = pl.getName();
				}
			}
			
			//Run the game
			super.runGame(game_id);
			
			//Wait the party end
			while (game.getGameState() != GameState.FINISHED);
			
			//Store winners stats
			pl2winners[game.getWinner()-1]++;
			
			//Release game memory
			super.removeGame(game_id);
		}
		
		//Prepare the display 
		String res = "";
		for (int i = 0; i < nb_players; i++) {
			res += pl2names[i] + " --> " + pl2winners[i] + "\n";
		}
		
		//Write result in stdout
		System.out.println(res);
		
		//Write result in file
		try {
		      FileWriter myWriter = new FileWriter(OUT_FILE_NAME);
		      myWriter.write(res);
		      myWriter.close();
		      System.out.println("Successfully wrote to the file.");
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		
	}
	
	public ConsoleViewer(Main main) {
		this(main, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_DEPTH);
	}
	
	//Init players for each game
	public void initPlayers(Game game) {
		new ReverseTTRPlayer(game, "ReverseTopTwoReacherPlayer One");
		new TTRPlayer(game, "TopTwoReacherPlayer Two");
	}

	@Override
	public void initScreenBoard(int width, int height, int depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	@Override
	public void resetScreen() {
		this.fake_board = new int[this.width][this.height][this.depth];
		for (int z = 0; z < this.depth; z++) {
			for (int y = 0; y < this.height; y++) {
				for (int x = 0; x < this.width; x++) {
					this.fake_board[x][y][z] = -1;
				}
			}
		}
	}

	@Override
	public void shownBoard(Board board) {
		this.fake_board = new int[this.width][this.height][this.depth];
		for (int z = 0; z < this.depth; z++) {
			for (int y = 0; y < this.height; y++) {
				for (int x = 0; x < this.width; x++) {
					this.fake_board[x][y][z] = board.getPawnAtPosition(new Position(x, y, z));
				}
			}
		}
		//print(this.x_train);
	}

	@Override
	public synchronized void showPlayerPosition(VPlayer player, Position pos) {
		if (player == null) return;
		if (player.getId() == 2) {
			//print(this.x_train);
			//this.y_train.println(',');
			//this.y_train.print("[" + pos.getX() + ", " + pos.getY() + "]");
		}
		this.fake_board[pos.getX()][pos.getY()][pos.getZ()] = player.getId();
		return;
	}

	@Override
	public synchronized void showWinner(VPlayer player) {
		if (player == null) System.out.println("La partie est nulle !");
		else System.out.println(player.getName() + " a gagné la partie !");
	}

	@Override
	public synchronized void showPauseResume(boolean pr) {
		System.out.print("Pause : " + pr);
	}

	@Override
	public synchronized void exceptions(String message) {
		System.err.println(message);
		
	}
	
	//Print full board in console (up to 8 players)
	private void print(PrintStream out) {
		
		for (int z = 0; z < this.depth; z++) {
			out.println(',');
			out.print("[");
			boolean first_y = true;
			for (int y = 0; y < this.height; y++) {
				if (first_y)
					first_y = false;
				else out.println(',');
				out.print('[');
				boolean first_x = true;
				for (int x = 0; x < this.width; x++) {
					if (first_x)
						first_x = false;
					else out.print(',');
					int i = this.fake_board[x][y][z];
					char c;
					switch (i) {
					case 1: {
						c = '1'; break;
					}
					case 2: {
						c = '2'; break;
					}
					case 3: {
						c = '3'; break;
					}
					case 4: {
						c = '4'; break;
					}
					case 5: {
						c = '5'; break;
					}
					case 6: {
						c = '6'; break;
					}
					case 7: {
						c = '7'; break;
					}
					case 8: {
						c = '8'; break;
					}
					default:
						c = '0';
						
					}
					
					out.print(" " + c);
				}
				out.print("]");
			}
			out.print("]");
		}
		out.println();
	}
	
	

}
