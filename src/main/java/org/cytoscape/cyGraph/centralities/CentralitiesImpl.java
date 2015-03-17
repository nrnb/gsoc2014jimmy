/**
 * 
 */
package org.cytoscape.cyGraph.centralities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.cytoscape.cyGraph.algorithms.GraphAlgorithmsImpl;
import org.cytoscape.cyGraph.algorithms.api.DijkstraResult;
import org.cytoscape.cyGraph.algorithms.api.GraphAlgorithms;
import org.cytoscape.cyGraph.algorithms.api.WeightFunction;
import org.cytoscape.cyGraph.centralities.api.Centralities;
import org.cytoscape.cyGraph.centralities.api.NetworkStatsResults;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * @author Jimmy
 * 
 */
public class CentralitiesImpl implements Centralities {

	@Override
	public NetworkStatsResults computeAll(CyNetwork network, boolean directed,
			WeightFunction function) {

		Map<CyNode, DijkstraResult> nodeDistanceMap = new IdentityHashMap<CyNode, DijkstraResult>();
		GraphAlgorithms gAlgo = new GraphAlgorithmsImpl();

		List<CyNode> nodeList = network.getNodeList();

		Map<CyNode, Double> eccentricityMap = new IdentityHashMap<CyNode, Double>();
		Map<CyNode, Double> radialityMap = new IdentityHashMap<CyNode, Double>();
		Map<CyNode, Double> closenessMap = new IdentityHashMap<CyNode, Double>();
		Map<CyNode, Double> cCoefficientMap = new IdentityHashMap<CyNode, Double>();
		Map<CyNode, Double> nodeBetweennessMap = new IdentityHashMap<CyNode, Double>();
		Map<CyEdge, Double> edgeBetweennessMap = new IdentityHashMap<CyEdge, Double>();
		Map<CyNode, Double> nodeStressMap = new IdentityHashMap<CyNode, Double>();

		double diameter = 0.0;
		double eccentricity = 0.0;
		for (CyNode tempNode : nodeList) {

			DijkstraResult tempStats = gAlgo.findPath(network, tempNode,
					directed, function);
			nodeDistanceMap.put(tempNode, tempStats);
			eccentricity = tempStats.getEccentricity();
			eccentricityMap.put(tempNode, eccentricity);
			if (eccentricity > diameter) {
				diameter = eccentricity;
			}
		}

		Map<CyIdentifiable, List<Double>> bStressMap = getBetweennessStress(
				network, directed);

		for (CyNode tempNode : nodeList) {

			closenessMap.put(tempNode,
					getCloseness(network, nodeDistanceMap.get(tempNode)));
			radialityMap.put(
					tempNode,
					getRadiality(network, diameter,
							nodeDistanceMap.get(tempNode)));
			cCoefficientMap.put(tempNode,
					getClusteringCoefficient(network, tempNode, directed));
			nodeBetweennessMap.put(tempNode, bStressMap.get(tempNode).get(0));
			nodeStressMap.put(tempNode, bStressMap.get(tempNode).get(1));

		}

		for (CyEdge edge : network.getEdgeList()) {
			edgeBetweennessMap.put(edge, bStressMap.get(edge).get(0));
		}

		return new NetworkStatsResultsImpl(radialityMap, closenessMap,
				cCoefficientMap, nodeBetweennessMap, edgeBetweennessMap,
				nodeStressMap, eccentricityMap, diameter);
	}

