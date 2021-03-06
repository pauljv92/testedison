package edu.illinois.cs.cogcomp.ner.InferenceMethods;

import edu.illinois.cs.cogcomp.lbjava.learn.SparseNetworkLearner;
import edu.illinois.cs.cogcomp.lbjava.nlp.Word;
import edu.illinois.cs.cogcomp.lbjava.parse.LinkedVector;
import edu.illinois.cs.cogcomp.ner.ExpressiveFeatures.TwoLayerPredictionAggregationFeatures;
import edu.illinois.cs.cogcomp.ner.LbjFeatures.NETaggerLevel1;
import edu.illinois.cs.cogcomp.ner.LbjFeatures.NETaggerLevel2;
import edu.illinois.cs.cogcomp.ner.LbjTagger.Data;
import edu.illinois.cs.cogcomp.ner.LbjTagger.NEWord;
import edu.illinois.cs.cogcomp.ner.LbjTagger.ParametersForLbjCode;
import edu.illinois.cs.cogcomp.ner.LbjTagger.TextChunkRepresentationManager;

import java.util.ArrayList;


public class Decoder {

    /**
     * If you don't wanna use some of the classifiers - pass null parameters.
     */
    public static void annotateDataBIO(Data data, NETaggerLevel1 taggerLevel1,
            NETaggerLevel2 taggerLevel2) throws Exception {
        Decoder.annotateBIO_AllLevelsWithTaggers(data, taggerLevel1, taggerLevel2);
    }

    /**
     * use taggerLevel2=null if you want to use only one level of inference
     */
    protected static void annotateBIO_AllLevelsWithTaggers(Data data, NETaggerLevel1 taggerLevel1,
            NETaggerLevel2 taggerLevel2) throws Exception {

        clearPredictions(data);
        NETaggerLevel1.isTraining = false;
        NETaggerLevel2.isTraining = false;


        GreedyDecoding.annotateGreedy(data, taggerLevel1, 1);

        TextChunkRepresentationManager.changeChunkRepresentation(
                ParametersForLbjCode.currentParameters.taggingEncodingScheme,
                TextChunkRepresentationManager.EncodingScheme.BIO, data,
                NEWord.LabelToLookAt.PredictionLevel1Tagger);


        PredictionsAndEntitiesConfidenceScores.pruneLowConfidencePredictions(data,
                ParametersForLbjCode.currentParameters.minConfidencePredictionsLevel1,
                NEWord.LabelToLookAt.PredictionLevel1Tagger);

        // this block runs the level2 tagger
        // Previously checked if features included 'PatternFeatures'
        boolean level2 =
                ParametersForLbjCode.currentParameters.featuresToUse
                        .containsKey("PredictionsLevel1");
        if (taggerLevel2 != null && level2) {
            // annotate with patterns
            PredictionsAndEntitiesConfidenceScores.pruneLowConfidencePredictions(data, 0.0,
                    NEWord.LabelToLookAt.PredictionLevel1Tagger);
            TwoLayerPredictionAggregationFeatures.setLevel1AggregationFeatures(data, false);
            GreedyDecoding.annotateGreedy(data, taggerLevel2, 2);
            PredictionsAndEntitiesConfidenceScores.pruneLowConfidencePredictions(data,
                    ParametersForLbjCode.currentParameters.minConfidencePredictionsLevel2,
                    NEWord.LabelToLookAt.PredictionLevel2Tagger);
            TextChunkRepresentationManager.changeChunkRepresentation(
                    ParametersForLbjCode.currentParameters.taggingEncodingScheme,
                    TextChunkRepresentationManager.EncodingScheme.BIO, data,
                    NEWord.LabelToLookAt.PredictionLevel2Tagger);
        } else {
            for (int docid = 0; docid < data.documents.size(); docid++) {
                ArrayList<LinkedVector> sentences = data.documents.get(docid).sentences;
                for (LinkedVector sentence : sentences)
                    for (int i = 0; i < sentence.size(); i++) {
                        NEWord w = (NEWord) sentence.get(i);
                        w.neTypeLevel2 = w.neTypeLevel1;
                    }
            }
        }
    }

    /*
     * Lbj does some pretty annoying caching. We need this method for the beamsearch and the
     * viterbi.
     */
    public static void nullifyTaggerCachedFields(SparseNetworkLearner tagger) {
        NEWord w = new NEWord(new Word("lala1"), null, "O");
        w.parts = new String[0];
        NEWord[] words =
                {new NEWord(w, null, "O"), new NEWord(w, null, "O"), new NEWord(w, null, "O"),
                        new NEWord(w, null, "O"), new NEWord(w, null, "O"),
                        new NEWord(w, null, "O"), new NEWord(w, null, "O")};
        for (int i = 1; i < words.length; i++) {
            words[i].parts = new String[0];
            words[i].previous = words[i - 1];
            words[i].previousIgnoreSentenceBoundary = words[i - 1];
            words[i - 1].next = words[i];
            words[i - 1].nextIgnoreSentenceBoundary = words[i];
        }
        for (NEWord word : words)
            word.neTypeLevel1 = word.neTypeLevel2 = "O";
        tagger.classify(words[3]);
    }

    public static void clearPredictions(Data data) {
        for (int docid = 0; docid < data.documents.size(); docid++) {
            ArrayList<LinkedVector> sentences = data.documents.get(docid).sentences;
            for (LinkedVector sentence : sentences) {
                for (int i = 0; i < sentence.size(); i++) {
                    ((NEWord) sentence.get(i)).neTypeLevel1 = null;
                    ((NEWord) sentence.get(i)).neTypeLevel2 = null;
                }
            }
        }
    }
}
