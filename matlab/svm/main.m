clear;
clc;
% dataOri =load('data.txt');
% n = length(dataOri);%总样本数量
% dataSet = dataOri(:,1:3);
% dataSet=dataSet/(max(max(abs(dataSet)))-min(min(abs(dataSet))));
% labels = dataOri(:,4);%类别标志
% labels(labels==0) = -1;
% sigma=0.1;        %高斯核函数
% C = 1.1;

%twoCircles数据集
% load('twoCircles.mat');
% dataSetOri=twoCircles;
% n = length(dataSetOri);
% dataSet=dataSetOri/(max(max(abs(dataSetOri)))-min(min(abs(dataSetOri))));
% labels(labels==2) = -1;
% sigma=0.1;
% C = 1;

%biodeg,SpectfHeart数据集
load('SpectfHeart.mat');
dataSetOri=SpectfHeart;
n = length(dataSetOri);
dataSet=dataSetOri/max(max(abs(dataSetOri)));
labels(labels==2) = -1;
sigma=0.1;
C=1;
% svm_train=dataSet(2:3:end,:);
% svm_train_labels=labels(2:3:end,:);

%svm训练
[wt,alpha,b]=svm(dataSet,labels,sigma,C);

y=wt*dataSet'+b;

labelsnew=zeros(n,1);
for i = 1 : n
    if y(i)  < 0
        labelsnew(i)=2;
    else 
        labelsnew(i)=1;
    end
end

labels(labels==-1) = 2;

score=nmi(labels,labelsnew)
