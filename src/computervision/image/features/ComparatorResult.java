/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features;

import java.util.Comparator;

/**
 *
 * @author Malik Ahmed
 */
public class ComparatorResult<T> extends Result<T> implements Comparable<ComparatorResult<T>>
{
    private Comparator<T> comparator;

    private ComparatorResult()
    {
        super(null);
    }

    public ComparatorResult(T result, Comparator<T> comparator)
    {
        super(result);
        if (comparator == null)
            throw new IllegalArgumentException("Comparator cannot be null!");
        this.comparator = comparator;
    }

    @Override
    public int compareTo(ComparatorResult<T> o)
    {
        return comparator.compare(getValue(), (o == null? null : o.getValue()));
    }

}
