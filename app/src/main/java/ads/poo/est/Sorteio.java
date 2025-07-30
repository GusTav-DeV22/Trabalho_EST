package ads.poo.est;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Sorteio {

    private List<Integer> dezenas;
    private List<Integer> dezenaSorteada;



    public Sorteio() {

        dezenas = new ArrayList<>();
        for (int i = 1; i <= 60; i++) {
            dezenas.add(i);

        }

        Collections.shuffle(dezenas, new Random());

        dezenaSorteada = new ArrayList<>(dezenas.subList(0, 6));

        Collections.sort(dezenaSorteada);
    }

        public List<Integer> getNumerosSorteados(){
        return new ArrayList<>(dezenaSorteada);
        }

        public void imprimirSorteio(){
            System.out.println("-MEGA-SENA");
            for(int numero : dezenaSorteada){
                System.out.println("("+numero+")");
            }
        }

}
