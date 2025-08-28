package engine.tt;

import chessboard.SimpleChessBoard;

public class Test {
    public static void main(String[] args) {
        TranspositionTable tt = new TranspositionTable(4, 2);

        SimpleChessBoard board1 = new SimpleChessBoard("1r4nr/1bpk1q2/p1p2bp1/1pP2p2/1P1P3B/3B1N1P/P4PP1/R2QR1K1 b - - 0 20");
        SimpleChessBoard board2 = new SimpleChessBoard("r1b1kbnr/2p3q1/ppp3p1/5p1p/3P4/3BB3/PPP2PPP/RN1Q1RK1 w kq - 0 12");
        SimpleChessBoard board3 = new SimpleChessBoard("rnbqkbnr/2pp4/pp3pp1/7p/3P4/3B1N2/PPP2PPP/RNBQK2R b KQkq - 1 7");
        SimpleChessBoard board4 = new SimpleChessBoard("rnbqkbnr/ppppp3/5pp1/7p/3PP3/8/PPP1BPPP/RNBQK1NR w KQkq - 0 4");
        SimpleChessBoard board5 = new SimpleChessBoard("rnbqkbnr/ppppp3/5pp1/7p/3PP3/5N2/PPP1BPPP/RNBQK2R b KQkq - 1 4");
        SimpleChessBoard board6 = new SimpleChessBoard("rnbqkbnr/ppppp3/5pp1/7p/3PP3/5N2/PPP1BPPP/RNBQK b KQkq - 1 4");

        tt.put(board1, 0, Flag.EXACT, 1);
        tt.put(board2, -1.5, Flag.UPPER, 2);
        tt.put(board3, 2, Flag.UPPER, 2);
        tt.put(board4, 0, Flag.UPPER, 1);
        tt.put(board5, 0.1, Flag.LOWER, 2);

        System.out.println(tt);
        tt = tt.increaseDepth(4);
        tt.put(board6, 0, Flag.EXACT, 3);
        System.out.println(tt);

    }
}
