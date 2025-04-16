package cashflowoptimizer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Custom output stream that redirects System.out to a JTextArea
 */
public class TextAreaOutputStream extends OutputStream {
    private JTextArea textArea;
    private StringBuilder buffer;
    
    public TextAreaOutputStream(JTextArea textArea) {
        this.textArea = textArea;
        this.buffer = new StringBuilder();
    }
    
    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        buffer.append(c);
        
        if (c == '\n') {
            final String text = buffer.toString();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textArea.append(text);
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                }
            });
            buffer = new StringBuilder();
        }
    }
    
    /**
     * Create a PrintStream that redirects output to the given JTextArea
     */
    public static PrintStream redirectSystemOut(JTextArea textArea) {
        PrintStream customOut = new PrintStream(new TextAreaOutputStream(textArea));
        PrintStream originalOut = System.out;
        System.setOut(customOut);
        return originalOut; // Return original so it can be restored later
    }
}