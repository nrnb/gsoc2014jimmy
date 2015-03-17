/**
 * 
 */
package org.cytoscape.cyGraph;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.cytoscape.cyGraph.algorithms.api.WeightFunction;
import org.cytoscape.cyGraph.centralities.CentralitiesImpl;
import org.cytoscape.cyGraph.centralities.api.Centralities;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.NetworkTestSupport;
import org.junit.Test;

/**
 * @author Jimmy
 * 
 */
public class CentralitiesImplTest {

	private CyNetwork network, network1;

	@Test
	public void testgetBetweennessStress() {

		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();

		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		CyNode node5 = network.addNode();

		CyEdge edge1 = network.addEdge(node1, node2, false);
		CyEdge edge2 = network.addEdge(node2, node3, false);
		CyEdge edge3 = network.addEdge(node2, node4, false);
		CyEdge edge4 = network.addEdge(node3, node5, false);
		CyEdge edge5 = network.addEdge(node4, node5, false);

		Centralities cent = new CentralitiesImpl();

		Map<CyIdentifiable, List<Double>> nodeBetweennessMap = cent
				.getBetweennessStress(network, false);
		assertEquals(0.0, nodeBetweennessMap.get(node1).get(0), 0.01);
		assertEquals(0.583, nodeBetweennessMap.get(node2).get(0), 0.01);
		assertEquals(0.166, nodeBetweennessMap.get(node3).get(0), 0.01);
		assertEquals(0.166, nodeBetweennessMap.get(node4).get(0), 0.01);
		assertEquals(0.083, nodeBetweennessMap.get(node5).get(0), 0.01);

		assertEquals(0.0, nodeBetweennessMap.get(node1).get(1), 0.01);
		assertEquals(10.0, nodeBetweennessMap.get(node2).get(1), 0.01);
		assertEquals(4.0, nodeBetweennessMap.get(node3).get(1), 0.01);
		assertEquals(4.0, nodeBetweennessMap.get(node4).get(1), 0.01);
		assertEquals(2.0, nodeBetweennessMap.get(node5).get(1), 0.01);

		assertEquals(8.0, nodeBetweennessMap.get(edge1).get(0), 0.01);
		assertEquals(7.0, nodeBetweennessMap.get(edge2).get(0), 0.01);
		assertEquals(7.0, nodeBetweennessMap.get(edge3).get(0), 0.01);
		assertEquals(5.0, nodeBetweennessMap.get(edge4).get(0), 0.01);
		assertEquals(5.0, nodeBetweennessMap.get(edge5).get(0), 0.01);

	}

