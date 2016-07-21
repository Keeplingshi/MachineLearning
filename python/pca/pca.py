from numpy import *
import matplotlib
import matplotlib.pyplot as plt

def loadDataSet(fileName, delim='\t'):
    fr=open(fileName)
    stringArr=[line.strip().split(delim) for line in fr.readlines()]
    datArr=[list(map(float,line)) for line in stringArr]
    return mat(datArr)

def pca(dataMat,topNfeat=9999999):
    #计算并减去原始数据集中的平均值
    meanVals=mean(dataMat,axis=0)
    meanRemoved=dataMat-meanVals
    #计算协方差矩阵
    covMat=cov(meanRemoved,rowvar=0)
    #计算协方差矩阵的特征值和特征向量
    eigVals,eigVects=linalg.eig(mat(covMat))
    #将特征值从大到小排序
    eigValInd=argsort(eigVals)
    eigValInd=eigValInd[:-(topNfeat+1):-1]
    #将数据转换到保留的特征向量构建的新空间中
    redEigVects=eigVects[:,eigValInd]
    lowDDataMat=meanRemoved*redEigVects
    reconMat=(lowDDataMat * redEigVects.T)+meanVals
    return lowDDataMat,reconMat

dataM=loadDataSet('testSet.txt')
lowDMat,recMat=pca(dataM,1)

fig=plt.figure()
ax=fig.add_subplot(111)
ax.scatter(dataM[:,0].flatten().A[0],dataM[:,1].flatten().A[0],marker='^',s=90)
ax.scatter(recMat[:,0].flatten().A[0],recMat[:,1].flatten().A[0],marker='o',s=50,c='red')
plt.show()