clear;%清屏
clc;
% dataOri =load('data.txt');
% n = length(dataOri);%总样本数量
% dataSet = dataOri(:,1:3);
% labels = dataOri(:,4);%类别标志
% labels(labels==0) = -1;

load('Flame.mat');
dataSet = Flame;
n = length(dataSet);%总样本数量
% labels = dataOri(:,4);%类别标志
% labels(labels==0) = -1;

sigma=1;        %高斯核函数
TOL = 0.0001;   %精度要求
C = 1;          %参数，对损失函数的权重
b = 0;          %初始设置截距b
Wold = 0;       %未更新a时的W(a)
Wnew = 0;       %更新a后的W(a)

a = ones(n,1)*0.2;  %参数a

%高斯核函数处理数据
W=pdist(dataSet);
W=squareform(W);
W = -W.^2/(2*sigma*sigma);
W = full(spfun(@exp, W));
for i=1:n
    W(i,i)=1;
end

sum=(a.*labels)'*W;

while 1 %迭代过程
    
    %启发式选点
    n1 = 1;%初始化，n1,n2代表选择的2个点
    n2 = 2;
    %n1按照第一个违反KKT条件的点选择
    while n1 <= n
        if labels(n1) * (sum(n1) + b) == 1 && a(n1) >= C && a(n1) <=  0
            break;
        end
        if labels(n1) * (sum(n1) + b) > 1 && a(n1) ~=  0
            break;
        end
        if labels(n1) * (sum(n1) + b) < 1 && a(n1) ~=C
            break;
        end
        n1 = n1 + 1;
    end
    
    
    %n2按照最大化|E1-E2|的原则选取
    E1 = 0;
    E2 = 0;
    maxDiff = 0;%假设的最大误差
    E1 = sum(n1) + b - labels(n1);%n1的误差
    for i = 1 : n
        tempSum = sum(i) + b - labels(i);
        if abs(E1 - tempSum)> maxDiff
            maxDiff = abs(E1 - tempSum);
            n2 = i;
            E2 = tempSum;
        end
    end
    
    
    %以下进行更新
    a1old = a(n1);
    a2old = a(n2);
    KK = W(n1,n1) + W(n2,n2) - 2*W(n1,n2);
    a2new = a2old + labels(n2) *(E1 - E2) / KK;%计算新的a2
    
    %a2必须满足约束条件
    S = labels(n1) * labels(n2);
    if S == -1
        U = max(0,a2old - a1old);
        V = min(C,C - a1old + a2old);
    else
        U = max(0,a1old + a2old - C);
        V = min(C,a1old + a2old);
    end
    if a2new > V
        a2new = V;
    end
    if a2new < U
        a2new = U;
    end
    a1new = a1old + S * (a2old - a2new);%计算新的a1
    a(n1) = a1new;%更新a
    a(n2) = a2new;
    
    %更新部分值
    sum = zeros(n,1);
    for k = 1 : n
        for i = 1 : n
            sum(k) = sum(k) + a(i) * labels(i) * W(i,k);
        end
    end
    Wold = Wnew;
    Wnew = 0;%更新a后的W(a)
    tempSum = 0;%临时变量
    for i = 1 : n
        for j = 1 : n
            tempSum= tempSum + labels(i )*labels(j)*a(i)*a(j)*W(i,j);
        end
        Wnew= Wnew+ a(i);
    end
    Wnew= Wnew - 0.5 * tempSum;
    
    
    %以下更新b：通过找到某一个支持向量来计算
    support = 1;%支持向量坐标初始化
    while abs(a(support))< 1e-4 && support <= n
        support = support + 1;
    end
    b = 1 / labels(support) - sum(support);
    
    
    %判断停止条件
    if abs(Wnew/ Wold - 1 ) <= TOL
        break;
    end
end

%输出结果：包括原分类，辨别函数计算结果，svm分类结果
% for i = 1 : n
%     fprintf('第%d点:原标号 ',i);
%     if i <= 50
%         fprintf('-1');
%     else
%         fprintf(' 1');
%     end
%     fprintf('    判别函数值%f      分类结果',sum(i) + b);
%     if abs(sum(i) + b - 1) < 0.5
%         fprintf('1\n');
%     else if abs(sum(i) + b + 1) < 0.5
%             fprintf('-1\n');
%         else
%             fprintf('归类错误\n');
%         end
%     end
% end

result=zeros(n,1);
%输出结果：包括原分类，辨别函数计算结果，svm分类结果
for i = 1 : n
    if abs(sum(i) + b - 1) < 0.5
        result(i)=2;
    else
        result(i)=1;
    end
end

% labels(labels==-1)=2;
% result(result==-1)=2;

score=nmi(labels,result);