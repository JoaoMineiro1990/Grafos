import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;

public class Grafo {

    private List<Vertice> listaAdjacent;

    public Grafo(){
        listaAdjacent = new ArrayList<>();
    }

    public Grafo(int n) {
        listaAdjacent = new ArrayList<>();
        for (int i = 0; i <= n; i++) { 
            listaAdjacent.add(new Vertice());
        }
    }
    public List<Vertice> getListaAdjacent() {
        return listaAdjacent;
    }
    public void carregarDeArquivo(String caminhoArquivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha = br.readLine();
            String[] primeiraLinha = linha.split(" ");
            int numVertices = Integer.parseInt(primeiraLinha[0]);
            int numArestas = Integer.parseInt(primeiraLinha[1]);

            for (int i = listaAdjacent.size(); i <= numVertices; i++) {
                listaAdjacent.add(new Vertice());
            }

            for (int i = 0; i < numArestas; i++) {
                linha = br.readLine();
                String[] partes = linha.split(" ");
                int origem = Integer.parseInt(partes[0]);
                int destino = Integer.parseInt(partes[1]);

                Vertice verticeOrigem = listaAdjacent.get(origem);
                Vertice verticeDestino = listaAdjacent.get(destino);
                verticeOrigem.addAresta(verticeDestino, 1); 
            }
        } catch (IOException e) {
            throw e;
        }
    }
    public void printVertices() {
        for (int i = 1; i < listaAdjacent.size(); i++) { 
            Vertice vertice = listaAdjacent.get(i);
            System.out.println("Vértice " + i + ":");
            for (Aresta aresta : vertice.getTodArestas()) {
                System.out.println("  -> Destino: " + listaAdjacent.indexOf(aresta.getDestino()) +
                                   ", Capacidade: " + aresta.getCapacidade() +
                                   ", Fluxo: " + aresta.getFluxo());
            }
        }
    }
    public int[] dfsSalvandoAntecessores(int inicio) {
        int[] antecessores = new int[listaAdjacent.size()];
        boolean[] visitados = new boolean[listaAdjacent.size()];
    
        Arrays.fill(antecessores, -1);
        Stack<Integer> pilha = new Stack<>();
    
        pilha.push(inicio);
        antecessores[inicio] = -1;
    
        while (!pilha.isEmpty()) {
            int verticeAtual = pilha.pop();
    
            if (!visitados[verticeAtual]) {
                visitados[verticeAtual] = true;
                Vertice vertice = listaAdjacent.get(verticeAtual);
    
                for (Aresta aresta : vertice.getTodArestas()) {
                    int destino = listaAdjacent.indexOf(aresta.getDestino());
                    if (!visitados[destino]) {
                        pilha.push(destino);
                        antecessores[destino] = verticeAtual;
                    }
                }
            }
        }
    
        return antecessores;
    }
    public void printAntecessores(int[] antecessores) {
        System.out.println("Antecessores de cada vértice:");
        for (int i = 1; i < antecessores.length; i++) {
            if (antecessores[i] == -1) {
                System.out.println("Vértice " + i + " não tem antecessor (início ou isolado)");
            } else {
                System.out.println("Vértice " + i + " tem antecessor: " + antecessores[i]);
            }
        }
    }

    public Grafo construirGrafoResidual() {
        Grafo grafoResidual = new Grafo(listaAdjacent.size() - 1); 
    
        for (int i = 1; i < listaAdjacent.size(); i++) {
            Vertice vertice = listaAdjacent.get(i);
            for (Aresta aresta : vertice.getTodArestas()) {
                int capacidadeResidual = aresta.getCapacidade() - aresta.getFluxo();
                int destinoIndex = listaAdjacent.indexOf(aresta.getDestino()); 
                
                if (capacidadeResidual > 0) {
                    grafoResidual.listaAdjacent.get(i).addAresta(grafoResidual.listaAdjacent.get(destinoIndex), capacidadeResidual); // Aresta direta
                }
    
                if (aresta.getFluxo() > 0) {
                    grafoResidual.listaAdjacent.get(destinoIndex).addAresta(grafoResidual.listaAdjacent.get(i), aresta.getFluxo()); // Aresta reversa
                }
            }
        }
    
        return grafoResidual;
    }
    public void printVerticesResidual() {
        for (int i = 1; i < listaAdjacent.size(); i++) {
            Vertice vertice = listaAdjacent.get(i);
            System.out.println("Vértice " + i + ":");
            for (Aresta aresta : vertice.getTodArestas()) {
                int destinoIndex = listaAdjacent.indexOf(aresta.getDestino());
                int capacidadeResidual = aresta.getCapacidade() - aresta.getFluxo();
    
                if (capacidadeResidual > 0) {
                    System.out.println("  -> Aresta de ida para " + destinoIndex +
                                       ", Capacidade: " + aresta.getCapacidade() +
                                       ", Fluxo atual: " + aresta.getFluxo());
                }
    
                if (aresta.getFluxo() > 0) {
                    System.out.println("  -> Aresta de volta para " + i +
                                       ", Capacidade: " + aresta.getCapacidade() +
                                       ", Fluxo reverso: " + aresta.getFluxo());
                }
            }
        }
    }

    public List<Aresta> encontrarCaminho(int origem, int destino) {
    Queue<Integer> fila = new LinkedList<>();
    int[] antecessores = new int[listaAdjacent.size()]; 
    Aresta[] arestaCaminho = new Aresta[listaAdjacent.size()]; 
    boolean[] visitado = new boolean[listaAdjacent.size()];

    Arrays.fill(antecessores, -1); 
    fila.add(origem);
    visitado[origem] = true;

    while (!fila.isEmpty()) {
        int verticeAtual = fila.poll();

        if (verticeAtual == destino) {
            return construirCaminho(origem, destino, antecessores, arestaCaminho);
        }

        for (Aresta aresta : listaAdjacent.get(verticeAtual).getTodArestas()) {
            int proximo = listaAdjacent.indexOf(aresta.getDestino());
            int capacidadeResidual = aresta.getCapacidade() - aresta.getFluxo();

            if (!visitado[proximo] && capacidadeResidual > 0) {
                fila.add(proximo);
                visitado[proximo] = true;
                antecessores[proximo] = verticeAtual; 
                arestaCaminho[proximo] = aresta; 

                if (proximo == destino) {
                    return construirCaminho(origem, destino, antecessores, arestaCaminho);
                }
            }
        }
    }

    return null;
}

