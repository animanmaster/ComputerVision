/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.gui;

import computervision.image.RAWImage;
import computervision.image.features.segmentation.KMeansClustering;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Texture Segmentation
 * @author Malik Ahmed
 */
public class Homework6 extends RAWImageViewer
{
    private static int[] CLUSTER_COLORS = {Color.BLACK.getRGB(), Color.WHITE.getRGB(), Color.RED.getRGB(), Color.BLUE.getRGB(), Color.YELLOW.getRGB(), Color.GREEN.getRGB()};

    private KMeansClustering segmentator;
    private ExecutorService executor;

    private class ClusterJob implements Callable<int[][]>
    {
        int K;
        ClusterJob(int K)
        {
            this.K = K;
        }

        public int[][] call() throws Exception {
            return segmentator.performClustering(image, K);
        }
    };

    public Homework6()
    {
        segmentator = new KMeansClustering();
        executor = Executors.newCachedThreadPool();
        buildGui();
    }

    private void buildGui()
    {
        setTitle("Homework 6 - Texture Segmentation Using K-Means Clustering");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private int grayify(int color)
    {
        return color << 16 | color << 8 | color;
    }

    private BufferedImage buildImage(int[][] result, boolean gray)
    {
        BufferedImage raw = new BufferedImage(result.length, result[0].length, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < result.length; i++)
            for (int j = 0; j < result.length; j++)
                raw.setRGB(j, i, (gray? grayify(result[i][j] * 256/CLUSTER_COLORS.length) : CLUSTER_COLORS[result[i][j]]));
        return raw;
    }

    @Override
    public void displayImage(RAWImage image) {
        super.displayImage(image);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //New thread to prevent an unresponsive gui.
        new Thread(new Runnable()
        {
            int[][] result;
            int k;
            public void run()
            {
                try
                {
                    Future<int[][]>[] futures = new Future[3];
                    for (k = 4; k <= 6; k++)
                    {
                        //Work in the background on these ClusterJobs.
                        futures[k - 4] = executor.submit(new ClusterJob(k));
                    }
                    k = 4;
                    for (Future<int[][]> future : futures)
                    {
                        result = future.get();
                        //Update the gui
                        SwingUtilities.invokeAndWait(new Runnable()
                        {

                            public void run()
                            {
                                panel.add(new JLabel("K = " + k++, new ImageIcon(buildImage(result, false)), SwingConstants.CENTER));
                            }
                        });
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    setCursor(null);
                    panel.revalidate();
                }
            }
        }).start();
    }

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {
            //wutevs
        }
        new Homework6().setVisible(true);
    }
}
