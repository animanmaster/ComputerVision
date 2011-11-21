/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision;

import computervision.image.RAWImage;
import java.io.File;
import java.io.FilenameFilter;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Malik Ahmed
 */
public class ConvertDirToPng
{
    public static void main(String[] args) throws Exception
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("RAW Images", "raw", "RAW"));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            File file = chooser.getSelectedFile();
            if (file.isFile())
                file = file.getParentFile();
            File[] raws = file.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith("raw");
                }
            });
            RAWImage raw;
            for (File rawFile : raws)
            {
                raw = new RAWImage(rawFile, 128, 128);
                file = new File(rawFile.getAbsolutePath() + ".png");
                ImageIO.write(raw.toBufferedImage(), "png", file);
                System.out.println("Wrote " + file);
            }
            JOptionPane.showMessageDialog(null, "Finished converting images!");
        }
    }
}
