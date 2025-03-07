import java.io.*;
import java.util.*;

public class GrafoBruto implements Runnable {
    private int numVertices; 
    private int numArestas; 
    private int k; 
    private int[][] matrizAdj; 

    public GrafoBruto() {
        this.numVertices = 0;
        this.numArestas = 0;
        this.k = 0;
    }

    public GrafoBruto(String as) throws IOException {
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
                if (linha.isEmpty())
                    continue; 
                String[] partes = linha.split("\\s+");
                int u = Integer.parseInt(partes[0]);
                int v = Integer.parseInt(partes[1]);
                int peso = Integer.parseInt(partes[2]);

                matrizAdj[u][v] = peso;
                matrizAdj[v][u] = peso;
            }
        }
    }

    public void imprimirMatrizAdj() {
        System.out.println("Matriz de Adjacência:");
        for (int i = 1; i <= numVertices; i++) {
            for (int j = 1; j <= numVertices; j++) {
                if (matrizAdj[i][j] == Integer.MAX_VALUE) {
                    System.out.print("INF ");
                } else {
                    System.out.print(matrizAdj[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    @Override
    public void run() {
        bruteForceKCenter();
    }

    public void bruteForceKCenter() {
        long startTime = System.nanoTime();
        if (k <= 0 || numVertices == 0) {
            // System.out.println("Número de centros ou vértices inválido.");
            return;
        }

        int[] combination = new int[k];
        for (int i = 0; i < k; i++) {
            combination[i] = i + 1;
        }

        int menorRaio = Integer.MAX_VALUE;
        int[] melhorComb = new int[k];

        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Thread GrafoBruto interrompida.");
                return; // Ou saia do método de forma limpa
            }
            // Distâncias mínimas para os centros atuais
            int[] distMin = new int[numVertices + 1];
            Arrays.fill(distMin, Integer.MAX_VALUE);

            for (int centro : combination) {
                for (int i = 1; i <= numVertices; i++) {
                    distMin[i] = Math.min(distMin[i], matrizAdj[centro][i]);
                }
            }

            int raioAtual = 0;
            for (int i = 1; i <= numVertices; i++) {
                if (distMin[i] != Integer.MAX_VALUE) { 
                    raioAtual = Math.max(raioAtual, distMin[i]);
                }

            }

            // System.out.println("Distâncias mínimas para centros " +
            // Arrays.toString(combination) + ": " + Arrays.toString(distMin));
            // System.out.println(" Raio da combinação atual: " + raioAtual);

            if (raioAtual < menorRaio) {
                menorRaio = raioAtual;
                System.arraycopy(combination, 0, melhorComb, 0, k);
            }
            int i;
            for (i = k - 1; i >= 0; i--) {
                if (combination[i] < numVertices - k + i + 1) {
                    combination[i]++;
                    for (int j = i + 1; j < k; j++) {
                        combination[j] = combination[j - 1] + 1;
                    }
                    break;
                }
            }

            if (i < 0)
                break;
        }
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000_000.0; 
        System.out.println("Tempo de execução Estrategia Bruta: " + duration + " segundos");
        System.out.println("Menor raio Bruto encontrado: " + menorRaio);
        // System.out.println("Melhor combinação de centros: " +
        // Arrays.toString(melhorComb));
    }

    public static void main(String[] args) {
        try {
            //testar um arquivo especifico execute aki 
            GrafoBruto grafo = new GrafoBruto();
            grafo.carregarGrafo("testes/pmed2.txt");
            grafo.imprimirMatrizAdj();
            grafo.bruteForceKCenter();
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}
