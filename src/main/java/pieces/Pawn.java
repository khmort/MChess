package pieces;

import utils.BitTools;

public class Pawn {

	public static long getWhiteAttacks(int square) {
		long board = 0L, attacks = 0L;
		board = BitTools.setBitOn(board, square);
		if ((board & BitTools.not_h_file) != 0) {
			attacks |= BitTools.shiftLeft(board, 7);
		}
		if ((board & BitTools.not_a_file) != 0) {
			attacks |= BitTools.shiftLeft(board, 9);
		}
		return attacks;
	}

	public static long getBlackAttacks(int square) {
		long board = 0L, attacks = 0L;
		board = BitTools.setBitOn(board, square);
		if ((board & BitTools.not_h_file) != 0) {
			attacks |= BitTools.shiftRight(board, 9);
		}
		if ((board & BitTools.not_a_file) != 0) {
			attacks |= BitTools.shiftRight(board, 7);
		}
		return attacks;
	}

	public static long getMaskAttacks(int square, int side) {
		if (side == 0) {
			return getWhiteAttacks(square);
		}
		return getBlackAttacks(square);
	}

	public static long[][] attackBySquare;

}
