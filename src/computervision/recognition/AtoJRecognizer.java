/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition;

import computervision.image.BinaryImage;
import computervision.image.features.BoundingBox;
import computervision.recognition.neuralnet.NeuralNetwork;
import java.awt.Rectangle;

/**
 *
 * @author Malik Ahmed
 */
public class AtoJRecognizer implements Recognizer<Character>
{
    private NeuralNetwork neuralNetwork;
    private BoundingBox boundingBox = new BoundingBox();
    private byte[][] mappedImage = new byte[7][7];

//    private BinaryImage preprocess(BinaryImage image)
//    {
//        Rectangle box = boundingBox.extract(image, null).getValue();
//    }

    public Character recognize(BinaryImage image)
    {
//        image = preprocess(image);
        Rectangle box = boundingBox.extract(image, null).getValue();
        Character recognized = null;
        
        return recognized;
    }

}
