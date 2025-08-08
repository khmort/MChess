package chessboard;

import java.util.List;

import utils.BitTools;
import utils.textural_table.TextTable;

public class Move {

	public Move(char pieceName, int sourceSquare, int targetSquare, boolean isCaptureMove, boolean isDoublePush,
			boolean isCastleMove, char promotedName) {
		this.pieceName = pieceName;
		this.sourceSquare = sourceSquare;
		this.targetSquare = targetSquare;
		this.isCaptureMove = isCaptureMove;
		this.isDoublePush = isDoublePush;
		this.isCastleMove = isCaptureMove;
		this.promotedName = promotedName;
	}

	protected Move(char pieceName, int sourceSquare, int targetSquare, boolean isCaptureMove, char promotedName) {
		this.pieceName = pieceName;
		this.sourceSquare = sourceSquare;
		this.targetSquare = targetSquare;
		this.isCaptureMove = isCaptureMove;
		this.promotedName = promotedName;
	}

	public Move(char pieceName, int sourceSquare, int targetSquare) {
		this.pieceName = pieceName;
		this.sourceSquare = sourceSquare;
		this.targetSquare = targetSquare;
	}

	protected Move() {
	}

	public static Move parseString(String move, ChessBoard cb) {

		if (move.equals("o-o")) {
			return createCastleMove(cb.getSide() == 0 ? 'K' : 'k', cb.getSide() == 0 ? Square.e8 : Square.e1,
					cb.getSide() == 0 ? Square.g8 : Square.g1);
		}

		if (move.equals("o-o-o")) {
			return createCastleMove(cb.getSide() == 0 ? 'K' : 'k', cb.getSide() == 0 ? Square.e8 : Square.e1,
					cb.getSide() == 0 ? Square.c8 : Square.c1);
		}
		
		int source = Square.nameToSquare(move.substring(0, 2));
		int target;
		char name = '-';
		
		for (char piece : ChessBoard.NAMES) {
			if (BitTools.getBit(cb.getBitboard(piece), source) == 1) {
				name = piece;
				break;
			}
		}

		if (move.charAt(2) == 'x') {
			target = Square.nameToSquare(move.substring(3, 5));
			
			if (move.length() == 6) {
				return createPromoteMove(name, source, target, true, move.charAt(5));
			} else {
				return createCaptureMove(name, source, target, NULL_CHAR);
			}
			
		} else {
			target = Square.nameToSquare(move.substring(2, 4));
			
			if (move.length() == 5) {
				return createPromoteMove(name, source, target, false, move.charAt(4));
			} else {
				return new Move(name, source, target);
			}
			
		}
	}

	@Override
	public String toString() {
		String str;
		if (isCastleMove) {
			if (targetSquare == Square.g8 || targetSquare == Square.g1) {
				str = "O-O";
			} else {
				str = "O-O-O";
			}
		} else {
			if (isCaptureMove) {
				str = pieceName + "×" + Square.squareToName(targetSquare);
			} else {
				str = pieceName + Square.squareToName(targetSquare);
			}
			if (promotedName != NULL_CHAR) {
				str += promotedName;
			}
		}
		return str;
	}

	public static Move createDoublePushMove(char pieceName, int sourceSquare, int targetSquare) {
		Move m = new Move(pieceName, sourceSquare, targetSquare);
		m.isDoublePush = true;
		return m;
	}

	public static Move createCaptureMove(char pieceName, int sourceSquare, int targetSquare, char promotedName) {
		return new Move(pieceName, sourceSquare, targetSquare, true, promotedName);
	}

	public static Move createCastleMove(char pieceName, int sourceSquare, int targetSquare) {
		Move m = new Move(pieceName, sourceSquare, targetSquare);
		m.isCastleMove = true;
		return m;
	}

	public static Move createPromoteMove(char pieceName, int sourceSquare, int targetSquare, boolean isCaptureMove,
			char promotedName) {
		Move m = new Move(pieceName, sourceSquare, targetSquare);
		m.isCaptureMove = isCaptureMove;
		m.promotedName = promotedName;
		return m;
	}

	public static void printMoves(List<Move> moves) {
		TextTable table = new TextTable(85);

		if (moves == null || moves.isEmpty()) {
			table.addCell("EMPTY", "center");
			table.render();
			table.drawTable();
			return;
		}

		// HEADER
		table.addCell("No.", 0.6);
		table.addCell("Standard representation");
		table.addCell("Source piece");
		table.addCell("Source square");
		table.addCell("Target square");
		table.addCell("Capture flag");
		table.addCell("Double-push flag");
		table.addCell("Castle flag");
		table.addCell("Promoted piece");

		Move move;
		for (int i = 0; i < moves.size(); i++) {
			table.addRow();
			table.addCell((i + 1) + "", "center", 0.6);
			move = moves.get(i);
			table.addCell(move.toString(), "center");
			table.addCell(move.pieceName + "", "center");
			table.addCell(Square.squareToName(move.sourceSquare), "center");
			table.addCell(Square.squareToName(move.targetSquare), "center");
			table.addCell(move.isCaptureMove ? "1" : "0", "center");
			table.addCell(move.isDoublePush ? "1" : "0", "center");
			table.addCell(move.isCastleMove ? "1" : "0", "center");
			table.addCell(move.promotedName + "", "center");
		}

		table.addRow();
		table.addCell("Number of moves: " + moves.size(), "right");

		table.render();
		table.drawTable();
	}

	public char pieceName;
	public int sourceSquare;
	public int targetSquare;
	public char promotedName = '-';
	public boolean isCaptureMove = false;
	public boolean isDoublePush = false;
	public boolean isCastleMove = false;

	public static final char NULL_CHAR = '-';
}
