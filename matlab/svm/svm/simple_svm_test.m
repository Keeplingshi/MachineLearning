clc;
clear;

load Data

[r,c] = size(Data);
Test = Data(:,1:2);
Label = Data(:,3);

[b, alphas] = smoSimple(Test, Label, 0.6, 0.001, 40);
 
%%画图
figure(1)
axis([-2 12 -8 6])
for k = 1:1:r
    hold on
    if Data(k,3) == 1
        plot(Data(k,1),Data(k,2),'r+');
    else
        plot(Data(k,1),Data(k,2),'b*');
    end
end

%画支持向量及分割面
%result=[];
for k=1:1:r
    if alphas(k)~= 0
        hold on
        %result =[result;alphas(k)];
        QX = plot(Data(k,1:1),Data(k,2:2),'Ok','MarkerSize',12);
        set(QX,'LineWidth',2.0);
    end
end
W=(alphas.*Label)'*Data(:,1:2);
y=(-W(1).* Data(:,1:1)-b) ./W(2);
plot(Data(:,1:1),y);