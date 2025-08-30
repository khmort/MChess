package pieces;

import java.util.List;

import chessboard.ChessBoard;
import chessboard.Move;

public class Knight {

	public static void init() {
		attacksBySquare = new long[64];
		for (int i = 0; i < 64; i++) {
			attacksBySquare[i] = getMaskAttacks(i);
		}
	}

	public static long getMaskAttacks(int square) {
		long board = 1l << square, attacks = 0L;
		if ((board & 0xfefefefefefefefel) != 0) {
			attacks |= board << 15;
			attacks |= board >>> 17;
			if ((board & 0xfcfcfcfcfcfcfcfcl) != 0) {
				attacks |= board << 6;
				attacks |= board >>> 10;
			}
		}
		if ((board & 0x7f7f7f7f7f7f7f7fl) != 0) {
			attacks |= board >>> 15;
			attacks |= board << 17;
			if ((board & 0x3f3f3f3f3f3f3f3fl) != 0) {
				attacks |= board >>> 6;
				attacks |= board << 10;
			}
		}
		return attacks;
	}

	public static void generateMoves(List<Move> moves, ChessBoard board, int forPiece, int color) {

		int oppositeColor = color ^ 1;
		int sourceSquare;
		int targetName;
		int targetSquare;
		long knights = board.getBitboard(forPiece);
		long allMoves;
		long definiteAttacks;
		long oppositeOcc = board.getOccupancies(oppositeColor);
		long complementOcc = ~board.getOccupancies(color);
		
		while (knights != 0) {

			sourceSquare = Long.numberOfTrailingZeros(knights);
			knights &= knights - 1;
			
			allMoves = attacksBySquare[sourceSquare] & complementOcc;
			definiteAttacks = allMoves & oppositeOcc;

			while (definiteAttacks != 0) {
				targetSquare = Long.numberOfTrailingZeros(definiteAttacks);
				definiteAttacks &= definiteAttacks - 1;
				allMoves &= ~(1l << targetSquare);

				targetName = board.pieceAt(targetSquare);
				moves.add(
					Move.createCaptureMove(forPiece, (char) targetName, sourceSquare, targetSquare,
						false, Move.NULL, board.castleRight)
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
		}

	}

	public static long[] attacksBySquare;
}
