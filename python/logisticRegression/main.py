import logisticRegression
from numpy import *

dataMat,labelMat=logisticRegression.loadDataSet('testSet.txt')

#print(dataMat)
#print(labelMat)

#weights=logisticRegression.gradAscent(dataMat,labelMat)
#print(weights)

weights=logisticRegression.stocGradAscent1(array(dataMat),labelMat)
print(weights)

logisticRegression.plotBestFit(weights,dataMat,labelMat)
