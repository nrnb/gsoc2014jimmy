/**
 * 
 */
package org.cytoscape.cyGraph.algorithms;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.cytoscape.cyGraph.algorithms.api.BellmanFordResult;
import org.cytoscape.cyGraph.algorithms.api.BfsResult;
import org.cytoscape.cyGraph.algorithms.api.Callback;
import org.cytoscape.cyGraph.algorithms.api.DfsResult;
import org.cytoscape.cyGraph.algorithms.api.DijkstraResult;
import org.cytoscape.cyGraph.algorithms.api.FloydWarshallResult;
import org.cytoscape.cyGraph.algorithms.api.GraphAlgorithms;
import org.cytoscape.cyGraph.algorithms.api.HITSResults;
import org.cytoscape.cyGraph.algorithms.api.PagerankResults;
import org.cytoscape.cyGraph.algorithms.api.MSTResult;
import org.cytoscape.cyGraph.algorithms.api.WeightFunction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * @author Jimmy
 * 
 */
public class GraphAlgorithmsImpl implements GraphAlgorithms {

	private static final int INFINITY = Integer.MAX_VALUE;

	private final double epsilon = 0.0001;

	@Override
	public BfsResult breadthFirstSearch(CyNetwork network, CyNode source,
			boolean directed, Callback callback) {
		Map<CyNode, Integer> nodeToIndexMap = new IdentityHashMap<CyNode, Integer>();
		Map<Integer, CyNode> indexToNodeMap = new HashMap<Integer, CyNode>();

		int nodeCount = network.getNodeCount();

		boolean marked[] = new boolean[nodeCount];
		int distTo[] = new int[nodeCount];
		int edgeTo[] = new int[nodeCount];

		int i = 0;
		for (CyNode node : network.getNodeList()) {
			indexToNodeMap.put(i, node);
			nodeToIndexMap.put(node, i);
			distTo[i] = INFINITY;
			i++;
		}

		int index = nodeToIndexMap.get(source);
		distTo[index] = 0;
		marked[index] = true;

		Queue<CyNode> q = new LinkedList<CyNode>();

		q.add(source);

		int time = 0;
		boolean flag = false;
		
		while (!q.isEmpty()) {
			CyNode v = q.poll();

			List<CyNode> nodeList;
			if (directed) {
				nodeList = network.getNeighborList(v, CyEdge.Type.OUTGOING);
			} else {
				nodeList = network.getNeighborList(v, CyEdge.Type.ANY);
			}

			for (CyNode node : nodeList) {
				index = nodeToIndexMap.get(node);
				if (!marked[index]) {
					edgeTo[index] = nodeToIndexMap.get(v);
					distTo[index] = distTo[nodeToIndexMap.get(v)] + 1;
					marked[index] = true;
					if (callback != null) {
						if (callback.notify(node, ++time, distTo[index])){
							flag = true;
							break;
						}
					}
					
					q.add(node);
				}
			}
			if(flag){
				break;
			}
		}
		return new BfsResultImpl(source, network, marked, distTo, edgeTo,
				nodeToIndexMap, indexToNodeMap);
	}

