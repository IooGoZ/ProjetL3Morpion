package fr.NVT.TopOneReacher.boardgame;

import fr.NVT.TopOneReacher.utils.TwoDimensionsPositionStack;

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
	//---------
	
	//Size of board
	private int height;
	private int width;
	private int depth;
	//-------------
	
	//List / Array store the strokes
	private int[][][] board;//The ints are player's id or PAWN_NONE
	private TwoDimensionsPositionStack playersStrokes;
	//------------------------------
	
	//State of board
	private BoardState state = BoardState.LOADING;
	private int winner;
	//--------------
	
	
	//Constructor
	public Board(int width, int height, int depth, int nbPlayer) {
		this.width = width;//x_max
		this.height = height;//y_max
		this.depth = depth;//z_max
		
		playersStrokes = new TwoDimensionsPositionStack(nbPlayer, (width*height*depth)/nbPlayer + 1);
		
		this.board = new int[this.width][this.height][this.depth];
		initBoardTab(this.board);
		
		this.state = BoardState.INGAME;
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

	public BoardState getState() {
		return this.state;
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
	public boolean playPosition(int player, Position pos) {
		if (getPawnAtPosition(pos) == Board.PAWN_NONE && player != Board.PAWN_NONE && this.state == BoardState.INGAME) {
			this.playersStrokes.add(player, pos);
			
			this.board[pos.getX()][pos.getY()][pos.getZ()] = player;
			
			if (checkEnd(pos, player)) {
				this.state = BoardState.FINISHED;
				this.winner = player;
			}
			return true;
		} else return false;
	}
	
	
	private boolean checkEnd(Position pos, int player) {
		int dir_max = NB_CHECK_2D_AXES;
		if (this.depth != DEPTH_IN_2D) dir_max = NB_CHECK_3D_AXES;
		
		for (int dir = 1; dir <= dir_max; dir++) {
			if (checkDirection(dir, pos, player)) return true;
		}
		return false;
	}
	
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
	
	
	public static void test_getCheckPosition() {
		Board board = new Board(9, 9, 9, 1);
		Position pos = new Position(4, 4, 4);
		for (int dir = 1; dir <= 13; dir++) {
			for (int rg = -4; rg <= 4; rg++) {
				board.setPawnAtPosition(board.getCheckPosition(dir, pos, rg), 1);
			}
		}
		board.printBoard();
	}
}
