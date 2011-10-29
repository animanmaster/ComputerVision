package computervision.image.operations;

import computervision.image.BinaryImage;

/**
 * Encapsulate a morphological operations that operates on two BinaryImages.
 * The first is the input image, and the second is normally the structuring element.
 * 
 * @author Malik Ahmed
 */
public interface MorphologicalOperator
{
    //TODO This is gross. Either think of a better name for the "inline apply" or use another interface.
    //Also TODO - StructuringElement should probably be its own construct;
    //              after all, it uses offset pixels instead of normal pixels.

    /**
     * Apply the operator without modifying the original.
     *
     * Note that this will create a new image, so chaining operations like this
     * may not be good idea on memory-tight systems.
     *
     * @param image                 the input image.
     * @param structuringElement    the element to use as a structuring element.
     * @return the result of the operation (a new image)
     */
    BinaryImage apply(BinaryImage image, BinaryImage structuringElement);

    /**
     * Apply the operation on the input image.
     *
     * This method is more suited to chaining since it operates on the input image
     * and returns the altered image. Note that your original input image will be altered.
     *
     * @param image                 the input image.
     * @param structuringElement    the element to use as a structuring element.
     * @return  the result of the operation (performed on the input image reference)
     */
    BinaryImage iapply(BinaryImage image, BinaryImage structuringElement);
}
