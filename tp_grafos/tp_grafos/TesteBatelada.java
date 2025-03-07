import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TesteBatelada {
    private static final int[] TAMANHOS = { 10, 100, 1000, 10000, 100000 };
    private static final int REPETICOES = 30; // Número de repetições para cada teste
    private static final long LIMITE_TEMPO = 5 * 60 * 1000; // 5 minutos

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resultados_teste.txt"))) {

            // Cria um pool de threads com o número de processadores disponíveis
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for (int tamanho : TAMANHOS) {
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

                // Contadores de execuções que estouraram o tempo limite
                int estourouTempoVerificarBiconectividade = 0;
                int estourouTempoFindJoints = 0;
                int estourouTempoTarjan = 0;

                List<Future<Long>> futuresVerificarBiconectividade = new ArrayList<>();
                List<Future<Long>> futuresFindJoints = new ArrayList<>();
                List<Future<Long>> futuresTarjan = new ArrayList<>();

                // Executa os testes para o tamanho atual utilizando o número de repetições e
                // threads
                for (int i = 0; i < REPETICOES; i++) {
                    TarjanArticulation grafo = new TarjanArticulation(tamanho);

                    futuresVerificarBiconectividade
                            .add(executor.submit(() -> registrarTempo(() -> grafo.verificarTodosPares())));
                    futuresFindJoints.add(executor.submit(() -> registrarTempo(() -> grafo.acharArticulacoes())));
                    futuresTarjan.add(executor.submit(() -> registrarTempo(() -> grafo.tarjan())));
                }

                // Processa os resultados das execuções de verificarBiconectividadeGrafo
                for (Future<Long> future : futuresVerificarBiconectividade) {
                    try {
                        long tempo = future.get(LIMITE_TEMPO, TimeUnit.MILLISECONDS);
                        if (tempo > 0) {
                            tempoTotalVerificarBiconectividade += tempo;
                            maiorVerificarBiconectividade = Math.max(maiorVerificarBiconectividade, tempo);
                            menorVerificarBiconectividade = Math.min(menorVerificarBiconectividade, tempo);
                            execucoesSucessoVerificarBiconectividade++;
                        }
                    } catch (TimeoutException e) {
                        estourouTempoVerificarBiconectividade++;
                    } catch (Exception e) {
                    }
                }

                // Processa os resultados das execuções de findJoints
                for (Future<Long> future : futuresFindJoints) {
                    try {
                        long tempo = future.get(LIMITE_TEMPO, TimeUnit.MILLISECONDS);
                        if (tempo > 0) {
                            tempoTotalFindJoints += tempo;
                            maiorFindJoints = Math.max(maiorFindJoints, tempo);
                            menorFindJoints = Math.min(menorFindJoints, tempo);
                            execucoesSucessoFindJoints++;
                        }
                    } catch (TimeoutException e) {
                        estourouTempoFindJoints++;
                    } catch (Exception e) {
                    }
                }

                // Processa os resultados das execuções de encontrarPontosDeArticulacao
                for (Future<Long> future : futuresTarjan) {
                    try {
                        long tempo = future.get(LIMITE_TEMPO, TimeUnit.MILLISECONDS);
                        if (tempo >= 0) { // Aqui permitimos explicitamente tempos 0 sem ignorá-los
                            tempoTotalTarjan += tempo;
                            maiorTarjan = Math.max(maiorTarjan, tempo);
                            menorTarjan = Math.min(menorTarjan, tempo);
                            execucoesSucessoTarjan++;
                        }
                    } catch (TimeoutException e) {
                        estourouTempoTarjan++;
                    } catch (Exception e) {
                        // Ignorando a exceção propositalmente
                    }
                }
                if (menorTarjan == Long.MAX_VALUE) {
                    menorTarjan = 0;
                }
                if (maiorTarjan == Long.MIN_VALUE) {
                    maiorTarjan = 0;
                }
                // Calcula as médias apenas se houve execuções bem-sucedidas
                long mediaVerificarBiconectividade = execucoesSucessoVerificarBiconectividade > 0
                        ? tempoTotalVerificarBiconectividade / execucoesSucessoVerificarBiconectividade
                        : 0;
                long mediaFindJoints = execucoesSucessoFindJoints > 0
                        ? tempoTotalFindJoints / execucoesSucessoFindJoints
                        : 0;
                long mediaTarjan = execucoesSucessoTarjan > 0 ? tempoTotalTarjan / execucoesSucessoTarjan : 0;

                // Escreve as métricas no arquivo
                writer.write("Resultados para grafo de tamanho: " + tamanho + "\n");

                writer.write("Métricas para Caminhos Disjuntos:\n");
                writer.write("Execução mais demorada: " + maiorVerificarBiconectividade + " ms\n");
                writer.write("Execução menos demorada: " + menorVerificarBiconectividade + " ms\n");
                writer.write("Tempo médio: " + mediaVerificarBiconectividade + " ms\n");
                writer.write("Execuções que estouraram o tempo: " + estourouTempoVerificarBiconectividade + "\n\n");

                writer.write("Métricas para Remover Vertices:\n");
                writer.write("Execução mais demorada: " + maiorFindJoints + " ms\n");
                writer.write("Execução menos demorada: " + menorFindJoints + " ms\n");
                writer.write("Tempo médio: " + mediaFindJoints + " ms\n");
                writer.write("Execuções que estouraram o tempo: " + estourouTempoFindJoints + "\n\n");

                writer.write("Métricas para Tarjan:\n");
                writer.write("Execução mais demorada: " + maiorTarjan + " ms\n");
                writer.write("Execução menos demorada: " + menorTarjan + " ms\n");
                writer.write("Tempo médio: " + mediaTarjan + " ms\n");
                writer.write("Execuções que estouraram o tempo: " + estourouTempoTarjan + "\n\n");
            }

            executor.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para medir o tempo de execução de uma tarefa e retornar o
     * tempo gasto.
     */
    private static long registrarTempo(Runnable tarefa) {
        long inicio = System.currentTimeMillis();
        try {
            tarefa.run();
        } catch (Exception e) {
            return -1; // Indica falha
        }
        long duracao = System.currentTimeMillis() - inicio;

        // Se o tempo registrado for 0 ms, retorna explicitamente 0
        return duracao > 0 ? duracao : 0;
    }
}
