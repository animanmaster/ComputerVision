/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.gui.components;

import computervision.image.RAWImage;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Malik Ahmed
 */
public class RAWImagePanel extends JPanel
{
    private JLabel label;
    private ImageIcon icon;
    private JFileChooser fileChooser;

    RAWImage image;
    BufferedImage renderedImage;

    public RAWImagePanel()
    {
        super(new FlowLayout(FlowLayout.LEADING));
        this.icon = new ImageIcon();
        this.label = new JLabel(icon);
        this.add(label);
        
        this.image = null;
        this.renderedImage = null;
        this.fileChooser = new JFileChooser();
        
        addEventListeners();
    }

    public RAWImagePanel(RAWImage image)
    {
        this();
        setImage(image);
    }

    private void addEventListeners()
    {
        label.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JOptionPane.showMessageDialog(RAWImagePanel.this, "label clicked");
            }
        });
    }

    public void setImage(RAWImage image)
    {
        this.image = image;
        this.renderedImage = image.toBufferedImage();
        icon.setImage(image.toBufferedImage());
        revalidate();
        repaint();
    }

    public RAWImage getRAWImage()
    {
        return this.image;
    }

    public BufferedImage getRenderedImage()
    {
        return renderedImage;
    }

    public static void main(String[] args) throws Exception
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("RAW File", "raw", "RAW"));
        JFrame frame = new JFrame();
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
        {
            RAWImage image = new RAWImage(chooser.getSelectedFile(), 140, 140);
            frame.setContentPane(new RAWImagePanel(image));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(140, 140);
            frame.setVisible(true);
        }
    }

}
