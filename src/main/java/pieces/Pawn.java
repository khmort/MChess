package pieces;

import java.util.List;

import chessboard.ChessBoard;
import chessboard.Move;
import chessboard.Square;

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
		long board = 0l, attacks = 0l;
		board |= 1l << square;
		// not a-file
		if ((board & 0xfefefefefefefefel) != 0) {
			attacks |= board >>> 9;
		}
		// not h-file
		if ((board & 0x7f7f7f7f7f7f7f7fl) != 0) {
			attacks |= board >>> 7;
		}
		return attacks;
	}

	public static long getBlackAttacks(int square) {
		long board = 0l, attacks = 0l;
		board |= 1l << square;
		// not a-file
		if ((board & 0xfefefefefefefefel) != 0) {
			attacks |= board << 7;
		}
		// not h-file
		if ((board & 0x7f7f7f7f7f7f7f7fl) != 0) {
			attacks |= board << 9;
		}
		return attacks;
	}

	public static long getMaskAttacks(int square, int side) {
		if (side == 0) {
			return getWhiteAttacks(square);
		}
		return getBlackAttacks(square);
	}

	// public static void generateMoves(List<Move> moves, ChessBoard board, int forPiece, int color) {
	// 	if (color == 0) {
	// 		generatePawnMoves(moves, board, forPiece, color, 1, 7, 8, 'Q', 'K');
	// 	} else {
	// 		generatePawnMoves(moves, board, forPiece, color, 7, 1, -8, 'q', 'k');
	// 	}	
	// }

	public static void generateMoves(List<Move> moves, ChessBoard board, int forPiece, int color, int doublePushRow, int promoteRow,
										int direction, int promoteQueen, int promoteKnight) {

		int oppositeColor = color ^ 1;
		int sourceSquare;
		int targetName;
		int targetSquare;
		long pawns = board.getBitboard(forPiece);
		long oppositeOcc = board.getOccupancies(oppositeColor);
		long complementOcc = ~board.getOccupancies(color);

		while (pawns != 0) {
			
			sourceSquare = Long.numberOfTrailingZeros(pawns);
			pawns &= pawns - 1;

			// حملات احتمالی یا حملاتی که می تواند انجام دهد
			long possibleAttacks = attackBySquare[color][sourceSquare] & complementOcc;
			// حملاتی که 100 درصد می تواند انجام دهد
			long definiteAttacks = possibleAttacks & oppositeOcc;

			while (definiteAttacks != 0) {

				targetSquare = Long.numberOfTrailingZeros(definiteAttacks);
				definiteAttacks &= definiteAttacks - 1;

				targetName = board.pieceAt(targetSquare);

				if (Square.inRow(promoteRow, sourceSquare)) {
						moves.add(
							Move.createCaptureMove(forPiece, targetName, sourceSquare, targetSquare,
								true, promoteQueen, board.castleRight)
						);
						moves.add(
							Move.createCaptureMove(forPiece, targetName, sourceSquare, targetSquare,
								true, promoteKnight, board.castleRight)
						);
					} else {
						moves.add(
							Move.createCaptureMove(forPiece, targetName, sourceSquare, targetSquare,
								false, Move.NULL, board.castleRight)
						);
					}

			}

			int forwardSquare = sourceSquare + direction;

			if (forwardSquare >= 0 && forwardSquare < 64 && board.pieceAt(forwardSquare) == 0) {
				if (Square.inRow(promoteRow, sourceSquare)) {
					moves.add(
						Move.createPromoteMove(forPiece, Move.NULL, sourceSquare, forwardSquare,
												false, promoteQueen, board.castleRight)
					);
					moves.add(
						Move.createPromoteMove(forPiece, Move.NULL, sourceSquare, forwardSquare,
												false, promoteKnight, board.castleRight)
					);
				} else {
					// 2. یک واحد به سمت جلو
					moves.add(
						new Move(forPiece, Move.NULL, Move.NULL, sourceSquare,
								forwardSquare, false,
								false, false, false, board.castleRight)
					);

					// 3. حرکت double-push
					if (Square.inRow(doublePushRow, sourceSquare)) {
						if (board.pieceAt(forwardSquare + direction) == 0) {
							moves.add(
								Move.createDoublePushMove(forPiece, sourceSquare, forwardSquare + direction, board.castleRight)
							);
						}
					}
				}
			}	
		}
	}

	public static long[][] attackBySquare;

}