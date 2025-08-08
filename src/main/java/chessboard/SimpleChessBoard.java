package chessboard;

import utils.BitTools;

public class SimpleChessBoard extends ChessBoard {

	protected SimpleChessBoard(int side, long[] nameToOrder, long[] occupancies, int castle) {
		this.side = side;
		this.nameToOrder = nameToOrder;
		this.occupancies = occupancies;
		this.castle = castle;
	}

	public SimpleChessBoard(String fen) {
		super(fen);
	}

	public void doWhiteMove(Move m) {

		if (m.isCaptureMove) {
			// make position (bit) of source-square to 0 in bit-board.
			setBitboard(m.pieceName, BitTools.setBitOff(nameToOrder[m.pieceName], m.sourceSquare));
			for (char name : COLOR_TO_MORTAL_PIECES[1]) {
				if (BitTools.getBit(nameToOrder[name], m.targetSquare) == 1) {
					// make target position (bit) of target-square in target bit-board.
					setBitboard(name, BitTools.setBitOff(nameToOrder[name], m.targetSquare));
					break;
				}
			}
			if (m.pieceName == 'R') {
				if (m.sourceSquare == Square.h8) {
					castle = castle & ChessBoard.WK_CASTLE_OFF;
				} else {
					castle = castle & ChessBoard.WQ_CASTLE_OFF;
				}
			}
			if (m.promotedName == Move.NULL_CHAR) {
				if (m.pieceName == 'K') {
					castle = castle & ChessBoard.WK_CASTLE_OFF & ChessBoard.WQ_CASTLE_OFF;
				}
				setBitboard(m.pieceName, BitTools.setBitOn(nameToOrder[m.pieceName], m.targetSquare));
			} else {
				setBitboard(m.promotedName, BitTools.setBitOn(nameToOrder[m.promotedName], m.targetSquare));
			}

			// update all occupancies!
			updateOccupancies();

		} else if (m.isCastleMove) {
			switch (m.targetSquare) {
			case Square.g8:
				setBitboard('R', BitTools.setBitOff(getBitboard('R'), Square.h8));
				setBitboard('R', BitTools.setBitOn(getBitboard('R'), Square.f8));
				setBitboard('K', BitTools.K_oo);
				castle &= ChessBoard.WK_CASTLE_OFF;
				break;
			case Square.c8:
				setBitboard('R', BitTools.setBitOff(getBitboard('R'), Square.a8));
				setBitboard('R', BitTools.setBitOn(getBitboard('R'), Square.d8));
				setBitboard('K', BitTools.K_ooo);
				castle &= ChessBoard.WQ_CASTLE_OFF;
				break;
			default:
				break;
			}

			// update just white occupancies!
			updateWhiteOccupancies();

		} else {
			// make move
			setBitboard(m.pieceName, BitTools.setBitOff(nameToOrder[m.pieceName], m.sourceSquare));
			
			if (m.pieceName == 'R') {
				if (m.sourceSquare == Square.h8) {
					castle = castle & ChessBoard.WK_CASTLE_OFF;
				} else {
					castle = castle & ChessBoard.WQ_CASTLE_OFF;
				}
			}
			if (m.promotedName == Move.NULL_CHAR) {
				if (m.pieceName == 'K') {
					castle = castle & ChessBoard.WK_CASTLE_OFF & ChessBoard.WQ_CASTLE_OFF;
				}
				setBitboard(m.pieceName, BitTools.setBitOn(nameToOrder[m.pieceName], m.targetSquare));
			} else {
				setBitboard(m.promotedName, BitTools.setBitOn(nameToOrder[m.promotedName], m.targetSquare));
			}

			// update just white occupancies!
			updateWhiteOccupancies();
		}

		// switch side to black (1)
		side = 1;
	}

