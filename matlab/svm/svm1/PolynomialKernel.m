function [ result ] = PolynomialKernel( data,c,d )
%多项式核函数
%   data原始数据，行为条数，列为维度
%   c为常数项
%   d为幂次
%   result返回关系矩阵

[m,~] = size(data);
result = zeros(m);
for i=1:m
    for j=i:m
        result(i,j) = (data(i,:) * data(j,:)' + c)^d;
        result(j,i) = result(i,j);
    end
end

end

