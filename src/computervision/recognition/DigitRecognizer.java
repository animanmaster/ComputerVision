/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package computervision.recognition;

import computervision.image.BinaryImage;
import computervision.image.features.BoundingBox;
import computervision.image.features.BoundingBox.Box;
import computervision.image.features.Centroid;
import computervision.image.features.LakesBaysAndLids;
import computervision.image.features.Moment;
import computervision.image.features.components.Component;
import computervision.image.features.components.ComponentLabels.Components;
import computervision.image.operations.Closing;
import computervision.image.operations.CombinedOperation;
import computervision.image.operations.Dilation;
import computervision.image.operations.MorphologicalOperator;
import computervision.image.operations.Opening;
import computervision.image.operations.SetDifference;
import computervision.image.operations.UnitDisk;
import computervision.image.transforms.Translate;
import computervision.recognition.BinaryDecisionTree.Node;

/**
 *
 * @author Malik Ahmed
 */
public class DigitRecognizer implements Recognizer<Integer>
{
    private MorphologicalOperator preprocessOp = new CombinedOperation(
                                                    new Dilation(),
                                                    new Dilation(),
                                                    new Closing(),
                                                    new Opening());
    private SetDifference difference = new SetDifference();
//    private ComponentLabels labeler = new ComponentLabels();
    private BoundingBox boundingBoxer = new BoundingBox();
    private Moment moments = new Moment();
    private Centroid centroidExtractor = new Centroid();
    private LakesBaysAndLids waterFilter = new LakesBaysAndLids();

    private BinaryDecisionTree<Integer> decisionTree;

    //To be accessed by the decision tree.
    private BinaryImage image = null;
    private Component[] lakes, bays, lids;



        final int TOP_HALF = (Component.QUAD1 | Component.QUAD2),
                  BOTTOM_HALF = (Component.QUAD3 | Component.QUAD4),
                  LEFT_HALF = (Component.QUAD2 | Component.QUAD3),
                  RIGHT_HALF = (Component.QUAD1 | Component.QUAD4);

    public DigitRecognizer()
    {
        this.decisionTree = buildDecisionTree();
    }

    private Decision[] makeNumLakesDecisions()
    {
        Decision[] decisions = new Decision[3];
        decisions[0] = new Decision() {

            public boolean isYes() {
                return lakes.length == 0;
            }
        };
        decisions[1] = new Decision() {

            public boolean isYes() {
                return lakes.length == 1;
            }
        };
        decisions[2] = new Decision() {

            public boolean isYes() {
                return lakes.length == 2;
            }
        };
        return decisions;
    }
    private Decision[] makeNumBaysDecisions()
    {
        Decision[] decisions = new Decision[5];
        decisions[0] = new Decision() {

            public boolean isYes() {
                return bays.length == 0;
            }
        };
        decisions[1] = new Decision() {

            public boolean isYes() {
                return bays.length == 1;
            }
        };
        decisions[2] = new Decision() {

            public boolean isYes() {
                return bays.length == 2;
            }
        };
        decisions[3] = new Decision() {

            public boolean isYes() {
                return bays.length == 3;
            }
        };
        decisions[4] = new Decision() {

            public boolean isYes() {
                return bays.length == 4;
            }
        };
        return decisions;
    }

    private Decision[] makeNumberDecisions()
    {
        Decision[] decisions = new Decision[10];
        decisions[0] = new Decision() {

            public boolean isYes() {
                return lakes[0].isVertical();
            }
        };
        decisions[1] = new Decision() {

            public boolean isYes()
            {
                Moment.MomentResult moment = (Moment.MomentResult)moments.extract(image, null);
                return moment.getRowMoment() > moment.getColumnMoment();
            }
        };
        decisions[2] = new Decision()
        {
            public boolean isYes() {
                return lids[0].isToTheLeftOf(lids[1])
                        && lids[0].isSlightlyLeftOf(bays[0]);
            }
        };
        decisions[3] = new Decision() {

            public boolean isYes() {
                return lids[0].isSlightlyLeftOf(bays[0]);
                //Either two or one other lid...
            }
        };
        return decisions;
    }

