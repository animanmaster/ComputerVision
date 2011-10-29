/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.operations;

import computervision.image.BinaryImage;
import computervision.image.Pixel;
import java.util.Arrays;

/**
 *
 * @author Malik Ahmed
 */
public class UnitDisk
{
    /*
     *  +
     * +++
     *  +
     */
    public static final BinaryImage A = new BinaryImage(Arrays.asList(
                    new Pixel(-1, 0, 1),
                    new Pixel(0, -1, 1), new Pixel(0,0,1), new Pixel(0,1,1),
                    new Pixel(1,0,1)
                    ), 3, 3);

    /*
     * +++
     * +++
     * +++
     */
    public static final BinaryImage B = A.clone();
    static
    {
        B.addPixels(new Pixel(-1,-1,1), new Pixel(-1, 1, 1),
                new Pixel(1, -1, 1), new Pixel(1, 1, 1));
    }
}
