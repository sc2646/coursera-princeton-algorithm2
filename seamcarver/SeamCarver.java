package seamcarver;
import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
    private Picture picture;
    private double picEnergy[][];

    private final static double BORDER_VALUE = 1000.0;
    private final static String RED = "RED";
    private final static String BLUE = "BLUE";
    private final static String GREEN = "GREEN";


    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture){
        if (picture == null) throw new java.lang.IllegalArgumentException();
        this.picture = new Picture(picture);
        picEnergy();
    }

    // current picture
    public Picture picture(){
        return this.picture;
    }

    // width of current picture
    public int width(){
        return this.picEnergy.length;
    }

    // height of current picture
    public int height(){
        return this.picEnergy[0].length;
    }

    // picEnergy of pixel at column x and row y
    public double energy(int x, int y){
        int width = width();
        int height = height();
        if ( x<0 || y<0 || x>width-1 || y>height-1) throw new java.lang.IllegalArgumentException();
        if (x==0 || x==width-1 || y==0 || y==height-1) return BORDER_VALUE;

        double xR = getColor(x+1, y, RED) - getColor(x-1, y, RED);
        double xG = getColor(x+1, y, GREEN) - getColor(x-1, y, GREEN);
        double xB = getColor(x+1, y, BLUE) - getColor(x-1, y, BLUE);

        double deltaX = Math.pow(xR, 2) + Math.pow(xG, 2) + Math.pow(xB, 2);

        double yR = getColor(x, y+1, RED) - getColor(x, y-1, RED);
        double yG = getColor(x, y+1, GREEN) - getColor(x, y-1, GREEN);
        double yB = getColor(x, y+1, BLUE) - getColor(x, y-1, BLUE);

        double deltaY = Math.pow(yR, 2) + Math.pow(yG, 2) + Math.pow(yB, 2);

        return Math.sqrt(deltaX + deltaY);
    }

    // helper fxn to calculate picEnergy
    private int getColor(int x, int y, String rgb){
        if(rgb.equals(RED)) return this.picture.get(x,y).getRed();
        if(rgb.equals(GREEN)) return this.picture.get(x,y).getGreen();
        return this.picture.get(x,y).getBlue();
    }

    private void picEnergy(){
        this.picEnergy = new double[this.picture.width()][this.picture.height()];
        for(int col = 0; col < width(); col++){
            for(int row = 0;row < height(); row++){
                this.picEnergy[col][row] = energy(col, row);
            }
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam(){
        double[] distTo = new double[width()*height()];
        int[] edgeTo = new int[width()*height()];

        init(distTo, edgeTo);

        for(int col = 0; col < width() - 1 ;col++){
            for(int row = 0; row < height();row++){
                int location = pixelNum(col, row);
                // right-upper
                if(row -1 >= 0) relaxEdge(location, pixelNum(col+1, row-1), distTo, edgeTo);
                // right-center
                relaxEdge(location, pixelNum(col+1, row), distTo, edgeTo);
                // right-bottom
                if(row + 1 <= height() - 1 ) relaxEdge(location, pixelNum(col+1, row+1), distTo, edgeTo);
            }
        }

        // search min distTo => This is the begining of the seam (smallest picEnergy)
        double currMinDist = Double.POSITIVE_INFINITY;
        int lastSeamPixel = -1;
        for(int row = 0; row < height() ;row++){
            if(distTo[pixelNum(width() - 1, row)] < currMinDist){
                currMinDist = distTo[pixelNum(width() - 1, row)];
                lastSeamPixel = pixelNum(width() - 1, row);
            }
        }

        return restoreHSeam(lastSeamPixel, edgeTo);
    }

    private void  init(double[] distTo, int[] edgeTo){
        for(int col = 0; col < width() ; col++){
            for(int row = 0; row < height(); row++){
                int location = pixelNum(col, row);
                if(col==0) distTo[location] = 0;
                else distTo[location] = Double.POSITIVE_INFINITY;
                edgeTo[location] = -1;
            }
        }
    }

    private int pixelNum(int col, int row){
        return width()*row + col;
    }

    private int colNum(int pixelNum){
        return pixelNum % width();
    }

    private int rowNum(int pixelNum){
        return pixelNum/width();
    }

    private void relaxEdge(int source, int dest, double[] distTo, int[] edgeTo){
        int col = colNum(dest);
        int row = rowNum(dest);

        if(distTo[source] + this.picEnergy[col][row] < distTo[dest]){
            // relax edge
            distTo[dest] = distTo[source] + this.picEnergy[col][row];
            edgeTo[dest] = source;
        }
    }

    private int[] restoreHSeam(int lastSeamPixel, int[] edgeTo){
        int[] hSeam = new int[width()];
        int currPixel = lastSeamPixel;
        int i = width() - 1;
        // keep -1 , until hitting left most edge (col)
        while(currPixel != -1) {
            hSeam[i] = rowNum(currPixel);
            currPixel = edgeTo[currPixel];
            i--;
        }
        return hSeam;
    }

    private void transpose(){
        double[][] energyArray = new double[height()][width()];

        for(int j=0; j<width(); j++){
            for(int i=0;i< height();i++){
                energyArray[i][j] = energy(j,i);
            }
        }
        this.picEnergy = energyArray;
    }

    // sequence of indices for vertical seam
    // transpose the image, and call findVerticalSeam(), and transpose it back
    public int[] findVerticalSeam(){
        // use topological sort
        // Edge weighted Digraph
        double[][] copy = this.picEnergy;
        transpose();
        int[] verSeam = findHorizontalSeam();
        this.picEnergy = copy;
        return verSeam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam){
        if(seam == null ) throw new java.lang.IllegalArgumentException();
        if(seam.length != width()) throw new java.lang.IllegalArgumentException();
        if (height() <= 1) throw new java.lang.IllegalArgumentException();
        // check if the seam is valid
        for (int i = 0; i < seam.length; i++)
            if (seam[i] < 0 || seam[i] >= height() ||
                    i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1)
                throw new IllegalArgumentException();

        Picture updatedPicture = new Picture(width(), height()-1);
        double[][] updatedPictureEnergy = new double[width()][height()-1];

        for(int col = 0; col < width() ;col++){
            // copy upper part of the picture and picEnergy
            for(int row = 0 ; row < seam[col]; row++){
                updatedPicture.set(col, row, this.picture.get(col, row));
                updatedPictureEnergy[col][row] = this.picEnergy[col][row];
            }

            // copy bottom part of the picture and picEnergy
            for(int row = seam[col]+1; row < height(); row++){
                updatedPicture.set(col, row-1, this.picture.get(col, row));
                updatedPictureEnergy[col][row-1] = this.picEnergy[col][row];
            }
        }

        this.picture = updatedPicture;
        this.picEnergy = updatedPictureEnergy;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam){
        int height = height();
        int width = width();
        if(seam == null ) throw new java.lang.IllegalArgumentException();
        if(seam.length != height) throw new java.lang.IllegalArgumentException();
        if (width<=1) throw new java.lang.IllegalArgumentException();

        // check if the seam is valid
        for (int i = 0; i < seam.length; i++)
            if (seam[i] < 0 || seam[i] >= width ||
                    i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1)
                throw new IllegalArgumentException();

        Picture updatedPicture = new Picture(width() - 1, height());
        double[][] updatedPicEnergy = new double[width() - 1][height()];
        for (int row = 0; row < height(); row++) {
            // copy the upper part of the picture and picEnergy
            for (int col = 0; col < seam[row]; col++) {
                updatedPicture.set(col, row, this.picture.get(col, row));
                updatedPicEnergy[col][row] = this.picEnergy[col][row];
            }
            // copy the bottom part of the picEnergy
            for (int col = seam[row] + 1; col < width(); col++) {
                updatedPicture.set(col - 1, row, this.picture.get(col, row));
                updatedPicEnergy[col - 1][row] = this.picEnergy[col][row];
            }
        }

        this.picture = updatedPicture;
        this.picEnergy = updatedPicEnergy;
    }

}

