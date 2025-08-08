package utils.textural_table;

import java.util.List;

import utils.Dictionary;

public class Row {

	public Row() {
		cells = new Dictionary<>();
	}

	public void add(TexturalTableCell cell, double ratio) {
		cells.add(cell, ratio);
	}

	public void render(int rowWidth) {
		width = rowWidth;

		double sum = 0.0;
		for (int i = 0; i < cells.size(); i++) {
			sum += cells.getValue(i);
		}

		Dictionary<TexturalTableCell, Double> renderedCells = new Dictionary<>();
		for (int i = 0; i < cells.size(); i++) {
			TexturalTableCell renderedCell = cells.getKey(i);
			renderedCell.setWidth((int) (rowWidth * (cells.getValue(i) / sum)));
			renderedCells.add(renderedCell, cells.getValue(i) / sum);
		}

		int maximumHeight = getMaximumHeight(renderedCells.getKeys());

		for (int i = 0; i < cells.size(); i++) {
			renderedCells.getKey(i).setHeight(maximumHeight);
		}

		maxHeight = maximumHeight;

		this.cells = renderedCells;
	}

	protected int getMaximumHeight(List<TexturalTableCell> cells) {
		int max = -1;
		for (TexturalTableCell cell : cells) {
			max = Math.max(max, cell.getHeight());
		}
		return max;
	}

	@Override
	public String toString() {
		return "[width=" + width + " height=" + maxHeight + " cells=" + cells.toString() + "]";
	}

	public void drawRow() {
		List<TexturalTableCell> ttCells = cells.getKeys();

		for (int i = 0; i < maxHeight; i++) {
			for (TexturalTableCell tc : ttCells) {
				drawLine(tc, i);
			}
			System.out.println("|");
		}

	}

	protected void drawLine(TexturalTableCell cell, int pos) {

		int textLength = cell.getContentLength();
		int contentWidth = cell.getContentWidth();
		String alg = cell.alignment;

		int start = Math.min(textLength, pos * contentWidth);
		int end = Math.min(textLength, (pos + 1) * contentWidth);

		String target = cell.getContent().substring(start, end);
		int tlength = target.length();

		if (alg.equals(TexturalTableCell.LEFT)) {
			target = target + " ".repeat(contentWidth - tlength);
		} else if (alg.equals(TexturalTableCell.RIGHT)) {
			target = " ".repeat(contentWidth - tlength) + target;
		} else if (alg.equals(TexturalTableCell.CENTER)) {
			int ls = (contentWidth - tlength) / 2;
			int rs = contentWidth - tlength - ls;
			target = " ".repeat(ls) + target + " ".repeat(rs);
		}

		System.out.print("|" + " ".repeat(cell.margin) + target + " ".repeat(cell.margin));

	}

	protected void drawSplitter() {
		List<TexturalTableCell> ttCells = cells.getKeys();
		for (TexturalTableCell c : ttCells) {
			System.out.print("+");
			System.out.print("-".repeat(c.getWidth()));
		}
		System.out.println("+");
	}

	public int getMaximumWidth() {
		List<TexturalTableCell> ttCells = cells.getKeys();
		int sum = 0;
		for (TexturalTableCell cell : ttCells) {
			sum += cell.getWidth() + 2 * cell.getMargin();
		}
		return sum;
	}

	int maxHeight;
	int width;
	Dictionary<TexturalTableCell, Double> cells;
}
