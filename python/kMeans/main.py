import kMeans
from numpy import *

datMat=mat(kMeans.loadDataSet('testSet.txt'))
#print(datMat)

#print(kMeans.distEclud(datMat[0],datMat[1]))

#print(kMeans.randCent(datMat,2))

k = 4
centroids, clusterAssment = kMeans.kMeans(datMat, k)
#print(clusterAssment)

#kMeans.showCluster(datMat, k, centroids, clusterAssment)
