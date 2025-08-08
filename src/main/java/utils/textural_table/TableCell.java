package utils.textural_table;

public class TableCell extends Cell {
	
	public TableCell(String arg0) {
		setContent(arg0);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getMargin() {
		return margin;
	}
	
	public void setWidth(int w) {
		if (w < 1) {
			w = 0;
		}
		preferenceWidth = w;
	}
	
	public void setHeight(int h) {
		if (h < 1) {
			h = 0;
		}
		preferenceHeight = h;
	}
	
	public void setMargin(int m) {
		this.margin = Math.max(0, m);
	}
	
	@Override
	public void clear() {
		super.clear();
		preferenceWidth = 0;
		width = 0;
		height = 0;
		margin = 0;
	}
	
	protected int preferenceWidth;
	protected int preferenceHeight;
	protected int width;
	protected int height;
	protected int margin;
}
