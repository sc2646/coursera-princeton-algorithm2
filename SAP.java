import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * User: nataliechen (nataliechen@coupang.com)
 * Date: 4/2/18
 * Time: 5:01 PM
 */

public class SAP
{
    private Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G)
    {
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w)
    {
        if (0 > v || 0 > w || G.V() <= v || G.V() <= w)
            throw new IllegalArgumentException();
        BreadthFirstDirectedPaths bfdp1 = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfdp2 = new BreadthFirstDirectedPaths(G, w);
        int minDist = -1;
        for (int i = 0; i != G.V(); i++)
        {
            if (bfdp1.hasPathTo(i) && bfdp2.hasPathTo(i))
            {
                int dist = bfdp1.distTo(i) + bfdp2.distTo(i);
                if (minDist == -1 || dist < minDist)
                    minDist = dist;
            }
        }
        return minDist;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w)
    {
        if (0 > v || 0 > w || G.V() <= v || G.V() <= w)
            throw new IllegalArgumentException();
        BreadthFirstDirectedPaths bfdp1 = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfdp2 = new BreadthFirstDirectedPaths(G, w);
        int minDist = -1;
        int ancestor = -1;
        for (int i = 0; i != G.V(); i++)
        {
            if (bfdp1.hasPathTo(i) && bfdp2.hasPathTo(i))
            {
                int dist = bfdp1.distTo(i) + bfdp2.distTo(i);
                if (minDist == -1 || dist < minDist)
                {
                    minDist = dist;
                    ancestor = i;
                }
            }
        }
        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w)
    {
        if(v==null || w == null){
            throw new IllegalArgumentException();
        }
        for (int i : v)
            if (0 > i || G.V() <= i)
                throw new IllegalArgumentException();
        for (int i : w)
            if (0 > i || G.V() <= i)
                throw new IllegalArgumentException();
        BreadthFirstDirectedPaths bfdp1 = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfdp2 = new BreadthFirstDirectedPaths(G, w);
        int minDist = -1;
        for (int i = 0; i != G.V(); i++)
        {
            if (bfdp1.hasPathTo(i) && bfdp2.hasPathTo(i))
            {
                int dist = bfdp1.distTo(i) + bfdp2.distTo(i);
                if (minDist == -1 || dist < minDist)
                    minDist = dist;
            }
        }
        return minDist;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w)
    {
        if(v==null || w == null){
            throw new IllegalArgumentException();
        }
        for (int i : v)
            if (0 > i || G.V() <= i)
                throw new IllegalArgumentException();
        for (int i : w)
            if (0 > i || G.V() <= i)
                throw new IllegalArgumentException();
        BreadthFirstDirectedPaths bfdp1 = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfdp2 = new BreadthFirstDirectedPaths(G, w);
        int minDist = -1;
        int ancestor = -1;
        for (int i = 0; i != G.V(); i++)
        {
            if (bfdp1.hasPathTo(i) && bfdp2.hasPathTo(i))
            {
                int dist = bfdp1.distTo(i) + bfdp2.distTo(i);
                if (minDist == -1 || dist < minDist)
                {
                    minDist = dist;
                    ancestor = i;
                }
            }
        }
        return ancestor;
    }

    // for unit testing of this class (such as the one below)
    public static void main(String[] args)
    {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}