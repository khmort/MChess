package pieces;

import java.util.List;
import chessboard.ChessBoard;
import chessboard.Move;
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

	public static void generateMoves(List<Move> moves, ChessBoard board, char forPiece, int color) {

		int sourceSquare = BitTools.getFirstSetBitPos(board.getBitboard(forPiece)),
			oppositeColor = color ^ 1,
			targetName,
			targetSquare;

		long allMoves = attacksBySquare[sourceSquare] & ~board.getOccupancies(color);
		long definiteAttacks = allMoves & board.getOccupancies(oppositeColor);

		while (definiteAttacks != 0) {
			targetSquare = BitTools.getFirstSetBitPos(definiteAttacks);
			targetName = board.pieceAt(targetSquare);
			moves.add(
				Move.createCaptureMove(forPiece, (char) targetName, sourceSquare, targetSquare, false, Move.NULL_CHAR, board.castleRight)
			);
			definiteAttacks = BitTools.setBitOff(definiteAttacks, targetSquare);
			allMoves = BitTools.setBitOff(allMoves, targetSquare);
		}

		while (allMoves != 0) {
			targetSquare = BitTools.getFirstSetBitPos(allMoves);
			moves.add(
				new Move(forPiece, Move.NULL_CHAR, Move.NULL_CHAR, sourceSquare, targetSquare,
					false, false, false, false, board.castleRight)
			);
			allMoves = BitTools.setBitOff(allMoves, targetSquare);
		}

		if (board.canCastle(color, 0) == 1) {
			if (board.pieceAt(color == 0 ? 57 : 1) == 0 &&
					board.pieceAt(color == 0 ? 58 : 2) == 0 &&
					board.pieceAt(color == 0 ? 59 : 3) == 0) {
				moves.add(
					Move.createCastleMove(forPiece, sourceSquare, color == 0 ? 58 : 2, board.castleRight)
				);
			}
		}
		if (board.canCastle(color, 1) == 1) {
			if (board.pieceAt(color == 0 ? 61 : 5) == 0 &&
					board.pieceAt(color == 0 ? 62 : 6) == 0) {
				moves.add(
					Move.createCastleMove(forPiece, sourceSquare, color == 0 ? 62 : 6, board.castleRight)
				);
			}
		}

	}

	public static long[] attacksBySquare;
}
