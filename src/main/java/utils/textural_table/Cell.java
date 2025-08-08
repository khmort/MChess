package utils.textural_table;

public class Cell {
	public Cell(String content) {
		this.content = content;
	}

	public Cell() {
		this(null);
	}

	public void clear() {
		this.content = null;
	}

	public boolean isEmpty() {
		return this.content == null || this.content.isEmpty();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String context) {
		this.content = context;
	}

	private String content;
}
