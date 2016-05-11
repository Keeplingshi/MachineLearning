clear;
clc;
dataOri =load('data.txt');
n = length(dataOri);%总样本数量
dataSet = dataOri(:,1:3);
dataSet=dataSet/(max(max(abs(dataSet)))-min(min(abs(dataSet))));
labels = dataOri(:,4);%类别标志
labels(labels==0) = -1;
sigma=0.1;        %高斯核函数
C = 2;

svm_train=dataSet(2:2:end,:);
svm_train_labels=labels(2:2:end,:);

%svm训练
[wt,alpha,b]=svm(svm_train,svm_train_labels,sigma,C);
% [wt,alpha,b]=svm(dataSet,labels,sigma,C);

y=wt*dataSet'+b;

labelsnew=zeros(n,1);
for i = 1 : n
    if y(i)  < 0
        labelsnew(i)=1;
    else 
        labelsnew(i)=2;
    end
end

labels(labels==-1) = 2;

