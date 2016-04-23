function [b,alphas] = smoSimple(data, class, C, toler, maxIter)
b = 0;
[m,~] = size(data);
alphas = zeros(m,1);
iter=0;
while (iter < maxIter)
    alphasChanges = 0;
    for k=1:1:m
        fxk = (alphas .* class)' * data * data(k,:)' + b;   % f = wx+b
        ek = fxk - class(k);
        if (((ek*class(k) < -toler) && (alphas(k) < C)) || ((ek*class(k) > toler) && (alphas(k) > 0)))
            j = selectJrand(k,m);
            fxj = (alphas .* class)' * data * data(j,:)' + b;   % f = wx+b
            ej = fxj - class(j);
            
            temp_k = alphas(k);
            temp_j = alphas(j);
            if(class(k) ~= class(j))
                L = max(0, alphas(j) - alphas(k));
                H = min(C, C + alphas(j) - alphas(k));
            else
                L = max(0, alphas(k) + alphas(j) - C);
                H = min(C, alphas(k) + alphas(j));
            end
            if L == H
                continue;
            end
            eta = 2.0 * data(k,:) * data(j,:)' - data(k,:) * data(k,:)' - data(j,:) * data(j,:)';
            if eta >= 0
                continue;
            end
            alphas(j) = alphas(j) - class(j) * (ek - ej) / eta;
            alphas(j) = clipalpha(alphas(j), H, L);
            
            if(abs(alphas(j) - temp_j) < 0.00001)
                continue;
            end
            
            alphas(k) = alphas(k) + class(k) * class(j) * (temp_j - alphas(j));
            b1 = b - ek - class(k) * (alphas(k) - temp_k) * data(k,:) * data(k,:)' - class(j) * (alphas(j) - temp_j) * data(k,:) * data(j,:)';
            b2 = b - ej - class(k) * (alphas(k) - temp_k) * data(k,:) * data(j,:)' - class(j) * (alphas(j) - temp_j) * data(j,:) * data(j,:)'; 
            
            if (alphas(k) > 0 && alphas(k) < C)
                b = b1;
            elseif(alphas(j) > 0 && alphas(j) < C)
                b = b2;
            else
                b = (b1 + b2)/2;
            end
            alphasChanges = alphasChanges + 1;
        end 
    end
    if alphasChanges == 0
        iter = iter + 1;
    else
        iter = 0;
    end
end
end

function index = selectJrand(k,m)
    index = k;
    while(index == k)
        index = randi([1,m],1,1);  
    end
end

function res = clipalpha(a, H, L)
    if a > H
        a = H;
    end
    
    if a < L
        a = L;
    end
    res = a;
end