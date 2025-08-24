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



        SimpleChessBoard cb = new SimpleChessBoard("2k5/4q3/8/8/4P3/8/K7/8 b - - 0 1");
        
        List<Move> moves = cb.generateMoves();

        for (Move m : moves) {
            System.err.println(m);
        }

        // Collections.shuffle(moves);

        Move randomMove = moves.get(5);

        System.out.println("Move=" + randomMove);

        System.out.println("Chessboard <before move:");
        cb.print();
        System.out.println();

        cb.doMove(randomMove);

        System.out.println("Chessboard after> move:");
        cb.print();
    }
}
