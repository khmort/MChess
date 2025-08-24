package pieces;

import java.util.List;

import chessboard.ChessBoard;
import chessboard.Move;
import utils.BitTools;

public class Queen {

	public static long getAttacksOnFly(int square, long blocks) {
		return Rook.getAttacksOnFly(square, blocks & Rook.attacksBySquare[square])
				| Bishop.getAttacksOnFly(square, blocks & Bishop.attacksBySquare[square]);
	}

	public static void generateMoves(List<Move> moves, ChessBoard board, char forPiece, int color) {

		int sourceSquare,
			targetSquare,
			targetName;

		long queens = board.getBitboard(forPiece),
			allMoves,
			definiteAttacks;

		while (queens != 0) {

			sourceSquare = BitTools.getFirstSetBitPos(queens);
			queens = BitTools.setBitOff(queens, sourceSquare);

			allMoves = getAttacksOnFly(sourceSquare, board.getOccupancies(2)) & ~board.getOccupancies(color);
			definiteAttacks = allMoves & board.getOccupancies(color ^ 1);

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
}
