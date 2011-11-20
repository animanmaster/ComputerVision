/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition;

import computervision.image.BinaryImage;
import computervision.image.Pixel;
import computervision.image.features.BoundingBox;
import computervision.recognition.neuralnet.simple.NeuralNetwork;
import computervision.recognition.neuralnet.simple.ActivationFunction;
import computervision.recognition.neuralnet.simple.Neuron;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author Malik Ahmed
 */
public class AtoJRecognizer implements Recognizer<Character>
{
    public static final char UNRECOGNIZED = 0;

    private NeuralNetwork neuralNetwork = new NeuralNetwork(14);
    private BoundingBox boundingBox = new BoundingBox();
    private int[][] mappedImage = new int[7][7];

    private List<Future<Integer>> regionCounts = new LinkedList<Future<Integer>>();
    private ExecutorService executor = Executors.newCachedThreadPool();

    private static final char[] RECOGNIZABLE = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
    private static final ActivationFunction g = new ActivationFunction() {

        public double process(double... values) {
            double sum = 0;
            for (double val : values)
                sum += val;
            return 1/(1 + Math.pow(Math.E, -sum));  //g(x) = (1 + e^-x)^-1
        }
    };

    private class RegionCounter implements Callable<Integer>
    {
        private boolean[][] pixels;
        int row, col, width, height;

        RegionCounter(boolean[][] pixels, int row, int col, int width, int height)
        {
            this.pixels = pixels;
            this.row = row;
            this.col = col;
            this.width = width;
            this.height = height;
        }

        public Integer call() throws Exception
        {
            int total = 0;
            int maxRows = Math.min(row + width, pixels.length),
                maxCols = Math.min(col + height, pixels[0].length);
            for (int i = row; i < maxRows; i++)
            {
                for (int j = col; j < maxCols; j++)
                {
                    if (pixels[i][j])
                        total++;
                }
            }
            return total;
        }

    }

    public AtoJRecognizer()
    {
        this(null);
    }

    public AtoJRecognizer(NeuralNetwork network)
    {
        if (network == null)
            this.neuralNetwork = initNetwork();
        else
            this.neuralNetwork = network;
    }

    private static float randomWeight(Random rand)
    {
        return rand.nextFloat() - 0.5f;
    }

    private static float[] randomWeights(Random rand, int num)
    {
        float[] weights = new float[num];
        for (int i = 0; i < num; i++)
            weights[i] = randomWeight(rand);
        return weights;
    }

    private static Neuron[] randomlyWeightedNeurons(int numInputs, int num)
    {
        Neuron[] neurons = new Neuron[num];
        Random rand = new Random();
        for (int i = 0; i < num; i++)
            neurons[i] = new Neuron(g, randomWeight(rand), randomWeights(rand, numInputs));
        return neurons;
    }

    private static NeuralNetwork initNetwork()
    {
        NeuralNetwork neuralNetwork = new NeuralNetwork(14);
        neuralNetwork.addLayer(randomlyWeightedNeurons(14, 14));
        neuralNetwork.addLayer(randomlyWeightedNeurons(14, 12));
        neuralNetwork.addLayer(randomlyWeightedNeurons(12, 10));
        return neuralNetwork;
    }

    public NeuralNetwork getNeuralNetwork()
    {
        return neuralNetwork;
    }

    private int countRegion(boolean[][] pixels, int row, int col, int width, int height)
    {
        int total = 0;
        int maxRows = Math.min(row + height, pixels.length),
            maxCols = Math.min(col + width, pixels[0].length);
        for (int i = row; i < maxRows; i++)
        {
            for (int j = col; j < maxCols; j++)
            {
                if (pixels[i][j])
                    total++;
            }
        }
        return total;
    }

    private int getMappedRow(Pixel pixel, Rectangle box, int maxRows)
    {
        return (pixel.getRow() - box.y)/maxRows;
    }

    private int getMappedCol(Pixel pixel, Rectangle box, int maxCols)
    {
        return (pixel.getCol() - box.x)/maxCols;
    }

