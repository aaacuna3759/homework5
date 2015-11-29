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
		dotProduct = sum(trainData(j,:) .* testData(i,:));
		d1 = sqrt( sum(trainData(j,:).^2) );
		d2 = sqrt( sum(testData(i,:).^2) );
		similarity(j) = dotProduct / (d1 * d2);
    end
        
    % sort distances in descending order and retrieve the sorted 
    % values and their index 
    [~,I] = sort(similarity, 'descend');

    % get the first k indices
    kNN_indx = I(1:k); 

    % count how many records are regular and how many are spam
    % indices 1-101 are regular and 102-201 are spam
    for m = 1:k
        if (kNN_indx(m) <= 101)
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

    % calculate number of correct classifications
    % indices 1-101 are regular and 102-201 are spam
    if (class == 0) && (i <= 101)
        correct = correct + 1;
    elseif (class == 1) && (i > 101)
        correct = correct + 1;
    end
    
end

% display classification accuracy
accuracy = correct / size(testData, 1)

end
