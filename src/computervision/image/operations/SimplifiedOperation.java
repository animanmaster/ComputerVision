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
public abstract class SimplifiedOperation implements MorphologicalOperator
{
    @Override
    public BinaryImage apply(BinaryImage image, BinaryImage structuringElement)
    {
        return iapply(image.clone(), structuringElement);
    }
}
