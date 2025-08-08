package utils.textural_table;

public class TexturalTableCell extends TableCell {

	public TexturalTableCell(String content, String alignment, int margin, int width, int height) {
		super(content);
		setAlignment(alignment);
		setMargin(margin);
		setWidth(width);
		setHeight(height);
	}

	public TexturalTableCell(String content, String alignment, int margin) {
		super(content);
		setAlignment(alignment);
		setMargin(margin);
	}

	public TexturalTableCell(String content, String alignment) {
		this(content, alignment, 1);
	}

	public TexturalTableCell(String content, int margin) {
		this(content, LEFT, margin);
	}

	public TexturalTableCell(String arg0) {
		this(arg0, LEFT);
	}

	@Override
	public void setContent(String content) {
		super.setContent(content);
		if (isEmpty()) {
			length = 0;
		} else {
			length = content.length();
		}
		if (preferenceWidth == 0) {
			width = length + 2 * getMargin();
		} else {
			width = preferenceWidth;
		}

		lines = (int) Math.ceil((double) length / getContentWidth());

		if (preferenceHeight == 0) {
			height = lines;
		} else {
			height = Math.max(lines, preferenceHeight);
		}
	}

	public void resetContent() {
		setContent(getContent());
	}

	public void removePreferenceSize() {
		preferenceHeight = 0;
		preferenceWidth = 0;
		resetContent();
	}

	public void setAlignment(String allignment) {
		this.alignment = allignment;
	}

	@Override
	public void setWidth(int w) {
		super.setWidth(w);
		resetContent();
	}

	@Override
	public void setHeight(int h) {
		super.setHeight(h);
		resetContent();
	}

	@Override
	public void setMargin(int m) {
		super.setMargin(m);
		resetContent();
	}

	public String getAlignment() {
		return alignment;
	}

	public int getContentLength() {
		return length;
	}

	public int getContentWidth() {
		return getWidth() - 2 * getMargin();
	}

	@Override
	public String toString() {
		return "[`" + getContent() + "` width=" + getWidth() + " height=" + getHeight() + "]";
	}

	private int length;
	private int lines;
	protected String alignment = LEFT;

	public static String RIGHT = "right";
	public static String LEFT = "left";
	public static String CENTER = "center";

}
