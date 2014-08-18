package org.expath.exist.im4xquery;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.exist.dom.QName;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.Cardinality;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.Base64BinaryValueType;
import org.exist.xquery.value.BinaryValue;
import org.exist.xquery.value.BinaryValueFromInputStream;
import org.exist.xquery.value.FunctionParameterSequenceType;
import org.exist.xquery.value.FunctionReturnSequenceType;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.Type;
import org.im4java.core.IM4JavaException;


/**
 *
 * @author zwobit <tobias AT existsolutions.com>
 * @version 1.0
 */


public class Convert2JPGFunction extends BasicFunction {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Convert2JPGFunction.class);
    
    public final static FunctionSignature signature = new FunctionSignature(
            new QName("convert2jpg", Im4XQueryModule.NAMESPACE_URI, Im4XQueryModule.PREFIX),
            "converts an image",
            new SequenceType[]{
                new FunctionParameterSequenceType("image", Type.BASE64_BINARY, Cardinality.EXACTLY_ONE, "The image data"),
            },
            new FunctionReturnSequenceType(Type.BASE64_BINARY, Cardinality.ZERO_OR_ONE, "the converted image or an empty sequence if $image is invalid")   
    );

    public Convert2JPGFunction(XQueryContext context, FunctionSignature signature) {
        super(context, signature);
    }

    @Override
    public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {
        if(args[0].isEmpty()) {
            return Sequence.EMPTY_SEQUENCE;
        }
        
        InputStream inputImage = null;
        byte[] outputImage = null;
        
        try {
            
            //get the image data
            inputImage = ((BinaryValue) args[0].itemAt(0)).getInputStream();

            if (inputImage == null) {
                LOGGER.error("Unable to read image data!");
                return Sequence.EMPTY_SEQUENCE;
            }
            
            outputImage = Convert.convert2ImageFormat(inputImage, "jpg");
            
            if (outputImage != null) {
                return BinaryValueFromInputStream.getInstance(context, new Base64BinaryValueType(), new ByteArrayInputStream(outputImage));
            }
            return Sequence.EMPTY_SEQUENCE;
        } catch (IOException | InterruptedException | IM4JavaException ex) {
             throw new XPathException(this, ex.getMessage());
        }
    }
}
