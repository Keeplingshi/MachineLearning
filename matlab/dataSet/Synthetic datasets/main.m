clear;
load('twoCircles.mat');
dataSet=twoCircles;
plot(dataSet(labels==1,1),dataSet(labels==1,2),'r.', dataSet(labels==2,1),dataSet(labels==2,2),'b.');


% for i=1:m
%     if labels(i, 1)==1
%         plot(Flame(i,1),Flame(i,2),'b.','MarkerSize',10);
%         hold on;
%     elseif labels(i, 1)==2
%     	plot(Flame(i,1),Flame(i,2),'r.','MarkerSize',10);
%         hold on;
%     else
%         plot(Flame(i,1),Flame(i,2),'g.','MarkerSize',10);
%         hold on;
%     end
% end