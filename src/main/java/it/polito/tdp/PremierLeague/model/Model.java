package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.Adiacenza;
import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	PremierLeagueDAO dao = new PremierLeagueDAO();
	Graph<Player,DefaultWeightedEdge> grafo;
	Map<Integer,Player> idMap = new HashMap<>();
	List<Player> vertici = new ArrayList<>();
	
	// PUNTO2
	Simulatore sim;
	
	
	public List<Match> getAllMatches() {
		return this.dao.listAllMatches();
	}
	
	public String creaGrafo(Match m) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		// Aggiungo i vertici
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(idMap, m));
		
		// Aggiungo gli archi
		for(Adiacenza a : this.dao.getArchi(idMap, m))
			Graphs.addEdge(this.grafo, a.getP1(), a.getP2(), a.getPeso());
		
		return "Grafo creato!\n\nNumero vertici: "+this.grafo.vertexSet().size()+"\nNumero archi: "+this.grafo.edgeSet().size();
	}
	
	
	public String getMigliore() {
		double max=Double.MIN_VALUE;
		Player migliore=null;
		for(Player p : this.grafo.vertexSet()) {
			double val=0.0;
			for(DefaultWeightedEdge out : this.grafo.outgoingEdgesOf(p))
				val += this.grafo.getEdgeWeight(out);
			for(DefaultWeightedEdge in : this.grafo.incomingEdgesOf(p))
				val -= this.grafo.getEdgeWeight(in);
			
			if(val>max) {
				max = val;
				migliore = p;
			}
		}
		return "\n\nGiocatore migliore: "+migliore+" con punteggio "+max;
	}
	
	
	// PUNTO 2
	public String simula(int n, Match m) {
		this.sim = new Simulatore();
		
		Player giocatoreMigliore = this.getIlMigliore();
		Team squadraGiocatoreMigliore = this.dao.getSquadraGiocatoreMigliore(giocatoreMigliore);
		
		this.sim.init(n, grafo, m, squadraGiocatoreMigliore);
		
		this.sim.run();
		
		return this.sim.getRisultato()+this.sim.getEspulsi();
	}
	
	public Player getIlMigliore() {
		double max=Double.MIN_VALUE;
		Player migliore=null;
		for(Player p : this.grafo.vertexSet()) {
			double val=0.0;
			for(DefaultWeightedEdge out : this.grafo.outgoingEdgesOf(p))
				val += this.grafo.getEdgeWeight(out);
			for(DefaultWeightedEdge in : this.grafo.incomingEdgesOf(p))
				val -= this.grafo.getEdgeWeight(in);
			
			if(val>max) {
				max = val;
				migliore = p;
			}
		}
		return migliore;
	}
	
	
}
