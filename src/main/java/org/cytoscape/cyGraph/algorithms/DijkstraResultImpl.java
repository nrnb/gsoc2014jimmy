/**
 * 
 */
package org.cytoscape.cyGraph.algorithms;

import java.util.Map;
import java.util.Stack;

import org.cytoscape.cyGraph.algorithms.api.DijkstraResult;
import org.cytoscape.model.CyNode;

/**
 * @author Jimmy
 * 
 */
public class DijkstraResultImpl implements DijkstraResult{

	private Map<CyNode, MetaNode> nodeToMetaNodeMap;

	private CyNode source;

	public DijkstraResultImpl(CyNode source, Map<CyNode, MetaNode> nodeToMetaNodeMap) {

		this.nodeToMetaNodeMap = nodeToMetaNodeMap;
		this.source = source;
	}

	public double getDistanceTo(CyNode target) {

		return nodeToMetaNodeMap.get(target).getDistance();

	}

	public CyNode getSource() {

		return this.source;
	}

	public boolean hasPathTo(CyNode target) {

		return nodeToMetaNodeMap.get(target).getDistance() < Double.POSITIVE_INFINITY;

	}

	public CyNode getPredecessorTo(CyNode target) {

		return nodeToMetaNodeMap.get(target).getPredecessor();

	}

	public Iterable<CyNode> getPathTo(CyNode target) {

		if (!hasPathTo(target))
			return null;

		Stack<CyNode> path = new Stack<CyNode>();
		path.push(target);
		CyNode predecessor = nodeToMetaNodeMap.get(target).getPredecessor();
		while (predecessor != null) {
			path.push(predecessor);
			predecessor = nodeToMetaNodeMap.get(target).getPredecessor();
		}
		return path;

	}

	public double getEccentricity(){
		double max = 0.0;
		for(CyNode node : nodeToMetaNodeMap.keySet()){
			double temp = getDistanceTo(node);
			if(temp > max){
				max = temp;
			}
		}
		return max;
	}
}