/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.transforms;

import computervision.image.BinaryImage;
import computervision.image.Pixel;

/**
 *
 * @author Malik Ahmed
 */
public class Translate implements MorphologicalTransform
{
    private int rowAmount = 0, colAmount = 0;

    public Translate(int rowAmount, int colAmount)
    {
        this.rowAmount = rowAmount;
        this.colAmount = colAmount;
    }

    public BinaryImage apply(BinaryImage image)
    {
        return iapply(image.clone());
    }

    //In anticipation for this addition to the interface I will inevitably make.
    private BinaryImage iapply(BinaryImage image)
    {
        Pixel[] pixels = image.getPixels();
        image.removePixels(image);  //Removes all pixels from the image.
        for (Pixel pixel : pixels)
        {
            image.addPixel(pixel.getRow() + rowAmount, pixel.getCol() + colAmount);
        }
        return image;
    }

}
