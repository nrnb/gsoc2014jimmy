/**
 * 
 */
package org.cytoscape.cyGraph.algorithms;

import java.util.Comparator;

/**
 * @author Jimmy
 *
 */
public class DijkstraComparator implements Comparator<MetaNode>{

	private static DijkstraComparator dComparator = null;

	private DijkstraComparator() {

	}

	public static DijkstraComparator getInstance() {

		if (dComparator == null) {
			dComparator = new DijkstraComparator();
		}

		return dComparator;
	}

	@Override
	public int compare(MetaNode o1, MetaNode o2) {
		// TODO Auto-generated method stub
		return o1.getDistance() > o2.getDistance() ? 1 : o1.getDistance() == o2
				.getDistance() ? 0 : -1;
	}

}
