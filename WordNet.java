/**
 * User: nataliechen (nataliechen@coupang.com)
 * Date: 4/2/18
 * Time: 3:31 PM
 */
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordNet {
    private Map<Integer, String> synsets = new HashMap<Integer, String>();
    private Map<String, Set<Integer>> hypernyms = new HashMap<String, Set<Integer>>();
    private Digraph dag;

    // constructor takes the name of the two input files
    public WordNet(String synsetsFileName, String hypernymsFileName){
        if(synsetsFileName == null || hypernymsFileName == null) throw new NullPointerException();

        In synsetsInput = new In(synsetsFileName);
        while(synsetsInput.hasNextLine()){
            String line = synsetsInput.readLine();
            String[] splits = line.split(",");

            int synsetId = Integer.parseInt(splits[0]);
            String synset = splits[1];

            synsets.put(synsetId, synset);

            // parsing nouns
            String[] synsetStrings = synset.split(" ");

            for(String s: synsetStrings){
//                if(hypernyms.containsKey(s)){
//                    Set<Integer> hypernymsSet = hypernyms.get(s);
//                    hypernymsSet.add(new Integer(synsetId));
//                } else {
//                    Set<Integer> hypernymsSet = new HashSet<Integer>();
//                    hypernymsSet.add(new Integer(synsetId));
//                    hypernyms.put(s, hypernymsSet);
//                }
                Set<Integer> ids = new HashSet<Integer>();
                if(hypernyms.containsKey(s)){
                    ids = hypernyms.get(s);
                }
                ids.add(synsetId);
                hypernyms.put(s,ids);
            }

        }

        synsetsInput.close();

        dag = new Digraph(synsets.size());
        In hyernymsInput = new In(hypernymsFileName);

        while(hyernymsInput.hasNextLine()){
            String line = hyernymsInput.readLine();
            String[] splits = line.split(",");
            int synsetId = Integer.parseInt(splits[0]);
            for(int i=1; i<splits.length; i++){
                int hypernymId = Integer.parseInt(splits[i]);
                dag.addEdge(synsetId, hypernymId);

            }

        }

        hyernymsInput.close();

        // Detect if it is a rooted DAG
        int rooted = 0;
        for(int i=0; i<this.dag.V(); i++){
            if(!this.dag.adj(i).iterator().hasNext()) {
                rooted++;
            }
        }
        if(rooted != 1){
            throw new IllegalArgumentException("Not a rooted DAG");
        }

        // Check for cycles
        DirectedCycle cycle = new DirectedCycle(this.dag);
        if(cycle.hasCycle()) throw new IllegalArgumentException("Not a valid DAG");


    }

    // returns all WordNet nouns
    public Iterable<String> nouns(){
        return hypernyms.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word){
        if(word==null) throw new IllegalArgumentException();
        return hypernyms.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB){
        if(!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        SAP sap = new SAP(this.dag);

        Set<Integer> set1 = hypernyms.get(nounA);
        Set<Integer> set2 = hypernyms.get(nounB);

        return sap.length(set1, set2);

    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB){
        if(!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();

        Set<Integer> set1 = hypernyms.get(nounA);
        Set<Integer> set2 = hypernyms.get(nounB);

        SAP sap = new SAP(this.dag);

        return synsets.get(sap.ancestor(set1, set2));
    }

    // do unit testing of this class
    public static void main(String[] args){
        WordNet w = new WordNet("synsets.txt", "hypernyms.txt");
        System.out.println(w.sap("Rameses_the_Great", "Henry_Valentine_Miller"));
        System.out.println(w.distance("Rameses_the_Great", "Henry_Valentine_Miller"));
    }
}
