/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features;

/**
 *
 * @author Malik Ahmed
 */
public class ComparableResult<T extends Comparable<T>> extends Result<T>
                                    implements Comparable<ComparableResult<T>>
{
    private ComparableResult()
    {
        super(null);
    }

    public ComparableResult(T result)
    {
        super(result);
    }

    @Override
    public int compareTo(ComparableResult<T> o)
    {
        int comparison = -1;
        if (o != null && o.getValue() != null && o.getValue() instanceof Comparable)
        {
            comparison = this.getValue().compareTo(o.getValue());
        }
        return comparison;
    }

}
