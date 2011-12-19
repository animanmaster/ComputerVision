/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features.segmentation;

import computervision.gui.RAWImageViewer;
import computervision.image.Image;
import computervision.image.Pixel;
import computervision.image.RAWImage;
import java.io.File;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Malik Ahmed
 */
public class KMeansClustering
{
    private LawsTextureEnergyLevels energy = new LawsTextureEnergyLevels();

    private class Cluster
    {
        private int number;
        private double[] means;
        private double[] sums;  //To save us from recalculating means, keep track of the sum of the members.
        
        private Set<Pixel> members = new TreeSet<Pixel>();
        
        public Cluster(int number, double[] initialMeans)
        {
            this.number = number;
            this.means = new double[initialMeans.length];
            //We need to copy the array so we don't accidentally overwrite data.
            System.arraycopy(initialMeans, 0, this.means, 0, this.means.length);
            this.sums = new double[this.means.length];
        }
        
        public void addMember(Pixel pixel, double[] features)
        {
            if (members.add(pixel))
            {
                for (int i = 0; i < sums.length; i++)
                {
                    //Update means
                    sums[i] += features[i];
                    means[i] = sums[i]/members.size();
                }
            }
        }

        public void removeMember(Pixel pixel, double[] features)
        {
            if (members.remove(pixel))
            {
                for (int i = 0; i < sums.length; i++)
                {
                    //Update means
                    sums[i] -= features[i];
                    means[i] = sums[i]/members.size();
                }
            }
        }
    }

    private void initialize(Cluster[] means, double[][][] energyMap)
    {
        Random rand = new Random();
        for (int cluster = 0; cluster < means.length; cluster++)
        {
            //Choose a random pixel location and use the values there as your means for this cluster.
            means[cluster] = new Cluster(cluster, energyMap[rand.nextInt(energyMap.length)][rand.nextInt(energyMap[0].length)]);
        }
    }

    private double distance(double[] feature, double[] mean)
    {
        double d = 0;   //lol DD
        for (int i = 0; i < feature.length; i++)
        {
            d += Math.pow(feature[i] - mean[i], 2);
        }
        return Math.sqrt(d);
    }

    private Cluster closestCluster(double[] features, Cluster[] clusters)
    {
        int closest = 0;
        double minimumDistance = Double.MAX_VALUE;
        double distance;
        for (int cluster = 0; cluster < clusters.length; cluster++)
        {
            distance = distance(features, clusters[cluster].means);
            if (distance < minimumDistance)
            {
                closest = cluster;
                minimumDistance = distance;
            }
        }
        return clusters[closest];
    }

    private void print(Cluster[] clusters)
    {
        int i = 0;
        for (Cluster cluster : clusters)
        {
            System.out.printf("Cluster %d means: \n", i++);
            for(double mean : cluster.means)
            {
                System.out.printf("\t%.5f\n", mean);
            }
        }
    }


    public int[][] performClustering(Image image, int K)
    {
        int rows = image.getSize().height, cols = image.getSize().width;
        //This is the image with each pixel position containing the cluster number.
        int[][] clusterAssignments = new int[rows][cols];
        //The energy map using Laws' Energy Texture Levels.
        double[] [][] energyMap = energy.extract(image);

        //Store the means for the features of all K clusters.
        Cluster[] clusters = new Cluster[K];

        //Randomly choose the initial cluster means.
        initialize(clusters, energyMap);

        boolean meansChanged;
        Cluster cluster, oldCluster;
        Pixel pixel;
        double[] features;
        int ic = 0; //just to keep track of the iteration count.
        do
        {
            meansChanged = false;
            for (int row = 0; row < energyMap.length; row++)
            {
                for (int col = 0; col < energyMap[row].length; col++)
                {
                    pixel = new Pixel(row, col, 0);
                    oldCluster = clusters[clusterAssignments[row][col]];
                    features = energyMap[row][col];

                    //Assign each pixel to the closest cluster.
                    cluster = closestCluster(features, clusters);

                    //Update means based on the members of the cluster.
                    oldCluster.removeMember(pixel, features);
                    cluster.addMember(pixel, features);
                    clusterAssignments[row][col] = cluster.number;

                    //Repeat until the means don't change.
                    meansChanged = meansChanged || (oldCluster != cluster);

                }
            }
            ic++;
//            System.out.println(ic);
//            print(clusters);
        }
        while (meansChanged);
        System.out.printf("Iteration Count: %d\n", ic);
        return clusterAssignments;
    }



    public static void main(String[] args) throws Exception
    {
        RAWImage image = new RAWImage(new File("zebras.raw"), 256, 256);
        new RAWImageViewer(image).setVisible(true);
        KMeansClustering kmeans = new KMeansClustering();

        for (int k = 4; k <= 6; k++) {
        int[][] result = kmeans.performClustering(image, k);
        for (int i = 0; i < result.length; i++)
        {
            for (int j = 0; j < result[i].length; j++)
            {
//                System.out.printf("%d\t", result[i][j]);
                image.getPixels()[i][j] = result[i][j] * 255/k;
            }
//            System.out.println();
        }
        new RAWImageViewer(image).setVisible(true);
        }
    }

}
