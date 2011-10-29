/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features;

import computervision.image.BinaryImage;
import computervision.image.Pixel;
import java.util.Map;

/**
 *
 * @author Malik Ahmed
 */
public class Area implements Feature<Integer>
{

    @Override
    public Result<Integer> extract(BinaryImage image, Map<String, Object> imageInfo)
    {
        int area = 0;
        Pixel[] pixels = image.getPixels();
        for (Pixel pixel : pixels)
        {
//            area += pixel.getValue();
            area++;
        }
        return new ComparableResult<Integer>(area);
    }

}
