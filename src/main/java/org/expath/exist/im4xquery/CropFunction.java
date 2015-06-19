package org.expath.exist.im4xquery;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.Logger;
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
import org.exist.xquery.value.IntegerValue;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.Type;
import org.im4java.core.IM4JavaException;

/**
 *
 * @author zwobit <tobias AT existsolutions.com>
 * @version 1.0
 */


public class CropFunction extends BasicFunction {
    private static final org.apache.log4j.Logger LOGGER = Logger.getLogger(CropFunction.class);
    
    private final static int HEIGHT = 96;
    private final static int WIDTH = 96;
    private final static int RHEIGHT = 0;
    private final static int RWIDTH = 0;
    private final static int OFFSETX = 0;
    private final static int OFFSETY = 0;
    private final static boolean REPAGE = true;
    
    public CropFunction(XQueryContext context, FunctionSignature signature) {
        super(context, signature);
    }
 
    public final static FunctionSignature signature = new FunctionSignature(
            new QName("crop", Im4XQueryModule.NAMESPACE_URI, Im4XQueryModule.PREFIX),
            "Crop the image image to a specified dimension.  If no dimensions are specified, then the default values are 'maxheight = 100' and 'maxwidth = 100'.",
            new SequenceType[]{
                new FunctionParameterSequenceType("image", Type.BASE64_BINARY, Cardinality.EXACTLY_ONE, "The image data"),
                new FunctionParameterSequenceType("dimension", Type.INTEGER, Cardinality.ZERO_OR_MORE, "The dimension of the scaled image. expressed in pixels (width, height).  If empty, then the default values are 'width = 96' and 'height = 96'."),
                new FunctionParameterSequenceType("offset", Type.INTEGER, Cardinality.ZERO_OR_MORE, "The offset (starting in the topleft corner) for the cropped image. (x,y) defaults to (0,0)."),
                new FunctionParameterSequenceType("mimeType", Type.STRING, Cardinality.EXACTLY_ONE, "The mime-type of the image"),
                new FunctionParameterSequenceType("repage", Type.INTEGER, Cardinality.ZERO_OR_MORE, "Repage the cropped of the image to (width, height) pixels. Defaults to (0,0)")
            },
            new FunctionReturnSequenceType(Type.BASE64_BINARY, Cardinality.ZERO_OR_ONE, "the scaled image or an empty sequence if $image is invalid")
    );
    
    @Override
    public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {
         LOGGER.debug("------------ CropFunction ---------------");        
        //was an image and a mime-type speficifed
        if (args[0].isEmpty() || args[3].isEmpty()) {
            return Sequence.EMPTY_SEQUENCE;
        }
        
        //get the maximum dimensions to scale to
        int height = HEIGHT;
        int width = WIDTH;
        int rheight = RHEIGHT;
        int rwidth = RWIDTH;
        int offsetX = OFFSETX;
        int offsetY = OFFSETY;
        boolean repage = REPAGE;

        if (!args[1].isEmpty()) {
            width = ((IntegerValue) args[1].itemAt(0)).getInt();
            if (args[1].hasMany()) {
                height = ((IntegerValue) args[1].itemAt(1)).getInt();
            }
        }
        
        if (!args[2].isEmpty()) {
            offsetX = ((IntegerValue) args[2].itemAt(0)).getInt();
            if (args[1].hasMany()) {
                offsetY = ((IntegerValue) args[2].itemAt(1)).getInt();
            }
        }
        
        if (!args[4].isEmpty()) {
            rwidth = ((IntegerValue) args[4].itemAt(0)).getInt();
            if (args[4].hasMany()) {
                rheight = ((IntegerValue) args[4].itemAt(1)).getInt();
            }
        }

        String mimeType = args[3].itemAt(0).getStringValue();
        String formatName = mimeType.substring(mimeType.indexOf("/") + 1);
        
        //TODO currently ONLY tested for JPEG!!!
        InputStream inputImage = null;
        byte[] outputImage = null;
        
        try {
            //get the image data
            inputImage = ((BinaryValue) args[0].itemAt(0)).getInputStream();

            if (inputImage == null) {
                LOGGER.error("Unable to read image data!");
                return Sequence.EMPTY_SEQUENCE;
            }
            
            //scale the image
            outputImage = Convert.crop(inputImage, width, height, offsetX, offsetY, formatName, rwidth, rheight);

            if (outputImage == null) {
                LOGGER.error("Unable to get output image data!");
                return Sequence.EMPTY_SEQUENCE;
            }
            
            //return the new scaled image data
            return BinaryValueFromInputStream.getInstance(context, new Base64BinaryValueType(), new ByteArrayInputStream(outputImage));
        } catch (IOException | InterruptedException | IM4JavaException | XPathException e) {
            throw new XPathException(this, e.getMessage());
        }
    }
    
   
}
