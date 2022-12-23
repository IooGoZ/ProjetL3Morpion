package fr.NVT.TopOneReacher.modules.players;

import java.util.Random;

public class ChatGPTPlayer {
	private int[][][] board;

	public ChatGPTPlayer(int[][][] board) {
		this.board = board;
	}

	public int[] getBestMove(int n) {
		int bestRow = -1;
		int bestCol = -1;
		int bestDepth = -1;
		int bestScore = Integer.MIN_VALUE;

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				for (int k = 0; k < board[0][0].length; k++) {
					if (board[i][j][k] == 0) {
						int score = evaluate(i, j, k, n);
						if (score > bestScore) {
							bestRow = i;
							bestCol = j;
							bestDepth = k;
							bestScore = score;
						}
					}
				}
			}
		}

		if (bestRow == -1 || bestCol == -1 || bestDepth == -1) {
			Random rand = new Random();
			do {
				bestRow = rand.nextInt(board.length);
				bestCol = rand.nextInt(board[0].length);
				bestDepth = rand.nextInt(board[0][0].length);
			} while (board[bestRow][bestCol][bestDepth] != 0);
		}
		return new int[] { bestRow, bestCol, bestDepth };
	}

	private int evaluate(int i, int j, int k, int n) {
		int score = 0;

		// Évaluez la qualité de cette position en utilisant des algorithmes de
		// recherche de motifs
		// et d'autres approches pour déterminer si le joueur peut gagner à cette
		// position ou s'il peut empêcher l'adversaire de gagner
		score += checkLine(i, j, k, 1, 0, 0) * 10; // Vérifiez les lignes horizontales
		score += checkLine(i, j, k, 0, 1, 0) * 10; // Vérifiez les lignes verticales
		score += checkLine(i, j, k, 0, 0, 1) * 10; // Vérifiez les lignes de profondeur
		score += checkLine(i, j, k, 1, 1, 0) * 10; // Vérifiez les diagonales principales dans le plan xy
		score += checkLine(i, j, k, 1, 0, 1) * 10; // Vérifiez les diagonales principales dans le plan xz
		score += checkLine(i, j, k, 0, 1, 1) * 10; // Vérifiez les diagonales principales dans le plan yz
		score += checkLine(i, j, k, 1, 1, 1) * 10; // Vérifiez les diagonales principales dans l'espace
		score += checkLine(i, j, k, 1, -1, 0) * 10; // Vérifiez les diagonales secondaires dans le plan xy
		score += checkLine(i, j, k, 1, 0, -1) * 10; // Vérifiez les diagonales secondaires dans le plan xz
		score += checkLine(i, j, k, 0, 1, -1) * 10; // Vérifiez les diagonales secondaires dans le plan yz
		score += checkLine(i, j, k, 1, -1, -1) * 10; // Vérifiez les diagonales secondaires dans l'espace

		// Évaluez les positions futures possibles
		score += evaluateFuturePositions(i, j, k, n);

		// Attribuez des points en fonction de la manière dont les pions sont reliés
		// entre eux sur le plateau de jeu
		score += getConnectionScore(i, j, k);

		return score;
	}

	private int checkLine(int i, int j, int k, int x, int y, int z) {
		int player = board[i][j][k];
		int count = 1;
		int maxCount = 1;

		// Vérifiez dans la direction donnée (x, y, z) si le joueur a une ligne de cinq
		// pions alignés
		int row = i + x;
		int col = j + y;
		int depth = k + z;
		while (row >= 0 && row < board.length && col >= 0 && col < board[0].length && depth >= 0
				&& depth < board[0][0].length && board[row][col][depth] == player) {
			count++;
			row += x;
			col += y;
			depth += z;
		}

		// Vérifiez dans l'autre direction (x, y, z) si le joueur a une ligne de cinq
		// pions alignés
		row = i - x;
		col = j - y;
		depth = k - z;
		while (row >= 0 && row < board.length && col >= 0 && col < board[0].length && depth >= 0
				&& depth < board[0][0].length && board[row][col][depth] == player) {
			count++;
			row -= x;
			col -= y;
			depth -= z;
		}

		// Mettre à jour le compteur maximal de pions alignés trouvé jusqu'à présent
		maxCount = Math.max(maxCount, count);

		// Si le joueur a une ligne de cinq pions alignés, retournez un score élevé
		if (maxCount >= 5) {
			return 100;
		}

		// Sinon, retournez un score plus faible en fonction du nombre de pions alignés
		// trouvé
		return 10 * maxCount;
	}

	private int evaluateFuturePositions(int i, int j, int k, int n) {
		int score = 0;

		// Évaluez les positions futures possibles en utilisant une approche minimax
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) {
						continue;
					}
					int row = i + x;
					int col = j + y;
					int depth = k + z;
					if (row >= 0 && row < board.length && col >= 0 && col < board[0].length && depth >= 0
							&& depth < board[0][0].length)
					if (board[row][col][depth] == 0) {
						int futureScore = getFuturePositionsScore(row, col, depth, n - 1);
						score += futureScore;
					}
				}
			}
		}
		return score;
	}

	private int getFuturePositionsScore(int i, int j, int k, int n) {
		if (n == 0 || checkLine(i, j, k, 1, 0, 0) >= 5 || checkLine(i, j, k, 0, 1, 0) >= 5
				|| checkLine(i, j, k, 0, 0, 1) >= 5 || checkLine(i, j, k, 1, 1, 0) >= 5
				|| checkLine(i, j, k, 1, 0, 1) >= 5 || checkLine(i, j, k, 0, 1, 1) >= 5
				|| checkLine(i, j, k, 1, 1, 1) >= 5 || checkLine(i, j, k, 1, -1, 0) >= 5
				|| checkLine(i, j, k, 1, 0, -1) >= 5 || checkLine(i, j, k, 0, 1, -1) >= 5
				|| checkLine(i, j, k, 1, -1, -1) >= 5) {
			return 0;
		}

		int score = 0;

		// Évaluez les positions futures possibles en utilisant une approche minimax
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) {
						continue;
					}
					int row = i + x;
					int col = j + y;
					int depth = k + z;
					if (row >= 0 && row < board.length && col >= 0 && col < board[0].length && depth >= 0
							&& depth < board[0][0].length && board[row][col][depth] == 0) {
						board[row][col][depth] = 1;
						int futureScore = -getFuturePositionsScore(row, col, depth, n - 1);
						score = Math.max(score, futureScore);
						board[row][col][depth] = 0;
					}
				}
			}
		}

		return score;
	}

	private int getConnectionScore(int i, int j, int k) {
		int score = 0;

		// Attribuez des points en fonction de la manière dont les pions sont reliés
		// entre eux sur le plateau de jeu
		// Vous pouvez utiliser des algorithmes de recherche de motifs pour détecter les
		// motifs de connexion intéressants
		// et attribuer des points en conséquence

		// Exemple : attribuez des points si le pion actuel est entouré de pions dans
		// toutes les directions
		boolean isSurrounded = true;
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) {
						continue;
					}
					int row = i + x;
					int col = j + y;
					int depth = k + z;
					if (row < 0 || row >= board.length || col < 0 || col >= board[0].length || depth < 0
							|| depth >= board[0][0].length || board[row][col][depth] != 1) {
						isSurrounded = false;
						break;
					}
				}
				if (!isSurrounded) {
					break;
				}
			}
			if (!isSurrounded) {
				break;
			}
		}
		if (isSurrounded) {
			score += 10;
		}

		return score;
	}

}