	@Override
	public DfsResult depthFirstSearch(CyNetwork network, CyNode source,
			boolean directed, Callback callback) {
		Map<CyNode, Integer> nodeToIndexMap = new IdentityHashMap<CyNode, Integer>();
		Map<Integer, CyNode> indexToNodeMap = new HashMap<Integer, CyNode>();

		int nodeCount = network.getNodeCount();

		boolean marked[] = new boolean[nodeCount];
		int edgeTo[] = new int[nodeCount];
		int depthTo[] = new int[nodeCount];

		int i = 0;
		for (CyNode node : network.getNodeList()) {
			indexToNodeMap.put(i, node);
			nodeToIndexMap.put(node, i);
			i++;
		}
		
		LinkedList<CyNode> stack = new LinkedList<CyNode>();
		stack.push(source);
		depthTo[nodeToIndexMap.get(source)] = 0;
		int time = 0;
		while (!stack.isEmpty()) {
			CyNode node = stack.pop();
			int nodeIndex = nodeToIndexMap.get(node);
			
			if (!marked[nodeIndex]) {
				marked[nodeIndex] = true;
				
				time++;
				if(callback!=null){
					if(callback.notify(node, time, depthTo[nodeIndex])){
						break;
					}
				}
				List<CyNode> nodeList;

				if (directed) {

					nodeList = network.getNeighborList(source,
							CyEdge.Type.OUTGOING);
				} else {

					nodeList = network.getNeighborList(source, CyEdge.Type.ANY);
				}
				for (CyNode neighbor : nodeList) {

					int neighborIndex = nodeToIndexMap.get(neighbor);
					if (!marked[neighborIndex]) {
						edgeTo[neighborIndex] = nodeIndex;
						depthTo[nodeIndex] = depthTo[neighborIndex] + 1;
						stack.push(neighbor);
					}
				}
			}
		}

		// dfs(network, source, callback, nodeToIndexMap, marked, edgeTo,
		// directed);

		return new DfsResultImpl(source, network, marked, edgeTo,
				nodeToIndexMap, indexToNodeMap);
	}

	@SuppressWarnings("unused")
	private void dfs(CyNetwork network, CyNode source, Callback callback,
			Map<CyNode, Integer> nodeToIndexMap, boolean marked[],
			int edgeTo[], boolean directed) {

		int sourceIndex = nodeToIndexMap.get(source);

		marked[sourceIndex] = true;

		List<CyNode> nodeList;

		if (directed) {

			nodeList = network.getNeighborList(source, CyEdge.Type.OUTGOING);
		} else {

			nodeList = network.getNeighborList(source, CyEdge.Type.ANY);
		}

		for (CyNode node : nodeList) {

			int neighborIndex = nodeToIndexMap.get(node);
			if (!marked[neighborIndex]) {

				edgeTo[neighborIndex] = sourceIndex;
				// TODO include callback
				dfs(network, node, callback, nodeToIndexMap, marked, edgeTo,
						directed);
			}
		}
	}

	@Override
	public FloydWarshallResult findAllPairShortestPath(CyNetwork network,
			boolean directed, WeightFunction function) {
		boolean negativeCycle = false;

		int nodeCount = network.getNodeCount();

		double distTo[][] = new double[nodeCount][nodeCount];
		CyEdge edgeTo[][] = new CyEdge[nodeCount][nodeCount];

		Map<CyNode, Integer> nodeToIndexMap = new IdentityHashMap<CyNode, Integer>();

		int i = 0;

		List<CyNode> nodeList = network.getNodeList();

		for (CyNode node : nodeList) {
			nodeToIndexMap.put(node, i++);
		}

		for (i = 0; i < nodeCount; i++) {

			for (int j = 0; j < nodeCount; j++) {
				distTo[i][j] = Double.POSITIVE_INFINITY;
			}

		}

		for (CyNode node : nodeList) {

			int nodeIndex = nodeToIndexMap.get(node);
			for (CyEdge edge : network.getAdjacentEdgeList(node,
					CyEdge.Type.ANY)) {

				int sourceIndex = nodeToIndexMap.get(edge.getSource());
				int targetIndex = nodeToIndexMap.get(edge.getTarget());
				distTo[sourceIndex][targetIndex] = function.getWeight(edge);
				edgeTo[sourceIndex][targetIndex] = edge;

			}

			if (distTo[nodeIndex][nodeIndex] >= 0.0) {
				distTo[nodeIndex][nodeIndex] = 0.0;
				edgeTo[nodeIndex][nodeIndex] = null;
			}
		}

		for (CyNode node : nodeList) {

			int nodeIndex = nodeToIndexMap.get(node);

			for (CyNode node1 : nodeList) {

				int node1Index = nodeToIndexMap.get(node1);
				if (edgeTo[node1Index][nodeIndex] == null)
					continue;

				for (CyNode node2 : nodeList) {

					int node2Index = nodeToIndexMap.get(node2);
					if (distTo[node1Index][node2Index] > distTo[node1Index][nodeIndex]
							+ distTo[nodeIndex][node2Index]) {

						distTo[node1Index][node2Index] = distTo[node1Index][nodeIndex]
								+ distTo[nodeIndex][node2Index];
						edgeTo[node1Index][node2Index] = edgeTo[nodeIndex][node2Index];
					}

					if (distTo[node1Index][node1Index] < 0.0) {
						negativeCycle = true;
					}
				}
			}
		}
		return new FloydWarshallResultImpl(nodeToIndexMap, distTo, edgeTo,
				negativeCycle);
	}

