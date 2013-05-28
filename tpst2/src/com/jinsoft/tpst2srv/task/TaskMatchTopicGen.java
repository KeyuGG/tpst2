package com.jinsoft.tpst2srv.task;

import com.jinsoft.tpst2srv.GameData;
import com.jinsoft.tpst2srv.GameMessage;
import com.jinsoft.tpst2srv.GameQuery;
import com.jinsoft.tpst2srv.GameResultHandler;
import com.jinsoft.tpst2srv.GameServer;
import com.jinsoft.tpst2srv.common.Global;
import com.jinsoft.tpst2srv.vo.VoResponseData;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.PeriodicTaskHandle;
import com.sun.sgs.app.Task;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KeyuGG
 */
public class TaskMatchTopicGen implements Serializable, ManagedObject, Task {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger( TaskMatchTopicGen.class.getName() );
    
    public static final int DELAY_MS = 30000;
    public static final int PERIOD_MS = 60000;
    
    private int fmatchid;
    private PeriodicTaskHandle taskHandler;
    private DataManager dataMgr;
    private TaskMatchTime taskMatchTime;
    private String matchBinding;
    private String sql;
    
    public TaskMatchTopicGen( int fmatchid ){
        matchBinding = GameServer.MATCH_BIND_PREFIX + Integer.toString( this.fmatchid ) + ".";
        this.fmatchid = fmatchid;
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
        //this.dataMgr = AppContext.getDataManager();
        //dataMgr.markForUpdate(this);
        
        try
        {
            sql = String.format( GameData.QUERY_GAME_CULLTOPIC, 0, Global.DATAFORMAT_HM.format(GameData.matchTime.get("emucurrTime")) );
            //LOG.log(Level.INFO, "生成公共题的查询: {0}", sql);
            GameQuery.query( sql, new GameResultHandler( GameMessage.GAME_CULL_TOPIC, null ) );
   
        } catch ( Exception e ){
            LOG.log(Level.INFO, "生成关联题的查询时出错: {0}", e.getMessage());
        }
        
        //queryData();
        //sendResponseData();
        //taskHandler.cancel();
    }
    
    /*
     *  初始化时间
     */
    
    private void queryData(){
        Map<String,Object> topic = null;
        for ( int i = 0; i< GameData.list_topic.size(); i++)
        {
            topic = GameData.list_topic.get(i);
            if ( topic.get("favailable").equals(1) )
            {
                GameData.list_topic.get(i).put("favailable", 0);
                break;
            }
        }
        
        if ( topic != null )
        {
            topic.put("ffinished", 0);
            GameData.list_matchTopic.add(topic);
        }
       
        //LOG.log(Level.SEVERE, "添加关联题:{0}:{1}", new Object[]{topic,GameData.list_matchTopic.size()});
            //LOG.log(Level.SEVERE, "事件计数:{0}", new Object[]{GameData.list_matchTopic.size()});

        List<Map<String,Object>> list_topicAvailable = new ArrayList<>();
        for ( Map<String,Object> o3: GameData.list_matchTopic )
            if ( o3.get("ffinished").equals(0))
                list_topicAvailable.add(o3);
        
        VoResponseData rd = new VoResponseData();
        rd.message = GameMessage.MESSAGE_GETMATCHTOPIC;
        rd.data = list_topicAvailable;

        sendChannelMessage( rd );
        //LOG.log(Level.SEVERE, "{0}:{1}", new Object[]{list_topicAvailable,list_topicAvailable.size()});
    }
    
    private void sendChannelMessage( VoResponseData rd ){
        
        Channel channel = AppContext.getChannelManager().getChannel( GameServer.CHANNEL_PRIMARY );
        ByteBuffer buf = null;

        try {
                buf = ByteBuffer.wrap(Global.SerializeAmf3( rd ));
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "关联题任务发送消息时出错:{0}", ex);
            }

        channel.send( buf );
    }
}
