package edu.illinois.cs.cogcomp.nlp.corpusreaders.aceReaderTests;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.CoreferenceView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.aceReader.XMLException;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.aceReader.annotationStructure.ACEDocumentAnnotation;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.aceReader.annotationStructure.ACEEntity;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.aceReader.annotationStructure.ACERelation;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.aceReader.documentReader.ReadACEAnnotation;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Bhargav Mangipudi on 4/7/16.
 */
public class ACEReaderParseTest {

    @Ignore("ACE Dataset files will not be commited to repo.")
    @Test
    public void test2004Dataset() throws Exception {
        ACEReader reader = new ACEReader("src/test/resources/ACE/ace2004/data/English", true);
        testReaderParse(reader, 2);

    }

    @Ignore("ACE Dataset files will not be commited to repo.")
    @Test
    public void test2005Dataset() throws Exception {
        ACEReader reader = new ACEReader("src/test/resources/ACE/ace2005/data/English", false);
        testReaderParse(reader, 6);
    }

    private void testReaderParse(ACEReader reader, int numberOfDocs) throws XMLException {
        int numDocs = 0;
        ReadACEAnnotation.is2004mode = reader.Is2004Mode();
        String corpusIdGold = reader.Is2004Mode() ? "ACE2004" : "ACE2005";

        assertTrue(reader.hasNext());
        while (reader.hasNext()) {
            TextAnnotation doc = reader.next();
            ACEDocumentAnnotation annotation = ReadACEAnnotation.readDocument(doc.getId());

            assertNotNull(doc);
            assertNotNull(annotation);
            assertEquals(doc.getCorpusId(), corpusIdGold);

            Set<String> documentViews = doc.getAvailableViews();
            assertTrue(documentViews.contains(ViewNames.TOKENS));
            assertTrue(documentViews.contains(ACEReader.ENTITYVIEW));
            assertTrue(documentViews.contains(ACEReader.RELATIONVIEW));
            assertTrue(documentViews.contains(ViewNames.COREF));

            int entityMentions = 0;
            for (ACEEntity entity : annotation.entityList) entityMentions += entity.entityMentionList.size();

            SpanLabelView entityView = (SpanLabelView) doc.getView(ACEReader.ENTITYVIEW);
            assertEquals(entityView.getNumberOfConstituents(), entityMentions);

            CoreferenceView coreferenceView = (CoreferenceView) doc.getView(ViewNames.COREF);
            assertEquals(coreferenceView.getNumberOfConstituents(), entityMentions);

            int relationMentions = 0;
            for (ACERelation relation : annotation.relationList) relationMentions += relation.relationMentionList.size();

            PredicateArgumentView relationView = (PredicateArgumentView) doc.getView(ACEReader.RELATIONVIEW);
            assertEquals(relationView.getPredicates().size(), relationMentions);

            numDocs++;
        }

        assertEquals(numDocs, numberOfDocs);
    }
}
