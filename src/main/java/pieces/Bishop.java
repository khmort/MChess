package pieces;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import chessboard.ChessBoard;
import chessboard.Move;
import chessboard.function.MagicNumberFactory;

public class Bishop {

	public static void init() throws NumberFormatException, IOException, ClassNotFoundException {
		// load map and magic numbers
		magicNumbers = new long[64];
		maps = new Long[64][];

		for (int i=0; i<64; i++) {
			InputStream numStream = Bishop.class.getClassLoader().getResourceAsStream("magic numbers/bishop/" + i + ".txt");
			InputStream mapStream = Bishop.class.getClassLoader().getResourceAsStream("magic numbers/bishop/" + i + ".map");
			magicNumbers[i] = Long.parseLong(new String(numStream.readAllBytes(), StandardCharsets.UTF_8));
			numStream.close();
			ObjectInputStream ois = new ObjectInputStream(mapStream);
			maps[i] = (Long[]) ois.readObject();
			ois.close();
			mapStream.close();
		}
		
		attacksBySquare = new Long[64];
		attacksCountBySquare = new int[64];
		for (int i = 0; i < 64; i++) {
			attacksBySquare[i] = MagicNumberFactory.removeBorder(i, MagicNumberFactory.getBishopRawMoves(i, 0L));
			attacksCountBySquare[i] = Long.bitCount(attacksBySquare[i]);
		}
	}

	public static long getAttacksOnFly(int square, long blocks) {
		return maps[square][hash(blocks & attacksBySquare[square], magicNumbers[square], attacksCountBySquare[square])];
	}

	public static int hash(long blocks, long magicNumber, int bits) {
		return (int) ((magicNumber * blocks) >>> (64 - bits));
	}

	public static void generateMoves(List<Move> moves, ChessBoard board, int forPiece, int color) {

		int sourceSquare;
		int targetSquare;
		int targetName;
		long bishops = board.getBitboard(forPiece);
		long allMoves;
		long definiteAttacks;
		long oppositeOcc = board.getOccupancies(color ^ 1);
		long complementOcc = ~board.getOccupancies(color);

		while (bishops != 0) {

			sourceSquare = Long.numberOfTrailingZeros(bishops);
			bishops &= bishops - 1;

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

	public static int[] attacksCountBySquare;
	public static Long[] attacksBySquare;
	public static long[] magicNumbers;
	public static Long[][] maps;

}