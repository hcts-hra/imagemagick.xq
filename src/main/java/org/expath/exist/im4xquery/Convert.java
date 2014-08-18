/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.expath.exist.im4xquery;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.exist.xquery.Expression;
import org.exist.xquery.XPathException;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.Pipe;

/**
 *
 * @author zwobit <tobias AT existsolutions.com>
 * @version 1.0
 */

public class Convert {
    public static byte[] convert2ImageFormat(InputStream image, String format) throws IOException, InterruptedException, IM4JavaException {
        IMOperation iMOperation = new IMOperation();
        iMOperation.addImage("-");
        iMOperation.addImage( format + ":-");
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        Pipe pipeIn  = new Pipe(image, null);
        Pipe pipeOut = new Pipe(null, byteArrayOutputStream);
        
        ConvertCmd convertCmd = new ConvertCmd();
        convertCmd.setInputProvider(pipeIn);
        convertCmd.setOutputConsumer(pipeOut);
        convertCmd.run(iMOperation);
        
        return byteArrayOutputStream.toByteArray();
    }
    
    public static byte[] resize(InputStream image, Integer maxHeight, Integer maxWidth, String format) throws IOException, InterruptedException, IM4JavaException {
        IMOperation iMOperation = new IMOperation();
        iMOperation.addImage("-");
        iMOperation.resize(maxWidth,maxHeight);
        iMOperation.addImage(format +  ":-");
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        Pipe pipeIn  = new Pipe(image, null);
        Pipe pipeOut = new Pipe(null, byteArrayOutputStream);
        
        ConvertCmd convertCmd = new ConvertCmd();
        convertCmd.setInputProvider(pipeIn);
        convertCmd.setOutputConsumer(pipeOut);
        convertCmd.run(iMOperation);

        return byteArrayOutputStream.toByteArray();
    }
}
