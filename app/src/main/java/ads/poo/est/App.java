package ads.poo.est;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class App extends JFrame {

    private JComboBox<Integer> cbDezenas;
    private JTextArea areaResultado;
    private JButton btnSimular, btnExportar;
    private JRadioButton rbAteGanhar, rbFixo;
    private JCheckBox cbSurpresinha;
    private JTextField tfQtdSorteios;

    private List<Integer> jogoManual = null; // guardar o jogo manual se escolhido

    public App() {
        setTitle("Simulador da Mega-Sena");
        setSize(600, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        JPanel topo = new JPanel(new GridLayout(6, 2, 10, 10));

        topo.add(new JLabel("Quantidade de dezenas (6 a 20):"));
        cbDezenas = new JComboBox<>();
        for (int i = 6; i <= 20; i++) cbDezenas.addItem(i);
        topo.add(cbDezenas);

        cbSurpresinha = new JCheckBox("Surpresinha (jogo aleatório)", true);
        topo.add(cbSurpresinha);
        topo.add(new JLabel(""));

        JButton btnEscolherJogo = new JButton("Escolher dezenas manualmente");
        topo.add(btnEscolherJogo);
        topo.add(new JLabel(""));

        rbAteGanhar = new JRadioButton("Simular até ganhar");
        rbFixo = new JRadioButton("Simular quantidade fixa", true);
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbAteGanhar);
        grupo.add(rbFixo);
        topo.add(rbAteGanhar);
        topo.add(rbFixo);

        topo.add(new JLabel("Qtd de sorteios (se modo fixo):"));
        tfQtdSorteios = new JTextField("1000");
        topo.add(tfQtdSorteios);

        btnSimular = new JButton("Iniciar Simulação");
        btnExportar = new JButton("Exportar Resultado");
        btnExportar.setEnabled(false);  // só ativa após simulação

        topo.add(btnSimular);
        topo.add(btnExportar);

        painel.add(topo, BorderLayout.NORTH);

        areaResultado = new JTextArea();
        areaResultado.setEditable(false);
        areaResultado.setFont(new Font("Monospaced", Font.PLAIN, 12));
        painel.add(new JScrollPane(areaResultado), BorderLayout.CENTER);

        add(painel);

        // Ações
        btnEscolherJogo.addActionListener(e -> escolherJogoManual());
        btnSimular.addActionListener(e -> executarSimulacao());
        btnExportar.addActionListener(e -> exportarResultado());
        cbSurpresinha.addActionListener(e -> {
            if (cbSurpresinha.isSelected()) {
                jogoManual = null;
            }
        });
    }

    private void escolherJogoManual() {
        int dezenas = (int) cbDezenas.getSelectedItem();
        List<Integer> dezenasEscolhidas = new ArrayList<>();
        while (dezenasEscolhidas.size() < dezenas) {
            String input = JOptionPane.showInputDialog(this,
                    "Digite a dezena #" + (dezenasEscolhidas.size() + 1) + " (1 a 60):");
            if (input == null) { // Cancelou
                return;
            }
            try {
                int num = Integer.parseInt(input);
                if (num < 1 || num > 60) {
                    JOptionPane.showMessageDialog(this, "Número inválido! Deve ser entre 1 e 60.");
                } else if (dezenasEscolhidas.contains(num)) {
                    JOptionPane.showMessageDialog(this, "Número repetido! Escolha outro.");
                } else {
                    dezenasEscolhidas.add(num);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Digite um número válido!");
            }
        }
        Collections.sort(dezenasEscolhidas);
        jogoManual = dezenasEscolhidas;
        cbSurpresinha.setSelected(false);
        JOptionPane.showMessageDialog(this, "Jogo manual registrado: " + jogoManual);
    }

    private void executarSimulacao() {
        areaResultado.setText("");
        int dezenas = (int) cbDezenas.getSelectedItem();
        double preco = calcularPreco(dezenas);

        List<Integer> jogo;
        if (cbSurpresinha.isSelected() || jogoManual == null) {
            jogo = gerarJogoAleatorio(dezenas);
        } else {
            jogo = new ArrayList<>(jogoManual);
        }

        SimuladorGanhador sim = new SimuladorGanhador(jogo, preco);

        if (rbAteGanhar.isSelected()) {
            sim.simularAteGanharMega();
        } else {
            int sorteios;
            try {
                sorteios = Integer.parseInt(tfQtdSorteios.getText());
                if (sorteios < 1) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantidade de sorteios inválida!");
                return;
            }
            sim.simularQuantidadeDeSorteios(sorteios);
        }

        PrintCapturer capturer = new PrintCapturer(areaResultado);
        sim.exportarResultados(capturer);


        btnExportar.setEnabled(true);
    }

    private void exportarResultado() {
        JFileChooser fileChooser = new JFileChooser();
        int opcao = fileChooser.showSaveDialog(this);
        if (opcao == JFileChooser.APPROVE_OPTION) {
            File arquivo = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(arquivo)) {
                writer.write(areaResultado.getText());
                JOptionPane.showMessageDialog(this, "Resultado exportado para " + arquivo.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar: " + e.getMessage());
            }
        }
    }

    private List<Integer> gerarJogoAleatorio(int quantidade) {
        List<Integer> dezenas = new ArrayList<>();
        for (int i = 1; i <= 60; i++) dezenas.add(i);
        Collections.shuffle(dezenas);
        List<Integer> jogo = dezenas.subList(0, quantidade);
        Collections.sort(jogo);
        return new ArrayList<>(jogo);
    }

    private double calcularPreco(int dezenas) {
        return switch (dezenas) {
            case 6 -> 5.00;
            case 7 -> 35.00;
            case 8 -> 140.00;
            case 9 -> 420.00;
            case 10 -> 1050.00;
            case 11 -> 2310.00;
            case 12 -> 4620.00;
            case 13 -> 8580.00;
            case 14 -> 15015.00;
            case 15 -> 25025.00;
            case 16 -> 40040.00;
            case 17 -> 61880.00;
            case 18 -> 92820.00;
            case 19 -> 139230.00;
            case 20 -> 200000.00;
            default -> 0.0;
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}
