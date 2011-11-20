/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition.neuralnet.simple;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Malik Ahmed
 */
public class Layer implements Serializable
{
    protected List<Neuron> nodes;
    private double[] output;

    public Layer()
    {
        this.nodes = new LinkedList<Neuron>();
        this.output = new double[0];
    }

    public Layer(Neuron[] nodes)
    {
        this();
        this.addNodes(nodes);
    }

    public void addNodes(Neuron... nodes)
    {
        if (nodes != null && nodes.length > 0)
            this.nodes.addAll(Arrays.asList(nodes));
    }

    public void removeNodes(Neuron... nodes)
    {
        if (nodes != null && nodes.length > 0)
            this.nodes.removeAll(Arrays.asList(nodes));
    }

    private void resizeOutput()
    {
        if (nodes.size() != output.length)
            output = new double[nodes.size()];
    }

    public Neuron[] getNodes()
    {
        return this.nodes.toArray(new Neuron[nodes.size()]);
    }

    protected double[] process(double... inputs)
    {
        resizeOutput();
        for (int i = 0; i < nodes.size(); i++)
            output[i] = nodes.get(i).process(inputs);
        return output;
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        str.append("Layer (").append(nodes.size()).append(" nodes)\n");
        int n = 0;
        for (Neuron node : nodes)
        {
            str.append("\tNode ").append(n++).append(": ").append(node).append("\n");
        }
        return str.toString();
    }
}
