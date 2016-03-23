# coding=utf-8
from numpy import *
import time
import matplotlib.pyplot as plt

#  加载文件内容，读取数据
def loadDataSet(filename):
    dataMat=[]
    fr=open(filename)
    for line in fr.readlines():
        curLine = line.strip().split('\t')   # 读取每个数
        dataMat.append([float(curLine[0]), float(curLine[1])])
    return dataMat

# 计算两向量距离
def distEclud(vecA, vecB):
    return sqrt(sum(power(vecA-vecB,2)))

# 为给定数据集构建一个包含k个随机质心的集合
def randCent(dataSet, k):
    n=shape(dataSet)[1]
    centroids=mat(zeros((k,n)))
    for j in range(n):
        minJ=min(dataSet[:,j])
        maxJ=max(dataSet[:,j])
        rangeJ=float(maxJ-minJ)     #最大-最小的差值
        print(rangeJ)
        centroids[:,j] = minJ + rangeJ * random.rand(k,1)   #保证随机点在数据的边界之中
    return centroids

def kMeans(dataSet, k):
    m=shape(dataSet)[0]     #返回数据多少行
    clusterAssment = mat(zeros((m,2)))  #m行2列全零矩阵,用来存储簇分配结果，两列，一列记录簇索引值，另一列存储误差（当前点到簇质心的距离）
    centroids = randCent(dataSet, k)  #获取质心的点
    clusterChanged = True
    while clusterChanged:
        clusterChanged = False
        for i in range(m):
            minDist = inf   #正无穷
            minIndex = -1
            for j in range(k):
                distJI = distEclud(centroids[j,:],dataSet[i,:])     #计算距离
                if distJI < minDist:
                    minDist = distJI
                    minIndex = j
            if clusterAssment[i,0] != minIndex:     #簇变化
                clusterChanged = True
            clusterAssment[i,:] = minIndex,minDist**2
        for cent in range(k):
            ptsInClust = dataSet[nonzero(clusterAssment[:,0].A==cent)[0]]   #找出属于cent簇的dataSet集合
            centroids[cent,:] = mean(ptsInClust, axis=0)    #mean求平均
    return centroids, clusterAssment

def showCluster(dataSet, k, centroids, clusterAssment):
    numSamples, dim = dataSet.shape
    if dim != 2:
        print("Sorry! I can not draw because the dimension of your data is not 2!")
        return 1
    mark = ['or', 'ob', 'og', 'ok', '^r', '+r', 'sr', 'dr', '<r', 'pr']
    if k > len(mark):
        print("Sorry! Your k is too large! please contact Zouxy")
        return 1
    # draw all samples
    for i in range(numSamples):
        markIndex = int(clusterAssment[i, 0])
        plt.plot(dataSet[i, 0], dataSet[i, 1], mark[markIndex])

    mark = ['Dr', 'Db', 'Dg', 'Dk', '^b', '+b', 'sb', 'db', '<b', 'pb']
    # draw the centroids
    for i in range(k):
        plt.plot(centroids[i, 0], centroids[i, 1], mark[i], markersize = 12)

    plt.show()
