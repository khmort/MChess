package chessboard;

import java.io.IOException;
import java.util.List;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Rook;

public class Test {

    public static void main(String[] args) throws NumberFormatException, ClassNotFoundException, IOException {

        King.init();
        Rook.init();
        Bishop.init();
        Knight.init();
        Pawn.init();

        SimpleChessBoard scb = new SimpleChessBoard("4k2r/8/8/8/8/8/3K4/8 b k - 0 1");
        scb.print();

        List<Move> moves = scb.generateMoves();

        // for (Move move : moves) {
        //     move.print();
        // }

        scb.doMove(moves.get(5));
        scb.print();
        
        scb.undoMove(moves.get(5));
        scb.print();
    }

    // private static double runBenchmark(String[] fens, int iterations) {
    //     SimpleChessBoard cb = new SimpleChessBoard(fens[0]);
    //     List<Move> moves = cb.generateMoves();
    //     int moveIndex = 0;

    //     long start = System.nanoTime();

    //     for (int i = 0; i < iterations; i++) {
    //         // هر چند هزار حرکت یک FEN جدید برای تنوع
    //         if (i % 5000 == 0) {
    //             cb = new SimpleChessBoard(fens[i % fens.length]);
    //             moves = cb.generateMoves();
    //         }

    //         Move m = moves.get(moveIndex % moves.size());
    //         cb.doMove(m);
    //         cb.undoMove(m);
    //         moveIndex++;
    //     }

    //     long end = System.nanoTime();
    //     return (end - start) / 1e9; // به ثانیه
    // }
}
