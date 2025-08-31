package engine;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import chessboard.ChessBoard;
import chessboard.Move;
import chessboard.SimpleChessBoard;

public class ChessEngine {

	public static final long CENTER = 0x00003c3c3c3c0000l;
	public static final long BORDER = 0xffffc3c3c3c3ffffl;
	public static final double POSITIVE_INF = Double.POSITIVE_INFINITY;
	public static final double NEGITIVE_INF = Double.NEGATIVE_INFINITY;
	public static final Comparator<Move> SIMPLE_MOVE_SORTER = new Comparator<Move>() {
		@Override
		public int compare(Move arg0, Move arg1) {
			if (arg0.isCaptureMove || arg0.isCastleMove) return -1;
			if (arg1.isCaptureMove || arg1.isCastleMove) return 1;
			return 0;
		}
	};
	// نقشه مهره به ارزش شان
	public static final double[] PIECE_VALUE = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
															  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
															  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
															  0, 0, 0, 0, 0, 0, 0, 0, 0, 4.0, 0, 0, 0, 0, 0, 0, 0, 0,
															  1000.0, 0, 0, 3.5, 0, 1.0, 24.0, 6.0, 0, 0, 0, 0, 0, 0,
															  0, 0, 0, 0, 0, 0, 0, 0, 0, -4.0, 0, 0, 0, 0, 0, 0, 0, 0,
															  -1000.0, 0, 0, -3.5, 0, -1.0, -24.0, -6.0 };
	
	public double depthToScore[];
	public Move depthToMove[];
	private int nodeCount;
	private long calculationTime;

	public ChessEngine() {}

	public Move calculateBestMove(SimpleChessBoard board, int depth) {
		depthToMove = new Move[depth];
		depthToScore = new double[depth];
		nodeCount = 0;
		long beforeCalculation = System.nanoTime();
		minimax(depth, board, NEGITIVE_INF, POSITIVE_INF);
		calculationTime = System.nanoTime() - beforeCalculation;
		return depthToMove[depth - 1];
	}

	public void printSearchResult() {
		if (depthToMove == null || depthToScore == null) {
			return;
		}
		System.out.println("Time: " + (calculationTime / 1.0e9) + " sec");
		System.out.println("Nodes: " + nodeCount);
		for (int i=0; i<depthToMove.length; i++) {
			System.out.println("Depth=" + (i + 1) + " :: Best move -> " + depthToMove[i] + ", Best score -> " + depthToScore[i]);
		}
	}

	
	public double minimax(int depth, SimpleChessBoard simpleBoard, double alpha, double beta) {

		nodeCount++;
		if (depth == 0) return getBoardScore(simpleBoard);

		List<Move> moves = simpleBoard.generateMoves();

		// لیست حرکات برای ایجاد تنوع حرکتی
		// بدون shuffle موتور همیشه یک حرکت را انتخاب می کند
		Collections.shuffle(moves);
		// برای افزایش بازدهی هرس آلفا بتا
		moves.sort(SIMPLE_MOVE_SORTER);

		double score;

		if (simpleBoard.getSide() == 0) {

			for (Move move : moves) {
				simpleBoard.doMove(move);
				if (simpleBoard.isWhiteKingOnFire()) {
					simpleBoard.undoMove(move);
					continue;
				}
				score = minimax(depth - 1, simpleBoard, alpha, beta);
				simpleBoard.undoMove(move);
				if (score >= beta) {
					return beta;
				}
				if (score > alpha) {
					alpha = score;
					depthToMove[depth - 1] = move;
					// depthToScore[depth - 1] = score;
				}
			}
			return alpha;
		} else {

			for (Move move : moves) {
				simpleBoard.doMove(move);

				if (simpleBoard.isBlackKingOnFire()) {
					simpleBoard.undoMove(move);
					continue;
				}

				score = minimax(depth - 1, simpleBoard, alpha, beta);
				simpleBoard.undoMove(move);

				if (score <= alpha) {
					return alpha;
				}

				if (score < beta) {
					beta = score;
					depthToMove[depth - 1] = move;
					// depthToScore[depth - 1] = score;
				}
			}
			return beta;
		}
	}

	public double getBoardScore(ChessBoard cb) {
		double score = 0.0;
		for (char piece : ChessBoard.NAMES) {
			score += Long.bitCount(cb.getBitboard(piece)) * PIECE_VALUE[piece];
		}
		return score;
	}
}
