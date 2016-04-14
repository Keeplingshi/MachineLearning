import svm
from numpy import *

dataMat,labelMat=svm.loadDataSet('testSet.txt')

print(labelMat)