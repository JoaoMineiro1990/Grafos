import java.io.*;
import java.util.*;

class ProblemaTransporte {

    private int[] oferta;
    private int[] demanda;
    private int[][] custo;
    private int[][] transporte;

    public ProblemaTransporte(int[] oferta, int[] demanda, int[][] custo) {
        this.oferta = oferta;
        this.demanda = demanda;
        this.custo = custo;
        this.transporte = new int[oferta.length][demanda.length];
        verificarEBalancear();
    }

    private void verificarEBalancear() {
        int totalOferta = Arrays.stream(oferta).sum();
        int totalDemanda = Arrays.stream(demanda).sum();

        System.out.println("Total oferta: " + totalOferta + ", Total demanda: " + totalDemanda);

        if (totalOferta > totalDemanda) {
            System.out.println("Excesso de oferta: ");
            int diferenca = totalOferta - totalDemanda;
            demanda = Arrays.copyOf(demanda, demanda.length + 1);
            demanda[demanda.length - 1] = diferenca;

            for (int i = 0; i < custo.length; i++) {
                custo[i] = Arrays.copyOf(custo[i], custo[i].length + 1);
                custo[i][custo[i].length - 1] = 0;
            }
        } else if (totalDemanda > totalOferta) {
            System.out.println("Excesso de demanda:");
            int diferenca = totalDemanda - totalOferta;
            oferta = Arrays.copyOf(oferta, oferta.length + 1);
            oferta[oferta.length - 1] = diferenca;

            int[][] novoCusto = new int[custo.length + 1][custo[0].length];
            for (int i = 0; i < custo.length; i++) {
                System.arraycopy(custo[i], 0, novoCusto[i], 0, custo[i].length);
            }
            Arrays.fill(novoCusto[novoCusto.length - 1], 0);
            custo = novoCusto;
        }

        // Redimensionar a matriz de transporte
        int[][] novoTransporte = new int[oferta.length][demanda.length];
        for (int i = 0; i < transporte.length; i++) {
            System.arraycopy(transporte[i], 0, novoTransporte[i], 0, transporte[i].length);
        }
        transporte = novoTransporte;

        System.out.println("Matriz de transporte redimensionada para " + oferta.length + "x" + demanda.length);
    }

    public void resolverTransporte() {
        System.out.println("Iniciando");
        int[] ofertaRestante = Arrays.copyOf(oferta, oferta.length);
        int[] demandaRestante = Arrays.copyOf(demanda, demanda.length);

        while (true) {
            // Encontrar o menor custo
            int minCusto = Integer.MAX_VALUE;
            int minI = -1, minJ = -1;

            for (int i = 0; i < ofertaRestante.length; i++) {
                if (ofertaRestante[i] == 0) continue;
                for (int j = 0; j < demandaRestante.length; j++) {
                    if (demandaRestante[j] == 0) continue;
                    if (custo[i][j] < minCusto) {
                        minCusto = custo[i][j];
                        minI = i;
                        minJ = j;
                    }
                }
            }

            if (minI == -1 || minJ == -1) break; 
            int alocar = Math.min(ofertaRestante[minI], demandaRestante[minJ]);
            transporte[minI][minJ] = alocar;
            ofertaRestante[minI] -= alocar;
            demandaRestante[minJ] -= alocar;

            System.out.println("Alocando " + alocar + " unidades para (" + minI + ", " + minJ + ") com custo " + minCusto);
            System.out.println("Oferta restante: " + Arrays.toString(ofertaRestante));
            System.out.println("Demanda restante: " + Arrays.toString(demandaRestante));
        }

        System.out.println("Matriz de transporte final:");
        for (int[] linha : transporte) {
            System.out.println(Arrays.toString(linha));
        }
    }

    public int getCustoTotal() {
        int custoTotal = 0;
        for (int i = 0; i < transporte.length; i++) {
            for (int j = 0; j < transporte[0].length; j++) {
                custoTotal += transporte[i][j] * custo[i][j];
            }
        }
        return custoTotal;
    }

    public void imprimirMatrizTransporte() {
        System.out.println("Matriz de Transporte:");
        for (int[] linha : transporte) {
            for (int valor : linha) {
                System.out.print(valor + "\t");
            }
            System.out.println();
        }
    }
}

public class AmbienteDeTeste {

    private static ProblemaTransporte lerArquivo(String fileName) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String[] primeiraLinha = reader.readLine().trim().split("\\s+");
        int m = Integer.parseInt(primeiraLinha[0]);
        int n = Integer.parseInt(primeiraLinha[1]);

        System.out.println("Lendo arquivo: " + fileName);
        System.out.println("Dimensões: " + m + "x" + n);

        int[] oferta = new int[m];
        for (int i = 0; i < m; i++) {
            String line = reader.readLine();
            if (line == null) throw new IOException("Arquivo incompleto: faltam valores de oferta.");
            oferta[i] = Integer.parseInt(line.trim());
        }

        int[] demanda = new int[n];
        for (int i = 0; i < n; i++) {
            String line = reader.readLine();
            if (line == null) throw new IOException("Arquivo incompleto: faltam valores de demanda.");
            demanda[i] = Integer.parseInt(line.trim());
        }

        int[][] custo = new int[m][n];
        for (int i = 0; i < m; i++) {
            String line = reader.readLine();
            if (line == null) throw new IOException("Arquivo incompleto: faltam valores de custo.");
            String[] linhaCustos = line.trim().split("\\s+");
            if (linhaCustos.length != n) throw new IOException("Linha de custos mal formatada.");
            for (int j = 0; j < n; j++) {
                custo[i][j] = Integer.parseInt(linhaCustos[j]);
            }
        }
        reader.close();
        return new ProblemaTransporte(oferta, demanda, custo);
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o nome do arquivo contendo os dados do problema de transporte: ");
        String fileName = scanner.nextLine();

        try {
            ProblemaTransporte problema = lerArquivo(fileName);
            problema.resolverTransporte();
            System.out.println("Custo total ótimo: " + problema.getCustoTotal());
            problema.imprimirMatrizTransporte();

        } catch (IOException | NumberFormatException e) {
            System.out.println("Erro ao ler ou processar o arquivo: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

}
