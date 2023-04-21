package fr.NVT.TopOneReacher.modules.players;

import java.util.ArrayList;
import java.util.List;

import fr.NVT.TopOneReacher.kernel.Game;
import fr.NVT.TopOneReacher.kernel.boardgame.Board;
import fr.NVT.TopOneReacher.kernel.boardgame.VPlayer;
import fr.NVT.TopOneReacher.kernel.utils.Position;
import fr.NVT.TopOneReacher.modules.ressources.DirectionPosition;
import fr.NVT.TopOneReacher.modules.ressources.PlayerUtils;

public class TreePlayer extends VPlayer {

	
	private static int DEPTH = 3;
	
	private Board board;
	private Position best_pos;
	private int best_score;
	private int nb_res;

	public TreePlayer(Game game, String name) {
		super(game, name);
	}
	
	private synchronized void registerScore(Position position, int score) {
		System.out.println(score);
		if (score > best_score) {
			best_score = score;
			best_pos = position;
		}
		nb_res++;
		Thread.yield();
	}
	
	private List<Position> getAccessibleEmptyPosition(int[][][] board_array, int player_id) {
		List<Position> res = new ArrayList<>();
		for (int i = 0; i < board_array.length; i++)
			for (int j = 0; j < board_array[0].length; j++)
				for (int k = 0; k < board_array[0][0].length; k++) {
					Position curr_pos = new Position(i, j, k);
					if (board_array[curr_pos.getX()][curr_pos.getY()][curr_pos.getZ()] == player_id)
						for (byte dir = 0; dir <= 13; dir++)
							for (short rg = -4; rg <= 4; rg++) {
								Position pos = Board.getCheckPosition(dir, curr_pos, rg);
								if (!Position.validatePosition(pos, board.getWidth(), board.getHeight(), board.getDepth()) || res.contains(pos))
									continue;
								else if (board_array[pos.getX()][pos.getY()][pos.getZ()] == Board.PAWN_NONE) {
									res.add(pos);
								}
								continue;
							}
				}
		return res;
	}

	private Position findBestMove(Position default_pos) {
		
		final int[][][] board_array = new int[board.getWidth()][board.getHeight()][board.getDepth()];
		for (int i = 0; i < board.getWidth(); i++)
			for (int j = 0; j < board.getHeight(); j++)
				for (int k = 0; k < board.getDepth(); k++)
					board_array[i][j][k] = board.getPawnAtPosition(new Position(i, j, k));
		
		best_pos = default_pos;
		best_score = Integer.MIN_VALUE;
		
		nb_res = 0;
		int nb_curr = 0;
		
		for (int i = 1; i < getGame().getNbPlayers(); i++) {
			List<Position> positions = getAccessibleEmptyPosition(board_array, i);
			for (final Position pos : positions) {
				
				final int player_id = i;
				Thread t = new Thread(() -> {
					
					int[][][] curr_board = board_array.clone();
					curr_board[pos.getX()][pos.getY()][pos.getZ()] = player_id;
					
					int score = treeCourse(player_id, player_id, curr_board, DEPTH);
					
					registerScore(pos, score);
				});
				t.start();
				nb_curr++;
			}
		}
		
		
		
		while(nb_res != nb_curr) {
			Thread.yield();
		};

		return best_pos;
	}
	
	private int getNextPlayer(int player_id) {
		int nb = getGame().getNbPlayers();
		int val = ((player_id) % nb)+1;
		return val;
	}
	

	private int treeCourse(int player, int constant_player, int[][][] iboard, int depth) {
		List<Position> positions = getAccessibleEmptyPosition(iboard, player);
		int result = evaluateBoard(positions, iboard, player);
		if (depth == 0) {
			return result;
		}
		
		int curr_score = 0;
		
		for(Position pos : positions) {
			iboard[pos.getX()][pos.getY()][pos.getZ()] = player;
				curr_score = Math.max(curr_score, treeCourse(getNextPlayer(player), constant_player, iboard.clone(), depth - 1));
		}
		
		if (player == constant_player)
			return curr_score + result;
		else return curr_score;
	}

	public int evaluateBoard(List<Position> positions, int[][][] iboard, int player) {
		int score = 0;
		
		for(Position pos : positions) {
			for (byte dir = 1; dir <= 13; dir++) {
				score += evaluateDP(iboard, new DirectionPosition(pos, dir, (byte) 1), player);
			}
		}

	    return score;
	}
	
	private int evaluateDP(int[][][] iboard, DirectionPosition dp, int player) {
		if (PlayerUtils.zoneIsOpen(board, player, dp)) {
			int count = 0;
			for (short i = -4 ; i <= 4; i++) {
				Position check_pos = Board.getCheckPosition(dp.getDir(), dp.getPos(), i);
				int pawn = iboard[check_pos.getX()][check_pos.getY()][check_pos.getZ()];
				if (pawn == player) count++;
			}

			return (int) Math.pow(count, 1.5d);
		}
		return 0;
	}


	@Override
	public Position loop(Board board) {
		this.board = board;
		
		Position pos = PlayerUtils.getRandomPosition(board);
		
		Position best_pos = findBestMove(pos);
		
		

		return best_pos;
	}

}
