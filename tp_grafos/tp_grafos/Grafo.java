package tp_grafos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe que representa um grafo utilizando uma lista de adjacências.
 */

public class Grafo {

    private List<List<Integer>> adjacentesLista;

    /**
     * Construtor de grafos, vertices tem q ser fornecidos pelo usuario
     * aretas sao randomicas
     * 
     * @param vertices numero de vertices do grafo
     * @return grafo aleatorio
     */
    public Grafo(int vertices) {
        adjacentesLista = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjacentesLista.add(new ArrayList<>());
        }
        criarGrafoComFila();
        int desidade_desejada = 399996;
        int num_arestas = 0;
        for (List<Integer> adjacentes : adjacentesLista) {
           num_arestas += adjacentes.size();
        }
        int arestas_faltante = desidade_desejada - num_arestas;
        Random Aleatorio = new Random();

        for (int i = 0; i < arestas_faltante; i++) {
            int origem = Aleatorio.nextInt(vertices) + 1;
            int destino = Aleatorio.nextInt(vertices) + 1;
            if (!getSucessores(origem).contains(destino)) {
                AdicionarAresta(origem, destino);
            }
        }
    }

    /**
     * Funcao para salvar origem de destino das arestas em um txt
     * 
     * @param nomeArquivo de destino
     */
    public void salvarArestasEmArquivo(String nomeArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            // Cabeçalho do arquivo
            writer.write("Origem  Destino\n");

            // Itera sobre todos os vértices e suas arestas
            for (int i = 0; i < adjacentesLista.size(); i++) {
                for (int destino : adjacentesLista.get(i)) {
                    // Como internamente os índices são baseados em 0, soma 1 para mostrar os
                    // números corretos de vértices
                    writer.write((i + 1) + "            " + (destino + 1) + "\n");
                }
            }

            writer.flush(); // Garante que os dados sejam gravados no arquivo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adicionar uma aresta direcionada da origem para o destino
     * 
     * @param origem  origem da aresta .
     * @param destino destino da aresta.
     */

    public void AdicionarAresta(int origem, int destino) {
        int vOrigem = origem - 1;
        int vDestino = destino - 1;
        adjacentesLista.get(vOrigem).add(vDestino);
    }

    /**
     * retorna a lista de sucessores do vertice
     * 
     * @param selecionado vertice o qual se deseja a lista de sucessores
     * @return lista de sucessores do vertice
     */

    public List<Integer> getSucessores(int selecionado) {
        int vSelecionado = selecionado - 1;
        return new ArrayList<>(adjacentesLista.get(vSelecionado));
    }

    /**
     * funcao que retorna os predecessores do vertice selecionado
     * 
     * @param selecionado vertice o qual se deseja a lista de predecessores
     * @return lista de predecessores
     */
    public List<Integer> getPredecessores(int selecionado) {
        int vSelecionado = selecionado - 1;
        List<Integer> predecessores = new ArrayList<>();
        for (int i = 0; i < adjacentesLista.size(); i++) {
            if (adjacentesLista.get(i).contains(vSelecionado)) {
                predecessores.add(i);
            }
        }
        return predecessores;
    }

    /**
     * funcao que retorna o grau de entrada do vertice selecionado
     * 
     * @param vertice o qual deseja-se obter o grau de entrada
     * @return valor inteiro que representa o grau de entranda
     */
    public int getEntrada(int vertice) {
        int vVertice = vertice - 1;
        return adjacentesLista.get(vVertice).size();
    }

    /**
     * funcao que retorna o grau de saida do vertice selecionado
     * 
     * @param vertice vertice o qual deseja-se obter o grau de saida
     * @return valor inteiro que representa o grau de saida
     */
    public int getSaida(int vertice) {
        return getPredecessores(vertice).size();
    }

    /**
     * Método que cria e conecta o grafo a partir de uma fila embaralhada
     */
    public void criarGrafoComFila() {
        // Cria uma fila com inteiros de 2 a 100001
        Queue<Integer> fila = new LinkedList<>();
        for (int i = 2; i <= 100001; i++) {
            fila.add(i);
        }

        // Embaralha a fila
        List<Integer> listaParaEmbaralhar = new ArrayList<>(fila);
        Collections.shuffle(listaParaEmbaralhar);
        fila.clear();
        fila.addAll(listaParaEmbaralhar);

        int verticeAtual = 1;

        // Adiciona vértices ao grafo conectando ao verticeAtual
        while (!fila.isEmpty()) {
            int proximoVertice = fila.poll();
            AdicionarAresta(verticeAtual, proximoVertice);
            verticeAtual = proximoVertice;
        }
    }

    public static void main(String[] args) {
        Grafo grafo = new Grafo(100001); 
        grafo.salvarArestasEmArquivo("arestas.txt");
    }
}
