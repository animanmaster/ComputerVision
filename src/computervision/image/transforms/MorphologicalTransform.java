/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.transforms;

import computervision.image.BinaryImage;

/**
 *
 * @author Malik Ahmed
 */
public interface MorphologicalTransform
{
    BinaryImage apply(BinaryImage image);
}
