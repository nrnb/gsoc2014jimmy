/**
 * 
 */
package org.cytoscape.cyGraph.algorithms;

import java.util.Queue;

import org.cytoscape.cyGraph.algorithms.api.MSTResult;
import org.cytoscape.model.CyEdge;

/**
 * @author Jimmy
 *
 */
public class MSTResultImpl implements MSTResult{

	private Queue<CyEdge> mst;
	
	private double weight;
	
	public MSTResultImpl(Queue<CyEdge> mst, double weight){
		
		this.mst = mst;
		this.weight = weight;
	
	}
	
	public double getWeight(){
		
		return this.weight;
		
	}
	
	public Iterable<CyEdge> getMST(){
		
		return this.mst;
		
	}
}
