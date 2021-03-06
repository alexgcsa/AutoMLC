# Auto-MEKA_GGP
Automated Multi-Label Classification on the MEKA software using Grammar-based Genetic Programming

## **How to Run**

Run the following command line in the directory you downloaded from github:
/home/<your_user_dir>/<...>/jdk1.8.0_281/jre/bin/java -Xmx8g -cp automeka.jar meka.classifiers.multilabel.meta.AutoMEKA_StdGGP -t datasets/Flags-train1.arff -T datasets/Flags-test1.arff


If it works for you, you can try to explore the options for this method:
- -K <value> : to set the tournament size (default: 2).
- -V <value> : to set the elitism size (default: 1).
- -P <value> : to set the population size (default: 10).
- -R <value> : to set the resampling of the training and validation sets after R generations. (default: -1, meaning that no resampling is performed).
- -E <value> : 
- -G <value> : to set the number of generations (default: 2).
- -M <value> : to set the mutation probability (default: 0.10).
- -X <value> : to set the crossover probability (default: 0.90).
- -N <value> : to set the number of processes running in parallel (default: 1). This is just to run several MLC algorithms at the same time.
- -H <value>: to set the random seed (default: 11321).
- -Y <value>: to set the fold init (default: 0). This parameter is just useful to create the fold into the internal validation.
- -L <value> : to set the ML algorithm time limit (default: 60 [seconds]).
- -W <value>: to set the experiment name (default: ExperimentABC).
- -C : changes from generational to anytime behavior (default: not used, False).
- -B <value>: to set the maximum time limit for the experiment (default: 1 [minute]).
- -D <value>: saving directory (default: "~/"). You can define any directory.
- -O <value>: the grammar mode, where the options are: "full", "SimpGA", "SimpBO" (default: "full").

After running, you are going to have into the saving directory, the generational results, a compact list of the evaluation measures and a full list of the evaluation measures.

- For the results for each generation (e.g., 0Results-ExperimentABC.csv), you would have a file separated by semicolon with: 
generation;worstFitness;averageFitness;bestFitness

- For the compact list of predictive measures (e.g., 0StatisticsCompact-ExperimentABC.csv), you would have a file separated by semicolon with: 
seed;foldInit;actualGeneration;numbOfEvaluations;differenceTime;searchTime;exactMatch_FullTraining;exactMatch_Test;exactMatch_Training;exactMatch_Validation;hammingLoss_FullTraining;hammingLoss_Test;hammingLoss_Training;hammingLoss_Validation;rankLoss_FullTraining;rankLoss_Test;rankLoss_Training;rankLoss_Validation;f1MacroAveragedLabel_FullTraining;f1MacroAveragedLabel_Test;f1MacroAveragedLabel_Training;f1MacroAveragedLabel_Validation;;best_found_algorithm

- For the full list of predictive measures (e.g., 0Statistics-ExperimentABC.csv), you would have a file separated by semicolon with: 
seed;foldInit;actualGeneration;numbOfEvaluations;differenceTime;searchTime;accuracy_FullTraining;accuracy_Test;accuracy_Training;accuracy_Validation;hammingScore_FullTraining;hammingScore_Test;hammingScore_Training;hammingScore_Validation;exactMatch_FullTraining;exactMatch_Test;exactMatch_Training;exactMatch_Validation;jaccardDistance_FullTraining;jaccardDistance_Test;jaccardDistance_Training;jaccardDistance_Validation;hammingLoss_FullTraining;hammingLoss_Test;hammingLoss_Training;hammingLoss_Validation;zeroOneLoss_FullTraining;zeroOneLoss_Test;zeroOneLoss_Training;zeroOneLoss_Validation;harmonicScore_FullTraining;harmonicScore_Test;harmonicScore_Training;harmonicScore_Validation;oneError_FullTraining;oneError_Test;oneError_Training;oneError_Validation;rankLoss_FullTraining;rankLoss_Test;rankLoss_Training;rankLoss_Validation;avgPrecision_FullTraining;avgPrecision_Test;avgPrecision_Training;avgPrecision_Validation;microPrecision_FullTraining;microPrecision_Test;microPrecision_Training;microPrecision_Validation;microRecall_FullTraining;microRecall_Test;microRecall_Training;microRecall_Validation;macroPrecision_FullTraining;macroPrecision_Test;macroPrecision_Training;macroPrecision_Validation;macroRecall_FullTraining;macroRecall_Test;macroRecall_Training;macroRecall_Validation;f1MicroAveraged_FullTraining;f1MicroAveraged_Test;f1MicroAveraged_Training;f1MicroAveraged_Validation;f1MacroAveragedExample_FullTraining;f1MacroAveragedExample_Test;f1MacroAveragedExample_Training;f1MacroAveragedExample_Validation;f1MacroAveragedLabel_FullTraining;f1MacroAveragedLabel_Test;f1MacroAveragedLabel_Training;f1MacroAveragedLabel_Validation;aurcMacroAveraged_FullTraining;aurcMacroAveraged_Test;aurcMacroAveraged_Training;aurcMacroAveraged_Validation;aurocMacroAveraged_FullTraining;aurocMacroAveraged_Test;aurocMacroAveraged_Training;aurocMacroAveraged_Validation;emptyLabelvectorsPredicted_FullTraining;emptyLabelvectorsPredicted_Test;emptyLabelvectorsPredicted_Training;emptyLabelvectorsPredicted_Validation;labelCardinalityPredicted_FullTraining;labelCardinalityPredicted_Test;labelCardinalityPredicted_Training;labelCardinalityPredicted_Validation;levenshteinDistance_FullTraining;levenshteinDistance_Test;levenshteinDistance_Training;levenshteinDistance_Validation;labelCardinalityDifference_FullTraining;labelCardinalityDifference_Test;labelCardinalityDifference_Training;labelCardinalityDifference_Validation;;algorithm


