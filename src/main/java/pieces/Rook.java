package pieces;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import chessboard.ChessBoard;
import chessboard.Move;
import chessboard.function.MagicNumberFactory;

public class Rook {

	public static void init() throws NumberFormatException, IOException, ClassNotFoundException {
		// load map and magic numbers
		magicNumbers = new long[64];
		maps = new Long[64][];

		for (int i=0; i<64; i++) {
			InputStream numStream = Rook.class.getClassLoader().getResourceAsStream("magic numbers/rook/" + i + ".txt");
			InputStream mapStream = Rook.class.getClassLoader().getResourceAsStream("magic numbers/rook/" + i + ".map");
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
			attacksBySquare[i] = MagicNumberFactory.removeBorder(i, MagicNumberFactory.getRawMoves(i, 0L));
			attacksCountBySquare[i] = Long.bitCount(attacksBySquare[i]);
		}

		for (int i=0; i<64; i++) {
			InputStream numStream = Rook.class.getClassLoader().getResourceAsStream("magic numbers/rook/" + i + ".txt");
			InputStream mapStream = Rook.class.getClassLoader().getResourceAsStream("magic numbers/rook/" + i + ".map");
			magicNumbers[i] = Long.parseLong(new String(numStream.readAllBytes(), StandardCharsets.UTF_8));
			numStream.close();

			ObjectInputStream ois = new ObjectInputStream(mapStream);
			maps[i] = (Long[]) ois.readObject();
			ois.close();
			mapStream.close();
		}
	}

	public static long getAttacksOnFly(int square, long blocks) {
		return maps[square][Bishop.hash(blocks & attacksBySquare[square], magicNumbers[square], attacksCountBySquare[square])];
	}

	public static void generateMoves(List<Move> moves, ChessBoard board, int forPiece, int color) {

		int sourceSquare;
		int targetSquare;
		int targetName;
		long rooks = board.getBitboard(forPiece);
		long allMoves;
		long definiteAttacks;
		long oppositeOcc = board.getOccupancies(color ^ 1);
		long complementOcc = ~board.getOccupancies(color);

		while (rooks != 0) {

			sourceSquare = Long.numberOfTrailingZeros(rooks);
			rooks &= rooks - 1;

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

	public static Long[] attacksBySquare;
	public static int[] attacksCountBySquare;
	public static long[] magicNumbers;
	public static Long[][] maps;

}