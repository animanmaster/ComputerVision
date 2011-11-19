/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition.neuralnet;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Malik Ahmed
 */
public class NeuralNetwork<T>
{
    protected List<Layer<T>> layers;

    public NeuralNetwork()
    {
        this(0);
    }

    public NeuralNetwork(int numLayers)
    {
        this.layers = new LinkedList<Layer<T>>();
        for (int i = 0; i < numLayers; i++)
            this.layers.add(new Layer<T>());
    }

}
