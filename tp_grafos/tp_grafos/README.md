Instruções de Execução:

O arquivo .bat realizará as seguintes operações:

Removerá arquivos .class de compilações anteriores para garantir uma compilação limpa.
Compilará todos os arquivos .java necessários para o projeto.
Definirá o ClassPATH para o diretório atual, assegurando que todas as classes compiladas sejam acessíveis corretamente.
Executará o código principal TesteBatelada com uma stack de 32 MB para evitar problemas de estouro de pilha durante os testes.
Importante: O arquivo .bat precisa estar dentro da pasta que contém os arquivos .java para que o processo ocorra sem erros.

Principais Funcionalidades do TesteBatelada.java:

Execução de Testes em Lote:

O arquivo realiza uma sequência de execuções de três métodos de verificação de biconectividade.
Cada teste é executado diversas vezes (30 vezes para cada tamanho de grafo).

Tamanhos de Grafos Avaliados:

Os testes são realizados em grafos de tamanhos variados (10, 100, 1.000, 10.000, 100.000 vértices).

Controle de Limites de Tempo:

Caso a execução de um teste exceda 5 minutos.

Para executar o codigo corretamente, utilize o executar_trabalho_pratico32mb.bat fornecido
