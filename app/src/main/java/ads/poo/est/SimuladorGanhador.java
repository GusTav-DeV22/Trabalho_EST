package ads.poo.est;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class SimuladorGanhador {


    private final List<Integer> jogo;
    private final double precoPorJogo;

    private int sorteiosFeitos = 0;
    private int quadras = 0;
    private int quinas = 0;
    private int sena = 0;
    private double valorGanho = 0.0;
    private List<String> historico = new ArrayList<>();

    private final DecimalFormat df = new DecimalFormat("###,###.00");

    // Valores reais baseados no concurso 2894 (Mega-Sena)
    private static final double PREMIO_SENA = 67683670.18;
    private static final double PREMIO_QUINA = 67184.51;
    private static final double PREMIO_QUADRA = 1139.20;

    public SimuladorGanhador(List<Integer> jogo, double precoJogo) {
        this.jogo = new ArrayList<>(new TreeSet<>(jogo));
        this.precoPorJogo = precoJogo;
    }

    // Simula até ganhar a Mega-Sena
    public void simularAteGanharMega() {
        boolean ganhouMega = false;

        while (!ganhouMega) {
            Sorteio sorteio = new Sorteio();
            List<Integer> resultado = sorteio.getNumerosSorteados();
            int acertos = contarAcertos(resultado);
            sorteiosFeitos++;

            if (acertos == 6) {
                valorGanho += PREMIO_SENA;
                sena++;
                historico.add(formatarLinha(sorteiosFeitos, resultado, acertos, PREMIO_SENA));
                mostrarResumoFinal(resultado, acertos, PREMIO_SENA);
                ganhouMega = true;
            } else if (acertos == 5) {
                quinas++;
                valorGanho += PREMIO_QUINA;
            } else if (acertos == 4) {
                quadras++;
                valorGanho += PREMIO_QUADRA;
            }
        }
    }

    // Simula uma quantidade fixa de sorteios
    public void simularQuantidadeDeSorteios(int totalSorteios) {
        for (int i = 0; i < totalSorteios; i++) {
            Sorteio sorteio = new Sorteio();
            List<Integer> resultado = sorteio.getNumerosSorteados();
            int acertos = contarAcertos(resultado);
            sorteiosFeitos++;

            if (acertos == 6) {
                valorGanho += PREMIO_SENA;
                sena++;
                historico.add(formatarLinha(sorteiosFeitos, resultado, acertos, PREMIO_SENA));
            } else if (acertos == 5) {
                quinas++;
                valorGanho += PREMIO_QUINA;
            } else if (acertos == 4) {
                quadras++;
                valorGanho += PREMIO_QUADRA;
            }
        }

        mostrarResumoFinal(null, sena > 0 ? 6 : 0, sena > 0 ? PREMIO_SENA : 0);
    }

    private int contarAcertos(List<Integer> sorteio) {
        int acertos = 0;
        for (int dezena : jogo) {
            if (sorteio.contains(dezena)) {
                acertos++;
            }
        }
        return acertos;
    }

    private String formatarLinha(int numSorteio, List<Integer> resultado, int acertos, double premio) {
        return "#" + numSorteio + " " + resultado + " - " + acertos + " acertos (R$ " + df.format(premio) + ")";
    }

    private void mostrarResumoFinal(List<Integer> resultado, int acertos, double premioMega) {
        int semanas = (int) Math.ceil(sorteiosFeitos / 3.0);
        double totalGasto = sorteiosFeitos * precoPorJogo;
        double lucroLiquido = valorGanho - totalGasto;

        System.out.println("\n📊 RESUMO DA SIMULAÇÃO:");
        System.out.println("Jogo fixo:      " + jogo);
        if (resultado != null) {
            System.out.println("Último sorteio: " + resultado);
            System.out.println("Acertos último: " + acertos + (acertos == 6 ? " (SENA)" : ""));
            if (acertos == 6) {
                System.out.println("Prêmio (R$):    " + df.format(premioMega));
            }
        }
        System.out.println("Sena:          " + sena);
        System.out.println("Quinas:        " + quinas + " (R$ " + df.format(quinas * PREMIO_QUINA) + ")");
        System.out.println("Quadras:       " + quadras + " (R$ " + df.format(quadras * PREMIO_QUADRA) + ")");
        System.out.println("Sorteios:      " + sorteiosFeitos);
        System.out.println("Semanas:       " + semanas);
        System.out.println("Total gasto:   R$ " + df.format(totalGasto));
        System.out.println("Total ganho:   R$ " + df.format(valorGanho));
        System.out.println("Lucro/prejuízo:R$ " + df.format(lucroLiquido));

        if (!historico.isEmpty()) {
            System.out.println("\n📋 Sorteios com Sena:");
            for (String linha : historico) {
                System.out.println(linha);
            }
        }
    }

    public void exportarResultados(String nomeArquivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            writer.println("📊 RESUMO DA SIMULAÇÃO:");
            writer.println("Jogo fixo:      " + jogo);
            writer.println("Sena:          " + sena);
            writer.println("Quinas:        " + quinas + " (R$ " + df.format(quinas * PREMIO_QUINA) + ")");
            writer.println("Quadras:       " + quadras + " (R$ " + df.format(quadras * PREMIO_QUADRA) + ")");
            writer.println("Sorteios:      " + sorteiosFeitos);
            writer.println("Semanas:       " + (int) Math.ceil(sorteiosFeitos / 3.0));
            writer.println("Total gasto:   R$ " + df.format(sorteiosFeitos * precoPorJogo));
            writer.println("Total ganho:   R$ " + df.format(valorGanho));
            writer.println("Lucro/prejuízo:R$ " + df.format(valorGanho - (sorteiosFeitos * precoPorJogo)));
            writer.println();
            if (!historico.isEmpty()) {
                writer.println("📋 Sorteios com Sena:");
                for (String linha : historico) {
                    writer.println(linha);
                }
            }
            System.out.println("✅ Resultados exportados para o arquivo: " + nomeArquivo);
        } catch (IOException e) {
            System.out.println("❌ Erro ao exportar resultados: " + e.getMessage());
        }
    }
}
