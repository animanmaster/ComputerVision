/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features.segmentation;

import computervision.gui.RAWImageViewer;
import computervision.image.Image;
import computervision.image.RAWImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Malik Ahmed
 */
public class LawsTextureEnergyLevels //implements Feature
{
    //1D Masks
    public static final double[]   L5 = { 1,  4,  6,  4,  1},   //level
                                   E5 = {-1, -2,  0,  2,  1},   //edge
                                   S5 = {-1,  0,  2,  0, -1},   //spot,
                                   R5 = { 1, -4,  6, -4,  1};   //ripple

    public static final double[][]  L5E5 = multiply(L5, E5),
                                    L5S5 = multiply(L5, S5),
                                    L5R5 = multiply(L5, R5),

                                    E5L5 = multiply(E5, L5),
                                    E5E5 = multiply(E5, E5),
                                    E5S5 = multiply(E5, S5),
                                    E5R5 = multiply(E5, R5),

                                    S5L5 = multiply(S5, L5),
                                    S5E5 = multiply(S5, E5),
                                    S5S5 = multiply(S5, S5),
                                    S5R5 = multiply(S5, R5),

                                    R5L5 = multiply(R5, L5),
                                    R5E5 = multiply(R5, E5),
                                    R5S5 = multiply(R5, S5),
                                    R5R5 = multiply(R5, R5);

    //averages of symmetric pairs L5E5/E5L5, etc.
    private static final double[][] avgLE = average(L5E5, E5L5),
                                    avgLS = average(L5S5, S5L5),
                                    avgLR = average(L5R5, R5L5),
                                    avgES = average(E5S5, S5E5),
                                    avgER = average(E5R5, R5E5),
                                    avgSR = average(S5R5, R5S5);


    private static double[][] average(double[][] matrix1, double[][] matrix2)
    {
        double[][] average = new double[matrix1.length][matrix2.length];
        for (int i = 0; i < average.length; i++)
        {
            for (int j = 0; j < average[i].length; j++)
            {
                average[i][j] = (matrix1[i][j] + matrix2[i][j])/2;
            }
        }
        return average;
    }


    /**
     * Multiply two 1D matrices.
     *
     * @param colVector the first vector (treated as the column matrix)
     * @param rowVector the second vector (treated as the row matrix)
     * @return the matrix product of the two vectors.
     */
    private static double[][] multiply(double[] colVector, double[] rowVector)
    {
        /*
         *  _       ___          _____
         * |a|     |c|d|        |ac|ad|
         * |b|  *   ---     =   |bc|bd|
         *  -                    -----
         */
        double[][] product = new double[colVector.length][rowVector.length];
        for (int i = 0; i < product.length; i++)
        {
            for (int j = 0; j < product[i].length; j++)
            {
                product[i][j] = colVector[i] * rowVector[j];
            }
        }
        return product;
    }

    private int withinLimits(int val, int max)
    {
        //If val < 0, use 0.
        //If val > max, use max.
        //Otherwise, use val.
        return Math.min(max, Math.max(0, val));
    }

    private double averageOfNeighborhood(int neighborhood, int[][] pixels, int row, int col)
    {
        //avg 15 neighborhood around each pixel.
        int maxRow = pixels.length - 1;
        int maxCol = pixels[row].length - 1;
//        int actualRow, actualCol;
        double sum = 0;

        int startRow = Math.max(0, row - neighborhood/2),
            endRow   = Math.min(maxRow, row + neighborhood/2);

        int startCol = Math.max(0, col - neighborhood/2),
            endCol   = Math.min(maxCol, col + neighborhood/2);

        for (int i = startRow; i < endRow; i++)
        {
            for (int j = startCol; j < endCol; j++)
            {
//                actualRow = withinLimits(i, maxRow);
//                actualCol = withinLimits(j, maxCol);
//                sum += pixels[actualRow][actualCol];
                sum += pixels[i][j];
            }
        }
        return sum/(neighborhood * neighborhood);
    }

