import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray {
    private Integer[] index;
    private String input;

    // circular suffix array of s
    public CircularSuffixArray(String s){
        if(s == null || s.equals("")) throw new IllegalArgumentException();
        this.input = s;
        index = new Integer[length()];
        for(int i=0;i < this.index.length; i++){
            index[i] = i;
        }

        // algorithm: not to store strings; just compare them using number of shifts
        Arrays.sort(index, new Comparator<Integer>() {
            public int compare(Integer first, Integer second) {
                // get start indexes of chars to compare
                int firstIndex = first;
                int secondIndex = second;
                // for all characters
                for (int i = 0; i < input.length(); i++) {
                    // if out of the last char then start from beginning
                    if (firstIndex > input.length() - 1)
                        firstIndex = 0;
                    if (secondIndex > input.length() - 1)
                        secondIndex = 0;
                    // if first string > second
                    if (input.charAt(firstIndex) < input.charAt(secondIndex))
                        return -1;
                    else if (input.charAt(firstIndex) > input.charAt(secondIndex))
                        return 1;
                    // watch next chars
                    firstIndex++;
                    secondIndex++;
                }
                // equal strings
                return 0;
            }
        });

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

    // unit testing (required)
    public static void main(String[] args){
        CircularSuffixArray csa = new CircularSuffixArray("AAAA\n");
        for (int i = 0; i < csa.length(); i++ ) System.out.println(csa.index(i) );
        System.out.println();
        csa = new CircularSuffixArray("ABRACADABRA!");
        for (int i = 0; i < csa.length(); i++ ) System.out.println(csa.index(i) );
    }


}
