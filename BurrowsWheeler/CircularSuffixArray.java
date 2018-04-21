package BurrowsWheeler;

import java.util.Arrays;

public class CircularSuffixArray {
    private int[] index;
    private String input;

    // circular suffix array of s
    public CircularSuffixArray(String s){
        if(s == null || s.equals("")) throw new IllegalArgumentException();
        this.input = s;
        this.index = new int[length()];

        SuffixString[] suffixString = new SuffixString[length()];

        for(int offset=0; offset < length(); offset++){
            suffixString[offset] = new SuffixString(s, offset);
        }

        Arrays.sort(suffixString);

        for(int i=0; i<length(); i++){
            index[i] = suffixString[i].getOffset();
        }
    }

    // length of s
    public int length(){
        return this.input.length();

    }

    // returns index of ith sorted suffix
    public int index(int i){
        if(i < 0 || i >= length()) throw new IllegalArgumentException();
        return this.index[i];
    }


    private static class SuffixString implements Comparable<SuffixString> {
        private String s;
        private int N;
        private int offset;

        public SuffixString(String s, int offset){
            this.s = s;
            this.N = s.length();
            this.offset = offset;
        }

        public int charAt(int i){
            if (i >= N) return 0;
            return this.s.charAt((i+this.offset) % this.N);
        }

        public int getOffset(){
            return this.offset;
        }

        public int compareTo(SuffixString that){
            for(int i=0; i<this.N; i++){
                int diff = this.charAt(i) - that.charAt(i);
                if(diff != 0){
                    return diff;
                }
            }
            return 0;
        }
    }

    // unit testing (required)
    public static void main(String[] args){
        CircularSuffixArray csa = new CircularSuffixArray("AAAA\n");
        for (int i = 0; i < csa.length(); i++ ) System.out.println(csa.index(i) );
        System.out.println();
        csa = new CircularSuffixArray("ABRACADABRA!");
        for (int i = 0; i < csa.length(); i++ ) System.out.println(csa.index(i) );
    }


}
