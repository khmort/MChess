package pieces;

import utils.BitTools;

public class King {

	public static void init() {
		attacksBySquare = new long[64];
		for (int i = 0; i < 64; i++) {
			attacksBySquare[i] = getMaskAttacks(i);
		}
	}

	public static long getMaskAttacks(int square) {
		long board = BitTools.createBoard(square), attacks = 0L;
		if ((board & BitTools.not_a_file) != 0) {
			attacks |= BitTools.shiftLeft(board, 1);
			attacks |= BitTools.shiftLeft(board, 8);
			attacks |= BitTools.shiftLeft(board, 9);
			attacks |= BitTools.shiftRight(board, 7);
			attacks |= BitTools.shiftRight(board, 8);
		}
		if ((board & BitTools.not_h_file) != 0) {
			attacks |= BitTools.shiftRight(board, 1);
			attacks |= BitTools.shiftRight(board, 8);
			attacks |= BitTools.shiftRight(board, 9);
			attacks |= BitTools.shiftLeft(board, 7);
			attacks |= BitTools.shiftLeft(board, 8);
		}
		return attacks;
	}

	public static long[] attacksBySquare;
}