    private double[][] preprocess(Image image, double[][] output)
    {
        int[][] pixels = image.getPixels();
        if (output == null)
        {
            output = new double[pixels.length][pixels[0].length];
        }
        for (int row = 0; row < output.length; row++)
        {
            for (int col = 0; col < output[row].length; col++)
            {
                output[row][col] = pixels[row][col] - averageOfNeighborhood(15, pixels, row, col);
            }
        }
        return output;
    }

    private double[][] applyMask(double[][] image, double[][] mask)
    {
        double[][] result = new double[image.length][image[0].length];
        int centerRow = mask.length/2, centerCol = mask[0].length/2;
        double sum;
        for (int row = 0; row < result.length; row++)
        {
            for (int col = 0; col < result[row].length; col++)
            {
                //TODO - OPTIMIZE THIS MESS

                //To apply the mask, center the mask above (row,col), multiply each position,
                //and sum them all up. The sum goes into result[row][col]
                sum = 0;
                for (int matrixRow = 0, imageRow = row - centerRow; matrixRow < mask.length; matrixRow++, imageRow++)
                {
                    for (int matrixCol = 0, imageCol =  col - centerCol; matrixCol < mask[matrixRow].length; matrixCol++, imageCol++)
                    {
                        sum += (image[withinLimits(imageRow, image.length - 1)][withinLimits(imageCol, image[row].length - 1)] * mask[matrixRow][matrixCol]);
//                        try
//                        {
//                            sum += (image[imageRow][imageCol] * mask[matrixRow][matrixCol]);
//                        }
//                        catch(ArrayIndexOutOfBoundsException aioobe)
//                        {
//                            //:C Bad
//                        }
                    }
                }
                result[row][col] = sum;
            }
        }
        return result;
    }

    public double[][][] extract(Image image)
    {
        //9 features at each spot.
        
        //todo - apply masks, average symmetric masks, return
        List<double[][]> energyMeasures = new ArrayList<double[][]>();
        double[][] pixels = preprocess(image, null);
        energyMeasures.add(average(applyMask(pixels, L5E5), applyMask(pixels, E5L5)));
        energyMeasures.add(average(applyMask(pixels, L5S5), applyMask(pixels, S5L5)));
        energyMeasures.add(average(applyMask(pixels, L5R5), applyMask(pixels, R5L5)));
        energyMeasures.add(average(applyMask(pixels, E5S5), applyMask(pixels, S5E5)));
        energyMeasures.add(average(applyMask(pixels, R5E5), applyMask(pixels, E5R5)));
        energyMeasures.add(average(applyMask(pixels, S5R5), applyMask(pixels, R5S5)));
//        energyMeasures.add(applyMask(pixels, avgLE));
//        energyMeasures.add(applyMask(pixels, avgLS));
//        energyMeasures.add(applyMask(pixels, avgLR));
//        energyMeasures.add(applyMask(pixels, avgES));
//        energyMeasures.add(applyMask(pixels, avgER));
//        energyMeasures.add(applyMask(pixels, avgSR));
        energyMeasures.add(applyMask(pixels, E5E5));
        energyMeasures.add(applyMask(pixels, S5S5));
        energyMeasures.add(applyMask(pixels, R5R5));

        //Combine these energy measures into an "image" with 9 features at each pixel.
        int numFeatures = energyMeasures.size();
        double[][][] energyMap = new double[pixels.length][pixels[0].length][numFeatures];
        double[][] measure;
        for (int feature = 0; feature < numFeatures; feature++)
        {
            measure = energyMeasures.get(feature);
            for (int row = 0; row < measure.length; row++)
            {
                for (int col = 0; col < measure[0].length; col++)
                {
                    energyMap[row][col][feature] = measure[row][col];
                }
            }
        }
        return energyMap;
    }

    public static void main(String[] args) throws Exception
    {
        int SIZE = 256;
        RAWImage raw = new RAWImage(new File("zebras.raw"), SIZE, SIZE);
        new RAWImageViewer(raw).setVisible(true);
        double[][] processedImage = new LawsTextureEnergyLevels().preprocess(raw, null);
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                raw.getPixels()[i][j] = (int)processedImage[i][j];
        new RAWImageViewer(raw).setVisible(true);
    }
    
}
