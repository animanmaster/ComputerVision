/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image;

/**
 *
 * @author Malik Ahmed
 */
public class BinaryPixel extends Pixel
{
    public BinaryPixel(int row, int col)
    {
        this(row, col, true);
    }

    public BinaryPixel(int row, int col, boolean on)
    {
        super(row, col, on? 1 : 0);
    }
}
