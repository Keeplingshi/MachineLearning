function [b, res_alphas] = smoP(data, class, C, toler, maxIter)
    [m,n] = size(data);
    iter = 0;
    entireSet = 1;
    alphaPairsChanged = 0;
    oS = init(data,class,C,toler,m);
        
    while(((iter<maxIter)&&(alphaPairsChanged > 0)) || (entireSet == 1))
        alphaPairsChanged = 0;
        if entireSet == 1
            for k = 1:1:m
                [ret, oS] = innerL(k, oS);
                alphaPairsChanged = alphaPairsChanged + ret;
            end
            iter = iter + 1;
        else
            nonBoundIs = [];
            for k = 1:1:m
               if ((oS.alphas(k) < C) && (oS.alphas(k) > 0))
                   nonBoundIs = [nonBoundIs k];
               end
            end
            [r,c] = size(nonBoundIs);
            for k = 1:1:c
                index = nonBoundIs(k);
                [ret, oS] = innerL(index, oS);
                alphaPairsChanged = alphaPairsChanged + ret;
            end
            iter = iter + 1;
        end
        if entireSet == 1
            entireSet = 0;
        elseif alphaPairsChanged == 0
            entireSet = 1;
        end
    end
    b = oS.b;
    res_alphas = oS.alphas;
end

function oS = init(data,class,C,toler,m)
    alphas = zeros(m,1);
    eCache = zeros(m,2);
    b = 0;
    
    oS.data = data;
    oS.class = class;
    oS.C = C;
    oS.toler = toler;
    oS.m = m;
    oS.alphas = alphas;
    oS.b = b;
    oS.eCache = eCache;
    
end

function [ret,oS] = innerL(k, oS)
    Ei = calcEk(oS, k);
    if(((oS.class(k)*Ei < -oS.toler) && (oS.alphas(k) < oS.C)) || ((oS.class(k)*Ei > oS.toler) && (oS.alphas(k) > 0)))
        [j, Ej] = selectJ(k, oS, Ei);
        temp_k = oS.alphas(k);
        temp_j = oS.alphas(j);
        
        if oS.class(k) ~= oS.class(j)
            L = max(0, oS.alphas(j) - oS.alphas(k));
            H = min(oS.C, oS.C +oS.alphas(j) - oS.alphas(k));
        else
            L = max(0, oS.alphas(j) + oS.alphas(k) - oS.C);
            H = min(oS.C, oS.alphas(j) + oS.alphas(k));
        end
        if L == H
            ret = 0;
            return;
        end
        eta = 2.0 * oS.data(k,:) * oS.data(j,:)' - oS.data(k,:) * oS.data(k,:)' - oS.data(j,:) * oS.data(j,:)';
        if eta >=0 
            ret = 0;
            return;
        end
        oS.alphas(j) = oS.alphas(j) - oS.class(j) * (Ei - Ej) / eta;
        oS.alphas(j) = clipalpha(oS.alphas(j), H, L);
        
        %update Ek
        Et = calcEk(oS, j);
        oS.eCache(j,:) = [1 Et];
        
        if(abs(oS.alphas(j) - temp_j) < 0.00001)
            ret = 0;
            return;
        end
        
        oS.alphas(k) =   oS.alphas(k) + oS.class(j)*oS.class(k)*(temp_j - oS.alphas(j));
        Et = calcEk(oS, k);
        oS.eCache(k,:) = [1 Et];
        
        b1 = oS.b - Ei - oS.class(k) * (oS.alphas(k) - temp_k) * oS.data(k,:) * oS.data(k,:)' - oS.class(j) * (oS.alphas(j) - temp_j) * oS.data(k,:) * oS.data(j,:)';
        b2 = oS.b - Ej - oS.class(k) * (oS.alphas(k) - temp_k) * oS.data(k,:) * oS.data(j,:)' - oS.class(j) * (oS.alphas(j) - temp_j) * oS.data(j,:) * oS.data(j,:)'; 
        
        if (oS.alphas(k)>0) && (oS.alphas(k)<oS.C)
            oS.b = b1;
        elseif(oS.alphas(j)>0) && (oS.alphas(j)<oS.C)
            oS.b = b2;
        else
            oS.b = (b1+b2)/2.0;
        end
        ret = 1;
        return;
    else
        ret = 0;
        return;
    end
end

function Ek = calcEk(oS, k)
    fXk = (oS.alphas .* oS.class)' * oS.data * oS.data(k,:)' + oS.b;
    Ek = fXk - oS.class(k);
end

function [j, Ej] = selectJ(k, oS, Ei)
    maxK = -1;
    maxDeltaE = 0; 
    Ej = 0;
    oS.eCache(k,:) =[1 Ei];
    validEcacheList = [];
    
    for l = 1:1:oS.m
        if oS.eCache(l,1:1) ~= 0
            validEcacheList = [validEcacheList l];
        end
    end
    [r, c] = size(validEcacheList);
    if c > 1
        for l=1:1:c
            index = validEcacheList(l)
            if index == k
                continue;
            end
            Ek = calcEk(oS,index);
            deltaE = abs(Ei - Ek);
            if(deltaE > maxDeltaE)
                maxK = index;
                maxDeltaE = deltaE;
                Ej = Ek;
            end
        end
        j = maxK;
    else
        j = selectJrand(k, oS.m);
        Ej = calcEk(oS, j);
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
