k=4;
[centroids,clusterAssment,dataSet]=kMeans('testSet.txt',k);
[m,n]=size(dataSet);

for i=1:m
	if clusterAssment(i, 1)==1
        plot(dataSet(i,1),dataSet(i,2),'bo');
        hold on;
    elseif clusterAssment(i, 1)==2
    	plot(dataSet(i,1),dataSet(i,2),'ro');
        hold on;
    elseif clusterAssment(i, 1)==3
    	plot(dataSet(i,1),dataSet(i,2),'go');
        hold on;
    else 
        plot(dataSet(i,1),dataSet(i,2),'mo');
        hold on;
	end
end

plot(centroids(:,1),centroids(:,2),'kX','MarkerSize',12);