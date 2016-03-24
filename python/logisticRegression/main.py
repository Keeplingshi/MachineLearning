import logisticRegression
from numpy import *

dataMat,labelMat=logisticRegression.loadDataSet('testSet.txt')

#print(dataMat)
#print(labelMat)

weights=logisticRegression.gradAscent(dataMat,labelMat)
print(weights)

logisticRegression.plotBestFit(weights.getA(),dataMat,labelMat)
