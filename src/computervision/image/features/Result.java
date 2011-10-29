/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features;

import computervision.image.BinaryImage;

/**
 *
 * @author Malik Ahmed
 */
public class Result<T>
{
    private T value;
    private BinaryImage image;

    private Result()
    {
        this.value = null;
        this.image = null;
    }
    
    public Result(T value)
    {
        this(value, null);
    }

    public Result(T value, BinaryImage image)
    {
        this();
        this.value = value;
        this.image = image;
    }

    public T getValue()
    {
        return this.value;
    }

    public BinaryImage getImage()
    {
        return this.image;
    }
}
