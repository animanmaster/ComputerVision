/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.transforms;

import computervision.image.BinaryImage;
import computervision.image.Pixel;
import computervision.image.operations.Dilation;
import computervision.image.operations.Erosion;
import computervision.image.operations.SetDifference;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Malik Ahmed
 */
public class Skeleton implements MorphologicalTransform
{
    public static final BinaryImage unitDisk = new BinaryImage(Arrays.asList(new Pixel[]{
                    new Pixel(-1, 0, 1),
        new Pixel(0, -1, 1), new Pixel(0, 0, 1), new Pixel(0, 1, 1),
                    new Pixel(1, 0, 1)
    }), 3, 3);

    private static Erosion erosion = new Erosion();
    private static Dilation dilation = new Dilation();
    private static SetDifference difference = new SetDifference();
    
    private List<BinaryImage> s = new LinkedList<BinaryImage>();


    public BinaryImage apply(BinaryImage image)
    {
        Set<Pixel> skeletonPoints = getAllMaximalDiskCenters(image);
        return new BinaryImage(skeletonPoints, image.getNumberOfRows(), image.getNumberOfColumns());
    }

    private Set<Pixel> getAllMaximalDiskCenters(BinaryImage A)
    {
        s.clear();
        //s_n = (A - nB) \ (A open nB) = A - nB \ (A - nB) - B + B = A - nB \ (A - (n+1)B) + B
        BinaryImage B = unitDisk;
        
        //Using the above formula, we need to retain
        //A - nB and A - (n+1)B for each s_n
        //to optimize the calculation of s_n+1.
        BinaryImage currentNumerator, nextNumerator;

        //Calculate s0 first.
        //s_0 = A \ (A-B)+B
        nextNumerator = erosion.apply(A, B);    //A - B
        s.add(difference.apply(A, dilation.apply(nextNumerator, B)));  //A \ (A-B)+B

        boolean done = false;
        while (!done)
        {
            //Calculate s_n.
            //s_n = (A - NB) \ ((A - NB) - B) + B
            currentNumerator = nextNumerator;   //A - NB
            nextNumerator = erosion.apply(currentNumerator, B); //(A - NB) - B
            s.add(difference.apply(currentNumerator, dilation.apply(nextNumerator, B))); //(A-NB)\(A-NB)-B+B
            done = nextNumerator.getPixels().length == 0; // (A - NB) - B = empty set when we have the max N.
        }

        return union(s);
    }

    private Set<Pixel> union(Collection<BinaryImage> s)
    {
        Set<Pixel> pixels = new TreeSet<Pixel>();
        for (BinaryImage image : s)
        {
            pixels.addAll(Arrays.asList(image.getPixels()));
        }
        return pixels;
    }

    public List<BinaryImage> getAllSn()
    {
        return s;
    }

}
