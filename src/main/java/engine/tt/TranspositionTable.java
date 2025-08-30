package engine.tt;

import java.util.HashMap;
import java.util.Random;

import chessboard.ChessBoard;

public class TranspositionTable {

    public TranspositionTable(int size, int depth) {

        this.size = size;
        this.depth = depth;
        this.depthToPointer = new int[depth];
        this.depthToTT = new HashMap[depth];
        this.depthToHashs = new Long[depth][];

        // سهم هر عمق
        portion = size / depth;
        for (int i=0; i<depth; i++) {
            depthToTT[i] = new HashMap<>(portion);
            depthToHashs[i] = new Long[portion];
        }

        initZobrish();
    }

    private void initZobrish() {
        Random rand = new Random();
        int maxName = max('K', 'Q', 'R', 'B', 'N', 'P', 'k', 'q', 'r', 'b', 'n', 'p');
        zobrist = new long[maxName + 1][64];
        for (char name : ChessBoard.NAMES) {
            for (int i = 0; i < 64; i++) {
                zobrist[name][i] = rand.nextLong();
            }
        }
    }

    public void put(ChessBoard board, double score, Flag flag, int depth) {
        depth = depth - 1;
        int pointer = depthToPointer[depth];
        HashMap<Long, Infos> tt = depthToTT[depth];
        Long[] hashs = depthToHashs[depth];
        long hash;

        if (hashs[pointer] != null) {
            hash = hashs[pointer];
            tt.remove(hash);
        }

        hash = hash(board);
        tt.put(hash, new Infos(score, flag, depth));
        hashs[pointer] = hash;
        pointer++;

        if (pointer == portion) {
            pointer = 0;
        }

        depthToPointer[depth] = pointer;
    }

    public Infos get(ChessBoard board, int biggerThanDepth) {
        int i = biggerThanDepth - 1;
        long hash = hash(board);
        while (i < depth) {
            if (depthToTT[i].containsKey(hash)) {
                return depthToTT[i].get(hash);
            }
            i++;
        }
        return null;
    }

    public TranspositionTable increaseDepth(int toDepth) {
        TranspositionTable newTT = new TranspositionTable(toDepth * portion, toDepth);
        newTT.zobrist = zobrist;
        for (int i=0; i<depth; i++) {
            newTT.depthToTT[i] = depthToTT[i];
            newTT.depthToHashs[i] = depthToHashs[i];
            newTT.depthToPointer[i] = depthToPointer[i];
        }
        for (int i = depth; i < toDepth; i++) {
            newTT.depthToTT[i] = new HashMap<>(portion);
            newTT.depthToHashs[i] = new Long[portion];
            newTT.depthToPointer[i] = 0;
        }
        return newTT;
    }

    @Override
    public String toString() {
        String rows[] = new String[depth];
        int rowCounter = 0;
        for (int i=depth-1; i>=0; i--) {
            rows[rowCounter] = (i + 1) + ": " + depthToTT[i];
            rowCounter++;
        }
        return String.join("\n", rows);
    }

    public long hash(ChessBoard board) {
        long hash = 0, bb;
        int first;
        for (char name : ChessBoard.NAMES) {
            bb = board.getBitboard(name);
            while (bb != 0) {
                first = Long.numberOfTrailingZeros(bb);
                hash = hash ^ zobrist[name][first];
                bb &= ~(1l << first);
            }
        }
        return hash;
    }

    private final static int max(char... chs) {
        int max = -Integer.MAX_VALUE;
        for (char ch : chs) {
            max = Math.max(ch, max);
        }
        return max;
    }

    HashMap<Long, Infos> depthToTT[];
    Long depthToHashs[][];
    int depthToPointer[];
    int size;
    int depth;
    int portion;
    private long[][] zobrist;
}
