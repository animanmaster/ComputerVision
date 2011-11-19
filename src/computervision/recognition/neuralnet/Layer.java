/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition.neuralnet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Malik Ahmed
 */
public class Layer<T>
{
    protected Collection<Node<T>> nodes;

    public Layer()
    {
        this.nodes = new LinkedList<Node<T>>();
    }

    public void addNodes(Node<T>... nodes)
    {
        if (nodes != null && nodes.length > 0)
            this.nodes.addAll(Arrays.asList(nodes));
    }

    /**
     * Add the given node to this layer num times.
     * Note that the reference is used, so the same node object will be used
     * for each calculation.
     * @param node  the node to use
     * @param num   the number of times to use this node
     */
    public void addNode(Node<T> node, int num)
    {
        if (node != null)
        {
            for (int i = 0; i < num; i++)
            {
                this.nodes.add(node);
            }
        }
    }

    public void removeNodes(Node<T>... nodes)
    {
        if (nodes != null && nodes.length > 0)
            this.nodes.removeAll(Arrays.asList(nodes));
    }

    public Node<T>[] getNodes()
    {
        return (Node<T>[])this.nodes.toArray();
    }

    protected T[] process(T... inputs)
    {
        List<T> output = new ArrayList<T>(this.nodes.size());
        for (Node<T> node : this.nodes)
            output.add(node.process(inputs));
        return (T[])output.toArray();
    }
    
}
