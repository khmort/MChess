package pieces;

import utils.BitTools;

public class Knight {

	public static void init() {
		attacksBySquare = new long[64];
		for (int i = 0; i < 64; i++) {
			attacksBySquare[i] = getMaskAttacks(i);
		}
	}

	public static long getMaskAttacks(int square) {
		long board = BitTools.createBoard(square), attacks = 0L;
		BitTools.setBitOn(board, square);
		attacks |= BitTools.shiftRight(board, 6);
		attacks |= BitTools.shiftRight(board, 10);
		attacks |= BitTools.shiftRight(board, 15);
		attacks |= BitTools.shiftRight(board, 17);
		attacks |= BitTools.shiftLeft(board, 6);
		attacks |= BitTools.shiftLeft(board, 10);
		attacks |= BitTools.shiftLeft(board, 15);
		attacks |= BitTools.shiftLeft(board, 17);
		if ((board & BitTools.not_ab_file) == 0) {
			attacks &= BitTools.not_in_second_half;
		} else if ((board & BitTools.not_gh_file) == 0) {
			attacks &= BitTools.not_in_first_half;
		}
		return attacks;
	}

	public static long[] attacksBySquare;
}
