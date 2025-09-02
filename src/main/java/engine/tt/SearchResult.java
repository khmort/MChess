package engine.tt;

public class SearchResult {

    public long boardHash;
    public double score;
    public Flag flag;
    public int depth;

    public SearchResult(long boardHash, double score, Flag flag, int depth) {
        this.boardHash = boardHash;
        this.score = score;
        this.flag = flag;
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "[hash: " + boardHash + ", score: " + score + ", flag: " + flag +
               ", depth: " + depth + "]";
    }
}
