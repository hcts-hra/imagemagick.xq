package org.expath.exist.im4xquery;

import java.io.InputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.Pipe;
/**
 *
 * @author zwobit <tobias.krebs AT betterform.de>
 */


public class Convert {
    public static byte[] convert(InputStream image, String format) {
        IMOperation iMOperation = new IMOperation();
        iMOperation.addImage("-");
        iMOperation.addImage( format + ":-");
        
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
}
