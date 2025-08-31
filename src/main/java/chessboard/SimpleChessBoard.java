package chessboard;

import chessboard.function.AttackGeneratorFunction;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Queen;
import pieces.Rook;

public class SimpleChessBoard extends ChessBoard {

	protected static AttackGeneratorFunction[] attacskGenerators = new AttackGeneratorFunction[115];
	public static void init() {

		//  /\ !!!!!! /\
		// |! |      |! |
		// |! | PAWN |! |
		// |__|      |__|
		// (__)!!!!!!(__)

		// white pawn
		attacskGenerators['P'] = new AttackGeneratorFunction() {
			@Override
			public long apply(long positions, long blocks) {
				return ((positions & 0xfefefefefefefefel) >>> 9) | ((positions & 0x7f7f7f7f7f7f7f7fl) >>> 7);
			}
		};
		// black pawn
		attacskGenerators['p'] = new AttackGeneratorFunction() {
			@Override
			public long apply(long positions, long blocks) {
				return ((positions & 0xfefefefefefefefel) << 7) | ((positions & 0x7f7f7f7f7f7f7f7fl) << 9);
			}
		};

		//  /\ !!!!!! /\
		// |! |      |! |
		// |! | KING |! |
		// |__|      |__|
		// (__)!!!!!!(__)

		// white king
		attacskGenerators['K'] = new AttackGeneratorFunction() {
			@Override
			public long apply(long positions, long blocks) {
				return King.attacksBySquare[Long.numberOfTrailingZeros(positions)];
			}
		};
		// black king
		attacskGenerators['k'] = attacskGenerators['K'];

		//  /\ !!!!!!! /\
		// |! |       |! |
		// |! | QUEEN |! |
		// |__|       |__|
		// (__)!!!!!!!(__)

		// white queen
		attacskGenerators['Q'] = new AttackGeneratorFunction() {
			@Override
			public long apply(long positions, long blocks) {
				long attacks = 0L;
				int square;
				while (positions != 0) {
					square = Long.numberOfTrailingZeros(positions);
					attacks |= Queen.getAttacksOnFly(square, blocks);
					positions &= positions - 1;
				}
				return attacks;
			}
		};
		// black queen
		attacskGenerators['q'] = attacskGenerators['Q'];

		//  /\ !!!!!! /\
		// |! |      |! |
		// |! | ROOK |! |
		// |__|      |__|
		// (__)!!!!!!(__)

		// white rook
		attacskGenerators['R'] = new AttackGeneratorFunction() {
			@Override
			public long apply(long positions, long blocks) {
				long attacks = 0L;
				int square;
				while (positions != 0) {
					square = Long.numberOfTrailingZeros(positions);
					attacks |= Rook.getAttacksOnFly(square, blocks);
					positions &= positions - 1;
				}
				return attacks;
			}
		};

		// black rook
		attacskGenerators['r'] = attacskGenerators['R'];

		//  /\ !!!!!!!! /\
		// |! |        |! |
		// |! | BISHOP |! |
		// |__|        |__|
		// (__)!!!!!!!!(__)

		// white bishop
		attacskGenerators['B'] = new AttackGeneratorFunction() {
			@Override
			public long apply(long positions, long blocks) {
				long attacks = 0L;
				int square;
				while (positions != 0) {
					square = Long.numberOfTrailingZeros(positions);
					attacks = attacks | Bishop.getAttacksOnFly(square, blocks);
					positions &= positions - 1;
				}
				return attacks;
			}
		};
		// black bishop
		attacskGenerators['b'] = attacskGenerators['B'];

		//  /\ !!!!!!!! /\
		// |! |        |! |
		// |! | KNIGHT |! |
		// |__|        |__|
		// (__)!!!!!!!!(__)

		// white knight
		attacskGenerators['N'] = new AttackGeneratorFunction() {
			@Override
			public long apply(long positions, long blocks) {
				long attacks = 0L;
				int square;
				while (positions != 0) {
					square = Long.numberOfTrailingZeros(positions);
					attacks |= Knight.attacksBySquare[square];
					positions &= positions - 1;
				}
				return attacks;
			}
		};
		// black knight
		attacskGenerators['n'] = attacskGenerators['N'];
	}
	static {init();}

	public SimpleChessBoard(String fen) {super(fen);}

	public void doMove(Move m) {
		
		removeCastlingRightBaseOnSource(m);


		if (m.isCaptureMove) {

			removeCastlingRightBaseOnTarget(m);

			if (m.isPromoteMove) {
				transfer(m.pieceName, m.promotedName, m.sourceSquare, m.targetSquare);
			} else {
				transfer(m.pieceName, m.sourceSquare, m.targetSquare);
			}

			nameToOrder[m.targetName] &= ~(1l << m.targetSquare);
			occupancies[NAME_TO_COLOR[m.targetName]] &= ~(1l << m.targetSquare);

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
		occupancies[2] = occupancies[0] | occupancies[1];
	}

	public void undoMove(Move m) {
		if (m.isCaptureMove) {

			if (m.isPromoteMove) {
				transfer(m.promotedName, m.pieceName, m.targetSquare, m.sourceSquare);
			} else {
				transfer(m.pieceName, m.targetSquare, m.sourceSquare);
			}

			nameToOrder[m.targetName] |= 1l << m.targetSquare;
			occupancies[NAME_TO_COLOR[m.targetName]] |= 1l << m.targetSquare;
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
		occupancies[2] = occupancies[0] | occupancies[1];
	}

	protected void removeCastlingRightBaseOnSource(Move m) {
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
	}

	protected void removeCastlingRightBaseOnTarget(Move m) {
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
	}

	protected void transfer(int fromPiece, int toPiece, int from, int to) {
		squareToName[from] = 0;
		squareToName[to] = toPiece;

		nameToOrder[fromPiece] &= ~(1l << from);
		nameToOrder[toPiece] |= 1l << to;

		// محاسبه occupencies برای به صورت زنده
		int color = NAME_TO_COLOR[fromPiece];
		occupancies[color] = occupancies[color] & ~(1l << from) | (1l << to);
	}

	protected void transfer(int pieceName, int from, int to) {
		squareToName[from] = 0;
		squareToName[to] = pieceName;

		nameToOrder[pieceName] = nameToOrder[pieceName] & ~(1l << from) | (1l << to);

		// محاسبه occupencies برای به صورت زنده
		int color = NAME_TO_COLOR[pieceName];
		occupancies[color] = occupancies[color] & ~(1l << from) | (1l << to);
	}

	public boolean hasConflict() {
		for (int i = 0; i < 64; i++) {
			int cc = 0;
			for (char name : NAMES) {
				long bb = getBitboard(name);
				if (((bb >>> i) & 1) == 1) {
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
	
}