	public void doBlackMove(Move m) {
		
		if (m.isCaptureMove) {
			setBitboard(m.pieceName, BitTools.setBitOff(nameToOrder[m.pieceName], m.sourceSquare));
			for (char name : COLOR_TO_MORTAL_PIECES[0]) {
				if (BitTools.getBit(nameToOrder[name], m.targetSquare) == 1) {
					setBitboard(name, BitTools.setBitOff(nameToOrder[name], m.targetSquare));
					break;
				}
			}
			if (m.pieceName == 'r') {
				if (m.sourceSquare == Square.h1) {
					castle = castle & ChessBoard.BK_CASTLE_OFF;
				} else {
					castle = castle & ChessBoard.BQ_CASTLE_OFF;
				}
			}
			if (m.promotedName == Move.NULL_CHAR) {
				if (m.pieceName == 'k') {
					castle = castle & ChessBoard.BK_CASTLE_OFF & ChessBoard.BQ_CASTLE_OFF;
				}
				setBitboard(m.pieceName, BitTools.setBitOn(nameToOrder[m.pieceName], m.targetSquare));
			} else {
				setBitboard(m.promotedName, BitTools.setBitOn(nameToOrder[m.promotedName], m.targetSquare));
			}

			// update all occupancies!
			updateOccupancies();

		} else if (m.isCastleMove) {
			switch (m.targetSquare) {
			case Square.g1:
				setBitboard('r', BitTools.setBitOff(getBitboard('r'), Square.h1));
				setBitboard('r', BitTools.setBitOn(getBitboard('r'), Square.f1));
				setBitboard('k', BitTools.k_oo);
				castle &= ChessBoard.BK_CASTLE_OFF;
				break;
			case Square.c1:
				setBitboard('r', BitTools.setBitOff(getBitboard('r'), Square.a1));
				setBitboard('r', BitTools.setBitOn(getBitboard('r'), Square.d1));
				setBitboard('k', BitTools.k_ooo);
				castle &= ChessBoard.BQ_CASTLE_OFF;
				break;
			default:
				break;
			}

			// update just black occupancies!
			updateBlackOccupancies();
		} else {
			setBitboard(m.pieceName, BitTools.setBitOff(nameToOrder[m.pieceName], m.sourceSquare));
			if (m.pieceName == 'r') {
				if (m.sourceSquare == Square.h1) {
					castle = castle & ChessBoard.BK_CASTLE_OFF;
				} else {
					castle = castle & ChessBoard.BQ_CASTLE_OFF;
				}
			}
			if (m.promotedName == Move.NULL_CHAR) {
				if (m.pieceName == 'k') {
					castle = castle & ChessBoard.BK_CASTLE_OFF & ChessBoard.BQ_CASTLE_OFF;
				}
				setBitboard(m.pieceName, BitTools.setBitOn(nameToOrder[m.pieceName], m.targetSquare));
			} else {
				setBitboard(m.promotedName, BitTools.setBitOn(nameToOrder[m.promotedName], m.targetSquare));
			}
			// update just black occupancies!
			updateBlackOccupancies();
		}
		
		side = 0;
	}

	protected void updateWhiteOccupancies() {
		occupancies[0] = calcOccupancies(0);
		occupancies[2] = occupancies[0] | occupancies[1];
	}

	protected void updateBlackOccupancies() {
		occupancies[1] = calcOccupancies(1);
		occupancies[2] = occupancies[0] | occupancies[1];
	}

	public boolean isWhiteKingOnFire() {
		return isBitboardOnFire(nameToOrder['K'], 1);
	}

	public boolean isBlackKingOnFire() {
		return isBitboardOnFire(nameToOrder['k'], 0);
	}

	public SimpleChessBoard makeCopy() {
		return new SimpleChessBoard(side, nameToOrder.clone(), occupancies.clone(), castle);
	}

	protected final static char[][] COLOR_TO_MORTAL_PIECES = new char[][] { { 'P', 'N', 'B', 'R', 'Q' },
			{ 'p', 'n', 'b', 'r', 'q' } };
}
