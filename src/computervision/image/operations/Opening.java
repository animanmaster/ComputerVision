/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.operations;

import computervision.image.BinaryImage;

/**
 *
 * @author Malik Ahmed
 */
public class Opening implements MorphologicalOperator
{
    private Erosion erosion = new Erosion();
    private Dilation dilation = new Dilation();

    public BinaryImage apply(BinaryImage image, BinaryImage structuringElement)
    {
        //Opening is an erosion of the image followed by a dilation of the image.
        return dilation.apply(erosion.apply(image, structuringElement), structuringElement);
    }

    public BinaryImage iapply(BinaryImage image, BinaryImage structuringElement)
    {
        //Opening is an erosion of the image followed by a dilation of the image.
        return dilation.iapply(erosion.iapply(image, structuringElement), structuringElement);
    }

}
