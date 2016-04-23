function K = kernelTrans(X, A, k1)
    [m, n] = size(X);
    K = zeros(m,1);
    for j = 1:1:m
        deltaRow = X(j,:) - A;
        K(j) = deltaRow * deltaRow';
    end
    K = exp(K./(-2*k1));
end