import java.util.List;
import java.util.ArrayList;

public class TrabalhoPratico1 {

    public static List<Integer> findJoints(GrafoInterativo grafo){
        
        GrafoInterativo grafoClone = grafo.clone();
        List<Integer> articulacoes = new ArrayList<>();
        for (int i = 1; i < grafoClone.getNumVertices()+1; i++){
            grafoClone.removeVertice(i);
            boolean verificador = grafoClone.ehConexo(i);
            //System.out.println(verificador);
            if (verificador == false){
                articulacoes.add(i);
            }
            grafoClone = grafo.clone();
        }
        
        return articulacoes;
    }
    public static void main(String[] args){

        //! Lembrar de mudar o path antes de enviar
        String path = "C:\\Users\\joao_\\Desktop\\Grafos\\graph-test-100-1.txt";

        int num = 100;
        GrafoInterativo grafo = new GrafoInterativo(num);  
        List<Integer> articulacoes = findJoints(grafo);
        System.out.println(articulacoes);
        
    }
}
