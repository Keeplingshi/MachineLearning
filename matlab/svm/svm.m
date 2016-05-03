function [wt,alpha,b]=svm(dataSet,labels,sigma,C)
% 二分类svm
% 输入  : dataSet       : 数据集
%        labels         ：分类
%        sigma          : 高斯核函数,sigma值
%        C              : 参数，对损失函数的权重
%
% 输出  : alpha : 
%         b：     截距

    [n,m]=size(dataSet);%总样本数量
    
    wt=zeros(1,m);

    TOL = 0.0001;   %精度要求
    b = 0;          %初始设置截距b
    Wnew = 0;       %更新a后的W(a)

    alpha = ones(n,1)*C/2;  %参数a，随机初始化a,a属于[0,C]

    %高斯核函数处理数据
    K=kernelTrans(dataSet,sigma);

    sum=(alpha.*labels)'*K;

    while 1
        %启发式选点，n1,n2代表选择的2个点
        n1 = 1;
        n2 = 2;
        %n1，第一个违反KKT条件的点选择
        while n1 <= n
            if labels(n1) * (sum(n1) + b) == 1 && alpha(n1) >= C && alpha(n1) <=  0
                break;
            end
            if labels(n1) * (sum(n1) + b) > 1 && alpha(n1) ~=  0
                break;
            end
            if labels(n1) * (sum(n1) + b) < 1 && alpha(n1) ~=C
                break;
            end
            n1 = n1 + 1;
        end

        %n2按照最大化|E1-E2|的原则选取
        E2 = 0;
        maxDiff = 0;%假设的最大误差
        E1 = sum(n1) + b - labels(n1);%n1的误差
        for i = 1 : n
            tempW = sum(i) + b - labels(i);
            if abs(E1 - tempW)> maxDiff
                maxDiff = abs(E1 - tempW);
                n2 = i;
                E2 = tempW;
            end
        end

        %以下进行更新
        a1old = alpha(n1);
        a2old = alpha(n2);
        KK = K(n1,n1) + K(n2,n2) - 2*K(n1,n2);
        a2new = a2old + labels(n2) *(E1 - E2) / KK;

        yy=labels(n1) * labels(n2);
        if yy==-1
            L=max(0,a2old - a1old);
            H=min(C,C + a2old - a1old );
        else
            L=max(0,a1old + a2old - C);
            H=min(C,a1old + a2old);
        end

        a2new=min(a2new,H);
        a2new=max(a2new,L);
        a1new = a1old + yy * (a2old - a2new);
        wt=wt+labels(n1)*(a1new-a1old)*dataSet(i,:)+labels(n2)*(a2new-a2old)*dataSet(n2,:);
%         ai_new = a(i) + y(i) * y(j) * (a(j) - aj_clip);
%        w = w + y(i) * (ai_new - a(i)) * x(i,:) + y(j) * (aj_clip - a(j)) * x(j,:);

        %更新a
        alpha(n1) = a1new;
        alpha(n2) = a2new;

        %更新Ei和b
        sum=(alpha.*labels)'*K;

        Wold = Wnew;
        Wnew = 0;%更新a后的W(a)
        tempW=0;
        for i = 1 : n
            for j = 1 : n
                tempW= tempW + labels(i )*labels(j)*alpha(i)*alpha(j)*K(i,j);
            end
            Wnew= Wnew+ alpha(i);
        end
        Wnew= Wnew - tempW/2;

        %以下更新b：通过找到某一个支持向量来计算
        bold=b;
        if a1new>=0 && a1new<=C
            b=(a1old-a1new)*labels(n1)*K(n1,n1)+(a2old-a2new)*labels(n2)*K(n2,n1)-E1+bold;
        elseif a2new>=0 && a2new<=C
            b=(a1old-a1new)*labels(n1)*K(n1,n2)+(a2old-a2new)*labels(n2)*K(n2,n2)-E2+bold;
        else      % (a1new<0||a1new>C)&&(a2new<0||a2new>C)
            b1=(a1old-a1new)*labels(n1)*K(n1,n1)+(a2old-a2new)*labels(n2)*K(n2,n1)-E1+bold;
            b2=(a1old-a1new)*labels(n1)*K(n1,n2)+(a2old-a2new)*labels(n2)*K(n2,n2)-E2+bold;
            b=(b1+b2)/2;
        end

        %判断停止条件
        if abs(Wnew/ Wold - 1 ) <= TOL
            break;
        end
    end

end

function K = kernelTrans(dataSet,sigma)
    K=pdist(dataSet);
    K=squareform(K);
    K = -K.^2/(2*sigma*sigma);
    K=exp(K);
end  
