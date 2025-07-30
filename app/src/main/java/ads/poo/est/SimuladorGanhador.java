package ads.poo.est;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;

public class SimuladorGanhador {

    private final List<Integer> jogo;
    private final double precoPorJogo;

    private int[] contagemDezenas = new int[61];
    private int totalPares = 0;
    private int totalImpares = 0;

    private int sorteiosFeitos = 0;
    private int quadras = 0;
    private int quinas = 0;
    private int sena = 0;
    private double valorGanho = 0.0;
    private List<String> historico = new ArrayList<>();
    private List<String> historicoCompleto = new ArrayList<>(); // NOVO

    private final DecimalFormat df = new DecimalFormat("###,###.00");

    private static final double PREMIO_SENA = 67683670.18;
    private static final double PREMIO_QUINA = 67184.51;
    private static final double PREMIO_QUADRA = 1139.20;

    public SimuladorGanhador(List<Integer> jogo, double precoJogo) {
        this.jogo = new ArrayList<>(new TreeSet<>(jogo));
        this.precoPorJogo = precoJogo;
    }

    public void simularAteGanharMega() {
        boolean ganhouMega = false;

        while (!ganhouMega) {
            Sorteio sorteio = new Sorteio();
            List<Integer> resultado = sorteio.getNumerosSorteados();
            sorteiosFeitos++;

            // Contagem de dezenas e par/ímpar
            for (int dezena : resultado) {
                contagemDezenas[dezena]++;
                if (dezena % 2 == 0) totalPares++;
                else totalImpares++;
            }

            int acertos = contarAcertos(resultado);
            double premio = getPremioPorAcertos(acertos);
            historicoCompleto.add(formatarLinha(sorteiosFeitos, resultado, acertos, premio));

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


    public void simularQuantidadeDeSorteios(int totalSorteios) {
        for (int i = 0; i < totalSorteios; i++) {
            Sorteio sorteio = new Sorteio();
            List<Integer> resultado = sorteio.getNumerosSorteados();
            sorteiosFeitos++;

            // Contagem de dezenas e par/ímpar
            for (int dezena : resultado) {
                contagemDezenas[dezena]++;
                if (dezena % 2 == 0) totalPares++;
                else totalImpares++;
            }

            int acertos = contarAcertos(resultado);
            double premio = getPremioPorAcertos(acertos);
            historicoCompleto.add(formatarLinha(sorteiosFeitos, resultado, acertos, premio));

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

    private double getPremioPorAcertos(int acertos) {
        return switch (acertos) {
            case 6 -> PREMIO_SENA;
            case 5 -> PREMIO_QUINA;
            case 4 -> PREMIO_QUADRA;
            default -> 0.0;
        };
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

    public void exportarResultados(PrintCapturer out) {
        double totalGasto = sorteiosFeitos * precoPorJogo;

        // Cabeçalho
        out.println("🎰 Simulação da Mega-Sena");
        out.println("Jogo escolhido: " + jogo);
        out.println("Total de sorteios realizados: " + sorteiosFeitos);
        out.println("Total gasto: R$ " + String.format("%.2f", totalGasto));
        out.println("Sena: " + sena);
        out.println("Quinas: " + quinas);
        out.println("Quadras: " + quadras);
        out.println("Total ganho: R$ " + String.format("%.2f", valorGanho));
        out.println("Lucro/prejuízo: R$ " + String.format("%.2f", valorGanho - totalGasto));

        // Todos os sorteios
        out.println("\n📋 Todos os sorteios:");
        for (String linha : historicoCompleto) {
            out.println(linha);
        }

        // Análise final
        out.println("\n📊 ANÁLISE DOS RESULTADOS");

        // Frequência das dezenas
        int maisFrequente = 1;
        int menosFrequente = 1;
        for (int i = 2; i <= 60; i++) {
            if (contagemDezenas[i] > contagemDezenas[maisFrequente]) {
                maisFrequente = i;
            }
            if (contagemDezenas[i] < contagemDezenas[menosFrequente]) {
                menosFrequente = i;
            }
        }
        out.println("\n🔢 Dezena que mais saiu: " + maisFrequente + " (" + contagemDezenas[maisFrequente] + "x)");
        out.println("🔢 Dezena que menos saiu: " + menosFrequente + " (" + contagemDezenas[menosFrequente] + "x)");

        // Combinações repetidas
        Map<String, Integer> mapaCombinacoes = new HashMap<>();
        for (String linha : historicoCompleto) {
            String combinacao = linha.split(" - ")[0].trim().replaceAll("#\\d+", "").trim();
            mapaCombinacoes.put(combinacao, mapaCombinacoes.getOrDefault(combinacao, 0) + 1);
        }

        boolean houveRepeticao = false;
        for (int valor : mapaCombinacoes.values()) {
            if (valor > 1) {
                houveRepeticao = true;
                break;
            }
        }
        out.println("\n📁 Combinações repetidas:");
        out.println(houveRepeticao ? "Alguma combinação se repetiu." : "Nenhuma combinação se repetiu.");

        // Sorteios consecutivos iguais
        boolean consecutivosIguais = false;
        for (int i = 1; i < historicoCompleto.size(); i++) {
            String anterior = historicoCompleto.get(i - 1).split(" - ")[0];
            String atual = historicoCompleto.get(i).split(" - ")[0];
            if (anterior.equals(atual)) {
                consecutivosIguais = true;
                break;
            }
        }
        out.println("\n🔁 Houve sorteios consecutivos iguais? " + (consecutivosIguais ? "SIM" : "NÃO"));

        // Pares e ímpares
        out.println("\n⚖ Números pares e ímpares sorteados:");
        out.println("Total pares:   " + totalPares);
        out.println("Total ímpares: " + totalImpares);
    }
}