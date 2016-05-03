clc;clear;
% data = load('twoCircles.mat');
% [m,~] = size(data.labels);
% for i=1:m
%     if(data.labels(i)==2)
%         data.labels(i)=-1;
%     end
% end
% plot(data.twoCircles(:,1),data.twoCircles(:,2),'.');
% SVM(data.twoCircles,data.labels);


% ------------------------------------------------------------%
 % 构造两类训练数据集    以 y=ax^2+ b*x+c 为分界线   shift   
%   if a==0  then it is a line
 
% a=0;
% b=3;
% c=0.5;
% shift=0.5;
% b1=0.5;   
% 
% xx=-1:0.1:3.9;
% n = length(xx);
% x1=zeros(n,2);
% x2=zeros(n,2);
% x1(:,1) = xx';
% x2(:,1)=xx';
% x1(:,2)=a*x1(:,1).*x1(:,1)+b*x1(:,1)+c + shift+   abs(randn(n,1)) ;
% x2(:,2)=a*x2(:,1).*x2(:,1)+b*x2(:,1)+c - shift-  abs(randn(n,1)) ;
% y1 = ones(n,1);
% y2 = -ones(n,1);
% 
% X = [x1;x2]; % 训练样本
% Y = [y1;y2]; % 训练目标,n×1的矩阵,n为样本个数,值为+1或-1
% X1 = zeros(n/2,2);
% Y1 = zeros(n/2,1);
% X2 = zeros(n/2,2);
% Y2 = zeros(n/2,1);
% for i=1:n
%     X1(i,:) = X(2*i,:);
%     X2(i,:) = X(2*i-1,:);
%     Y1(i,:) = Y(2*i,:);
%     Y2(i,:) = Y(2*i-1,:);
% end
% figure;
% plot(X1(:,1),X1(:,2),'r.');
% hold on;
% plot(X2(:,1),X2(:,2),'b.');
%     
% 
% [w,b,a] = SVM(X1, Y1, 0.001);
% 
% 
% % [w,b,a] = SVM(data.twoCircles, data.labels, 0.001);
% y = (w * X2' + b)';
% [m,~] = size(y);
% mid = sum(y)/m
% for i=1:m
%     if(y(i) - mid >0)
%         plot(X2(i,1),X2(i,2),'ro');
%     end
% end

load('Iris');
for i=1:100
    if labels(i)==2
        labels(i)=-1;
    end
end
[w,b,a] = SVM(Iris(1:100,:), labels(1:100,:), 0.001);
X=Iris(1:100,:);
% K = PolynomialKernel(X,1,3)

y = (w * X' + b)';
[m,~] = size(y);
mid = sum(y)/m
figure;
hold on;
for i=1:m
    if(y(i) - mid >0)
        plot(X(i,1),X(i,2),'ro');
    else
        plot(X(i,1),X(i,2),'bo');
    end
end
% re = nmi