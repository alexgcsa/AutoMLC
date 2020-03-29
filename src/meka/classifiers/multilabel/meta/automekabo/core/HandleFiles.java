/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meka.classifiers.multilabel.meta.automekabo.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import mulan.data.InvalidDataFormatException;
import mulan.data.IterativeStratification;
import mulan.data.MultiLabelInstances;
import weka.core.Instances;

/**
 *
 * @author alexgcsa
 */
public class HandleFiles {
    
    public static void copyFile(File in, File out) 
        throws IOException 
    {
        FileChannel inChannel = new
            FileInputStream(in).getChannel();
        FileChannel outChannel = new
            FileOutputStream(out).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(),
                    outChannel);
        } 
        catch (IOException e) {
            throw e;
        }
        finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }
    
    /**
     * It splits the training data (in a stratified way) into two subsets: learning and validation.     
     * While the former is used to learn the model, the latter is used to valid the produce model 
     * by that MLC algorithm. It uses Mulan Java library to perform the stratified sampling.
     * @param exp
     * @param path
     * @param arffDir
     * @param seed - The seed to sample the data.
     * @param fold - The fold to be sampled.
     * @param n_labels - The number of labels of the input dataset.
     * @return a string vector with the directories of the learning and validation sets. 
     */
    public void splitDataInAStratifiedWay(String expName, String path, long seed, int fold, int n_labels){
        try {    
            String arffDir = path + expName +".arff";
            MultiLabelInstances dataset = new MultiLabelInstances(arffDir, n_labels);
            
            IterativeStratification is = new IterativeStratification(seed);
            MultiLabelInstances[] folds = is.stratify(dataset, 5);
            
            /** It creates the validation set. **/
            MultiLabelInstances validationData = new MultiLabelInstances(folds[fold].getDataSet(), dataset.getLabelsMetaData());
            validationData.getDataSet().setRelationName(dataset.getDataSet().relationName());
            
            Instances learningInst = null;
            boolean validFirstFold = true;
            
            for(int f=0; f< folds.length; f++){                
                if(f!=fold && validFirstFold){
                    learningInst = new Instances(folds[f].getDataSet());
                    validFirstFold = false;
                }else if (f!=fold){
                    learningInst.addAll(folds[f].getDataSet());
                }                
            }            
            
            MultiLabelInstances learningData = new MultiLabelInstances(learningInst, dataset.getLabelsMetaData());
            learningData.getDataSet().setRelationName(dataset.getDataSet().relationName());
            
            try (FileWriter fLearning = new FileWriter(path + "Learning.arff", false)) {
                fLearning.write(learningData.getDataSet().toString());
                fLearning.close();
            }
            
            try (FileWriter fValidation = new FileWriter(path + "Validation.arff", false)) {
                fValidation.write(validationData.getDataSet().toString());
                fValidation.close();
            }       
        
        } catch (InvalidDataFormatException ex) {
            System.err.println("Invalid data format exception: "+ex);
        } catch (IOException ex) {
            System.err.println("General exception: "+ex);
        }
    }    
    
}
