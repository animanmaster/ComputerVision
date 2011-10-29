/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features.components;

import computervision.image.features.Moment.MomentResult;
import computervision.image.BinaryImage;
import computervision.image.Pixel;
import computervision.image.features.BoundingBox;
import computervision.image.features.Centroid;
import computervision.image.features.Centroid.Position;
import computervision.image.features.Moment;
import java.util.Collection;

//TODO Ew
import static computervision.image.features.BoundingBox.*;

/**
 *
 * @author Malik Ahmed
 */
public class Component extends BinaryImage
{
    private String label;
    private BinaryImage originalImage;

    private BoundingBox boundingBoxer = new BoundingBox();
    private Centroid centroidExtractor = new Centroid();
    private Moment moment = new Moment();



    public static byte QUAD1 = 1,
                       QUAD2 = 1 << 1,
                       QUAD3 = 1 << 2,
                       QUAD4 = 1 << 3;


    private Box boundingBox = null;
    private Position centroid = null, originalCentroid = null;
    private MomentResult moments = null;

    public Component(String label, BinaryImage originalImage, Collection<Pixel> pixels)
    {
        super(pixels, originalImage.getNumberOfRows(), originalImage.getNumberOfColumns());
        this.label = label;
        this.originalImage = originalImage;
        this.originalCentroid = centroidExtractor.extract(originalImage, null).getValue();
    }

    public Component(String label, BinaryImage originalImage)
    {
        this(label, originalImage, null);
    }

    public String getLabel()
    {
        return label;
    }

    protected void dirty()
    {
        this.boundingBox = null;
        this.centroid = null;
        this.moments = null;
    }

    protected void recalculateStuff()
    {
        this.boundingBox = (Box)boundingBoxer.extract(this, null);
        this.centroid = centroidExtractor.extract(this, null).getValue();
        this.moments = (MomentResult)moment.extract(this, null);
    }

    @Override
    public boolean addPixel(Pixel pixel)
    {
        dirty();
        return super.addPixel(pixel);
    }

    @Override
    public boolean removePixel(Pixel pixel)
    {
        dirty();
        return super.removePixel(pixel);
    }

    @Override
    public void removePixels(Collection<Pixel> pixels) {
        dirty();
        super.removePixels(pixels);
    }


    public MomentResult getMoments()
    {
        if (moments == null)
            recalculateStuff();
        return moments;
    }

    public Box getBoundingBox()
    {
        if (boundingBox == null)
            recalculateStuff();
        return boundingBox;
    }

    public Position getCentroid()
    {
        if (centroid == null)
            recalculateStuff();
        return centroid;
    }

    private boolean checkRelationship(Box otherBox, byte relationship)
    {
        return ((getBoundingBox().getSpatialRelationship(otherBox) & relationship) != NONE);
    }

    public boolean isVertical()
    {
        return getMoments().getRowMoment() > getMoments().getColumnMoment();
    }

    public boolean isHorizontal()
    {
        return getMoments().getColumnMoment() > getMoments().getRowMoment();
    }

    public boolean isAbove(Component otherComponent)
    {
        return checkRelationship(otherComponent.getBoundingBox(), ABOVE);
    }

    public boolean isBelow(Component otherComponent)
    {
        return checkRelationship(otherComponent.getBoundingBox(), BELOW);
    }

    public boolean isToTheRightOf(Component otherComponent)
    {
        return checkRelationship(otherComponent.getBoundingBox(), RIGHT);
    }

    public boolean isToTheLeftOf(Component otherComponent)
    {
        return checkRelationship(otherComponent.getBoundingBox(), LEFT);
    }

    public boolean isSlightlyAbove(Component otherComponent)
    {
        return getCentroid().row < otherComponent.getCentroid().row;
    }

    public boolean isSlightlyBelow(Component otherComponent)
    {
        return getCentroid().row > otherComponent.getCentroid().row;
    }

    public boolean isSlightlyLeftOf(Component otherComponent)
    {
        return getCentroid().col < otherComponent.getCentroid().col;
    }

    public boolean isSlightlyRightOf(Component otherComponent)
    {
        return getCentroid().col > otherComponent.getCentroid().col;
    }

    public int getQuadrant()
    {
        int quadrant = (QUAD1 | QUAD2 | QUAD3 | QUAD4);
        if (getCentroid().row < originalCentroid.row)
        {
            //In either quad 1 or quad2.
            quadrant &= QUAD1 | QUAD2;
            if (getCentroid().col > originalCentroid.col)
                quadrant &= QUAD1;
            else if (getCentroid().col < originalCentroid.col)
                quadrant &= QUAD2;
        }
        else if (getCentroid().row > originalCentroid.row)
        {
            //In either quad 3 or 4.
            quadrant &= QUAD3 | QUAD4;

            if (getCentroid().col > originalCentroid.col)
                quadrant &= QUAD4;
            else if (getCentroid().col < originalCentroid.col)
                quadrant &= QUAD3;
        }
        return quadrant;
    }

    public boolean isInQuadrant(int quads)
    {
        return (getQuadrant() & quads )> 0;
    }

}
