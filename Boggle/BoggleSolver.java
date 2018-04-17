package Boggle;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {
    private static final String QU_STRING = "QU";
    private static final int R = 26;
    private Node root;
    private boolean[][] usedMatrix;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary){
        if(dictionary == null) throw new IllegalArgumentException();

        for(String s: dictionary){
            addWord(s);
        }
    }

    private void addWord(String word){
        root = add(root, word, 0);
    }

    private Node add(Node node, String word, int index){
        if(node == null) node = new Node();
        if(index == word.length()){
            node.hasWord = true;
            return node;
        }
        char c = charAt(word, index);
        node.next[c] = add(node.next[c], word, index+1);
        return node;
    }

    private char charAt(String word, int index){
        return (char)(word.charAt(index) - 'A');
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word){
        if (hasWord(word)) {
            switch (word.length()) {
                case 0:
                case 1:
                case 2:
                    return 0;
                case 3:
                case 4:
                    return 1;
                case 5:
                    return 2;
                case 6:
                    return 3;
                case 7:
                    return 5;
                default:
                    return 11;
            }
        } else {
            return 0;
        }
    }

    private boolean hasWord(String word){
        Node node = get(root, word, 0);
        return node != null && node.hasWord == true && word.length() > 2;
    }

    private Node get(Node node, String word, int index){
        if(node == null) return null;
        if(index == word.length()) return node;
        char c = charAt(word, index);
        return get(node.next[c], word, index+1);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board){
        if(board == null) throw new IllegalArgumentException();

        int rows = board.rows();
        int cols = board.cols();

        this.usedMatrix = new boolean[rows][cols];
        Set<String> words = new HashSet<String>();
        for(int row = 0; row < rows; row++){
            for(int col = 0; col < cols; col++){
                char c = board.getLetter(row, col);
                if(c == 'Q') searchForWords(board, row, col, QU_STRING, words);
                else searchForWords(board, row, col, "" + c, words);
            }
        }
        return words;
    }

    private boolean isValidPrefix(String prefix){
        Node node = root;
        for(int i=0; i<prefix.length() && node!= null; i++) node = node.next[charAt(prefix, i)];
        return node != null;
    }

    private void searchForWords(BoggleBoard board, int i, int j, String prefix, Set<String> words){
        if(!isValidPrefix(prefix)) return;

        usedMatrix[i][j] = true;
        if(hasWord(prefix)) words.add(prefix);

        for(int row = i-1; row<=i+1; row++){
            for(int col=j-1; col<=j+1; col++){
                if(isValidIndex(board, row, col) && !usedMatrix[row][col]){
                    char c = board.getLetter(row, col);
                    searchForWords(board, row, col, prefix + (c == 'Q'? QU_STRING: c), words);
                }
            }
        }
        usedMatrix[i][j] = false;
    }

    private boolean isValidIndex(BoggleBoard board, int row, int col){
        return row>=0 && row<board.rows() && col>=0 && col<board.cols();
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }


    private static class Node{
        private boolean hasWord;
        private Node[] next = new Node[R];
    }
}
