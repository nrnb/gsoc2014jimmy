/**
 * 
 */
package org.cytoscape.cyGraph.algorithms;

import java.util.Map;
import java.util.Stack;

import org.cytoscape.cyGraph.algorithms.api.FloydWarshallResult;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * @author Jimmy
 *
 */
public class FloydWarshallResultImpl implements FloydWarshallResult{

	private Map<CyNode, Integer> nodeToIndexMap;
	
	private double distTo[][];
	
	private CyEdge edgeTo[][];
	
	private boolean negativeCycle;
	
	public FloydWarshallResultImpl(Map<CyNode, Integer> nodeToIndexMap, double distTo[][], CyEdge edgeTo[][], boolean negativeCycle){
		
		this.nodeToIndexMap = nodeToIndexMap;
		this.distTo = distTo;
		this.edgeTo = edgeTo;
		this.negativeCycle = negativeCycle;
	}
	
	@Override
	public boolean hasNegativeCycle() {
		// TODO Auto-generated method stub
		return negativeCycle;
	}

	@Override
	public boolean hasPath(CyNode source, CyNode target) {
		// TODO Auto-generated method stub
		return distTo[nodeToIndexMap.get(source)][nodeToIndexMap.get(target)] < Double.POSITIVE_INFINITY;
	}

	@Override
	public double getDistance(CyNode source, CyNode target) {
		// TODO Auto-generated method stub
		return distTo[nodeToIndexMap.get(source)][nodeToIndexMap.get(target)];
	}

	@Override
	public Iterable<CyEdge> getPath(CyNode source, CyNode target) {
		// TODO Auto-generated method stub
		if(!hasPath(source, target))
			return null;
		Stack<CyEdge> path = new Stack<CyEdge>();
		for(CyEdge edge = edgeTo[nodeToIndexMap.get(source)][nodeToIndexMap.get(target)]; edge != null; edge = edgeTo[nodeToIndexMap.get(source)][nodeToIndexMap.get(edge.getSource())])
			path.push(edge);
		return path;
	}

	@Override
	public Iterable<CyEdge> getNegativeCycle() {
		// TODO Auto-generated method stub
		Stack<CyEdge> cycle = new Stack<CyEdge>();
		
		for(int i = 0; i < distTo.length; i++){
			
			if(distTo[i][i] < 0.0){
				
				for(int j = 0; j < edgeTo.length; j++){
					if(edgeTo[i][j] != null){
						cycle.push(edgeTo[i][j]);
					}	
				}
				return cycle;
			}
		}
		return null;
	}
}
