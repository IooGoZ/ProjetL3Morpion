package fr.NVT.TopOneReacher.kernel.boardgame;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.utils.GameState;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.kernel.utils.TwoDimensionsPositionStack;

public class Board {

	
	/*
	
	Class Board
	This class represents the board game and validate stroke played.
	
	
	*/
	
	//Constants
	public static final int PAWN_NONE = 0;
	public static final int DEFAULT_OUT_PAWN = -1;
	
	private static final int NB_CHECK_3D_AXES = 13;
	private static final int NB_CHECK_2D_AXES = 4;
	private static final int CHECK_RANGE_MAX = 4;
	private static final int CHECK_LENGTH = 5;
	private static final int DEPTH_IN_2D = 1;
	
	private static final int TWO_DIMENSIONS_POSITION_STACK_LENGTH_MARGIN = 1;
	private static final int TWO_DIMENSIONS_POSITION_STACK_SHIFT = 1;

	
	
	
	//---------
	
	//Size of board
	private final int height;
	private final int width;
	private final int depth;
	
	//-------------
	
	private final Game game;
	
	//List / Array store the strokes
	private final short[][][] board;//The ints are player's id or PAWN_NONE
	private final TwoDimensionsPositionStack playersStrokes;
	//------------------------------
	
	//State of board
	private final int nbPlayer;
	//--------------
	private int lastPositionSize;

	
	
	
	//Constructor
	public Board(Game game, int width, int height, int depth, int nbPlayer) {
		
		this.game = game;
		this.nbPlayer = nbPlayer;
		
		this.width = width;//x_max
		this.height = height;//y_max
		this.depth = depth;//z_max
		
		this.lastPositionSize = (width*height*depth)/nbPlayer + TWO_DIMENSIONS_POSITION_STACK_LENGTH_MARGIN;
		playersStrokes = new TwoDimensionsPositionStack(nbPlayer, this.lastPositionSize, TWO_DIMENSIONS_POSITION_STACK_SHIFT);
		
		this.board = new short[this.width][this.height][this.depth];
		initBoardTab();
	}

	
	//Methods
	
	
	//Init board with none value
	private void initBoardTab() {
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				for (int z = 0; z < this.depth; z++) {
					this.board[x][y][z] = Board.PAWN_NONE;
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
	
	public int getPawnAtPosition(Position pos) {
		if (Position.validatePosition(pos, width, height, depth)) 
			return this.board[pos.getX()][pos.getY()][pos.getZ()];
		return DEFAULT_OUT_PAWN;
	}

	private void setPawnAtPosition(Position pos, int id) {
		this.board[pos.getX()][pos.getY()][pos.getZ()] = (short) id;
	}
	
	
	//Play stroke and validate
	public boolean playPosition(VPlayer player, Position pos) {
		int player_id = player.getId();
		if (getPawnAtPosition(pos) == Board.PAWN_NONE && player_id != Board.PAWN_NONE && this.game.getGameState() == GameState.INGAME) {
			this.playersStrokes.add(player_id, pos);
			
			this.setPawnAtPosition(pos,player_id);
			
			checkEnd(pos, player);
			return true;
		} else return false;
	}
	
	
	//Check if player ends the game
	private boolean checkEnd(Position pos, VPlayer player) {
		int dir_max = NB_CHECK_2D_AXES;
		if (this.depth != DEPTH_IN_2D) dir_max = NB_CHECK_3D_AXES;
		
		for (byte dir = 1; dir <= dir_max; dir++) {
			if (checkDirection(dir, pos, player.getId())) {
				this.game.setWinner(player, pos);
				return true;
			}
		}
		
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				for (int z = 0; z < this.depth; z++) {
					if (this.board[x][y][z] == Board.PAWN_NONE) return false;
				}
			}
		}
		
		this.game.setWinner(null, null);
		return true;
		
	}
	
	public boolean checkEnd() {
			for (int x = 0; x < this.width; x++) {
				for (int y = 0; y < this.height; y++) {
					for (int z = 0; z < this.depth; z++) {
						if (this.board[x][y][z] == Board.PAWN_NONE) return false;
					}
				}
			}
			this.game.setWinner(null, null);
			return true;
	}
	
	//Check pawn alignment in a direction
	private boolean checkDirection(byte dir, Position pos, int player) {
		int rec = 0;
		
		for (short rg = -CHECK_RANGE_MAX; rg <= CHECK_RANGE_MAX; rg++) {
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
	public static Position getCheckPosition(byte dir, Position pos, short rg) {
		switch(dir) {
		//2D & 3D
		case 1 : //90
			return new Position(pos.getX()-rg, pos.getY(), pos.getZ());
		case 2 : //90
			return new Position(pos.getX(), pos.getY()-rg, pos.getZ());
		case 3 : //45
			return new Position(pos.getX()-rg, pos.getY()-rg, pos.getZ());
		case 4 : //45
			return new Position(pos.getX()+rg, pos.getY()-rg, pos.getZ());
		//3D Only
		case 5 : //90
			return new Position(pos.getX(), pos.getY(), pos.getZ()-rg);
		case 6 : //45
			return new Position(pos.getX(), pos.getY()+rg, pos.getZ()-rg);
		case 7 : //45
			return new Position(pos.getX(), pos.getY()-rg, pos.getZ()-rg);
		case 8 : //45
			return new Position(pos.getX()+rg, pos.getY(), pos.getZ()-rg);
		case 9 : //45
			return new Position(pos.getX()-rg, pos.getY(), pos.getZ()-rg);
		case 10 : //45*45
			return new Position(pos.getX()-rg, pos.getY()-rg, pos.getZ()-rg);
		case 11 : //45*45
			return new Position(pos.getX()-rg, pos.getY()-rg, pos.getZ()+rg);
		case 12 : //45*45
			return new Position(pos.getX()-rg, pos.getY()+rg, pos.getZ()-rg);
		case 13 : //45*45
			return new Position(pos.getX()-rg, pos.getY()+rg, pos.getZ()+rg);
		default : //45*45
			return null;
		}
	}
	
	//Return lasts positions list play (with index)
	public Position[] getLastPositions(int index) {
		Position[] poss = new Position[this.nbPlayer];
		boolean isNull = true;
		for (int i = 1; i <= this.nbPlayer; i++) {
			poss[i-1] = playersStrokes.getPosition(i, index);
			if (poss[i-1] != null) isNull = false;
		}
		if (isNull) return null;
		else return poss;
	}
	
	//Return the size of lasts positions list
	public int getLastPositionsSize() {
		return this.lastPositionSize;
	}
}
