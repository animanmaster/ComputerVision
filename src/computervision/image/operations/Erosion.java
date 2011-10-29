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
public class Erosion implements MorphologicalOperator
{
    private boolean useA = true;

    public BinaryImage apply(BinaryImage image, BinaryImage structuringElement)
    {
        return iapply(image.clone(), structuringElement);
//        return new BinaryImage(result, image.getNumberOfRows(), image.getNumberOfColumns());
    }


    public BinaryImage iapply(BinaryImage image, BinaryImage structuringElement)
    {
        if (structuringElement != null)
        {
            Pixel[] sePixels = structuringElement.getPixels(),
                    imagePixels = image.getPixels();

            boolean fits;
            for (Pixel pixel : imagePixels)
            {
                //See if the structuring element fits here.
                fits = true;
                for (int i = 0; i < sePixels.length && fits; i++)
                {
                    //Note that we must search the array instead of the image itself
                    //since we're altering the image in-place.
                    //TODO this assumes that the pixels are sorted, but that could change in future implementations
                    fits = Arrays.binarySearch(imagePixels, new Pixel(pixel.getRow() + sePixels[i].getRow(),
                                                                  pixel.getCol() + sePixels[i].getCol(),
                                                                  pixel.getValue())) >= 0;
                }

                //If it does, it's in the erosion. Otherwise, it isn't.
                if (fits)
                {
                    image.addPixel(pixel);
                }
                else
                {
                    image.removePixel(pixel);
                }
            }
        }
        else
        {
            image = iapply(image, (useA? UnitDisk.A : UnitDisk.B));
            useA = !useA;
        }

        return image;
    }

}
