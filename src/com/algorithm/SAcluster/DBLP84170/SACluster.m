k = 10;
PRUNE_SIZE = 0.0001;
Restart = 0.15;
Sigma = 0.005;
Gama = 0.005;
VertexSize = 1920;
AttributeSize = 4;
AttributeValueSize1 = 169;
AttributeValueSize2 = 17;
AttributeValueSize3 = 11;
AttributeValueSize4 = 6;
AttributeValueSize = AttributeValueSize1 + AttributeValueSize2 + AttributeValueSize3 + AttributeValueSize4;
MatrixDimension = VertexSize + AttributeValueSize;
Attribute1StartDimension = VertexSize + 1;
Attribute2StartDimension = Attribute1StartDimension + AttributeValueSize1;
Attribute3StartDimension = Attribute2StartDimension + AttributeValueSize2;
Attribute4StartDimension = Attribute3StartDimension + AttributeValueSize4;
WeightOld1 = 2;
WeightOld2 = 2;
WeightOld3 = 2;
WeightOld4 = 2;
WeightSum = WeightOld1 + WeightOld2 + WeightOld3 + WeightOld4;
IncrementFactor = 1.75;
OuterIterationTime = 1;
InnerIterationTime = 10;



tic;
load Dataset.txt;
Transition = spconvert(Dataset);
clear Dataset;

load DataAttribute.txt;
Attribute = spconvert(DataAttribute);
clear DataAttribute;


Factor = (1.0 - Restart) * Transition;
clear Transition;
PruneSize = PRUNE_SIZE;
Value = logical(Factor > 0.0 & Factor <= PruneSize / Restart);
Factor(Value) = 0.0;
clear Value;
Factor = sparse(Factor);
Iteration = Restart * Factor;
RandomWalk = Iteration;
PruneSize = PruneSize / IncrementFactor;

for i = 2 : InnerIterationTime
    Iteration = Iteration * Factor;
	Value = logical(Iteration > 0.0 & Iteration <= PruneSize);
	Iteration(Value) = 0.0;
	clear Value;
    Iteration = sparse(Iteration);
    PruneSize = PruneSize / IncrementFactor;
	RandomWalk = RandomWalk + Iteration;
end;
clear Iteration;



load Dataset.txt;
Transition = spconvert(Dataset);
clear Dataset;

Density = zeros(VertexSize, 1);
for i = 1 : VertexSize
    Density(i, 1) = nnz(Transition(i, 1 : VertexSize));   
end;

CenterLabel = zeros(k, 1);
[Density, Index] = sortrows(Density);
for i = 1 : k
	CenterLabel(i, 1) = Index(VertexSize + 1 - i);
end;
clear Density;
clear Index;



