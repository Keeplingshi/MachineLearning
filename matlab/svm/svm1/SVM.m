function [ w,b,a ] = SVM( x, y, sigma )
%SVM Summary of this function goes here
%  x为原始数据，行为条数，列为维度
%  y为对应的分类信息，列向量，取值1或-1
%  返回w为超平面的X系数
%  返回b为超平面常数项

C = inf;
[m,n] = size(x);
K = PolynomialKernel(x,1,3);
a = zeros(m,1);
w = zeros(1,n);
b=0;
err = -y;
isLoopAll = true;
loopCount =0;

while loopCount < 30
    loopCount = loopCount + 1;
    isFinish = true;
    %第一个不满足KKT条件的作为第一个a
    for i=1:m
        ui = err(i) + y(i) +b;
        if ~(a(i)==0 && y(i)*ui>=1-sigma  ||  a(i)>0 && a(i)<C && y(i)*ui>=1-sigma && y(i)*ui<=1+sigma  ||  a(i)==C && y(i)*ui<=1+sigma)
            isFinish = false;
            %使|Ei-Ej|最大的作为第二个a
            if err(i) >= 0
                [~,index] = min(err);
                j = index(1);
            else
                [~,index] = max(err);
                j = index(1);
            end
            %计算aj上下边界H、L
            if(y(i) == y(j))
                L = max(0,a(i)+a(j)-C);
                H = min(C,a(i)+a(j));
            else
                L = max(0,a(j)-a(i));
                H = min(C,a(j)-a(i)+C);
            end
            if(L==H)
                continue;
            end
            % eta等于K11 + K22 - 2K12
            eta = (K(i,i)+K(j,j)-2*K(i,j));
            if(eta==0)
                continue;
            end
            % 计算新的aj
            aj_new = a(j) + y(j) * (err(i) - err(j)) / eta;
            % 按边界裁剪aj
            if(aj_new < L)
                aj_clip = L;
            elseif(aj_new > H)
                aj_clip = H;
            else
                aj_clip = aj_new;
            end
            if(abs(aj_clip - a(j)) <= 0.00001)
                continue;
            end
            ai_new = a(i) + y(i) * y(j) * (a(j) - aj_clip);
            w = w + y(i) * (ai_new - a(i)) * x(i,:) + y(j) * (aj_clip - a(j)) * x(j,:);
            % 计算b
            b1 = b - err(i) - y(i)*(ai_new - a(i))*K(i,i) - y(j)*(aj_clip - a(j))*K(i,j);
            b2 = b - err(j) - y(i)*(ai_new - a(i))*K(i,j) - y(j)*(aj_clip - a(j))*K(j,j);
            if(ai_new > 0 && ai_new < C)
                b = b1;
            elseif(aj_clip > 0 && aj_clip < C)
                b = b2;
            else
                b = (b1+b2) / 2;
            end

            a(i) = ai_new;
            a(j) = aj_clip;
            err = (w*x')'-y;
        end
    end
    if(isFinish)
        break;
    end
    
end

end

