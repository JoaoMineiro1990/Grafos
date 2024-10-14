package tp_grafos;

import java.util.*;

public class TarjanStack {

    private GrafoInterativo grafo;
    private int[] disc;
    private int[] low;
    private boolean[] articulationPoints;
    private int time;

    public TarjanStack(GrafoInterativo grafo) {
        this.grafo = grafo;
        int numVertices = grafo.getNumVertices();
        disc = new int[numVertices + 1];
        low = new int[numVertices + 1];
        articulationPoints = new boolean[numVertices + 1];
        time = 0;
    }

    public void encontrarPontosDeArticulacao() {
        Arrays.fill(disc, -1);
        Arrays.fill(low, -1);
        Arrays.fill(articulationPoints, false);

        for (int u = 1; u <= grafo.getNumVertices(); u++) {
            if (disc[u] == -1) {
                iterativeDFS(u);
            }
        }

        imprimirPontosDeArticulacao();
    }

    private void iterativeDFS(int start) {
        Stack<Frame> stack = new Stack<>();
        stack.push(new Frame(start, -1, 0));

        while (!stack.isEmpty()) {
            Frame current = stack.peek();

            if (disc[current.u] == -1) {
                disc[current.u] = low[current.u] = ++time;
            }

            List<Integer> adj = grafo.getSucessoresDiretos(current.u);
            if (current.nextIndex < adj.size()) {
                int v = adj.get(current.nextIndex);
                stack.peek().nextIndex++;

                if (v == current.parent) {
                    continue;
                }

                if (disc[v] == -1) {
                    stack.push(new Frame(v, current.u, 0));
                } else {
                    low[current.u] = Math.min(low[current.u], disc[v]);
                }
            } else {
                stack.pop();
                if (current.parent != -1) {
                    Frame parentFrame = stack.peek();
                    low[parentFrame.u] = Math.min(low[parentFrame.u], low[current.u]);

                    if (low[current.u] >= disc[parentFrame.u]) {
                        articulationPoints[parentFrame.u] = true;
                    }
                } else {
                    // Raiz
                    int children = 0;
                    for (int v : grafo.getSucessoresDiretos(current.u)) {
                        if (disc[v] > disc[current.u]) {
                            children++;
                        }
                    }
                    if (children > 1) {
                        articulationPoints[current.u] = true;
                    }
                }
            }
        }
    }

    private class Frame {
        int u;
        int parent;
        int nextIndex;

        Frame(int u, int parent, int nextIndex) {
            this.u = u;
            this.parent = parent;
            this.nextIndex = nextIndex;
        }
    }

    public void imprimirPontosDeArticulacao() {
        boolean hasArticulationPoints = false;
        System.out.println("Pontos de Articulação:");

        for (int u = 1; u <= grafo.getNumVertices(); u++) {
            if (articulationPoints[u]) {
                System.out.println("Vértice " + u);
                hasArticulationPoints = true;
            }
        }

        if (!hasArticulationPoints) {
            System.out.println("Nenhum ponto de articulação encontrado. O grafo é biconectado.");
        }
    }

    public boolean isBiconectado() {
        for (int u = 1; u <= grafo.getNumVertices(); u++) {
            if (articulationPoints[u]) {
                return false;
            }
        }
        return true;
    }
}
