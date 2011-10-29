package computervision;

import computervision.gui.RAWImageViewer;
import computervision.image.BinaryImage;
import computervision.image.RAWImage;
import computervision.image.transforms.Reconstruction;
import computervision.image.transforms.Skeleton;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Homework 3 - Homework3
 *
 * Take in a raw image, produce its skeleton, and reconstruct the image from
 * its skeleton points.
 *
 * @author Malik Ahmed
 */
public class Homework3
{

    /**
     * The program takes sets of 3 arguments:
     * image.raw rows cols
     *
     * For each set of 3 arguments, create a RAW image using the given file and size,
     * perform the morphological skeleton transformation and reconstruction.
     *
     * It will also write the skeleton and partial reconstructions to PNG files
     * for easy viewing.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception
    {
        int arg = 0;
        if (args.length == 0)
        {
            new RAWImageViewer().setVisible(true);
        }
        else
        {
            while (arg <= args.length - 3)
            {
                //Read in the original RAW.
                RAWImage rawImage = new RAWImage(new File(args[arg]), Integer.parseInt(args[arg+1]), Integer.parseInt(args[arg+2]));

                //Convert it to binary.
                BinaryImage A = rawImage.toBinaryImage();
                System.out.println("Original:");
                System.out.println(A);

                //Apply the skeleton transform.
                Skeleton skeletonOp = new Skeleton();
                BinaryImage skeleton = skeletonOp.apply(A);
                System.out.println("Skeleton:");
                System.out.println(skeleton);

                //Save the skeleton image.
                RAWImage binaryToRaw = new RAWImage(skeleton);
                ImageIO.write(binaryToRaw.toBufferedImage(), "png", new File(args[arg] + ".skeleton.png"));

                //Reconstruct the image using the skeleton points.
                Reconstruction reconstruction = new Reconstruction(skeletonOp.getAllSn(), Skeleton.unitDisk);
                BinaryImage reconstructed = reconstruction.apply(skeleton);
                System.out.println("Reconstructed:");
                System.out.println(reconstructed);

                //Save the reconstructed image.
                binaryToRaw = new RAWImage(reconstructed);
                ImageIO.write(binaryToRaw.toBufferedImage(), "png", new File("reconstructed.png"));

                //Save the partial reconstructions.
                int stage = 0;
                for (BinaryImage image : reconstruction.getStagesOfReconstruction())
                {
                    System.out.println("Reconstruction Stage " + stage + ":\n" + image);
                    binaryToRaw = new RAWImage(image);
                    ImageIO.write(binaryToRaw.toBufferedImage(), "png", new File("reconstruction." + stage + ".png"));
                    stage++;
                }

                //Move on to the next set of args.
                arg += 3;
            }
        }
    }

}
