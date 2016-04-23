clear;
load('data.mat');
pos = find(y == 1); 
neg = find(y == -1);
plot(X(pos, 1), X(pos, 2), 'k+');
hold on;
plot(X(neg, 1), X(neg, 2), 'ko');
hold on; 

C = 1;m = size(X,1);
%%kernel
K=X*X';

one = ones(m,1);
alpha = zeros(m,1);
cvx_begin;
    variables alpha(m);
    maximize(one'*alpha - 1/2*(alpha.*y)'*K*(alpha.*y) );
    subject to;
    0 <= alpha <= C;
    alpha'*y == 0;
cvx_end;

ind = find(alpha>=C*0.999999);
ind = ind(1);

%KKT conditions
b = y(ind) - alpha'*(y.*K(:,ind));
w = ((alpha.*y)'*X)';

x1 = linspace(min(X(:,1)), max(X(:,1)), 100);
%w(1)*x1+w(2)*x2+b=0
x2 = - (w(1)*x1 + b)/w(2);
plot(x1, x2, '-r'); 

ind = find(alpha>=C*0.999999 & alpha<=C & y == 1);
ind = ind(1);
b = -alpha'*(y.*K(:,ind));
x2 = - (w(1)*x1 + b)/w(2);
plot(x1, x2, '-b'); 

ind = find(alpha>=C*0.999999 & alpha<=C & y == -1);
ind = ind(1);
b = -alpha'*(y.*K(:,ind));
x2 = - (w(1)*x1 + b)/w(2);
plot(x1, x2, '-g'); 

pos_sv = find(alpha>=C*0.9999999 & y==1);
plot(X(pos_sv, 1), X(pos_sv, 2), 'ro');

pos_neg = find(alpha>=C*0.9999999 & y==-1);
plot(X(pos_neg, 1), X(pos_neg, 2), 'r+');

hold off;