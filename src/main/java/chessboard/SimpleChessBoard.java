package chessboard;

import org.nd4j.linalg.cpu.nativecpu.bindings.Nd4jCpu.square;

import chessboard.function.AttackGeneratorFunction;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Queen;
import pieces.Rook;
import utils.BitTools;

public class SimpleChessBoard extends ChessBoard {

	public SimpleChessBoard(String fen) {
		super(fen);
	}

	public void doMove(Move m) {

		if (m.pieceName == 'R') {
			if (m.sourceSquare == 56) {
				castleRight = castleRight & 0b1110;
			} else if (m.sourceSquare == 63) {
				castleRight = castleRight & 0b1101;
			}
		} else if (m.pieceName == 'r') {
			if (m.sourceSquare == 0) {
				castleRight = castleRight & 0b1011;
			} else if (m.sourceSquare == 7) {
				castleRight = castleRight & 0b0111;
			}
		} else if (m.pieceName == 'K') {
			castleRight = castleRight & 0b1100;
		} else if (m.pieceName == 'k') {
			castleRight = castleRight & 0b0011;
		}


		if (m.isCaptureMove) {

			if (m.targetName == 'R') {
				if (m.targetSquare == 56) {
					castleRight = castleRight & 0b1110;
				} else if (m.targetSquare == 63) {
					castleRight = castleRight & 0b1101;
				}
			} else if (m.targetName == 'r') {
				if (m.targetSquare == 0) {
					castleRight = castleRight & 0b1011;
				} else if (m.targetSquare == 7) {
					castleRight = castleRight & 0b0111;
				}
			}

			if (m.isPromoteMove) {
				transfer(m.pieceName, m.promotedName, m.sourceSquare, m.targetSquare);
			} else {
				transfer(m.pieceName, m.sourceSquare, m.targetSquare);
			}

			nameToOrder[m.targetName] = BitTools.setBitOff(nameToOrder[m.targetName], m.targetSquare);

		} else if (m.isCastleMove) {
			transfer(m.pieceName, m.sourceSquare, m.targetSquare);
			// از بین بردن حق قلعه
			switch (m.targetSquare) {
				case 2:
					transfer('r', 0, 3);
					castleRight = castleRight & 0b0011;
					break;
				case 6:
					transfer('r', 7, 5);
					castleRight = castleRight & 0b0011;	
					break;
				case 58:
					transfer('R', 56, 59);
					castleRight = castleRight & 0b1100;	
					break;
				case 62:
					transfer('R', 63, 61);
					castleRight = castleRight & 0b1100;	
					break;
				default:
					break;
			}
		} else if (m.isPromoteMove) {
			transfer(m.pieceName, m.promotedName, m.sourceSquare, m.targetSquare);
		} else {
			transfer(m.pieceName, m.sourceSquare, m.targetSquare);
		}
		side ^= 1;
		updateOccupancies();
	}

	public void undoMove(Move m) {
		if (m.isCaptureMove) {
			if (m.isPromoteMove) {
				transfer(m.promotedName, m.pieceName, m.targetSquare, m.sourceSquare);
			} else {
				transfer(m.pieceName, m.targetSquare, m.sourceSquare);
			}
			setBitboard(m.targetName, BitTools.setBitOn(getBitboard(m.targetName), m.targetSquare));
			squareToName[m.targetSquare] = m.targetName;
		} else if (m.isCastleMove) {
			transfer(m.pieceName, m.targetSquare, m.sourceSquare);
			switch (m.targetSquare) {
				case 2:
					transfer('r', 3, 0);
					break;
				case 6:
					transfer('r', 5, 7);
					break;
				case 58:
					transfer('R', 59, 56);
					break;
				case 62:
					transfer('R', 61, 63);
					break;
				default:
					break;
			}
		} else if (m.isPromoteMove) {
			transfer(m.promotedName, m.pieceName, m.targetSquare, m.sourceSquare);
		} else {
			transfer(m.pieceName, m.targetSquare, m.sourceSquare);
		}

		castleRight = m.castleRight;
		side ^= 1;
		updateOccupancies();
	}

	protected void transfer(char fromPiece, char toPiece, int from, int to) {
		squareToName[from] = 0;
		squareToName[to] = toPiece;
		nameToOrder[fromPiece] = BitTools.setBitOff(nameToOrder[fromPiece], from);
		nameToOrder[toPiece] = BitTools.setBitOn(nameToOrder[toPiece], to);
	}

	protected void transfer(char pieceName, int from, int to) {
		squareToName[from] = 0;
		squareToName[to] = pieceName;
		nameToOrder[pieceName] = BitTools.setBitOff(nameToOrder[pieceName], from);
		nameToOrder[pieceName] = BitTools.setBitOn(nameToOrder[pieceName], to);
	}

	protected static long transfer(long board, int from, int to) {
		board = BitTools.setBitOff(board, from);
		board = BitTools.setBitOn(board, to);
		return board;
	}

	public boolean hasConflict() {
		for (int i = 0; i < 64; i++) {
			int cc = 0;
			for (char name : NAMES) {
				long bb = getBitboard(name);
				if (BitTools.getBit(bb, i) == 1) {
					cc++;
					if (cc > 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isWhiteKingOnFire() {
		long kingBoard = getBitboard('K');
		for (char oppositeName : COLOR_TO_NAMES[1]) {
			long attacks = attacskGenerators[oppositeName].apply(nameToOrder[oppositeName], occupancies[2]);
			if ((attacks & kingBoard) != 0)
				return true;
		}
		return false;
	}

	public boolean isBlackKingOnFire() {
		long kingBoard = getBitboard('k');
		for (char oppositeName : COLOR_TO_NAMES[0]) {
			long attacks = attacskGenerators[oppositeName].apply(nameToOrder[oppositeName], occupancies[2]);
			if ((attacks & kingBoard) != 0)
				return true;
		}
		return false;
	}

	protected final static char[][] COLOR_TO_MORTAL_PIECES = new char[][] { { 'P', 'N', 'B', 'R', 'Q' },
			{ 'p', 'n', 'b', 'r', 'q' } };
	
	// برای محاسبه سریع حملات ممکن توسط یک مهره
	// این توابع برای محاسبه کیش طراحی شده اند
	protected static AttackGeneratorFunction[] attacskGenerators = new AttackGeneratorFunction[115];
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
}
