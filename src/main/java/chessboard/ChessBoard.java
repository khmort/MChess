package chessboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import chessboard.function.AttackGeneratorFunction;
import chessboard.function.MovesGeneratorFunction;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Queen;
import pieces.Rook;
import utils.BitTools;

public class ChessBoard {

	protected int side;
	protected long[] nameToOrder;
	protected long[] occupancies;
	protected int castle;

	protected static AttackGeneratorFunction[] attacskGenerators = new AttackGeneratorFunction[115];
	protected static MovesGeneratorFunction[] movesGenerators = new MovesGeneratorFunction[115];
	static {

		// ========== PAWN ==========
		// white pawn
		attacskGenerators['P'] = new AttackGeneratorFunction() {
			@Override
			public long apply(long positions, long blocks) {
				return ((positions & BitTools.not_a_file) >>> 9) | ((positions & BitTools.not_h_file) >>> 7);
			}
		};

		// black pawn
		attacskGenerators['p'] = new AttackGeneratorFunction() {
			@Override
			public long apply(long positions, long blocks) {
				return ((positions & BitTools.not_a_file) << 7) | ((positions & BitTools.not_h_file) << 9);
			}
		};

		// ========== KING ==========
		// white king

		attacskGenerators['K'] = new AttackGeneratorFunction() {
			@Override
			public long apply(long positions, long blocks) {
				return King.attacksBySquare[BitTools.getFirstSetBitPos(positions)];
			}
		};
		// black king
		attacskGenerators['k'] = attacskGenerators['K'];

		// ========== QUEEN ==========
		// white queen
		attacskGenerators['Q'] = new AttackGeneratorFunction() {
			@Override
			public long apply(long positions, long blocks) {
				long attacks = 0L;
				int square;
				while (positions != 0) {
					square = BitTools.getFirstSetBitPos(positions);
					attacks |= Queen.getAttacksOnFly(square, blocks);
					positions = BitTools.setBitOff(positions, square);
				}
				return attacks;
			}
		};

		// black queen
		attacskGenerators['q'] = attacskGenerators['Q'];

		// ========== ROOK ==========
		// white rook
		attacskGenerators['R'] = new AttackGeneratorFunction() {

			@Override
			public long apply(long positions, long blocks) {
				long attacks = 0L;
				int square;
				while (positions != 0) {
					square = BitTools.getFirstSetBitPos(positions);
					attacks |= Rook.getAttacksOnFly(square, blocks);
					positions = BitTools.setBitOff(positions, square);
				}
				return attacks;
			}
		};

		// black rook
		attacskGenerators['r'] = attacskGenerators['R'];

		// ========== BISHOP ==========
		// white bishop
		attacskGenerators['B'] = new AttackGeneratorFunction() {
			@Override
			public long apply(long positions, long blocks) {
				long attacks = 0L;
				int square;
				while (positions != 0) {
					square = BitTools.getFirstSetBitPos(positions);
					attacks = attacks | Bishop.getAttacksOnFly(square, blocks);
					positions = BitTools.setBitOff(positions, square);
				}
				return attacks;
			}
		};

		// black bishop
		attacskGenerators['b'] = attacskGenerators['B'];

		// ========== KNIGHT ==========
		// white knight
		attacskGenerators['N'] = new AttackGeneratorFunction() {
			@Override
			public long apply(long positions, long blocks) {
				long attacks = 0L;
				int square;
				while (positions != 0) {
					square = BitTools.getFirstSetBitPos(positions);
					attacks |= Knight.attacksBySquare[square];
					positions = BitTools.setBitOff(positions, square);
				}
				return attacks;
			}
		};

		// black knight
		attacskGenerators['n'] = attacskGenerators['N'];
	}
	static {

		movesGenerators['K'] = new MovesGeneratorFunction() {
			@Override
			public long apply(int square, long[] occ) {
				return King.attacksBySquare[square] & ~occ[0];
			}
		};

		movesGenerators['Q'] = new MovesGeneratorFunction() {
			@Override
			public long apply(int square, long[] occ) {
				return Queen.getAttacksOnFly(square, occ[2]) & ~occ[0];
			}
		};

		movesGenerators['R'] = new MovesGeneratorFunction() {
			@Override
			public long apply(int square, long[] occ) {
				return Rook.getAttacksOnFly(square, occ[2]) & ~occ[0];
			}
		};

		movesGenerators['B'] = new MovesGeneratorFunction() {
			@Override
			public long apply(int square, long[] occ) {
				return Bishop.getAttacksOnFly(square, occ[2]) & ~occ[0];
			}
		};

		movesGenerators['N'] = new MovesGeneratorFunction() {
			@Override
			public long apply(int square, long[] occ) {
				return Knight.attacksBySquare[square] & ~occ[0];
			}
		};

		movesGenerators['k'] = new MovesGeneratorFunction() {
			@Override
			public long apply(int square, long[] occ) {
				return King.attacksBySquare[square] & ~occ[1];
			}
		};

		movesGenerators['q'] = new MovesGeneratorFunction() {
			@Override
			public long apply(int square, long[] occ) {
				return Queen.getAttacksOnFly(square, occ[2]) & ~occ[1];
			}
		};

		movesGenerators['r'] = new MovesGeneratorFunction() {
			@Override
			public long apply(int square, long[] occ) {
				return Rook.getAttacksOnFly(square, occ[2]) & ~occ[1];
			}
		};

		movesGenerators['b'] = new MovesGeneratorFunction() {
			@Override
			public long apply(int square, long[] occ) {
				return Bishop.getAttacksOnFly(square, occ[2]) & ~occ[1];
			}
		};

		movesGenerators['n'] = new MovesGeneratorFunction() {
			@Override
			public long apply(int square, long[] occ) {
				return Knight.attacksBySquare[square] & ~occ[1];
			}
		};
	}

