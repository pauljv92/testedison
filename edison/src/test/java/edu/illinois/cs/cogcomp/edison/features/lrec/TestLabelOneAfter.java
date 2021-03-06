package edu.illinois.cs.cogcomp.edison.features.lrec;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.edison.features.FeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.Feature;
import edu.illinois.cs.cogcomp.edison.features.helpers.TestPosHelper;
import edu.illinois.cs.cogcomp.edison.features.lrec.LabelOneAfter;
import edu.illinois.cs.cogcomp.edison.features.lrec.TestPOSBaseLineFeatureExtractor;
import edu.illinois.cs.cogcomp.edison.utilities.EdisonException;
import edu.illinois.cs.cogcomp.edison.utilities.POSBaseLineCounter;
import edu.illinois.cs.cogcomp.edison.utilities.POSMikheevCounter;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * NOTE: you must set the path in src/test/resources/lrec-config.txt to point to the relevant POS
 * corpus.
 */

public class TestLabelOneAfter extends TestCase {


    private static List<TextAnnotation> tas;

    static {
        try {
            tas = IOUtils.readObjectAsResource(TestPOSBaseLineFeatureExtractor.class, "test.ta");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
    }

    public final void test() throws Exception {
        System.out.println("LabelOneAfter Feature Extractor");
        // Using the first TA and a constituent between span of 30-40 as a test
        TextAnnotation ta = tas.get(2);
        View TOKENS = ta.getView("TOKENS");

        System.out.println("GOT TOKENS FROM TEXTAnn");

        List<Constituent> testlist = TOKENS.getConstituentsCoveringSpan(0, 20);

        for (Constituent c : testlist) {
            System.out.println(c.getSurfaceForm());
        }

        System.out.println("Testlist size is " + testlist.size());

        // Constituent test = testlist.get(1);

        // System.out.println("The constituent we are extracting features from
        // in this test is: " + test.getSurfaceForm());

        // String fileName =
        // "C:\\Users\\Jason\\Desktop\\UIUC 2015 Fall\\Cogcomp\\pos-translation\\pos";

        POSBaseLineCounter posBaseLine = new POSBaseLineCounter("posBaseLine");
        posBaseLine.buildTable(TestPosHelper.corpus);

        POSMikheevCounter posMikheev = new POSMikheevCounter("posMikheev");
        posMikheev.buildTable(TestPosHelper.corpus);

        LabelOneAfter l1aPOS = new LabelOneAfter("l1aPOS");
        LabelOneAfter l1aPOSBaseLine = new LabelOneAfter("l1aPOSBaseLine", posBaseLine);
        LabelOneAfter l1aPOSMikheev = new LabelOneAfter("l1aPOSMikheev", posMikheev);

        // Test when using POS View
        ArrayList<Set<Feature>> featslist = new ArrayList<>();

        for (Constituent test : testlist)
            featslist.add(l1aPOS.getFeatures(test));

        if (featslist.isEmpty()) {
            System.out.println("Feats list is returning NULL.");
        }

        System.out.println("\n" + "Test when using POS View");
        System.out.println("Printing list of Feature set");

        for (Set<Feature> feats : featslist) {
            for (Feature f : feats)
                System.out.println(f.getName());
        }

        // Test when using POS baseline Counting
        featslist.clear();

        for (Constituent test : testlist)
            featslist.add(l1aPOSBaseLine.getFeatures(test));

        if (featslist.isEmpty()) {
            System.out.println("Feats list is returning NULL.");
        }

        System.out.println("\n" + "Test when using POS baseline Counting");
        System.out.println("Printing list of Feature set");

        for (Set<Feature> feats : featslist) {
            for (Feature f : feats)
                System.out.println(f.getName());
        }
        // Test when using POS Mikheev Counting
        featslist.clear();

        for (Constituent test : testlist)
            featslist.add(l1aPOSMikheev.getFeatures(test));

        if (featslist.isEmpty()) {
            System.out.println("Feats list is returning NULL.");
        }

        System.out.println("\n" + "Test when using POS Mikheev Counting");
        System.out.println("Printing list of Feature set");

        for (Set<Feature> feats : featslist) {
            for (Feature f : feats)
                System.out.println(f.getName());
        }

        System.out.println("GOT FEATURES YES!");
    }

    private void testFex(FeatureExtractor fex, boolean printBoth, String... viewNames)
            throws EdisonException {

        for (TextAnnotation ta : tas) {
            for (String viewName : viewNames)
                if (ta.hasView(viewName))
                    System.out.println(ta.getView(viewName));
        }
    }
}
