from copy import deepcopy

# The following GrowingList was adapted based on http://stackoverflow.com/questions/4544630/automatically-growing-lists-in-python
class GrowingList(list):
    def __init__(self, fillerValue=None):
        self.fillerValue = fillerValue

    def expandAsNeeded(self, index):
        '''
        If we need to extend the list to accomodate the given index, do so and fill the new space with
        whatever was chosen to be fillerValue.
        '''
        if index >= len(self):
            self.extend([deepcopy(self.fillerValue) for i in range(index + 1 - len(self))])

    # This following two methods override setting and accessing elements in the array
    # using [].
    def __setitem__(self, index, value):
        self.expandAsNeeded(index)
        list.__setitem__(self, index, value)

    def __getitem__(self, index):
        self.expandAsNeeded(index)
        return list.__getitem__(self, index)

