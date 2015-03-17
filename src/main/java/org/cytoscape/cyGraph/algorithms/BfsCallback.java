/**
 * 
 */
package org.cytoscape.cyGraph.algorithms;

import org.cytoscape.cyGraph.algorithms.api.Callback;
import org.cytoscape.model.CyNode;

/**
 * @author Jimmy
 * 
 */
public class BfsCallback implements Callback {

	private CyNode target;

	public BfsCallback(CyNode target) {

		this.target = target;

	}

	@Override
	public boolean notify(CyNode node, int time, int dist) {
		// TODO Auto-generated method stub
		return node == target;
	}

}
