function [centroids,clusterAssment]=kMeans(dataSet,k)
    clusterAssment=dataSet;
    centroids=k;
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
