/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.transforms;

import computervision.image.BinaryImage;
import computervision.image.operations.Dilation;
import computervision.image.operations.SetUnion;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Malik Ahmed
 */
public class Reconstruction implements MorphologicalTransform
{
    private static Dilation dilation = new Dilation();
    private static SetUnion union = new SetUnion();

    private BinaryImage[] s = null;
    private BinaryImage B = null;

    private List<BinaryImage> partialReconstructions = new LinkedList<BinaryImage>();

    private Reconstruction() {}

    public Reconstruction(List<BinaryImage> s, BinaryImage B)
    {
        this.s = s.toArray(new BinaryImage[s.size()]);
        this.B = B;
    }

    public BinaryImage apply(BinaryImage skeleton)
    {
        //The restored image A is calculated as follows:
        //A = (S_N + NB) U (S_N-1 + (N-1)B) U ... U S_0
        //  = ((S_N + B U S_N-1) + B U S_N-2) + ...
        int n = s.length - 1;
        if (n >= 0)
        {
            partialReconstructions.clear();
            BinaryImage restored = s[n];
            partialReconstructions.add(restored);
            while (n > 0)
            {
                restored = union.apply(dilation.apply(restored, B), s[n-1]);
                n--;
                partialReconstructions.add(restored);
            }
            return restored;
        }
        else
        {
            return null;
        }
    }

    public List<BinaryImage> getStagesOfReconstruction()
    {
        return partialReconstructions;
    }
}
