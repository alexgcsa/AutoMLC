/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package meka.classifiers.multilabel.meta.gaautomlc.core.xmlparser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Alex G. C. de Sa (alexgcsa@dcc.ufmg.br)
 */
public class XMLGeneHandler {
    private Allele m_genome;
    private File m_xmlPath;
    private int m_depth;
    
    private final String str_gene = "gene";
    private final String str_allele = "allele";
    private final String str_parameter = "parameter";
    private final String str_comment = "comment";
    private final String str_type = "type";
    private final String str_step = "step";
    private final String str_counting = "counting";
    private final int nAttributes;
    private final int nLabels;
    
   int totalCount = 0;
   LinkedHashMap<String, String> intevalosAlg = null;
    
    public XMLGeneHandler(File xmlPath, int nAttributes, int nLabels) throws Exception{

        this.intevalosAlg = new LinkedHashMap<String, String>();
        this.nAttributes = nAttributes;
        this.nLabels = nLabels;
        m_xmlPath = this.tranformFile(xmlPath);
        parsing();       
     
    }   
    
    /**
     * Tranforming the configuration file to understand label-based or attribute-based hyper-parameters
     * @param dir
     * @return
     * @throws IOException 
     */
    public final File tranformFile(File dir) throws IOException{
        String dirIn = dir.getAbsolutePath();
        String dirOut = dirIn.substring(0, dirIn.length() - 4) + "-temp.xml";
        Path path2 = Paths.get(dirOut);
        File tst = new File(path2.toString()); 
        int left_bound_int = 0;
        int right_bound_int = 0;
        String content = "";
        String thresholdValues = "";
        String confidenceFactorValues = "";
        String kernelType = "";
        String widthDensity = "";
        String bagPercent = "";
        
        if(!tst.exists()){

            Path path = Paths.get(dirIn);
            Charset charset = StandardCharsets.UTF_8;
            
            //Dealing with a hyper-parameter of RAKEL (i.e., number of subsets to rum in an ensemble).
            content = new String(Files.readAllBytes(path), charset);                        
            right_bound_int = Math.min(2 * this.nLabels, 100);
            content = content.replace("<allele>2,min(2*L,100)</allele>", "<allele>2," + right_bound_int + "</allele>");
            Files.write(path2, content.getBytes(charset));
            
            //Dealing with a hyper-parameter of RAKEL (i.e., number of labels for each subset).
            right_bound_int = (int) (this.nLabels/2);            
            content = content.replace("<allele>1,L/2</allele>", "<allele>1," + right_bound_int + "</allele>");
            Files.write(path2, content.getBytes(charset));  
            
            //Dealing with a hyper-parameter of ML-BPNN (i.e., number of hidden units).
            left_bound_int = (int) (0.2*this.nAttributes);
            right_bound_int = (int) (0.8*this.nAttributes);            
            content = content.replace("<allele>0.2*n_attributes,0.8*n_attributes</allele>", "<allele>"+left_bound_int +","+ right_bound_int + "</allele>");
            Files.write(path2, content.getBytes(charset));
            
            //Dealing with hyper-parameter float for the threshold values:
            thresholdValues = this.getThresholdValues();
            content = content.replace("<allele>Threshold(PCut1,PCutL,[0.00000000000000000001,1.00])</allele>", thresholdValues);
            Files.write(path2, content.getBytes(charset));            

            //Dealing with hyper-parameters from C4.5 (J48) -- unpruned, confidence factor, collapse tree and subset raising:
            confidenceFactorValues = this.getConfFactorValues();
            content = content.replace("<allele>[-S] [-O] -C [0.0,1.0]</allele>", confidenceFactorValues);  
            Files.write(path2, content.getBytes(charset));
            
            //Dealing with the different kernels from SMO:
            kernelType = this.getKernelType1and2Values("NormalizedPolyKernel");
            content = content.replace("<allele>-K weka.classifiers.functions.supportVector.NormalizedPolyKernel -E [0.2,5.0] [-L]</allele>", kernelType);  
            Files.write(path2, content.getBytes(charset));
            
            kernelType = this.getKernelType1and2Values("PolyKernel");
            content = content.replace("<allele>-K weka.classifiers.functions.supportVector.PolyKernel -E [0.2,5.0] [-L]</allele>", kernelType);  
            Files.write(path2, content.getBytes(charset));   
            
            kernelType = this.getKernelType3Values();
            content = content.replace("<allele>-K weka.classifiers.functions.supportVector.Puk -O [0.1,1.0] -S [0.1,10.0]</allele>", kernelType);  
            Files.write(path2, content.getBytes(charset));      
            
            kernelType = this.getKernelType4Values();
            content = content.replace("<allele>-K weka.classifiers.functions.supportVector.RBF -G [0.0001,1.0]</allele>", kernelType);  
            Files.write(path2, content.getBytes(charset));
            
                    
            right_bound_int = (int) (Math.sqrt(nLabels) + 1);
            widthDensity = this.getWidthDensityValues(right_bound_int);
            content = content.replace("<allele>{-H 0 -L 1,-H -1 -L [1,SQRT(L)+1]}</allele>", widthDensity);
            Files.write(path2, content.getBytes(charset));              
            
            bagPercent = this.getBagSizePercentValues();
            content = content.replace("<allele>{-O -P 100, -P [10,100]}</allele>", bagPercent);
            Files.write(path2, content.getBytes(charset));                          
      
            tst = new File(path2.toString()); 
            
        }
            return tst;
        
        

    }
    
