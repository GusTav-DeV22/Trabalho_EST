package ads.poo.est;
import java.util.*;


    public class AnaliseMegaSena {

        private List<Integer> jogo;

        public AnaliseMegaSena(List<Integer> dezenasJogadas) {
            if (dezenasJogadas.size() < 6 || dezenasJogadas.size() > 20) {
                throw new IllegalArgumentException("Você deve escolher entre 6 e 20 dezenas.");
            }

            // Elimina duplicadas e ordena
            this.jogo = new ArrayList<>(new TreeSet<>(dezenasJogadas));
        }

        public int contarAcertos(List<Integer> sorteio) {
            int acertos = 0;
            for (int dezena : jogo) {
                if (sorteio.contains(dezena)) {
                    acertos++;
                }
            }
            return acertos;
        }

        public void analisarResultado(List<Integer> sorteio) {
            int acertos = contarAcertos(sorteio);

            System.out.println("Seu jogo: " + jogo);
            System.out.println("Sorteio : " + sorteio);
            System.out.println("Você acertou " + acertos + " dezenas.");

            switch (acertos) {
                case 6:
                    System.out.println(">>> Parabéns! Acertou a SENA!");
                    break;
                case 5:
                    System.out.println(">>> Parabéns! Acertou a QUINA!");
                    break;
                case 4:
                    System.out.println(">>> Parabéns! Acertou a QUADRA!");
                    break;
                default:
                    System.out.println(">>> Não houve premiação.");
            }
        }

        public static void simularJogosVariados() {
            Sorteio sorteio = new Sorteio();
            List<Integer> resultado = sorteio.getNumerosSorteados();

            System.out.println("\nResultado do sorteio:");
            sorteio.imprimirSorteio();
            System.out.println("\n--- Analisando diferentes tamanhos de jogos ---");

            for (int tamanho = 6; tamanho <= 15; tamanho++) {
                List<Integer> jogoAleatorio = gerarJogoAleatorio(tamanho);
                AnaliseMegaSena analise = new AnaliseMegaSena(jogoAleatorio);
                System.out.println("\n[Jogo com " + tamanho + " dezenas]");
                analise.analisarResultado(resultado);
            }
        }

        private static List<Integer> gerarJogoAleatorio(int tamanho) {
            List<Integer> dezenas = new ArrayList<>();
            for (int i = 1; i <= 60; i++) {
                dezenas.add(i);
            }
            Collections.shuffle(dezenas);
            return new ArrayList<>(dezenas.subList(0, tamanho));
        }
    }

