/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv;

import com.sun.sgs.app.AppContext;
import com.zero_separation.pds.sql.SQLConnection;
import com.zero_separation.pds.sql.SQLManager;
import com.zero_separation.pds.sql.SQLResultHandler;
import com.zero_separation.pds.sql.SQLStatement;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 *
 * @author KeyuGG
 */
public class GameQuery implements Serializable {

    private static final Logger LOG = Logger.getLogger(GameData.class.getName());
    
    private static SQLManager sqlmngr;
    private static SQLConnection connection;

    private static String DBSRV_URL                                             = "127.0.0.1";
    //private static String DBSRV_URL                                             = "125.66.152.100";
    //public static String DBSRV_URL                                            = "192.168.1.216";
    //public static String DBSRV_URL                                              = "192.168.0.144";
    private static String DBSRV_PORT                                            = "21633";
    private static String DBSRV_DBNAME                                          = "tpst_data";
    private static String DBSRV_USER                                            = "tpstadmin";
    private static String DBSRV_PASSWORD                                        = "kingdee";
    private static String DBSRV_DRIVER                                          = "org.postgresql.Driver";

    public synchronized static String getConnectionDriver(){
        return DBSRV_DRIVER;
    }
    
    public synchronized static String getConnectionString(){
        return "jdbc:postgresql://" + DBSRV_URL + ":" + DBSRV_PORT + "/" + DBSRV_DBNAME + "?user=" + DBSRV_USER + "&password=" + DBSRV_PASSWORD;
    }

    private synchronized static SQLConnection getConnection() {
        if ( sqlmngr == null) sqlmngr = AppContext.getManager(SQLManager.class);
        if ( connection == null ) connection = sqlmngr.createConnection( getConnectionDriver(), getConnectionString() );
        return connection;
    }
    
    public synchronized static void getdata( int message, Object data ) {
        
    }
    
    public static void query( String sq, SQLResultHandler handler ) {
        SQLStatement statement = new SQLStatement( sq );
        getConnection().performQuery( statement, handler );
    }
}