    private BinaryDecisionTree<Integer> buildDecisionTree()
    {
        BinaryDecisionTree<Integer> decisionTree = new BinaryDecisionTree<Integer>();

        Integer deadend = null; //The result to use as a dead end.

        //To make the following tree a little cleaner,
        //declare most of the decisions you need to make here.
        Decision[] numLakes = makeNumLakesDecisions();
        Decision[] numBays = makeNumBaysDecisions();
        Decision[] isSimpleNumber = makeNumberDecisions();    //assuming we have enough lakes and bays.

        Node<Integer> root = decisionTree.getRoot();
        root.setDecision(numLakes[0])
            .onYes(numBays[0])
                .onYes(1) //0 lakes and 0 bays => 1
                //If lakes.length == 0 && bays.length > 0:
                .onNo(numBays[1])
                    //1 bay?
                    .onYes(new Decision() {
                        public boolean isYes() {
                            return lids[0].isSlightlyLeftOf(bays[0]);
                        }
                    }).onYes(7) //7 only if lid to the left.
                      .onNo(deadend) //dead end if the single lid isn't to the left of the single bay.
                      .getParent()
                    //More than 1 bay
                    .onNo(numBays[2])
                        //2 bays?
                        .onYes(new Decision() {
                            public boolean isYes() {
                                return lids[0].isHorizontal() && lids[0].isInQuadrant(TOP_HALF)
                                        && lids[1].isVertical()
                                        && lids[1].isSlightlyLeftOf(bays[1])
                                        && lids[1].isInQuadrant(LEFT_HALF);
                            }
                        }).onYes(4)
                          .onNo(new Decision() {
                                public boolean isYes() {
                                    return lids[0].isSlightlyAbove(lids[1])
                                            && lids[0].isVertical()
                                            && lids[0].isSlightlyRightOf(bays[0])
                                            && lids[1].isVertical()
                                            && lids[1].isSlightlyLeftOf(bays[1]);
                                }
                            }).onYes(5)
                              .onNo(new Decision() {

                                    public boolean isYes() {
                                        return lids[0].isSlightlyLeftOf(bays[0]) && lids[0].isInQuadrant(LEFT_HALF)
                                                && lids[1].isSlightlyRightOf(bays[1]) && lids[1].isInQuadrant(RIGHT_HALF);
                                    }

                                }).onYes(2)
                                  .onNo(new Decision() {
                                        public boolean isYes() {
                                            return lids[0].isSlightlyLeftOf(bays[0]) && lids[0].isInQuadrant(LEFT_HALF)
                                                    && lids[1].isSlightlyLeftOf(bays[1])
                                                    && lids[0].isAbove(lids[1]);
                                            //Sometimes the left side of the three becomes 1 bay
                                            //and the other side is iffy.
                                        }
                                        }).onYes(3)
                                          .onNo(deadend)
                                          .getParent()
                                  .getParent()
                              .getParent()
                          .getParent()
                        .onNo(numBays[3])
                            //3 bays?
                            .onYes(new Decision() {

                                public boolean isYes() 
                                {
                                    int rightLid = (lids[1].isToTheRightOf(lids[0])? 1 : 2);
                                    int bottomLeftLid = (rightLid == 1? 2 : 1);
                                    return lids[0].isSlightlyLeftOf(bays[0])
                                            && lids[0].isToTheLeftOf(lids[rightLid])
                                            && lids[0].isSlightlyAbove(lids[bottomLeftLid]);
                                }
                            }).onYes(3)
                              .onNo(deadend)
                              .getParent()
                            .onNo(numBays[4])
                                //0 lakes, 4 bays?
                                .onYes(new Decision() {

                                    public boolean isYes() {
                                        return lids[0].isSlightlyLeftOf(bays[0])
                                                && lids[1].isSlightlyRightOf(bays[1])
                                                && lids[2].isSlightlyLeftOf(bays[2])
                                                && lids[3].isSlightlyRightOf(bays[3])
                                                && lids[0].isSlightlyAbove(lids[2])
                                                && lids[0].isToTheLeftOf(lids[1])
                                                && lids[2].isSlightlyAbove(lids[3]);
                                    }
                                }).onYes(7)
                                  .onNo(deadend)
                                  .getParent()
                                .onNo(deadend);  //0 lakes, but >4 bays = deadend
        Node<Integer> oneLake = root.onNo(numLakes[1]);
        oneLake.onYes(numBays[0])
                    //0 bays
                    .onYes(0)
                    .onNo(numBays[1])
                        //1 lake, 1 bay
                        .onYes(new Decision() {

                            public boolean isYes() {
                                return bays[0].isSlightlyAbove(lakes[0])
                                        && lids[0].isSlightlyRightOf(bays[0]);
                            }
                        }).onYes(6)
                          .onNo(new Decision() {

                                public boolean isYes() {
                                    return bays[0].isSlightlyBelow(lakes[0])
                                            && lids[0].isSlightlyLeftOf(bays[0]);
                                }
                            }).onYes(9)
                              .onNo(deadend)
                              .getParent()
                          .getParent()
                       .onNo(numBays[3])
                          //1 lake, 3 bays
                          .onYes(new Decision() {
                                public boolean isYes() {
                                    return lids[0].isSlightlyLeftOf(bays[0])
                                            && bays[0].isSlightlyAbove(lakes[0]);
                                }
                            }).onYes(2) //loopy 2
                              .onNo(deadend)
                              .getParent()
                          .onNo(numBays[4])
                                //one lake, 4 bays
                                .onYes(4)   //closed up 4
                                .onNo(deadend);

        oneLake.onNo(numLakes[2])
                //2 lakes
                .onYes(new Decision() {
                    public boolean isYes() {
                        return lakes[0].isSlightlyAbove(lakes[1]);
                    }
                }).onYes(8)
                  .onNo(deadend);
        return decisionTree;
    }

