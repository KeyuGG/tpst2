package com.jinsoft.tpst2srv.task;

import com.jinsoft.tpst2srv.GameData;
import com.jinsoft.tpst2srv.GameMessage;
import com.jinsoft.tpst2srv.GameServer;
import com.jinsoft.tpst2srv.common.Global;
import com.jinsoft.tpst2srv.vo.VoResponseData;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.PeriodicTaskHandle;
import com.sun.sgs.app.Task;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KeyuGG
 */
public class TaskMatchTeamObject implements Serializable, ManagedObject, Task {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger( TaskMatchTeamObject.class.getName() );
    
    public static final int DELAY_MS = 1000;
    public static final int PERIOD_MS = 1000;

    private int fmatchid;
    private PeriodicTaskHandle taskHandler;
    
    public TaskMatchTeamObject( int fmatchid ){
        this.fmatchid = fmatchid;

        markRunningForMatch();
    }

    public int getMatchId(){
        return fmatchid;
    }
    
    public PeriodicTaskHandle getTaskHandler(){
        return taskHandler;
    }
    
    public void setTaskHandler( PeriodicTaskHandle taskHandler ){
        this.taskHandler = taskHandler;
    }
    
    @Override
    public void run() throws Exception {
        AppContext.getDataManager().markForUpdate(this);
        
        sendResponseData();

        //taskHandler.cancel();
    }
    
    /*
     *  初始化时间
     */
    
    private void sendResponseData(){
            VoResponseData rd = new VoResponseData();
            rd.message = GameMessage.MESSAGE_GETMATCHTEAMOBJECT;

            List<Map<String, Object>> list_matchTeamCar = new ArrayList<>();
            for ( Map<String, Object> o: GameData.list_matchTeamCar ){
                if (o.get("fmatchid").equals(fmatchid))
                    list_matchTeamCar.add(o);
            }
            
            List<Map<String, Object>> list_matchTeamBike = new ArrayList<>();
            for ( Map<String, Object> o: GameData.list_matchTeamBike ){
                if (o.get("fmatchid").equals(fmatchid))
                    list_matchTeamBike.add(o);
            }

            List<Map<String, Object>> list_matchTeamPolice = new ArrayList<>();
            for ( Map<String, Object> o: GameData.list_matchTeamPolice ){
                if (o.get("fmatchid").equals(fmatchid))
                    list_matchTeamPolice.add(o);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("car", list_matchTeamCar);
            data.put("bike", list_matchTeamBike);
            data.put("police", list_matchTeamPolice);

            rd.data = data;

            sendChannelMessage( rd );
    }
    
    private void markStoppedForMatch(){
    }
    
    private void markRunningForMatch(){
    }
    
    private void sendChannelMessage( VoResponseData rd ){
        
        Channel channel = AppContext.getChannelManager().getChannel( GameServer.CHANNEL_PRIMARY );
        ByteBuffer buf = null;

        try {
                buf = ByteBuffer.wrap(Global.SerializeAmf3( rd ));
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "时间任务发送消息时出错:{0}", ex);
            }

        channel.send( buf );
    }    
}
