/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.gui;

import computervision.image.BinaryImage;
import computervision.image.RAWImage;
import computervision.recognition.AtoJRecognizer;
import computervision.recognition.neuralnet.simple.NeuralNetwork;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * TODO Make this more generalized (later).
 * @author Malik Ahmed
 */
public class TrainingViz extends JFrame
{
    //TODO
    //BAMF diagram with nodes and weights and GOOD STUFF.

    private class SampleImage
    {
        private BinaryImage image;
        private char character;
        private File srcFile;

        public SampleImage(BinaryImage image, char character, File srcFile)
        {
            this.image = image;
            this.character = character;
            this.srcFile = srcFile;
        }
    }

    private class NeuralNetViz extends JPanel
    {
        private NeuralNetwork network;
        private JLabel label;

        NeuralNetViz(NeuralNetwork network)
        {
            this.network = network;
            setLayout(new GridBagLayout());
            label = new JLabel();
            add(label, new GridBagConstraints());
            redraw();
        }

        
        void redraw()
        {
            label.setText("<html>" + network.toString().replace("\n", "<br>").replace("\t", "____") + "</html>");
            repaint();
        }
    }

    private NeuralNetViz netviz;
    private AtoJRecognizer recognizer;

    private static final File storedNeuralNet = new File(System.getProperty("user.home") + File.separator + ".computervision/neuralnet.obj");

    private ObjectOutputStream os = null;
    private ObjectInputStream is = null;

    private List<SampleImage> samples = new ArrayList<SampleImage>();

    private volatile boolean inTraining = false, liveUpdate = true;
    
    public static void main(String[] args)
    {
        new TrainingViz().setVisible(true);
    }


    public TrainingViz()
    {
        super("Training Visualization");
        this.recognizer = new AtoJRecognizer(readNeuralNet());
        this.netviz = new NeuralNetViz(recognizer.getNeuralNetwork());
        build();
        writeNeuralNet(recognizer.getNeuralNetwork());
        setPreferredSize(new Dimension(400, 300));
        setSize(getPreferredSize());
        setVisible(true);
    }

