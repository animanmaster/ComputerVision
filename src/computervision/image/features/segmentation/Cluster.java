/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features.segmentation;

import computervision.image.Pixel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Malik Ahmed
 */
public class Cluster
{
    private double mean = 0, sum = 0;
    
    private List<Pixel> members = new ArrayList<Pixel>();

    public double getMean()
    {
        return mean;
    }

    public void addMember(Pixel pixel, double value)
    {
        
    }
}
