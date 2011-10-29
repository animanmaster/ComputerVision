/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Malik Ahmed
 */
public class BinaryImage implements Cloneable
{
    private Set<Pixel> pixels = new TreeSet<Pixel>();
    private int rows = 0;
    private int cols = 0;

    public BinaryImage(Image image)
    {
        this.rows = image.rows;
        this.cols = image.cols;
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                if (image.pixels[row][col] == 0)
                {
                    //0 in images is usually black, but we want to consider
                    //0 white and 1 black in these binary images.
                    pixels.add(new Pixel(row, col, 1));
                }
            }
        }
    }

    BinaryImage(int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
    }

    public BinaryImage(Collection<Pixel> pixels, int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
        if (pixels != null)
        {
            this.pixels.addAll(pixels);
        }
    }

    public Pixel[] getPixels()
    {
        return this.pixels.toArray(new Pixel[this.pixels.size()]);
    }

    public boolean addPixel(Pixel pixel)
    {
        return (pixel == null? false : this.pixels.add(pixel));
    }

    //TODO maybe this should be the only way to add pixels?
    //This way we can ensure binary.
    public void addPixel(int row, int col)
    {
        addPixel(new BinaryPixel(row, col));
    }

    public void addPixels(Pixel... morePixels)
    {
        if (morePixels != null)
        {
            for (Pixel pixel : morePixels)
            {
                addPixel(pixel);
            }
        }
    }

    public void addPixels(BinaryImage image)
    {
        if (image != null && image.pixels != null)
        {
            for (Pixel pixel : image.pixels)
            {
                addPixel(pixel);
            }
        }
    }
    

    public boolean removePixel(Pixel pixel)
    {
        return this.pixels.remove(pixel);
    }

    public void removePixels(Collection<Pixel> pixels)
    {
        this.pixels.removeAll(pixels);
    }

    //Set difference
    public void removePixels(BinaryImage image)
    {
        removePixels(image.pixels);
    }

    public boolean contains(Pixel pixel)
    {
        return this.pixels.contains(pixel);
    }

    public int getNumberOfRows()
    {
        return rows;
    }
    
    public int getNumberOfColumns()
    {
        return cols;
    }

    public boolean[][] getPixelValues()
    {
        boolean[][] allPixels = new boolean[rows][cols];
        for (Pixel pixel : this.pixels)
        {
            allPixels[pixel.row][pixel.col] = true;
        }
        return allPixels;
    }

    @Override
    public int hashCode() {
        return pixels.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BinaryImage? this.pixels.equals(((BinaryImage)obj).pixels) : false);
    }


    @Override
    public BinaryImage clone()
    {
        BinaryImage copy = new BinaryImage(rows, cols);
        copy.pixels.addAll(pixels);
        return copy;
    }

    @Override
    public String toString()
    {
        boolean[][] pixels = getPixelValues();
        StringBuilder str = new StringBuilder();
        for (boolean[] row : pixels)
        {
            for (boolean value : row)
            {
                if (value)
                    str.append('1');
                else
                    str.append('0');
            }
            str.append('\n');
        }
        return str.toString();
    }

}
