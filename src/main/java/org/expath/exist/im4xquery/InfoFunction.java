/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.expath.exist.im4xquery;

import java.io.InputStream;
import org.apache.log4j.Logger;
import org.exist.dom.QName;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.Cardinality;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.BinaryValue;
import org.exist.xquery.value.FunctionParameterSequenceType;
import org.exist.xquery.value.FunctionReturnSequenceType;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.Type;
import org.im4java.core.Info;
import org.im4java.core.InfoException;

/**
 *
 * @author zwobit
 */
public class InfoFunction extends BasicFunction {

    private static final Logger LOGGER = Logger.getLogger(InfoFunction.class);

    public final static FunctionSignature signature = new FunctionSignature(
            new QName("info", Im4XQueryModule.NAMESPACE_URI, Im4XQueryModule.PREFIX),
            "get metainfo for an image",
            new SequenceType[]{
                new FunctionParameterSequenceType("image", Type.BASE64_BINARY, Cardinality.EXACTLY_ONE, "The image data"),},
            new FunctionReturnSequenceType(Type.NODE, Cardinality.ZERO_OR_ONE, "the converted image or an empty sequence if $image is invalid")
    );

    public InfoFunction(XQueryContext context, FunctionSignature signature) {
        super(context, signature);
    }

    @Override
    public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {
        //was an image and a mime-type speficifed
        if (args[0].isEmpty()) {
            return Sequence.EMPTY_SEQUENCE;
        }

        InputStream inputImage = null;
        
        try {
            //get the image data
            inputImage = ((BinaryValue) args[0].itemAt(0)).getInputStream();

            if (inputImage == null) {
                LOGGER.error("Unable to read image data!");
                return Sequence.EMPTY_SEQUENCE;
            }
            
            Info info = new Info("-", inputImage, false);
            
            LOGGER.debug("ImageFormat: " + info.getImageFormat());
            LOGGER.debug("ImageWidth: " + info.getImageWidth());
            LOGGER.debug("ImageHeight: " + info.getImageHeight());
            LOGGER.debug("ImageGeometry: " + info.getImageGeometry());
            LOGGER.debug("ImageDepth: " + info.getImageDepth());
            LOGGER.debug("ImageClass: " + info.getImageClass());
             
            

            return Sequence.EMPTY_SEQUENCE;
        } catch (InfoException e) {
            throw new XPathException(this, e.getMessage());
        }
    }
    
    
}