    private double[] getNeuralNetInput(BinaryImage image)
    {
        Rectangle box = boundingBox.extract(image, null).getValue();
        //dividing the bounding box into a 7x7 image
//        int partitionWidth = box.width/7, partitionHeight = box.height/7;
//        boolean[][] pixels = image.getPixelValues();
        //if the number of pixels in the region exceeds this value, consider it a 1 in the mappedImage;
//        int threshold = 0;
        
//        for (int i = 0; i < mappedImage.length; i++)
//        {
//            for (int j = 0; j < mappedImage[i].length; j++)
//            {
//                regionCounts.add(executor.submit(new RegionCounter(pixels, i * partitionWidth, j * partitionHeight, partitionWidth, partitionHeight)));
//            }
//        }
//
//        Iterator<Future<Integer>> it = regionCounts.iterator();
//        for (int i = 0; i < mappedImage.length; i++)
//        {
//            for (int j = 0; j < mappedImage[i].length; j++)
//            {
//                try
//                {
//                    mappedImage[i][j] = (it.next().get() > threshold? 1 : 0) ;
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                    mappedImage[i][j] = 0;
//                }
//            }
//        }
        for (int[] row : mappedImage)
        {
            for (int i = 0; i < row.length; i++)
            {
                row[i] = 0;
            }
        }
        Pixel[] pixels = image.getPixels();
        int i, j;
        for (Pixel pixel : pixels)
        {
            i = Math.min(getMappedRow(pixel, box, 7), 6);   //in case we get a 7
            j = Math.min(getMappedCol(pixel, box, 7), 6);
            mappedImage[i][j] = 1;
        }

//        System.out.println("Mapped Image:");
//        for (int[] row : mappedImage)
//            System.out.println(Arrays.toString(row));
        
        double[] in = new double[14];
        double val;
        for (int row = 0; row < mappedImage.length; row++)
        {
            for (int col = 0; col < mappedImage[row].length; col++)
            {
                val = mappedImage[row][col];
                in[row] += val;
                in[col + mappedImage.length] += val;
            }
        }

//        System.out.println("Input: " + Arrays.toString(in));

        //Normalization TODO Make this more efficient! You can do this inside the above loop with some effort.
        for (int row = 0; row < mappedImage.length; row++)
        {
            in[row] = in[row]/10.0 + 0.15;    //Turns 0 into .15, 1 to .25, ..., 7 to .85
            for (int col = 0; col < mappedImage[row].length; col++)
            {
                in[col + mappedImage.length] = in[col + mappedImage.length]/10 + 0.15;
            }
        }
//        System.out.println("Normalized Input: " + Arrays.toString(in));

        return in;
    }
    
    private int getResultIndex(double[] output)
    {
        double maxValue = Double.MIN_VALUE;
        int index = -1;
        
        for (int i = 0; i < output.length; i++)
        {
            if (output[i] > maxValue)
            {
                maxValue = output[i];
                index = i;
            }
        }
        return index;
    }
    
    public char train(BinaryImage image, char actual, double learnRate)
    {
        double[] input = getNeuralNetInput(image);
        double[] ideal = new double[10];
        int index = -1;
        char result = 0;
        for (index = 0; index < input.length && (RECOGNIZABLE[index] != actual); index++)
        {
            //lolwut
        }
        ideal[index] = 1.0; //the rest are 0 by default.
        index = getResultIndex(neuralNetwork.train(ideal, learnRate, input).getLast());
        if (index >= 0 && index < RECOGNIZABLE.length)
            result = RECOGNIZABLE[index];
        System.out.println("Trained the network for '" + actual + "':");
        System.out.println(neuralNetwork);
        return result;
    }

    public Character recognize(BinaryImage image)
    {
//        image = preprocess(image);
        char recognized = 0;
        int index = getResultIndex(neuralNetwork.process(getNeuralNetInput(image)));
        if (index >= 0 && index < RECOGNIZABLE.length)
            recognized = RECOGNIZABLE[index];
        return recognized;
    }

}
