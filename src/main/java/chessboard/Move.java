package chessboard;

import utils.textural_table.TextTable;

public class Move {
	
	public Move(int pieceName, int targetName, int promotedName, int sourceSquare, int targetSquare, boolean isCaptureMove, boolean isDoublePush,
			boolean isCastleMove, boolean isPromoteMove, int castleRight) {
		this.pieceName = pieceName;
		this.targetName = targetName;
		this.sourceSquare = sourceSquare;
		this.targetSquare = targetSquare;
		this.isCaptureMove = isCaptureMove;
		this.isDoublePush = isDoublePush;
		this.isCastleMove = isCastleMove;
		this.promotedName = promotedName;
		this.isPromoteMove = isPromoteMove;
		this.castleRight = castleRight;
	}

	public static Move createDoublePushMove(int pieceName, int sourceSquare, int targetSquare, int castleRight) {
		return new Move(pieceName, NULL, NULL, sourceSquare, targetSquare,
				false, true, false, false, castleRight);
	}

	public static Move createCaptureMove(int pieceName, int targetName, int sourceSquare, int targetSquare,
			boolean isPromoteMove, int promotedName, int castleRight) {
		return new Move(pieceName, targetName, promotedName, sourceSquare, targetSquare,
					true, false, false, isPromoteMove, castleRight);
	}

	public static Move createCastleMove(int pieceName, int sourceSquare, int targetSquare, int castleRight) {
		return new Move(pieceName, NULL, NULL, sourceSquare, targetSquare,
					false, false, true, false, castleRight);
	}

	public static Move createPromoteMove(int pieceName, int targetName, int sourceSquare, int targetSquare,
			boolean isCaptureMove, int promotedName, int castleRight) {
		return new Move(pieceName, targetName, promotedName, sourceSquare, targetSquare,
					isCaptureMove, false, false, true, castleRight);
	}

	public void print() {
		TextTable table = new TextTable(100);
		table.addCell("Source Name");
		table.addCell("Target Name");
		table.addCell("Promoted Name");
		table.addCell("Source Square");
		table.addCell("Target Square");
		table.addCell("Capture Move?");
		table.addCell("Double-push Move?");
		table.addCell("Castle Move?");
		table.addCell("Promote Move?");
		table.addCell("Castling right");
		table.addRow();
		table.addCell((pieceName == 0 ? "0" : (char) pieceName + ""));
		table.addCell((targetName == 0 ? "0" : (char) targetName + ""));
		table.addCell((promotedName == 0 ? "0" : (char) promotedName + ""));
		table.addCell(sourceSquare + " (r: " + (sourceSquare / 8) + " c: " + (sourceSquare % 8) + ")");
		table.addCell(targetSquare + " (r: " + (targetSquare / 8) + " c: " + (targetSquare % 8) + ")");
		table.addCell(isCaptureMove ? "1" : "-");
		table.addCell(isDoublePush ? "1" : "-");
		table.addCell(isCastleMove ? "1" : "-");
		table.addCell(isPromoteMove ? "1" : "-");
		table.addCell(
			(castleRight & 1) + " " +
			((castleRight >>> 1) & 1) + " " +
			((castleRight >>> 2) & 1) + " " +
			((castleRight >>> 3) & 1)
		);
		table.render();
		table.drawTable();
	}

	@Override
	public String toString() {
		String res = (char) pieceName + "";
		if (isCaptureMove) {
			res += "×";
		}
		res += Square.SQUARES_NAME[targetSquare];
		if (isPromoteMove) {
			res += (char) promotedName;
		}
		return res;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		Move other = (Move) obj;
		return pieceName == other.pieceName &&
			   sourceSquare == other.sourceSquare &&
			   targetName == other.targetName &&
			   targetSquare == other.targetSquare &&
			   promotedName == other.promotedName;
	}

	public int pieceName;
	public int targetName;
	public int promotedName;
	public int sourceSquare;
	public int targetSquare;
	public boolean isCaptureMove;
	public boolean isDoublePush;
	public boolean isCastleMove;
	public boolean isPromoteMove;
	public int castleRight;

	public static final int NULL = '-';
}
