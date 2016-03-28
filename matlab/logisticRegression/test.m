load('Flame.mat');
[a,b]=size(Flame);

dataSet=zeros(a,b+1);
dataSet(:,1)=1;
dataSet(:,2:b+1)=Flame(:,:);
labelMat=labels;

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

x=linspace(0,15,50);
y = (-weights(1,1)-weights(2,1)*x)/weights(3,1); 
%plot(x(1,:),y(1,:),'k-','MarkerSize',15);
hold off;