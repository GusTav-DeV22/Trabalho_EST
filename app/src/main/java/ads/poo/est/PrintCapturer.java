package ads.poo.est;

import java.io.PrintWriter;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class PrintCapturer extends PrintWriter {
    private final JTextArea output;

    public PrintCapturer(JTextArea output) {
        super(System.out); // apenas para cumprir o construtor da superclasse
        this.output = output;
    }

    @Override
    public void println(String line) {
        SwingUtilities.invokeLater(() -> output.append(line + "\n"));
    }

    @Override
    public void print(String text) {
        SwingUtilities.invokeLater(() -> output.append(text));
    }
}
