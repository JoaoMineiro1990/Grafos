import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Random;

class GrafoInterativo {
    
    // Classe para representar o tipo de aresta
    private Map<Integer, List<Integer>> adjList; // hashmap para armazenar os vértices e suas respectivas listas de adjacência
    private int[] TD; // Tempo de Descoberta
    private int[] TT; // Tempo de Término
    private int[] pai; // Array que guarda o "pai" de cada vértice na DFS

    // Contador para marcar o tempo de descoberta e término
    private int contador;

    // Lista que guarda as arestas e seus tipos
    private ArrayList<TypeAresta> arestas;

    /**
     * Construtor para o grafo que inicializa com o número de vértices
     * 
     * @param vertices especificados pelo usuário
     */

    public GrafoInterativo() {
        // Cria tudo zerado
        adjList = new HashMap<>();
        TD = new int[0];
        TT = new int[0];
        pai = new int[0];
        arestas = new ArrayList<>();
    }

    /**
     * Construtor para o grafo que inicializa com o número de vértices
     * 
     * @param vertices especificados pelo usuário
     */

    /**
     * Método que cria um grafo conexo com o número de vértices especificado
     * e adiciona arestas aleatórias até atingir a densidade desejada.
     * 
     * @param vertices Número de vértices do grafo
     */
    public GrafoInterativo(int vertices) {
        // Inicializa as estruturas de dados do grafo
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

        // Cria uma fila com todos os vértices e embaralha aleatoriamente
        List<Integer> lista = new ArrayList<>();
        for (int i = 1; i <= vertices; i++) {
            lista.add(i);
        }
        int count = 0;
        Random rand = new Random();
        Collections.shuffle(lista, rand);
        for (int v = 1; v < lista.size(); v++) {
            int vertice1 = lista.get(v);
            int vertice2 = lista.get(rand.nextInt(v)); // Escolhe um vértice aleatório entre 0 e v-1
            addAresta(vertice1, vertice2);
            count++;
        }
        // Densidade desejada de arestas
        int ArestasDesejadas = (int)(vertices* 2);
        int arestasFaltantes = ArestasDesejadas - count;

        // Adiciona arestas aleatórias até atingir a densidade desejada
        Random aleatorio = new Random();
        while (arestasFaltantes > 0) {
            int origem = aleatorio.nextInt(vertices) + 1;
            int destino = aleatorio.nextInt(vertices) + 1;

            // Verifica se a aresta não existe e não conecta um vértice a si mesmo
            if (!adjList.get(origem).contains(destino) && origem != destino) {
                addAresta(origem, destino);
                arestasFaltantes--;
            }
        }
    }

    /**
     * Construtor que recebe um PATH para um arquivo e cria um grafo a partir dele
     * 
     * @param numVertices Número de vértices do grafo
     * @param caminho     Caminho do arquivo
     */
    public GrafoInterativo(int numVertices, String caminho) {
        // Inicializa os atributos da instância
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
     * @param return cópia do grafo
     */
    @Override
    public GrafoInterativo clone() {
        // Cria uma nova instância de GrafoInterativo
        GrafoInterativo clone = new GrafoInterativo();

        // Clona a lista de adjacência (cópia profunda)
        clone.adjList = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : this.adjList.entrySet()) {
            // Clona cada lista de adjacência associada a cada vértice
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
    }

    /**
     * Método para clonar o grafo e remover a direção das arestas
     * 
     * @return cópia do grafo não direcionado
     */
    public GrafoInterativo CloneConexo() {
        // Cria uma nova instância de GrafoInterativo
        GrafoInterativo cloneConexo = new GrafoInterativo();

        // Inicializa a lista de adjacência do clone
        cloneConexo.adjList = new HashMap<>();
        for (int vertice : this.adjList.keySet()) {
            cloneConexo.adjList.put(vertice, new ArrayList<>());
        }

        // Percorre todas as arestas do grafo original para clonar sem direção
        for (Map.Entry<Integer, List<Integer>> entry : this.adjList.entrySet()) {
            int origem = entry.getKey();
            List<Integer> sucessores = entry.getValue();

            for (int destino : sucessores) {
                // Adiciona a aresta no sentido original
                if (!cloneConexo.adjList.get(origem).contains(destino)) {
                    cloneConexo.adjList.get(origem).add(destino);
                }
                // Adiciona a aresta no sentido oposto para tornar o grafo não direcionado
                if (!cloneConexo.adjList.get(destino).contains(origem)) {
                    cloneConexo.adjList.get(destino).add(origem);
                }
            }
        }

        // Clona os arrays de TD, TT e pai (mesmo tamanho e valores)
        cloneConexo.TD = Arrays.copyOf(this.TD, this.TD.length);
        cloneConexo.TT = Arrays.copyOf(this.TT, this.TT.length);
        cloneConexo.pai = Arrays.copyOf(this.pai, this.pai.length);

        // Clona o contador
        cloneConexo.contador = this.contador;


        return cloneConexo;
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

                // Se o tempo de descoberta for 0, ele ainda não foi visitado (arvore)
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
            // Se nao ha mais sucessores termina o processamento deste vértice
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
     * Retorna as arestas que contém o vértice especificado
     * 
     * @param vertice
     * @return arraylist de arestas
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
     * Verifica se o grafo é conexo
     * @return true ou false
     */
    public boolean ehConexo(int valor) {
        GrafoInterativo clone = CloneConexo();
        return clone._ehConexo(valor);
    }

    /**
     * Verifica se o grafo é conexo
     * 
     * @return true se o grafo é conexo, false caso contrário
     */
    private boolean _ehConexo(int valor) {

        if (adjList.isEmpty()) {
            return true;
        }

        // Inicia uma busca em profundidade a partir de um vértice presente na adjList
        int qualquerVertice = adjList.keySet().iterator().next();
        BP(qualquerVertice);
        for (int i = 0; i < TD.length; i++) {
            if(i == valor-1){continue;}
            if (TD[i] == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param vertice o qual se deseja a lista de sucessores
     * @return lista de sucessores diretos e indiretos do vertice
     */
    public List<Integer> getTodosSucessores(int vertice) {
        // Lista para armazenar todos os sucessores alcançados
        List<Integer> todosSucessores = new ArrayList<>();
        // Conjunto para manter o controle dos vértices visitados e evitar ciclos
        Set<Integer> visitados = new HashSet<>();
        // Fila para processar os vértices na ordem (utilizada para BFS)
        Queue<Integer> fila = new LinkedList<>();

        // Adiciona o vértice inicial à fila e ao conjunto de visitados
        fila.add(vertice);
        visitados.add(vertice);

        // Enquanto houver vértices a serem processados
        while (!fila.isEmpty()) {
            // Remove o vértice da fila
            int atual = fila.poll();
            // Adiciona o vértice atual à lista de todos os sucessores
            todosSucessores.add(atual);

            // Obtém a lista de sucessores do vértice atual
            List<Integer> sucessores = adjList.getOrDefault(atual, new ArrayList<>());

            // Para cada sucessor do vértice atual
            for (int sucessor : sucessores) {
                // Se o sucessor ainda não foi visitado
                if (!visitados.contains(sucessor)) {
                    // Marca como visitado e adiciona à fila para processamento
                    visitados.add(sucessor);
                    fila.add(sucessor);
                }
            }
        }
        return todosSucessores;
    }

}
