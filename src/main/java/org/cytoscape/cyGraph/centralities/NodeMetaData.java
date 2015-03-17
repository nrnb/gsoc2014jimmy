/**
 * 
 */
package org.cytoscape.cyGraph.centralities;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * @author Jimmy
 *
 */
public class NodeMetaData {

	private CyNode node;

	private double stressDependency;

	private double betweennessDependency;

	private double distance;

	private double shortestPaths;

	private List<CyNode> predecessors;

	private List<CyEdge> connectingEdges;

	public NodeMetaData(CyNode node, double betweennessDependency,
			double distance, double stressDependency, double shortestPaths) {
		this.node = node;
		this.betweennessDependency = betweennessDependency;
		this.distance = distance;
		this.stressDependency = stressDependency;
		this.shortestPaths = shortestPaths;
		this.predecessors = new ArrayList<CyNode>();
		this.connectingEdges = new ArrayList<CyEdge>();
	}

	public void resetAll() {
		this.betweennessDependency = 0.0;
		this.distance = -1;
		this.stressDependency = 0.0;
		this.shortestPaths = 0.0;
		this.predecessors = new ArrayList<CyNode>();
		this.connectingEdges = new ArrayList<CyEdge>();

	}

	public double getStressDependency() {

		return this.stressDependency;
	}

	public double getDistance() {

		return this.distance;

	}

	public CyNode getCyNode() {
		return this.node;
	}

	public double getBetweennessDependency() {

		return this.betweennessDependency;
	}

	public double getShortestPaths() {

		return this.shortestPaths;
	}

	public List<CyNode> getPredecessors() {

		return this.predecessors;
	}

	public List<CyEdge> getConnectingEdges() {

		return this.connectingEdges;

	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void setShortestPaths(double sp) {
		this.shortestPaths = sp;
	}

	public void setBetweennessDependency(double bd) {
		this.betweennessDependency = bd;
	}

	public void setStressDependency(double sd) {

		this.stressDependency = sd;
	}
}
