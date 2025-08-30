package pieces;

import java.util.List;

import chessboard.ChessBoard;
import chessboard.Move;

public class Queen {

	public static long getAttacksOnFly(int square, long blocks) {
		return Rook.getAttacksOnFly(square, blocks & Rook.attacksBySquare[square])
				| Bishop.getAttacksOnFly(square, blocks & Bishop.attacksBySquare[square]);
	}

	public static void generateMoves(List<Move> moves, ChessBoard board, int forPiece, int color) {

		int sourceSquare;
		int targetSquare;
		int targetName;
		long queens = board.getBitboard(forPiece);
		long allMoves;
		long definiteAttacks;
		long oppositeOcc = board.getOccupancies(color ^ 1);
		long complementOcc = ~board.getOccupancies(color);

		while (queens != 0) {

			sourceSquare = Long.numberOfTrailingZeros(queens);
			queens &= queens - 1;

			allMoves = getAttacksOnFly(sourceSquare, board.getOccupancies(2)) & complementOcc;
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

	public static void printBitboard(long bitboard) {
		int bit;
		for (int i=0; i<64; i++) {
			bit = (int) (bitboard & 1);
			bitboard = bitboard >>> 1;
			if (i % 8 == 0) {
				System.out.println();
			}
			System.out.print(bit == 0 ? ". " : "1 ");
		}
		System.out.println("\n");
	}

}
