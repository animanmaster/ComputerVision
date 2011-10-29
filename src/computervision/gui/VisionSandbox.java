/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.gui;

import computervision.image.BinaryImage;
import computervision.image.Pixel;
import computervision.image.RAWImage;
import computervision.image.features.BoundingBox;
import computervision.image.features.Centroid;
import computervision.image.features.Circularity;
import computervision.image.features.components.ComponentLabels;
import computervision.image.features.LakesBaysAndLids;
import computervision.image.features.Moment;
import computervision.image.operations.Closing;
import computervision.image.operations.CombinedOperation;
import computervision.image.operations.Dilation;
import computervision.image.operations.Erosion;
import computervision.image.operations.Opening;
import computervision.image.operations.RepeatedOp;
import computervision.image.operations.SetDifference;
import computervision.image.operations.SetUnion;
import computervision.image.transforms.Skeleton;
import computervision.recognition.DigitRecognizer;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * As a lazy way to prevent duplicating code, this class extends RAWImageViewer
 * so that it does all the extra work and then this class can force
 * the contentPane into another panel.
 * @author Malik Ahmed
 */
public class VisionSandbox extends RAWImageViewer
{
    private Container imageView;
    private JPanel panel;

    public VisionSandbox()
    {
        super();
        buildGui();
    }

