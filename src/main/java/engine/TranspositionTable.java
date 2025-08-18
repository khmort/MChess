package engine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import chessboard.ChessBoard;
import utils.BitTools;

public class TranspositionTable {

    public TranspositionTable(int maxSize) {
        this.maxSize = maxSize;
        this.pointer = 0;
        Random rand = new Random();
        int maxName = max('K', 'Q', 'R', 'B', 'N', 'P', 'k', 'q', 'r', 'b', 'n', 'p');
        zobrist = new long[maxName + 1][64];
        for (char name : ChessBoard.NAMES) {
            for (int i = 0; i < 64; i++) {
                zobrist[name][i] = rand.nextLong();
            }
        }
        hashToIndex = new Long[maxSize];
        hashToInfos = new HashMap<>(maxSize);
    }

    public void put(ChessBoard board, double score, Flag flag, int depth) {
        long hash = hash(board);
        Infos infs = hashToInfos.get(hash);
        if (infs == null) {
            if (pointer >= maxSize) {
                pointer = 0;
            }
            infs = new Infos(score, flag, depth);
            if (hashToIndex[pointer] == null) {
                hashToIndex[pointer] = hash;
            } else {
                long trash = hashToIndex[pointer];
                hashToIndex[pointer] = hash;
                hashToInfos.remove(trash);
            }
            hashToInfos.put(hash, infs);
        } else {
            if (depth >= infs.depth) {
                infs.score = score;
                infs.f = flag;
                infs.depth = depth;    
            }
        }
        pointer++;
    }

    public Infos get(ChessBoard cb) {
        return hashToInfos.get(hash(cb));
    }

    public void remove(ChessBoard cb) {
        long hash = hash(cb);
        if (hashToInfos.containsKey(hash)) {
            hashToInfos.remove(hash);
            int index = Arrays.asList(hashToIndex).indexOf(hash);
            hashToIndex[index] = null;
        }
    }

    public static long hash(ChessBoard board) {
        long hash = 0, bb;
        int first;
        for (char name : ChessBoard.NAMES) {
            bb = board.getBitboard(name);
            while (bb != 0) {
                first = BitTools.getFirstSetBitPos(bb);
                hash = hash ^ zobrist[name][first];
                bb = BitTools.setBitOff(bb, first);
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

    private int maxSize;
    private int pointer;
    private HashMap<Long, Infos> hashToInfos;
    private Long[] hashToIndex;

    private static long[][] zobrist;
}