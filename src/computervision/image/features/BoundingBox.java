/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features;

import computervision.image.BinaryImage;
import computervision.image.Pixel;
import java.awt.Rectangle;
import java.util.Map;

/**
 * Detect
 * @author Malik Ahmed
 */
public class BoundingBox implements Feature<Rectangle>
{
    public static final byte NONE = 0,
                             ABOVE = 1,
                             BELOW = (1 << 1),
                             LEFT  = (1 << 2),
                             RIGHT = (1 << 3);

    public static class Box extends Result<Rectangle> implements Comparable<Box>
    {
        private Rectangle box;
        private int centerX, centerY;

        private Box(int minRow, int minCol, int maxRow, int maxCol)
        {
            //Why +1 for the width and height? Take the case where min{Row,Col} = max{Row,Col}
            //Then we have a box of width/height 1, not 0!
            super(new Rectangle(minCol, minRow, maxCol - minCol + 1, maxRow - minRow + 1));
            this.box = getValue();
            this.centerY = minRow + (maxRow - minRow)/2;
            this.centerX = minCol + (maxCol - minCol)/2;
        }

        public Rectangle getBox()
        {
            return box;
        }

        public int getCenterRow()
        {
            return getCenterY();
        }
        
        public int getCenterY()
        {
            return centerY;
        }

        public int getCenterColumn()
        {
            return getCenterX();
        }

        public int getCenterX()
        {
            return centerX;
        }

        public byte getSpatialRelationship(Box to)
        {
            byte relationship = NONE;
            if (box.x + box.width <= to.box.x)
            {
                //if the right edge of this box is to the left of the other box,
                //then this box is to the left of the other box.
                relationship |= LEFT;
            }
            if (box.x >= to.box.x + to.box.width)
            {
                //if the left edge of this box is to the right of the other box,
                //then this box is to the right of the other box.
                relationship |= RIGHT;
            }
            if (box.y + box.height <= to.box.y)
            {
                //if the bottom edge of this box is above the top edge of the other box,
                //then this box is above the other box.
                relationship |= ABOVE;
            }
            if (box.y >= to.box.y + to.box.height)
            {
                //if the top edge of this box is below the bottom edge of the other box,
                //then this box is below the other box.
                relationship |= BELOW;
            }
            return relationship;
        }

        @Override
        public String toString()
        {
            return this.box.toString();
        }

        @Override
        public int compareTo(Box o)
        {
            int result;
            if (o != null && o.box != null)
            {
                result = box.y - o.box.y; //This way, if this box is closer to the top, result will be negative.
                if (result == 0)
                {
                    //The two boxes are on the same row,
                    //so see if this box comes before or after the second box (from left-to-right).
                    result = box.x - o.box.x;
                }
            }
            else
            {
                //This will cause nulls to be pushed to the end if we're sorting in ascending order.
                result = -1;
            }
            return result;
        }
    }

    @Override
    public Result<Rectangle> extract(BinaryImage image, Map<String, Object> imageInfo)
    {
        int minRow, maxRow, minCol, maxCol;
        int row, col;
        minRow = maxRow = minCol = maxCol = 0;
        Pixel[] pixels = image.getPixels();
        if (pixels.length > 0)
        {
            minRow = maxRow = pixels[0].getRow();
            minCol = maxCol = pixels[0].getCol();
            for (Pixel pixel : pixels)
            {
                row = pixel.getRow();
                col = pixel.getCol();
                if (row < minRow)
                    minRow = row;
                else if (row > maxRow)
                    maxRow = row;

                if (col < minCol)
                    minCol = col;
                else if (col > maxCol)
                    maxCol = col;
            }
        }
        return new Box(minRow, minCol, maxRow, maxCol);
    }

}
