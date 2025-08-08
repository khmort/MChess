package chessboard;

import java.util.HashMap;

public class Square {

	public static String squareToName(int square) {
		if (square < 0 || square > 63)
			return "-";
		return SQUARES_NAME[square];
	}

	public static boolean inRow(int row, int square) {
		return square < row * 8 && square >= (row - 1) * 8;
	}
	
	public static int nameToSquare(String name) {
		return NAME_TO_SQUARE.get(name);
	}

	public final static int a1 = 0, b1 = 1, c1 = 2, d1 = 3, e1 = 4, f1 = 5, g1 = 6, h1 = 7, a2 = 8, b2 = 9, c2 = 10,
			d2 = 11, e2 = 12, f2 = 13, g2 = 14, h2 = 15, a3 = 16, b3 = 17, c3 = 18, d3 = 19, e3 = 20, f3 = 21, g3 = 22,
			h3 = 23, a4 = 24, b4 = 25, c4 = 26, d4 = 27, e4 = 28, f4 = 29, g4 = 30, h4 = 31, a5 = 32, b5 = 33, c5 = 34,
			d5 = 35, e5 = 36, f5 = 37, g5 = 38, h5 = 39, a6 = 40, b6 = 41, c6 = 42, d6 = 43, e6 = 44, f6 = 45, g6 = 46,
			h6 = 47, a7 = 48, b7 = 49, c7 = 50, d7 = 51, e7 = 52, f7 = 53, g7 = 54, h7 = 55, a8 = 56, b8 = 57, c8 = 58,
			d8 = 59, e8 = 60, f8 = 61, g8 = 62, h8 = 63;

	public static final String[] SQUARES_NAME = new String[] { "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "a2",
			"b2", "c2", "d2", "e2", "f2", "g2", "h2", "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "a4", "b4", "c4",
			"d4", "e4", "f4", "g4", "h4", "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "a6", "b6", "c6", "d6", "e6",
			"f6", "g6", "h6", "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", "a8", "b8", "c8", "d8", "e8", "f8", "g8",
			"h8" };
	
	public static HashMap<String, Integer> NAME_TO_SQUARE = new HashMap<String, Integer>(); 
	
	static {
		NAME_TO_SQUARE.put("a1", 0);
		NAME_TO_SQUARE.put("b1", 1);
		NAME_TO_SQUARE.put("c1", 2);
		NAME_TO_SQUARE.put("d1", 3);
		NAME_TO_SQUARE.put("e1", 4);
		NAME_TO_SQUARE.put("f1", 5);
		NAME_TO_SQUARE.put("g1", 6);
		NAME_TO_SQUARE.put("h1", 7);
		NAME_TO_SQUARE.put("a2", 8);
		NAME_TO_SQUARE.put("b2", 9);
		NAME_TO_SQUARE.put("c2", 10);
		NAME_TO_SQUARE.put("d2", 11);
		NAME_TO_SQUARE.put("e2", 12);
		NAME_TO_SQUARE.put("f2", 13);
		NAME_TO_SQUARE.put("g2", 14);
		NAME_TO_SQUARE.put("h2", 15);
		NAME_TO_SQUARE.put("a3", 16);
		NAME_TO_SQUARE.put("b3", 17);
		NAME_TO_SQUARE.put("c3", 18);
		NAME_TO_SQUARE.put("d3", 19);
		NAME_TO_SQUARE.put("e3", 20);
		NAME_TO_SQUARE.put("f3", 21);
		NAME_TO_SQUARE.put("g3", 22);
		NAME_TO_SQUARE.put("h3", 23);
		NAME_TO_SQUARE.put("a4", 24);
		NAME_TO_SQUARE.put("b4", 25);
		NAME_TO_SQUARE.put("c4", 26);
		NAME_TO_SQUARE.put("d4", 27);
		NAME_TO_SQUARE.put("e4", 28);
		NAME_TO_SQUARE.put("f4", 29);
		NAME_TO_SQUARE.put("g4", 30);
		NAME_TO_SQUARE.put("h4", 31);
		NAME_TO_SQUARE.put("a5", 32);
		NAME_TO_SQUARE.put("b5", 33);
		NAME_TO_SQUARE.put("c5", 34);
		NAME_TO_SQUARE.put("d5", 35);
		NAME_TO_SQUARE.put("e5", 36);
		NAME_TO_SQUARE.put("f5", 37);
		NAME_TO_SQUARE.put("g5", 38);
		NAME_TO_SQUARE.put("h5", 39);
		NAME_TO_SQUARE.put("a6", 40);
		NAME_TO_SQUARE.put("b6", 41);
		NAME_TO_SQUARE.put("c6", 42);
		NAME_TO_SQUARE.put("d6", 43);
		NAME_TO_SQUARE.put("e6", 44);
		NAME_TO_SQUARE.put("f6", 45);
		NAME_TO_SQUARE.put("g6", 46);
		NAME_TO_SQUARE.put("h6", 47);
		NAME_TO_SQUARE.put("a7", 48);
		NAME_TO_SQUARE.put("b7", 49);
		NAME_TO_SQUARE.put("c7", 50);
		NAME_TO_SQUARE.put("d7", 51);
		NAME_TO_SQUARE.put("e7", 52);
		NAME_TO_SQUARE.put("f7", 53);
		NAME_TO_SQUARE.put("g7", 54);
		NAME_TO_SQUARE.put("h7", 55);
		NAME_TO_SQUARE.put("a8", 56);
		NAME_TO_SQUARE.put("b8", 57);
		NAME_TO_SQUARE.put("c8", 58);
		NAME_TO_SQUARE.put("d8", 59);
		NAME_TO_SQUARE.put("e8", 60);
		NAME_TO_SQUARE.put("f8", 61);
		NAME_TO_SQUARE.put("g8", 62);
		NAME_TO_SQUARE.put("h8", 63);
	}
}
