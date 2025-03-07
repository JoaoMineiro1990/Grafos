import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TesteCaminhosDisjuntos {
    public static void main(String[] args) throws IOException {
        String[] arquivos = {
            "grafo_10v_15e.txt", 
            "grafo_100v_400e.txt", 
            "grafo_1000v_3000e.txt", 
            "grafo_10000v_30000e.txt",
            "lattice_10x10.txt",
            "lattice_100x100.txt",
            "lattice_250x250.txt",
            "lattice_500x500.txt" 
        };

        int[][] testes = {
            {1, 10},    
            {1, 100},  
            {1, 1000}, 
            {1, 10000},
            {1, 100},   
            {1, 10000},
            {1, 62500}, 
            {1, 250000}
            
        };

        try (PrintWriter writer = new PrintWriter(new FileWriter("CaminhosDisjuntosResultados.txt"))) {
            writer.println("Arquivo, Origem, Destino, Caminhos Disjuntos, Tempo (ms)");
            
            for (int i = 0; i < arquivos.length; i++) {
                System.out.println("\nTeste com arquivo: " + arquivos[i]);
                Grafo grafo = new Grafo();
                grafo.carregarDeArquivo(arquivos[i]);

                int origem = testes[i][0];
                int destino = testes[i][1];

                long inicio = System.currentTimeMillis();
                int caminhosDisjuntos = grafo.calcularCaminhosDisjuntos(origem, destino);
                long tempoExecucao = System.currentTimeMillis() - inicio;

                writer.printf("%s, %d, %d, %d, %d\n", arquivos[i], origem, destino, caminhosDisjuntos, tempoExecucao);

                System.out.println("Número máximo de caminhos disjuntos: " + caminhosDisjuntos);
                System.out.println("Tempo de execução: " + tempoExecucao + " ms");
            }
        }

        System.out.println("Resultados salvos em 'CaminhosDisjuntosResultados.txt'.");
    }
}
