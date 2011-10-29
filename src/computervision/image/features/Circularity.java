/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features;

import computervision.image.BinaryImage;
import computervision.image.Pixel;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Malik Ahmed
 */
public class Circularity implements Feature<Double>
{
    private Perimeter perimeter = new Perimeter(true);
    private Centroid centroider = new Centroid();

    private double distance(Pixel pixel, Centroid.Position centroid)
    {
        return Math.sqrt(Math.pow(pixel.getRow() - centroid.row, 2) + Math.pow(pixel.getCol() - centroid.col, 2));
    }

    public Result<Double> extract(BinaryImage image, Map<String, Object> imageInfo)
    {
        double circularity = 0.0;
        if (image != null)
        {
            double mean_radial_distance = 0.0;
            double std_dev_of_radial_distance = 0.0;

            Centroid.Position centroid = centroider.extract(image, imageInfo).getValue();
            Set<Pixel> perimeterPixels = perimeter.extract(image, imageInfo).getValue();
            int r_bar = centroid.row, c_bar = centroid.col;
            int k = perimeterPixels.size();

            if (k != 0)
            {
                //Calculate mean
                for (Pixel pixel : perimeterPixels)
                {
                    mean_radial_distance += distance(pixel, centroid);
                }
                mean_radial_distance /= k;

                //calculate std_dev
                for (Pixel pixel : perimeterPixels)
                {
                    std_dev_of_radial_distance += Math.pow(distance(pixel, centroid) - mean_radial_distance, 2);
                }
                std_dev_of_radial_distance = Math.sqrt(std_dev_of_radial_distance/k);

                circularity = mean_radial_distance/std_dev_of_radial_distance;
            }
        }
        return new ComparableResult<Double>(circularity);
    }

}
