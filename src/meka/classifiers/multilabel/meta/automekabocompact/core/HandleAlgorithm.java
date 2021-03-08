/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meka.classifiers.multilabel.meta.automekabocompact.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created to handle the strings produced by the ExperimentConstructor class
 * @author alexgcsa
 */
public class HandleAlgorithm {
    
    public String[] genMLCWithBaseSLCAlgorithm(String startCommand, String[] options){        
        String [] startCL = startCommand.split(" ");        
        String [] completeCommand = this.concatenate(startCL, options);
        
        return completeCommand;
        
    }
    
    public String[] generateAlgorith(int timeoutLimit, String algorithm, String[] options, String path, String learningSet, String evaluationSet){
        String mlc_algorithm = algorithm;
        int p = 0;
//        
        if(mlc_algorithm.startsWith("NO_META_MLC")){
            p++;
            mlc_algorithm = options[p];
            p++;
            p++;
        }        
        int newtimeoutLimit = 180;
        String javaDir = "java";
        
        String startCommand = "timeout " + newtimeoutLimit+"s "+javaDir+" -Xmx2g -cp weka.jar:meka.jar " +mlc_algorithm + " -t " + path + learningSet + " -T " + path + evaluationSet + " -verbosity 6 ";
        String [] resultantAlgorithm = null;
        ArrayList<String> newOptions = new ArrayList<String>();
        
        for(int i=p; i < options.length; i++){
            if(options[i].startsWith("NO_META_SLC")){
                i++;
                i++;
            }else{
                newOptions.add(options[i]);
            }
        }
        
        String[] opts = new String[newOptions.size()];
        int j = 0;
        for(String s : newOptions){
            opts[j] = s;
            j++;
        }
        resultantAlgorithm = this.genMLCWithBaseSLCAlgorithm(startCommand, opts );


        
        
        return resultantAlgorithm;
    }
    
    public String[] concatenate(String[] first, String[] second) {
        List<String> both = new ArrayList<String>(first.length + second.length);
        Collections.addAll(both, first);
        Collections.addAll(both, second);
    return both.toArray(new String[both.size()]);
    }    
}