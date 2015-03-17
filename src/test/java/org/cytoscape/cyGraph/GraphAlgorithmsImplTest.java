/**
 * 
 */
package org.cytoscape.cyGraph;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.cytoscape.cyGraph.algorithms.BfsCallback;
import org.cytoscape.cyGraph.algorithms.GraphAlgorithmsImpl;
import org.cytoscape.cyGraph.algorithms.api.BellmanFordResult;
import org.cytoscape.cyGraph.algorithms.api.BfsResult;
import org.cytoscape.cyGraph.algorithms.api.Callback;
import org.cytoscape.cyGraph.algorithms.api.DfsResult;
import org.cytoscape.cyGraph.algorithms.api.DijkstraResult;
import org.cytoscape.cyGraph.algorithms.api.FloydWarshallResult;
import org.cytoscape.cyGraph.algorithms.api.GraphAlgorithms;
import org.cytoscape.cyGraph.algorithms.api.HITSResults;
import org.cytoscape.cyGraph.algorithms.api.MSTResult;
import org.cytoscape.cyGraph.algorithms.api.PagerankResults;
import org.cytoscape.cyGraph.algorithms.api.WeightFunction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.NetworkTestSupport;
import org.junit.Test;

/**
 * @author Jimmy
 *
 */
public class GraphAlgorithmsImplTest {

	private CyNetwork network, network2;
	
	@SuppressWarnings("unused")
	@Test
	public void breadthFirstSearchTest(){
		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();
		
		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		CyNode node5 = network.addNode();
		CyNode node6 = network.addNode();
		CyNode node7 = network.addNode();
		CyNode node8 = network.addNode();
		
		CyEdge edge1 = network.addEdge(node1, node2, true);
		CyEdge edge2 = network.addEdge(node2, node3, true);
		CyEdge edge3 = network.addEdge(node3, node4, true);
		CyEdge edge4 = network.addEdge(node4, node5, true);
		CyEdge edge5 = network.addEdge(node4, node6, true);
		CyEdge edge6 = network.addEdge(node5, node6, true);
		CyEdge edge7 = network.addEdge(node5, node8, true);
		CyEdge edge8 = network.addEdge(node6, node8, true);
		CyEdge edge9 = network.addEdge(node6, node7, true);
		CyEdge edge10 = network.addEdge(node7, node8, true);
		
		Callback callback = new BfsCallback(node8);
		
		GraphAlgorithms graphAlgo = new GraphAlgorithmsImpl();
		BfsResult bfsStats = graphAlgo.breadthFirstSearch(network, node3, false, callback);
		
		assertEquals(0,bfsStats.getDistanceTo(node3));
		assertEquals(1,bfsStats.getDistanceTo(node4));
		assertEquals(1,bfsStats.getDistanceTo(node2));
		assertEquals(2,bfsStats.getDistanceTo(node1));
		assertEquals(2,bfsStats.getDistanceTo(node5));
		assertEquals(2,bfsStats.getDistanceTo(node6));
		assertEquals(3,bfsStats.getDistanceTo(node7));
		assertEquals(3,bfsStats.getDistanceTo(node8));
		
		BfsResult bfsStats2 = graphAlgo.breadthFirstSearch(network, node1, true, callback);
		assertEquals(1,bfsStats2.getDistanceTo(node2));
		assertEquals(3,bfsStats2.getDistanceTo(node4));
		assertEquals(4,bfsStats2.getDistanceTo(node5));
		
		Stack<CyNode> s = (Stack<CyNode>) bfsStats2.getPathTo(node6);
		assertEquals(node1,s.pop());
		assertEquals(node2,s.pop());
		assertEquals(node3,s.pop());
		assertEquals(node4,s.pop());
		assertEquals(node6,s.pop());
	}
	