    public String getBagSizePercentValues(){
         String output = "";
         int left_bound = 10;
         int right_bound = 100;
        
         for(int i=left_bound; i<=right_bound; i++){
            output +=  "<allele>-O -P 100</allele>";             
            output +=  "<allele>-P "+i+"</allele>";
         }
        
        return output;
        
    }    
    
    public String getWidthDensityValues(int right_bound){
         String output = "";
         int left_bound = 1;
        
         for(int i=left_bound; i<=right_bound; i++){
            output +=  "<allele>-H 0 -L 1</allele>";             
            output +=  "<allele>-H -1 -L " + i + "</allele>";
         }
        
        return output;
        
    }
    
    /**
     * It gets the normalized/not normalized polynomial kernel float/Boolean values in the allele form.
     * @return 
     */
    public String getKernelType1and2Values(String kernel){
        String output = "";
        double left_bound = 0.2;
        double right_bound = 5.00;
        
        double increment = (right_bound - left_bound)/(double)(1000);        
        
        double start = left_bound;
        
        for(int i = 0; i <= 1000; i++){
           output += "<allele>-K weka.classifiers.functions.supportVector."+kernel +"#-E#"+start +"#-L</allele>";
           output += "<allele>-K weka.classifiers.functions.supportVector."+kernel +"#-E#"+start +"</allele>";
           start += increment;
        } 
        
        return output;
    }    
    
    /**
     * It gets the Puk kernel float values in the allele form.
     * @return 
     */
    public String getKernelType3Values(){
        String output = "";
        double left_bound1 = 0.1;
        double right_bound1 = 1.00;        
        double increment1 = (right_bound1 - left_bound1)/(double)(1000);         
        double start1 = left_bound1;
        
        double left_bound2 = 0.1;
        double right_bound2 = 10.00;        
        double increment2 = (right_bound2 - left_bound2)/(double)(1000);         
        double start2 = left_bound2;        
        
        for(int i = 0; i <= 1000; i++){
           output += "<allele>-K weka.classifiers.functions.supportVector.Puk#-O#"+start1 +"#-S#"+start2+"</allele>";
           start1 += increment1;
           start2 += increment2;
        } 
        
        return output;
    }        
    
    /**
     * It gets the RBF kernel float values in the allele form.
     * @return 
     */
    public String getKernelType4Values(){
        String output = "";
        double left_bound1 = 0.0001;
        double right_bound1 = 1.00;        
        double increment1 = (right_bound1 - left_bound1)/(double)(1000);         
        double start1 = left_bound1;
   
        
        for(int i = 0; i <= 1000; i++){
           output += "<allele>-K weka.classifiers.functions.supportVector.RBFKernel#-G#"+start1 +"</allele>";
           start1 += increment1;
   
        } 
        
        return output;
    }       
    

    /**
     * It gets the float threshold values in the allele form.
     * @return 
     */
    public String getThresholdValues(){
        String output = "";
        double left_bound = 0.00000000000000000001;
        double right_bound = 1.00;
        
        double increment = (right_bound - left_bound)/(double)(1000);        
        
        double start = left_bound;
        
        for(int i = 0; i <= 1000; i++){
           output += "<allele>PCut1</allele>";
           output += "<allele>PCutL</allele>";
           output += "<allele>"+start+"</allele>";
           start += increment;
        } 
        
        return output;
    }
    
