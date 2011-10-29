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
public interface Feature<T>
{
    Result<T> extract(BinaryImage image, Map<String, Object> imageInfo);
}
