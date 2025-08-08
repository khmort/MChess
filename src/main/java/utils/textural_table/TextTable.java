package utils.textural_table;

import java.util.ArrayList;
import java.util.List;

public class TextTable extends Table {

	public TextTable(int width) {
		setWidth(width);
		rows = new ArrayList<>();
		addRow();
	}

	public TextTable() {
		this(0);
	}

	@Override
	public void setHeight(int height) {
		throw new UnsupportedOperationException();
	}

	public void addCell(TexturalTableCell cell, double ratio) {
		lastRow.add(cell, ratio);
	}

	public void addCell(String content, double ratio) {
		TexturalTableCell cell = new TexturalTableCell(content);
		addCell(cell, ratio);
	}

	public void addCell(String content) {
		addCell(content, 1.0);
	}

	public void addCell(String content, String alignment, double ratio) {
		TexturalTableCell cell = new TexturalTableCell(content);
		cell.setAlignment(alignment);
		addCell(cell, ratio);

	}

	public void addCell(String content, String alignment) {
		addCell(content, alignment, 1.0);
	}

	public void addRow() {
		lastRow = new Row();
		rows.add(lastRow);
	}

	public void render() {

		if (getWidth() == 0) {
			int mx = -1;
			for (Row r : rows) {
				mx = Math.max(r.getMaximumWidth(), mx);
			}
			setWidth(mx);
		}

		for (int i = 0; i < rows.size(); i++) {
			rows.get(i).render(getWidth());
		}
	}

	public void drawTable() {
		Row firstRow = rows.get(0);
		firstRow.drawSplitter();
		for (Row r : rows) {
			r.drawRow();
			r.drawSplitter();
		}
	}

	@Override
	public String toString() {
		return "{width=" + getWidth() + " height=" + height + "\n" + rows.toString() + "}";
	}

	List<Row> rows;
	Row lastRow;
}