private List<Aresta> construirCaminho(int origem, int destino, int[] antecessores, Aresta[] arestaCaminho) {
    List<Aresta> caminho = new LinkedList<>();
    int verticeAtual = destino;

    while (verticeAtual != origem) {
        Aresta aresta = arestaCaminho[verticeAtual];
        caminho.add(0, aresta);
        verticeAtual = antecessores[verticeAtual];
    }
    System.out.println("Caminho disjunto em arestas encontrado:");
    return caminho;
}

public int calcularCaminhosDisjuntos(int origem, int destino) {
    int numeroCaminhosDisjuntos = 0;
    
    List<Aresta> caminhoAumentante = encontrarCaminho(origem, destino);

    while (caminhoAumentante != null && !caminhoAumentante.isEmpty()) {
        System.out.print("Caminho " + (numeroCaminhosDisjuntos + 1) + ": ");
        System.out.print(origem);
        for (Aresta aresta : caminhoAumentante) {
            int destinoIndex = listaAdjacent.indexOf(aresta.getDestino());
            System.out.print(" -> " + destinoIndex);
        }
        System.out.println(); 
        for (Aresta aresta : caminhoAumentante) {
            int origemIndex = listaAdjacent.indexOf(aresta.getOrigem());

            if (aresta.getOrigem() == listaAdjacent.get(origemIndex)) {
                aresta.setFluxo(aresta.getFluxo() + 1);
            } else {
                aresta.setFluxo(aresta.getFluxo() - 1);
            }
        }
        numeroCaminhosDisjuntos++;
        this.construirGrafoResidual();
        caminhoAumentante = encontrarCaminho(origem, destino);
    }

    return numeroCaminhosDisjuntos;
}
}
