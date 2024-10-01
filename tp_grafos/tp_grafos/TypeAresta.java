
class TypeAresta {
    
    int origem;
    int destino;
    String classificacao;
    
    public TypeAresta(int origem, int destino, String classificacao) {
        this.origem = origem;
        this.destino = destino;
        this.classificacao = classificacao;
    }

    @Override
    public String toString() {
        return origem + " -> " + destino + " (" + classificacao + ")";
    }
}


