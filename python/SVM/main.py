import svm
from numpy import *

dataMat,labelMat=svm.loadDataSet('testSet.txt')

#print(labelMat)

b,alphas=svm.smoSimple(dataMat,labelMat,0.6,0.001,40)

print(b)