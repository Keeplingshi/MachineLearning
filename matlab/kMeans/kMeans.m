function [centroids,clusterAssment]=kMeans(dataSet,k)
    [m,n]=size(dataSet);
    clusterAssment = zeros(m,2);    %m行2列全零矩阵,用来存储簇分配结果，两列，一列记录簇索引值，另一列存储误差（当前点到簇质心的距离）
    centroids = randCent(dataSet, k);
    clusterChanged = true;
    while clusterChanged
        clusterChanged = false;
        for i=1:m
            minDist=Inf;
            minIndex=-1;
            for j=1:k
                distJI=distEclud(centroids(j,:),dataSet(i,:))   %计算距离
                if distJI < minDist
                    minDist = distJI;
                    minIndex = j;
                end
            end
            if clusterAssment(i,0)~=minIndex
                clusterChanged = true;
            end
            clusterAssment(i,1) = minIndex;
            clusterAssment(i,2) = minDist^2;
        end
        for cent=1:k
           
        end
%                 for i in range(m):
%             minDist = inf   #正无穷
%             minIndex = -1
%             for j in range(k):
%                 distJI = distEclud(centroids[j,:],dataSet[i,:])     #计算距离
%                 if distJI < minDist:
%                     minDist = distJI
%                     minIndex = j
%             if clusterAssment[i,0] != minIndex:     #簇变化
%                 clusterChanged = True
%             clusterAssment[i,:] = minIndex,minDist**2
%         for cent in range(k):
%             ptsInClust = dataSet[nonzero(clusterAssment[:,0].A==cent)[0]]   #找出属于cent簇的dataSet集合
%             centroids[cent,:] = mean(ptsInClust, axis=0)    #mean求平均
    end
end

%读取文本数据，以矩阵形式返回
function dataSet=loadDataSet(filename)
    dataSet=importdata(filename,'\t');
end

%计算两点之间距离
function dist=distEclud(vecA, vecB)
    vecSum=(vecA(1,:)-vecB(1,:)).^2;
    dist=sqrt(vecSum(:,1)+vecSum(:,2));
end

function centroids=randCent(dataSet,k)
    [m,n]=size(dataSet);    % m:输出x有多少行   n:输出x有多少列
    centroids=zeros(k,n);
    for j=1:n
       minJ=min(dataSet(:,j));  %第j列最大值
       maxJ=max(dataSet(:,j));  %第j列最小值
       rangeJ=maxJ-minJ;
       centroids(:,j)=minJ+rangeJ*rand(k,1);    %生成随机矩阵，作为簇的质心
    end
end
