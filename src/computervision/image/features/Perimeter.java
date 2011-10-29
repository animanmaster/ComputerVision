/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features;

import computervision.image.BinaryImage;
import computervision.image.Pixel;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO
 * @author Malik Ahmed
 */
public class Perimeter implements Feature<Set<Pixel>>
{

    private class Neighborhood
    {
        private boolean eightNeighborhood;
        private boolean[][] pixels;
        
        private Neighborhood(boolean[][] pixels)
        {
            this(pixels, true);
        }
        
        private Neighborhood(boolean[][] pixels, boolean eightNeighborhood)
        {
            this.pixels = pixels;
            this.eightNeighborhood = eightNeighborhood;
        }
        
        boolean isInNeighborhood(int row, int col)
        {
            return row < pixels.length && col < pixels[row].length && pixels[row][col]; 
        }
        
        BinaryImage getNeighborhood(int row, int col)
        {
            BinaryImage neighborhood = new BinaryImage(null, pixels.length, (pixels.length == 0? 0 : pixels[0].length));
            
            //get 4-neighborhood pixels.
            if (isInNeighborhood(row, col))
            {
                
            }
            return neighborhood;
        }
    }


    private boolean eightNeighborhood;

    public Perimeter(boolean eightNeighborhood)
    {
        //The Eight Neighborhood Perimter definition uses a 4-neighborhood when looking at surrounding pixels.
        this.eightNeighborhood = !eightNeighborhood;
    }

    private boolean isPerimeterPixel(Pixel pixel, boolean[][] pixels)
    {
        int row = pixel.getRow(), col = pixel.getCol();
        int total = 0;

        for (int r = row - 1; r <= row+1; r++)
        {
            if (r > 0 && r < pixels.length)
            {
                for (int c = col - 1; c <= col+1; c++)
                {
                    if ( (c > 0 && c < pixels[r].length) || pixels[r][c] )
                    {
                        total++;
                    }
                    else
                    {
                        //this pixel's at the edge of the array, so it
                        //automatically counts as a perimeter pixel.
                        return true;
                    }
                }
            }
            else
            {
                //this pixel's at the edge of the array, so it
                //automatically counts as a perimeter pixel.
                return true;
            }
        }
        //To be a perimeter pixel that isn't at the edge of the image,
        //the sum of all the pixels around you must be less than the size of the neighborhood you're looking in (+1 for yourself).
        return (eightNeighborhood? total != 9 : total != 5);
    }

    @Override
    public Result<Set<Pixel>> extract(BinaryImage image, Map<String, Object> imageInfo) {
        Set<Pixel> perimeter = new TreeSet<Pixel>();
        if (image != null)
        {
            Pixel[] pixels = image.getPixels();
            boolean[][] pixelValues = image.getPixelValues();
            for (Pixel pixel : pixels)
            {
                if(isPerimeterPixel(pixel, pixelValues))
                    perimeter.add(pixel);
            }
        }
        return new Result(perimeter);
    }

}
