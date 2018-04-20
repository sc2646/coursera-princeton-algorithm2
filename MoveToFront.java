import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import java.util.LinkedList;

public class MoveToFront {
        private static final int ALPHABET_SIZE = 256;
        private static final int CHAR_BITS = 8;

        // apply move-to-front encoding, reading from standard input and writing to standard output
        public static void encode(){
                LinkedList<Integer> linkedList = new LinkedList<Integer>();
                for(int i = 0; i < ALPHABET_SIZE; i++){
                        linkedList.add(i);
                }

                while(!BinaryStdIn.isEmpty()){
                        int in = BinaryStdIn.readChar();
                        int index = linkedList.indexOf(in);
                        BinaryStdOut.write(index, CHAR_BITS);

                        linkedList.remove(index);
                        linkedList.add(0, in);
                }
                BinaryStdOut.close();
        }

        // apply move-to-front decoding, reading from standard input and writing to standard output
        public static void decode(){
                LinkedList<Integer> linkedList = new LinkedList<Integer>();
                for(int i=0; i<ALPHABET_SIZE; i++){
                        linkedList.add(i);
                }

                while(!BinaryStdIn.isEmpty()){
                        int index = BinaryStdIn.readChar();
                        int in = linkedList.get(index);
                        BinaryStdOut.write(in, CHAR_BITS);

                        linkedList.remove(index);
                        linkedList.add(0, in);
                }
                BinaryStdOut.close();
        }

        // if args[0] is '-', apply move-to-front encoding
        // if args[0] is '+', apply move-to-front decoding
        public static void main(String[] args){
                if(args.length == 0) return;
                if("-".equals(args[0])) encode();
                else decode();

        }
}
