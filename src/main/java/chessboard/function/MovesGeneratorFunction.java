package chessboard.function;

public interface MovesGeneratorFunction {
	
	public long apply(int square, long[] occ);

}
