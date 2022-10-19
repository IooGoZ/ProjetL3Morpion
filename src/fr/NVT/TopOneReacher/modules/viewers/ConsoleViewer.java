package fr.NVT.TopOneReacher.modules.viewers;

import java.io.FileWriter;
import java.io.IOException;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.Main;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.boardgame.VViewer;
import fr.NVT.TopOneReacher.kernel.utils.GameState;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.players.TeachersPlayer;
import fr.NVT.TopOneReacher.modules.players.TopTwoReacherPlayer;


	//ConsoleViewer is basic display system to run tests
public class ConsoleViewer extends VViewer {

	//Constants
	private static final int DEFAULT_WIDTH = 50;
	private static final int DEFAULT_HEIGHT = 50;
	private static final int DEFAULT_DEPTH = 1;
	
	private static final int NB_OF_GAMES = 100;
	
	private static final String OUT_FILE_NAME = "morpion_out.txt";
	
	//Variables
	private int width, height, depth;
	
	private int[][][] fake_board;
	
	
	//Constructors
	public ConsoleViewer(Main main, int width, int height, int depth) {
		super(main);
		
		//Store players stats
		int pl2winners[] = null;
		String pl2names[] = null;
		int nb_players = -1;
		
		//Loop to create games
		for (int game_index = 0; game_index < NB_OF_GAMES; game_index++) {
			//Create game
			int game_id =  super.createNewGame(width, height, depth);
			Game game = super.getGame(game_id);
			
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
		new TeachersPlayer(game, "TeachersPlayer");
		new TopTwoReacherPlayer(game, "TopTwoReacher");
	}

	@Override
	public void initScreenBoard(int width, int height, int depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	@Override
	public void resetScreen() {
		//no action
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
		if (player == null) return;
		fake_board[pos.getX()][pos.getY()][pos.getZ()] = player.getId();
		print();
	}

	@Override
	public void showWinner(VPlayer player) {
		if (player == null) System.out.println("La partie est nulle !");
		else System.out.println(player.getName() + " a gagné la partie !");
	}

	@Override
	public void showPauseResume(boolean pr) {
		System.out.print("Pause : " + pr);
	}

	@Override
	public void exceptions(String message) {
		System.err.println(message);
		
	}
	
	//Print full board in console (up to 8 players)
	private void print() {
		for (int z = 0; z < this.depth; z++) {
			System.out.println("z = " + z + "--------------");
			for (int y = 0; y < this.height; y++) {
				if (y < 10 ) System.out.print("y = 0" + y + "----");
				else System.out.print("y = " + y + "----");
				for (int x = 0; x < this.width; x++) {
					int i = this.fake_board[x][y][z];
					char c;
					switch (i) {
					case 1: {
						c = 'X'; break;
					}
					case 2: {
						c = '0'; break;
					}
					case 3: {
						c = '$'; break;
					}
					case 4: {
						c = '#'; break;
					}
					case 5: {
						c = ':'; break;
					}
					case 6: {
						c = '!'; break;
					}
					case 7: {
						c = '?'; break;
					}
					case 8: {
						c = '&'; break;
					}
					default:
						c = ' ';
					}
					
					System.out.print("|" + c);
				}
				System.out.println("|");
			}
			System.out.println("-------------------");
		}
	}
	
	

}