    private void buildGui()
    {
        setTitle("Vision Sandbox");
        imageView = getContentPane();
        panel = new JPanel(new BorderLayout(5, 5));
        panel.add(imageView, BorderLayout.CENTER);
        panel.add(createActionBar(), BorderLayout.EAST);
        setContentPane(panel);
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private Container createActionBar()
    {
        JPanel container = new JPanel(new BorderLayout(0, 1));
        JPanel panel = new JPanel(new GridLayout(0,2, 3,3));
        final BinaryImage unitDiskA = new BinaryImage(Arrays.asList(
                new Pixel(-1, 0, 1),
                new Pixel(0, -1, 1), new Pixel(0, 0, 1), new Pixel(0, 1, 1),
                new Pixel(1, 0, 1)),
                3, 3);
        final BinaryImage unitDiskB = unitDiskA.clone();
        unitDiskB.addPixels(new Pixel(-1, -1, 1), new Pixel(-1, 1, 1),
                            new Pixel(1, -1, 1), new Pixel(1, 1, 1));

        final Dilation dilation = new Dilation();
        final Erosion erosion = new Erosion();
        final Opening opening = new Opening();
        final Closing closing = new Closing();
        final SetUnion union = new SetUnion();
        final SetDifference difference = new SetDifference();

        //TODO To be more memory efficient, replace these ActionListeners to one listener.

        panel.add(new JLabel("<html><table width='100%' height='100%'><th colspan=2>Unit Disk A</th>"
                + "<tr><td></td><td>+</td><td></td></tr>"
                + "<tr><td>+</td><td>+</td><td>+</td></tr>"
                + "<tr><td></td><td>+</td><td></td></tr></table></html>"));
        panel.add(new JLabel("<html><table width='100%' height='100%'><th colspan=2>Unit Disk B</th>"
                + "<tr><td>+</td><td>+</td><td>+</td></tr>"
                + "<tr><td>+</td><td>+</td><td>+</td></tr>"
                + "<tr><td>+</td><td>+</td><td>+</td></tr></table></html>"));

        JButton button = new JButton("Dilate (A)");
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                displayImage(new RAWImage(dilation.iapply(image.toBinaryImage(), unitDiskA)));
            }
        });
        panel.add(button);
        button = new JButton("Dilate (B)");
        button.addActionListener(new ActionListener() {   
            public void actionPerformed(ActionEvent e) {
                displayImage(new RAWImage(dilation.iapply(image.toBinaryImage(), unitDiskB)));
            }
        });
        panel.add(button);
        
        button = new JButton("Erode (A)");
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                displayImage(new RAWImage(erosion.iapply(image.toBinaryImage(), unitDiskA)));
            }
        });
        panel.add(button);
        button = new JButton("Erode (B)");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayImage(new RAWImage(erosion.iapply(image.toBinaryImage(), unitDiskB)));
            }
        });
        panel.add(button);
        
        
        button = new JButton("Open (A)");
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                displayImage(new RAWImage(opening.iapply(image.toBinaryImage(), unitDiskA)));
            }
        });
        panel.add(button);
        button = new JButton("Open (B)");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayImage(new RAWImage(opening.iapply(image.toBinaryImage(), unitDiskB)));
            }
        });
        panel.add(button);
        
        
        button = new JButton("Close (A)");
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                displayImage(new RAWImage(closing.iapply(image.toBinaryImage(), unitDiskA)));
            }
        });
        panel.add(button);
        button = new JButton("Close (B)");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayImage(new RAWImage(closing.iapply(image.toBinaryImage(), unitDiskB)));
            }
        });
        panel.add(button);
        
        
        button = new JButton("Union");
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) 
            {
                RAWImage opened = openFile();
                if (opened != null)
                {
                    displayImage(new RAWImage(union.iapply(image.toBinaryImage(), opened.toBinaryImage())));
                }
            }
        });
        panel.add(button);

        button = new JButton("Difference");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) 
            {
                RAWImage opened = openFile();
                if (opened != null)
                {
                    displayImage(new RAWImage(difference.iapply(image.toBinaryImage(), opened.toBinaryImage())));
                }
            }
        });
        panel.add(button);


        button = new JButton("Show Bounding Box");
        ActionListener boundBoxListener = new ActionListener()
        {
            BoundingBox boundingBox = new BoundingBox();
            BoundingBox.Box box = null;

            void drawBox(int[][] pixels, int color)
            {
                int x = box.getBox().x, y = box.getBox().y, maxX = x + box.getBox().width, maxY = y + box.getBox().height;
                for (int col = x; col < maxX; col++)
                {
                    //Draw the top and bottom lids
                    pixels[y][col] = color;
                    pixels[maxY - 1][col] = color;
                }
                for (int row = y; row < maxY; row++)
                {
                    //Draw the sides
                    pixels[row][x] = color;
                    pixels[row][maxX - 1] = color;
                }
            }

            public void actionPerformed(ActionEvent e)
            {
                //EW HACKJOBS
                if (((JButton)e.getSource()).getText().equals("Show Bounding Box"))
                {
                    box = (BoundingBox.Box) boundingBox.extract(image.toBinaryImage(), null);
                    drawBox(image.getPixels(), RAWImage.BLACK);
                }
                else
                {
                    if (box != null)
                    {
                        drawBox(image.getPixels(), RAWImage.WHITE);
                        box = null;
                    }
                }
                displayImage(image);
            }
        };
        button.addActionListener(boundBoxListener);
        panel.add(button);
        button = new JButton("Hide Bounding Box");
        button.addActionListener(boundBoxListener);
        panel.add(button);


        button = new JButton("Show Centroid");
        ActionListener centroidListener = new ActionListener()
        {
            Centroid centroid = new Centroid();
            Centroid.Position point = null;
            int oldColor = 0;

            void drawPoint(int[][] pixels, int color)
            {
                oldColor = pixels[point.row][point.col];
                pixels[point.row][point.col] = color;
            }

            public void actionPerformed(ActionEvent e)
            {
                //EW HACKJOBS
                if (((JButton)e.getSource()).getText().equals("Show Centroid"))
                {
                    point = (Centroid.Position) centroid.extract(image.toBinaryImage(), null).getValue();
                    drawPoint(image.getPixels(), RAWImage.GRAY);
                }
                else
                {
                    if (point != null)
                    {
                        drawPoint(image.getPixels(), oldColor);
                        point = null;
                    }
                }
                displayImage(image);
            }
        };
        button.addActionListener(centroidListener);
        panel.add(button);
        button = new JButton("Hide Centroid");
        button.addActionListener(centroidListener);
        panel.add(button);

        button = new JButton("Close Da Hole");
        button.addActionListener(new ActionListener()
        {
            private BoundingBox boundingBox = new BoundingBox();

            public void actionPerformed(ActionEvent e)
            {
                BoundingBox.Box box = (BoundingBox.Box)boundingBox.extract(image.toBinaryImage(), null);
                int n = Math.min(box.getBox().width, box.getBox().height)/2;  //Bigger of the two.
                System.out.println(n);
                CombinedOperation nClose = new CombinedOperation(new RepeatedOp(dilation, n), new RepeatedOp(erosion, n+1));
                displayImage(new RAWImage(nClose.iapply(image.toBinaryImage(), null)));
            }
        });
        panel.add(button);

        button = new JButton("Number of Lakes");
        button.addActionListener(new ActionListener()
        {
            private LakesBaysAndLids lakes = new LakesBaysAndLids();

            public void actionPerformed(ActionEvent e)
            {
                BinaryImage binaryImage = new CombinedOperation(dilation, closing, opening).apply(image.toBinaryImage(), unitDiskA);
                int numLakes = lakes.extract(binaryImage, new HashMap<String, Object>()).getValue().getNumberOfLakes();
                JOptionPane.showMessageDialog(VisionSandbox.this, numLakes + " lakes.");
            }
        });
        panel.add(button);

        button = new JButton("Number of Components");
        button.addActionListener(new ActionListener()
        {
            private ComponentLabels labels = new ComponentLabels();

            public void actionPerformed(ActionEvent e)
            {
                ComponentLabels.Components result = (ComponentLabels.Components)labels.extract(image.toBinaryImage(), new HashMap<String, Object>());
                int numComponents = result.getComponents().length;
                JOptionPane.showMessageDialog(VisionSandbox.this, numComponents + " components.");
            }
        });
        panel.add(button);

        button = new JButton("Second Order Moments");
        button.addActionListener(new ActionListener()
        {
            private Moment moments = new Moment();

            public void actionPerformed(ActionEvent e)
            {
                Moment.MomentResult result = (Moment.MomentResult)moments.extract(image.toBinaryImage(), new HashMap<String, Object>());
                JOptionPane.showMessageDialog(VisionSandbox.this,
                        "<html><table>"
                        + "<tr><td>Second Order Row Moment:</td><td>" + result.getRowMoment() + "</td></tr>"
                        + "<tr><td>Second Order Column Moment:</td><td>"+result.getColumnMoment()+"</td></tr>"
                        + "<tr><td>Second Order Mixed Moment:</td><td>"+result.getMixedMoment()+"</td></tr>"
                        + "</table></html>");
            }
        });
        panel.add(button);

        button = new JButton("Y'all Best Recognize");
        button.addActionListener(new ActionListener()
        {
            private DigitRecognizer digitRecognizer = new DigitRecognizer();

            public void actionPerformed(ActionEvent e)
            {
                Integer recognized = digitRecognizer.recognize(image.toBinaryImage());
                JOptionPane.showMessageDialog(VisionSandbox.this, recognized);
            }
        });
        panel.add(button);

        button = new JButton("Circularity");
        button.addActionListener(new ActionListener()
        {
            private Circularity circularity = new Circularity();

            public void actionPerformed(ActionEvent e)
            {
                Double rotundness = circularity.extract(image.toBinaryImage(), null).getValue();
                JOptionPane.showMessageDialog(VisionSandbox.this, rotundness);
            }
        });
        panel.add(button);


        container.add(panel, BorderLayout.CENTER);
        panel = new JPanel(new GridLayout(0,1));
        button = new JButton("Skeleton");
        button.addActionListener(new ActionListener() {

            Skeleton skeleton = new Skeleton();
            public void actionPerformed(ActionEvent e) {
                displayImage(new RAWImage(skeleton.apply(image.toBinaryImage())));
            }
        });
        panel.add(button);
        button = new JButton("Clear");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //Is it good or bad that I can cheat like this? :P
                displayImage(new RAWImage(new BinaryImage(null, image.getRows(), image.getColumns())));
            }
        });
        panel.add(button);
        button = new JButton("Draw");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) 
            {
                final BinaryDraw drawer = new BinaryDraw(image);
                drawer.addWindowListener(new WindowAdapter()
                {
                    @Override
                    public void windowClosing(WindowEvent e)
                    {
                        if (JOptionPane.showConfirmDialog(drawer,
                                "Would you like to use this image in the sandbox?",
                                "Can haz image?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                        {
                            displayImage(drawer.getRAW());
                        }
                    }
                });
                drawer.setVisible(true);
            }
        });
        panel.add(button);
        container.add(panel, BorderLayout.SOUTH);
        return container;
    }

    public static void main(String[] args)
    {
        new VisionSandbox().setVisible(true);
    }
}