	@Override
	public MSTResult findKruskalTree(CyNetwork network, WeightFunction function) {
		double weight = 0;
		Queue<MetaEdge> pq = new PriorityQueue<MetaEdge>(
				network.getEdgeCount(), CyEdgeComparator.getInstance());

		Map<CyEdge, MetaEdge> edgeToMetaEdgeMap = new IdentityHashMap<CyEdge, MetaEdge>();

		Map<CyNode, Integer> nodeToIndexMap = new IdentityHashMap<CyNode, Integer>();

		Queue<CyEdge> mst = new LinkedList<CyEdge>();

		int k = 0;
		for (CyNode node : network.getNodeList()) {

			nodeToIndexMap.put(node, k);
			k++;
		}
		for (CyEdge edge : network.getEdgeList()) {

			MetaEdge metaEdge = new MetaEdge(edge, function.getWeight(edge));
			edgeToMetaEdgeMap.put(edge, metaEdge);
			pq.add(metaEdge);
		}

		UnionFind uf = new UnionFind(network.getNodeCount());

		while (!pq.isEmpty() && mst.size() < network.getNodeCount() - 1) {

			MetaEdge metaEdge = pq.poll();
			int i = nodeToIndexMap.get(metaEdge.getCyEdge().getSource());
			int j = nodeToIndexMap.get(metaEdge.getCyEdge().getTarget());

			if (!uf.isConnected(i, j)) {
				uf.union(i, j);
				mst.add(metaEdge.getCyEdge());
				weight += metaEdge.getWeight();
			}
		}

		return new MSTResultImpl(mst, weight);
	}

	@Override
	public DijkstraResult findPath(CyNetwork network, CyNode source,
			boolean directed, WeightFunction function) {
		Map<CyNode, MetaNode> nodeToMetaNodeMap = new IdentityHashMap<CyNode, MetaNode>();

		Queue<MetaNode> pq = new PriorityQueue<MetaNode>(
				network.getNodeCount(), DijkstraComparator.getInstance());

		for (CyNode node : network.getNodeList()) {

			MetaNode metaNode = new MetaNode(node, Double.POSITIVE_INFINITY,
					null);
			nodeToMetaNodeMap.put(node, metaNode);

		}

		nodeToMetaNodeMap.get(source).setDistance(0.0);
		pq.add(nodeToMetaNodeMap.get(source));

		while (!pq.isEmpty()) {
			MetaNode metaNode = pq.remove();
			List<CyEdge> edgeList = network.getAdjacentEdgeList(
					metaNode.getNode(), CyEdge.Type.OUTGOING);
			if (directed) {
				edgeList = network.getAdjacentEdgeList(metaNode.getNode(),
						CyEdge.Type.OUTGOING);
			} else {
				edgeList = network.getAdjacentEdgeList(metaNode.getNode(),
						CyEdge.Type.ANY);
			}
			for (CyEdge edge : edgeList) {
				MetaNode neighborMetaNode;
				if (edge.getTarget() == metaNode.getNode()) {
					neighborMetaNode = nodeToMetaNodeMap.get(edge.getSource());
				} else {
					neighborMetaNode = nodeToMetaNodeMap.get(edge.getTarget());
				}
				if (neighborMetaNode.getDistance() > metaNode.getDistance()
						+ function.getWeight(edge)) {
					neighborMetaNode.setDistance(metaNode.getDistance()
							+ function.getWeight(edge));
					neighborMetaNode.setPredecessor(metaNode.getNode());
					pq.add(neighborMetaNode);
				}
			}

		}
		return new DijkstraResultImpl(source, nodeToMetaNodeMap);
	}

