import java.util.*;

public class TarjanStack {

    private GrafoInterativo grafo;// o outro grafo
    private int[] low; // low[u] é o menor valor de descoberta que pode ser alcançado a partir de u
    private boolean[] visited; // visited[u] é verdadeiro se u foi visitado
    private boolean[] articulationPoints; // articulationPoints[u] é verdadeiro se u é um ponto de articulação
    private int contador; // contador de tempo para TD e LOw

    //Construtor padrao;
    public TarjanStack(GrafoInterativo grafo) {
        this.grafo = grafo;
        int numVertices = grafo.getNumVertices();
        low = new int[numVertices + 1];
        visited = new boolean[numVertices + 1];
        articulationPoints = new boolean[numVertices + 1];
        contador = 0;
    }
    /**
     * Encontra os pontos de articulação do grafo
     * @return lista com pontos de articulação
     */

    public void encontrarPontosDeArticulacao() {
        Arrays.fill(grafo.getTD(), -1);// TD[u] é o tempo de descoberta de u
        Arrays.fill(low, -1);// low[u] é o menor valor de descoberta que pode ser alcançado a partir de u
        Arrays.fill(grafo.getPai(), -1);//  pai[u] é o pai de u no DFS

        //para todo vertice do grafo vai chamar a funcao iterativeDFS
        for (int i = 1; i <= grafo.getNumVertices(); i++) {
            if (grafo.getTD()[i - 1] == -1) {
                iterativeDFS(i);
            }
        }
    }

    private void iterativeDFS(int start) {
        Stack<Integer> stack = new Stack<>();// pilha para armazenar os vertices
        stack.push(start);// empilha o vertice inicial
        grafo.getPai()[start - 1] = -1;// pai do vertice inicial é -1
        int children = 0;// contador de filhos do vertice

        while (!stack.isEmpty()) {
            int u = stack.peek();

            if (!visited[u]) {
                visited[u] = true;
                grafo.getTD()[u - 1] = low[u] = ++contador;
            }

            boolean todosFilhosProcessados = true;

            for (int v : grafo.getSucessoresDiretos(u)) {
                if (!visited[v]) {
                    stack.push(v);
                    grafo.getPai()[v - 1] = u;
                    todosFilhosProcessados = false;
                    children++;
                    break;
                } else if (v != grafo.getPai()[u - 1]) {
                    low[u] = Math.min(low[u], grafo.getTD()[v - 1]);//atualiza o low do vertice u
                }
            }

            if (todosFilhosProcessados) {
                stack.pop();
                int p = grafo.getPai()[u - 1];

                if (p == -1 && children > 1) // se u é raiz e tem mais de um filho
                {
                    articulationPoints[u] = true;// se u é raiz e tem mais de um filho, então u é ponto de articulação
                }

                if (p != -1 && !articulationPoints[p])
                {
                    low[p] = Math.min(low[p], low[u]);// atualiza o low do pai de u
                    if (low[u] >= grafo.getTD()[p - 1]) {
                        articulationPoints[p] = true;// se u não é raiz e low[u] >= TD[p], então p é ponto de articulação
                    }
                }
            }
        }
    }

    public void imprimirPontosDeArticulacao() {
        encontrarPontosDeArticulacao(); 
        boolean hasArticulationPoints = false;
        System.out.println("{articulacoes}:");
    
        for (int i = 1; i < articulationPoints.length; i++) {
            if (articulationPoints[i]) {
                System.out.println("vertice " + i);
                hasArticulationPoints = true;
            }
        }
    
        if (!hasArticulationPoints) {
            System.out.println("Nenhum ponto de articulação encontrado");
        }
    }
}
