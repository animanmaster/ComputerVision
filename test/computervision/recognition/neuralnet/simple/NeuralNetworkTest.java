/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition.neuralnet.simple;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Malik Ahmed
 */
public class NeuralNetworkTest {

    public NeuralNetworkTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    private ActivationFunction g = new ActivationFunction() {

        public double process(double... values) {
            double sum = 0;
            for (double val : values)
                sum += val;
            return (sum > 0 ? 1 : 0);
        }
    };
    private Neuron andNode = new Neuron(g, -1,  1, 1),
                 orNode  = new Neuron(g,  0,  1, 1);


    @Test
    public void andNetwork()
    {
        NeuralNetwork network = new NeuralNetwork(2);
        network.addLayer(andNode);
        double[] input = new double[] { 0, 0 };
        assertEquals(0.0, network.process(input)[0], 0.5);  //0 && 0 = 0
        input[0] = 0; input[1] = 1;
        assertEquals(0.0, network.process(input)[0], 0.5);  //0 && 1 = 0
        input[0] = 1; input[1] = 0;
        assertEquals(0.0, network.process(input)[0], 0.5);  //1 && 0 = 0
        input[0] = 1; input[1] = 1;
        assertEquals(1.0, network.process(input)[0], 0.5);  //1 && 1 = 1
    }

    @Test
    public void notNetwork()
    {
        NeuralNetwork network = new NeuralNetwork(1);
        network.addLayer(new Neuron(g, 0,  -1));
        double input = 0;
        assertEquals(0.0, network.process(input)[0], 0.5);  //!0 = 1
        input = 1;
        assertEquals(0.0, network.process(input)[0], 0.5);  //!1 = 0
    }

    @Test
    public void orNetwork()
    {
        NeuralNetwork network = new NeuralNetwork(2);
        network.addLayer(orNode);
        double[] input = new double[] { 0, 0 };
        assertEquals(0.0, network.process(input)[0], 0.5);  //0 || 0 = 0
        input[0] = 0; input[1] = 1;
        assertEquals(1.0, network.process(input)[0], 0.5);  //0 || 1 = 1
        input[0] = 1; input[1] = 0;
        assertEquals(1.0, network.process(input)[0], 0.5);  //1 || 0 = 1
        input[0] = 1; input[1] = 1;
        assertEquals(1.0, network.process(input)[0], 0.5);  //1 || 1 = 1
    }

    @Test
    public void xorNetwork()
    {
        NeuralNetwork network = new NeuralNetwork(2);
        network.addLayer(andNode, orNode);
        network.addLayer(new Neuron(g, 0, -1, 1));
        double[] input = new double[] { 0, 0 };
        assertEquals(0.0, network.process(input)[0], 0.5);  //0 xor 0 = 0
        input[0] = 0; input[1] = 1;
        assertEquals(1.0, network.process(input)[0], 0.5);  //0 xor 1 = 1
        input[0] = 1; input[1] = 0;
        assertEquals(1.0, network.process(input)[0], 0.5);  //1 xor 0 = 1
        input[0] = 1; input[1] = 1;
        assertEquals(0.0, network.process(input)[0], 0.5);  //1 xor 1 = 0
    }

}
