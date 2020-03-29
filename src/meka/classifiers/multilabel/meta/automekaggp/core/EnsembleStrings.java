/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meka.classifiers.multilabel.meta.automekaggp.core;

/**
 *
 * @author alexgcsa
 */
public class EnsembleStrings {
    
    private String ensembleStrAll;
    private String ensembleStrFew;

    public EnsembleStrings(String ensembleStrAll, String ensembleStrFew) {
        this.ensembleStrAll = ensembleStrAll;
        this.ensembleStrFew = ensembleStrFew;
    }

    public String getEnsembleStrAll() {
        return ensembleStrAll;
    }

    public String getEnsembleStrFew() {
        return ensembleStrFew;
    }

    public void setEnsembleStrAll(String ensembleStrAll) {
        this.ensembleStrAll = ensembleStrAll;
    }

    public void setEnsembleStrFew(String ensembleStrFew) {
        this.ensembleStrFew = ensembleStrFew;
    }
    
    
    
    
}
