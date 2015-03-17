/**
 * 
 */
package org.cytoscape.cyGraph.algorithms;

import org.cytoscape.model.CyNode;

/**
 * @author Jimmy
 *
 */
public class MetaNode {

	private CyNode node;

	private double distance;

	private CyNode predecessor;

	public MetaNode(CyNode node, double distance, CyNode predecessor) {

		this.node = node;
		this.distance = distance;
		this.predecessor = predecessor;
	}

	public void setPredecessor(CyNode node) {
		// TODO Auto-generated method stub
		this.predecessor = node;
	}

	public CyNode getPredecessor() {

		return this.predecessor;
	}

	public double getDistance() {
		// TODO Auto-generated method stub
		return this.distance;
	}

	public CyNode getNode() {
		// TODO Auto-generated method stub
		return this.node;
	}

	public void setDistance(double distance) {
		// TODO Auto-generated method stub
		this.distance = distance;
	}
}
