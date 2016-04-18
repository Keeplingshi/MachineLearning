clc;
clear;
close all;

%% Load Data

load('Flame.mat');


 y=labels;
 y(y==2)=-1;
 x=Flame;

% x=[TrainT2(41:80,1:2)',TrainT2(109:160,1:2)'];
% y=[TrainT2(21:80,3)',TrainT2(109:160,3)'];

n=numel(y);

ClassA=find(y==1);
ClassB=find(y==-1);


%% Design SVM

C=10;

H=zeros(n,n);
for i=1:n
    for j=i:n
        H(i,j)=y(i)*y(j)*x(:,i)'*x(:,j);
        H(j,i)=H(i,j);
    end
end

f=-ones(n,1);

Aeq=y;
beq=0;

lb=zeros(n,1);
ub=C*ones(n,1);