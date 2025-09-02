package engine.tt;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import chessboard.Move;
import chessboard.SimpleChessBoard;
import engine.ChessEngine;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Rook;

public class Test {
    public static void main(String[] args) throws NumberFormatException, ClassNotFoundException, IOException {

        Pawn.init();
		Bishop.init();
		Rook.init();
		Knight.init();
		King.init();

        SimpleChessBoard chessboard = new SimpleChessBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        ChessEngine engine = new ChessEngine(3_000_000);
        System.out.println(engine.tt.size);

        engine.calculateBestMove(chessboard, 9);
        engine.printSearchResult();

    }
}
