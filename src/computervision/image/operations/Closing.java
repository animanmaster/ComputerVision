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
public class Closing implements MorphologicalOperator
{
    private Erosion erosion = new Erosion();
    private Dilation dilation = new Dilation();

    public BinaryImage apply(BinaryImage image, BinaryImage structuringElement)
    {
        //A closing operation is a dilation followed by an erosion.
        return erosion.apply(dilation.apply(image, structuringElement), structuringElement);
    }

    public BinaryImage iapply(BinaryImage image, BinaryImage structuringElement)
    {
        //A closing operation is a dilation followed by an erosion.
        return erosion.iapply(dilation.iapply(image, structuringElement), structuringElement);
    }

}
