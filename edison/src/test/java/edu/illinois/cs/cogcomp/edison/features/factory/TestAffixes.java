package edu.illinois.cs.cogcomp.edison.features.factory.newfexes;

import org.apache.commons.lang.ArrayUtils;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.edison.features.FeatureCollection;
import edu.illinois.cs.cogcomp.edison.features.FeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.FeatureUtilities;
import edu.illinois.cs.cogcomp.edison.features.Feature;
import edu.illinois.cs.cogcomp.edison.utilities.CreateTestFeaturesResource;
import edu.illinois.cs.cogcomp.edison.utilities.CreateTestTAResource;
import edu.illinois.cs.cogcomp.edison.utilities.EdisonException;
import junit.framework.TestCase;

import java.util.List;
import java.util.Set;
import java.util.Random;
import java.io.Writer;


/**
 * Test class for SHALLOW PARSER Affixes Feature Extractor
 *
 * @author Paul Vijayakumar, Mazin Bokhari
 */
public class TestAffixes extends TestCase {

    private static List<TextAnnotation> tas;
    
    static {
	try {
	    tas = IOUtils.readObjectAsResource(TestAffixes.class, "test.ta");
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }
    
    protected void setUp() throws Exception {
	super.setUp();
    }
    
    public final void testAffixes() throws EdisonException {
	
	// System.out.println("Affixes Feature Extractor");
	//Using the first TA and a constituent between span of 30-40 as a test
	TextAnnotation ta = tas.get(1);
	View TOKENS = ta.getView("TOKENS");
	
	// System.out.println("GOT TOKENS FROM TEXTAnn");
	
	List<Constituent> testlist = TOKENS.getConstituentsCoveringSpan(0,20);
	
	for(Constituent c: testlist){
	    // System.out.println(c.getSurfaceForm());
	}

	// System.out.println("Testlist size is "+testlist.size());

	Constituent test = testlist.get(1);
	
	// System.out.println("The constituent we are extracting features from in this test is: "+test.getSurfaceForm());
	
       	Affixes afx = new Affixes("Affixes");
	
	System.out.println("Startspan is "+test.getStartSpan()+" and Endspan is "+test.getEndSpan());
     
	Set<Feature> feats = afx.getFeatures(test);
	String[] expected_outputs = {
		"Affixes:p|(giv)",
		"Affixes:s|(e)",
		"Affixes:s|(ve)",
		"Affixes:s|(ive)"
	};

	if(feats == null){
	    // System.out.println("Feats are returning NULL.");
	}
	
	// System.out.println("Printing Set of Features");
	for(Feature f: feats){
	    System.out.println(f.getName());
		assert(ArrayUtils.contains( expected_outputs, f.getName()));
	}
	 
    }

}
