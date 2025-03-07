import java.io.*;
import java.util.*;

public class GrafoGuloso implements Runnable{
    private int numVertices;       
    private int numArestas;        
    private int k;                 
    private int[][] matrizAdj;     // Matriz de adjacência

    // Construtor
    public GrafoGuloso() {
        this.numVertices = 0;
        this.numArestas = 0;
        this.k = 0;
    }

    // Construtor
    public GrafoGuloso(String as) throws IOException {
        carregarGrafo(as);
    }

    public void carregarGrafo(String nomeArquivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
    
            do {
                linha = br.readLine();
            } while (linha != null && linha.trim().isEmpty());
    
            if (linha == null) {
                throw new IOException("Arquivo vazio ou inválido.");
            }
    
            String[] primeiraLinha = linha.trim().split("\\s+");
            this.numVertices = Integer.parseInt(primeiraLinha[0]);
            this.numArestas = Integer.parseInt(primeiraLinha[1]);
            this.k = Integer.parseInt(primeiraLinha[2]);
    
            // Inicializa a matriz de adjacência
            matrizAdj = new int[numVertices + 1][numVertices + 1];
            for (int i = 1; i <= numVertices; i++) {
                Arrays.fill(matrizAdj[i], Integer.MAX_VALUE);
                matrizAdj[i][i] = 0; 
            }
    
            // Lê as arestas
            while ((linha = br.readLine()) != null) {

                linha = linha.trim();
                if (linha.isEmpty()) continue;
    
                String[] partes = linha.split("\\s+");
                int u = Integer.parseInt(partes[0]);
                int v = Integer.parseInt(partes[1]);
                int peso = Integer.parseInt(partes[2]);
    
                matrizAdj[u][v] = peso;
                matrizAdj[v][u] = peso; 
            }
        }
    }
    
    //Imprimir
    public void imprimirMatrizAdj() {
        System.out.println("Matriz:");
        for (int i = 1; i <= numVertices; i++) {
            for (int j = 1; j <= numVertices; j++) {
                if (matrizAdj[i][j] == Integer.MAX_VALUE) {
                    System.out.print("INF ");
                } else {
                    System.out.print(matrizAdj[i][j] + " ");
                }
            }
        }
    }
    @Override
    public void run() {
        greedyKCenter();
    }

    public void greedyKCenter() {
        long startTime = System.nanoTime();
        if (k <= 0 || numVertices == 0) {
            //System.out.println("Número inválido");
            return;
        }
    
        List<Integer> centros = new ArrayList<>();
        boolean[] isCentro = new boolean[numVertices + 1];
    
        // Escolhe um centro inicial aleatório
        int primeiroCentro = 1;
        centros.add(primeiroCentro);
        isCentro[primeiroCentro] = true;
        //System.out.println("Centro escolhido: " + primeiroCentro);
    
        // próximos centros
        for (int i = 1; i < k; i++) {
            int maisDistante = -1;
            int maiorDistancia = -1;
    
            // Busca o vértice mais distante de qualquer centro atual
            for (int v = 1; v <= numVertices; v++) {

                if (isCentro[v]){
                    continue;
                } 
    
                int menorDistancia = Integer.MAX_VALUE;
                for (int centro : centros) {
                    if (matrizAdj[centro][v] != Integer.MAX_VALUE) {
                        menorDistancia = Math.min(menorDistancia, matrizAdj[centro][v]);
                    }
                }

                if (menorDistancia < Integer.MAX_VALUE && menorDistancia > maiorDistancia) {
                    maiorDistancia = menorDistancia;
                    maisDistante = v;
                }
            }
  
            if (maisDistante == -1) {
                //System.out.println("Erro: não há mais vértices alcançáveis para escolher novos centros.");
                break;
            }
    
            centros.add(maisDistante);
            isCentro[maisDistante] = true;
            //System.out.println("Novo centro escolhido: " + maisDistante + " (Distância: " + maiorDistancia + ")");
        }
    
        // Raio final
        int raio = 0;
        for (int v = 1; v <= numVertices; v++) {

            if (!isCentro[v]) {
                int menorDistancia = Integer.MAX_VALUE;
                for (int centro : centros) {
                    if (matrizAdj[centro][v] != Integer.MAX_VALUE) { // Ignora caminhos "INF"
                        menorDistancia = Math.min(menorDistancia, matrizAdj[centro][v]);
                    }
                }
        
                if (menorDistancia < Integer.MAX_VALUE) {
                    raio = Math.max(raio, menorDistancia);
                }
            }
        }
        long endTime = System.nanoTime();

        // Calcula o tempo total de execução em segundos
        double duration = (endTime - startTime) / 1_000_000_000.0; // Tempo em segundos
        System.out.println("Tempo de execução Estrategia Gulosa: " + duration + " segundos");
        // System.out.println("Centros escolhidos: " + centros);
        System.out.println("Raio aproximado Guloso encontrado: " + raio);
    }
    
    public static void main(String[] args) {
        try {
            GrafoGuloso grafo = new GrafoGuloso();

            grafo.carregarGrafo("testes/pmed31.txt");
            grafo.imprimirMatrizAdj();
            grafo.greedyKCenter();
            
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}
