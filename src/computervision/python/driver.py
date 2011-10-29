#!/usr/bin/env python
import sys

from binaryimage import BinaryImage
import visionalgos

def parse_args():
    if len(sys.argv) == 1:
        print "usage:", sys.argv[0], " image1 [image2 ...]"
        exit(1)

    imgs = []
    for index in range(1, len(sys.argv)):
        arg = sys.argv[index]
        imgs.append(BinaryImage(arg))

    return imgs

def main_hw1():
    images = parse_args()

    for image in images:
        holes = visionalgos.count_holes(image);
        print "File", image.filename, "contains", holes, "hole." if holes == 1 else "holes."

# MAIN:
def main_hw2():
    images = parse_args()

    for image in images:
        labelled_image, parents = visionalgos.label(image)
        print "File", image.filename,":" 
        print image
        print "Labelled Image:"
        print labelled_image
        print "Parents:", parents   # Mote that the array is zero-based, so the first element will always be 0.
        print "==============================================="


# The "correct" way to do this, in case this file is imported.
if __name__ == "__main__":
    main_hw2()


