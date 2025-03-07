import java.io.IOException;

public class ExecutarTudo {
    public static void main(String[] args) throws IOException {
        String[] arquivos = {
            "testes/pmed1.txt",
            "testes/pmed2.txt",
            "testes/pmed3.txt",
            "testes/pmed4.txt",
            "testes/pmed5.txt",
            "testes/pmed6.txt",
            "testes/pmed7.txt",
            "testes/pmed8.txt",
            "testes/pmed9.txt",
            "testes/pmed10.txt",
            "testes/pmed11.txt",
            "testes/pmed12.txt",
            "testes/pmed13.txt",
            "testes/pmed14.txt",
            "testes/pmed15.txt",
            "testes/pmed16.txt",
            "testes/pmed17.txt",
            "testes/pmed18.txt",
            "testes/pmed19.txt",
            "testes/pmed20.txt",
            "testes/pmed21.txt",
            "testes/pmed22.txt",
            "testes/pmed23.txt",
            "testes/pmed24.txt",
            "testes/pmed25.txt",
            "testes/pmed26.txt",
            "testes/pmed27.txt",
            "testes/pmed28.txt",
            "testes/pmed29.txt",
            "testes/pmed30.txt",
            "testes/pmed31.txt",
            "testes/pmed32.txt",
            "testes/pmed33.txt",
            "testes/pmed34.txt",
            "testes/pmed35.txt",
            "testes/pmed36.txt",
            "testes/pmed37.txt",
            "testes/pmed38.txt",
            "testes/pmed39.txt",
            "testes/pmed40.txt"
        };
        int timeout = 300;
        for(String as : arquivos){
            Thread t1 = new Thread(new GrafoBruto(as));
            Thread t2 = new Thread(new GrafoGuloso(as));
            int timenow = (int) System.currentTimeMillis();
            t1.start();
            t2.start();
            while(t1.isAlive() || t2.isAlive()&& (int) System.currentTimeMillis() - timenow < timeout * 1000){
                if((int) System.currentTimeMillis() - timenow > timeout * 1000){
                    t1.interrupt();
                    t2.interrupt();
                    break;
                }
            }
        }
    }
}
