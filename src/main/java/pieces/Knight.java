package pieces;

import java.util.List;

import chessboard.ChessBoard;
import chessboard.Move;
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

	public static void generateMoves(List<Move> moves, ChessBoard board, char forPiece, int color) {

		int oppositeColor = color ^ 1,
			sourceSquare,
			targetName,
			targetSquare;
		
		long knights = board.getBitboard(forPiece),
				allMoves,
				definiteAttacks;
		
		while (knights != 0) {
			sourceSquare = BitTools.getFirstSetBitPos(knights);
			knights = BitTools.setBitOff(knights, sourceSquare);
			
			allMoves = attacksBySquare[sourceSquare] & ~board.getOccupancies(color);
			definiteAttacks = allMoves & board.getOccupancies(oppositeColor);

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
		}

	}

	public static long[] attacksBySquare;
}
