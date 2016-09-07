clear;%清空变量
clc;%清空命令窗口
a=sin(pi/2);
%who 命令显示所有已经使用的变量名
disp('who 输出信息：');
who
%whos 命令显示多一点有关变量的相关信息
disp('whos 输出信息：');
whos

%默认情况下，MATLAB 四个小数位值显示数字。这就是所谓的 short format.但是，如果想更精确，需要使用 format 命令。
%长(long ) 命令格式显示小数点后16位。
format long
x = 7 + 10/3 + 5 ^ 1.2;
x
%short命令格式显示小数点后4位。
format short
x = 7 + 10/3 + 5 ^ 1.2;
x