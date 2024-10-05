import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TesteBatelada {

    // private static final int[] TAMANHOS = {100, 1000, 10000, 100000}; // Tamanhos grandes para teste posterior
    private static final int[] TAMANHOS = {10,100,1000}; // Tamanhos para teste
    private static final int REPETICOES = 30; // Número de repetições para cada teste
    private static final long LIMITE_TEMPO = 4 * 60 * 1000; // 4 minutos em milissegundos

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultados_teste.txt"))) {
            // Redireciona a saída padrão para o arquivo
            PrintStream fileOut = new PrintStream("resultados_teste.txt");
            PrintStream consoleErr = System.err; // Mantém a saída de erro para o console
            System.setOut(fileOut); // Redireciona todos os prints normais para o arquivo

            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for (int tamanho : TAMANHOS) {
                System.out.println("Resultados para grafo de tamanho: " + tamanho);

                // Métricas para cada método
                long tempoTotalVerificarBiconectividade = 0;
                long tempoTotalFindJoints = 0;
                long tempoTotalTarjan = 0;

                long maiorVerificarBiconectividade = Long.MIN_VALUE;
                long menorVerificarBiconectividade = Long.MAX_VALUE;

                long maiorFindJoints = Long.MIN_VALUE;
                long menorFindJoints = Long.MAX_VALUE;

                long maiorTarjan = Long.MIN_VALUE;
                long menorTarjan = Long.MAX_VALUE;

                int execucoesSucessoVerificarBiconectividade = 0;
                int execucoesSucessoFindJoints = 0;
                int execucoesSucessoTarjan = 0;

                List<Future<Long>> futuresVerificarBiconectividade = new ArrayList<>();
                List<Future<Long>> futuresFindJoints = new ArrayList<>();
                List<Future<Long>> futuresTarjan = new ArrayList<>();

                // Submete todas as 30 execuções de cada método
                for (int i = 0; i < REPETICOES; i++) {
                    TarjanArticulation grafo = new TarjanArticulation(tamanho);

                    // Submete a execução de verificarBiconectividadeGrafo
                    futuresVerificarBiconectividade.add(executor.submit(() -> registrarTempo(() -> grafo.verificarBiconectividadeGrafo())));

                    // Submete a execução de findJoints
                    futuresFindJoints.add(executor.submit(() -> registrarTempo(() -> grafo.findJoints())));

                    // Submete a execução de encontrarPontosDeArticulacao
                    futuresTarjan.add(executor.submit(() -> registrarTempo(() -> grafo.encontrarPontosDeArticulacao())));
                }

                // Processa os resultados das execuções de verificarBiconectividadeGrafo
                for (Future<Long> future : futuresVerificarBiconectividade) {
                    try {
                        long tempo = future.get(LIMITE_TEMPO, TimeUnit.MILLISECONDS);
                        tempoTotalVerificarBiconectividade += tempo;
                        maiorVerificarBiconectividade = Math.max(maiorVerificarBiconectividade, tempo);
                        menorVerificarBiconectividade = Math.min(menorVerificarBiconectividade, tempo);
                        execucoesSucessoVerificarBiconectividade++;
                    } catch (Exception e) {
                        consoleErr.println("Erro ou tempo excedido em verificarBiconectividadeGrafo");
                        e.printStackTrace(consoleErr);
                    }
                }

                // Processa os resultados das execuções de findJoints
                for (Future<Long> future : futuresFindJoints) {
                    try {
                        long tempo = future.get(LIMITE_TEMPO, TimeUnit.MILLISECONDS);
                        tempoTotalFindJoints += tempo;
                        maiorFindJoints = Math.max(maiorFindJoints, tempo);
                        menorFindJoints = Math.min(menorFindJoints, tempo);
                        execucoesSucessoFindJoints++;
                    } catch (Exception e) {
                        consoleErr.println("Erro ou tempo excedido em findJoints");
                        e.printStackTrace(consoleErr);
                    }
                }

                // Processa os resultados das execuções de encontrarPontosDeArticulacao
                for (Future<Long> future : futuresTarjan) {
                    try {
                        long tempo = future.get(LIMITE_TEMPO, TimeUnit.MILLISECONDS);
                        tempoTotalTarjan += tempo;
                        maiorTarjan = Math.max(maiorTarjan, tempo);
                        menorTarjan = Math.min(menorTarjan, tempo);
                        execucoesSucessoTarjan++;
                    } catch (Exception e) {
                        consoleErr.println("Erro ou tempo excedido em encontrarPontosDeArticulacao");
                        e.printStackTrace(consoleErr);
                    }
                }

                // Calcula as médias apenas se houve execuções bem-sucedidas
                long mediaVerificarBiconectividade = execucoesSucessoVerificarBiconectividade > 0 ?
                        tempoTotalVerificarBiconectividade / execucoesSucessoVerificarBiconectividade : 0;
                long mediaFindJoints = execucoesSucessoFindJoints > 0 ?
                        tempoTotalFindJoints / execucoesSucessoFindJoints : 0;
                long mediaTarjan = execucoesSucessoTarjan > 0 ?
                        tempoTotalTarjan / execucoesSucessoTarjan : 0;

                // Exibe as métricas finais para verificarBiconectividadeGrafo
                System.out.println("Métricas para verificarBiconectividadeGrafo:");
                System.out.println("Tempo total: " + tempoTotalVerificarBiconectividade + " ms");
                System.out.println("Execução mais demorada: " + maiorVerificarBiconectividade + " ms");
                System.out.println("Execução menos demorada: " + menorVerificarBiconectividade + " ms");
                System.out.println("Tempo médio: " + mediaVerificarBiconectividade + " ms\n");

                // Exibe as métricas finais para findJoints
                System.out.println("Métricas para findJoints:");
                System.out.println("Tempo total: " + tempoTotalFindJoints + " ms");
                System.out.println("Execução mais demorada: " + maiorFindJoints + " ms");
                System.out.println("Execução menos demorada: " + menorFindJoints + " ms");
                System.out.println("Tempo médio: " + mediaFindJoints + " ms\n");

                // Exibe as métricas finais para encontrarPontosDeArticulacao
                System.out.println("Métricas para encontrarPontosDeArticulacao:");
                System.out.println("Tempo total: " + tempoTotalTarjan + " ms");
                System.out.println("Execução mais demorada: " + maiorTarjan + " ms");
                System.out.println("Execução menos demorada: " + menorTarjan + " ms");
                System.out.println("Tempo médio: " + mediaTarjan + " ms\n");
            }

            fileOut.close(); // Fecha o stream de saída para o arquivo
            executor.shutdown(); // Fecha o executor
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para medir o tempo de execução de uma tarefa e retornar o tempo gasto.
     */
    private static long registrarTempo(Runnable tarefa) {
        long inicio = System.currentTimeMillis();
        tarefa.run();
        return System.currentTimeMillis() - inicio;
    }
}
