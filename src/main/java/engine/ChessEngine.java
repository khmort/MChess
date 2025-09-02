package engine;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import chessboard.ChessBoard;
import chessboard.Move;
import chessboard.SimpleChessBoard;
import engine.tt.Flag;
import engine.tt.SearchResult;
import engine.tt.TT;

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
	public TT tt;
	private int nodeCount;
	private long calculationTime;

	public ChessEngine(int ttSize) {
		tt = new TT(ttSize);
	}

	public Move calculateBestMove(SimpleChessBoard board, int depth) {
		tt.remove(board);
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
		Collections.sort(moves, SIMPLE_MOVE_SORTER);

		long hash = TT.generateHash(simpleBoard);
		
		SearchResult nodeResult = tt.get(hash);
		if (nodeResult != null && nodeResult.depth >= depth) {
			if (nodeResult.flag == Flag.EXACT) {
				return nodeResult.score;
			}
			if (nodeResult.score >= beta && nodeResult.flag == Flag.LOWER) {
				return nodeResult.score;
			}
			if (nodeResult.score <= alpha && nodeResult.flag == Flag.UPPER) {
				return nodeResult.score;
			}
		}

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
					tt.put(new SearchResult(hash, score, Flag.LOWER, depth));
					return score;
				}

				if (score > alpha) {
					alpha = score;
					depthToMove[depth - 1] = move;
				}
			}
			tt.put(new SearchResult(hash, alpha, Flag.EXACT, depth));
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
					tt.put(new SearchResult(hash, score, Flag.UPPER, depth));
					return score;
				}

				if (score < beta) {
					beta = score;
					depthToMove[depth - 1] = move;
				}
			}
			tt.put(new SearchResult(hash, beta, Flag.EXACT, depth));
			return beta;
		}
	}

	public double getBoardScore(ChessBoard cb) {
		double score = 0.0;
		for (int piece : ChessBoard.NAMES) {
			score += Long.bitCount(cb.getBitboard(piece)) * PIECE_VALUE[piece];
		}
		return score;
	}
}
