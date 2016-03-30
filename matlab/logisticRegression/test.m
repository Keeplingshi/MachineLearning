clear;
clc;
load('Flame.mat');
[a,b]=size(Flame);

dataSet=zeros(a,b+1);
dataSet(:,1)=1;
dataSet(:,2)=Flame(:,1);
dataSet(:,3)=Flame(:,2);
dataSet(:,4)=Flame(:,1).^2;

labelMat=labels-1;

weights=logisticFlame(dataSet*0.01,labelMat)
m=size(dataSet,1);
for i=1:m
    if labels(i, 1)==1
        plot(Flame(i,1),Flame(i,2),'b.','MarkerSize',10);
        hold on;
    else
        plot(Flame(i,1),Flame(i,2),'r.','MarkerSize',10);
        hold on;
    end
end

syms x y eq;
eq=weights(1,1)+weights(2,1)*x+weights(3,1)*y+weights(4,1)*x*x;
%eq=x*x-y;
ezplot(eq);

hold off;
