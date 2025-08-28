package engine.tt;

public class Infos {
    public Infos(double score, Flag flag, int depth) {
        this.score = score;
        this.f = flag;
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "[score: " + score + " flag: " + f + " depth: " + depth + "]";
    }

    public double score;
    public int depth;
    public Flag f;
}