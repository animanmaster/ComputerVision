/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition.neuralnet.simple;

import java.io.Serializable;

/**
 *
 * @author Malik Ahmed
 */
public interface ActivationFunction extends Serializable
{
    double process(double... values);
}
