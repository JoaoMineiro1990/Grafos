package tp_grafos;

import javax.xml.crypto.Data;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TesteBatelada {
    private static final int[] TAMANHOS = {10, 100, 1000};
    private static final int REPETICOES = 30; // Número de repetições para cada teste
    private static final long LIMITE_TEMPO = 1000 * 5 * 60; // 5 minutos

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultados_teste.txt"))) {
            for (int tamanho : TAMANHOS) {
                Thread[] threads = new Thread[REPETICOES * 3];
                ArrayList<ArrayList<TarjanArticulation.DataSource>> compiledData = new ArrayList<>();
                for (int i = 0; i < REPETICOES * 3; i += 3) {
                    TarjanArticulation grafo = new TarjanArticulation(tamanho);

                    compiledData.add(grafo.getData());

                    grafo.setMethod(TarjanArticulation.Method.PARES);
                    threads[i] = new Thread(grafo);
                    threads[i].start();

                    grafo.setMethod(TarjanArticulation.Method.ARTICULACOES);
                    threads[i + 1] = new Thread(grafo);
                    threads[i + 1].start();

                    grafo.setMethod(TarjanArticulation.Method.TARJAN);
                    threads[i + 2] = new Thread(grafo);
                    threads[i + 2].start();
                }

                for (Thread t : threads) {
                    try {
                        t.join(LIMITE_TEMPO);
                    } catch (InterruptedException e) {
                        System.out.printf("Erro (thread: %s): %s\n", t.getName(), e.getMessage());
                    }
                }

                for (ArrayList<TarjanArticulation.DataSource> d : compiledData) {
                    for (TarjanArticulation.DataSource s : d) {
                        s.writeData(writer);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para medir o tempo de execução de uma tarefa e retornar o
     * tempo gasto.
     */
    private static long registrarTempo(Runnable tarefa) {
        long inicio = System.currentTimeMillis();
        try {
            tarefa.run();
        } catch (Exception e) {
            return -1; // Indica falha
        }
        long duracao = System.currentTimeMillis() - inicio;

        // Se o tempo registrado for 0 ms, retorna explicitamente 0
        return duracao > 0 ? duracao : 0;
    }
}
