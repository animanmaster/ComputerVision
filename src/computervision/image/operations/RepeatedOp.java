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
public class RepeatedOp implements MorphologicalOperator
{
    private int times = 0;
    private MorphologicalOperator op = null;

    private RepeatedOp(){}

    public RepeatedOp(MorphologicalOperator operator, int nTimes)
    {
        this.times = nTimes;
        this.op = operator;
    }

    public BinaryImage apply(BinaryImage image, BinaryImage structuringElement) 
    {
        return iapply(image.clone(), structuringElement);
    }

    public BinaryImage iapply(BinaryImage image, BinaryImage structuringElement)
    {
        for (int i = 0; i < times; i++)
        {
            image = op.iapply(image, structuringElement);
        }
        return image;
    }

}
