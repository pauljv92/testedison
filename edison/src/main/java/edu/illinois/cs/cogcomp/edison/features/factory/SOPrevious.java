package edu.illinois.cs.cogcomp.edison.features.factory;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Queries;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.features.DiscreteFeature;
import edu.illinois.cs.cogcomp.edison.features.Feature;
import edu.illinois.cs.cogcomp.edison.features.FeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.RealFeature;
import edu.illinois.cs.cogcomp.edison.features.helpers.SpanLabelsHelper;
import edu.illinois.cs.cogcomp.edison.utilities.EdisonException;

import java.util.*;

/**
 *
 * @author Paul Vijayakumar, Mazin Bokhari
 *
 */
public class SOPrevious implements FeatureExtractor {

    public static View SHALLOW_PARSE, POS, TOKENS;

    private final String viewName;
    
    public SOPrevious(String viewName) {
	this.viewName = viewName;
    }
    
    @Override
    /**
     * This feature extractor assumes that the TOKEN View, POS View and the SHALLOW_PARSE View have been
     * generated in the Constituents TextAnnotation. It will use its own POS tag and well as the POS tag
     * and the SHALLOW_PARSE (Chunk) labels of the previous two tokens and return it as a discrete feature. 
     *
     **/
    public Set<Feature> getFeatures(Constituent c) throws EdisonException {
	
	TextAnnotation ta = c.getTextAnnotation();
	
	TOKENS = ta.getView(ViewNames.TOKENS);
	POS = ta.getView(ViewNames.POS);
	SHALLOW_PARSE = ta.getView(ViewNames.SHALLOW_PARSE);
	
	//We can assume that the constituent in this case is a Word(Token) described by the LBJ chunk definition
	int startspan = c.getStartSpan();
	int endspan = c.getEndSpan();
	
	//All our constituents are words(tokens)
	List<Constituent> wordstwobefore = getWordsTwoBefore(TOKENS,startspan);
	
	String[] tags = new String[3];
	String[] labels = new String[2];
	
	int i = 0;
	for(Constituent token : wordstwobefore){
	    
	    //Should only be one POS tag for each token
	    List<String> POS_tag = POS.getLabelsCoveringSpan(token.getStartSpan(), token.getEndSpan());
	    List<String> Chunk_label = SHALLOW_PARSE.getLabelsCoveringSpan(token.getStartSpan(), token.getEndSpan());
	    
	    if(POS_tag.length != 1 || Chunk_label.length != 1){
		    System.out.println("Error token has more than one POS tag or Chunk Label.");
	    }
	    
	    labels[i] = Chunk_label.get(0);
	    tags[i] = POS_tag.get(0);
	    i++;
	}
	tags[i] = POS.getLabelsCoveringSpan(startspan, endspan);
	
	Set<Feature> __result = new LinkedHashSet<Feature>();
	
	__id = "ll";
	__value = "" + (labels[0] + "_" + labels[1]);
	__result.add(new DiscreteFeature(__value));
	__id = "lt1";
	__value = "" + (labels[0] + "_" + tags[1]);
	__result.add(new DiscreteFeature(__value));
	__id = "lt2";
	__value = "" + (labels[1] + "_" + tags[2]);
	__result.add(new DiscreteFeature(__value));
	
	return __result;
    }
    
    @Override
    public String getName() {
	return "#path#" + viewName;
    }    
}
