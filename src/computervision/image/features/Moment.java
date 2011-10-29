/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features;

import computervision.image.BinaryImage;
import computervision.image.Pixel;
import computervision.image.features.Moment.MomentResult;
import java.util.Map;

/**
 *
 * @author Malik Ahmed
 */
public class Moment implements Feature<double[]>
{
    public class MomentResult extends Result<double[]>
    {
        private MomentResult(double rowMoment, double colMoment, double mixedMoment)
        {
            super(new double[]{ rowMoment, colMoment, mixedMoment});
        }

        public double getRowMoment()
        {
            return getValue()[0];
        }

        public double getColumnMoment()
        {
            return getValue()[1];
        }

        public double getMixedMoment()
        {
            return getValue()[2];
        }
    }
    
    private Area areaFeature = new Area();
    private Centroid centroidFeature = new Centroid();

    private double getArea(BinaryImage image, Map<String, Object> imageInfo)
    {
        double area = 0;
        if (imageInfo != null && imageInfo.get(Area.class.getName()) instanceof Number)
        {
            area = ((Result<Integer>)imageInfo.get(Area.class.getName())).getValue();
        }
        else if (image != null)
        {
            area = this.areaFeature.extract(image, imageInfo).getValue();
        }
        return area;
    }

    private Centroid.Position getCentroid(BinaryImage image, Map<String, Object> imageInfo)
    {
        Centroid.Position position = null;
        if (imageInfo != null && imageInfo.get(Centroid.class.getName()) instanceof Number)
        {
            position = ((Result<Centroid.Position>)imageInfo.get(Centroid.class.getName())).getValue();
        }
        else
        {
            position = this.centroidFeature.extract(image, imageInfo).getValue();
        }
        return position;
    }

    @Override
    public Result<double[]> extract(BinaryImage image, Map<String, Object> imageInfo)
    {
        double r_bar, c_bar;
        double row_moment = 0, col_moment = 0, mixed_moment = 0;

        if (image != null)
        {
//            area = getArea(image, imageInfo);
            double area = 0;
            Centroid.Position centroid = getCentroid(image, imageInfo);
            if (centroid != null)
            {
                r_bar = centroid.row;
                c_bar = centroid.col;
            }
            else
            {
                r_bar = c_bar = 0;
            }
            Pixel[] pixels = image.getPixels();
            double row_diff, col_diff;
            for (Pixel pixel : pixels)
            {
                row_diff = (pixel.getRow() - r_bar);
                col_diff = (pixel.getCol() - c_bar);

                row_moment += (row_diff * row_diff);
                col_moment += (col_diff * col_diff);
                mixed_moment += (row_diff * col_diff);

                area++;
            }
            row_moment /= area;
            col_moment /= area;
            mixed_moment /= area;
        }

        return new MomentResult(row_moment, col_moment, mixed_moment);
    }

}
