package BurrowsWheeler;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
        private static final int R = 256;

        // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
        public static void transform(){
                String s = BinaryStdIn.readString();
                CircularSuffixArray circularSuffixArray = new CircularSuffixArray(s);

                int len = s.length();
                for(int i=0; i<len ;i++){
                        if(circularSuffixArray.index(i) == 0 ){
                                BinaryStdOut.write(i);
                        }
                }
                for(int i=0; i<len; i++){
                        BinaryStdOut.write(s.charAt((circularSuffixArray.index(i)+len-1)%len), 8);
                }
                BinaryStdOut.close();
        }

        // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
        public static void inverseTransform(){
                int first = BinaryStdIn.readInt();
                String s = BinaryStdIn.readString();

                int len = s.length();
                char[] charArray = s.toCharArray();
                char[] sorted = new char[len];
                // build the sorted array
                int[] count = new int[R+1];
                for(int i=0; i<len; i++){
                        count[charArray[i]+1]++;
                }
                for(int i = 0; i<R; i++){
                        count[i+1] += count[i];
                }
                for(int i=0; i<len; i++){
                        sorted[count[charArray[i]]++] = charArray[i];
                }

                count = new int[R+1];
                for(int i=0;i<len;i++){
                        count[sorted[i]+1]++;
                }
                for(int i=0;i<R;i++){
                        count[i+1]+=count[i];
                }

                int[] next = new int[len];
                for(int j=0;j<len;j++){
                        int i = count[charArray[j]]++;
                        next[i]=j;
                }

                for (int i = 0, j = first; i < len; i++) {
                        BinaryStdOut.write(sorted[j]);
                        j = next[j];
                }
                BinaryStdOut.close();
        }

        // if args[0] is '-', apply Burrows-Wheeler transform
        // if args[0] is '+', apply Burrows-Wheeler inverse transform
        public static void main(String[] args){
                if (args.length == 0) return;
                if ("-".equals(args[0]))
                        transform();
                else if ("+".equals(args[0]))
                        inverseTransform();
        }

}
