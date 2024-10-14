package tp_grafos;

import javax.xml.crypto.Data;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.foreign.MemoryLayout;
import java.util.*;

public class TarjanArticulation extends GrafoInterativo implements Runnable {
    private int[] disc; // Tempo de descoberta
    private int[] minimo;  // Menor tempo alcançável
    private boolean[] articulacoes;// Pontos de articulaçao
    private Integer time;
    private Method method;
    private final ArrayList<DataSource> data;

    public enum Method {
        PARES,
        ARTICULACOES,
        TARJAN,
    }

    public ArrayList<DataSource> getData() {
        return data;
    }

    public class DataSource {
        private final Long initalTime;
        private final Long finalTime;
        private final Integer graphSize;
        private final Method method;

        public DataSource(Long initalTime, Long finalTime, Integer graphSize, Method method) {
            this.initalTime = initalTime;
            this.finalTime = finalTime;
            this.method = method;
            this.graphSize = graphSize;
        }

        public void writeData(BufferedWriter writer) throws IOException {
            String method = "";
            Long totalTime = finalTime - initalTime;
            switch (this.method) {
                case PARES:
                    method = "Caminhos disjuntos";
                    break;

                case ARTICULACOES:
                    method = "Remover vertices";
                    break;

                case TARJAN:
                    method = "Tarjan";
                    break;
            }

            writer.write(method + "(Tamanho do grafo: %d)".formatted(graphSize) + ": " + totalTime + "\n");
        }
    }

    public TarjanArticulation(int numVertices) {
        super(numVertices);
        disc = new int[numVertices + 1];
        minimo = new int[numVertices + 1];
        articulacoes = new boolean[numVertices + 1];
        time = 0;
        data = new ArrayList<>();
    }

    public void setMethod(Method m) {
        method = m;
    }

    @Override
    public void run() {
        long initialTime = System.currentTimeMillis();
        Method actualMethod = method;
        switch (actualMethod ) {
            case PARES:
                verificarTodosPares();
                break;
            case ARTICULACOES:
                acharArticulacoes();
                break;

            case TARJAN:
                tarjan();
                break;
        }
        long finalTime = System.currentTimeMillis();
        data.add(new DataSource(initialTime, finalTime, getNumVertices(), actualMethod ));
    }
    /**
     * Método de Tarjan para encontrar pontos de articulação
     * @param return printa os pontos de articulação
     */
    public void tarjan() {
        Arrays.fill(disc, -1);
        Arrays.fill(minimo, -1);
        Arrays.fill(articulacoes, false);

        for (int u = 1; u <= getNumVertices(); u++) {
            if (disc[u] == -1) {
                dfs(u, -1);
            }
        }
        //PRINT REMOVIDO PARA NAO IMPACTAR O TEMPO DE EXECUCAO
        // imprimirPontosDeArticulacao();
    }

    private void dfs(int u, int parent) {
        disc[u] = minimo[u] = ++time;
        int children = 0;

        for (int v : getSucessoresDiretos(u)) {
            if (v == parent) {
                continue;
            }

            if (disc[v] == -1) {
                children++;
                dfs(v, u);
                minimo[u] = Math.min(minimo[u], minimo[v]);

                // Se u nao eh raiz e minimo[v] >= disc[u] logo u eh um ponto de articulaçao
                if (parent != -1 && minimo[v] >= disc[u]) {
                    articulacoes[u] = true;
                }
            } else {
                // Atualiza minimo[u] se v já foi visitado e nao eh o pai
                minimo[u] = Math.min(minimo[u], disc[v]);
            }
        }
        // Se u eh a raiz e tem mais de um filho, logo u eh um ponto de articulaçao
        if (parent == -1 && children > 1) {
            articulacoes[u] = true;
        }
    }

    public void imprimirPontosDeArticulacao() {
        boolean temArticulacoes = false;
        System.out.println("Pelo Mehtodo de Tarjan:");
        System.out.println("Pontos de Articulaçao:");
        System.out.print("{ ");
        
        for (int u = 1; u <= getNumVertices(); u++) {
                        if (articulacoes[u]) {
                System.out.print(u + ", ");
                temArticulacoes = true;
            }
        }
        System.out.println("}");

        if (!temArticulacoes) {
            System.out.println("Nenhum ponto de articulaçao encontrado. O grafo eh biconectado.");
        }
    }

}
