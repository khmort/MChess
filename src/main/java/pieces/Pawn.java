package pieces;

import java.util.List;

import chessboard.ChessBoard;
import chessboard.Move;
import chessboard.Square;
import utils.BitTools;

public class Pawn {

	public static void init() {
		attackBySquare = new long[2][64];
		for (int i=0; i<64; i++) {
			attackBySquare[0][i] = getWhiteAttacks(i);
		}
		for (int i=0; i<64; i++) {
			attackBySquare[1][i] = getBlackAttacks(i);
		}
	}

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

	public static void generateMoves(List<Move> moves, ChessBoard board, char forPiece, int color) {

		int oppositeColor = color ^ 1,
			sourceSquare,
			targetName,
			targetSquare;
		
		long pawns = board.getBitboard(forPiece);

		while (pawns != 0) {
			
			sourceSquare = BitTools.getFirstSetBitPos(pawns);
			pawns = BitTools.setBitOff(pawns, sourceSquare);

			// حملات احتمالی یا حملاتی که می تواند انجام دهد
			long possibleAttacks = attackBySquare[color][sourceSquare] & ~board.getOccupancies(color);
			// حملاتی که 100 درصد می تواند انجام دهد
			long definiteAttacks = possibleAttacks & board.getOccupancies(oppositeColor);

			while (definiteAttacks != 0) {

				targetSquare = BitTools.getFirstSetBitPos(definiteAttacks);

				targetName = board.pieceAt(targetSquare);

				if (Square.inRow(color == 0 ? 1 : 8, targetSquare)) {
					moves.add(
						Move.createCaptureMove(forPiece, (char) targetName, sourceSquare, targetSquare, true, color == 0 ? 'Q' : 'q', board.castleRight)
					);
					moves.add(
						Move.createCaptureMove(forPiece, (char) targetName, sourceSquare, targetSquare, true, color == 0 ? 'N' : 'n', board.castleRight)
					);
				} else {
					moves.add(
						Move.createCaptureMove(forPiece, (char) targetName, sourceSquare, targetSquare, false, Move.NULL_CHAR, board.castleRight)
					);
				}

				definiteAttacks = BitTools.setBitOff(definiteAttacks, targetSquare);

			}

			// محاسبه حرکات ساده
			// 1. حرکت promote
			if (Square.inRow(color == 0 ? 2 : 7, sourceSquare)) {
				moves.add(
					Move.createPromoteMove(forPiece, Move.NULL_CHAR, sourceSquare,
											sourceSquare + (color == 0 ? -8 : 8),
											false, color == 0 ? 'Q' : 'q', board.castleRight)
				);
				moves.add(
					Move.createPromoteMove(forPiece, Move.NULL_CHAR, sourceSquare,
											sourceSquare + (color == 0 ? -8 : 8),
											false, color == 0 ? 'N' : 'n', board.castleRight)
				);
			} else {
				
				if (board.pieceAt(sourceSquare + (color == 0 ? -8 : 8)) == 0) {
					// 2. یک واحد به سمت جلو
					moves.add(
						new Move(forPiece, Move.NULL_CHAR, Move.NULL_CHAR, sourceSquare,
								sourceSquare + (color == 0 ? -8 : 8), false,
								false, false, false, board.castleRight)
					);

					// 3. حرکت double-push
					if (Square.inRow(color == 0 ? 7 : 2, sourceSquare)) {
						if (board.pieceAt(sourceSquare + (color == 0 ? -16 : 16)) == 0) {
							moves.add(
								Move.createDoublePushMove(forPiece, sourceSquare, sourceSquare + (color == 0 ? -16 : 16), board.castleRight)
							);
						}
					}
				}
			}

		}
	}

	public static long[][] attackBySquare;

}