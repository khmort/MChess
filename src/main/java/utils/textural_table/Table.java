package utils.textural_table;

public class Table {

	public Table(int width, int height) {
		setWidth(width);
		setHeight(height);
	}

	public Table() {
	}

	public void setWidth(int width) {
		this.width = Math.abs(width);
	}

	public void setHeight(int height) {
		this.height = Math.abs(height);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int calculateArea() {
		return width * height;
	}
	
	public int calculatePerimeter() {
		return 2 * (width + height);
	}

	int width;
	int height;

}