# Illinois Part-of-Speech Tagger


Part-of-Speech Tagging is the identification of words as nouns, verbs, adjectives, adverbs, etc. The system implemented 
here is based of the following paper: 

```
@inproceedings{Even-ZoharRo01,
    author = {Y. Even-Zohar and D. Roth},
    title = {A Sequential Model for Multi Class Classification},
    booktitle = {EMNLP},
    pages = {10-19},
    year = {2001},
    url = " http://cogcomp.cs.illinois.edu/papers/emnlp01.pdf"
}
```


## Usage

If you want to use this in your project, you need to take two steps. First add the dependencies, and then call the functions 
in your program. 
Here is how you can add maven dependencies into your program: 

```xml
    <repositories>
        <repository>
            <id>CogcompSoftware</id>
            <name>CogcompSoftware</name>
            <url>http://cogcomp.cs.illinois.edu/m2repo/</url>
        </repository>
    </repositories>
    
    <dependencies>
        <dependency>
            <groupId>edu.illinois.cs.cogcomp</groupId>
            <artifactId>illinois-pos</artifactId>
            <version>VERSION</version>
        </dependency>
    </dependencies>
```

**Note:** Make sure to change the pom.xml parameter `VERSION` to the latest version of the project.

In general, the best way to use the POS Tagger is through the [`POSAnnotator class`](src/main/java/edu/illinois/cs/cogcomp/pos/POSAnnotator.java). Like any other annotator, it is used by calling the `addView()` method on the `TextAnnotation` containing sentences to be tagged.

If you would prefer to skip the use of our core data structures, the [`TrainedPOSTagger class`](src/main/java/edu/illinois/cs/cogcomp/pos/TrainedPOSTagger.java) can be used, allowing for tokens to be tagged.

## Models
When using either `POSAnnotator` or `TrainedPOSTagger`, the models are loaded automatically from one of the following 
two locations, which are checked in order:
* First, the directory specified in the `Property` [`POSConfigurator.MODEL_PATH`](src/main/java/edu/illinois/cs/cogcomp/pos/POSConfigurator.java)
* If the files are not found in this directory, the classpath will be checked (this will result in loading the files 
from the Maven repository)

Thus, to use your own models, simply place them in this directory and they will be loaded; otherwise, the model version 
specified in this project's `pom.xml` file will be loaded from the Maven repository and used.

## Training
The class [`POSTrain`](src/main/java/edu/illinois/cs/cogcomp/pos/POSTrain.java) contains a main method that can be used to 
train the models for a POS tagger provided you have access to the necessary training data. It can be called from the top-level 
of the POS sub-project using the following command, where `[MODEL PATH]` is the directory where the model will be written and 
`[TRAINING DATA PATH]` is the file containing the training data.

    mvn exec:java -Dexec.mainClass="edu.illinois.cs.cogcomp.pos.POSTrain" -Dexec.args="[MODEL PATH] [TRAINING DATA PATH]"
