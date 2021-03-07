# Auto-MEKA_BO
Automated Multi-Label Classification on the MEKA software using Bayesian Optimization.

This method is an adaptation of Auto-WEKA.

## **How to Run**

Run the following command line in the directory you downloaded from github to run Auto-MEKA_BO for the minimal or medium search spaces:

> /home/<your_user_dir>/<...>/jdk1.8.0_171/jre/bin/java -Xmx8g -cp automeka.jar meka.classifiers.multilabel.meta.AutoMEKA_BO_MinimalMedium -t datasets/01-Flags-Stratified5FoldsCV-Meka/Flags-train1.arff -T datasets/01-Flags-Stratified5FoldsCV-Meka/Flags-test1.arff

Or run the following command line in the directory you downloaded from github to run Auto-MEKA_BO for the large search spaces:

> /home/<your_user_dir>/<...>/jdk1.8.0_171/jre/bin/java -Xmx8g -cp automeka.jar meka.classifiers.multilabel.meta.AutoMEKA_BO_Large -t datasets/01-Flags-Stratified5FoldsCV-Meka/Flags-train1.arff -T datasets/01-Flags-Stratified5FoldsCV-Meka/Flags-test1.arff



If it works for you, you can try to explore the options for this method:
- -seed <value> : to set the random seed (default: 456).
- -timeLimit <value> : to set the maximum time limit for the experiment (default: 1 [minute]).
- -memLimit <value> : to set the memory limit (default: 1024 [MB])
- -parallelRuns <value> : to set the number of processes running in parallel (default: 1). This is just to run several MLC algorithms at the same time.
- -fold <value>: to set the fold init (default: 0). This parameter is just useful to create the fold into the internal validation.
- -expName <value> : to set the the experiment name (default: "ExperimentABC").
- -savingDir <path> : to set the path to save the results for this experiment (default: "").
- -searchSpaceMode <value> :  to set the search space mode, i.e., which search space the BO will use to guide its search. It depends on the code it's being applied on. The options are: 
  - 0: Minimal
  - 1: Medium
  - 2: Large (default)



## **Experimental Results**

After running, you are going to have into the saving directory, a set of log files, a compact list of the evaluation measures and a full list of the evaluation measures.


- For the compact list of predictive measures by intermediate time budget (e.g., StatisticsCompactStandard-ExperimentABC-tb1s11321f0.csv), you would have a file separated by semicolon with: 
> seed;foldInit;numberOfEvaluations;differenceTime;exactMatch_FullTraining;exactMatch_Test;exactMatch_Training;exactMatch_Validation;hammingLoss_FullTraining; hammingLoss_Test; hammingLoss_Training; hammingLoss_Validation;rankLoss_FullTraining; rankLoss_Test; rankLoss_Training; rankLoss_Validation;f1MacroAveragedLabel_FullTraining; f1MacroAveragedLabel_Test; f1MacroAveragedLabel_Training; f1MacroAveragedLabel_Validation;;MLCalgorithm

- For the full list of predictive measures by intermediate time budget (e.g., StatisticsStandard-ExperimentABC-tb1s11321f0.csv), you would have a file separated by semicolon with: 
> seed;foldInit;numberOfEvaluations;differenceTime;accuracy_FullTraining;accuracy_Test;accuracy_Training;accuracy_Validation;hammingScore_FullTraining;hammingScore_Test;hammingScore_Training;hammingScore_Validation;exactMatch_FullTraining;exactMatch_Test;exactMatch_Training;exactMatch_Validation;jaccardDistance_FullTraining;jaccardDistance_Test;jaccardDistance_Training;jaccardDistance_Validation;hammingLoss_FullTraining;hammingLoss_Test;hammingLoss_Training;hammingLoss_Validation;zeroOneLoss_FullTraining;zeroOneLoss_Test;zeroOneLoss_Training;zeroOneLoss_Validation;harmonicScore_FullTraining;harmonicScore_Test;harmonicScore_Training;harmonicScore_Validation;oneError_FullTraining;oneError_Test;oneError_Training;oneError_Validation;rankLoss_FullTraining;rankLoss_Test;rankLoss_Training;rankLoss_Validation;avgPrecision_FullTraining;avgPrecision_Test;avgPrecision_Training;avgPrecision_Validation;microPrecision_FullTraining;microPrecision_Test;microPrecision_Training;microPrecision_Validation;microRecall_FullTraining;microRecall_Test;microRecall_Training;microRecall_Validation;macroPrecision_FullTraining;macroPrecision_Test;macroPrecision_Training;macroPrecision_Validation;macroRecall_FullTraining;macroRecall_Test;macroRecall_Training;macroRecall_Validation;f1MicroAveraged_FullTraining;f1MicroAveraged_Test;f1MicroAveraged_Training;f1MicroAveraged_Validation;f1MacroAveragedExample_FullTraining;f1MacroAveragedExample_Test;f1MacroAveragedExample_Training;f1MacroAveragedExample_Validation;f1MacroAveragedLabel_FullTraining;f1MacroAveragedLabel_Test;f1MacroAveragedLabel_Training;f1MacroAveragedLabel_Validation;aurcMacroAveraged_FullTraining;aurcMacroAveraged_Test;aurcMacroAveraged_Training;aurcMacroAveraged_Validation;aurocMacroAveraged_FullTraining;aurocMacroAveraged_Test;aurocMacroAveraged_Training;aurocMacroAveraged_Validation;emptyLabelvectorsPredicted_FullTraining;emptyLabelvectorsPredicted_Test;emptyLabelvectorsPredicted_Training;emptyLabelvectorsPredicted_Validation;labelCardinalityPredicted_FullTraining;labelCardinalityPredicted_Test;labelCardinalityPredicted_Training;labelCardinalityPredicted_Validation;levenshteinDistance_FullTraining;levenshteinDistance_Test;levenshteinDistance_Training;levenshteinDistance_Validation;labelCardinalityDifference_FullTraining;labelCardinalityDifference_Test;labelCardinalityDifference_Training;labelCardinalityDifference_Validation;;MLCalgorithm
