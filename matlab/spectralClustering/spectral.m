function C = spectral(W, k)  
	[m,~]=size(W);
    s = sum(W);
    D = full(sparse(1:m, 1:m, s));
    %L = D - W;
    %[Q, ~] = eigs(L, k, 'SA');
    E = D^(-1/2)*W*D^(-1/2);  
    [Q, ~] = eigs(E, k);  
    C = kmeans(Q, k);  
end  