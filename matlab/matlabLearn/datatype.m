clear;%清空变量
clc;%清空命令窗口

str = 'Hello World!';
n = 2345;
d = double(n);
un = uint32(789.50);
rn = 5678.92347;
c = int32(rn);

%判断x是否为某一数据类型，类似还有很多判断函数，这里不一一列举
x = 23.54;
isinteger(x);      % 是否为整数 返回结果 =0
isfloat(x);     % 是否为浮点型 返回结果 =1
