clear;%清屏
clc;
X =load('data.txt');
n = length(X);%总样本数量
y = X(:,4);%类别标志
X = X(:,1:3);
TOL = 0.0001;%精度要求
C = 1;%参数，对损失函数的权重
b = 0;%初始设置截距b
Wold = 0;%未更新a时的W(a)
Wnew = 0;%更新a后的W(a)
for i = 1 : 50%设置类别标志为1或者-1
    y(i) = -1;
end
a = zeros(n,1);%参数a
for i = 1 : n%随机初始化a,a属于[0,C]
        a(i) = 0.2;
end

%为简化计算，减少重复计算进行的计算
K = ones(n,n);
for i = 1 :n%求出K矩阵，便于之后的计算
    for j = 1 : n
        K(i,j) = k(X(i,:),X(j,:));
    end
end
sum = zeros(n,1);%中间变量，便于之后的计算，sum(k)=sigma a(i)*y(i)*K(k,i);
for k = 1 : n
    for i = 1 : n
        sum(k) = sum(k) + a(i) * y(i) * K(i,k);
    end
end

while 1%迭代过程
    
%启发式选点
n1 = 1;%初始化，n1,n2代表选择的2个点
n2 = 2;
%n1按照第一个违反KKT条件的点选择
while n1 <= n
    if y(n1) * (sum(n1) + b) == 1 && a(n1) >= C && a(n1) <=  0
         break;
    end
    if y(n1) * (sum(n1) + b) > 1 && a(n1) ~=  0
           break;
    end
    if y(n1) * (sum(n1) + b) < 1 && a(n1) ~=C
          break;
    end
     n1 = n1 + 1;              
end
%n2按照最大化|E1-E2|的原则选取
E1 = 0;
E2 = 0;
maxDiff = 0;%假设的最大误差
E1 = sum(n1) + b - y(n1);%n1的误差
for i = 1 : n
    tempSum = sum(i) + b - y(i);
    if abs(E1 - tempSum)> maxDiff
        maxDiff = abs(E1 - tempSum);
        n2 = i;
        E2 = tempSum;
    end
end

%以下进行更新
a1old = a(n1);
a2old = a(n2);
KK = K(n1,n1) + K(n2,n2) - 2*K(n1,n2);
a2new = a2old + y(n2) *(E1 - E2) / KK;%计算新的a2
%a2必须满足约束条件
S = y(n1) * y(n2);
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
        sum(k) = sum(k) + a(i) * y(i) * K(i,k);
    end
end
Wold = Wnew;
Wnew = 0;%更新a后的W(a)
tempSum = 0;%临时变量
for i = 1 : n
    for j = 1 : n
    tempSum= tempSum + y(i )*y(j)*a(i)*a(j)*K(i,j);
    end
    Wnew= Wnew+ a(i);
end
Wnew= Wnew - 0.5 * tempSum;
%以下更新b：通过找到某一个支持向量来计算
support = 1;%支持向量坐标初始化
while abs(a(support))< 1e-4 && support <= n
    support = support + 1;
end
b = 1 / y(support) - sum(support);
%判断停止条件
if abs(Wnew/ Wold - 1 ) <= TOL
    break;
end
end
%输出结果：包括原分类，辨别函数计算结果，svm分类结果
for i = 1 : n
    fprintf('第%d点:原标号 ',i);
    if i <= 50
        fprintf('-1');
    else
        fprintf(' 1');
    end
    fprintf('    判别函数值%f      分类结果',sum(i) + b);
    if abs(sum(i) + b - 1) < 0.5
        fprintf('1\n');
    else if abs(sum(i) + b + 1) < 0.5
            fprintf('-1\n');
        else
            fprintf('归类错误\n');
        end
    end
end
