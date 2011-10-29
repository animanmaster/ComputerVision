/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.operations;

import computervision.image.BinaryImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Malik Ahmed
 */
public class CombinedOperation implements MorphologicalOperator
{
    private List<MorphologicalOperator> ops;

    private CombinedOperation()
    {
        this.ops = new ArrayList<MorphologicalOperator>();
    }

    public CombinedOperation(MorphologicalOperator... operations)
    {
        this((operations == null? null : Arrays.asList(operations)));
    }

    public CombinedOperation(List<MorphologicalOperator> operations)
    {
        this();
        if (operations != null)
        {
            ops.addAll(operations);
        }
    }

    public List<MorphologicalOperator> getOperations()
    {
        return ops;
    }

    @Override
    public BinaryImage apply(BinaryImage image, BinaryImage otherOperand) {
        return iapply(image.clone(), otherOperand);
    }

    @Override
    public BinaryImage iapply(BinaryImage image, BinaryImage otherOperand)
    {
        for (MorphologicalOperator op : ops)
        {
            image = op.iapply(image, otherOperand);
        }
        return image;
    }

}
