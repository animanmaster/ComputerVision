#!/usr/bin/env python

# Binary Image Class
# Project: Computer Vision HW 2
# Author:  Malik Ahmed
# 
# BinaryImage files are in the following format:
#
# 10101011
# 10101011
# 10101011
# 10101011
#
# Where 0 signifies a hole and 1 signifies a material.
#
# It even supports jagged images! :D


class BinaryImage:

    def __init__(self, filename=None):
        self.pixels = [] 
        self.filename = filename
        self.parse(filename)

    def parse(self, filename):
        if filename:
            lines = open(filename).readlines()
            for line in lines:
                row = []
                for char in line.rstrip():
                    if char == '1':
                        row.append(1)
                    else:
                        row.append(0)
                self.pixels.append(row)

    def get_size(self):
        return len(self.pixels), (self.pixels and len(self.pixels[0])) or 0

    def set_size(self, numRows, numCols):
        self.pixels = [ [ (col < len(self.pixels[row]) and self.pixels[row][col]) or 0 for col in range(numCols) ] for row in range(numRows) ]
 
    def copy(self):
        img = BinaryImage()
        img.pixels = [ [pixel for pixel in row] for row in self.pixels ]  # Must copy each pixel individually
        return img

    # The Python version of toString().
    def __str__(self):
        str_img = ""
        for row in self.pixels:
            for val in row:
                str_img += str(val) + (" " if len(str(val)) == 1 else "") # The extra space makes it easier to see 2-digit labels.
            str_img += '\n'
        return str_img
