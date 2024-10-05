import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class GrafoInterativo implements Runnable, Cloneable {

    // Classe para representar o tipo de aresta
    private Map<Integer, List<Integer>> adjList; // Lista de adjacência
    private int[] TD; // Tempo de Descoberta
    private int[] TT; // Tempo de Término
    private int[] pai; // Pai na DFS

    // Contador para marcar o tempo de descoberta e término
    private int contador;

    // Lista que guarda as arestas e seus tipos
    private ArrayList<TypeAresta> arestas;

    //Construtor padrão.
    public GrafoInterativo() {
        adjList = new HashMap<>();
        TD = new int[0];
        TT = new int[0];
        pai = new int[0];
        arestas = new ArrayList<>();
    }

    /**
     * Construtor para criar um grafo conexo com arestas aleatórias.
     *
     * @param vertices Número de vértices do grafo.
     */
    public GrafoInterativo(int vertices) {
        adjList = new HashMap<>();
        TD = new int[vertices];
        TT = new int[vertices];
        pai = new int[vertices];
        arestas = new ArrayList<>();
        contador = 0;

        // Inicializa a lista de adjacência para cada vértice
        for (int i = 1; i <= vertices; i++) {
            adjList.put(i, new ArrayList<>());
        }

        // Cria uma árvore geradora aleatória para garantir conectividade
        List<Integer> lista = new ArrayList<>();
        for (int i = 1; i <= vertices; i++) {
            lista.add(i);
        }

        Collections.shuffle(lista, new Random());
        for (int v = 1; v < lista.size(); v++) {
            int vertice1 = lista.get(v);
            int vertice2 = lista.get(new Random().nextInt(v)); // Escolhe um vértice aleatório entre 0 e v-1
            addAresta(vertice1, vertice2);
            addAresta(vertice2, vertice1);
        }

        // Define a densidade desejada de arestas
        int arestasDesejadas = (int) (vertices *1.2);
        int count = adjList.size() - 1; // Número de arestas já adicionadas
        int arestasFaltantes = arestasDesejadas - count;

        // Adiciona arestas aleatórias até atingir a densidade desejada
        Random aleatorio = new Random();
        while (arestasFaltantes > 0) {
            int origem = aleatorio.nextInt(vertices) + 1;
            int destino = aleatorio.nextInt(vertices) + 1;

            // Verifica se a aresta não existe e não conecta um vértice a si mesmo
            if (!adjList.get(origem).contains(destino) && origem != destino) {
                addAresta(origem, destino);
                addAresta(destino, origem);
                arestasFaltantes--;
            }
        }
    }

    /**
     * Construtor que recebe um PATH para um arquivo e cria um grafo a partir dele.
     *
     * @param numVertices Número de vértices do grafo.
     * @param caminho     Caminho do arquivo.
     */
    public GrafoInterativo(int numVertices, String caminho) {
        adjList = new HashMap<>();
        TD = new int[numVertices];
        TT = new int[numVertices];
        pai = new int[numVertices];
        arestas = new ArrayList<>();
        contador = 0;

        // Inicializar a lista de adjacência para cada vértice
        for (int i = 1; i <= numVertices; i++) {
            adjList.put(i, new ArrayList<>());
        }

        // Tenta ler o arquivo e adicionar as arestas ao grafo
        try (BufferedReader leitura = new BufferedReader(new FileReader(caminho))) {
            String linha;
            // Pulando o cabeçalho
            leitura.readLine();
            while ((linha = leitura.readLine()) != null) {
                String[] separado = linha.split("\\s+");
                int origem = Integer.parseInt(separado[1]);
                int destino = Integer.parseInt(separado[2]);
                addAresta(origem, destino);
            }
        } catch (IOException e) {
            System.out.println("Erro ao abrir o arquivo: " + e.getMessage());
        }
    }

    public int getNumVertices() {
        return adjList.size();
    }

    public void removeVertice(int vertice) {
        adjList.remove(vertice);

        // Remove referências ao vértice em todas as outras listas de adjacência
        for (List<Integer> sucessores : adjList.values()) {
            sucessores.remove(Integer.valueOf(vertice));
        }
    }

    /**
     * Método para clonar o grafo
     *
     * @return cópia do grafo
     */
    @Override
    public GrafoInterativo clone() {
        try {
            // Cria uma nova instância de GrafoInterativo
            GrafoInterativo clone = (GrafoInterativo) super.clone();
    
            // Clona a lista de adjacência (cópia profunda)
            clone.adjList = new HashMap<>();
            for (Map.Entry<Integer, List<Integer>> entry : this.adjList.entrySet()) {
                clone.adjList.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
    
            // Clona os arrays de TD, TT e pai
            clone.TD = Arrays.copyOf(this.TD, this.TD.length);
            clone.TT = Arrays.copyOf(this.TT, this.TT.length);
            clone.pai = Arrays.copyOf(this.pai, this.pai.length);
    
            // Clona o contador
            clone.contador = this.contador;
    
            // Clona a lista de arestas (cópia profunda)
            clone.arestas = new ArrayList<>();
            for (TypeAresta aresta : this.arestas) {
                clone.arestas.add(new TypeAresta(aresta.origem, aresta.destino, aresta.classificacao));
            }
    
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Isto não deveria acontecer, já que estamos clonando um GrafoInterativo
        }
    }

    /**
     * Método para printar o grafo
     */
    void printGrafo() {
        for (Map.Entry<Integer, List<Integer>> entry : adjList.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            for (Integer i : entry.getValue()) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    // Adiciona uma aresta de u para v
    public void addAresta(int u, int v) {
        adjList.get(u).add(v);
    }

    /**
     * Função que realiza a busca em profundidade em ordem lexicográfica
     */
    public void BP() {
        // Inicializa o contador e os arrays de descoberta, término e pais com 0
        contador = 0;
        Arrays.fill(TD, 0);
        Arrays.fill(TT, 0);
        Arrays.fill(pai, 0);

        // Percorre todos os vértices do grafo que ainda não foram visitados
        for (int i = 0; i < TD.length; i++) {
            if (TD[i] == 0) {
                BP(i + 1);
            }
        }
    }

    private void BP(int raiz) {
        // Cria uma nova pilha para simulação da recursão e adiciona a raiz
        Stack<Integer> stack = new Stack<>();
        stack.push(raiz);
        contador++;

        // HashMap para armazenar as arestas que já foram visitadas
        HashMap<String, Boolean> visitado = new HashMap<>();

        TD[raiz - 1] = contador;

        // Loop para percorrer todos os vértices da pilha
        while (!stack.isEmpty()) {
            boolean descoberto = false;

            // Pega o vértice atual do topo da pilha
            int atual = stack.peek();

            // Pega a lista de sucessores do vértice atual
            List<Integer> sucessores = adjList.get(atual);

            // Ordena a lista de sucessores para garantir ordem lexicográfica
            Collections.sort(sucessores);

            // Loop para percorrer todos os sucessores do vértice atual
            for (Integer vertice : sucessores) {
                // Para cada sucessor, verifica se ele já foi visitado
                String edge = atual + " " + vertice;
                if (visitado.containsKey(edge)) {
                    continue;
                } else {
                    // Marca no HashMap `visitado` que a aresta foi visitada
                    visitado.put(edge, true);
                }

                // Se o tempo de descoberta for 0, ele ainda não foi visitado (árvore)
                if (TD[vertice - 1] == 0) {
                    // Define `atual` como pai do `vertice`
                    pai[vertice - 1] = atual;

                    // Adiciona ao atributo `arestas` o tipo desta aresta
                    arestas.add(new TypeAresta(atual, vertice, "Arvore"));

                    // Empilha o vértice e incrementa o contador
                    stack.push(vertice);
                    contador++;

                    // Marca o tempo de descoberta do vértice
                    TD[vertice - 1] = contador;
                    descoberto = true;
                    break;
                } else {
                    // Classifica a aresta
                    if (TT[vertice - 1] == 0) {
                        arestas.add(new TypeAresta(atual, vertice, "Retorno"));
                    } else if (TD[atual - 1] < TD[vertice - 1]) {
                        arestas.add(new TypeAresta(atual, vertice, "Avanco"));
                    } else {
                        arestas.add(new TypeAresta(atual, vertice, "Cruzamento"));
                    }
                }
            }
            // Se não há mais sucessores, termina o processamento deste vértice
            if (!descoberto) {
                contador++;
                TT[atual - 1] = contador;
                stack.pop();
            }
        }
    }

    // Método para obter as arestas classificadas
    public ArrayList<TypeAresta> getArestas() {
        return arestas;
    }

    /**
     * Retorna as arestas que contêm o vértice especificado
     *
     * @param vertice Vértice a ser verificado.
     * @return Lista de arestas que contêm o vértice.
     */
    public ArrayList<TypeAresta> getArestasConjunto(int vertice) {
        ArrayList<TypeAresta> arestasConjunto = new ArrayList<>();

        for (TypeAresta aresta : arestas) {
            if (aresta.origem == vertice || aresta.destino == vertice) {
                arestasConjunto.add(aresta);
            }
        }

        return arestasConjunto;
    }

    /**
     * Verifica se o grafo é conexo.
     *
     * @param valor Vértice a ser ignorado na verificação (não utilizado aqui).
     * @return true se o grafo é conexo, false caso contrário.
     */
    public boolean ehConexo(int valor) {
        GrafoInterativo clone = clone();
        return clone._ehConexo(valor);
    }

    /**
     * Método auxiliar para verificar conectividade.
     *
     * @param valor Vértice a ser ignorado na verificação.
     * @return true se o grafo é conexo após remover o vértice especificado, false caso contrário.
     */
    private boolean _ehConexo(int valor) {
        if (adjList.isEmpty()) {
            return true;
        }

        // Inicia uma busca em profundidade a partir de um vértice presente na adjList
        int qualquerVertice = adjList.keySet().iterator().next();
        BP(qualquerVertice);
        for (int i = 0; i < TD.length; i++) {
            if (i == valor - 1) {
                continue;
            }
            if (TD[i] == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retorna todos os sucessores de um vértice.
     *
     * @param vertice Vértice cujo sucessores são retornados.
     * @return Lista de sucessores diretos do vértice.
     */
    public List<Integer> getSucessoresDiretos(int vertice) {
        return adjList.getOrDefault(vertice, Collections.emptyList());
    }

    /**
     * Verifica se o grafo está conectado.
     *
     * @return true se estiver conectado, false caso contrário.
     */
    public boolean isConnected() {
        int numVertices = getNumVertices();
        if (numVertices == 0 || numVertices == 1) {
            return true;
        }

        boolean[] visited = new boolean[numVertices + 1];
        Queue<Integer> queue = new LinkedList<>();

        // Encontra o primeiro vértice com pelo menos uma aresta
        int start = 1;
        while (start <= numVertices && getSucessoresDiretos(start).isEmpty()) {
            start++;
        }

        if (start > numVertices) {
            // Grafo sem arestas (todos vértices isolados)
            return numVertices <= 1;
        }

        // BFS a partir do vértice start
        queue.add(start);
        visited[start] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : getSucessoresDiretos(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }

        // Verifica se todos os vértices com arestas foram visitados
        for (int u = 1; u <= numVertices; u++) {
            if (!getSucessoresDiretos(u).isEmpty() && !visited[u]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verifica se o grafo é biconectado.
     *
     * @return true se o grafo for biconectado, false caso contrário.
     */
    public boolean isBiconnected() {
        // Verifica se o grafo está conectado
        if (!isConnected()) {
            System.out.println("O grafo não está conectado.");
            return false;
        }

        int numVertices = getNumVertices();
        // Itera sobre todos os pares de vértices
        for (int origem = 1; origem <= numVertices; origem++) {
            for (int destino = origem + 1; destino <= numVertices; destino++) {
                if (!hasTwoInternallyDisjointPaths(origem, destino)) {
                    System.out.println("Entre os vértices " + origem + " e " + destino + ", não existem dois caminhos internamente disjuntos.");
                    return false;
                }
            }
        }

        System.out.println("O grafo é biconectado.");
        return true;
    }

    /**
     * Verifica se existem dois caminhos internamente disjuntos entre origem e destino.
     *
     * @param origem  Vértice de origem.
     * @param destino Vértice de destino.
     * @return true se existirem dois caminhos internamente disjuntos, false caso contrário.
     */
    private boolean hasTwoInternallyDisjointPaths(int origem, int destino) {
        // Encontra o primeiro caminho
        List<Integer> path1 = findPath(origem, destino, null);
        if (path1.isEmpty()) {
            // Nenhum caminho existe
            return false;
        }

        // Bloqueia os vértices internos do primeiro caminho (excluindo origem e destino)
        Set<Integer> blocked = new HashSet<>(path1);
        blocked.remove(origem);
        blocked.remove(destino);

        // Encontra o segundo caminho ignorando os vértices bloqueados
        List<Integer> path2 = findPath(origem, destino, blocked);

        return !path2.isEmpty();
    }

    /**
     * Encontra um caminho entre origem e destino, ignorando certos vértices.
     *
     * @param origem   Vértice de origem.
     * @param destino  Vértice de destino.
     * @param blocked  Conjunto de vértices a serem ignorados (vértices bloqueados).
     * @return Lista representando o caminho encontrado. Lista vazia se nenhum caminho for encontrado.
     */
    private List<Integer> findPath(int origem, int destino, Set<Integer> blocked) {
        Stack<Integer> stack = new Stack<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> parent = new HashMap<>();

        stack.push(origem);
        visited.add(origem);

        while (!stack.isEmpty()) {
            int current = stack.pop();

            if (current == destino) {
                // Reconstruir o caminho
                List<Integer> path = new ArrayList<>();
                int node = destino;
                while (node != origem) {
                    path.add(node);
                    node = parent.get(node);
                    if (node == -1) { // Caminho não encontrado
                        return new ArrayList<>();
                    }
                }
                path.add(origem);
                Collections.reverse(path);
                return path;
            }

            for (int neighbor : getSucessoresDiretos(current)) {
                if ((blocked == null || !blocked.contains(neighbor)) && !visited.contains(neighbor)) {
                    stack.push(neighbor);
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                }
            }
        }

        // Nenhum caminho encontrado
        return new ArrayList<>();
    }

    /**
     * Verifica a biconectividade do grafo utilizando a abordagem de dois caminhos internamente disjuntos.
     */
    @Override
    public void run() {
        verificarBiconectividadeGrafo();
        findJoints();
    }
    public void verificarBiconectividadeGrafo() {
        boolean biconectado = true;
        List<String> paresNaoBiconectados = new ArrayList<>();
        
        int numVertices = getNumVertices();
        
        // Itera sobre todos os pares de vértices
        for (int origem = 1; origem <= numVertices; origem++) {
            for (int destino = origem + 1; destino <= numVertices; destino++) {
                if (!hasTwoInternallyDisjointPaths(origem, destino)) {
                    biconectado = false;
                    paresNaoBiconectados.add(origem + " - " + destino);
                }
            }
        }
        // System.out.println("Pelo metodo de verificar 2 caminhos internamente disjuntos:");
        // // Reporta os resultados
        // if (biconectado) {
        //     System.out.println("O grafo é biconectado.");
        // } else {
        //     System.out.println("O grafo NÃO é biconectado.");
        // }
    }
    public List<Integer> findJoints() {
        GrafoInterativo clone = this.clone();  // Clona o grafo uma única vez
        List<Integer> articulacoes = new ArrayList<>();
        
        for (int i = 1; i <= clone.getNumVertices(); i++) {
            GrafoInterativo grafoClone = clone.clone();  // Clona a cada iteração, mas de forma controlada
            grafoClone.removeVertice(i);
            
            boolean verificador = grafoClone.ehConexo(i);
    
            if (!verificador) {
                articulacoes.add(i);  // Adiciona o vértice atual à lista de articulações
            }
        }
        
        return articulacoes;  // Retorna a lista de articulações
    }
    
}
