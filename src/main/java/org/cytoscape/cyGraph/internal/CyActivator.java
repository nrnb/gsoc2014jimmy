/**
 * 
 */
package org.cytoscape.cyGraph.internal;

import java.util.Properties;

import org.cytoscape.cyGraph.algorithms.GraphAlgorithmsImpl;
import org.cytoscape.cyGraph.algorithms.api.GraphAlgorithms;
import org.cytoscape.cyGraph.centralities.CentralitiesImpl;
import org.cytoscape.cyGraph.centralities.api.Centralities;
import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Jimmy
 *
 */
public class CyActivator extends AbstractCyActivator{

	public CyActivator() {
		super();
	}
	
	@Override
	public void start(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
		GraphAlgorithms gAlgo = new GraphAlgorithmsImpl();
		Centralities cent = new CentralitiesImpl();
		registerService(context, gAlgo, GraphAlgorithms.class, new Properties());
		registerService(context, cent, Centralities.class, new Properties());
		
	}

}