	@Override
	public MSTResult findPrimTree(CyNetwork network, WeightFunction function) {
		double weight = 0.0;

		int nodeCount = network.getNodeCount();

		int edgeCount = network.getEdgeCount();

		Queue<CyEdge> mst = new LinkedList<CyEdge>();

		Queue<MetaEdge> pq = new PriorityQueue<MetaEdge>(edgeCount,
				CyEdgeComparator.getInstance());

		boolean marked[] = new boolean[nodeCount];

		Map<CyNode, Integer> nodeToIndexMap = new IdentityHashMap<CyNode, Integer>();

		int i = 0;
		List<CyNode> nodeList = network.getNodeList();

		for (CyNode node : nodeList) {

			nodeToIndexMap.put(node, i);
			i++;
		}

		for (CyNode node : nodeList) {

			int nodeIndex = nodeToIndexMap.get(node);

			if (!marked[nodeIndex]) {

				scan(network, node, nodeIndex, nodeToIndexMap, marked, pq,
						function);

				while (!pq.isEmpty()) {

					MetaEdge metaEdge = pq.poll();

					int v = nodeToIndexMap
							.get(metaEdge.getCyEdge().getSource());
					int w = nodeToIndexMap
							.get(metaEdge.getCyEdge().getTarget());

					if (marked[v] && marked[w])
						continue;

					mst.add(metaEdge.getCyEdge());

					weight += metaEdge.getWeight();

					if (!marked[v])
						scan(network, metaEdge.getCyEdge().getSource(), v,
								nodeToIndexMap, marked, pq, function);
					if (!marked[w])
						scan(network, metaEdge.getCyEdge().getTarget(), w,
								nodeToIndexMap, marked, pq, function);
				}
			}
		}
		return new MSTResultImpl(mst, weight);
	}

	private void scan(CyNetwork network, CyNode node, int nodeIndex,
			Map<CyNode, Integer> nodeToIndexMap, boolean marked[],
			Queue<MetaEdge> pq, WeightFunction function) {

		marked[nodeIndex] = true;
		for (CyEdge edge : network.getAdjacentEdgeList(node, CyEdge.Type.ANY)) {

			CyNode neighbor;
			if (edge.getSource() == node) {

				neighbor = edge.getTarget();

			} else {

				neighbor = edge.getSource();
			}

			int neighborIndex = nodeToIndexMap.get(neighbor);
			if (!marked[neighborIndex]) {

				pq.add(new MetaEdge(edge, function.getWeight(edge)));
			}
		}
	}

	@Override
	public BellmanFordResult findShortestPath(CyNetwork network, CyNode source,
			boolean directed, WeightFunction function) {
		Map<CyNode, MetaNode> nodeToMetaNodeMap = new IdentityHashMap<CyNode, MetaNode>();

		boolean negativeCycle = true;

		for (CyNode node : network.getNodeList()) {

			MetaNode metaNode = new MetaNode(node, Double.POSITIVE_INFINITY,
					null);
			nodeToMetaNodeMap.put(node, metaNode);

		}

		nodeToMetaNodeMap.get(source).setDistance(0.0);

		for (int i = 0; i < network.getNodeCount() - 1; i++) {

			for (CyEdge edge : network.getEdgeList()) {

				MetaNode sourceMetaNode = nodeToMetaNodeMap.get(edge
						.getSource());
				MetaNode targetMetaNode = nodeToMetaNodeMap.get(edge
						.getTarget());

				if (targetMetaNode.getDistance() > sourceMetaNode.getDistance()
						+ function.getWeight(edge)) {
					targetMetaNode.setDistance(sourceMetaNode.getDistance()
							+ function.getWeight(edge));
					targetMetaNode.setPredecessor(sourceMetaNode.getNode());
				}
			}
		}

		for (CyEdge edge : network.getEdgeList()) {

			MetaNode sourceMetaNode = nodeToMetaNodeMap.get(edge.getSource());
			MetaNode targetMetaNode = nodeToMetaNodeMap.get(edge.getTarget());
			if (targetMetaNode.getDistance() > sourceMetaNode.getDistance()
					+ function.getWeight(edge))
				negativeCycle = false;
		}

		return new BellmanFordResultImpl(source, nodeToMetaNodeMap,
				negativeCycle);
	}