	@Override
	public Map<CyIdentifiable, List<Double>> getBetweennessStress(
			CyNetwork network, boolean directed) {

		Map<CyIdentifiable, List<Double>> betweennessStressMap = new IdentityHashMap<CyIdentifiable, List<Double>>();

		Map<CyEdge, Double> edgeBetweennessMap = new IdentityHashMap<CyEdge, Double>();
		Map<CyNode, NodeMetaData> nodeToNodeMetaDataMap = new IdentityHashMap<CyNode, NodeMetaData>();

		List<CyNode> nodeList = network.getNodeList();

		for (CyNode node : nodeList) {
			List<Double> list = new ArrayList<Double>();
			list.add(0.0);
			list.add(0.0);
			betweennessStressMap.put(node, list);
			nodeToNodeMetaDataMap.put(node, new NodeMetaData(node, 0.0, -1,
					0.0, 0.0));

		}

		for (CyNode node : nodeList) {

			Map<CyEdge, Double> edgeDependencyMap = new IdentityHashMap<CyEdge, Double>();
			for (CyNode node2 : nodeList) {
				nodeToNodeMetaDataMap.get(node2).resetAll();
			}

			LinkedList<CyNode> nodeStack = new LinkedList<CyNode>();
			Queue<CyNode> nodeQueue = new LinkedList<CyNode>();
			nodeQueue.add(node);
			nodeToNodeMetaDataMap.get(node).setDistance(0.0);
			nodeToNodeMetaDataMap.get(node).setShortestPaths(1.0);

			while (!nodeQueue.isEmpty()) {
				CyNode node2 = nodeQueue.remove();
				nodeStack.push(node2);
				NodeMetaData m2Node = nodeToNodeMetaDataMap.get(node2);
				List<CyNode> neighborList;
				if (directed) {
					neighborList = network.getNeighborList(node2,
							CyEdge.Type.OUTGOING);
				} else {
					neighborList = network.getNeighborList(node2,
							CyEdge.Type.ANY);
				}
				for (CyNode node3 : neighborList) {
					List<CyEdge> edges = network.getConnectingEdgeList(node2,
							node3, CyEdge.Type.ANY);
					NodeMetaData m3Node = nodeToNodeMetaDataMap.get(node3);
					if (m3Node.getDistance() < 0) {
						nodeQueue.add(node3);
						m3Node.setDistance(m2Node.getDistance() + 1.0);

					}

					if (m3Node.getDistance() == (m2Node.getDistance() + 1.0)) {
						m3Node.setShortestPaths(m3Node.getShortestPaths()
								+ m2Node.getShortestPaths());

						m3Node.getPredecessors().add(node2);

						for (CyEdge edge : edges) {
							m2Node.getConnectingEdges().add(edge);

						}
					}

					for (CyEdge edge : network.getConnectingEdgeList(node2,
							node3, CyEdge.Type.ANY)) {
						if (!edgeDependencyMap.containsKey(edge)) {
							edgeDependencyMap.put(edge, 0.0);
						}
					}

				}

			}
			while (!nodeStack.isEmpty()) {
				CyNode node3 = nodeStack.pop();
				NodeMetaData m3Node = nodeToNodeMetaDataMap.get(node3);
				for (CyNode node4 : m3Node.getPredecessors()) {

					NodeMetaData m4Node = nodeToNodeMetaDataMap.get(node4);

					m4Node.setBetweennessDependency(m4Node
							.getBetweennessDependency()
							+ ((m4Node.getShortestPaths() / m3Node
									.getShortestPaths()) * (1 + m3Node
									.getBetweennessDependency())));

					m4Node.setStressDependency(m4Node.getStressDependency() + 1
							+ m3Node.getStressDependency());

					List<CyEdge> edges = network.getConnectingEdgeList(node4,
							node3, CyEdge.Type.ANY);

					if (edges.size() != 0) {

						CyEdge compedge = edges.get(0);
						List<CyEdge> outEdges = m3Node.getConnectingEdges();
						double oldbetweenness = 0.0;
						double newbetweenness = 0.0;
						for (CyEdge edge : edges) {
							if (edgeBetweennessMap.containsKey(edge)) {
								oldbetweenness = edgeBetweennessMap.get(edge);
								break;
							}
						}
						if (outEdges.size() == 0) {
							newbetweenness = m4Node.getShortestPaths()
									/ m3Node.getShortestPaths();
						} else {
							double neighborbetweenness = 0.0;
							for (CyEdge neighbor : outEdges) {
								if (!edges.contains(neighbor)) {
									neighborbetweenness += edgeDependencyMap
											.get(neighbor);
								}
							}

							newbetweenness = (1 + neighborbetweenness)
									* (m4Node.getShortestPaths() / m3Node
											.getShortestPaths());
						}
						edgeDependencyMap.put(compedge, newbetweenness);
						for (CyEdge edge : edges) {
							edgeBetweennessMap.put(edge, newbetweenness
									+ oldbetweenness);
						}
					}

				}
				if (node3 != node) {
					List<Double> list = betweennessStressMap.get(node3);
					double temp = list.remove(0);
					list.add(0, temp + m3Node.getBetweennessDependency());
					temp = list.remove(1);
					list.add(
							1,
							temp + m3Node.getShortestPaths()
									* m3Node.getStressDependency());
					betweennessStressMap.put(node3, list);

				}
			}
		}
		int n = network.getNodeCount();
		for (CyNode node : nodeList) {
			List<Double> list = betweennessStressMap.get(node);
			double temp = list.remove(0);
			list.add(0, temp / ((n - 1) * (n - 2)));
			betweennessStressMap.put(node, list);
		}

		for (CyEdge edge : network.getEdgeList()) {
			List<Double> list = new ArrayList<Double>();
			list.add(edgeBetweennessMap.get(edge));
			betweennessStressMap.put(edge, list);
		}
		return betweennessStressMap;
	}

	@Override
	public double getCloseness(CyNetwork network, CyNode node,
			boolean directed, WeightFunction function) {
		// TODO Auto-generated method stub
		GraphAlgorithms gAlgo = new GraphAlgorithmsImpl();
		DijkstraResult dStats = gAlgo.findPath(network, node, directed,
				function);

		double sum = 0.0;
		for (CyNode tempNode : network.getNodeList()) {
			sum += dStats.getDistanceTo(tempNode);
		}
		return (network.getNodeCount() - 1) / sum;
	}

