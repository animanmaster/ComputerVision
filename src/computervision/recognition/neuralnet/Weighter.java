/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition.neuralnet;

/**
 *
 * @author Malik Ahmed
 */
public interface Weighter<T>
{
    T apply(T value, float weight);
}
