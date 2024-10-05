@echo off
REM Aumenta o tamanho da stack para 16m, remove arquivos .class, compila todos os arquivos Java e executa TrabalhoPratico1

REM Definir o diretório onde os arquivos .java estão localizados
cd /d "C:\Users\joao_\Desktop\Grafos\tp_grafos\tp_grafos"

REM Verificar se há arquivos .class antes de tentar removê-los
if exist *.class (
    del *.class
    echo Arquivos .class removidos.
) else (
    echo Nenhum arquivo .class encontrado.
)

REM Compilar todos os arquivos .java no diretório
javac *.java

REM Verificar se a compilação foi bem-sucedida
if %ERRORLEVEL% neq 0 (
    echo Erro na compilação dos arquivos Java.
    pause
    exit /b %ERRORLEVEL%
)

REM Definir o CLASSPATH para o diretório atual (pasta com os arquivos .class)
set CLASSPATH=.

REM Executar o arquivo TrabalhoPratico1 com a stack aumentada para 16m
java -Xss16m TesteBatelada

REM Pausar para evitar fechamento automático
pause
