package fr.NVT.TopOneReacher.kernel.boardgame;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.utils.GameState;
import fr.NVT.TopOneReacher.kernel.utils.TwoDimensionsPositionStack;
import fr.NVT.TopOneReacher.kernel.vplayer.VPlayer;

public class Board {

	
	/*
	
	Class Board
	This class represents the board game and validate stroke played.
	
	
	*/
	
	//Constants
	private static final int PAWN_NONE = 0;
	
	private static final int NB_CHECK_3D_AXES = 13;
	private static final int NB_CHECK_2D_AXES = 4;
	private static final int CHECK_RANGE_MAX = 4;
	private static final int CHECK_LENGTH = 5;
	private static final int DEPTH_IN_2D = 1;
	
	private static final int TWO_DIMENSIONS_POSITION_STACK_LENGTH_MARGIN = 1;
	
	
	//---------
	
	//Size of board
	private int height;
	private int width;
	private int depth;
	//-------------
	
	private Game game;
	
	//List / Array store the strokes
	private int[][][] board;//The ints are player's id or PAWN_NONE
	private TwoDimensionsPositionStack playersStrokes;
	//------------------------------
	
	//State of board
	private int winner;
	//--------------

	
	
	
	//Constructor
	public Board(Game game, int width, int height, int depth, int nbPlayer) {
		
		this.game = game;
		
		this.width = width;//x_max
		this.height = height;//y_max
		this.depth = depth;//z_max
		
		playersStrokes = new TwoDimensionsPositionStack(nbPlayer, (width*height*depth)/nbPlayer + TWO_DIMENSIONS_POSITION_STACK_LENGTH_MARGIN);
		
		this.board = new int[this.width][this.height][this.depth];
		initBoardTab(this.board);
	}

	
	//Methods
	
	
	//Init board with none value
	private void initBoardTab(int[][][] tab) {
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.width; y++) {
				for (int z = 0; z < this.width; z++) {
					tab[x][y][z] = Board.PAWN_NONE;
				}
			}
		}
	}
	
	//Access function
	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}
	
	public int getDepth() {
		return depth;
	}

	public int getWinner() {
		return this.winner;
	}
	
	public int getPawnAtPosition(Position pos) {
		return this.board[pos.getX()][pos.getY()][pos.getZ()];
	}

	private void setPawnAtPosition(Position pos, int id) {
		this.board[pos.getX()][pos.getY()][pos.getZ()] = id;
	}
	
	
	//Play stroke and validate
	public boolean playPosition(VPlayer player, Position pos) {
		int player_id = player.getId();
		if (getPawnAtPosition(pos) == Board.PAWN_NONE && player_id != Board.PAWN_NONE && this.game.getGameState() == GameState.INGAME) {
			this.playersStrokes.add(player_id, pos);
			
			this.setPawnAtPosition(pos,player_id);
			
			if (checkEnd(pos, player_id)) {
				this.game.setWinner(player);
			}
			return true;
		} else return false;
	}
	
	
	//Check if player ends the game
	private boolean checkEnd(Position pos, int player) {
		int dir_max = NB_CHECK_2D_AXES;
		if (this.depth != DEPTH_IN_2D) dir_max = NB_CHECK_3D_AXES;
		
		for (int dir = 1; dir <= dir_max; dir++) {
			if (checkDirection(dir, pos, player)) return true;
		}
		return false;
	}
	
	//Check pawn alignment in a direction
	private boolean checkDirection(int dir, Position pos, int player) {
		int rec = 0;
		
		for (int rg = -CHECK_RANGE_MAX; rg <= CHECK_RANGE_MAX; rg++) {
			if (rec == CHECK_LENGTH) return true;
			Position check_position = getCheckPosition(dir, pos, rg);
			if (!Position.validatePosition(check_position, this.width, this.height, this.depth)) continue;
			if (this.board[check_position.getX()][check_position.getY()][check_position.getZ()] == player) rec++;
			else rec = 0;
		}
		if (rec == CHECK_LENGTH) return true;
		return false;
	}

	//Get coordonate in a direction
	private Position getCheckPosition(int dir, Position pos, int rg) {
		switch(dir) {
		//2D & 3D
		case 1 :
			return new Position(pos.getX()-rg, pos.getY(), pos.getZ());
		case 2 :
			return new Position(pos.getX(), pos.getY()-rg, pos.getZ());
		case 3 :
			return new Position(pos.getX()-rg, pos.getY()-rg, pos.getZ());
		case 4 :
			return new Position(pos.getX()+rg, pos.getY()-rg, pos.getZ());
		//3D Only
		case 5 :
			return new Position(pos.getX(), pos.getY(), pos.getZ()-rg);
		case 6 :
			return new Position(pos.getX(), pos.getY()+rg, pos.getZ()-rg);
		case 7 :
			return new Position(pos.getX(), pos.getY()-rg, pos.getZ()-rg);
		case 8 :
			return new Position(pos.getX()+rg, pos.getY(), pos.getZ()-rg);
		case 9 :
			return new Position(pos.getX()-rg, pos.getY(), pos.getZ()-rg);
		case 10 :
			return new Position(pos.getX()-rg, pos.getY()-rg, pos.getZ()-rg);
		case 11 :
			return new Position(pos.getX()-rg, pos.getY()-rg, pos.getZ()+rg);
		case 12 :
			return new Position(pos.getX()-rg, pos.getY()+rg, pos.getZ()-rg);
		case 13 :
			return new Position(pos.getX()-rg, pos.getY()+rg, pos.getZ()+rg);
		default :
			return null;
		}
	}
	
	//DEBUG (non livré dans la version finale)======================================================================
	
	
	//Print the board game
	public void printBoard() {
		for (int z = 0; z < depth; z++) {
			System.out.println("z = " + z + "--------------");
			for (int y = 0; y < height; y++) {
				System.out.print("y = " + y + "----");
				for (int x = 0; x < width; x++) {
					int i = board[x][y][z];
					System.out.print(" " + i + " ");
				}
				System.out.println();
			}
			System.out.println("-------------------");
		}
	}
}