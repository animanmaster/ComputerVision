/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.image.features;

import computervision.image.features.components.ComponentLabels;
import computervision.image.BinaryImage;
import computervision.image.features.LakesBaysAndLids.WaterContainer;
import computervision.image.features.components.Component;
import computervision.image.operations.CombinedOperation;
import computervision.image.operations.Dilation;
import computervision.image.operations.Erosion;
import computervision.image.operations.RepeatedOp;
import computervision.image.operations.SetDifference;
import computervision.image.operations.UnitDisk;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Malik Ahmed
 */
public class LakesBaysAndLids implements Feature<WaterContainer>
{
    public static class WaterContainer
    {
        private List<Component> lakes, bays, lids;

        WaterContainer()
        {
            this.lakes = new ArrayList<Component>();
            this.bays  = new ArrayList<Component>();
            this.lids  = new ArrayList<Component>();
        }

        private void addComponent(Component component, List<Component> toList)
        {
            toList.add(component);
        }

        void addLake(Component lake)
        {
            addComponent(lake, lakes);
        }

        void addBay(Component bay)
        {
            addComponent(bay, bays);
        }

        void addLid(Component lid)
        {
            addComponent(lid, lids);
        }

        public int getNumberOfLakes()
        {
            return lakes.size();
        }

        public int getNumberOfBays()
        {
            return bays.size();
        }

        public int getNumberOfLids()
        {
            return lids.size();
        }

        public Component[] getLakes()
        {
            return lakes.toArray(new Component[lakes.size()]);
        }

        public Component[] getBays()
        {
            return bays.toArray(new Component[bays.size()]);
        }

        public Component[] getLids()
        {
            return lids.toArray(new Component[lids.size()]);
        }
    }


    private BoundingBox boundingBox = new BoundingBox();
    private Dilation dilation = new Dilation();
    private SetDifference difference = new SetDifference();
    private ComponentLabels componentLabels = new ComponentLabels();
    private Area areaExtractor = new Area();

    //Mostly for debug purposes.
    private List<BinaryImage> intermediateImages = new ArrayList<BinaryImage>();

    private BoundingBox.Box getBoundingBox(BinaryImage image, Map<String, Object> imageInfo)
    {
        BoundingBox.Box box = null;
//        if (imageInfo != null && imageInfo.containsKey(BoundingBox.class.toString()));
//        {
//            try
//            {
//                box = (BoundingBox.Box)imageInfo.get(BoundingBox.class.toString());
//            }
//            catch(ClassCastException cce)
//            {
//                //Not a box...
//            }
//        }
        if (box == null)
        {
            box = (BoundingBox.Box)boundingBox.extract(image, imageInfo);
        }
        return box;
    }



    private BinaryImage getClosedImage(BinaryImage image, Map<String, Object> imageInfo)
    {
        BinaryImage closed = null;
//        if (imageInfo.get(CLOSED_IMAGE_KEY) != null)
//        {
//            try
//            {
//                closed = (BinaryImage)imageInfo.get(CLOSED_IMAGE_KEY);
//            }
//            catch(ClassCastException cce)
//            {
//                //Not a BinaryImage
//            }
//        }
//        if (closed == null)
//        {
            BoundingBox.Box box = getBoundingBox(image, imageInfo);
            int n = Math.min(box.getBox().width, box.getBox().height)/2;  //Bigger of the two.
            CombinedOperation nClose = new CombinedOperation(new RepeatedOp(new Dilation(), n), new RepeatedOp(new Erosion(), n+1));
            closed = nClose.apply(image, null);
//            imageInfo.put(CLOSED_IMAGE_KEY, closed);
//        }
        return closed;
    }

    private boolean isNegligible(Component component, int totalArea)
    {
        double componentArea = areaExtractor.extract(component, null).getValue();
        System.out.printf("Percentage = %.4f\n", componentArea/(double)totalArea);
        return componentArea/(double)totalArea < .02;
//        return false;
    }

    @Override
    public Result<WaterContainer> extract(BinaryImage image, Map<String, Object> imageInfo)
    {
        intermediateImages.clear();
        intermediateImages.add(image);

        Rectangle r = boundingBox.extract(image, imageInfo).getValue();
        int area = r.width * r.height;

        //nclose
        BinaryImage closed = getClosedImage(image, null);
        intermediateImages.add(closed);

        //subtract original, leaving just lakes and bays
        BinaryImage componentsImage = difference.apply(closed, image);
        intermediateImages.add(componentsImage);

        //Extract components
        Result<Component[]> result = componentLabels.extract(componentsImage, imageInfo);
        Component[] lakesAndBays = result.getValue();

        //dilate
        BinaryImage dilatedComponentsImage = dilation.apply(componentsImage, UnitDisk.A);
        intermediateImages.add(dilatedComponentsImage);


        //subtract original image, leaving components again, but this time the bays have lids.
        //Then subtract the original components to get just the lids.
        componentsImage = difference.apply(difference.apply(dilatedComponentsImage, image), componentsImage);
        intermediateImages.add(componentsImage);
        
        Component[] justLids = componentLabels.extract(componentsImage, null).getValue();

//        System.out.println("Original components: " + lakesAndBays.length + ", Resulting components: " + justLids.length);
//        int numLakes = lakesAndBays.length - justLids.length;  //A lid would be left for each bay, so lakes would disappear.

        //Now try to figure out which components are lakes and which are bays.
        //We do so here by seeing which components touch the lids.
        //If a component intersects a lid, then it's a bay.
        WaterContainer lakesBaysAndLids = new WaterContainer();

        Rectangle[] lidBoxes = new Rectangle[justLids.length];

        for (int i = 0; i < lidBoxes.length; i++)
        {
            lidBoxes[i] = boundingBox.extract(justLids[i], null).getValue();
            //While we're at it, we might as well add the lids to the result
//            lakesBaysAndLids.addLid(justLids[i]);
        }

        Rectangle box;
        Component lid = null;
        boolean isBay;
        for (Component lakeOrBay : lakesAndBays)
        {
            isBay = false;
            //Dilating the component since the lids came as a result of a dilation,
            //so this should guarantee that the bounding boxes intersect.
            box = boundingBox.extract(dilation.apply(lakeOrBay, UnitDisk.A), null).getValue();
            for (int i = 0; i < lidBoxes.length; i++)
            {
                if (justLids[i] != null && lidBoxes[i].intersects(box))
                {
                    //lids are touching bays
                    isBay = true;
                    lid = justLids[i];
                    justLids[i] = null;
                    break;
                }
            }
            if (isBay)
            {
                if (!isNegligible(lakeOrBay, area))
                {
                    lakesBaysAndLids.addBay(lakeOrBay);
                    lakesBaysAndLids.addLid(lid);
                }
            }
            else
            {
                lakesBaysAndLids.addLake(lakeOrBay);
            }
        }

        return new Result<WaterContainer>(lakesBaysAndLids);
    }

    public BinaryImage[] getIntermediateImages()
    {
        return intermediateImages.toArray(new BinaryImage[intermediateImages.size()]);
    }

}
