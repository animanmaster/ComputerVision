/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition.neuralnet.simple;

import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @author Malik Ahmed
 */
public class Neuron implements Cloneable, Serializable
{
    private static final long serialVersionUID = 9003L;

    public static final float[] DEFAULT_WEIGHTS = new float[]{ 1.0f };
    public static final float   DEFAULT_SPECIAL_WEIGHT = 0.0f;

    protected ActivationFunction function;
    protected float specialWeight;
    protected float[] weights;  //TODO abstract the weight application so that
                                //weights can be applied to non-numeric numbers

    private Neuron()
    {
    }

    public Neuron(ActivationFunction function)
    {
        this(function, DEFAULT_SPECIAL_WEIGHT, DEFAULT_WEIGHTS);
    }

    public Neuron(ActivationFunction function, float w0, float... weights)
    {
        this.specialWeight = w0;
        this.function = function;
        this.weights = (weights == null || weights.length == 0? DEFAULT_WEIGHTS : weights);
    }

    double process(double... inputs)
    {
        double[] weighted_inputs = new double[inputs.length + 1];
        weighted_inputs[0] = specialWeight; //special input = 1 * w0.
        for (int i = 1; i <= inputs.length; i++)
        {
            weighted_inputs[i] = inputs[i-1] * weights[(i-1) % weights.length];
        }
        return function.process(weighted_inputs);
    }

    public void setInputWeights(float[] weights)
    {
        if (weights != null && weights.length > 0)
        {
            this.weights = weights;
        }
        else
        {
            this.weights = DEFAULT_WEIGHTS;     //Eh...
        }
    }

    public void setSpecialWeight(float w0)
    {
        this.specialWeight = w0;
    }

    public float[] getInputWeights()
    {
        return Arrays.copyOf(weights, weights.length);
    }

    public float getSpecialWeight()
    {
        return specialWeight;
    }

    @Override
    public Neuron clone()
    {
        Neuron newNeuron = new Neuron(function);
        newNeuron.specialWeight = specialWeight;
        newNeuron.weights = weights;
        return newNeuron;
    }

    @Override
    public String toString()
    {
        return "w[] = " + Arrays.toString(weights);
    }
}
