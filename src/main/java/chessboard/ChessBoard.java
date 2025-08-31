package chessboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Queen;
import pieces.Rook;

public class ChessBoard {

	public static final char[] NAMES = new char[] { 'K', 'Q', 'R', 'B', 'N', 'P', 'k', 'q', 'r', 'b', 'n', 'p' };
	public static final char[] UNICODES = new char[] { '♔', '♕', '♖', '♗', '♘', '♙', '♚', '♛', '♜', '♝', '♞', '♟' };
	public static final char[][] COLOR_TO_NAMES = new char[][] {
		{'K', 'Q', 'R', 'B', 'N', 'P' },
		{ 'k', 'q', 'r', 'b', 'n', 'p' }};
	public static final char[][] COLOR_TO_RANDOM_NAMES = new char[][] {
		{'B', 'Q', 'N', 'R', 'P', 'K'},
		{'b', 'q', 'n', 'r', 'p', 'k'}
	};

	public static final int[] NAME_TO_COLOR = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1 };
	
	public static final int[] NAME_TO_OPPOSITE_COLOR = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	public int side;
	public long[] nameToOrder;
	public long[] occupancies;
	public int castleRight;
	public int[] squareToName;

	protected ChessBoard(int side, long[] nameToOrder, int[] squareToName, long[] occupancies, byte castleRight) {
		this.side = side;
		this.nameToOrder = nameToOrder;
		this.occupancies = occupancies;
		this.squareToName = squareToName;
		this.castleRight = castleRight;
	}

	public ChessBoard(String fen) {
		this();
		parseFEN(fen);
	}

	public ChessBoard() {
		side = 0;
		nameToOrder = new long[115];
		occupancies = new long[3];
		squareToName = new int[64];
		castleRight = 0;
		reset();
	}
	
	
	public List<Move> generateMoves() {
		
		List<Move> container = new ArrayList<>();
		
		if (side == 0) {
			Pawn.generateMoves(container, this, 'P', 0, 7, 2, -8, 'Q', 'N');
			Bishop.generateMoves(container, this, 'B', 0);
			Knight.generateMoves(container, this, 'N', 0);
			Queen.generateMoves(container, this, 'Q', 0);
			Rook.generateMoves(container, this, 'R', 0);
			King.generateMoves(container, this, 'K', 0, 58, 62, King.WHITE_QUEEN_SIDE_OCC,
			King.WHITE_KING_SIDE_OCC);
			} else {
				Pawn.generateMoves(container, this, 'p', 1, 2, 7, 8, 'q', 'n');
			Bishop.generateMoves(container, this, 'b', 1);
			Knight.generateMoves(container, this, 'n', 1);
			Queen.generateMoves(container, this, 'q', 1);
			Rook.generateMoves(container, this, 'r', 1);
			King.generateMoves(container, this, 'k', 1, 2, 6, King.BLACK_QUEEN_SIDE_OCC,
				King.BLACK_KING_SIDE_OCC);
		}
		
		return container;
	}
	
	public int getSide() {
		return side;
	}

	public int getOpposingSite() {
		return side ^ 1;
	}
	
	public void setSide(int side) {
		this.side = side;
	}

	public long getBitboard(int pieceName) {
		return nameToOrder[pieceName];
	}

	public void setBitboard(int pieceName, long bitboard) {
		nameToOrder[pieceName] = bitboard;
	}
	
	public long getOccupancies(int color) {
		return occupancies[color];
	}

	protected long calcOccupancies(int color) {
		long res = 0L;
		for (char c : COLOR_TO_NAMES[color]) {
			res |= nameToOrder[c];
		}
		return res;
	}
	
	public void updateOccupancies() {
		occupancies[0] = calcOccupancies(0);
		occupancies[1] = calcOccupancies(1);
		occupancies[2] = occupancies[0] | occupancies[1];
	}

	/**
	 * نام قطعه در خانه داده شده را بر می گرداند. اگر مهره ای وجود
	 * نداشته باشد صفر را بر می گرداند
	 */
	public int pieceAt(int square) {
		return squareToName[square];
	}

	/**
	 * آیا می توانید با مهره سفید (side=0) یا سیاه
	 * (side=1) در جهت وزیر (queenOrKingSide=0)
	 * یا در جهت شاه (queenOrKingSide=1) قلعه بروید
	 * 
	 * @param side if you are white: side=0 else side=1
	 * @param queenOrKingSide اگر می خواهید بدانید در جهت شاه می توانید قلعه بروید 1 و در جهت وزیر 0 را وارد کنید
	 * @return آیا می توانید با رنگ مهره و جهت داده شده قلعه کنید؟
	 */
	public int canCastle(int side, int queenOrKingSide) {
		return ((castleRight >>> (side * 2)) >>> queenOrKingSide) & 1;
	}

	/**
	 * بوسیله یک رشته FEN بورد را تنظیم کنید
	 * @param fen رشته FEN
	 */
	public void parseFEN(String fen) {
		reset();

		/*       _\|/_
				(o o)
		+----oOO-{_}-OOo----------------+
		|Field-0 -> Piece placement data|
		|Field-1 -> Active color        |
		|Field-2 -> Castling right      |
		+------------------------------*/
		String[] fields = fen.split(" ");

		/*---------*
		| Field-0 |
		*---------*/		
		String[] rows = fields[0].split("/");
		int pointer;
		for (int row = 0; row < 8; row++) {
			pointer = 0;
			for (char c : rows[row].toCharArray()) {
				if (c >= '0' && c <= '9') {
					pointer += (c - '0');
				} else {
					nameToOrder[c] |= 1l << (row * 8 + pointer);
					pointer++;
				}
			}
		}

		/*---------*
		 | Field-1 |
		 *---------*/
		if (fields[1].equals("w")) side = 0;
		else side = 1;

		/*---------*
		 | Field-2 |
		 *---------*/
		castleRight = 0;
		String castleStatus = fields[2];
		HashMap<Character, Integer> charToStatus = new HashMap<>(5);
		charToStatus.put('K', 0b0010);
		charToStatus.put('Q', 0b0001);
		charToStatus.put('k', 0b1000);
		charToStatus.put('q', 0b0100);
		charToStatus.put('-', 0);
		for (char c : castleStatus.toCharArray()) {
			castleRight = castleRight | charToStatus.get(c);
		}

		/*----------------------------*
		 | آپدیت آرایه square-to-name |
		 *----------------------------*/
		for (char pieceName : NAMES) {
			long board = getBitboard(pieceName);
			while (board != 0) {
				int pos = Long.numberOfTrailingZeros(board);
				squareToName[pos] = pieceName;
				board &= board - 1;
			}
		}

		/*-------------------------*
		 | آپدیت آرایه Occupencies |
		 *-------------------------*/
		updateOccupancies();
	}

	/**
	 * make all bitboard zero!
	 */
	public void reset() {
		nameToOrder['K'] = 0L;
		nameToOrder['Q'] = 0L;
		nameToOrder['R'] = 0L;
		nameToOrder['B'] = 0L;
		nameToOrder['N'] = 0L;
		nameToOrder['P'] = 0L;
		nameToOrder['k'] = 0L;
		nameToOrder['q'] = 0L;
		nameToOrder['r'] = 0L;
		nameToOrder['b'] = 0L;
		nameToOrder['n'] = 0L;
		nameToOrder['p'] = 0L;
	}

	public void print() {
		System.out.println("    a b c d e f g h    a b c d e f g h");
		int row = 1;
		System.out.print("\n" + row + "   ");
		for (int i = 0; i < 64; i++) {

			int pieceName = pieceAt(i);

			if (pieceName == 0) {
				System.out.print(". ");
			} else {
				System.out.print(getUnicode((char) pieceName) + " ");
			}

			if ((i + 1) % 8 == 0 && row <= 8) {

				// رسم بورد بر اساس بیت بورد ها
				System.out.print("   ");
				int j = (row - 1) * 8;
				char piece;
				while (j < row * 8) {
					piece = 0;
					for (char name : NAMES) {
						long bb = getBitboard(name);
						if (((bb >>> j) & 1) == 1) {
							piece = name;
						}
					}
					if (piece == 0) {
						System.out.print(". ");
					} else {
						System.out.print(getUnicode(piece) + " ");
					}
					j++;
				}

				row++;
				if (row <= 8)
					System.out.print("\n" + row + "   ");
			}
		}
		System.out.println("\n\n    side: " + side + " (" + (side == 0 ? "white" : "black") + ") castle: " +
							(castleRight & 1) + " " +
							((castleRight >>> 1) & 1) + " " +
							((castleRight >>> 2) & 1) + " " +
							((castleRight >>> 3) & 1));
	}

	public static char getUnicode(char pieceName) {
		int j = 0;
		while (j < NAMES.length) {
			if (NAMES[j] == pieceName) break;
			j++;
		}
		return UNICODES[j];
	}

}
