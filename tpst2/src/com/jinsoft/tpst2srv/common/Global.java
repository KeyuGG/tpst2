/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv.common;

import com.jinsoft.tpst2srv.vo.VoUser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.Amf3Input;
import flex.messaging.io.amf.Amf3Output;
import flex.messaging.messages.AcknowledgeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.logging.Level;

/**
 *
 * @author KeyuGG
 */
public class Global {
    
    private static final Logger LOG = Logger.getLogger(Global.class.getName());
    
    public static final SimpleDateFormat DATAFORMAT_YMD = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATAFORMAT_HM = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat DATAFORMAT_LOG = new SimpleDateFormat("yyyy\u5e74MM\u6708dd\u65e5 HH\u65f6mm\u5206ss\u79d2 E ");
    public static final SimpleDateFormat DATAFORMAT_TABLE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATAFORMAT_ADMIN = new SimpleDateFormat("yyyy-MM-dd");
    
    
    /*
    public static String PORT_TCP;
    public static String PORT_UDP;
    public static String PORT_AMF;
    */
    
    public static final String REMOTE_GET_DEPARTMENT				= "remoteGetDepartment";
    public static final String REMOTE_GET_DIVISION                              = "remoteGetDivision";
    public static final String REMOTE_GET_BRANCH				= "remoteGetBranch";
    public static final String REMOTE_GET_USER					= "remoteGetUser";
    public static final String REMOTE_GET_POST					= "remoteGetPost";
    public static final String REMOTE_GET_TEAM					= "remoteGetTeam";
    public static final String REMOTE_GET_MATCH					= "remoteGetMatch";
    
    public static int SAFESANDBOX_PORT                                          = 21683;
    
    public static ArrayList DATA_DEPARTMENT;
    public static ArrayList DATA_DIVISION;
    public static ArrayList DATA_BRANCH;
    
    public static ArrayList<VoUser> CURRENT_USER;
    
    public static final byte CLIENT_MESSAGE_GETMATCHLIST = 0x50;
    public static final byte CLIENT_MESSAGE_GETTEAMLIST = 0x51;
    
    public static final String MESSAGE_CHARSET = "UTF-8";
    
    public static String getAdmin()
    {
        return DATAFORMAT_ADMIN.format(new Date());
    }
    
    public static long getMorningTimeStamp(){ 
        Calendar cal = Calendar.getInstance(); 
        cal.set(Calendar.HOUR_OF_DAY, 0); 
        cal.set(Calendar.SECOND, 0); 
        cal.set(Calendar.MINUTE, 0); 
        cal.set(Calendar.MILLISECOND, 0); 
        return cal.getTimeInMillis(); 
    }
    
    /*
    public static byte[] SerializeToAmf3Object(Object object, String correlationId)
    {
        try (Amf3Output amf3Output = new Amf3Output(SerializationContext.getSerializationContext()))
        {
            //Amf3Output amf3Output = new Amf3Output(SerializationContext.getSerializationContext());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            amf3Output.setOutputStream(baos);

            AcknowledgeMessage message = new AcknowledgeMessage();
            message.setBody(object);

            message.setCorrelationId(correlationId);
            amf3Output.writeObject(message);
            byte[] res = baos.toByteArray();
            amf3Output.close();
            return res;
        }
        catch (Exception e)
        {
            LOG.log(Level.SEVERE, e.getMessage());
            return null;
        }
    }*/
    
    public static int decodeInt(ByteBuffer message) {
        try {
            return message.asIntBuffer().get();
        } catch ( Exception ex ) {
            LOG.log(Level.WARNING, "在转换客户端发来的消息时发生错误: {0}", ex.getMessage() );
            return -1;
        }
    }    
    
    public static String decodeString(ByteBuffer message) {
        try {
            byte[] bytes = new byte[message.remaining()];
            message.get(bytes);
            return new String(bytes, MESSAGE_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new Error("Required character set " + MESSAGE_CHARSET +
                " not found", e);
        }
    }
    
   public static byte[] SerializeAmf3(Object data) throws IOException
    {
        SerializationContext context = getSerializationContext();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try (Amf3Output amf3Output = new Amf3Output(context)) {
            amf3Output.setOutputStream(bout);
            amf3Output.writeObject(data);
            amf3Output.flush();
        }
        return bout.toByteArray();
    }
    
    public static Object DeSerializeAmf3(byte[] b) throws ClassNotFoundException, IOException
    {
        SerializationContext context = getSerializationContext();
        InputStream bIn = new ByteArrayInputStream(b);
        Amf3Input amf3Input = new Amf3Input(context);
        amf3Input.setInputStream(bIn);
        return amf3Input.readObject();
    }
    
    public static SerializationContext getSerializationContext() {
        
        //Threadlocal SerializationContent
        SerializationContext serializationContext = SerializationContext.getSerializationContext();
        serializationContext.enableSmallMessages = true;
        serializationContext.instantiateTypes = true;
        //use _remoteClass field
        serializationContext.supportRemoteClass = true;
        //false  Legacy Flex 1.5 behavior was to return a java.util.Collection for Array
        //ture New Flex 2+ behavior is to return Object[] for AS3 Array
        serializationContext.legacyCollection = false;

        serializationContext.legacyMap = false;
        //false Legacy flash.xml.XMLDocument Type
        //true New E4X XML Type
        serializationContext.legacyXMLDocument = false;

        //determines whether the constructed Document is name-space aware
        serializationContext.legacyXMLNamespaces = false;
        serializationContext.legacyThrowable = false;
        serializationContext.legacyBigNumbers = false;

        serializationContext.restoreReferences = false;
        serializationContext.logPropertyErrors = false;
        serializationContext.ignorePropertyErrors = true;

        return serializationContext;

    /*
    serializationContext.enableSmallMessages = serialization.getPropertyAsBoolean(ENABLE_SMALL_MESSAGES, true);
    serializationContext.instantiateTypes = serialization.getPropertyAsBoolean(INSTANTIATE_TYPES, true);
    serializationContext.supportRemoteClass = serialization.getPropertyAsBoolean(SUPPORT_REMOTE_CLASS, false);
    serializationContext.legacyCollection = serialization.getPropertyAsBoolean(LEGACY_COLLECTION, false);
    serializationContext.legacyMap = serialization.getPropertyAsBoolean(LEGACY_MAP, false);
    serializationContext.legacyXMLDocument = serialization.getPropertyAsBoolean(LEGACY_XML, false);
    serializationContext.legacyXMLNamespaces = serialization.getPropertyAsBoolean(LEGACY_XML_NAMESPACES, false);
    serializationContext.legacyThrowable = serialization.getPropertyAsBoolean(LEGACY_THROWABLE, false);
    serializationContext.legacyBigNumbers = serialization.getPropertyAsBoolean(LEGACY_BIG_NUMBERS, false);
    boolean showStacktraces = serialization.getPropertyAsBoolean(SHOW_STACKTRACES, false);
    if (showStacktraces && Log.isWarn())
    log.warn("The " + SHOW_STACKTRACES + " configuration option is deprecated and non-functional. Please remove this from your configuration file.");
    serializationContext.restoreReferences = serialization.getPropertyAsBoolean(RESTORE_REFERENCES, false);
    serializationContext.logPropertyErrors = serialization.getPropertyAsBoolean(LOG_PROPERTY_ERRORS, false);
    serializationContext.ignorePropertyErrors = serialization.getPropertyAsBoolean(IGNORE_PROPERTY_ERRORS, true);
     */
    }    
}
