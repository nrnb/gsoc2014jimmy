/**
 * 
 */
package org.cytoscape.cyGraph.algorithms;

import java.util.Map;
import java.util.Stack;

import org.cytoscape.cyGraph.algorithms.api.DfsResult;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * @author Jimmy
 * 
 */
public class DfsResultImpl implements DfsResult{

	private boolean marked[];

	private int edgeTo[];

	private CyNode source;

	@SuppressWarnings("unused")
	private CyNetwork network;

	private Map<CyNode, Integer> nodeToIndexMap;

	private Map<Integer, CyNode> indexToNodeMap;

	public DfsResultImpl(CyNode source, CyNetwork network, boolean marked[],
			int edgeTo[], Map<CyNode, Integer> nodeToIndexMap,
			Map<Integer, CyNode> indexToNodeMap) {

		this.edgeTo = edgeTo;
		this.marked = marked;
		this.network = network;
		this.source = source;
		this.nodeToIndexMap = nodeToIndexMap;
		this.indexToNodeMap = indexToNodeMap;
	}

	public CyNode getSource() {

		return source;
	}

	public boolean hasPathTo(CyNode target) {

		return marked[nodeToIndexMap.get(target)];
	}

	public Iterable<CyNode> getPathTo(CyNode target) {

		if (!hasPathTo(target))
			return null;

		Stack<CyNode> path = new Stack<CyNode>();
		int x;
		for (x = nodeToIndexMap.get(target); indexToNodeMap.get(edgeTo[x]) != source; x = edgeTo[x])
			path.push(indexToNodeMap.get(x));
		path.push(indexToNodeMap.get(x));
		return path;
	}
}
