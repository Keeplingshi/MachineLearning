function [weights,dataSet,labelMat]=logisticRegression(filename)
    format long
    [dataSet,labelMat]=loadDataSet(filename);
    [m,n]=size(dataSet);
    %alpha = 0.001;  %向目标移动的步长
    maxCycles = 500;    %迭代次数
    weights = ones(1,n);
    for j=1:maxCycles
        dataIndex=1:1:m;
        for i=1:m
            if(isempty(dataIndex))
                break;
            end
            alpha = 4/(1.0+j+i)+0.0001;
            randIndex=ceil(rand(1,1)*length(dataIndex));
            h=sigmoid(weights*dataSet(randIndex,:).');
            error=(labelMat(randIndex,:)-h);
            weights = weights + alpha * error * dataSet(randIndex,:);
            dataIndex(:,randIndex)=[];
        end
    end

end

%读取文本数据，以矩阵形式返回
function [dataSet,labelMat]=loadDataSet(filename)
    format long
    dataMatTemp=importdata(filename,'\t');  %将文件内容读取到一个矩阵
    [m,n]=size(dataMatTemp);
    dataSet=zeros(m,n);
    dataSet(:,1)=1;
    dataSet(:,2:n)=dataMatTemp(:,1:n-1);
    labelMat=dataMatTemp(:,n);
end

%Sigmoid函数
function sigmoidre=sigmoid(x)
    format long
    sigmoidre=1.0./(1+exp(-x));
end



