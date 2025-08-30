package chessboard;

import java.io.IOException;
import java.util.List;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Queen;
import pieces.Rook;

public class Test {

    public static void main(String[] args) throws NumberFormatException, ClassNotFoundException, IOException {

        King.init();
        Rook.init();
        Bishop.init();
        Knight.init();
        Pawn.init();

        System.out.println(Knight.attacksBySquare[50]);
        System.exit(0);

        SimpleChessBoard scb = new SimpleChessBoard("r3kbnr/ppp2pp1/3p1q2/1P2p3/3nP3/6P1/P1PP1P1P/RNBQK1NR b KQkq - 2 10");
        scb.print();

        List<Move> moves = scb.generateMoves();

        int idx = 0;
        for (Move move : moves) {
            System.out.println("idx=" + idx);
            move.print();
            idx++;
        }

        // // System.out.println(scb.getOccupancies(0));

        // scb.doMove(moves.get(moves.size() - 1));
        // scb.print();
        // System.out.println(scb.getOccupancies(1));
        
        // scb.undoMove(moves.get(moves.size() - 1));
        // scb.print();
        // System.out.println(scb.getOccupancies(1));
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
