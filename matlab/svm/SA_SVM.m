function [predicted_label,Accuacy,Sensitivity,Specificity]=SA_SVM(trainlabel,traindata,testlabel,testdata,varargin)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%   classifier based on "Self-advising support vectore machine, Yashar Maali, Adel Al-Jumaily,Knowledge-Based Systems 52 (2013) 214–222"
%   Traindata is a numeric matrix of predictor data. Rows of traindata correspond to observations; 
%   columns correspond to features. Trainlabel is a column vector that contains the known class 
%   labels (1 or 0) for traindata. Each element of trainlabel specifies the group the corresponding
%   row of traindata belongs to traindata and trainlabel must have the same number of rows. 
%   Testdata is a numeric matrix of predictor data. Rows of testdata correspond to observations; 
%   columns correspond to features.Testlabel is a column vector that contains the known class 
%   labels (1 or 0) for testdata. Each element of testlabel specifies the group the corresponding
%   row of testdata belongs to. Testdata and testlabel must have the same number of rows. 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Usage: [predicted,Acc,Sen,Spec]= SA_SVM(training_label_vector, training_instance_matrix,test_label_vector, test_instance_matrix, s,t,d,g,c,n);
% Parameters:
% -s svm_type : set type of SVM (default 0)
% 	0 -- C-SVC		(binary-class classification)
% 	1 -- nu-SVC		(binary-class classification)
% -t kernel_type : set type of kernel function (default 2)
% 	0 -- linear: u'*v
% 	1 -- polynomial: (gamma*u'*v + coef0)^degree
% 	2 -- radial basis function: exp(-gamma*|u-v|^2)
% 	3 -- sigmoid: tanh(gamma*u'*v + coef0)
% -d degree : set degree in kernel function (default 3)
% -g gamma : set gamma in kernel function (default 1/num_features)
% -c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)
% -n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)
% Returns:
%   predicted_label: SA_SVM prediction output vector.
%   Accuacy:         Accuracy of classification
%   Sensitivity:     Sensitivity of classification
%   Specificity:     Specificity of classification
% Important note: You need to have LIBSVM package to be able to run this code properly
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
switch length(varargin)
    case 0
        SVMtype=0; kernel=2;d=3; gamma=1/size(traindata,2);c=1;n=0.5;
    case 1
        SVMtype=varargin{1};kernel=2;d=3; gamma=1/size(traindata,2);c=1;n=0.5;
    case 2
       SVMtype=varargin{1}; kernel=varargin{2}; d=3; gamma=1/size(traindata,2);c=1;n=0.5;
    case 3
        SVMtype=varargin{1}; kernel=varargin{2}; d=varargin{3}; gamma=1/size(traindata,2);c=1;n=0.5;
    case 4
         SVMtype=varargin{1}; kernel=varargin{2}; d=varargin{3};gamma=varargin{4}; c=1;n=0.5;
    case 5
        SVMtype=varargin{1}; kernel=varargin{2}; d=varargin{3};gamma=varargin{4}; c=varargin{5};n=0.5;
    case 6
        SVMtype=varargin{1}; kernel=varargin{2}; d=varargin{3};gamma=varargin{4}; c=varargin{5};n=varargin{6};
    otherwise
         error('Wrong number of inputs')     
end;
%%
if   SVMtype~=0 && SVMtype~=1
    error('Wrong SVM type is chosed')
end;
if   kernel~=0 && kernel~=1 && kernel~=2 && kernel~=3
    error('Wrong kernel type is chosed')
