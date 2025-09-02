package engine.tt;

import java.util.Random;

import chessboard.ChessBoard;

public class TT {

    static long[][] zobristArray;
    static {
        Random rand = new Random();
        zobristArray = new long[115][64];
        for (int piece : ChessBoard.NAMES) {
            for (int j = 0; j < 64; j++) {
                zobristArray[piece][j] = rand.nextLong();
            }
        }
    }

    public SearchResult[] table;
    public int size;

    public TT(int sz) {
        size = (int) (Math.pow(2.0, Math.ceil(log2(sz + 1))) - 1);
        table = new SearchResult[size + 1];
    }

    protected final static double log2(double x) {
        return Math.log(x) / Math.log(2.0);
    }

    public static long generateHash(ChessBoard board) {
        long hash = 0;
        int piece;
        for (int i = 0; i < 64; i++) {
            piece = board.squareToName[i];
            if (piece != 0) {
                hash ^= zobristArray[piece][i];
            }
        }
        return hash;
    }

    public void put(ChessBoard board, double score, Flag flag, int depth) {
        long boardHash = generateHash(board);
        int index = (int) (boardHash & size);
        SearchResult res = new SearchResult(boardHash, score, flag, depth);
        SearchResult conflict = table[index];
        if (   conflict == null ||
               depth > conflict.depth ||
               (conflict.depth - depth <= 2 && flag == Flag.EXACT)) {

            table[index] = res;
        }
    }

    public void put(SearchResult sr) {
        int index = (int) (sr.boardHash & size);
        SearchResult conflict = table[index];
        if (   conflict == null ||
               sr.depth > conflict.depth ||
               (conflict.depth - sr.depth <= 2 && sr.flag == Flag.EXACT)) {

            table[index] = sr;
        }
    }

    public SearchResult get(ChessBoard board) {
        int index = (int) (generateHash(board) & size);
        if (table[index] == null) return null;
        long boardHash = generateHash(board);
        long tableBoardHash = table[index].boardHash;
        if (boardHash == tableBoardHash) {
            return table[index];
        }
        return null;
    }

    public SearchResult get(long hash) {
        int index = (int) (hash & size);
        if (table[index] == null) return null;
        long tableBoardHash = table[index].boardHash;
        if (hash == tableBoardHash) {
            return table[index];
        }
        return null;
    }

    public void remove(ChessBoard board) {
        long hash = generateHash(board);
        int index = (int) (hash & size);
        if (table[index] != null && table[index].boardHash == hash) {
            table[index] = null;
        }
    }

    public void remove(long hash) {
        int index = (int) (hash & size);
        if (table[index] != null && table[index].boardHash == hash) {
            table[index] = null;
        }
    }

    @Override
    public String toString() {
        String[] items = new String[size];
        int i=0;
        for (SearchResult res : table) {
            if (res == null) {
                items[i] = "null";
                continue;
            }
            items[i] = res.toString();
            i++;
        }
        return "[" + String.join(", ", items) + "]";
    }

}
