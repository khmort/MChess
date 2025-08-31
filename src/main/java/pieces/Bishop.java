package pieces;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.nd4j.shade.guava.io.Files;

import chessboard.ChessBoard;
import chessboard.Move;
import chessboard.function.MagicNumberFactory;

public class Bishop {

	public static void init() throws NumberFormatException, IOException, ClassNotFoundException {
		// load map and magic numbers
		magicNumbers = new long[64];
		maps = new Long[64][];
		// edit `folder` base on your local path
		String folder = "/home/khmort/Programming/JAVA projects/MChess/src/main/resources/magic numbers";
		File parent = new File(folder + "/bishop");
		for (File f : parent.listFiles()) {
			String name = f.getName();
			String pure = name.substring(0, name.indexOf('.'));
			int square = Integer.parseInt(pure);
			if (name.endsWith(".txt")) {
				magicNumbers[square] = Long.parseLong(Files.readLines(f, StandardCharsets.UTF_8).get(0));
			} else if (name.endsWith(".map")) {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
				maps[square] = (Long[]) ois.readObject();
				ois.close();
			}
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