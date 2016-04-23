tempSum=zeros(n,1);
for i = 1 : n
	tempSum= tempSum + (labels.*a)*(labels.*a).*K(:,i);
end

zz=0;
for j=1:n
   zz=zz+tempSum(j); 
end


% tempSum=zeros(n,1);
% for i = 1 : n
% 	tempSum= tempSum + labels(i)*a(i)*labels.*a.*K(:,i);
% end
% 
% zz=0;
% for j=1:n
%    zz=zz+tempSum(j); 
% end