	@SuppressWarnings("unused")
	@Test
	public void depthFirstSearchTest(){
		
		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();
		
		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		CyNode node5 = network.addNode();
		CyNode node6 = network.addNode();
		CyNode node7 = network.addNode();
		
		CyEdge edge1 = network.addEdge(node1, node2, false);
		CyEdge edge2 = network.addEdge(node1, node3, false);
		CyEdge edge3 = network.addEdge(node1, node6, false);
		CyEdge edge4 = network.addEdge(node2, node3, false);
		CyEdge edge5 = network.addEdge(node3, node5, false);
		CyEdge edge6 = network.addEdge(node3, node4, false);
		CyEdge edge7 = network.addEdge(node4, node5, false);
		CyEdge edge8 = network.addEdge(node4, node6, false);
		
		GraphAlgorithms graphAlgo = new GraphAlgorithmsImpl();
		DfsResult dfsResult = graphAlgo.depthFirstSearch(network, node1, false, null);
		assertEquals(false, dfsResult.hasPathTo(node7));
		assertEquals(true, dfsResult.hasPathTo(node6));
	}
	
	@Test
	public void findAllPairShortestPathTest(){
		
		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();
		
		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		CyNode node5 = network.addNode();
		
		CyEdge edge1 = network.addEdge(node1, node2, true);
		CyEdge edge2 = network.addEdge(node1, node3, true);
		CyEdge edge3 = network.addEdge(node1, node5, true);
		CyEdge edge4 = network.addEdge(node2, node4, true);
		CyEdge edge5 = network.addEdge(node2, node5, true);
		CyEdge edge6 = network.addEdge(node3, node2, true);
		CyEdge edge7 = network.addEdge(node4, node3, true);
		CyEdge edge8 = network.addEdge(node4, node1, true);
		CyEdge edge9 = network.addEdge(node5, node4, true);
		
		network.getDefaultEdgeTable().createColumn("Weight", Double.class,
				false);
		network.getRow(edge1).set("Weight", 3.0);
		network.getRow(edge2).set("Weight", 8.0);
		network.getRow(edge3).set("Weight", -4.0);
		network.getRow(edge4).set("Weight", 1.0);
		network.getRow(edge5).set("Weight", 7.0);
		network.getRow(edge6).set("Weight", 4.0);
		network.getRow(edge7).set("Weight", -5.0);
		network.getRow(edge8).set("Weight", 2.0);
		network.getRow(edge9).set("Weight", 6.0);
		
		GraphAlgorithms graphAlgo = new GraphAlgorithmsImpl();
		FloydWarshallResult fResult = graphAlgo.findAllPairShortestPath(network, true, new WeightFunction() {
			public double getWeight(CyEdge edge) {
				return network.getRow(edge).get("Weight", Double.class);
			}
		});
		
		assertEquals(0.0, fResult.getDistance(node1, node1), 0.01);
		assertEquals(1.0, fResult.getDistance(node1, node2), 0.01);
		assertEquals(-3.0, fResult.getDistance(node1, node3), 0.01);
		assertEquals(2.0, fResult.getDistance(node1, node4), 0.01);
		assertEquals(-4.0, fResult.getDistance(node1, node5), 0.01);
		
		
		assertEquals(3.0, fResult.getDistance(node2, node1), 0.01);
		assertEquals(0.0, fResult.getDistance(node2, node2), 0.01);
		assertEquals(-4.0, fResult.getDistance(node2, node3), 0.01);
		assertEquals(1.0, fResult.getDistance(node2, node4), 0.01);
		assertEquals(-1.0, fResult.getDistance(node2, node5), 0.01);
		
		assertEquals(7.0, fResult.getDistance(node3, node1), 0.01);
		assertEquals(4.0, fResult.getDistance(node3, node2), 0.01);
		assertEquals(0.0, fResult.getDistance(node3, node3), 0.01);
		assertEquals(5.0, fResult.getDistance(node3, node4), 0.01);
		assertEquals(3.0, fResult.getDistance(node3, node5), 0.01);
		
		assertEquals(2.0, fResult.getDistance(node4, node1), 0.01);
		assertEquals(-1.0, fResult.getDistance(node4, node2), 0.01);
		assertEquals(-5.0, fResult.getDistance(node4, node3), 0.01);
		assertEquals(0.0, fResult.getDistance(node4, node4), 0.01);
		assertEquals(-2.0, fResult.getDistance(node4, node5), 0.01);
		
		assertEquals(8.0, fResult.getDistance(node5, node1), 0.01);
		assertEquals(5.0, fResult.getDistance(node5, node2), 0.01);
		assertEquals(1.0, fResult.getDistance(node5, node3), 0.01);
		assertEquals(6.0, fResult.getDistance(node5, node4), 0.01);
		assertEquals(0.0, fResult.getDistance(node5, node5), 0.01);
		
	}
	
