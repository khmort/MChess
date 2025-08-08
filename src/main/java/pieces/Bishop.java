package pieces;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;

import org.nd4j.shade.guava.io.Files;

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

	public static long getMaskAttacks(int square) {
		int y = square / 8, x = square % 8, x1 = x, y1 = y;
		long attacks = 0L;
		// up-right
		while (x1 < 8 && y1 > -1) {
			attacks = BitTools.setBitOn(attacks, y1 * 8 + x1);
			x1++;
			y1--;
		}
		// up-left
		x1 = x;
		y1 = y;
		while (x1 > -1 && y1 > -1) {
			attacks = BitTools.setBitOn(attacks, y1 * 8 + x1);
			x1--;
			y1--;
		}
		// down-right
		x1 = x;
		y1 = y;
		while (x1 < 8 && y1 < 8) {
			attacks = BitTools.setBitOn(attacks, y1 * 8 + x1);
			x1++;
			y1++;
		}
		// down-left
		x1 = x;
		y1 = y;
		while (x1 > -1 && y1 < 8) {
			attacks = BitTools.setBitOn(attacks, y1 * 8 + x1);
			x1--;
			y1++;
		}

		attacks = BitTools.setBitOff(attacks, square);

		return attacks;
	}

	public static int hash(long blocks, long magicNumber, int bits) {
		return (int) ((magicNumber * blocks) >>> (64 - bits));
	}

	public static int[] attacksCountBySquare;
	public static Long[] attacksBySquare;
	public static long[] magicNumbers;
	public static Long[][] maps;

}