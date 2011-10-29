/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image;

/**
 *
 * @author Malik Ahmed
 */
public class Pixel implements Comparable<Pixel>
{
    protected int row, col;
    protected int value = 0;

    public Pixel(int row, int col, int value)
    {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean equal = false;
        if (o instanceof Pixel)
        {
            Pixel other = (Pixel)o;
            equal = (row == other.row &&
                     col == other.col &&
                     value == other.value);
        }
        return equal;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.row;
        hash = 17 * hash + this.col;
        hash = 17 * hash + this.value;
        return hash;
    }

    @Override
    public int compareTo(Pixel o)
    {
        int comparison;
        if (row < o.row)
        {
            //This pixel comes before the other pixel,
            //so this pixel is < the other pixel.
            comparison = -1;
        }
        else if (row > o.row)
        {
            //This pixel comes after the other pixel,
            //so this pixel is > the other pixel.
            comparison = 1;
        }
        else
        {
            //The pixels are in the same row.
            //Check their column positions.
            comparison = col - o.col;
        }
        return comparison;
    }

    @Override
    public String toString()
    {
        return value + " @(" + row + ", " + col + ")";
    }

}
