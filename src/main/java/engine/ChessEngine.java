package engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import chessboard.ChessBoard;
import chessboard.Move;
import chessboard.SimpleChessBoard;
import chessboard.graphics.GraphicChessBoard;
import utils.BitTools;

public class ChessEngine {

	public ChessEngine() {
		// tt = new TranspositionTable(100_000_000);
	}

	public Move calcBestMove(SimpleChessBoard scb, int depth) {

		nodeMoves = new Move[depth];
		nodes = new double[depth];

		long time = System.currentTimeMillis();
		visits = 0;
		// tt.remove(scb);
		minimax(depth, scb, NEG_INF, POS_INF);
		System.out.println("Visits: " + visits);
		System.out.println("Time: " + (System.currentTimeMillis() - time) / 1000.0 + " sec");
		Move minimaxMove = nodeMoves[depth - 1];

		System.out.println("Chain:");
		System.out.println(Arrays.deepToString(nodeMoves));

		nodes = null;
		nodeMoves = null;

		return minimaxMove;
	}

	
	public double minimax(int depth, SimpleChessBoard simpleBoard, double alpha, double beta) {

		visits++;

		if (depth == 0) {
			return getBoardScore(simpleBoard);
		}

		List<Move> moves = simpleBoard.generateMoves();
		Collections.shuffle(moves);
		moves.sort(moveSorter);
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
				if (score > alpha) {
					alpha = score;
					nodeMoves[depth - 1] = move;
				}
				if (score >= beta) {
					return score;
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
				if (score < beta) {
					beta = score;
					nodeMoves[depth - 1] = move;
				}
				if (score <= alpha) {
					return score;
				}
			}
			return beta;
		}

	}

	public double getBoardScore(ChessBoard cb) {
		double score = 0.0;
		for (char piece : ChessBoard.NAMES) {
			score += BitTools.bitCount(cb.getBitboard(piece)) * PIECE_VALUE[piece];
		}
		return score;
	}

	public static final List<Move> removeMovesRandomly(List<Move> list, double perc) {
		perc = perc < 0.0 ? 0.0 : (perc > 1.0 ? 1.0 : perc);
		int count = (int) (perc * list.size());
		int randIndex;
		Random randObj = new Random();
		for (int i = 0; i < count; i++) {
			randIndex = randObj.nextInt(count - i);
			list.remove(randIndex);
		}
		return list;
	}

	public TranspositionTable tt;

	public static double nodes[];
	public static Move nodeMoves[];
	
	protected static long visits = 0;

	protected static final double POS_INF = Double.POSITIVE_INFINITY;
	protected static final double NEG_INF = Double.NEGATIVE_INFINITY;

	public static final double[] PIECE_VALUE = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4.0, 0, 0, 0, 0, 0, 0, 0, 0, 1000.0, 0, 0, 3.5, 0, 1.0, 24.0, 6.0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -4.0, 0, 0, 0, 0, 0, 0, 0, 0, -1000.0, 0, 0, -3.5, 0, -1.0, -24.0,
			-6.0 };

	protected static final Comparator<Move> moveSorter = new Comparator<Move>() {
		@Override
		public int compare(Move arg0, Move arg1) {
			if (arg0.isCaptureMove) {
				return -1;
			}
			if (arg1.isCaptureMove) {
				return 1;
			}
			return 0;
		}
	};

	public static final long CENTER = 0x00003c3c3c3c0000l;
	public static final long TOP = 0x0000000000ffffffl;
	public static final long DOWN = 0xffffff0000000000l;
	public static final long BORDER = 0xffffc3c3c3c3ffffl;
}
