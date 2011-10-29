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
public class SetUnion implements MorphologicalOperator
{
    public BinaryImage apply(BinaryImage image, BinaryImage other)
    {
        return iapply(image.clone(), other);
    }

    public BinaryImage iapply(BinaryImage image, BinaryImage other)
    {
        image.addPixels(other);
        return image;
    }
}
