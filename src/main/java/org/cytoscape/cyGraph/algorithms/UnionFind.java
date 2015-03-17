/**
 * 
 */
package org.cytoscape.cyGraph.algorithms;

/**
 * @author Jimmy
 *
 */
public class UnionFind {

	private int id[];

	private int count;

	private int rank[];

	public UnionFind(int n) {

		id = new int[n];
		rank = new int[n];
		count = n;

		for (int i = 0; i < count; i++) {

			id[i] = i;
			rank[i] = 0;
		}
	}

	public int find(int p) {

		if (p < 0 || p >= id.length)
			throw new IndexOutOfBoundsException();
		while (p != id[p]) {

			id[p] = id[id[p]];
			p = id[p];
		}

		return p;
	}

	public int count() {

		return this.count;

	}

	public boolean isConnected(int p, int q) {

		return find(p) == find(q);

	}

	public void union(int p, int q) {

		int i = find(p);
		int j = find(q);

		if (i == j)
			return;

		if (rank[i] < rank[j])
			id[i] = j;
		else if (rank[i] > rank[j])
			id[j] = i;
		else {

			id[j] = i;
			rank[i]++;
		}
		count--;
	}
}
