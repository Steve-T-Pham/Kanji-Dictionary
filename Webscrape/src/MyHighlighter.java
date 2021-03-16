import java.awt.Color;
import javax.swing.text.DefaultHighlighter;

//class to create a highlighter
public class MyHighlighter extends DefaultHighlighter.DefaultHighlightPainter {
	public MyHighlighter(Color color) {
		super(color);
	}
}
