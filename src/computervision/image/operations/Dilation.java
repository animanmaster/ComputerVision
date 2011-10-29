/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.operations;

import computervision.image.BinaryImage;
import computervision.image.Pixel;

/**
 *
 * @author Malik Ahmed
 */
public class Dilation implements MorphologicalOperator
{
    private boolean useA = true;

    public BinaryImage apply(BinaryImage image, BinaryImage structuringElement)
    {
        BinaryImage result = image.clone();
        return iapply(result, structuringElement);
    }

    public BinaryImage iapply(BinaryImage image, BinaryImage structuringElement)
    {
        if (structuringElement != null)
        {
            for (Pixel pixel : image.getPixels())
            {
                for (Pixel offsetPixel : structuringElement.getPixels())
                {
                    image.addPixel(new Pixel(pixel.getRow() + offsetPixel.getRow(),
                                             pixel.getCol() + offsetPixel.getCol(),
                                             pixel.getValue()));
                }
            }
        }
        else
        {
            image = iapply(image, (useA? UnitDisk.A : UnitDisk.B) );
            useA = !useA;
        }
        return image;
    }
}
