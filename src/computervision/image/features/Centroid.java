/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features;

import computervision.image.BinaryImage;
import computervision.image.Pixel;
import computervision.image.features.Centroid.Position;
import java.util.Map;

/**
 *
 * @author Malik Ahmed
 */
public class Centroid implements Feature<Position>
{
    public static class Position implements Comparable<Position>
    {
        public final int row, col;
        private Position(int row, int col)
        {
            this.row = row;
            this.col = col;
        }

        public int compareTo(Position o)
        {
            int result = -1;
            if (o != null)
            {
                result = this.row - o.row;
                if (result == 0)    //Same row
                    result = this.col - o.col;
            }
            return result;
        }

    }

    @Override
    public Result<Position> extract(BinaryImage image, Map<String, Object> imageInfo)
    {
        int area = 0;
        int r_bar = 0, c_bar = 0;
        Pixel[] pixels = image.getPixels();

        for (Pixel pixel : pixels)
        {
            area++;
            r_bar += pixel.getRow();
            c_bar += pixel.getCol();
        }
        r_bar /= area;
        c_bar /= area;

        return new ComparableResult<Position>(new Position(r_bar, c_bar));
    }

}
