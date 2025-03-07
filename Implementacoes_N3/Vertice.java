import java.util.List;
import java.util.ArrayList;

public class Vertice {

    private List<Aresta> todArestas = new ArrayList<Aresta>();

    public List<Aresta> getTodArestas() {
        return todArestas;
    }
    public void addAresta(Vertice destino, int capacidade) {
        Aresta aresta = new Aresta(capacidade, this, destino);
        todArestas.add(aresta);
    }
}
