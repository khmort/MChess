package pieces;

import java.util.List;
import chessboard.ChessBoard;
import chessboard.Move;

public class King {

	public static void init() {
		attacksBySquare = new long[64];
		for (int i = 0; i < 64; i++) {
			attacksBySquare[i] = getMaskAttacks(i);
		}
	}

	public static long getMaskAttacks(int square) {
		long board = 1l << square, attacks = 0L;
		// not a-file
		if ((board & 0xfefefefefefefefel) != 0) {
			attacks |= board >>> 1;
			attacks |= board >>> 9;
			attacks |= board << 7;
		}
		// not h-file
		if ((board & 0x7f7f7f7f7f7f7f7fl) != 0) {
			attacks |= board << 1;
			attacks |= board << 9;
			attacks |= board >>> 7;
		}
		attacks |= board << 8;
		attacks |= board >>> 8;
		return attacks;
	}

	public static void generateMoves(List<Move> moves, ChessBoard board, int forPiece, int color, int qsCastleSquare,
			int ksCastleSquare, long queenSideOcc, long kingSideOcc) {

		int sourceSquare = Long.numberOfTrailingZeros(board.getBitboard(forPiece));
		int oppositeColor = color ^ 1;
		int targetName;
		int targetSquare;
		long allMoves = attacksBySquare[sourceSquare] & ~board.getOccupancies(color);
		long definiteAttacks = allMoves & board.getOccupancies(oppositeColor);

		while (definiteAttacks != 0) {

			targetSquare = Long.numberOfTrailingZeros(definiteAttacks);
			definiteAttacks &= definiteAttacks - 1;
			allMoves &= ~(1l << targetSquare);

			targetName = board.pieceAt(targetSquare);
			moves.add(
				Move.createCaptureMove(forPiece, targetName, sourceSquare, targetSquare, false, Move.NULL, board.castleRight)
			);
		}

		while (allMoves != 0) {
			targetSquare = Long.numberOfTrailingZeros(allMoves);
			allMoves &= allMoves - 1;

			moves.add(
				new Move(forPiece, Move.NULL, Move.NULL, sourceSquare, targetSquare,
					false, false, false, false, board.castleRight)
			);
		}

		if (board.canCastle(color, 0) == 1) {
			if ((board.getOccupancies(2) & queenSideOcc) == 0) {
				moves.add(
					Move.createCastleMove(forPiece, sourceSquare, qsCastleSquare, board.castleRight)
				);
			}
		}
		if (board.canCastle(color, 1) == 1) {
			if ((board.getOccupancies(2) & kingSideOcc) == 0) {
				moves.add(
					Move.createCastleMove(forPiece, sourceSquare, ksCastleSquare, board.castleRight)
				);
			}
		}

	}

	public static long BLACK_QUEEN_SIDE_OCC = 0x000000000000000el;
	public static long BLACK_KING_SIDE_OCC = 0x0000000000000060l;
	public static long WHITE_QUEEN_SIDE_OCC = 0x0e00000000000000l;
	public static long WHITE_KING_SIDE_OCC = 0x6000000000000000l;

	public static long[] attacksBySquare;
}
