/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Malik Ahmed
 */
public class RAWImage extends Image
{
    public static final byte BLACK = (byte)0,
                            GRAY = (byte) 128,
                            WHITE = (byte)255;
    protected File src;

    public RAWImage(File file, int rows, int cols) throws IOException
    {
        super(rows, cols);
        this.src = file;
        if (file != null)
            read(new FileInputStream(file));
    }
    
    public RAWImage(int rows, int cols)
    {
        super(rows, cols);
        this.src = null;
    }

    public RAWImage(BinaryImage binaryImage)
    {
        this(binaryImage.getNumberOfRows(), binaryImage.getNumberOfColumns());
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                //Whitify all the pixels!
                this.pixels[i][j] = WHITE;
            }
        }
        for (Pixel pixel : binaryImage.getPixels())
        {
            if (pixel.getRow() >= 0 && pixel.getCol() >= 0
                    && pixel.getRow() < rows && pixel.getCol() < cols)
                this.pixels[pixel.getRow()][pixel.getCol()] = BLACK;
        }
    }

    public RAWImage(BufferedImage bufferedImage)
    {
        this(bufferedImage.getHeight(), bufferedImage.getWidth());
        for (int y = 0; y < rows; y++)
        {
            for (int x = 0; x < cols; x++)
            {
                this.pixels[y][x] = (bufferedImage.getRGB(x, y) == Color.BLACK.getRGB()? BLACK : WHITE);
            }
        }
    }

    public int getRows()
    {
        return cols;
    }

    public int getColumns()
    {
        return rows;
    }

    public File getSourceFile()
    {
        return src;
    }

    public BinaryImage toBinaryImage()
    {
        /*BinaryImage image = new BinaryImage(this.pixels.length, this.pixels[0].length);
        boolean[][] bits = image.getPixels();
        for (int i = 0; i < bits.length; i++)
        {
            for (int j = 0; j < bits[i].length; j++)
            {
                //0 in RAW means black, which we represent as 1 (or true) in
                //a BinaryImage.
                bits[i][j] = pixels[i][j] == 0;
            }
        }
        return image;*/
        return new BinaryImage(this);
    }

    @Override
    public BufferedImage toBufferedImage()
    {
        //Rows <=> Height/Y-axis, Columns <=> Width/X-axis
        BufferedImage image = new BufferedImage(getColumns(), getRows(), BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < this.pixels.length; y++)
        {
            for (int x = 0; x < this.pixels[y].length; x++)
            {
                image.setRGB(x, y, this.pixels[y][x]);
            }
        }
        return image;
    }

    private byte[] bytes(int[] row)
    {
        byte[] bytes = new byte[row.length];
        for (int i = 0; i < row.length; i++)
            bytes[i] = (byte)row[i];
        return bytes;
    }

    @Override
    public void write(OutputStream out) throws IOException
    {
        for (int[] row : this.pixels)
            out.write(bytes(row));
        out.flush();
    }

    @Override
    void read(InputStream in) throws IOException
    {
        int bytesRead;
        //RAW image pixels use one byte each.
        byte[] buffer = new byte[rows];
        for (int[] row : pixels)
        {
            bytesRead = in.read(buffer);
            if (bytesRead < 0)
            {
                //EOF
                break;
            }
            else
            {
                for (int i = 0; i < buffer.length; i++)
                {
                    row[i] = buffer[i];
                }
            }
        }
        in.close();
    }
}