AssignmentLast = zeros(VertexSize, 1);
CenterMatrix = zeros(VertexSize, k);
AttributeValueDistribution1 = zeros(AttributeValueSize1, 1);
AttributeValueDistribution2 = zeros(AttributeValueSize2, 1);
AttributeValueDistribution3 = zeros(AttributeValueSize3, 1);
AttributeValueDistribution4 = zeros(AttributeValueSize4, 1);
while (true)
    CenterMatrix = RandomWalk(1 : VertexSize, CenterLabel);
	[MaxSimilarity, AssignmentCurrent] = max(CenterMatrix, [], 2);
    clear MaxSimilarity;
    for i = 1 : k
        AssignmentCurrent(CenterLabel(i, 1)) = i;
    end;
       
    if((nnz(AssignmentCurrent - AssignmentLast) / VertexSize) < Sigma)
        TimeElapsed(OuterIterationTime) = toc;
        FileName = 'Runtime.txt';
        if(OuterIterationTime == 1)
            Fid = fopen(FileName, 'wt');
        else
            Fid = fopen(FileName, 'at');
        end;
        fprintf(Fid, '%d    %8.6f\n', OuterIterationTime, TimeElapsed(OuterIterationTime));
        fclose(Fid);
        break;
    end;
 	AssignmentLast = AssignmentCurrent;

    
	AttributeContribution1 = 0;
	AttributeContribution2 = 0;   
    AttributeContribution3 = 0;
    AttributeContribution4 = 0;   
 	for i = 1 : k
		Cluster = RandomWalk(AssignmentCurrent == i, AssignmentCurrent == i);
		RandomWalkIndex = find(AssignmentCurrent == i);
		ClusterSize = size(RandomWalkIndex, 1);
        
        Similarity = zeros(ClusterSize, 1);
        for j = 1 : ClusterSize
            Similarity = Similarity + Cluster(:, j).^2;
        end;

		[MaxSimilarity, ClusterIndex] = max(Similarity);
        clear MaxSimilarity;
		CenterLabel(i, 1) = RandomWalkIndex(ClusterIndex(1));

        AttributeContribution1 = AttributeContribution1 + size(find(Attribute(RandomWalkIndex, 1) == Attribute(CenterLabel(i, 1), 1)), 1);
        AttributeContribution2 = AttributeContribution2 + size(find(Attribute(RandomWalkIndex, 2) == Attribute(CenterLabel(i, 1), 2)), 1);
        AttributeContribution3 = AttributeContribution3 + size(find(Attribute(RandomWalkIndex, 3) == Attribute(CenterLabel(i, 1), 3)), 1);
        AttributeContribution4 = AttributeContribution4 + size(find(Attribute(RandomWalkIndex, 4) == Attribute(CenterLabel(i, 1), 4)), 1);
    end;

    
    AttributeContributionSum = AttributeContribution1 + AttributeContribution2 + AttributeContribution3 + AttributeContribution4;
    WeightNew1 = (WeightOld1 + WeightSum * AttributeContribution1 / AttributeContributionSum) / 2;
    WeightNew2 = (WeightOld2 + WeightSum * AttributeContribution2 / AttributeContributionSum) / 2;
    WeightNew3 = (WeightOld3 + WeightSum * AttributeContribution3 / AttributeContributionSum) / 2;
    WeightNew4 = (WeightOld4 + WeightSum * AttributeContribution4 / AttributeContributionSum) / 2;
    WeightProportion1 = WeightNew1 / WeightOld1;
    WeightProportion2 = WeightNew2 / WeightOld2;
    WeightProportion3 = WeightNew3 / WeightOld3;
    WeightProportion4 = WeightNew4 / WeightOld4;
    WeightIncrement1 = WeightProportion1 - 1;
    WeightIncrement2 = WeightProportion2 - 1;
    WeightIncrement3 = WeightProportion3 - 1;
    WeightIncrement4 = WeightProportion4 - 1;
    
    if (((abs(WeightNew1 - WeightOld1) + abs(WeightNew2 - WeightOld2) + abs(WeightNew3 - WeightOld3) + abs(WeightNew4 - WeightOld4)) / AttributeSize) < Gama)
        TimeElapsed(OuterIterationTime) = toc;
        FileName = 'Runtime.txt';
        if(OuterIterationTime == 1)
            Fid = fopen(FileName, 'wt');
        else
            Fid = fopen(FileName, 'at');
        end;
        fprintf(Fid, '%d    %8.6f\n', OuterIterationTime, TimeElapsed(OuterIterationTime));
        fclose(Fid);
        break;
    end;
 	WeightOld1 = WeightNew1;
	WeightOld2 = WeightNew2;
    WeightOld3 = WeightNew3;
    WeightOld4 = WeightNew4;
    
    
    
    TimeElapsed(OuterIterationTime) = toc;
	FileName = 'Runtime.txt';
	if(OuterIterationTime == 1)
        Fid = fopen(FileName, 'wt');
    else
        Fid = fopen(FileName, 'at');
	end;
	fprintf(Fid, '%d    %8.6f\n', OuterIterationTime, TimeElapsed(OuterIterationTime));
	fclose(Fid);  
    OuterIterationTime = OuterIterationTime + 1;
    
    
    
    tic;
    PruneSize = PRUNE_SIZE;
    Factor(1 : VertexSize, Attribute1StartDimension : Attribute2StartDimension - 1) = WeightProportion1 * Factor(1 : VertexSize, Attribute1StartDimension : Attribute2StartDimension - 1);
    Factor(1 : VertexSize, Attribute2StartDimension : Attribute3StartDimension - 1) = WeightProportion2 * Factor(1 : VertexSize, Attribute2StartDimension : Attribute3StartDimension - 1);
    Factor(1 : VertexSize, Attribute3StartDimension : Attribute4StartDimension - 1) = WeightProportion3 * Factor(1 : VertexSize, Attribute3StartDimension : Attribute4StartDimension - 1);
    Factor(1 : VertexSize, Attribute4StartDimension : MatrixDimension) = WeightProportion4 * Factor(1 : VertexSize, Attribute4StartDimension : MatrixDimension);
    Iteration = Restart * Factor;
    RandomWalk = Iteration;
    PruneSize = PruneSize / IncrementFactor;


    for i = 2 : InnerIterationTime
        Iteration = Iteration * Factor;
    	Value = logical(Iteration > 0.0 & Iteration <= PruneSize);
    	Iteration(Value) = 0.0;
    	clear Value;
        Iteration = sparse(Iteration);
        PruneSize = PruneSize / IncrementFactor;
        RandomWalk = RandomWalk + Iteration;
    end;
    clear Iteration;


    load Dataset.txt;
    Transition = spconvert(Dataset);
    clear Dataset;
