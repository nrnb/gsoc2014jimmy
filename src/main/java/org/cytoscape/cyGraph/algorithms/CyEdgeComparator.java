/**
 * 
 */
package org.cytoscape.cyGraph.algorithms;

import java.util.Comparator;

/**
 * @author Jimmy
 *
 */
public class CyEdgeComparator implements Comparator<MetaEdge> {

	private static CyEdgeComparator edgeComparator = null;

	private CyEdgeComparator() {

	}

	public static CyEdgeComparator getInstance() {

		if (edgeComparator == null) {
			edgeComparator = new CyEdgeComparator();
		}

		return edgeComparator;
	}

	@Override
	public int compare(MetaEdge o1, MetaEdge o2) {
		// TODO Auto-generated method stub
		return o1.getWeight() > o2.getWeight() ? 1 : o1.getWeight() == o2
				.getWeight() ? 0 : -1;
	}

}
