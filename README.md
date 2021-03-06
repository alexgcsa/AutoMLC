![Java 8](https://img.shields.io/badge/java-8-blue.svg) 
![License](https://img.shields.io/badge/license-GPLv3-blue.svg) 

# AutoMLC
Automated Multi-Label Classification

## **Overview**

This project presents the AutoMLC project. This is a project that provides 6 AutoML methods in the context of multi-label classification: GA-Auto-MLC (Genetic Algorithm), Auto-MEKA_GGP (Grammar-based Genetic Programming), Auto-MEKA_spGGP (Speciation-based GGP), AutoMEKA_BO (Bayesian Optimization), Auto_MEKA_RS (Random Search) and Auto-MEKA_LS (Local Search). All methods try to enhance the performance of multi-label classification algorithms on the [MEKA tool](http://waikato.github.io/meka/). 


If you use this project in your work, be aware of the details in the following papers:

1. **GA-Auto-MLC**

  - A. G. C. de Sá, G. L. Pappa, and A. A. Freitas. Towards a method for automatically selecting and configuring multi-label classification algorithms. In Proceedings of the Genetic and Evolutionary Computation Conference Companion , pp. 1125–1132, 2017. [ [PDF](https://www.cs.kent.ac.uk/people/staff/aaf/pub_papers.dir/GECCO-2017-ECADA-Wksp-de-Sa.pdf) ] [ [ACM](https://dl.acm.org/citation.cfm?id=3082053) ]

2. **Auto-MEKA_GGP**

  - A. G. C. de Sá, G. L. Pappa, and A. A. Freitas. Automated Selection and Configuration of Multi-Label Classification Algorithms with Grammar-based Genetic Programming. In Proceedings of the  International Conference on Parallel Problem Solving from Nature (PPSN), 2018.  [ [PDF](https://www.cs.kent.ac.uk/people/staff/aaf/pub_papers.dir/PPSN-2018-de-Sa.pdf) ] [ [Springer](https://link.springer.com/chapter/10.1007/978-3-319-99259-4_25) ]

3. **Auto-MEKA_BO, Auto-MEKA_spGGP, Auto-MEKA_RS and Auto-MEKA_LS**

  - A. G. C. de Sá, C. G. Pimenta,  A. A. Freitas, and G. L. Pappa. TA robust experimental evaluation of automated multi-label classification methods. In Proceedings of the Genetic and Evolutionary Computation Conference, pp. 175–183, 2020. [ [PDF](https://arxiv.org/pdf/2005.08083.pdf) ] [ [ACM](https://dl.acm.org/doi/abs/10.1145/3377930.3390231) ]
  
  - Note that Auto-MEKA_GGP uses modified versions of EpochX, MEKA and WEKA to perform automated multi-label classification. The versions of these frameworks can be found  at: [EpochX](https://github.com/alexgcsa/EpochX), [MEKA](https://github.com/alexgcsa/MEKA), and [WEKA](https://github.com/alexgcsa/WEKA)
  
  

## **Description of the Search Space**

The description of the multi-label classification (MLC) search is available in the following link: [MLC Search Space](https://arxiv.org/abs/1811.11353)

## **How to Use the AutoML Methods**

All methods depend on Java 8. We recommend you to use the jdk 1.8.0 171 as all methods were compiled on this jdk version. However, all jdk 1.8.0 should be fine. You can find jdk for Java 8 to download at:  https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html

You are going to use the jdk directory to run the AutoML methods.

### **[Auto-MEKA_GGP](https://github.com/alexgcsa/AutoMLC/tree/master/datasets).**

## **Datasets**

Several datatasets are available at: [datasets](https://github.com/alexgcsa/AutoMLC/tree/master/datasets).

## **License**

See [LICENSE](https://github.com/alexgcsa/AutoMLC/blob/master/LICENSE) file.


## **Support**

Any questions or comments should be directed to Alex de Sá (alexgcsa@gmail.com). You can also create an issue on the repository.


