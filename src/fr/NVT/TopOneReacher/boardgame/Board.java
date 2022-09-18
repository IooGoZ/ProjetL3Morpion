package fr.NVT.TopOneReacher.boardgame;

import java.util.ArrayList;
import java.util.List;

public class Board {

	private int height;
	private int width;
	
	private Pawn[][] board;
	private List<Position> noughtTab = new ArrayList<>();
	private List<Position> crosseTab = new ArrayList<>();
	
	private BoardState state = BoardState.LOADING;
	private Pawn winner = Pawn.NONE;
	
	public Board(int width, int height) {
		this.width = width;//x_max
		this.height = height;//y_max
		
		this.board = new Pawn[this.width][this.height];
		initBoardTab(this.board);
		
		this.state = BoardState.INGAME;
	}

	private void initBoardTab(Pawn[][] tab) {
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.width; y++) {
				tab[x][y] = Pawn.NONE;
			}
		}
	}
	
	
	
	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public BoardState getState() {
		return this.state;
	}

	public Pawn getWinner() {
		return this.winner;
	}
	
	public Pawn getPawnAtPosition(Position pos) {
		return this.board[pos.getX()][pos.getY()];
	}

	
	
	public boolean playPosition(Pawn player, Position pos) {
		if (getPawnAtPosition(pos).equals(Pawn.NONE) && !player.equals(Pawn.NONE) && this.state == BoardState.INGAME) {
			if (player.equals(Pawn.NOUGHT))
				this.noughtTab.add(pos);
			else
				this.crosseTab.add(pos);
			
			this.board[pos.getX()][pos.getY()] = player;
			
			if (checkEnd(pos, player)) {
				this.state = BoardState.FINISHED;
				this.winner = player;
			}
			return true;
		} else return false;
	}
	
	
	private boolean checkEnd(Position pos, Pawn player) {
		for (int dir = 1; dir <= 4; dir++) {
			if (checkDirection(dir, pos, player)) return true;
		}
		return false;
	}
	
	private boolean checkDirection(int dir, Position pos, Pawn player) {
		int rec = 0;
		for (int rg = -4; rg <= 4; rg++) {
			if (rec == 5) return true;
			Position check_position = getCheckPosition(dir, pos, rg);
			if (!Position.validatePosition(check_position, this.width, this.height)) continue;
			if (this.board[check_position.getX()][check_position.getY()].equals(player)) rec++;
			else rec = 0;
		}
		if (rec == 5) return true;
		return false;
	}

	private Position getCheckPosition(int dir, Position pos, int rg) {
		switch(dir) {
		case 1 :
			return new Position(pos.getX()+rg, pos.getY()-rg);
		case 2 :
			return new Position(pos.getX(), pos.getY()-rg);
		case 3 :
			return new Position(pos.getX()+rg, pos.getY()+rg);
		case 4 :
			return new Position(pos.getX()-rg, pos.getY());
		default :
			return null;
		}
	}
	
}