	@Test
	public void findKruskalTreeTest(){
		
		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();

		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		CyNode node5 = network.addNode();
		CyNode node6 = network.addNode();
		CyNode node7 = network.addNode();
		CyNode node8 = network.addNode();
		CyNode node9 = network.addNode();

		CyEdge edge1 = network.addEdge(node1, node2, false);
		CyEdge edge2 = network.addEdge(node2, node3, false);
		CyEdge edge3 = network.addEdge(node3, node4, false);
		CyEdge edge4 = network.addEdge(node4, node5, false);
		CyEdge edge5 = network.addEdge(node5, node6, false);
		CyEdge edge6 = network.addEdge(node6, node7, false);
		CyEdge edge7 = network.addEdge(node7, node8, false);
		CyEdge edge8 = network.addEdge(node8, node1, false);
		CyEdge edge9 = network.addEdge(node8, node2, false);
		CyEdge edge10 = network.addEdge(node4, node6, false);
		CyEdge edge11 = network.addEdge(node3, node6, false);
		CyEdge edge12 = network.addEdge(node8, node9, false);
		CyEdge edge13 = network.addEdge(node7, node9, false);
		CyEdge edge14 = network.addEdge(node9, node3, false);

		network.getDefaultEdgeTable().createColumn("Weight", Double.class,
				false);
		network.getRow(edge1).set("Weight", 4.0);
		network.getRow(edge2).set("Weight", 8.0);
		network.getRow(edge3).set("Weight", 7.0);
		network.getRow(edge4).set("Weight", 9.0);
		network.getRow(edge5).set("Weight", 10.0);
		network.getRow(edge6).set("Weight", 2.0);
		network.getRow(edge7).set("Weight", 1.0);
		network.getRow(edge8).set("Weight", 8.0);
		network.getRow(edge9).set("Weight", 11.0);
		network.getRow(edge10).set("Weight", 14.0);
		network.getRow(edge11).set("Weight", 4.0);
		network.getRow(edge12).set("Weight", 7.0);
		network.getRow(edge13).set("Weight", 6.0);
		network.getRow(edge14).set("Weight", 2.0);
		
		GraphAlgorithms graphAlgo = new GraphAlgorithmsImpl();
	    MSTResult kruskalStats = graphAlgo.findKruskalTree(network,
				new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				});
		assertEquals(37, kruskalStats.getWeight(), 0.01);

		Iterable<CyEdge> edgeList = kruskalStats.getMST();
		Set<CyEdge> mstEdges = new HashSet<CyEdge>(
				(Collection<CyEdge>) edgeList);