	@Override
	public HITSResults getHITSScores(CyNetwork network, boolean directed) {

		int nodeCount = network.getNodeCount();

		double authority[] = new double[nodeCount];

		double hubs[] = new double[nodeCount];

		Map<CyNode, Integer> nodeIndexMap = new IdentityHashMap<CyNode, Integer>();

		int index = 0;

		List<CyNode> nodeList = network.getNodeList();

		for (CyNode node : nodeList) {
			nodeIndexMap.put(node, index);
			index++;
		}

		double tempHubs[] = new double[nodeCount];

		double tempAuthority[] = new double[nodeCount];

		for (int i = 0; i < nodeCount; i++) {

			authority[i] = tempAuthority[i] = 1.0;
			hubs[i] = tempAuthority[i] = 1.0;
		}

		while (true) {

			boolean completed = true;
			updateAuthorityValue(network, tempAuthority, hubs, directed,
					nodeIndexMap, nodeList);
			updateHubValue(network, tempHubs, authority, directed,
					nodeIndexMap, nodeList);

			completed = checkDifference(authority, tempAuthority, epsilon)
					&& checkDifference(hubs, tempHubs, epsilon);

			System.arraycopy(tempAuthority, 0, authority, 0, nodeCount);
			System.arraycopy(tempHubs, 0, hubs, 0, nodeCount);

			if (completed)
				break;
		}
		return new HITSResultImpl(authority, hubs, nodeIndexMap);
	}

	private void updateHubValue(CyNetwork network, double tempHubs[],
			double authority[], boolean directed,
			Map<CyNode, Integer> nodeIndexMap, List<CyNode> nodeList) {

		double norm = 0.0;
		for (CyNode node : nodeList) {

			int nodeIndex = nodeIndexMap.get(node);

			double hub = 0.0;
			List<CyNode> neighborList;
			if (directed) {
				neighborList = network.getNeighborList(node,
						CyEdge.Type.OUTGOING);
			} else {
				neighborList = network.getNeighborList(node, CyEdge.Type.ANY);
			}

			for (CyNode neighbor : neighborList) {

				hub += authority[nodeIndexMap.get(neighbor)];
			}

			tempHubs[nodeIndex] = hub;

			norm += tempHubs[nodeIndex] * tempHubs[nodeIndex];

		}
		norm = Math.sqrt(norm);
		if (norm > 0) {
			for (int i = 0; i < tempHubs.length; i++) {
				tempHubs[i] = tempHubs[i] / norm;
			}
		}
	}

	private void updateAuthorityValue(CyNetwork network,
			double tempAuthority[], double hubs[], boolean directed,
			Map<CyNode, Integer> nodeIndexMap, List<CyNode> nodeList) {

		double norm = 0.0;
		for (CyNode node : nodeList) {
			int nodeIndex = nodeIndexMap.get(node);
			double auth = 0.0;
			List<CyNode> neighborList;
			if (directed) {
				neighborList = network.getNeighborList(node,
						CyEdge.Type.INCOMING);
			} else {
				neighborList = network.getNeighborList(node, CyEdge.Type.ANY);
			}

			for (CyNode neighbor : neighborList) {
				auth += hubs[nodeIndexMap.get(neighbor)];
			}

			tempAuthority[nodeIndex] = auth;

			norm += tempAuthority[nodeIndex] * tempAuthority[nodeIndex];

		}
		norm = Math.sqrt(norm);
		if (norm > 0) {

			for (int i = 0; i < tempAuthority.length; i++) {
				tempAuthority[i] = tempAuthority[i] / norm;
			}
		}
	}

	private boolean checkDifference(double[] oldValues, double[] newValues,
			double epsilon) {

		for (int i = 0; i < oldValues.length; i++) {
			if (oldValues[i] > 0
					&& ((newValues[i] - oldValues[i]) / oldValues[i]) >= epsilon) {
				return false;
			}
		}
		return true;
	}