end;


	FileName = 'Runtime.txt';
	Fid = fopen(FileName, 'at');
	fprintf(Fid, '\nTotalTime    %8.6f', sum(TimeElapsed));
	fclose(Fid);
clear Factor;
clear RandomWalk;



 EntropySum = 0;
 AttributeValueDistribution1 = zeros(AttributeValueSize1, 1);
 AttributeValueDistribution2 = zeros(AttributeValueSize2, 1);
 AttributeValueDistribution3 = zeros(AttributeValueSize3, 1);
 AttributeValueDistribution4 = zeros(AttributeValueSize4, 1);
 for i = 1 : k
 	TransitionIndex = find(AssignmentCurrent == i);
 	ClusterSize = size(TransitionIndex, 1);
 	Entropy = 0;
 	for j = 1 : AttributeValueSize1
         AttributeValueDistribution1(j, 1) = size(find(Attribute(TransitionIndex, 1) == (j-1)), 1) / ClusterSize;
         if (AttributeValueDistribution1(j, 1) > 0)            
             Entropy = Entropy - WeightOld1 * AttributeValueDistribution1(j, 1) * log(AttributeValueDistribution1(j, 1)) / log(2);
         end;            
 	end;
 	EntropySum = EntropySum + Entropy * ClusterSize / VertexSize / WeightSum;         

 	Entropy = 0;
 	for j = 1 : AttributeValueSize2
         AttributeValueDistribution2(j, 1) = size(find(Attribute(TransitionIndex, 2) == j), 1) / ClusterSize;
         if (AttributeValueDistribution2(j, 1) > 0)            
             Entropy = Entropy - WeightOld2 * AttributeValueDistribution2(j, 1) * log(AttributeValueDistribution2(j, 1)) / log(2);
         end; 
 	end;
 	EntropySum = EntropySum + Entropy * ClusterSize / VertexSize / WeightSum;

     Entropy = 0;
     for j = 1 : AttributeValueSize3
         AttributeValueDistribution3(j, 1) = size(find(Attribute(TransitionIndex, 3) == j), 1) / ClusterSize;
         if (AttributeValueDistribution3(j, 1) > 0)            
             Entropy = Entropy - WeightOld3 * AttributeValueDistribution3(j, 1) * log(AttributeValueDistribution3(j, 1)) / log(2);
         end; 
     end;
     EntropySum = EntropySum + Entropy * ClusterSize / VertexSize / WeightSum;
    
     Entropy = 0;
     for j = 1 : AttributeValueSize4
         AttributeValueDistribution4(j, 1) = size(find(Attribute(TransitionIndex, 2) == j), 1) / ClusterSize;
         if (AttributeValueDistribution4(j, 1) > 0)            
             Entropy = Entropy - WeightOld4 * AttributeValueDistribution4(j, 1) * log(AttributeValueDistribution4(j, 1)) / log(2);
         end; 
     end;
     EntropySum = EntropySum + Entropy * ClusterSize / VertexSize / WeightSum;
 end;

 fileName = 'Entropy.txt';
 fid = fopen(fileName, 'wt');
 fprintf(fid, 'Entropy: %8.6f\n', EntropySum);
 fclose(fid);  

load Dataset.txt;
Transition = spconvert(Dataset);
clear Dataset;
Cohensive = 0;
for i = 1 : k
	Cohensive = Cohensive + nnz(Transition(AssignmentCurrent == i, AssignmentCurrent == i));
end;
Cohensive = Cohensive / nnz(Transition(1 : VertexSize, 1 : VertexSize));
clear Transition;

fileName = 'Cohensive.txt';
fid = fopen(fileName, 'wt');
fprintf(fid, 'Density: %8.6f\n', Cohensive);
fclose(fid);