		assertEquals(true, mstEdges.contains(edge1));
		assertEquals(true, mstEdges.contains(edge3));
		assertEquals(true, mstEdges.contains(edge6));
		assertEquals(true, mstEdges.contains(edge14));
		assertEquals(true, mstEdges.contains(edge4));
		assertEquals(true, mstEdges.contains(edge7));
		assertEquals(true, mstEdges.contains(edge8));
		assertEquals(true, mstEdges.contains(edge11));
		assertEquals(false, mstEdges.contains(edge2));
		assertEquals(false, mstEdges.contains(edge5));
		assertEquals(false, mstEdges.contains(edge9));
		assertEquals(false, mstEdges.contains(edge10));
		assertEquals(false, mstEdges.contains(edge12));
		assertEquals(false, mstEdges.contains(edge13));
	}
	
	@Test
	public void findPathTest(){
		
		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();
		
		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		CyNode node5 = network.addNode();
		
		CyEdge edge1 = network.addEdge(node1, node2, true);
		CyEdge edge2 = network.addEdge(node1, node3, true);
		CyEdge edge3 = network.addEdge(node2, node4, true);
		CyEdge edge4 = network.addEdge(node3, node5, true);
		CyEdge edge5 = network.addEdge(node5, node1, true);
		CyEdge edge6 = network.addEdge(node2, node3, true);
		CyEdge edge7 = network.addEdge(node3, node2, true);
		CyEdge edge8 = network.addEdge(node3, node4, true);
		CyEdge edge9 = network.addEdge(node4, node5, true);
		CyEdge edge10 = network.addEdge(node5, node4, true);
		
		network.getDefaultEdgeTable().createColumn("Weight", Double.class,
				false);
		
		network.getRow(edge1).set("Weight", 10.0);
		network.getRow(edge2).set("Weight", 5.0);
		network.getRow(edge3).set("Weight", 1.0);
		network.getRow(edge4).set("Weight", 2.0);
		network.getRow(edge5).set("Weight", 7.0);
		network.getRow(edge6).set("Weight", 2.0);
		network.getRow(edge7).set("Weight", 3.0);
		network.getRow(edge8).set("Weight", 9.0);
		network.getRow(edge9).set("Weight", 4.0);
		network.getRow(edge10).set("Weight", 6.0);
		
		GraphAlgorithms graphAlgo = new GraphAlgorithmsImpl();
		DijkstraResult dijkstraStats = graphAlgo.findPath(network, node1, true, new WeightFunction() {
			public double getWeight(CyEdge edge) {
				return network.getRow(edge).get("Weight", Double.class);
			}
		});
		
		
		assertEquals(8.0, dijkstraStats.getDistanceTo(node2),0.01);
		assertEquals(7.0, dijkstraStats.getDistanceTo(node5),0.01);
		assertEquals(9.0, dijkstraStats.getDistanceTo(node4),0.01);
		assertEquals(5.0, dijkstraStats.getDistanceTo(node3),0.01);
		assertEquals(0.0, dijkstraStats.getDistanceTo(node1),0.01);
		
		
		network2 = networkTestSupport.getNetwork();
		
		CyNode node_1 = network2.addNode();
		CyNode node_2 = network2.addNode();
		CyNode node_3 = network2.addNode();
		CyNode node_4 = network2.addNode();
		CyNode node_5 = network2.addNode();
		
		CyEdge edge_1 = network2.addEdge(node_1, node_2, false);
		CyEdge edge_2 = network2.addEdge(node_2, node_3, false);
		CyEdge edge_3 = network2.addEdge(node_2, node_4, false);
		CyEdge edge_4 = network2.addEdge(node_3, node_5, false);
		CyEdge edge_5 = network2.addEdge(node_4, node_5, false);
		
		network2.getDefaultEdgeTable().createColumn("Weight", Double.class,
				false);
		network2.getRow(edge_1).set("Weight", 1.0);
		network2.getRow(edge_2).set("Weight", 1.0);
		network2.getRow(edge_3).set("Weight", 1.0);
		network2.getRow(edge_4).set("Weight", 1.0);
		network2.getRow(edge_5).set("Weight", 1.0);
		
		DijkstraResult dijkstraStats1 = graphAlgo.findPath(network2, node_2, false, new WeightFunction() {
			public double getWeight(CyEdge edge) {
				return network2.getRow(edge).get("Weight", Double.class);
			}
		});
		
		assertEquals(0.0, dijkstraStats1.getDistanceTo(node_2),0.01);
		assertEquals(2.0, dijkstraStats1.getDistanceTo(node_5),0.01);
		assertEquals(1.0, dijkstraStats1.getDistanceTo(node_4),0.01);
		assertEquals(1.0, dijkstraStats1.getDistanceTo(node_3),0.01);
		assertEquals(1.0, dijkstraStats1.getDistanceTo(node_1),0.01);
	}
	
	@Test
	public void findPrimTreeTest(){
		
		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();

		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		CyNode node5 = network.addNode();
		CyNode node6 = network.addNode();
		CyNode node7 = network.addNode();
		CyNode node8 = network.addNode();
		CyNode node9 = network.addNode();

		CyEdge edge1 = network.addEdge(node1, node2, false);
		CyEdge edge2 = network.addEdge(node2, node3, false);
		CyEdge edge3 = network.addEdge(node3, node4, false);
		CyEdge edge4 = network.addEdge(node4, node5, false);
		CyEdge edge5 = network.addEdge(node5, node6, false);
		CyEdge edge6 = network.addEdge(node6, node7, false);
		CyEdge edge7 = network.addEdge(node7, node8, false);
		CyEdge edge8 = network.addEdge(node8, node1, false);
		CyEdge edge9 = network.addEdge(node8, node2, false);
		CyEdge edge10 = network.addEdge(node4, node6, false);
		CyEdge edge11 = network.addEdge(node3, node6, false);
		CyEdge edge12 = network.addEdge(node8, node9, false);
		CyEdge edge13 = network.addEdge(node7, node9, false);
		CyEdge edge14 = network.addEdge(node9, node3, false);

		network.getDefaultEdgeTable().createColumn("Weight", Double.class,
				false);
		network.getRow(edge1).set("Weight", 4.0);
		network.getRow(edge2).set("Weight", 8.0);
		network.getRow(edge3).set("Weight", 7.0);
		network.getRow(edge4).set("Weight", 9.0);
		network.getRow(edge5).set("Weight", 10.0);
		network.getRow(edge6).set("Weight", 2.0);
		network.getRow(edge7).set("Weight", 1.0);
		network.getRow(edge8).set("Weight", 8.0);
		network.getRow(edge9).set("Weight", 11.0);
		network.getRow(edge10).set("Weight", 14.0);
		network.getRow(edge11).set("Weight", 4.0);
		network.getRow(edge12).set("Weight", 7.0);
		network.getRow(edge13).set("Weight", 6.0);
		network.getRow(edge14).set("Weight", 2.0);


		GraphAlgorithms graphAlgo = new GraphAlgorithmsImpl();
		MSTResult primStats = graphAlgo.findPrimTree(network, new WeightFunction() {
			public double getWeight(CyEdge edge) {
				return network.getRow(edge).get("Weight", Double.class);
			}
		});
		assertEquals(37, primStats.getWeight(), 0.01);

		Iterable<CyEdge> edgeList = primStats.getMST();
		Set<CyEdge> primEdges = new HashSet<CyEdge>(
				(Collection<CyEdge>) edgeList);

		assertEquals(true, primEdges.contains(edge1));
		assertEquals(true, primEdges.contains(edge3));
		assertEquals(true, primEdges.contains(edge6));
		assertEquals(true, primEdges.contains(edge14));
		assertEquals(true, primEdges.contains(edge4));
		assertEquals(true, primEdges.contains(edge7));
		assertEquals(true, primEdges.contains(edge8));
		assertEquals(true, primEdges.contains(edge11));
		assertEquals(false, primEdges.contains(edge2));
		assertEquals(false, primEdges.contains(edge5));
		assertEquals(false, primEdges.contains(edge9));
		assertEquals(false, primEdges.contains(edge10));
		assertEquals(false, primEdges.contains(edge12));
		assertEquals(false, primEdges.contains(edge13));
	}
	
	@Test
	public void findShortestPathTest(){
		
		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();

		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		CyNode node5 = network.addNode();

		CyEdge edge1 = network.addEdge(node1, node2, true);
		CyEdge edge2 = network.addEdge(node1, node3, true);
		CyEdge edge3 = network.addEdge(node2, node4, true);
		CyEdge edge4 = network.addEdge(node4, node2, true);
		CyEdge edge5 = network.addEdge(node2, node3, true);
		CyEdge edge6 = network.addEdge(node3, node5, true);
		CyEdge edge7 = network.addEdge(node5, node4, true);
		CyEdge edge8 = network.addEdge(node2, node5, true);
		CyEdge edge9 = network.addEdge(node3, node4, true);
		CyEdge edge10 = network.addEdge(node5, node1, true);

		network.getDefaultEdgeTable().createColumn("Weight", Double.class,
				false);
		
		network.getRow(edge1).set("Weight", 6.0);
		network.getRow(edge2).set("Weight", 7.0);
		network.getRow(edge3).set("Weight", 5.0);
		network.getRow(edge4).set("Weight", -2.0);
		network.getRow(edge5).set("Weight", 8.0);
		network.getRow(edge6).set("Weight", 9.0);
		network.getRow(edge7).set("Weight", 7.0);
		network.getRow(edge8).set("Weight", -4.0);
		network.getRow(edge9).set("Weight", -3.0);
		network.getRow(edge10).set("Weight", 2.0);
		
		GraphAlgorithms graphAlgo = new GraphAlgorithmsImpl();
		BellmanFordResult bellmanFordStats = graphAlgo.findShortestPath(network, node1,
				true, new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				});

		assertEquals(0.0, bellmanFordStats.getDistanceTo(node1), 0.01);
		assertEquals(2.0, bellmanFordStats.getDistanceTo(node2), 0.01);
		assertEquals(7.0, bellmanFordStats.getDistanceTo(node3), 0.01);
		assertEquals(4.0, bellmanFordStats.getDistanceTo(node4), 0.01);
		assertEquals(-2.0, bellmanFordStats.getDistanceTo(node5), 0.01);

	}
	
	@SuppressWarnings("unused")
	@Test
	public void getHITSScoresTest(){
		
		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();
		
		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		
		CyEdge edge1 = network.addEdge(node1, node3, true);
		CyEdge edge2 = network.addEdge(node2, node3, true);
		CyEdge edge3 = network.addEdge(node1, node4, true);
		CyEdge edge4 = network.addEdge(node3, node4, true);
		
		GraphAlgorithms graphAlgo = new GraphAlgorithmsImpl();
		HITSResults hitsResults = graphAlgo.getHITSScores(network, true);
		assertEquals(0,hitsResults.getAuthorityValue(node1),0.001);
		assertEquals(0,hitsResults.getAuthorityValue(node2),0.001);
		assertEquals(0.707,hitsResults.getAuthorityValue(node3),0.001);
		assertEquals(0.707,hitsResults.getAuthorityValue(node4),0.001);
		
		assertEquals(0.816,hitsResults.getHubValue(node1),0.001);
		assertEquals(0.408,hitsResults.getHubValue(node2),0.001);
		assertEquals(0.408,hitsResults.getHubValue(node3),0.001);
		assertEquals(0.0,hitsResults.getHubValue(node4),0.001);
		
	}
	
	@SuppressWarnings("unused")
	@Test
	public void getPageranksTest(){
		
		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();
		
		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		CyNode node5 = network.addNode();
		CyNode node6 = network.addNode();
		CyNode node7 = network.addNode();
		CyNode node8 = network.addNode();
		
		CyEdge edge1 = network.addEdge(node1, node2, true);
		CyEdge edge2 = network.addEdge(node1, node3, true);
		CyEdge edge3 = network.addEdge(node2, node4, true);
		CyEdge edge4 = network.addEdge(node4, node2, true);
		CyEdge edge5 = network.addEdge(node3, node2, true);
		CyEdge edge6 = network.addEdge(node3, node5, true);
		CyEdge edge7 = network.addEdge(node4, node5, true);
		CyEdge edge8 = network.addEdge(node4, node6, true);
		CyEdge edge9 = network.addEdge(node5, node6, true);
		CyEdge edge10 = network.addEdge(node5, node7, true);
		CyEdge edge11 = network.addEdge(node5, node8, true);
		CyEdge edge12 = network.addEdge(node7, node5, true);
		CyEdge edge13 = network.addEdge(node7, node8, true);
		CyEdge edge14 = network.addEdge(node6, node8, true);
		CyEdge edge15 = network.addEdge(node8, node6, true);
		CyEdge edge16 = network.addEdge(node8, node7, true);
		
		GraphAlgorithms graphAlgo = new GraphAlgorithmsImpl();
		PagerankResults pagerankResults = graphAlgo.getPageranks(network, null, true);
		assertEquals(0.019,pagerankResults.getPagerank(node1),0.001);
		assertEquals(0.057,pagerankResults.getPagerank(node2),0.001);
		assertEquals(0.027,pagerankResults.getPagerank(node3),0.001);
		assertEquals(0.067,pagerankResults.getPagerank(node4),0.001);
		assertEquals(0.129,pagerankResults.getPagerank(node5),0.001);
		assertEquals(0.206,pagerankResults.getPagerank(node6),0.001);
		assertEquals(0.187,pagerankResults.getPagerank(node7),0.001);
		assertEquals(0.309,pagerankResults.getPagerank(node8),0.001);
	}
}
