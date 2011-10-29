
import computervision.image.BinaryImage;
import computervision.image.Pixel;
import computervision.image.operations.Closing;
import computervision.image.operations.Dilation;
import computervision.image.operations.Erosion;
import computervision.image.operations.MorphologicalOperator;
import computervision.image.operations.Opening;
import computervision.image.operations.SetDifference;
import computervision.image.transforms.Reconstruction;
import computervision.image.transforms.Skeleton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Malik Ahmed
 */

public class MorphologicalOpsTest
{
    private BinaryImage A;
    private BinaryImage B;

    public MorphologicalOpsTest()
    {
        A = new BinaryImage(new HashSet<Pixel>(
                buildPixelList(new int[]{
                 0,1,
            1,0, 1,1, 1,2, 1,3, 1,4,
            2,0, 2,1, 2,2, 2,3, 2,4,
            3,0, 3,1,      3,3, 3,4
        })), 6, 6);
        B = new BinaryImage(buildPixelList(new int[]{
            0,0, 0,1,
            1,0, 1,1
        }), 2, 2);
    }

    private List<Pixel> buildPixelList(int[] coords)
    {
        List<Pixel> pixels = new ArrayList<Pixel>();
        for (int i = 0, j = 1; i < coords.length - 1; i+=2, j+=2)
        {
            pixels.add(new Pixel(coords[i], coords[j], 1));
        }
        return pixels;
    }

    private void print(BinaryImage img)
    {
        Pixel[] pixels = img.getPixels();
        for (Pixel pixel : pixels)
        {
            System.out.println("<" + pixel.getRow() + "," + pixel.getCol() + ">");
        }
        System.out.println();
    }

    private BinaryImage doOp(MorphologicalOperator op)
    {
        return op.apply(A, B);
    }

    @org.junit.Test
    public void dilation()
    {
        System.out.println("Dilation result: ");
        print(doOp(new Dilation()));
    }

    @org.junit.Test
    public void erosion()
    {
        System.out.println("Erosion result: ");
        print(doOp(new Erosion()));
    }

    @org.junit.Test
    public void opening()
    {
        System.out.println("Opening result: ");
        print(doOp(new Opening()));
    }

    @org.junit.Test
    public void closing()
    {
        System.out.println("Closing result:");
        print(doOp(new Closing()));
    }

    private Skeleton skeletonOp = new Skeleton();
    private BinaryImage skeleton = null;

    @org.junit.Test
    public void skeleton()
    {
        BinaryImage A = new BinaryImage(buildPixelList(new int[]{
                0,2, 0,3,
           1,1, 1,2, 1,3, 1,4, 1,5,
      2,0, 2,1, 2,2, 2,3, 2,4, 2,5, 2,6,
           3,1, 3,2, 3,3, 3,4, 3,5,
                4,2, 4,3
        }), 8,8);
        System.out.println("Skeleton:");
        print(this.skeleton = skeletonOp.apply(A));
    }


    @org.junit.Test
    public void reconstruction()
    {
        skeleton();
        List<BinaryImage> s = skeletonOp.getAllSn();
        System.out.println("S1");
        print(s.get(1));
        BinaryImage B = Skeleton.unitDisk;
        Reconstruction r = new Reconstruction(s, B);
        System.out.println("Restored:");
        print(r.apply(skeleton));
    }

    @org.junit.Test
    public void setDifference()
    {
        BinaryImage A = new BinaryImage(buildPixelList(new int[]{
                0,2, 0,3,
           1,1, 1,2, 1,3, 1,4, 1,5,
      2,0, 2,1, 2,2, 2,3, 2,4, 2,5, 2,6,
           3,1, 3,2, 3,3, 3,4, 3,5,
                4,2, 4,3 }), 8, 8);
        BinaryImage B = new BinaryImage(buildPixelList(new int[]{
                1,2, 1,3, 2,2, 2,3
        }), 5, 5);
        SetDifference diff = new SetDifference();
        System.out.println("Set Difference:");
        print(diff.apply(A, B));
    }

}
