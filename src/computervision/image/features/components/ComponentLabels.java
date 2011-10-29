/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features.components;

import computervision.image.BinaryImage;
import computervision.image.BinaryPixel;
import computervision.image.Pixel;
import computervision.image.features.Feature;
import computervision.image.features.Result;
import computervision.image.features.components.ComponentLabels.Components;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Malik Ahmed
 */
public class ComponentLabels implements Feature<Component[]>
{
    public static final String COMPONENTS_KEY = "Components";

    private class GrowingList extends ArrayList<Integer>
    {
        private void growToAccomodate(int index)
        {
            while (size() <= index)
            {
                add(0);
            }
        }

        @Override
        public Integer get(int index)
        {
            growToAccomodate(index);
            return super.get(index);
        }

        @Override
        public Integer set(int index, Integer element)
        {
            growToAccomodate(index);
            return super.set(index, element);
        }

    }

    public class Components extends Result<Component[]>
    {
        private Map<String, Component> components;
        private BinaryImage originalImage;

        Components(BinaryImage originalImage)
        {
            super(null);
            this.components = new LinkedHashMap<String, Component>();
            this.originalImage = originalImage;
        }

        public Component[] getComponents()
        {
            return this.components.values().toArray(new Component[this.components.size()]);
        }

        void addToComponent(String label, Pixel pixel)
        {
            Component component = components.get(label);
            if (component == null)
            {
                component = new Component(label, originalImage);
                components.put(label, component);
            }
            component.addPixel(pixel);
        }

        @Override
        public Component[] getValue()
        {
            return getComponents();
        }
    }

    private int find(int x, GrowingList parent)
    {
        int j = x;
        while (parent.get(j) != 0)
        {
            j = parent.get(j);
        }
        return j;
    }

    private void union(int x, int y, GrowingList parent)
    {
        int j = find(x, parent);
        int k = find(y, parent);
        if (j != k)
            parent.set(k, j);
    }

    private Set<Integer> prior_neighbors(int row, int col, int[][] labelledPixels)
    {
        Set<Integer> labels = new TreeSet<Integer>();
        if (row > 0)
        {
            if (col > 0 && labelledPixels[row - 1][col - 1] > 0)
            {
                labels.add(labelledPixels[row - 1][col - 1]);
            }
            if (labelledPixels[row - 1][col] > 0)
            {
                labels.add(labelledPixels[row - 1][col]);
            }
            if (col + 1 < labelledPixels[row - 1].length && labelledPixels[row - 1][col + 1] > 0)
            {
                labels.add(labelledPixels[row - 1][col + 1]);
            }
        }
        if (col > 0 && labelledPixels[row][col - 1] > 0)
        {
            labels.add(labelledPixels[row][col - 1]);
        }
        return labels;
    }

    @Override
    public Result<Component[]> extract(BinaryImage image, Map<String, Object> imageInfo)
    {
        boolean[][] imagePixels = image.getPixelValues();
        int[][] pixelLabels = new int[image.getNumberOfRows()][image.getNumberOfColumns()];
        Set<Integer> prior;
        int label = 1;
        int M;
        GrowingList parent = new GrowingList();
        for (int row = 0; row < image.getNumberOfRows(); row++)
        {
            for (int col = 0; col < image.getNumberOfColumns(); col++)
            {
                if (imagePixels[row][col])
                {
                    prior = prior_neighbors(row, col, pixelLabels);
                    if (prior.isEmpty())
                    {
                        M = label;
                        label++;
                    }
                    else
                    {
                        M = (Integer)prior.toArray()[0];
                    }
                    pixelLabels[row][col] = M;
                    for (int x : prior)
                    {
                        if (x != M)
                            union(M, x, parent);
                    }
                }
            }
        }

        Components result = new Components(image);

        for (int row = 0; row < pixelLabels.length; row++)
        {
            for (int col = 0; col < pixelLabels[row].length; col++)
            {
                if (imagePixels[row][col])
                {
                    pixelLabels[row][col] = find(pixelLabels[row][col], parent);
                    result.addToComponent(Integer.toString(pixelLabels[row][col]), new BinaryPixel(row, col));

                }
            }
        }
        if (imageInfo != null)
            imageInfo.put(COMPONENTS_KEY, result);
        return result;
    }
    
}
