package pieces;

public class Queen {

	public static long getAttacksOnFly(int square, long blocks) {
		return Rook.getAttacksOnFly(square, blocks & Rook.attacksBySquare[square])
				| Bishop.getAttacksOnFly(square, blocks & Bishop.attacksBySquare[square]);
	}
}
