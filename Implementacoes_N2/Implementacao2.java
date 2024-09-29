//package Implementacoes_N2;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Implementacao2 {

    public static Grafo criarGrafo(int numVertices, String caminho) {
        Grafo grafo = new Grafo(numVertices);

        try (BufferedReader leitura = new BufferedReader(new FileReader(caminho))) {
            String linha;
            // Pulando o cabeçalho
            linha = leitura.readLine();
            while ((linha = leitura.readLine()) != null) {
                String[] separado = linha.split("\\s+");
                int origem = Integer.parseInt(separado[1]);
                int destino = Integer.parseInt(separado[2]);
                grafo.addAresta(origem, destino);
            }
        } catch (IOException e) {
            System.out.println("Erro ao abrir o arquivo: " + e.getMessage());
        }
        return grafo;
    }

    public static void main(String[] args) {
        int numVertices = 50000;
        String caminho = "C:\\Users\\joao_\\Desktop\\Grafos\\graph-test-50000-1.txt";
        Grafo grafo = criarGrafo(numVertices, caminho);
        grafo.BP();

        int aresta = 120;
        for (TypeAresta edge : grafo.getArestasConjunto(aresta)) {
            System.out.println(edge);
        }
    }
}

class Grafo {

    // hashmap para armazenar os vértices e suas respectivas listas de adjacência
    private Map<Integer, List<Integer>> adjList;
    private int[] TD; // Tempo de Descoberta
    private int[] TT; // Tempo de Término
    private int[] pai; // Array que guarda o "pai" de cada vértice na DFS

    // Contador para marcar o tempo de descoberta e término
    private int contador;

    // Lista que guarda as arestas e seus tipos
    private ArrayList<TypeAresta> arestas;

    /**
     * Construtor para o grafo que inicializa com o número de vértices
     * @param vertices especificados pelo usuário
     */
    public Grafo(int vertices) {
        // Preenche o HashMap com as listas de adjacência
        adjList = new HashMap<>();
        for (int i = 1; i <= vertices; i++) {
            adjList.put(i, new ArrayList<>());
        }

        // Inicializa os arrays de tempo de descoberta, término, pais e arestas
        TD = new int[vertices];
        TT = new int[vertices];
        pai = new int[vertices];
        arestas = new ArrayList<>();
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
}

class TypeAresta {
    int origem;
    int destino;
    String classificacao;

    public TypeAresta(int origem, int destino, String classificacao) {
        this.origem = origem;
        this.destino = destino;
        this.classificacao = classificacao;
    }

    @Override
    public String toString() {
        return origem + " -> " + destino + " (" + classificacao + ")";
    }
}


