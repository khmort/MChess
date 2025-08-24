package chessboard;

public class Move {
	
	public Move(char pieceName, char targetName, char promotedName, int sourceSquare, int targetSquare, boolean isCaptureMove, boolean isDoublePush,
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

	public static Move createDoublePushMove(char pieceName, int sourceSquare, int targetSquare, int castleRight) {
		return new Move(pieceName, NULL_CHAR, NULL_CHAR, sourceSquare, targetSquare,
				false, true, false, false, castleRight);
	}

	public static Move createCaptureMove(char pieceName, char targetName, int sourceSquare, int targetSquare,
			boolean isPromoteMove, char promotedName, int castleRight) {
		return new Move(pieceName, targetName, promotedName, sourceSquare, targetSquare,
					true, false, false, isPromoteMove, castleRight);
	}

	public static Move createCastleMove(char pieceName, int sourceSquare, int targetSquare, int castleRight) {
		return new Move(pieceName, NULL_CHAR, NULL_CHAR, sourceSquare, targetSquare,
					false, false, false, false, castleRight);
	}

	public static Move createPromoteMove(char pieceName, char targetName, int sourceSquare, int targetSquare,
			boolean isCaptureMove, char promotedName, int castleRight) {
		return new Move(pieceName, targetName, promotedName, sourceSquare, targetSquare,
					isCaptureMove, false, false, true, castleRight);
	}

	@Override
	public String toString() {
		return "[name=" + pieceName + ", target-name=" + targetName + ", promote-to=" + promotedName + ", source-square=" + sourceSquare + ", target-square=" + targetSquare + ", {capture double-push castle promote " + (isCaptureMove ? "1" : "0") + " " + (isDoublePush ? "1" : "0") + " " + (isCastleMove ? "1" : "0") + " " + (isPromoteMove ? "1" : "0") + "}, castle-right=" + castleRight + "]";
	}

	public char pieceName;
	public char targetName;
	public char promotedName;
	public int sourceSquare;
	public int targetSquare;
	public boolean isCaptureMove;
	public boolean isDoublePush;
	public boolean isCastleMove;
	public boolean isPromoteMove;
	public int castleRight;

	public static final char NULL_CHAR = '-';
}
