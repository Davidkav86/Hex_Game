/****************************************************************************
 * Compilation: javac WeightedQuickUnionUF.java Execution: java
 * WeightedQuickUnionUF < input.txt Dependencies: StdIn.java StdOut.java
 * 
 * Weighted quick-union with path compression.
 * 
 ****************************************************************************/

public class WeightedQuickUnionUF
{
	private int[] id; // id[i] = parent of i
	private int[] sz; // sz[i] = number of objects in subtree rooted at i
	private int count; // number of components

	// Create an empty union find data structure with N isolated sets.
	public WeightedQuickUnionUF(int N)
	{
		count = N;
		id = new int[N + 4];
		sz = new int[N + 4];
		for (int i = 0; i < N + 4; i++)
		{
			id[i] = i;
			sz[i] = 1;
		}
	}

	// Getter for the id[] Array
	public int[] getId()
	{
		return id;
	}

	// Setter for the id[] Array
	public void setId(int[] id)
	{
		this.id = id;
	}

	// Return the number of disjoint sets.
	public int count()
	{
		return count;
	}

	// Return component identifier for component containing p
	public int find(int p)
	{
		while (p != id[p])
		{
			id[p] = id[id[p]];//Path compression code
			p = id[p];
		}
		return p;
	}

	// Print the id[] array to screen. Set up to perform tests in the console
	public void printIdArray()
	{
		for (int i = 0; i < id.length; i++)
		{
			StdOut.print(id[i] + ", ");
		}
		StdOut.print("\n ");
	}

	public int checkId(int i)
	{

		int j = 0;
		for (int x = 0; x < i; x++)
		{
			j = id[x];
		}
		return j;
	}

	// Are objects p and q in the same set?
	public boolean connected(int p, int q)
	{
		return find(p) == find(q);
	}

	// Replace sets containing p and q with their union.
	public void union(int p, int q)
	{
		int i = find(p);
		int j = find(q);
		if (i == j)
			return;

		// make smaller root point to larger one
		if (sz[i] < sz[j])
		{
			id[i] = j;
			sz[j] += sz[i];
		} else
		{
			id[j] = i;
			sz[i] += sz[j];
		}
		count--;
	}

	public static void main(String[] args)
	{
		int N = StdIn.readInt();
		WeightedQuickUnionUF uf = new WeightedQuickUnionUF(N);

		// read in a sequence of pairs of integers (each in the range 0 to N-1),
		// calling find() for each pair: If the members of the pair are not
		// already
		// call union() and print the pair.
		while (!StdIn.isEmpty())
		{
			int p = StdIn.readInt();
			int q = StdIn.readInt();
			if (uf.connected(p, q))
				continue;
			uf.union(p, q);
			StdOut.println(p + " " + q);
		}
		StdOut.println(uf.count() + " components");
	}

}
