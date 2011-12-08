/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features.segmentation;

import computervision.image.Image;

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


    private static double[][] multiply(double[] colVector, double[] rowVector)
    {
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

    private double averageOf15Neighborhood(int[][] pixels, int row, int col)
    {
        //avg 15 neighborhood around each pixel.
        int minRow = 0, maxRow = pixels.length - 1;
        int minCol = 0, maxCol = pixels[row].length - 1;
        int actualRow, actualCol;
        double sum = 0;

        for (int i = row - 7; i <= row + 7; i++)
        {
            for (int j = col - 7; j <= col + 7; col++)
            {
                if (i < minRow)
                {
                    actualRow = minRow;
                }
                else if (i > maxRow)
                {
                    actualRow = maxRow;
                }
                else
                {
                    actualRow = i;
                }

                if (j < minCol)
                {
                    actualCol = minCol;
                }
                else if (j > maxCol)
                {
                    actualCol = maxCol;
                }
                else
                {
                    actualCol = j;
                }
                sum += pixels[actualRow][actualCol];
            }
        }
        return sum/(15 * 15);
    }

    private double[][] preprocess(Image image, double[][] output)
    {
        int[][] pixels = image.getPixels();
        if (output == null || (output.length != pixels.length && output[0].length != pixels[0].length))
        {
            output = new double[pixels.length][pixels[0].length];
        }
        for (int row = 0; row < output.length; row++)
        {
            for (int col = 0; col < output[row].length; col++)
            {
                output[row][col] = averageOf15Neighborhood(pixels, row, col);
            }
        }
        return output;
    }

    
}
