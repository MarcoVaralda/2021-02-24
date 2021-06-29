package it.polito.tdp.PremierLeague.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.PremierLeague.model.Event.EventType;

public class Simulatore {
	
	// Eventi
	PriorityQueue<Event> queue = new PriorityQueue<Event>();
	
	// Parametri
	int n; // Numero azioni salienti da simulare
	Graph<Player,DefaultWeightedEdge> grafo;
	Team squadraGiocatoreMigliore;
	Integer idCasa;
	Integer idOspite;
	
	// Stato del sistema
	Map<Integer,Integer> squadraGiocatori;
	int numAzioni;
	
	// Misure in uscita
	int goalCasa;
	int goalOspite;
	int espulsiCasa;
	int espulsiOspite;
	
	
	public void init(int n, Graph<Player,DefaultWeightedEdge> grafo, Match m, Team squadraGiocatoreMigliore) {
		this.n=n;
		this.grafo=grafo;
		this.squadraGiocatoreMigliore=squadraGiocatoreMigliore;
		
		this.idCasa=m.teamHomeID;
		this.idOspite=m.teamAwayID;
		
		this.squadraGiocatori = new HashMap<>();
		this.squadraGiocatori.put(m.teamHomeID, 11);
		this.squadraGiocatori.put(m.teamAwayID, 11);
		
		this.goalCasa=0;
		this.goalOspite=0;
		this.espulsiCasa=0;
		this.espulsiOspite=0;
		
		for(int i=1;i<=n;i++) {
			int random = (int)(Math.random()*100);
			Event e = null;
			if(random<=50)
				e = new Event(EventType.GOAL,i);
			else if(random>50 && random<=80)
				e = new Event(EventType.ESPULSIONE,i);
			else
				e = new Event(EventType.INFORTUNIO,i);
			this.queue.add(e);
			this.numAzioni++;
		}
	}
	
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			processEvent(e);
		}
	}

	private void processEvent(Event e) {
		switch(e.getTipo()) {
		case GOAL: // Controllo quale squadra ha più giocatori
			if(this.squadraGiocatori.get(this.idCasa)>this.squadraGiocatori.get(idOspite)) 
				this.goalCasa++;
			else if(this.squadraGiocatori.get(this.idOspite)>this.squadraGiocatori.get(idCasa))
				this.goalOspite++;
			else {
				if(this.idCasa==this.squadraGiocatoreMigliore.getTeamID())
					this.goalCasa++;
				else
					this.goalOspite++;
			}			
			break;
			
		case ESPULSIONE:
			int random = (int)(Math.random()*100);
			if(random<=60) {
				this.squadraGiocatori.replace(this.squadraGiocatoreMigliore.getTeamID(),this.squadraGiocatori.get(this.squadraGiocatoreMigliore.getTeamID())-1);
				
				if(this.idCasa==this.squadraGiocatoreMigliore.getTeamID()) 
					this.espulsiCasa++;
				else 
					this.espulsiOspite++;
			}
			else {
				if(this.idCasa==this.squadraGiocatoreMigliore.getTeamID()) {
					this.squadraGiocatori.replace(idOspite, this.squadraGiocatori.get(idOspite)-1);
					this.espulsiOspite++;
				}
				else {
					this.squadraGiocatori.replace(idCasa, this.squadraGiocatori.get(idCasa)-1);
					this.espulsiCasa++;
				}
			}
			break;
			
		case INFORTUNIO:
			int prob = (int)(Math.random()*100);

			if(prob<=50) { // +2 eventi
				for(int i=this.numAzioni+1;i<=this.numAzioni+2;i++) {
					int prob2 = (int)(Math.random()*100);
					Event nuovoEvento = null;
					if(prob2<=50)
						nuovoEvento = new Event(EventType.GOAL,i);
					else if(prob2>50 && prob2<=80)
						nuovoEvento = new Event(EventType.ESPULSIONE,i);
					else
						nuovoEvento = new Event(EventType.INFORTUNIO,i);
					this.queue.add(nuovoEvento);
				}
				this.numAzioni = this.numAzioni+2;
			}
			else { // +3 eventi
				for(int i=this.numAzioni+1;i<=this.numAzioni+3;i++) {
					int prob2 = (int)(Math.random()*100);
					Event nuovoEvento = null;
					if(prob2<=50)
						nuovoEvento = new Event(EventType.GOAL,i);
					else if(prob2>50 && prob2<=80)
						nuovoEvento = new Event(EventType.ESPULSIONE,i);
					else
						nuovoEvento = new Event(EventType.INFORTUNIO,i);
					this.queue.add(nuovoEvento);
				}
				this.numAzioni = this.numAzioni+3;
			}
			
			break;
		}
	}
	
	public String getRisultato() {
		return "\n\nRisultato partita: "+this.goalCasa +" - "+this.goalOspite;
	}
	
	public String getEspulsi() {
		return "\nEspulsi casa: "+this.espulsiCasa+"\nEspulsi ospite: "+this.espulsiOspite;
	}

}