	// =============================
	// ********* CONSTANTS *********
	// =============================
	public static final char[] NAMES = new char[] { 'K', 'Q', 'R', 'B', 'N', 'P', 'k', 'q', 'r', 'b', 'n', 'p' };
	public static final char[] UNICODES = new char[] { '♔', '♕', '♖', '♗', '♘', '♙', '♚', '♛', '♜', '♝', '♞', '♟' };
	public static final char[][] COLOR_TO_NAMES = new char[][] { { 'K', 'Q', 'R', 'B', 'N', 'P' },
			{ 'k', 'q', 'r', 'b', 'n', 'p' } };
	
	public static final char[][] COLOR_TO_RANDOM_NAMES = new char[][] {
		{'B', 'Q', 'N', 'R', 'P', 'K'},
		{'b', 'q', 'n', 'r', 'p', 'k'}
	};

	// this array can convert char-name ('P' 'Q' ...) to the side by doing
	// NAME_TO_COLOR['P'].
	public static final int[] NAME_TO_COLOR = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1 };
	// this array can convert char-name ('P' 'Q' ...) to the opposite color.
	public static final int[] NAME_TO_OPPOSITE_COLOR = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	protected static final int[] CASTLE_TABLE = new int[] { 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0,
			1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 1,
			0, 1, 1, 1, 1, 1, 1, 1 };

	public final static int WK_CASTLE = 2;
	public final static int WQ_CASTLE_OFF = ~WK_CASTLE;
	public final static int WQ_CASTLE = 1;
	public final static int WK_CASTLE_OFF = ~WQ_CASTLE;
	public final static int BK_CASTLE = 8;
	public final static int BK_CASTLE_OFF = ~BK_CASTLE;
	public final static int BQ_CASTLE = 4;
	public final static int BQ_CASTLE_OFF = ~BQ_CASTLE;
	public final static int WHITE = 0;
	public final static int BLACK = 1;

	protected ChessBoard(int side, long[] nameToOrder, long[] occupancies, int castle) {
		this.side = side;
		this.nameToOrder = nameToOrder;
		this.occupancies = occupancies;
		this.castle = castle;
	}

	public ChessBoard(String fen) {
		this();
		parseFEN(fen);
	}

	public ChessBoard() {
		side = 0;
		nameToOrder = new long[115];
		occupancies = new long[3];
		castle = 0;
		reset();
	}


	public void setSide(int side) {
		this.side = side;
	}

	public int getSide() {
		return side;
	}

	public int getOpposingSite() {
		return side ^ 1;
	}

	/**
	 * calculates all allowed moves (not including check) for the current `side`.
	 */
	public List<Move> generateMoves() {
		List<Move> allMoves = new ArrayList<>();
		for (char piece : COLOR_TO_NAMES[side]) {
			if (nameToOrder[piece] == 0)
				continue;
			allMoves.addAll(generateMoves(piece));
		}
		return allMoves;
	}

	/**
	 * calculates all legal moves (not including check) for `pieceName`.
	 */
	public List<Move> generateMoves(char pieceName) {

		List<Move> moves = new ArrayList<>();
		long bitboard = getBitboard(pieceName);
		int pos;
		int apos;
		long attacks;

		// =============================================================
		// ** White & black pawns bit-board will calculate Separately **
		// =============================================================

		if (pieceName == 'P') {

			while (bitboard != 0) {
				pos = BitTools.getFirstSetBitPos(bitboard);
				bitboard = BitTools.setBitOff(bitboard, pos);

				// quiet moves
				if (BitTools.getBit(occupancies[2], pos - 8) == 0) {
					if (Square.inRow(2, pos)) {
						moves.add(Move.createPromoteMove('P', pos, pos - 8, false, 'Q'));
						moves.add(Move.createPromoteMove('P', pos, pos - 8, false, 'N'));
					} else if (Square.inRow(7, pos)) {
						moves.add(new Move('P', pos, pos - 8));
						if (BitTools.getBit(occupancies[2], pos - 16) == 0) {
							moves.add(Move.createDoublePushMove('P', pos, pos - 16));
						}
					} else {
						moves.add(new Move('P', pos, pos - 8));
					}
				}

				// capture moves
				attacks = Pawn.getWhiteAttacks(pos) & occupancies[1] & ~occupancies[0];

				if (Square.inRow(2, pos)) {
					while (attacks != 0) {
						apos = BitTools.getFirstSetBitPos(attacks);
						attacks = BitTools.setBitOff(attacks, apos);
						moves.add(Move.createCaptureMove('P', pos, apos, 'Q'));
						moves.add(Move.createCaptureMove('P', pos, apos, 'N'));
					}
				} else {
					while (attacks != 0) {
						apos = BitTools.getFirstSetBitPos(attacks);
						attacks = BitTools.setBitOff(attacks, apos);
						moves.add(Move.createCaptureMove('P', pos, apos, Move.NULL_CHAR));
					}
				}
			}
		} else if (pieceName == 'p') {
			while (bitboard != 0) {
				pos = BitTools.getFirstSetBitPos(bitboard);
				bitboard = BitTools.setBitOff(bitboard, pos);

				// quiet moves
				if (BitTools.getBit(occupancies[2], pos + 8) == 0) {
					if (Square.inRow(7, pos)) {
						moves.add(Move.createPromoteMove('p', pos, pos + 8, false, 'q'));
						moves.add(Move.createPromoteMove('p', pos, pos + 8, false, 'n'));
					} else if (Square.inRow(2, pos)) {
						moves.add(new Move('p', pos, pos + 8));
						if (BitTools.getBit(occupancies[2], pos + 16) == 0) {
							moves.add(Move.createDoublePushMove('p', pos, pos + 16));
						}
					} else {
						moves.add(new Move('p', pos, pos + 8));
					}
				}

				// capture moves
				attacks = Pawn.getBlackAttacks(pos) & occupancies[0] & ~occupancies[1];

				if (Square.inRow(7, pos)) {
					while (attacks != 0) {
						apos = BitTools.getFirstSetBitPos(attacks);
						attacks = BitTools.setBitOff(attacks, apos);
						moves.add(Move.createCaptureMove('p', pos, apos, 'q'));
						moves.add(Move.createCaptureMove('p', pos, apos, 'n'));
					}
				} else {
					while (attacks != 0) {
						apos = BitTools.getFirstSetBitPos(attacks);
						attacks = BitTools.setBitOff(attacks, apos);
						moves.add(Move.createCaptureMove('p', pos, apos, Move.NULL_CHAR));
					}
				}
			}
		} else {

			long quiet, capture;

			// ====================
			// ****** Castle ******
			// ====================

			if (pieceName == 'K') {
				pos = BitTools.getFirstSetBitPos(bitboard);
				if (castlingRightBit(1) == 1 && (BitTools.wks_castle_occ & occupancies[2]) == 0
						&& !isBitboardOnFire(BitTools.wks_castle_check)) {
					moves.add(Move.createCastleMove(pieceName, pos, Square.g8));
				}
				if (castlingRightBit(0) == 1 && (BitTools.wqs_castle_occ & occupancies[2]) == 0
						&& !isBitboardOnFire(BitTools.wqs_castle_check)) {
					moves.add(Move.createCastleMove(pieceName, pos, Square.c8));
				}
			} else if (pieceName == 'k') {
				pos = BitTools.getFirstSetBitPos(bitboard);
				if (castlingRightBit(0) == 1 && (BitTools.bks_castle_occ & occupancies[2]) == 0
						&& !isBitboardOnFire(BitTools.bks_castle_check)) {
					moves.add(Move.createCastleMove(pieceName, pos, Square.g1));
				}
				if (castlingRightBit(1) == 1 && (BitTools.bqs_castle_occ & occupancies[2]) == 0
						&& !isBitboardOnFire(BitTools.bqs_castle_check)) {
					moves.add(Move.createCastleMove(pieceName, pos, Square.c1));
				}
			}

			while (bitboard != 0) {

				pos = BitTools.getFirstSetBitPos(bitboard);
				bitboard = BitTools.setBitOff(bitboard, pos);

				attacks = movesGenerators[pieceName].apply(pos, occupancies);

				// capture and quiet moves are calculated separately. It is easy to calculate a
				// capture move with an and (&) operator.
				// For example, let's say I can have an attack like this by the queen:
				//
				// a b c d e f g h
				//
				// 1 . . . 1 . . . .
				// 2 . . . 1 . . . .
				// 3 . . . 1 . 1 . .
				// 4 . . 1 1 1 . . .
				// 5 . . 1 . 1 . . .
				// 6 . . 1 1 1 . . .
				// 7 . 1 . 1 . . . .
				// 8 1 . . 1 . . . .
				//
				// and the occupancy of opposite color is like this:
				//
				// a b c d e f g h
				//
				// 1 1 . . . 1 . . .
				// 2 . 1 1 . . 1 1 1
				// 3 1 1 . . . 1 . .
				// 4 . . 1 . . . . .
				// 5 1 . 1 . 1 . . .
				// 6 . . . . 1 . . .
				// 7 . . . . . . . .
				// 8 . . . . . . . .
				//
				// If I AND these two bit-boards, the following result is obtained:
				//
				// a b c d e f g h
				//
				// 1 . . . . . . . .
				// 2 . . . . . . . .
				// 3 . . . . . 1 . .
				// 4 . . 1 . . . . .
				// 5 . . 1 . 1 . . .
				// 6 . . . . 1 . . .
				// 7 . . . . . . . .
				// 8 . . . . . . . .
				//
				// upper bit-board indicates capture attacks!

				// Based on the above description, we calculate capture and quiet.
				capture = attacks & occupancies[side ^ 1];
				quiet = attacks & ~capture;

				while (capture != 0) {
					apos = BitTools.getFirstSetBitPos(capture);
					capture = BitTools.setBitOff(capture, apos);
					moves.add(Move.createCaptureMove(pieceName, pos, apos, Move.NULL_CHAR));
				}

				while (quiet != 0) {
					apos = BitTools.getFirstSetBitPos(quiet);
					quiet = BitTools.setBitOff(quiet, apos);
					moves.add(new Move(pieceName, pos, apos));
				}
			}
		}

		return moves;
	}

	/**
	 * calculations attacks of `pieceName` in current chess-board. The Pawn Attack
	 * search algorithm is different from others. All pieces are shifted at the same
	 * time! This makes the search speed faster.
	 */
	public long calcAttacks(char pieceName) {
		return attacskGenerators[pieceName].apply(nameToOrder[pieceName], occupancies[2]);
	}

	/**
	 * sets the entire piece positions of `pieceName`.
	 * 
	 * @param bitboard `pieceName` positions as bitboard.
	 */
	public void setBitboard(char pieceName, long bitboard) {
		nameToOrder[pieceName] = bitboard;
	}

	public long getBitboard(char pieceName) {
		return nameToOrder[pieceName];
	}

	/**
	 * It calculates which squares are occupied by the given side color.
	 * 
	 * @param color white (0) or black (1)?
	 * @return a bitboard contains all the squares occupied by the given color.
	 */
	protected long calcOccupancies(int color) {
		long res = 0L;
		for (char c : COLOR_TO_NAMES[color]) {
			res |= nameToOrder[c];
		}
		return res;
	}

	/**
	 * The occupancies are not recalculated and the values ​​in the `occupancies`
	 * array are used.
	 * 
	 * @param color white (0) or black (1)?
	 */
	public long getOccupancies(int color) {
		return occupancies[color];
	}

	/**
	 * First, the occupied squares are calculated by the `calcOccupancies()`
	 * function, then they are stored in the `occupancies` array with the color
	 * index.
	 */
	public void updateOccupancies() {
		occupancies[0] = calcOccupancies(0);
		occupancies[1] = calcOccupancies(1);
		occupancies[2] = occupancies[0] | occupancies[1];
	}

	/**
	 * @return `true` if given square is attacked by opposite color, otherwise
	 *         `false`.
	 */
	public boolean isSquareOnFire(int square) {
		return isBitboardOnFire(BitTools.createBoard(square));
	}
		
	public boolean isBitboardOnFire(long bitboard) {
		for (char pieceName : COLOR_TO_RANDOM_NAMES[side ^ 1]) {
			if ((calcAttacks(pieceName) & bitboard) != 0)
				return true;
		}
		return false;
	}
	
	public boolean isBitboardOnFire(long bitboard, int color) {
		for (char pieceName : COLOR_TO_RANDOM_NAMES[color]) {
			if ((calcAttacks(pieceName) & bitboard) != 0)
				return true;
		}
		return false;
	}
	
	/**
	 * Has the current king side been attacked by the opposing side?
	 * @return `true` if king is check, otherwise `false`.
	 */
	public boolean isKingOnFire() {
		return isBitboardOnFire(getBitboard(side == 0 ? 'K' : 'k'));
	}

	/**
	 * This version of the function finds the answer by searching in a table.
	 * 
	 * @param alignment queen-side (0) or king-side (1)?
	 * @return 1 if current color have castling right in given side, otherwise 0.
	 */
	public int castlingRightTable(int alignment) {
		return CASTLE_TABLE[4 * castle + 2 * side + alignment];
	}

	public int castlingRightTable(int color, int alignment) {
		return CASTLE_TABLE[4 * castle + 2 * color + alignment];
	}

	/**
	 * This method works by checking the bit in the castle encoding field. I think
	 * this version should be faster than the table version. Maybe not!
	 * 
	 * @param alignment queen-side (0) or king-side (1)?
	 * @return 1 if current color have castling right in given side, otherwise 0.
	 */
	public long castlingRightBit(int alignment) {
		if (side == 0) {
			return BitTools.getBit(castle, alignment ^ 1);
		}
		return BitTools.getBit(castle, (alignment ^ 1) + 2);
	}

	public long castlingRightBit(int color, int alignment) {
		if (color == 0) {
			return BitTools.getBit(castle, alignment ^ 1);
		}
		return BitTools.getBit(castle, (alignment ^ 1) + 2);
	}

	/**
	 * sets the settings of the board based on a FEN string.
	 */
	public void parseFEN(String fen) {
		reset();

		// field-0: piece placement data
		// field-1: active color
		// field-2: castling availability
		String[] fields = fen.split(" ");

		// ========== Field 0 ==========
		String[] rows = fields[0].split("/");
		int pointer;
		for (int row = 0; row < 8; row++) {
			pointer = 0;
			for (char c : rows[row].toCharArray()) {
				if (c >= '0' && c <= '9') {
					pointer += (c - '0');
				} else {
					nameToOrder[c] = BitTools.setBitOn(nameToOrder[c], row * 8 + pointer);
					pointer++;
				}
			}
		}

		// ========== Field 1 ==========
		if (fields[1].equals("w")) {
			side = 0;
		} else {
			side = 1;
		}

		// ========== Field 2 ==========
		castle = 0;
		String castleStatus = fields[2];

		HashMap<Character, Integer> charToStatus = new HashMap<>(5);
		charToStatus.put('K', WK_CASTLE);
		charToStatus.put('Q', WQ_CASTLE);
		charToStatus.put('k', BK_CASTLE);
		charToStatus.put('q', BQ_CASTLE);
		charToStatus.put('-', 0);

		for (char c : castleStatus.toCharArray()) {
			castle |= charToStatus.get(c);
		}

		// update occupancies array
		updateOccupancies();
	}

	/**
	 * returns a fen string based on the pieces and status of the board.
	 */
	public String getFEN() {
		String fen = "";

		// ========== pieces data ==========
		int empty = 0;
		boolean isEmpty;
		for (int i = 0; i < 8; i++) {
			fen += "/";
			for (int j = 0; j < 8; j++) {
				isEmpty = true;
				for (int k = 0; k < 12; k++) {
					if (BitTools.getBit(nameToOrder[NAMES[k]], i * 8 + j) == 1) {
						if (empty != 0) {
							fen += String.valueOf(empty);
							empty = 0;
						}
						fen += NAMES[k];
						isEmpty = false;
						break;
					}
				}
				if (isEmpty) {
					empty++;
				}
			}
			if (empty != 0) {
				fen += String.valueOf(empty);
				empty = 0;
			}
		}
		fen = fen.substring(1);

		// ========== active side ==========
		fen += (" " + (side == 0 ? "w" : "b"));

		// ========== castle status ==========
		String castleStatus = "";

		// white & black castling rights
		long wkside = castlingRightBit(0, 1);
		long wqside = castlingRightBit(0, 0);
		long bkside = castlingRightBit(1, 1);
		long bqside = castlingRightBit(1, 0);

		if (wkside == 0 && wqside == 0 && bkside == 0 && bqside == 0) {
			castleStatus += "-";
		} else {
			if (wkside == 0 && wqside == 0) {
				castleStatus += "-";
			} else if (wkside == 1) {
				castleStatus += "K";
			} else {
				castleStatus += "Q";
			}
			if (bkside == 0 && bqside == 0) {
				castleStatus += "-";
			} else if (bkside == 1) {
				castleStatus += "k";
			} else {
				castleStatus += "q";
			}
		}

		fen += (" " + castleStatus);

		return fen;
	}

	/**
	 * make all pieces's bitboard zero!
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

	// Creates a clone of the current ChessBoard object
	public ChessBoard makeCopy() {
		return new ChessBoard(side, nameToOrder.clone(), occupancies.clone(), castle);
	}

	public void imbibe(ChessBoard cb) {
		side = cb.side;
		nameToOrder = cb.nameToOrder;
		occupancies = cb.occupancies;
		castle = cb.castle;
	}

	/**
	 * prints view of the chess board with Unicode pieces on the console.
	 */
	public void print() {
		System.out.println("    a b c d e f g h");
		System.out.println();
		boolean isEmpty;
		for (int i = 0; i < 8; i++) {
			System.out.print((i + 1) + "   ");
			for (int j = 0; j < 8; j++) {
				isEmpty = true;
				for (int k = 0; k < 12; k++) {
					if (BitTools.getBit(nameToOrder[NAMES[k]], i * 8 + j) == 1) {
						System.out.print(UNICODES[k] + " ");
						isEmpty = false;
						break;
					}
				}
				if (isEmpty) {
					System.out.print(". ");
				}
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("side: " + side + " (" + (side == 0 ? "white" : "black") + ")");
		System.out.println();
	}

	@Override
	public int hashCode() {
		int result = 17;  // عدد اولیه غیرصفر
		result = 31 * result + side;
		result = 31 * result + Arrays.hashCode(nameToOrder);
		result = 31 * result + Arrays.hashCode(occupancies);
		result = 31 * result + castle;
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		ChessBoard other = (ChessBoard) o;
		return side == other.side
			&& castle == other.castle
			&& Arrays.equals(nameToOrder, other.nameToOrder)
			&& Arrays.equals(occupancies, other.occupancies);
	}

}