    private void readSamples(File dir)
    {
        samples.clear();
        File[] rawFiles = dir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("raw");
            }
        });
        for (File file : rawFiles)
        {
            try
            {
                samples.add(new SampleImage(new RAWImage(file, 128, 128).toBinaryImage(), file.getName().charAt(0), file));
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
    }

    private void build()
    {
        setLayout(new BorderLayout(5,5));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel;

//        final JLabel status = new JLabel();
        //Action panel
        panel = new JPanel();
        JButton button = new JButton("Load Samples Dir");
        button.addActionListener(new ActionListener() {
            private JFileChooser chooser = new JFileChooser();

            {
                chooser.setDialogTitle("Load Samples");
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

                // disable the "All files" option.
                //
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileFilter(new FileNameExtensionFilter("RAW Images", "raw", "RAW"));
                //
            }

            public void actionPerformed(ActionEvent e) 
            {
              if (chooser.showOpenDialog(TrainingViz.this) == JFileChooser.APPROVE_OPTION) {
                  File file = chooser.getSelectedFile();
                  if (!file.isDirectory())
                      file = file.getParentFile();
                  readSamples(file);
              }
            }
        });
        panel.add(button);
        final JButton train = new JButton("Start Training");
        final JButton stopTraining = new JButton("Stop Training");
        final JLabel trainingSessions = new JLabel("0 (0%)");
        panel.add(trainingSessions);
        panel.add(train);
        panel.add(stopTraining);
        trainingSessions.setToolTipText("Number of times the network has been trained and its correctness percentage");
        train.addActionListener(new ActionListener() {

            double averageCorrectness = 0;
            int trainingCount = 0;

            final double delta = 0.1;
            final Random rand = new Random();
            final Runnable update = new Runnable()
            {
                public void run() 
                {
                    netviz.redraw();
                    trainingSessions.setText(trainingCount + " (" + String.format("%.2f", averageCorrectness * 100.0) + "%)");
                }  
            };
            final Runnable doTraining = new Runnable()
                {
                    public void run()
                    {
                        int num;
                        SampleImage sample;
                        char result;
                        while (inTraining)
                        {
                            num = rand.nextInt(samples.size());
                            sample = samples.get(num);
                            result = recognizer.train(sample.image, sample.character, delta);
                            updateAverageAndTotal(result == sample.character);
                            SwingUtilities.invokeLater(update);
                        }
                        updateButtons(inTraining);
                    }
                };
            
            private void updateAverageAndTotal(boolean latestIsCorrect)
            {
                trainingCount++;
                double an = (latestIsCorrect? 1.0 : 0.0);
                //recursive formula for average is
                //avg = (n-1)/n * lastAvg * an/n
                averageCorrectness = (trainingCount-1)/trainingCount * averageCorrectness + (an / trainingCount);
            }

            void updateButtons(boolean isTraining)
            {
                train.setEnabled(!isTraining);
                stopTraining.setEnabled(isTraining);
            }

            public void actionPerformed(ActionEvent e) 
            {
                inTraining = true;
                updateButtons(inTraining);
                Thread t = new Thread(doTraining, "TrainingThread");
                t.setDaemon(true);  //if this thread is the only thread left, go ahead and kill the VM.
                t.start();
            }
        });
        stopTraining.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                inTraining = false;
            }
        });
        button = new JButton("Test all Samples");
        button.addActionListener(new ActionListener() {

            JFrame frame = new JFrame("Test Results");
            private String getTableHeader()
            {
                return "<tr><th>File</th><th>Expected</th><th>Result</th></tr>";
            }

            private void appendTestResultString(StringBuilder str, SampleImage sample, char result)
            {
                str.append("<tr bgcolor=").append((sample.character == result? "'green'" : "'red'"))
                        .append("><td>").append(sample.srcFile.getName())
                        .append("</td><td><center>").append(sample.character)
                        .append("</center></td><td><center>").append(result).append("</center></td></tr>");
            }

            public void actionPerformed(ActionEvent e) {
                if (inTraining)
                {
                    JOptionPane.showMessageDialog(TrainingViz.this, "Can't test while training!");
                }
                else
                {
                    char result;
                    float numCorrect = 0;
                    StringBuilder testResults = new StringBuilder("<html>");
                    testResults.append("<table width='100%'>")
                               .append(getTableHeader());
                    for (SampleImage sample : samples)
                    {
                        result = recognizer.recognize(sample.image);
                        appendTestResultString(testResults, sample, result);
                        if (result == sample.character)
                            numCorrect++;
                    }
                    testResults.append("</table><center><b>Correctness: ")
                            .append(numCorrect/samples.size()).append("</b></center></html>");
                    frame.setContentPane(new JScrollPane(new JLabel(testResults.toString())));
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setSize(400, 300);
                    frame.setVisible(true);
                }
            }
        });
        panel.add(button);
        button = new JButton("Save Neural Network");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                writeNeuralNet(recognizer.getNeuralNetwork());
            }
        });
        panel.add(button);
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(netviz), BorderLayout.CENTER);
    }

    private boolean writeNeuralNet(NeuralNetwork net)
    {
        boolean greatSuccess = true;
        try
        {
            os = new ObjectOutputStream(new FileOutputStream(storedNeuralNet, false));
            os.writeObject(net);
            os.flush();
            os.close();
            os = null;
        }
        catch(Exception e)
        {
            greatSuccess = false;
            e.printStackTrace();
        }
        return greatSuccess;
    }

    private NeuralNetwork readNeuralNet()
    {
        NeuralNetwork net = null;
        storedNeuralNet.getParentFile().mkdirs();
        try
        {
            if (is == null)
                is = new ObjectInputStream(new FileInputStream(storedNeuralNet));
            if (storedNeuralNet.exists())
                net = (NeuralNetwork) is.readObject();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return net;
    }
}
