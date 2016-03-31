clear;
clc;
load('Flame.mat');
[a,b]=size(Flame);
Flame=Flame*0.1;
dataSet=zeros(a,b+1);
dataSet(:,1)=1;
dataSet(:,2)=Flame(:,1);
dataSet(:,3)=Flame(:,2);
dataSet(:,4)=Flame(:,1).^2;

labelMat=labels-1;

weights=logisticFlame(dataSet,labelMat);
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
eq=weights(1,1)+weights(1,2)*x+weights(1,3)*y+weights(1,4)*x*x;
ezplot(eq);

hold off;
