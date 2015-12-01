function knnAccuracy (trainData, testData, k)
% initialize correct count variable
correct = 0;

for i=1:size(testData, 1)
    % initialize variables
    similarity = zeros(1, size(testData, 1));
    reg_count = 0;
	spam_count = 0;
    
    % calculate the cosine similarity for test point with 
    % each record in trainData and store in similarity vector.
	for j=1:size(trainData, 1)
		dotProduct = sum(trainData(j,1:size(trainData,2)-1).*testData(i,1:size(testData,2)-1));
		d1 = sqrt( sum(trainData(j,1:size(trainData,2)).^2) );
		d2 = sqrt( sum(testData(i,1:size(testData,2)).^2) );
		similarity(j) = dotProduct / (d1 * d2);
    end
        
    % sort distances in descending order and retrieve the sorted 
    % values and their index 
    [~,I] = sort(similarity, 'descend');

    % get the first k indices
    kNN_indx = I(1:k); 

    % count how many records are regular and how many are spam
    for m = 1:k
        if (trainData(kNN_indx(m),size(trainData,2)) == 0)
            reg_count = reg_count + 1;
        else 
            spam_count = spam_count + 1;
        end
    end
    
    % determine class of test point: 0 is regular and 1 is spam
    if (reg_count >= spam_count)
        class = 0;
    else
        class = 1;
    end
    
    % determine whether classification was accurate
    if (testData(i, size(testData,2)) == class)
        correct = correct + 1;
    end
    
end

% display total classification accuracy
accuracy = correct / size(testData, 1)

end
