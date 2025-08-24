package chessboard;

import utils.BitTools;

public class SimpleChessBoard extends ChessBoard {

	public SimpleChessBoard(String fen) {
		super(fen);
	}

	public void doMove(Move m) {

		if (m.pieceName == 'R') {
			if (m.sourceSquare == 56) {
				castleRight = castleRight & 0b0111;
			} else if (m.sourceSquare == 63) {
				castleRight = castleRight & 0b1011;
			}
		} else if (m.pieceName == 'r') {
			if (m.sourceSquare == 0) {
				castleRight = castleRight & 0b1101;
			} else if (m.sourceSquare == 7) {
				castleRight = castleRight & 0b1110;
			}
		} else if (m.pieceName == 'K') {
			castleRight = castleRight & 0b0011;
		} else if (m.pieceName == 'k') {
			castleRight = castleRight & 0b1100;
		}


		if (m.isCaptureMove) {

			if (m.targetName == 'R') {
				if (m.targetSquare == 56) {
					castleRight = castleRight & 0b0111;
				} else if (m.sourceSquare == 63) {
					castleRight = castleRight & 0b1011;
				}
			} else if (m.pieceName == 'r') {
				if (m.sourceSquare == 2) {
					castleRight = castleRight & 0b1101;
				} else if (m.sourceSquare == 7) {
					castleRight = castleRight & 0b1110;
				}
			}

			if (m.isPromoteMove) {
				// تنظیم نگاشت خانه ها به نام قطعات
				squareToName[m.sourceSquare] = 0;
				squareToName[m.targetSquare] = m.promotedName;
				// تنظیم بورد ها
				transfer(m.pieceName, m.promotedName, m.sourceSquare, m.targetSquare);
				setBitboard(m.targetName, BitTools.setBitOff(getBitboard(m.targetName), m.targetSquare));
			} else {
				squareToName[m.sourceSquare] = 0;
				squareToName[m.targetSquare] = m.pieceName;
				transfer(m.pieceName, m.sourceSquare, m.targetSquare);
				setBitboard(m.targetName, BitTools.setBitOff(getBitboard(m.targetName), m.targetSquare));
			}

		} else if (m.isCastleMove) {
			squareToName[m.sourceSquare] = 0;
			transfer(m.pieceName, m.sourceSquare, m.targetSquare);
			// از بین بردن حق قلعه
			switch (m.targetSquare) {
				case 2:
					squareToName[3] = 'r';
					transfer('r', 0, 3);
					castleRight = castleRight & 0b1101;
					break;
				case 6:
					squareToName[5] = 'r';
					transfer('r', 7, 5);
					castleRight = castleRight & 0b1110;	
					break;
				case 58:
					squareToName[59] = 'R';
					transfer('r', 56, 59);
					castleRight = castleRight & 0b0111;	
					break;
				case 62:
					squareToName[61] = 'R';
					transfer('r', 63, 61);
					castleRight = castleRight & 0b1011;	
					break;
				default:
					break;
			}
		} else if (m.isPromoteMove) {
			squareToName[m.sourceSquare] = 0;
			squareToName[m.targetSquare] = m.promotedName;
			transfer(m.pieceName, m.promotedName, m.sourceSquare, m.targetSquare);
		} else {
			squareToName[m.sourceSquare] = 0;
			squareToName[m.targetSquare] = m.pieceName;
			transfer(m.pieceName, m.sourceSquare, m.targetSquare);
		}

		updateOccupancies();
	}

	public void undoMove(Move m) {
		if (m.isCaptureMove) {
			squareToName[m.targetSquare] = m.targetName;
			squareToName[m.sourceSquare] = m.pieceName;
			setBitboard(m.targetName, BitTools.setBitOn(getBitboard(m.targetName), m.targetSquare));
			transfer(m.pieceName, m.targetSquare, m.sourceSquare);
		} else if (m.isCastleMove) {
			squareToName[m.targetSquare] = 0;
			squareToName[m.sourceSquare] = m.pieceName;
			transfer(m.pieceName, m.targetSquare, m.sourceSquare);
			switch (m.targetSquare) {
				case 2:
					squareToName[3] = 0;
					squareToName[0] = 'r';
					transfer('r', 3, 0);
					break;
				case 6:
					squareToName[5] = 0;
					squareToName[7] = 'r';
					transfer('r', 5, 7);
					break;
				case 58:
					squareToName[59] = 0;
					squareToName[56] = 'R';
					transfer('R', 59, 56);
					break;
				case 62:
					squareToName[61] = 0;
					squareToName[63] = 'R';
					transfer('R', 61, 63);
					break;
				default:
					break;
			}
		} else {
			squareToName[m.targetSquare] = 0;
			squareToName[m.sourceSquare] = m.pieceName;
			transfer(m.pieceName, m.targetSquare, m.sourceSquare);
		}

		castleRight = m.castleRight;
		updateOccupancies();
	}

	protected void transfer(char fromPiece, char toPiece, int from, int to) {
		setBitboard(fromPiece, BitTools.setBitOff(fromPiece, from));
		setBitboard(toPiece, BitTools.setBitOn(toPiece, to));
	}

	protected void transfer(char pieceName, int from, int to) {
		setBitboard(pieceName, transfer(getBitboard(pieceName), from, to));
	}

	protected static long transfer(long board, int from, int to) {
		board = BitTools.setBitOff(board, from);
		board = BitTools.setBitOn(board, to);
		return board;
	}

	protected void updateWhiteOccupancies() {
		occupancies[0] = calcOccupancies(0);
		occupancies[2] = occupancies[0] | occupancies[1];
	}

	protected void updateBlackOccupancies() {
		occupancies[1] = calcOccupancies(1);
		occupancies[2] = occupancies[0] | occupancies[1];
	}

	protected final static char[][] COLOR_TO_MORTAL_PIECES = new char[][] { { 'P', 'N', 'B', 'R', 'Q' },
			{ 'p', 'n', 'b', 'r', 'q' } };
}
