package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
	private List<Album> allAlbums;
	private SimpleDirectedWeightedGraph<Album, DefaultWeightedEdge> graph;
	private ItunesDAO dao;
	
	//variabili globali ricorsione
	private List<Album> bestPath;
	private int bestScore;
	
	
	// Costruttore modello
	public Model() {
		this.allAlbums = new ArrayList<>();
		this.graph = new SimpleDirectedWeightedGraph<Album, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.dao = new ItunesDAO();
	}
	
	public void buldGraph(int n) {
		clearGraph();
		loadNodes(n);
		
		Graphs.addAllVertices(this.graph, this.allAlbums);
		
		// Non avendo nessuna query il doppio for non risulta troppo oneroso
		for(Album a1: this.allAlbums) {
			for(Album a2: this.allAlbums) {
				int peso = a1.getNumSongs()-a2.getNumSongs();
				
				if(peso>0) { // in questo modo ottengo solo archi orientati da a2 a a1
					Graphs.addEdgeWithVertices(this.graph, a2, a1, peso);
				}
			}
		}
		System.out.println(this.graph.vertexSet().size());
		System.out.println(this.graph.edgeSet().size());
		
		
	}
	
	private void clearGraph() {
		this.allAlbums = new ArrayList<>();
		this.graph = new SimpleDirectedWeightedGraph<Album, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	}

	public void loadNodes(int n) {
		if(this.allAlbums.isEmpty())
			this.allAlbums = dao.getFilteredAlbums(n);
	}

	public int getNumEdges() {
		
		return this.graph.edgeSet().size();
	}

	public int getNumVertices() {
		return this.graph.vertexSet().size();
	}
	
	private int getBilancio(Album a) {
		int bilancio = 0;
		List<DefaultWeightedEdge> edgesIN = new ArrayList<>(this.graph.incomingEdgesOf(a));
		List<DefaultWeightedEdge> edgesOUT = new ArrayList<>(this.graph.outgoingEdgesOf(a));
		
		for ( DefaultWeightedEdge edge : edgesIN) 
			bilancio += this.graph.getEdgeWeight(edge);
		
		for ( DefaultWeightedEdge edge : edgesOUT) 
			bilancio -= this.graph.getEdgeWeight(edge);
	
		return bilancio;
	}

	public List<Album> getVertices(){
		List<Album> allVertices = new ArrayList<>(this.graph.vertexSet());
		Collections.sort(allVertices);
		
		return allVertices;
	}
	
	public List<BilancioAlbum> getAdiacenti(Album a) {
		List<Album> successori = Graphs.successorListOf(this.graph, a); // ritorna tutti i nodi che vengono dopo, tutti gli archi uscenti
		List<BilancioAlbum> bilancioSuccessori = new ArrayList<>();
		
		for(Album s:  successori) {
			int bilancio = this.getBilancio(s);
			bilancioSuccessori.add(new BilancioAlbum(s, bilancio));
		}
		
		Collections.sort(bilancioSuccessori);
		
		return bilancioSuccessori;
	}
	
	
	public List<Album> getPath(Album source, Album target, int threshold){ //calcola Percorso
		
		List<Album> parziale = new ArrayList<>();
		this.bestPath = new ArrayList<>();
		this.bestScore = 0;
		parziale.add(source);
		
		//chiamata ricorsiva
		calcolaPercorso(parziale, target, threshold);
		return this.bestPath;
	}

	private void calcolaPercorso(List<Album> parziale, Album target, int threshold) {
		
		Album corrente= parziale.get(parziale.size()-1);
		
		// condizione di uscita
		if(corrente.equals(target)) { // se non entro in questo if devo continuare ->  aggiungo return;
			//controllo se questa soluzione è migliore della best già salavat
			if(getScore(parziale)>this.bestScore) {
				this.bestScore = getScore(parziale);
				this.bestPath = new ArrayList<>(parziale); 
				// NO!!! this.bestPath = parziale perchè COPIO UN RIFERIMENTO 
				// e andando avanti nella ricorsione avrei sempre la parziale attuale 
			}
			return;
			
		}
		
		// continuo ad aggiugnere elementi in parziale
		List<Album> successors = Graphs.successorListOf(this.graph, corrente);
		
		for(Album s: successors) {
			if(this.graph.getEdgeWeight(this.graph.getEdge(corrente, s))>=threshold) { // se il peso è maggiore della soglia
				//aggiungo s alla soluzone parziale
				parziale.add(s);
				calcolaPercorso(parziale, target, threshold);
				parziale.remove(s);
				
			}
		}
		
	}

	private int getScore(List<Album> parziale) { // calcolo lo score della soluzione parziale
		
		Album source = parziale.get(0);
		int score =0;
		
		for(Album a : parziale) {
			if(getBilancio(a)>getBilancio(source)) {
				score ++;
			}
		}
		return score;
	}
}