/**
 * 
 */
package org.cytoscape.cyGraph.algorithms;

import java.util.Map;
import java.util.Stack;

import org.cytoscape.cyGraph.algorithms.api.BellmanFordResult;
import org.cytoscape.model.CyNode;

/**
 * @author Jimmy
 * 
 */
public class BellmanFordResultImpl implements BellmanFordResult{

	private Map<CyNode, MetaNode> nodeToMetaNodeMap;

	private CyNode source;

	private boolean negativeCycle;

	public BellmanFordResultImpl(CyNode source,
			Map<CyNode, MetaNode> nodeToMetaNodeMap, boolean negativeCycle) {

		this.source = source;
		this.nodeToMetaNodeMap = nodeToMetaNodeMap;
		this.negativeCycle = negativeCycle;
	}

	public boolean hasNegativeCycle() {

		return this.negativeCycle;

	}

	public CyNode getSource() {

		return this.source;
	}

	public double getDistanceTo(CyNode target) {

		return nodeToMetaNodeMap.get(target).getDistance();

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
}
