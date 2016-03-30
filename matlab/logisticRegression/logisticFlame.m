function [weights]=logisticFlame(dataSet,labelMat)
% 逻辑回归，调用Flame数据集
%
%
%

    format long
    n=size(dataSet,2);
    alpha = 0.001;  %向目标移动的步长
    maxCycles = 500;    %迭代次数
    weights = ones(n,1);
    %dataSet*weights
    sigmoid(dataSet*weights)
    for k=1:maxCycles
        h=sigmoid(dataSet*weights);
        error=(labelMat-h);
        weights = weights + alpha *dataSet.'* error;
    end

end

%Sigmoid函数
function sigmoidre=sigmoid(x)
    format long
    sigmoidre=1.0./(1+exp(-x));
end



