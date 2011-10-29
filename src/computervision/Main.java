/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision;

import computervision.gui.DigitRecognization;
import computervision.gui.VisionSandbox;
import javax.swing.UIManager;

/**
 *
 * @author Malik Ahmed
 */
public class Main
{
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            //Meh.
            e.printStackTrace();
        }
//        new VisionSandbox().setVisible(true);
        new DigitRecognization().setVisible(true);
    }
}
