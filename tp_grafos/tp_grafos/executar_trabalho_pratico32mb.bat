@echo off
REM Aumenta o tamanho da stack para 32 MB, remove arquivos .class, compila todos os arquivos Java e executa TesteBatelada

REM Exibir os passos no console
echo Removendo arquivos .class...
REM Verificar se há arquivos .class antes de tentar removê-los
if exist *.class (
    del *.class
    echo Arquivos .class removidos.
) else (
    echo Nenhum arquivo .class encontrado.
)

echo Compilando arquivos Java...
REM Compilar todos os arquivos .java no diretório
javac *.java

REM Verificar se a compilação foi bem-sucedida
if %ERRORLEVEL% neq 0 (
    echo Erro na compilação dos arquivos Java.
    pause
    exit /b %ERRORLEVEL%
) else (
    echo Compilação bem-sucedida.
)

REM Definir o CLASSPATH para o diretório atual (pasta com os arquivos .class)
set CLASSPATH=.

echo Executando TesteBatelada...
REM Executar o arquivo TesteBatelada com a stack aumentada para 32 MB
java -Xss32m TesteBatelada

REM Pausar para evitar fechamento automático
pause
