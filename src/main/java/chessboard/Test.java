package chessboard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;

import engine.ChessEngine;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Queen;
import pieces.Rook;
import utils.BitTools;

public class Test {

	public static void prerequisites() throws FileNotFoundException, ClassNotFoundException, IOException {

		// load `attacks by square`
		Bishop.attacksBySquare = loadLongArray("/home/mortkh/Desktop/bishop/attacks_by_square.arr");
		Rook.attacksBySquare = loadLongArray("/home/mortkh/Desktop/rook/attacks_by_square.arr");
		King.attacksBySquare = loadLongArray("/home/mortkh/Desktop/king/attacks_by_square.arr");
		Knight.attacksBySquare = loadLongArray("/home/mortkh/Desktop/knight/attacks_by_square.arr");

		Pawn.attackBySquare = new long[2][64];
		Pawn.attackBySquare[0] = loadLongArray("/home/mortkh/Desktop/pawn/white/attacks_by_square.arr");
		Pawn.attackBySquare[1] = loadLongArray("/home/mortkh/Desktop/pawn/black/attacks_by_square.arr");

		// // load `pawn quiet moves`
		// Pawn.quietMoveBySquare = new long[2][64];
		// Pawn.quietMoveBySquare[0] =
		// loadLongArray("/home/mortkh/Desktop/pawn/white/quiet_moves_by_square.arr");
		// Pawn.quietMoveBySquare[1] =
		// loadLongArray("/home/mortkh/Desktop/pawn/black/quiet_moves_by_square.arr");

		// load `pawn moves count by square`
		// Pawn.movesCountBySquare = new int[2][64];
		// Pawn.movesCountBySquare[0] =
		// loadIntArray("/home/mortkh/Desktop/pawn/white/move_count_by_square.arr");
		// Pawn.movesCountBySquare[1] =
		// loadIntArray("/home/mortkh/Desktop/pawn/black/move_count_by_square.arr");

		// load `attacks count by square`
		Bishop.attacksCountBySquare = loadIntArray("/home/mortkh/Desktop/bishop/attacks_count_by_square.arr");
		Rook.attacksCountBySquare = loadIntArray("/home/mortkh/Desktop/rook/attacks_count_by_square.arr");

		// load `magic numbers`
		Bishop.magicNumbers = loadMagicNumbers("/home/mortkh/Desktop/bishop/bishop_magic_numbers.txt");
		Rook.magicNumbers = loadMagicNumbers("/home/mortkh/Desktop/rook/rook_magic_numbers.txt");

		// Pawn.quietMagicNumber = new long[2][64];
		// Pawn.quietMagicNumber[0] =
		// loadMagicNumbers("/home/mortkh/Desktop/pawn/white/magic_numbers.txt");
		// Pawn.quietMagicNumber[1] =
		// loadMagicNumbers("/home/mortkh/Desktop/pawn/black/magic_numbers.txt");

		// load `maps`
		Bishop.maps = loadMap("/home/mortkh/Desktop/bishop/magic maps");
		Rook.maps = loadMap("/home/mortkh/Desktop/rook/magic maps");

		// Pawn.quietMaps = new long[2][][];
		// Pawn.quietMaps[0] = loadMap("/home/mortkh/Desktop/pawn/white/magic maps");
		// Pawn.quietMaps[1] = loadMap("/home/mortkh/Desktop/pawn/black/magic maps");

	}

	public static int[] loadIntArray(String pathname)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(pathname)));
		int[] arr = (int[]) ois.readObject();
		ois.close();
		return arr;
	}

	public static long[] loadLongArray(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		long[] arr = (long[]) ois.readObject();
		ois.close();
		return arr;
	}

	public static long[] loadLongArray(String pathname)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		return loadLongArray(new File(pathname));
	}

	public static long[][] loadMap(String directory) throws FileNotFoundException, ClassNotFoundException, IOException {
		File[] files = new File(directory).listFiles();
		long[][] maps = new long[64][];
		for (File file : files) {
			if (!file.getName().endsWith(".map"))
				continue;
			maps[getMapIndex(file)] = loadLongArray(file);
		}
		return maps;
	}

	public static int getMapIndex(File file) {
		String name = file.getName();
		name = name.substring(name.lastIndexOf('_') + 1, name.lastIndexOf('.'));
		return Integer.parseInt(name);
	}

	public static long[] loadMagicNumbers(String pathname) throws IOException {
		long[] magicNumbers = new long[64];
		BufferedReader br = new BufferedReader(new FileReader(new File(pathname)));
		String line;
		String[] keyValue;
		while ((line = br.readLine()) != null) {
			keyValue = line.split(" ");
			magicNumbers[Integer.parseInt(keyValue[0])] = Long.parseLong(keyValue[1]);
		}
		br.close();
		return magicNumbers;
	}

	public static void main(String[] args)
			throws FileNotFoundException, ClassNotFoundException, IOException, InterruptedException {
		
//		prerequisites();
//		
//		SimpleChessBoard chessboard = new SimpleChessBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq e3 0 1");
//		List<Move> moves = chessboard.generateMoves();
//		
//		long time = System.nanoTime();
//		for (int i = 0; i < 100_000_000; i++) {
//			Collections.shuffle(moves);
//		}
//		printTime(time);
//		
//		time = System.nanoTime();
//		for (int i = 0; i < 100_000_000; i++) {
//			moves = ChessEngine.shuffleMovesList(moves);
//		}
//		printTime(time);
		
	}
	
	public static void printTime(long statrtNanoTime) {
		double ms = (System.nanoTime() - statrtNanoTime) / 10_000_000;
		System.out.print("duration: ");
		if (ms > 100.0) {
			double sec = ms / 100.0;
			if (sec > 60.0) {
				double min = sec / 60.0;
				System.out.format("%s min & %.2f sec\n", (int) min, sec % 60.0);
				return;
			}
			System.out.format("%s sec & %.1f milisec\n", (int) sec, ms % 100.0);
			return;
		}
		System.out.format("%.1f mili-seconds\n", ms);
	}
}