	@Test
	public void testgetCloseness() {

		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();

		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		CyNode node5 = network.addNode();

		CyEdge edge1 = network.addEdge(node1, node2, false);
		CyEdge edge2 = network.addEdge(node2, node3, false);
		CyEdge edge3 = network.addEdge(node2, node4, false);
		CyEdge edge4 = network.addEdge(node3, node5, false);
		CyEdge edge5 = network.addEdge(node4, node5, false);

		network.getDefaultEdgeTable().createColumn("Weight", Double.class,
				false);
		network.getRow(edge1).set("Weight", 1.0);
		network.getRow(edge2).set("Weight", 1.0);
		network.getRow(edge3).set("Weight", 1.0);
		network.getRow(edge4).set("Weight", 1.0);
		network.getRow(edge5).set("Weight", 1.0);

		Centralities cent = new CentralitiesImpl();
		assertEquals(0.5,
				cent.getCloseness(network, node1, false, new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
		assertEquals(0.8,
				cent.getCloseness(network, node2, false, new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
		assertEquals(0.667,
				cent.getCloseness(network, node3, false, new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
		assertEquals(0.667,
				cent.getCloseness(network, node4, false, new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
		assertEquals(0.5714,
				cent.getCloseness(network, node5, false, new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
	}

	@SuppressWarnings("unused")
	@Test
	public void testgetClusteringCoefficient() {

		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();
		network1 = networkTestSupport.getNetwork();

		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();

		CyEdge edge1 = network.addEdge(node1, node2, false);
		CyEdge edge2 = network.addEdge(node1, node3, false);
		CyEdge edge3 = network.addEdge(node2, node4, false);
		CyEdge edge4 = network.addEdge(node3, node4, false);
		CyEdge edge5 = network.addEdge(node2, node3, false);

		Centralities cent = new CentralitiesImpl();

		assertEquals(1.0, cent.getClusteringCoefficient(network, node1, false),
				0.01);
		assertEquals(0.66,
				cent.getClusteringCoefficient(network, node2, false), 0.01);
		assertEquals(0.66,
				cent.getClusteringCoefficient(network, node3, false), 0.01);
		assertEquals(1.0, cent.getClusteringCoefficient(network, node4, false),
				0.01);

		CyNode node_1 = network1.addNode();
		CyNode node_2 = network1.addNode();
		CyNode node_3 = network1.addNode();
		CyNode node_4 = network1.addNode();

		CyEdge edge_1 = network1.addEdge(node_1, node_2, true);
		CyEdge edge_2 = network1.addEdge(node_1, node_3, true);
		CyEdge edge_3 = network1.addEdge(node_4, node_2, true);
		CyEdge edge_4 = network1.addEdge(node_3, node_4, true);
		CyEdge edge_5 = network1.addEdge(node_2, node_3, true);

		assertEquals(0.50,
				cent.getClusteringCoefficient(network1, node_1, true), 0.01);
		assertEquals(0.33,
				cent.getClusteringCoefficient(network1, node_2, true), 0.01);
		assertEquals(0.33,
				cent.getClusteringCoefficient(network1, node_3, true), 0.01);
		assertEquals(0.50,
				cent.getClusteringCoefficient(network1, node_4, true), 0.01);
	}

	@Test
	public void testgetEccentricity() {

		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();

		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		CyNode node5 = network.addNode();

		CyEdge edge1 = network.addEdge(node1, node2, false);
		CyEdge edge2 = network.addEdge(node2, node3, false);
		CyEdge edge3 = network.addEdge(node2, node4, false);
		CyEdge edge4 = network.addEdge(node3, node5, false);
		CyEdge edge5 = network.addEdge(node4, node5, false);

		network.getDefaultEdgeTable().createColumn("Weight", Double.class,
				false);
		network.getRow(edge1).set("Weight", 1.0);
		network.getRow(edge2).set("Weight", 1.0);
		network.getRow(edge3).set("Weight", 1.0);
		network.getRow(edge4).set("Weight", 1.0);
		network.getRow(edge5).set("Weight", 1.0);

		Centralities cent = new CentralitiesImpl();
		assertEquals(0.33, cent.getEccentricity(network, node1, false,
				new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
		assertEquals(0.5, cent.getEccentricity(network, node2, false,
				new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
		assertEquals(0.5, cent.getEccentricity(network, node3, false,
				new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
		assertEquals(0.5, cent.getEccentricity(network, node4, false,
				new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
		assertEquals(0.33, cent.getEccentricity(network, node5, false,
				new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
	}

	@Test
	public void testgetRadiality() {

		NetworkTestSupport networkTestSupport = new NetworkTestSupport();
		network = networkTestSupport.getNetwork();

		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		CyNode node5 = network.addNode();

		CyEdge edge1 = network.addEdge(node1, node2, false);
		CyEdge edge2 = network.addEdge(node2, node3, false);
		CyEdge edge3 = network.addEdge(node2, node4, false);
		CyEdge edge4 = network.addEdge(node3, node5, false);
		CyEdge edge5 = network.addEdge(node4, node5, false);

		network.getDefaultEdgeTable().createColumn("Weight", Double.class,
				false);
		network.getRow(edge1).set("Weight", 1.0);
		network.getRow(edge2).set("Weight", 1.0);
		network.getRow(edge3).set("Weight", 1.0);
		network.getRow(edge4).set("Weight", 1.0);
		network.getRow(edge5).set("Weight", 1.0);

		Centralities cent = new CentralitiesImpl();
		assertEquals(0.66,
				cent.getRadiality(network, node1, false, new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
		assertEquals(0.916,
				cent.getRadiality(network, node2, false, new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
		assertEquals(0.833,
				cent.getRadiality(network, node3, false, new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
		assertEquals(0.833,
				cent.getRadiality(network, node4, false, new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
		assertEquals(0.75,
				cent.getRadiality(network, node5, false, new WeightFunction() {
					public double getWeight(CyEdge edge) {
						return network.getRow(edge).get("Weight", Double.class);
					}
				}), 0.01);
	}
}
