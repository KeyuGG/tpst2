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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KeyuGG
 */
public class TaskMatchTime implements Serializable, ManagedObject, Task {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger( TaskMatchTime.class.getName() );
    
    public static final int DELAY_MS = 1000;
    public static final int PERIOD_MS = 1000;

    private long startTime;
    private long endTime;
    private long currTime;
    private long emucurrTime;
    private long morningTime;
    private double timeRatio;
    private int fmatchid;
    private PeriodicTaskHandle taskHandler;
    
    public TaskMatchTime( int fmatchid ){
        this.fmatchid = fmatchid;
        
        markRunningForMatch();
        initializeTime();
    }
    
    public long getStartTime(){
        return startTime;
    }

    public long getEndTime(){
        return endTime;
    }
    
    public long getCurrTime(){
        return currTime;
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
        
        currTime = System.currentTimeMillis();
        if ( currTime >= endTime ){
            markStoppedForMatch();
            taskHandler.cancel();
        }else{
            sendResponseData();
        }

        //LOG.log(Level.SEVERE, "\n开始时间:{0}\n结束时间:{1}\n时间比率:{2}\n当前虚拟时间:{3}\n今天凌晨时间:{4}", new Object[]{ Global.DATAFORMAT_TABLE.format(startTime),Global.DATAFORMAT_TABLE.format(endTime),timeRatio, Global.DATAFORMAT_TABLE.format(getEmuCurrTime()), Global.DATAFORMAT_TABLE.format(Global.getMorningTimeStamp()) });
        //LOG.log(Level.SEVERE, "\n开始时间:{0}\n结束时间:{1}\n时间比率:{2}\n当前虚拟时间:{3}\n今天凌晨时间:{4}", new Object[]{ startTime, endTime, timeRatio,  getEmuCurrTime(),  Global.getMorningTimeStamp() });

        //taskHandler.cancel();
    }
    
    /*
     *  初始化时间
     */
    private void initializeTime(){
        morningTime = Global.getMorningTimeStamp();
        for ( Map<String,Object> match: GameData.list_match )
            if ( match.get("fid").equals( fmatchid ) ){
                startTime = (long) match.get("fstarttime");
                endTime = (long) match.get("fendtime");
                timeRatio = (24 * GameData.TIME_MICROSECONDS_HOUR) / (endTime - startTime);
                break;
            }
    }
    
    private void sendResponseData(){
            VoResponseData rd = new VoResponseData();
            rd.message = GameMessage.MESSAGE_TASK_MATCH_TIME;
            
            Map<String, Object> data = new HashMap<>();
            
            data.put( "emucurrTime", getEmuCurrTime() );
            data.put( "currTime", currTime );
            
            GameData.setMatchTime( data );
            
            rd.data = data;
            
            sendChannelMessage( rd );
    }
    
    private void markStoppedForMatch(){
        for ( Map<String,Object> match: GameData.list_match )
            if ( match.get("fid").equals( fmatchid ) ){
                match.put("fstatus", GameData.MATCH_STATUS_STOPPED );
            }
    }
    
    private void markRunningForMatch(){
        for ( Map<String,Object> match: GameData.list_match )
            if ( match.get("fid").equals( fmatchid ) ){
                match.put("fstatus", GameData.MATCH_STATUS_RUNNING );
            }
    }
    
    public long getEmuCurrTime(){
        long passTime;
        passTime = System.currentTimeMillis() - startTime;
        //LOG.log(Level.INFO, "逝去的秒数: {0}", new Object[]{ passTime });
        
        emucurrTime = (long) ( morningTime + (passTime * timeRatio)) ;
                
        return emucurrTime;
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
