package Implementacoes_N1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa um grafo utilizando uma lista de adjacências.
 */
class Grafo{

    private List<List<Integer>> adjacentesLista;

    /**
        * Construtor para o grafo que inicializa com o numero de vertices
        * especificados pelo usuario
        * @param vertices Numero de vertices que contem o grafo
        */
    
        public Grafo(int vertices){
            adjacentesLista = new ArrayList<>();
            for(int i = 0 ; i < vertices ; i++){
                adjacentesLista.add(new ArrayList<>());
            }
        }
    /*
     * A partir daqui as variaveis terao remocao de -1 em seu valor
     * para se ajustarem ao indice 0
     */

    /**
     * Adicionar uma aresta direcionada da origem para o destino
     * @param origem origem da aresta .
     * @param destino destino da aresta.   
     */

        public void AdicionarAresta(int origem, int destino){
            int vOrigem = origem -1;
            int vDestino = destino -1;
            adjacentesLista.get(vOrigem).add(vDestino);
        }
    /**
     * retorna a lista de sucessores do vertice
     * @param selecionado vertice o qual se deseja a lista de sucessores
     * @return lista de sucessores do vertice
     */

        public List<Integer> getSucessores(int selecionado){
            int vSelecionado = selecionado -1;
            return new ArrayList<>(adjacentesLista.get(vSelecionado));
        }

    /**
     * funcao que retorna os predecessores do vertice selecionado
     * @param selecionado vertice o qual se deseja a lista de predecessores
     * @return lista de predecessores
     */
        public List<Integer> getPredecessores(int selecionado){
            int vSelecionado = selecionado -1;
            List<Integer> predecessores = new ArrayList<>();
            for(int i = 0 ; i < adjacentesLista.size() ; i++){
                if(adjacentesLista.get(i).contains(vSelecionado)){
                    predecessores.add(i);
                }
            }
            return predecessores;
        }
    /**
     * funcao que retorna o grau de entrada do vertice selecionado
     * @param vertice o qual deseja-se obter o grau de entrada
     * @return valor inteiro que representa o grau de entranda
     */
        public int getEntrada(int vertice){
            int vVertice = vertice -1;
            return adjacentesLista.get(vVertice).size();
        }

    /**
     * funcao que retorna o grau de saida do vertice selecionado
     * @param vertice vertice o qual deseja-se obter o grau de saida
     * @return valor inteiro que representa o grau de saida
     */
        public int getSaida(int vertice){
            return getPredecessores(vertice).size();
        }
}


public class fila_flexivel {

    /**
     * funcao que recebe o caminho do arquivo e retorna o grafo
     * @param caminho do arquivo a ser aberto
     * @param numVertices do grafo
     * @return grafo com todos os valores do arquivo
     * @throws Exception erro desconhecido ao abrir o arquivo
     */

    public static Grafo criarGrafo(int numVertices, String caminho){

        Grafo grafo = new Grafo(numVertices);
        try(BufferedReader leitura = new BufferedReader(new FileReader(caminho))){
            String linha;
            /*
             * Pulando o cabecalho
             */
            linha = leitura.readLine();
            while((linha = leitura.readLine()) != null){
                String[] separado = linha.split("\s+");
                int origem = Integer.parseInt(separado[1]);
                int destino = Integer.parseInt(separado[2]);
                grafo.AdicionarAresta(origem,destino);
            }
        }
        catch (IOException e) {
           System.out.println("Erro ao abrir o arquivo");;
        }
        return grafo;

    }


    public static void main(String[] args){
        int numVertices = 50000;
        String caminho = "C:\\Users\\joao_\\Desktop\\Grafos\\graph-test-50000-1.txt";
        Grafo grafo = criarGrafo(numVertices,caminho);
        
        int vertice = 50000;
        int grau_saida = grafo.getSaida(vertice);
        int grau_entrada = grafo.getEntrada(vertice);
        System.out.println("O vertice " + vertice + " possui:\n" +
                   "(i) grau de saída: " + grau_saida + "\n" +
                   "(ii) grau de entrada: " + grau_entrada + "\n" +
                   "(iii) conjunto de sucessores: " + grafo.getSucessores(vertice) + "\n" +
                   "(iv) conjunto de predecessores: " + grafo.getPredecessores(vertice));

    }
    
}
