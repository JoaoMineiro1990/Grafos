import java.util.*;

public class TarjanArticulation extends GrafoInterativo {
    private int[] disc; // Tempo de descoberta
    private int[] low;  // Menor tempo alcançável
    private boolean[] articulationPoints;
    private int time;


    public TarjanArticulation(int numVertices) {
        super(numVertices);
        disc = new int[numVertices + 1];
        low = new int[numVertices + 1];
        articulationPoints = new boolean[numVertices + 1];
        time = 0;
    }
    @Override
    public void run() {
        encontrarPontosDeArticulacao();
    }
    public void encontrarPontosDeArticulacao() {
        Arrays.fill(disc, -1);
        Arrays.fill(low, -1);
        Arrays.fill(articulationPoints, false);

        for (int u = 1; u <= getNumVertices(); u++) {
            if (disc[u] == -1) {
                dfs(u, -1);
            }
        }

        imprimirPontosDeArticulacao();
    }

    private void dfs(int u, int parent) {
        disc[u] = low[u] = ++time;
        int children = 0;

        for (int v : getSucessoresDiretos(u)) {
            if (v == parent) {
                continue;
            }

            if (disc[v] == -1) {
                children++;
                dfs(v, u);
                low[u] = Math.min(low[u], low[v]);

                // Se u não é raiz e low[v] >= disc[u], então u é um ponto de articulação
                if (parent != -1 && low[v] >= disc[u]) {
                    articulationPoints[u] = true;
                }
            } else {
                // Atualiza low[u] se v já foi visitado e não é o pai
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        // Se u é a raiz e tem mais de um filho, então u é um ponto de articulação
        if (parent == -1 && children > 1) {
            articulationPoints[u] = true;
        }
    }

    public void imprimirPontosDeArticulacao() {
        // boolean hasArticulationPoints = false;
        // System.out.println("Pelo Método de Tarjan:");
        // System.out.println("Pontos de Articulação:");
        // System.out.print("{ ");
        
        // for (int u = 1; u <= grafo.getNumVertices(); u++) {
        //                 if (articulationPoints[u]) {
        //         System.out.print(u + ", ");
        //         hasArticulationPoints = true;
        //     }
        // }
        // System.out.println("}");

        // if (!hasArticulationPoints) {
        //     System.out.println("Nenhum ponto de articulação encontrado. O grafo é biconectado.");
        // }
    }

}
