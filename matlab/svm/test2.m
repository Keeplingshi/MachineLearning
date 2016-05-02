clear;%清屏
clc;
dataOri =load('data.txt');
n = length(dataOri);%总样本数量
dataSet = dataOri(:,1:3);
dataSet=dataSet/(max(max(abs(dataSet)))-min(min(abs(dataSet))));
labels = dataOri(:,4);%类别标志
labels(labels==0) = -1;

sigma=1;
C=1;
b=0;

%核函数
% K=zeros(n,n);
for i=1:n
    for j=1:n
        K{i,j}=exp((dataSet(i,:)-dataSet(j,:)).^2/(2*sigma.^2));
    end
end
% K=pdist(dataSet);
% K=squareform(K);
% K = -K.^2/(2*sigma.^2);
% K=exp(K);

alpha = ones(n,1)*C/2;  %参数a，随机初始化a,a属于[0,C]

f=(alpha.*labels)'*K+b;

