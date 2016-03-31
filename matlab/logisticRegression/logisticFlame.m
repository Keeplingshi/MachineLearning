function [weights]=logisticFlame(dataSet,labelMat)
% 逻辑回归，调用Flame数据集
% dataSet  数据集
% labelMat 标签值
%

    %默认梯度上升法
     format long
     n=size(dataSet,2);
     alpha = 0.02;  %向目标移动的步长
     maxCycles = 500;    %迭代次数
     weights = ones(n,1);
     for k=1:maxCycles
         h=sigmoid(dataSet*weights);
         error=(labelMat-h);
         weights = weights + alpha *dataSet.'* error;
     end
     weights=weights.';
%     %改进随机梯度上升法
%     format long
%     [m,n]=size(dataSet);
%     %迭代次数，使用Flame数据测试，500次拟合效果并不好，100000次拟合效果还可以，
%     %但是未经改进的梯度上升法，500次拟合效果比改进后100000次拟合效果要好
%     %以上仅针对Flame数据集，其他数据集测试，改进后的算法效果更好
%     maxCycles = 500;    
%     weights = ones(1,n);
%     for j=1:maxCycles
%         dataIndex=1:1:m;
%         for i=1:m
%             if(isempty(dataIndex))
%                 break;
%             end
%             alpha = 4/(1.0+j+i)+0.001;
%             randIndex=ceil(rand(1,1)*length(dataIndex));
%             h=sigmoid(weights*dataSet(randIndex,:).');
%             error=(labelMat(randIndex,:)-h);
%             weights = weights + alpha * error * dataSet(randIndex,:);
%             dataIndex(:,randIndex)=[];
%         end
%     end
end

%Sigmoid函数
function sigmoidre=sigmoid(x)
    format long
    sigmoidre=1.0./(1+exp(-x));
end



