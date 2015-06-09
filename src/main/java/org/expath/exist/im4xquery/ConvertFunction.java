package org.expath.exist.im4xquery;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.io.output.ByteArrayOutputStream;
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
import org.exist.xquery.value.StringValue;
import org.exist.xquery.value.Type;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.Pipe;

/**
 *
 * @author zwobit <tobias AT existsolutions.com>
 * @version 1.0
 */


public class ConvertFunction extends BasicFunction {
    public final static FunctionSignature signature = new FunctionSignature(
            new QName("convert", Im4XQueryModule.NAMESPACE_URI, Im4XQueryModule.PREFIX),
            "converts an image",
            new SequenceType[]{
                new FunctionParameterSequenceType("image", Type.BASE64_BINARY, Cardinality.EXACTLY_ONE, "The image data"),
                new FunctionParameterSequenceType("convertOptions", Type.STRING, Cardinality.EXACTLY_ONE, "Option string for convert"),
            },
            new FunctionReturnSequenceType(Type.BASE64_BINARY, Cardinality.ZERO_OR_ONE, "the converted image or an empty sequence if $image is invalid")   
    );

    public ConvertFunction(XQueryContext context, FunctionSignature signature) {
        super(context, signature);
    }
    
    @Override
    public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {
        if(args[0].isEmpty() || args[1].isEmpty()) {
            return Sequence.EMPTY_SEQUENCE;
        }
        byte[] image = convertWithOptions(((BinaryValue)args[0].itemAt(0)).getInputStream(), ((StringValue) args[1]).toString());
        return BinaryValueFromInputStream.getInstance(context, new Base64BinaryValueType(), new ByteArrayInputStream(image));
    }
    
    
    
    public byte[] convertWithOptions(InputStream image, String options) {
        IMOperation iMOperation = new IMOperation();
        iMOperation.addImage("-");
        iMOperation.addRawArgs(options);
        
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        Pipe pipeIn  = new Pipe(image, null);
        Pipe pipeOut = new Pipe(null, byteArrayOutputStream);
        
        ConvertCmd convertCmd = new ConvertCmd();
        convertCmd.setInputProvider(pipeIn);
        convertCmd.setOutputConsumer(pipeOut);
        try {
            convertCmd.run(iMOperation);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
        
        return byteArrayOutputStream.toByteArray();
    }
    
    private List<String> parseOptions(String options) {
        List<String> optionsList = new LinkedList<String>();
        
        StringTokenizer stringTokenizer = new StringTokenizer(options, " ");
        
        while(stringTokenizer.hasMoreTokens()) {
            optionsList.add(stringTokenizer.nextToken());
        }
        
        return optionsList;
    }
}
