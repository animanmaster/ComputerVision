/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition.neuralnet;

/**
 *
 * @author Malik Ahmed
 */
public class Node<T> implements Cloneable
{
    protected ActivationFunction<T> function;
    protected Weighter<T> weighter;
    protected float[] weights;  //TODO abstract the weight application so that
                                //weights can be applied to non-numeric numbers

    public Node(ActivationFunction<T> function)
    {
        this.function = function;
        this.weights = new float[]{1f};
    }

    public T process(T... inputs)
    {
        return function.process(inputs);
    }

    public void setInputWeights(float[] weights)
    {
        if (weights != null && weights.length > 0)
        {
            this.weights = weights;
        }
    }

    @Override
    public Node<T> clone()
    {
        return new Node<T>(function);
    }

}