	public double getCloseness(CyNetwork network, DijkstraResult dStats) {

		double sum = 0.0;
		for (CyNode tempNode : network.getNodeList()) {
			sum += dStats.getDistanceTo(tempNode);
		}
		return (network.getNodeCount() - 1) / sum;
	}

	@Override
	public double getClusteringCoefficient(CyNetwork network, CyNode node,
			boolean directed) {
		List<CyNode> neighborList;
		neighborList = network.getNeighborList(node, CyEdge.Type.ANY);
		double edgeCount = 0.0;
		Set<CyEdge> edgeSet = new HashSet<CyEdge>();
		for (CyNode neighbor1 : neighborList) {

			for (CyNode neighbor2 : neighborList) {
				if (neighbor1 == neighbor2)
					continue;
				if (directed) {
					for (CyEdge edge : network.getConnectingEdgeList(neighbor1,
							neighbor2, CyEdge.Type.ANY)) {
						edgeSet.add(edge);
					}
				} else {
					for (@SuppressWarnings("unused")
					CyEdge edge : network.getConnectingEdgeList(neighbor1,
							neighbor2, CyEdge.Type.ANY)) {
						edgeCount++;
						break;
					}
				}
			}
		}
		if (directed)
			return ((double) edgeSet.size() / (neighborList.size() * (neighborList
					.size() - 1)));
		else
			return edgeCount
					/ (neighborList.size() * (neighborList.size() - 1));
	}

	@Override
	public int getDegree(CyNetwork network, CyNode node) {
		return network.getNeighborList(node, CyEdge.Type.ANY).size();
	}

	@Override
	public double getDiameter(CyNetwork network, boolean directed,
			WeightFunction function) {
		Map<CyNode, DijkstraResult> nodeDistanceMap = new IdentityHashMap<CyNode, DijkstraResult>();
		GraphAlgorithms gAlgo = new GraphAlgorithmsImpl();
		double diameter = 0.0;
		double eccentricity = 0.0;
		for (CyNode tempNode : network.getNodeList()) {

			DijkstraResult tempStats = gAlgo.findPath(network, tempNode,
					directed, function);
			nodeDistanceMap.put(tempNode, tempStats);
			eccentricity = tempStats.getEccentricity();
			if (eccentricity > diameter) {
				diameter = eccentricity;
			}
		}
		return diameter;
	}
	
	@Override
	public int getIndegree(CyNetwork network, CyNode node) {
		return network.getNeighborList(node, CyEdge.Type.INCOMING).size();
	}

	@Override
	public int getOutdegree(CyNetwork network, CyNode node) {
		return network.getNeighborList(node, CyEdge.Type.OUTGOING).size();
	}

	@Override
	public double getEccentricity(CyNetwork network, CyNode node,
			boolean directed, WeightFunction function) {
		GraphAlgorithms gAlgo = new GraphAlgorithmsImpl();
		DijkstraResult dStats = gAlgo.findPath(network, node, directed,
				function);
		double max = dStats.getDistanceTo(node);
		for (CyNode tempNode : network.getNodeList()) {
			double temp = dStats.getDistanceTo(tempNode);
			if (temp > max) {
				max = temp;
			}
		}
		return 1 / max;
	}

	@Override
	public double getRadiality(CyNetwork network, CyNode node,
			boolean directed, WeightFunction function) {
		// TODO Auto-generated method stub
		Map<CyNode, DijkstraResult> nodeDistanceMap = new IdentityHashMap<CyNode, DijkstraResult>();
		GraphAlgorithms gAlgo = new GraphAlgorithmsImpl();
		double diameter = 0.0;
		double eccentricity = 0.0;
		for (CyNode tempNode : network.getNodeList()) {

			DijkstraResult tempStats = gAlgo.findPath(network, tempNode,
					directed, function);
			nodeDistanceMap.put(tempNode, tempStats);
			eccentricity = tempStats.getEccentricity();
			if (eccentricity > diameter) {
				diameter = eccentricity;
			}
		}

		double radiality = 0.0;
		for (CyNode tempNode : network.getNodeList()) {
			if (node != tempNode)
				radiality = radiality
						+ (diameter + 1 - nodeDistanceMap.get(node)
								.getDistanceTo(tempNode)) / diameter;
		}
		return radiality / (network.getNodeCount() - 1);
	}

	public double getRadiality(CyNetwork network, double diameter,
			DijkstraResult dStats) {

		double radiality = 0.0;
		for (CyNode tempNode : network.getNodeList()) {
			if (dStats.getSource() != tempNode)
				radiality = radiality
						+ (diameter + 1 - dStats.getDistanceTo(tempNode))
						/ diameter;
		}
		return radiality / (network.getNodeCount() - 1);
	}

}
