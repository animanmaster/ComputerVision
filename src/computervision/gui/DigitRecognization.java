/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.gui;

import computervision.gui.components.RAWImagePanel;
import computervision.image.BinaryImage;
import computervision.image.RAWImage;
import computervision.recognition.DigitRecognizer;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Malik Ahmed
 */
public class DigitRecognization extends BinaryDraw
{
    public DigitRecognization()
    {
        super();
        buildGui();
    }

    private void buildGui()
    {
        Container drawingPane = getContentPane();
        final JPanel content = new JPanel(new BorderLayout(5, 5));
        final JPanel intermediateImages = new JPanel(new GridLayout(1, 0, 2, 2));
        final JPanel recognition = new JPanel(new BorderLayout());
        
        final JLabel recognized = new JLabel();
        setTitle("Vision HW 4 - Digit Recognization");

        JPanel buttonContainer = new JPanel(new GridLayout(1, 1));
        JButton recognize = new JButton("RECOGNIZE");
        recognize.addActionListener(new ActionListener()
        {

            private DigitRecognizer recognizer = new DigitRecognizer();

            public void actionPerformed(ActionEvent e)
            {
                Integer result = recognizer.recognize(getBinaryImage());
                if (result == null)
                {
                    recognized.setText("<html><center><h1>?</h1></center></html>");
                }
                else
                {
                    recognized.setText("<html><center><h1>"+result+"</h1></center></html>");
                }
                intermediateImages.removeAll();
                for (BinaryImage intermediateImage : recognizer.getIntermediateImages())
                {
                    intermediateImages.add(new RAWImagePanel(new RAWImage(intermediateImage)));
                }
                content.revalidate();
            }
        });
        buttonContainer.add(recognize);

        recognition.add(recognized, BorderLayout.CENTER);
        recognition.add(buttonContainer, BorderLayout.SOUTH);

        content.add(drawingPane, BorderLayout.CENTER);
        content.add(recognition, BorderLayout.EAST);
        content.add(intermediateImages, BorderLayout.SOUTH);
        setContentPane(content);
    }
}
