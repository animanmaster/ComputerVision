/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition;

import computervision.image.BinaryImage;

/**
 *
 * @author Malik Ahmed
 */
public interface Recognizer<T>
{
    T recognize(BinaryImage image);
}
