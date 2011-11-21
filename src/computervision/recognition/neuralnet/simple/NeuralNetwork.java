/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition.neuralnet.simple;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Malik Ahmed
 */
public class NeuralNetwork implements Serializable, Cloneable
{
    private static final long serialVersionUID = 9001L;

    protected LinkedList<Layer> layers;
    protected int numInputs;

    public NeuralNetwork(int numInputs)
    {
        this.layers = new LinkedList<Layer>();
        this.numInputs = numInputs;
    }

    public int getNumInputs()
    {
        return numInputs;
    }

    public void setNumInputs(int numInputs)
    {
        this.numInputs = numInputs;
    }

    public int addLayer(int numNodes, Neuron prototype)
    {
        if (numNodes > 0)
        {
            Layer layer = new Layer();
            for (int i = 0; i < numNodes; i++)
            {
                layer.addNodes(prototype.clone());
            }
            layers.add(layer);
        }
        return layers.size();
    }

    public int addLayer(Neuron... nodes)
    {
        if (nodes.length > 0)
        {
            Layer layer = new Layer();
            layer.addNodes(nodes);
            layers.add(layer);
        }
        return layers.size();
    }

    public boolean removeLayer(int layerNumber)
    {
        boolean removed = false;
        if (layerNumber >= 0 && layerNumber < layers.size())
        {
            layers.remove(layerNumber);
            removed = true;
        }
        return removed;
    }

    public double[] process(double... in)
    {
        LinkedList<double[]> outputs = new LinkedList<double[]>();
        process(in, outputs);
        return outputs.getLast();
    }

    private List<double[]> process(double[] in, List<double[]> outputs)
    {
        if (in == null || in.length != numInputs)
            throw new IllegalArgumentException("Expected " + numInputs + " inputs, got " + (in == null? "null" : in.length));

        if (outputs == null)
            outputs = new LinkedList<double[]>();

        double[] output = in;
        outputs.add(output);
        for (Layer layer : layers)
        {
            output = layer.process(output);
            outputs.add(output);
        }
        return outputs;
    }

    public LinkedList<double[]> train(double[] expectedOutput, double learnRate, double... in)
    {
        LinkedList<double[]> outputs = new LinkedList<double[]>();
        process(in, outputs);

        Iterator<double[]> outputIterator = outputs.descendingIterator();
        Iterator<Layer> layerIterator = layers.descendingIterator();
        double[] layerOuts = outputIterator.next();
        double[] layerIns = outputIterator.next();
        Layer layer = layerIterator.next();
        Neuron[] nodes = layer.getNodes();

        Neuron node;
        double nodeOut;
        double[] delta = new double[nodes.length];
        double gPrime, base;

        //Output layer node.
        //System.out.println("[NeuralNetwork] Output Layer");
        for (int j = 0; j < nodes.length; j++)
        {
            nodeOut = layerOuts[j];
            delta[j] = expectedOutput[j] - nodeOut;
            node = nodes[j];
            gPrime = nodeOut * (1 - nodeOut);
            //System.out.println("[NeuralNetwork] Output Layer gPrime = " + gPrime );
            base = learnRate * delta[j] * gPrime; //this stays the same for the node.
            //handle special weight first, where input = 1.
            node.specialWeight += base * 1;
            //System.out.println("[NeuralNetwork] base = " + base);
            for (int i = 0; i < layerIns.length; i++)
            {
                node.weights[i] += base * layerIns[i];
                //System.out.println("[NeuralNetwork] Output Layer Weight " + i + " = " + node.weights[i]);
            }
        }

        double[] lastDelta = delta;
        Neuron[] lastNodes = nodes;
        while (layerIterator.hasNext() && outputIterator.hasNext())
        {
            layerOuts = layerIns;   //the previous layer's inputs are the new layer's outputs.
            layerIns = outputIterator.next();
            layer = layerIterator.next();

            lastNodes = nodes;
            nodes = layer.getNodes();

            lastDelta = delta;
            delta = new double[nodes.length];

            for (int j = 0; j < nodes.length; j++)
            {
                nodeOut = layerOuts[j];
                node = nodes[j];
                for (int i = 0; i < lastNodes.length; i++)
                {
                    delta[j] += (lastNodes[i].weights[j] * lastDelta[i]);
                }
                gPrime = nodeOut * (1 - nodeOut);
                //System.out.println("[NeuralNetwork] Hidden Layer gPrime = " + gPrime );
                base = learnRate * delta[j] * gPrime;
                //handle special weight first, where input = 1.
                node.specialWeight += base * 1;

                //System.out.println("[NeuralNetwork] Hidden Layer base = " + base );
                for (int i = 0; i < layerIns.length; i++)
                {
                    node.weights[i] += base * layerIns[i];
                    //System.out.println("[NeuralNetwork] Hidden Layer Node " + i + " weight = " + node.weights[i]);
                }
            }
        }
        return outputs;
    }

    public Neuron[][] getNeurons()
    {
        Neuron[][] neurons = new Neuron[layers.size()][];
        for (int i = 0; i < layers.size(); i++)
        {
            neurons[i] = layers.get(i).getNodes();
        }
        return neurons;
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        str.append("Neural Network [").append(layers.size()).append(" layers]:\n");
        int n = 0;
        for (Layer layer : layers)
            str.append("Layer ").append(n++).append(": ").append(layer).append("\n");
        return str.toString();
    }

    @Override
    public NeuralNetwork clone()
    {
        NeuralNetwork newNet = new NeuralNetwork(numInputs);
        Neuron[] neurons;
        for (Layer layer : layers)
        {
            neurons = layer.getNodes();
            for (int i = 0; i < neurons.length; i++)
                neurons[i] = neurons[i].clone();
            newNet.addLayer(neurons);
        }
        return newNet;
    }

}
