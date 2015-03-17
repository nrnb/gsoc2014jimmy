/**
 * 
 */
package org.cytoscape.cyGraph.algorithms;

import java.util.Map;

import org.cytoscape.cyGraph.algorithms.api.PagerankResults;
import org.cytoscape.model.CyNode;

/**
 * @author Jimmy
 *
 */
public class PagerankResultImpl implements PagerankResults{

	private Map<CyNode, Integer> nodeIndexMap;

	private double pageranks[];
	
	public PagerankResultImpl(Map<CyNode, Integer> nodeIndexMap, double pageranks[]){
		
		this.nodeIndexMap = nodeIndexMap;
		this.pageranks = pageranks;
	}
	@Override
	public double getPagerank(CyNode node) {
		// TODO Auto-generated method stub
		return pageranks[nodeIndexMap.get(node)];
	}

	
}