end;
%%
param = ['-s ', num2str(SVMtype), ' -t ',num2str(kernel), ' -d ',num2str(d), ' -g ', num2str(gamma), ' -c ',num2str(c), '-n ', num2str(n)];
m=svmtrain(trainlabel,traindata,param);
[result, ~,PER]=svmpredict(testlabel,testdata,m);
NewResult=result;
[mdata,mislabel,mper,~,cdata]=missdata(traindata,trainlabel,m);
if ~isempty(cdata)  %if SVM can solve the classification problem
    o=closetest(cdata,testdata,mdata,mislabel,c,d,gamma,kernel);
    if ~isempty(o)  %if there is some test data close to miss data
        [b,~]=unique(o(:,1));   % test data with neighbour in miss data
        Sneighb=length(b);
        for i=1:Sneighb
            bb=find(o(:,1)==b(i));     
            S1=0;S2=0;
            for j=1:length(bb)
                if o(bb(j),3)==1
                    S1=S1+(abs(mper(o(bb(j),2))))/(1+o(bb(j),4)^2);
                elseif o(bb(j),3)==0
                    S2=S2+(abs(mper(o(bb(j),2))))/(1+o(bb(j),4)^2);
                end;
            end;
            if S1>S2
                S=S1;
            else
                S=S2;
            end;
            if S>=abs(PER(o(bb(j),1)))
                NewResult(o(bb(j),1))=o(bb(j),3);
            end;
        end;
    end;
    predicted_label=NewResult;
       
else %SVM can't solve the problem
    predicted_label=[];
    
end
cp = classperf(testlabel,predicted_label);
Accuacy=cp.CorrectRate;
Sensitivity=cp.Sensitivity;
Specificity=cp.Specificity;
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function [mdata,mislabel,mper,mIndex,cdata]=missdata(data,label,m)
 %This function find data that has wrong label from training of SVM 
 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    
 mdata=[];
 mislabel=[];
 mper=0;
 mIndex=[];
 cdata=[];
 clabel=[];
 [result,~,per]=svmpredict(label,data,m);
 j=1;
k=1;
 for i=1:length(result)
     if result(i)~=label(i)
         mdata(j,:)=data(i,:);
         mislabel(j)=label(i);
         mper(j)=per(i);
         mIndex(j)=i;
         j=j+1;
     else
         cdata(k,:)=data(i,:);
         k=k+1;
     end;
 end;
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

function o=closetest(cdata,testdata,mdata,mislabel,best_C,best_d,best_gamma,best_kernel)
%This function return the test data close to the miss data
 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
o=[];
for i=1:size(mdata,1)
    region(i)=InsurenceRegion(mdata(i,:),cdata,best_C,best_d,best_gamma,best_kernel);
end
k=1;
for i=1:size(testdata,1)
    for j=1:size(mdata,1)
        dis=(kernelmap(mdata(j,:),mdata(j,:),best_C,best_d,best_gamma,best_kernel)+kernelmap(testdata(i,:),testdata(i,:),best_C,best_d,best_gamma,best_kernel)-2*kernelmap(mdata(j,:),testdata(i,:),best_C,best_d,best_gamma,best_kernel))^0.5;
        if dis<=region(j);
            o(k,1)=i;    %index of test data
            o(k,2)=j;    %index of miss data 
            o(k,3)=mislabel(j); %labell of miss data
            o(k,4)=dis;         %distance of test data and miss data 
            o(k,5)=region(j);   %insurence region of miss data 
            k=k+1;
        end;
    end;
end;
end
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
function out=InsurenceRegion(x,cdata,best_C,best_d,best_gamma,best_kernel)
%This function compute Insurence Region
 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
for i=1:size(cdata,1)
    dis(i)=(kernelmap(x,x,best_C,best_d,best_gamma,best_kernel)+kernelmap(cdata(i,:),cdata(i,:),best_C,best_d,best_gamma,best_kernel)-2*kernelmap(x,cdata(i,:),best_C,best_d,best_gamma,best_kernel))^0.5;
end;
out=(min(dis(dis>0)))/2;
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 

function out=kernelmap(x,y,best_C,best_d,best_gamma,best_kernel)
%This function map x and y by attention to the kernel type
 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
gamma=best_gamma;
d=best_d;
c=best_C;
if size(x,1)>1
    x=x';
end;
if size(y,2)~=1
    y=y';
end;

if best_kernel==1
    out=(gamma*x*y+c)^d;
elseif best_kernel==0
    out=x*y;
elseif best_kernel==2
    out=exp(-gamma*(x*x'+y'*y-2*x*y));
elseif best_kernel==3
    out=tanh(gamma*x*y + c);
end;
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 