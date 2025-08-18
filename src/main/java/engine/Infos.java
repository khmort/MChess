package engine;

public class Infos {
    public Infos(double score, Flag flag, int depth) {
        this.score = score;
        this.f = flag;
        this.depth = depth;
    }

    double score;
    int depth;
    Flag f;
}