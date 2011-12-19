/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Malik Ahmed
 */
public abstract class Image implements Cloneable
{
    protected int[][] pixels;
    protected int rows;
    protected int cols;

    public Image(int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
        this.pixels = new int[rows][cols];
    }

    protected Image(Image original)
    {
        this(original.rows, original.cols);
        for (int i = 0; i < original.cols; i++)
        {
            for (int j = 0; j < original.rows; j++)
            {
                pixels[i][j] = original.pixels[i][j];
            }
        }
    }

    abstract void read(InputStream in) throws IOException;
    abstract void write(OutputStream out) throws IOException;

    public BufferedImage toBufferedImage(int imageType)
    {
        BufferedImage image = new BufferedImage(rows, cols, imageType);
        for (int row = 0; row < cols; row++)
        {
            for (int col = 0; col < rows; col++)
            {
                image.setRGB(col, row, pixels[row][col]);
            }
        }
        return image;
    }

    public BufferedImage toBufferedImage()
    {
        return toBufferedImage(BufferedImage.TYPE_INT_ARGB);
    }

    public int[][] getPixels()
    {
        return this.pixels;
    }

    public Dimension getSize()
    {
        return new Dimension(cols, rows);
    }
}
