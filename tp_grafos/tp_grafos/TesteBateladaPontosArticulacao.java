import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TesteBateladaPontosArticulacao {

    private static final int[] TAMANHOS = {100000}; // Tamanhos para teste
    private static final int REPETICOES = 20; // Número de repetições para cada teste
    private static final long LIMITE_TEMPO = 5 * 60 * 1000; // 5 minutos em milissegundos

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultados_teste_pontos_articulacao.txt"))) {
            // Redireciona a saída padrão para o arquivo
            PrintStream fileOut = new PrintStream("resultados_teste_pontos_articulacao.txt");
            PrintStream consoleErr = System.err; // Mantém a saída de erro para o console
            System.setOut(fileOut); // Redireciona todos os prints normais para o arquivo

            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for (int tamanho : TAMANHOS) {
                System.out.println("Resultados para grafo de tamanho: " + tamanho);

                // Métricas para encontrarPontosDeArticulacao
                long tempoTotalTarjan = 0;
                long maiorTarjan = Long.MIN_VALUE;
                long menorTarjan = Long.MAX_VALUE;

                int execucoesSucessoTarjan = 0;

                // Contadores de erros
                int estourouTempoTarjan = 0;
                int erroStackTarjan = 0;

                List<Future<Long>> futuresTarjan = new ArrayList<>();

                // Submete todas as 20 execuções de encontrarPontosDeArticulacao
                for (int i = 0; i < REPETICOES; i++) {
                    TarjanArticulation grafo = new TarjanArticulation(tamanho);

                    // Submete a execução de encontrarPontosDeArticulacao
                    futuresTarjan.add(executor.submit(() -> registrarTempo(() -> grafo.findJoints())));
                }

                // Processa os resultados das execuções de encontrarPontosDeArticulacao
                for (Future<Long> future : futuresTarjan) {
                    try {
                        long tempo = future.get(LIMITE_TEMPO, TimeUnit.MILLISECONDS);
                        tempoTotalTarjan += tempo;
                        maiorTarjan = Math.max(maiorTarjan, tempo);
                        menorTarjan = Math.min(menorTarjan, tempo);
                        execucoesSucessoTarjan++;
                    } catch (TimeoutException e) {
                        estourouTempoTarjan++;
                        consoleErr.println("Tempo excedido em encontrarPontosDeArticulacao");
                    } catch (ExecutionException e) {
                        if (e.getCause() instanceof StackOverflowError) {
                            erroStackTarjan++;
                            consoleErr.println("Erro de StackOverflow em encontrarPontosDeArticulacao");
                        } else {
                            consoleErr.println("Erro desconhecido em encontrarPontosDeArticulacao");
                            e.printStackTrace(consoleErr);
                        }
                    } catch (Exception e) {
                        consoleErr.println("Erro em encontrarPontosDeArticulacao");
                        e.printStackTrace(consoleErr);
                    }
                }

                // Calcula as médias apenas se houve execuções bem-sucedidas
                long mediaTarjan = execucoesSucessoTarjan > 0 ? tempoTotalTarjan / execucoesSucessoTarjan : 0;

                // Exibe as métricas finais para encontrarPontosDeArticulacao
                System.out.println("Métricas para encontrarPontosDeArticulacao:");
                System.out.println("Tempo total: " + tempoTotalTarjan + " ms");
                System.out.println("Execução mais demorada: " + maiorTarjan + " ms");
                System.out.println("Execução menos demorada: " + menorTarjan + " ms");
                System.out.println("Tempo médio: " + mediaTarjan + " ms");
                System.out.println("Execuções que estouraram o tempo: " + estourouTempoTarjan);
                System.out.println("Execuções com erro de StackOverflow: " + erroStackTarjan + "\n");
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
