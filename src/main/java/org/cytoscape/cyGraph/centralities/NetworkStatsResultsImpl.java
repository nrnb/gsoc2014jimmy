/**
 * 
 */
package org.cytoscape.cyGraph.centralities;

import java.util.Map;

import org.cytoscape.cyGraph.centralities.api.NetworkStatsResults;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * @author Jimmy
 * 
 */
public class NetworkStatsResultsImpl implements NetworkStatsResults {

	private Map<CyNode, Double> radialityMap;

	private Map<CyNode, Double> closenessMap;

	private Map<CyNode, Double> cCoefficientMap;

	private Map<CyNode, Double> nodeBetweennessMap;

	private Map<CyEdge, Double> edgeBetweennessMap;

	private Map<CyNode, Double> nodeStressMap;

	private Map<CyNode, Double> eccentricityMap;

	private double diameter;

	public NetworkStatsResultsImpl(Map<CyNode, Double> radialityMap,
			Map<CyNode, Double> closenessMap,
			Map<CyNode, Double> cCoefficientMap,
			Map<CyNode, Double> nodeBetweennessMap,
			Map<CyEdge, Double> edgeBetweennessMap,
			Map<CyNode, Double> nodeStressMap,
			Map<CyNode, Double> eccentricityMap, double diameter) {

		this.cCoefficientMap = cCoefficientMap;
		this.closenessMap = closenessMap;
		this.edgeBetweennessMap = edgeBetweennessMap;
		this.nodeBetweennessMap = nodeBetweennessMap;
		this.nodeStressMap = nodeStressMap;
		this.radialityMap = radialityMap;
		this.diameter = diameter;
		this.eccentricityMap = eccentricityMap;
	}

	@Override
	public Map<CyNode, Double> getBetweennessMap() {
		// TODO Auto-generated method stub
		return this.nodeBetweennessMap;
	}

	@Override
	public Map<CyNode, Double> getClosenessMap() {
		// TODO Auto-generated method stub
		return this.closenessMap;
	}

	@Override
	public Map<CyNode, Double> getClusteringCoefficientMap() {
		// TODO Auto-generated method stub
		return this.cCoefficientMap;
	}

	@Override
	public double getDiameter() {
		// TODO Auto-generated method stub
		return this.diameter;
	}

	@Override
	public Map<CyNode, Double> getEccentricityMap() {
		// TODO Auto-generated method stub
		return this.eccentricityMap;
	}

	@Override
	public Map<CyEdge, Double> getEdgeBetweennessMap() {
		// TODO Auto-generated method stub
		return this.edgeBetweennessMap;
	}

	@Override
	public Map<CyNode, Double> getRadialityMap() {
		// TODO Auto-generated method stub
		return this.radialityMap;
	}

	@Override
	public Map<CyNode, Double> getStressMap() {
		// TODO Auto-generated method stub
		return this.nodeStressMap;
	}

}
