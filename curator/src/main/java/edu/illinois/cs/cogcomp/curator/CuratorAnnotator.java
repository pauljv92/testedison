package edu.illinois.cs.cogcomp.curator;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.thrift.base.AnnotationFailedException;
import edu.illinois.cs.cogcomp.thrift.base.ServiceUnavailableException;
import org.apache.thrift.TException;

import java.net.SocketException;

/**
 * A single annotator object, corresponding to a
 * {@link edu.illinois.cs.cogcomp.thrift.curator.Curator.Client}'s annotator. Multiple instances of
 * this class will be used as {@link Annotator}s in {@link CuratorAnnotatorService}.
 *
 * The {@link #viewName} and {@link #requiredViews} fields are defined in Curator's configuration
 * file ({@code dist/configs/annotators.xml}).
 *
 * @author Christos Christodoulopoulos
 */
public class CuratorAnnotator extends Annotator {
    private CuratorClient curatorClient;


    public CuratorAnnotator(CuratorClient curatorClient, String viewName, String[] requiredViews) {
        super(viewName, requiredViews);
        this.curatorClient = curatorClient;
    }


    @Override
    public void addView(TextAnnotation ta) throws AnnotatorException {
        try {
            ta.addView(viewName, curatorClient.getTextAnnotationView(ta, viewName));
        } catch (TException | AnnotationFailedException | SocketException
                | ServiceUnavailableException e) {
            throw new AnnotatorException(e.getMessage());
        }
    }

}
