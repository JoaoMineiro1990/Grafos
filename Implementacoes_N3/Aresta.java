public class Aresta {

    private int capacidade;
    private int fluxo;
    private Vertice origem;
    private Vertice destino;

    public Aresta(int capacidade,Vertice origem,Vertice destino) {
        this.origem = origem;
        this.destino = destino;
        this.capacidade = capacidade;
        this.fluxo = 0;
    }
    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }
    
    public void setFluxo(int fluxo) {
        this.fluxo = fluxo;
    }
    
    public Vertice getDestino() {
        return destino;
    }
    public Vertice getOrigem() {
        return origem;
    }
    public int getCapacidade() {
        return capacidade;
    }

    public int getFluxo() {
        return fluxo;
    }
}
