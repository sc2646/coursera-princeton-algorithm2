package wordnet;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * User: nataliechen (nataliechen@coupang.com)
 * Date: 4/2/18
 * Time: 5:55 PM
 */
public class Outcast {
    private WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet){
        this.wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns){
        String outcast = null;
        int outcastDist = -1;
        for(String nounA : nouns){
            int dist = 0;
            for(String nounB : nouns){
                if(nounA.equals(nounB)) continue;
                int d = wordNet.distance(nounA, nounB);
                if(d==-1) continue;
                dist += d;
            }

            if(dist > outcastDist){
                outcast = nounA;
                outcastDist = dist;
            }
        }
        return outcast;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}