	@Override
	public PagerankResults getPageranks(CyNetwork network,
			WeightFunction function, boolean directed) {
		return getPageranks(network, function, directed, 0.85, 0.001);
	}

	@Override
	public PagerankResults getPageranks(CyNetwork network,
			WeightFunction function, boolean directed, double probability,
			double epsilon) {
		Map<CyNode, Integer> nodeIndexMap = new IdentityHashMap<CyNode, Integer>();

		int nodeIndex = 0;
		List<CyNode> nodeList = network.getNodeList();

		for (CyNode node : nodeList) {

			nodeIndexMap.put(node, nodeIndex++);
		}

		int nodeCount = network.getNodeCount();

		double pageranks[] = new double[nodeCount];

		double temp[] = new double[nodeCount];

		double weights[] = new double[nodeCount];

		// set initial values
		nodeIndex = 0;
		for (CyNode node : nodeList) {

			pageranks[nodeIndex] = 1.0f / nodeCount;
			if (function != null) {

				double sum = 0.0;
				List<CyEdge> edgeList;
				if (directed) {
					edgeList = network.getAdjacentEdgeList(node,
							CyEdge.Type.OUTGOING);
				} else {
					edgeList = network.getAdjacentEdgeList(node,
							CyEdge.Type.ANY);
				}

				for (CyEdge edge : edgeList) {

					sum += function.getWeight(edge);
				}
				weights[nodeIndex] = sum;
			}
			nodeIndex++;
		}

		while (true) {

			double r = 0.0;
			for (CyNode node : nodeList) {

				int s_index = nodeIndexMap.get(node);
				boolean out;
				if (directed) {
					out = network.getAdjacentEdgeList(node,
							CyEdge.Type.OUTGOING).size() > 0;
				} else {
					out = network.getAdjacentEdgeList(node, CyEdge.Type.ANY)
							.size() > 0;
				}

				if (out) {
					r += (1.0 - probability) * (pageranks[s_index] / nodeCount);
				} else {
					r += (pageranks[s_index] / nodeCount);
				}
			}

			boolean completed = true;

			for (CyNode node : nodeList) {

				int s_index = nodeIndexMap.get(node);
				temp[s_index] = updateValueForNode(network, node, r, function,
						pageranks, nodeIndexMap, weights, directed, probability);

				if ((temp[s_index] - pageranks[s_index]) / pageranks[s_index] >= epsilon) {
					completed = false;
				}
			}
			pageranks = temp;
			temp = new double[nodeCount];
			if (completed) {
				break;
			}
		}

		return new PagerankResultImpl(nodeIndexMap, pageranks);
	}

	private double updateValueForNode(CyNetwork network, CyNode node, double r,
			WeightFunction function, double pageranks[],
			Map<CyNode, Integer> nodeIndexMap, double weights[],
			boolean directed, double probability) {

		double res = r;

		List<CyEdge> edgeList;
		if (directed) {
			edgeList = network.getAdjacentEdgeList(node, CyEdge.Type.INCOMING);
		} else {
			edgeList = network.getAdjacentEdgeList(node, CyEdge.Type.ANY);
		}

		for (CyEdge edge : edgeList) {

			CyNode neighbor;
			if (node == edge.getSource()) {
				neighbor = edge.getTarget();
			} else {
				neighbor = edge.getSource();
			}

			int neighborIndex = nodeIndexMap.get(neighbor);
			int normalize;

			if (directed) {
				normalize = network.getAdjacentEdgeList(neighbor,
						CyEdge.Type.OUTGOING).size();
			} else {
				normalize = network.getAdjacentEdgeList(neighbor,
						CyEdge.Type.ANY).size();
			}

			if (function != null) {
				double weight = function.getWeight(edge)
						/ weights[neighborIndex];
				res += probability * pageranks[neighborIndex] * weight;
			} else {
				res += probability * (pageranks[neighborIndex] / normalize);
			}
		}

		return res;
	}
}
