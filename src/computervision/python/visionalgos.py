#!/usr/bin/env python

from growinglist import GrowingList

#==================================================================#
#HW1:
def count_holes(image):
    """
    This function will count the number of holes it finds in
    the passed in binary image using a 4-neighborhood definition.
    It will do its best for files that aren't properly formatted
    or have ambiguous parts.
    """
    external = 0
    internal = 0
    for index in range(len(image.pixels) - 1):
        row1 = image.pixels[index]
        row2 = image.pixels[index+1]
        for index in range(len(row1) - 1):
            if index < len(row1) - 1 and index < len(row2) - 1:
                total = row1[index] + row1[index+1] + \
                        row2[index] + row2[index+1]
                if total == 1:
                    internal = internal + 1
                elif total == 3:
                    external = external + 1
    return (external - internal)/4.0

#==================================================================#

# HW2:
#
# The pseudocode from class translates really well into Python! :3
#
# Any Python-specific quirks are documented in the functions below.
# Everything else should be easily understandable.

def find(x, parent):
    '''
    Find the root parent of the given element 
    (i.e. the one whose parent is 0).
    '''
    j = x
    while parent[j] != 0:
        j = parent[j]
    return j

def union(x, y, parent):
    '''
    Join two equivalence classes together by taking the
    root of one and making it the child of the other.
    '''
    j = find(x, parent)
    k = find(y, parent)
    if (j != k):
        parent[k] = j

def prior_neighbors(image, row, col):
    '''
    Return the prior neighbors already processed in the 8-neighborhood.
    
    Ex:

    +++
    +.

    where . is the current position in image at row, col and
    + are the neighbors to return.
    '''
    neighbors = []
    if (row > 0):   
        # If we have a previous row,
        # Grab the pixels from the previous column up to the next column
        # (provided they exist).
        #
        # The nice thing about Python array slices is that if the end
        # index is greater than the length of the array, it'll return as much
        # as it can, which is why we don't need to check whether 
        neighbors.extend(image.pixels[row-1][max(0, col-1) : col+2]) 
    if (col > 0): 
        # If we have a previous column,
        # grab the pixel immediately to the left.
        neighbors.extend([image.pixels[row][col-1]])

    return neighbors

def labels(neighbors):
    '''
    Return an array of labels for the given list of neighbors.
    Essentially, this'll return a set (so no duplicates) of numbers > 0
    since 0 doesn't count as a label.
    '''
    return set([label for label in neighbors if label != 0])

def label(image, labelled_image=None):
    '''
    Labels the given image (and stores the labels in the labelled_image)
    according to the labeling algorithm given in class.
    '''

    label = 1   # The label to use for new components.

    # If an image isn't provided to label, make a copy of the input image.
    if not labelled_image:
        labelled_image = image.copy()

    parent = GrowingList(0) # Using a custom, automatically expanding list for clarity.

    # Begin first pass.
    for row in range(len(image.pixels)):
        for col in range(len(image.pixels[row])):
            if (image.pixels[row][col] == 1):
                prior = labels(prior_neighbors(labelled_image, row, col))
                if not prior:   # Empty lists are regarded as False in Python.
                    M = label
                    label = label + 1
                else:
                    M = min(prior)
                labelled_image.pixels[row][col] = M
                for x in prior:
                    if x != M:
                        union(M, x, parent)
    
    # Begin second pass.
    for row in range(len(image.pixels)):
        for col in range(len(image.pixels[row])):
            if (image.pixels[row][col] == 1):
                labelled_image.pixels[row][col] = find(labelled_image.pixels[row][col], parent)

    return labelled_image, parent