    BinaryImage preprocess(BinaryImage image)
    {
        BinaryImage preprocessed = preprocessOp.apply(image, UnitDisk.A);
        BoundingBox.Box box = (BoundingBox.Box)boundingBoxer.extract(image, null);
        int centerRow = image.getNumberOfRows()/2, centerCol = image.getNumberOfColumns()/2;
        Translate translate = new Translate(centerRow - box.getCenterRow(), centerCol - box.getCenterColumn());
        preprocessed = translate.apply(preprocessed);
        return preprocessed;
    }

    private Integer decide()
    {
        return decisionTree.decide();
    }

    private Integer decide(BinaryImage originalImage, Component[] lakes, Component[] bays, Component[] lids)
    {
        Integer result = null;
        
        Moment.MomentResult moment = (Moment.MomentResult)moments.extract(originalImage, null);
        Centroid.Position centroid = centroidExtractor.extract(originalImage, null).getValue();
        Box boundingBox = (Box)boundingBoxer.extract(originalImage, null);
        switch(lakes.length)
        {
            case 0: //0 lakes.
                //Could be a 1, 2, 3, 4, 5, or 7.
                switch (bays.length)
                {
                    case 0: //0 bays
                        //Could be a 1. Let's make sure it's vertical.\
                        if (moment.getRowMoment() > moment.getColumnMoment())
                        {
                            result = 1;
                        }
                        break;
                    case 1: //1 bay
                        //Probably a 7 or a 4 with a negligible bay beneath it.
                        if (lids[0].isSlightlyLeftOf(bays[0]) && lids[0].isInQuadrant(LEFT_HALF))
                        {
                            //Lid is to the left; this is a 7 after all!
                            result = 7;
                        }
                        else if (lids[0].isHorizontal())
                        {
                            //Mostly horizontal lid, so it's a 4.
                            result = 4;
                        }
                        break;
                    case 2: //2 bays
                        //Could be a 4 [with an open top] or a 5 or a 2.
                        //Note that I'm counting on the implementation to return the top lid first.
                        if (lids[0].isHorizontal())
                        {
                            //Mostly horizontal lid, so it's a 4.
                            result = 4;
                        }
                        else
                        {
                            //Vertical lid near top.
                            if (lids[0].isSlightlyAbove(lids[1]) && lids[0].isToTheRightOf(lids[1]))
                            {
                                //The top lid is NE of the other lid; this is a 5.
                                result = 5;
                            }
                            else if (lids[0].isToTheLeftOf(lids[1]) && lids[0].isSlightlyLeftOf(bays[0]) && lids[1].isSlightlyRightOf(bays[1]))
                            {
                                //The lids are to the left and right; this is a 2.
                                result = 2;
                            }
                            else if (lids[0].isSlightlyAbove(lids[1]) && lids[0].isSlightlyLeftOf(bays[0]) && lids[1].isSlightlyLeftOf(bays[1]))
                            {
                                //The two lids are above each other, it could be a 3.
                                result = 3;
                            }
                        }
                        break;
                    case 3: //3 bays
                        //Could be a 3 if the lids look something like this:
                        //  |
                        //      |
                        //  |

//                        lidBox = (Box)boundingBoxer.extract(lids[0], null);
//                        byte positioning1vs2 = lidBox.getSpatialRelationship((Box)boundingBoxer.extract(lids[1], null));
//                        byte positioning1vs3 = lidBox.getSpatialRelationship((Box)boundingBoxer.extract(lids[2], null));
//                        if ( (positioning1vs2 & (BoundingBox.LEFT | BoundingBox.ABOVE)) > 0  &&
//                                (positioning1vs3 & BoundingBox.ABOVE) > 0)
                        if (lids[0].isSlightlyLeftOf(bays[0]) &&
                                ((lids[1].isSlightlyRightOf(bays[1]) && lids[1].isToTheRightOf(lids[2])) || (lids[1].isSlightlyLeftOf(bays[1]) && lids[1].isToTheLeftOf(lids[2]))))
                        {
                            result = 3;
                        }
                        break;
                    case 4:
                        //Could be a 7 with a line through it, in which case we have lids kinda like this:
                        /*
                         *  |   |
                         *
                         *  |   |
                         */
//                        lidBox = (Box)boundingBoxer.extract(lids[0], null);
//                        Box lidBox4 = (Box)boundingBoxer.extract(lids[3], null);
//                        positioning1vs2 = lidBox.getSpatialRelationship((Box)boundingBoxer.extract(lids[1], null));
//                        byte positioning4vs3 = lidBox4.getSpatialRelationship((Box)boundingBoxer.extract(lids[2], null));
//                        if ( (positioning1vs2 & BoundingBox.LEFT) > 0 && (positioning4vs3 & BoundingBox.RIGHT) > 0)
                        if (lids[0].isInQuadrant(TOP_HALF) && lids[1].isInQuadrant(TOP_HALF) && lids[2].isInQuadrant(BOTTOM_HALF) && lids[3].isInQuadrant(BOTTOM_HALF))
                        {
                            result = 7;
                        }
                    default:
                        //TODO
                        //Dunno what this is.
                        break;
                }
                //END 0 lakes.
                break;
            case 1: //1 lake

                if (bays.length == 0)
                {
                    //1 lake and 0 bays? Sounds like a 0 to me!
                    result = 0;
                }
                else //if (bays.length > 0)
                {
                    //See where the bay is with respect to the lake.
//                    Box lakeBox = (Box)boundingBoxer.extract(lakes[0], null);
//                    Box bayBox = (Box)boundingBoxer.extract(bays[0], null);
//                    byte lakeVsBay = lakeBox.getSpatialRelationship(bayBox);
//                    if ((lakeVsBay & BoundingBox.ABOVE) > 0)
                    if (lakes[0].isSlightlyAbove(bays[0]))
                    {
                        //The lake is above the bay.
                        //It could be a 4 or a 9, which is hard without checking circularity,
                        //so we'll say it's a 9.
                        result = 9;
                    }
                    else if (lakes[0].isSlightlyBelow(bays[0]))
                    {
                        //The lake is below the bay, so this is either a 6 or a loopy 2.
//                        Box lidBox = (Box)boundingBoxer.extract(lids[0], null);
//                        byte lidVsBay = lidBox.getSpatialRelationship(bayBox);
//                        if ( (lidVsBay & BoundingBox.LEFT) > 0)
                        if (lids[0].isSlightlyLeftOf(bays[0]))
                        {
                            //Since the lid is to the left of the bay,
                            //this is loopy 2.
                            result = 2;
                        }
                        else// if ( (lidVsBay & BoundingBox.RIGHT) > 0)
                        {
                            //The lid is to the right of the bay, so this is a 6.
                            result = 6;
                        }
                    }
                }
                break;
                //End 1 lake.
            case 2: //2 lakes
//                if (bays.length >= 2)
//                {
                    //>= only because we get extra bays if you're sloppy.
                    //The only other requirement is that the lakes are on top of each other.
//                    Box lake1 = (Box)boundingBoxer.extract(lakes[0], null),
//                        lake2 = (Box)boundingBoxer.extract(lakes[1], null);
//
//                    byte positions = lake1.getSpatialRelationship(lake2);
//                    if ( (positions & (BoundingBox.ABOVE | BoundingBox.BELOW)) > 0)
                    if (lakes[0].isSlightlyAbove(lakes[1]))
                    {
                        result = 8;
                    }
//                }
                break;
            //TODO Other cases?
            default:
                //TODO
                //No idea wtf this is.
                break;
        }
        return result;
    }

    @Override
    public Integer recognize(BinaryImage image)
    {
        Integer result = null;
        if (image != null)
        {
            this.image = preprocess(image);
//            new RAWImageViewer(new RAWImage(image)).setVisible(true);
            LakesBaysAndLids.WaterContainer featureResults = waterFilter.extract(this.image, null).getValue();
            lakes = featureResults.getLakes();
            bays  = featureResults.getBays();
            lids  = featureResults.getLids();
            System.out.printf("%d lakes, %d bays, %d lids\n", lakes.length, bays.length, lids.length);
            result = decide(this.image, lakes, bays, lids);
//            result = decide();
        }
        return result;
    }

    //Debug
    public BinaryImage[] getIntermediateImages()
    {
        return waterFilter.getIntermediateImages();
    }


}
