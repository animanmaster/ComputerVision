/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features;

import computervision.image.BinaryImage;
import java.util.Map;

/**
 *
 * @author Malik Ahmed
 */
public class Holes implements Feature<Double>
{

    @Override
    public Result<Double> extract(BinaryImage image, Map<String, Object> imageInfo)
    {
        int external = 0, internal = 0;
        boolean[][] pixels = image.getPixelValues();
        boolean[] row1, row2;
        int total;
        for (int index = 0; index < image.getNumberOfRows() - 1; index++)
        {
            row1 = pixels[index];
            row2 = pixels[index+1];
            for (int column = 0; column < row1.length - 1; column++)
            {
                if (column < row1.length - 1 && column < row2.length - 1)
                {
                    total = (row1[index]? 1 : 0)
                             + (row1[index+1]? 1 : 0)
                             + (row2[index]? 1 : 0)
                             + (row2[index+1] ? 1 : 0);
                    if (total == 1)
                        internal++;
                    else if (total == 3)
                        external++;
                }
            }
        }
        return new ComparableResult((external - internal)/4.0);
    }

}
