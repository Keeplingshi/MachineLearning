clc
clf
clear

%twoCircles数据集
load('twoCircles.mat');
dataSet=twoCircles;
num_clusters=2;
sigma=0.05;

%XOR数据集
% load('XOR.mat');
% dataSet=XOR;
% num_clusters=4;
% sigma=10;

Z=pdist(dataSet);
W=squareform(Z);

C = spectral(W,sigma, num_clusters);

plot(dataSet(C==1,1),dataSet(C==1,2),'r.', dataSet(C==2,1),dataSet(C==2,2),'b.', dataSet(C==3,1),dataSet(C==3,2),'g.', dataSet(C==4,1),dataSet(C==4,2),'m.');
