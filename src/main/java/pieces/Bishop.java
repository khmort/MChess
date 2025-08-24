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
import chessboard.function.MagicNumbers;
import utils.BitTools;

public class Bishop {

	public static void init() throws NumberFormatException, IOException, ClassNotFoundException {
		// load map and magic numbers
		magicNumbers = new long[64];
		maps = new Long[64][];
		String folder = "/home/khmort/Programming/JAVA projects/MChess/magic numbers/bishop";
		File parent = new File(folder);
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
			attacksBySquare[i] = MagicNumbers.removeBorder(i, MagicNumbers.getBishopRawMoves(i, 0L));
			attacksCountBySquare[i] = BitTools.bitCount(attacksBySquare[i]);
		}
	}

	public static long getAttacksOnFly(int square, long blocks) {
		return maps[square][hash(blocks & attacksBySquare[square], magicNumbers[square], attacksCountBySquare[square])];
	}

	public static int hash(long blocks, long magicNumber, int bits) {
		return (int) ((magicNumber * blocks) >>> (64 - bits));
	}

	public static void generateMoves(List<Move> moves, ChessBoard board, char forPiece, int color) {

		int sourceSquare,
			targetSquare,
			targetName;

		long bishops = board.getBitboard(forPiece),
			allMoves,
			definiteAttacks;

		while (bishops != 0) {

			sourceSquare = BitTools.getFirstSetBitPos(bishops);
			bishops = BitTools.setBitOff(bishops, sourceSquare);

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

	public static int[] attacksCountBySquare;
	public static Long[] attacksBySquare;
	public static long[] magicNumbers;
	public static Long[][] maps;

}