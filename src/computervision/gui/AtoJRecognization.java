/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.gui;

import computervision.recognition.AtoJRecognizer;
import computervision.recognition.neuralnet.simple.NeuralNetwork;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Malik Ahmed
 */
public class AtoJRecognization extends BinaryDraw
{

    private AtoJRecognizer recognizer = null;

    public static void main(String[] args)
    {
        new AtoJRecognization().setVisible(true);
    }

    public AtoJRecognization()
    {
        super();
        buildGui();
    }

    private void buildGui()
    {
        Container drawingPane = getContentPane();
        final JPanel content = new JPanel(new BorderLayout(5, 5));
        final JPanel recognition = new JPanel(new BorderLayout());

        final JLabel recognized = new JLabel();
        setTitle("Vision HW 5 - Neural Net A - J Recognization");

        JPanel buttonContainer = new JPanel(new GridLayout(0, 1));
        JButton button = new JButton("Recognize");
        button.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                if (recognizer == null)
                {
                    JOptionPane.showMessageDialog(AtoJRecognization.this, "Load a neural network first!");
                }
                else
                {
                    Character result = recognizer.recognize(getBinaryImage());
                    if (result == null || result.equals(AtoJRecognizer.UNRECOGNIZED))
                    {
                        recognized.setText("<html><center><h1>?</h1></center></html>");
                    }
                    else
                    {
                        recognized.setText("<html><center><h1>"+result+"</h1></center></html>");
                    }
                    content.revalidate();
                }
            }
        });
        buttonContainer.add(button);
        button = new JButton("Load Neural Network");
        button.addActionListener(new ActionListener() {
            private JFileChooser chooser = new JFileChooser();
            {
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileFilter(new FileNameExtensionFilter("Neural Network", "net", "NET"));
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            }
            public void actionPerformed(ActionEvent e) {
                if (chooser.showOpenDialog(AtoJRecognization.this) == JFileChooser.APPROVE_OPTION)
                {
                    try
                    {
                        NeuralNetwork neuralNet = (NeuralNetwork)new ObjectInputStream(new FileInputStream(chooser.getSelectedFile())).readObject();
                        recognizer = new AtoJRecognizer(neuralNet);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(AtoJRecognization.this, "Invalid Neural Network object!");
                    }
                }
            }
        });
        buttonContainer.add(button);
        recognition.add(recognized, BorderLayout.CENTER);
        recognition.add(buttonContainer, BorderLayout.SOUTH);

        content.add(drawingPane, BorderLayout.CENTER);
        content.add(recognition, BorderLayout.EAST);
        setContentPane(content);
    }
}
