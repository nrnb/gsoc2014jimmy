/**
 * 
 */
package org.cytoscape.cyGraph.algorithms;

import org.cytoscape.model.CyEdge;

/**
 * @author Jimmy
 *
 */
public class MetaEdge {

	private CyEdge edge;

	private double weight;

	public MetaEdge(CyEdge edge, double weight) {

		this.edge = edge;
		this.weight = weight;

	}

	public CyEdge getCyEdge() {

		return edge;

	}

	public double getWeight() {

		return weight;

	}

}
