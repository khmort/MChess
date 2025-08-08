package utils;

public class BitTools {

	public static long createBoard(int... poses) {
		long board = 0;
		for (int i = 0, size = poses.length; i < size; i++) {
			board = setBitOn(board, poses[i]);
		}
		return board;
	}

	public static long createBoard(int pos) {
		return setBitOn(0L, pos);
	}

	public static long getBit(long board, int pos) {
		return (board >>> pos) & 1L;
	}

	public static long setBitOn(long board, int pos) {
		long on = 1L << pos;
		board = board | on;
		return board;
	}

	public static long setBitOff(long board, int pos) {
		long off = ~(1L << pos);
		board = board & off;
		return board;
	}

	public static long shiftRight(long board, int count) {
		return board << count;
	}

	public static long shiftLeft(long board, int count) {
		return board >>> count;
	}

	public static int bitCount(long x) {
		return Long.bitCount(x);
	}

//	public static int getFirstSetBitPos(long x) {
//		return bitCount((x & -x) - 1);
//	}

	public static int getFirstSetBitPos(long x) {
		return Long.numberOfTrailingZeros(x);
	}

	public static void print(long board) {
		System.out.println(" ".repeat(4) + boardHeader);
		System.out.println();
		long bit;
		for (int i = 0; i < 8; i++) {
			System.out.print((i + 1) + " ".repeat(3));
			for (int j = 0; j < 8; j++) {
				bit = getBit(board, i * 8 + j);
				if (bit == 0) {
					System.out.print(". ");
				} else {
					System.out.print(bit + " ");
				}
			}
			System.out.println();
		}
		System.out.println();
		System.out.println("64-bit nmber: " + board + "");
		System.out.println();
	}

	private final static String boardHeader = "a b c d e f g h";
	public static final long not_a_file = -72340172838076674L;
	public static final long not_h_file = 9187201950435737471L;
	public static final long not_ab_file = -217020518514230020L;
	public static final long not_gh_file = 4557430888798830399L;
	public static final long not_in_first_half = -1085102592571150096L;
	public static final long not_in_second_half = 1085102592571150095L;
	public static final long not_on_borders = 35604928818740736L;
	public static final long not_on_top_border = -256L;
	public static final long not_on_down_border = 72057594037927935L;
	public static final long wks_castle_occ = 6917529027641081856L;
	public static final long wqs_castle_occ = 1008806316530991104L;
	public static final long bks_castle_occ = 96L;
	public static final long bqs_castle_occ = 14L;
	public static final long wks_castle_check = 3458764513820540928L;
	public static final long wqs_castle_check = 1729382256910270464L;
	public static final long bks_castle_check = 48L;
	public static final long bqs_castle_check = 24L;
	public static final long wking_start_pos = 1152921504606846976L;
	public static final long bking_start_pos = 16L;
	
	public static final long K_oo = 4611686018427387904L;
	public static final long K_ooo = 288230376151711744L;
	public static final long k_oo = 64L;
	public static final long k_ooo = 4L;
}
