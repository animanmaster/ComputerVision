/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition;

import computervision.image.BinaryImage;
import computervision.image.features.Feature;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Malik Ahmed
 */
public abstract class RecognitionEngine
{
    private List<Feature> featuresToCollect;
    private Recognizer recognizer;

    public RecognitionEngine()
    {
        this.featuresToCollect = new LinkedList<Feature>();
    }

    public RecognitionEngine(Feature[] features)
    {
        this(features, null);
    }
    
    public RecognitionEngine(Recognizer recognizer)
    {
        this(null, recognizer);
    }
    
    public RecognitionEngine(Feature[] features, Recognizer recognizer)
    {
        this();
        addFeatures(features);
        setRecognizer(recognizer);
        
    }

    public void addFeatures(Feature[] features)
    {
        if (features != null)
        {
            for (Feature feature : features)
            {
                if (feature != null)
                    addFeature(feature);
            }
        }
    }

    public void setRecognizer(Recognizer recognizer)
    {
        this.recognizer = recognizer;
    }

    public void addFeature(Feature feature)
    {
        this.featuresToCollect.add(feature);
    }

    public Object performRecognition(BinaryImage image)
    {
        return performRecognition(image, this.recognizer);
    }

    public <T> T performRecognition(BinaryImage image, Recognizer<T> recognizer)
    {
        return null;
    }
}
