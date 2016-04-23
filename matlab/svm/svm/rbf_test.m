clc;
clear;

load NData
load NTest

Data = ndata;
Data_Test = ntest;
[r,c] = size(Data);
Test = Data(:,1:2);
Label = Data(:,3);

[b, alphas] = rbf_smoP(Test, Label, 200, 0.0001, 1000,1.3);

%%画图
figure(1)
axis([-1.5 1.5 -1.5 1.5])
for k = 1:1:r
    hold on
    if Data(k,3) == 1
        plot(Data(k,1),Data(k,2),'r+');
    else
        plot(Data(k,1),Data(k,2),'b*');
    end
end
%%画支持向量
support_vector = [];
lable_sv = [];
alphas_sv = [];
for k=1:1:r
    if alphas(k)~= 0
        hold on
        support_vector = [support_vector; Test(k,1:2)];
        lable_sv = [lable_sv Label(k)];
        alphas_sv = [alphas_sv alphas(k)];
        %result =[result;alphas(k)];
        QX = plot(Data(k,1:1),Data(k,2:2),'Ok','MarkerSize',12);
        set(QX,'LineWidth',2.0);
    end
end
%%预测
temp = lable_sv .* alphas_sv;
[m, n] = size(Data_Test);
errorCount = 0;
for k = 1:1:m
    value = kernelTrans(support_vector, Data_Test(k,1:2),1.3);
    predict = temp * value + b;
    if predict > 0
        predict = 1;
    else
        predict = -1;
    end
    if predict ~= Data_Test(k,3:3)
        errorCount = errorCount + 1;
    end
end
errorCount