    public String getConfFactorValues(){
        
        String output = "";
        double left_bound = 0.0;
        double right_bound = 1.00;
        
        double increment = (right_bound - left_bound)/(double)(1000);        
        
        double start = left_bound;
        
        for(int i = 0; i <= 1000; i++){
           output += "<allele> -S -C "+start+"</allele>";
           output += "<allele> -O -C "+start+"</allele>";
           output += "<allele> -S -O -C "+start+"</allele>";           
           output += "<allele>-C "+start+"</allele>";             
           start += increment;
        }         
        
        
        return output;
    }
    
    /**
     * @param args the command line arguments
     */
    private void parsing() throws Exception{
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(m_xmlPath); 

            
            String depth = doc.getDocumentElement().getAttribute("maxDepth");
            m_depth = Integer.parseInt(depth);
            
            ArrayList<Node> nodes = getChildrenByTagName(doc.getDocumentElement(), str_gene);
            
            m_genome = getGene(nodes.get(0));
            m_genome.setM_totalCount(this.totalCount);
            m_genome.setIntevalosAlg(intevalosAlg);

        }
        catch(Exception e){
            throw new Exception("XML parser error", e);
        }
    }
    
    private ArrayList<Node> getChildrenByTagName(Element documentElement, String str_component) {
        ArrayList<Node> nodeList = new ArrayList<Node>();
        for (Node child = documentElement.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE && str_component.equals(child.getNodeName())) {
                nodeList.add((Element) child);
            }
        }
        return nodeList;
    }
    
    /**
     * Recover a component from the node
     * @param node input node
     * @return recovered component
     * @throws Exception error on parsing
     */
    private Allele getGene(Node node) throws Exception{
        try{
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                
                String parameter = getAttribute(element, str_parameter);
                String comment = getAttribute(element, str_comment);
                String type = getAttribute(element, str_type);
                String step = getAttribute(element, str_step);
                String counting = getAttribute(element, str_counting);                
                
                if(type.equals("")){
                    ArrayList<Node> nodes = getChildrenByTagName(element, str_allele);
                    
                    Allele gene = new Gene(comment, parameter, counting, "");
                    
                    for(int i = 0; i < nodes.size(); i++){
                        Node comp_node = nodes.get(i);
                        gene.addAllele(getAllele(comp_node));
                    }
                    
                    return gene;
                }
                // Else, the gene has alleles (values).
                else{
                    GeneOption gene = new GeneOption(comment, parameter, type, step, counting);
                    
                    ArrayList<Node> nodes = getChildrenByTagName(element, str_allele);
                    
                    for(int i = 0; i < nodes.size(); i++){
                        Element comp_node = (Element) nodes.get(i);
                        String value;
                        if(comp_node.getChildNodes().getLength() == 0){
                            value = "";
                        }
                        else{                            
                            value = comp_node.getChildNodes().item(0).getNodeValue();                           

                        }  
                        gene.addAllele(value, getAllele(comp_node).getComment());
                        
                    }
                    return gene;
                }
            }
        }
        catch (Exception ex) {
            throw new Exception("XML: Gene parser error", ex);
        }
        return null;
    }
    

    
    private Allele getAllele(Node node) throws Exception{
        Allele allele;
        
        try{
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                
                String parameter = getAttribute(element, str_parameter);
                String comment = getAttribute(element, str_comment);
                String counting = getAttribute(element, str_counting);
                
                if(!counting.equals("")){
                    String intervalo = this.totalCount +";";
                    this.totalCount += Integer.parseInt(counting);
                    intervalo += this.totalCount +"";
                    this.intevalosAlg.put(parameter, intervalo);
                }
                 
                allele = new Allele(comment, parameter, counting,"set");
                
                ArrayList<Node> nodes = getChildrenByTagName(element, str_gene);
                for(int i = 0; i < nodes.size(); i++){
                    allele.addAllele(getGene(nodes.get(i)));
                }
                return allele;
            }
        }
        catch (Exception ex) {
            throw new Exception("XML: Allele  parser error", ex);
        }
        return null;
    }
    
    private String getAttribute(Element element, String attibuteName){
        String str = element.getAttribute(attibuteName);
        if(str == null) return "";
        else return str;
    }
    
    public Allele getGenes() {
        return m_genome;
    }

    public int getDepth() {
        return m_depth;
    }

    public File getXMLPath() {
        return m_xmlPath;
    }
    
    
    
    
    
}