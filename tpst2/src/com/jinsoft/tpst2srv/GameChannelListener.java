package com.jinsoft.tpst2srv;

import static com.jinsoft.tpst2srv.GameData.list_match;
import com.jinsoft.tpst2srv.common.Global;
import com.jinsoft.tpst2srv.task.TaskMatchPubTopicGen;
import com.jinsoft.tpst2srv.task.TaskMatchTime;
import com.jinsoft.tpst2srv.task.TaskMatchTopicGen;
import com.jinsoft.tpst2srv.vo.VoResponseData;
import com.jinsoft.tpst2srv.vo.VoMessage;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelListener;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.NameNotBoundException;
import com.sun.sgs.app.PeriodicTaskHandle;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KeyuGG
 */
public class GameChannelListener implements Serializable, ChannelListener{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger( GameChannelListener.class.getName() );
    protected static final String TEAM_BIND_PREFIX = "Team.";
    protected static final String USER_BIND_PREFIX = "User.";

    @Override
    public void receivedMessage( Channel channel, ClientSession sender, ByteBuffer message ) {
        if ( channel.getName().equals(GameServer.CHANNEL_PRIMARY) )
        {
            try {
                byte[] bytes = new byte[ message.remaining() ];
                message.get(bytes);
                VoMessage messagedata = ( VoMessage )Global.DeSerializeAmf3(bytes);
                sendResponseData( channel, sender, messagedata );
            } catch ( ClassNotFoundException | IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void sendResponseData( Channel channel, ClientSession sender, VoMessage message ){
        VoResponseData rd = new VoResponseData();
        rd.message = message.message;
        switch ( message.message ) {
            case GameMessage.MESSAGE_GETMATCHTEAMPOSTLIST: rd.data = GameData.getMatchTeamPostList( message.id ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_GETMATCHTEAMCAR: rd.data = GameData.getMatchTeamCar( message.id ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_GETMATCHTEAMBIKE: rd.data = GameData.getMatchTeamBike( message.id ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_GETMATCHTEAMPOLICE: rd.data = GameData.getMatchTeamPolice( message.id ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_MATCH_START: startMatch( message.id);  rd.message = GameMessage.MESSAGE_MATCH_STATUS; rd.data = GameData.getMatchStatus( message.id ); sendMessage( rd ); break;
        }
    }
    
    private void startMatch ( int fmatchid ){
        
        long currentTime;
        currentTime = System.currentTimeMillis();
        
        //初始化对抗任务, 写开始时间
        for ( Map<String,Object> match: list_match )
            if ( match.get("fid").equals(fmatchid) ){
                match.put( "fstarttime", currentTime );
                match.put( "fendtime", currentTime + 3 * GameData.TIME_MICROSECONDS_HOUR );
                //LOG.log(Level.INFO, "{0}", new Object[]{match});
                break;
            }

        //初始化计时器任务
        String matchBinding = GameServer.MATCH_BIND_PREFIX + Integer.toString( fmatchid ) + ".";
        DataManager dataMgr = AppContext.getDataManager();
        PeriodicTaskHandle taskHanlder;
        
        // 发送时间任务
        TaskMatchTime taskMatchTime = new TaskMatchTime( fmatchid );
        dataMgr.setBinding(matchBinding+"."+TaskMatchTime.class, taskMatchTime);
        taskHanlder = AppContext.getTaskManager().schedulePeriodicTask( taskMatchTime, TaskMatchTime.DELAY_MS, TaskMatchTime.PERIOD_MS );
        taskMatchTime.setTaskHandler( taskHanlder );
        LOG.log( Level.SEVERE, "已创建任务计时器并启动, 任务ID: {0}, 结束时间: {1}", new Object[]{ fmatchid, Global.DATAFORMAT_TABLE.format( currentTime + 3 * GameData.TIME_MICROSECONDS_HOUR ) } );
        
        // 生成 TOPIC 任务
        TaskMatchTopicGen taskMatchTopicGen = new TaskMatchTopicGen( fmatchid );
        dataMgr.setBinding(matchBinding+"."+TaskMatchTopicGen.class, taskMatchTopicGen);
        taskHanlder = AppContext.getTaskManager().schedulePeriodicTask( taskMatchTopicGen, TaskMatchTopicGen.DELAY_MS, TaskMatchTopicGen.PERIOD_MS );
        taskMatchTopicGen.setTaskHandler( taskHanlder );
        LOG.log( Level.SEVERE, "已创建题干生成器并启动, 任务ID: {0}, 时间间隔: {1}", new Object[]{ fmatchid, TaskMatchTopicGen.PERIOD_MS } );
        
        // 生成 PUBTOPIC 任务
        TaskMatchPubTopicGen taskMatchPubTopicGen = new TaskMatchPubTopicGen( fmatchid );
        dataMgr.setBinding(matchBinding+"."+TaskMatchPubTopicGen.class, taskMatchPubTopicGen);
        taskHanlder = AppContext.getTaskManager().schedulePeriodicTask( taskMatchPubTopicGen, TaskMatchPubTopicGen.DELAY_MS, TaskMatchPubTopicGen.PERIOD_MS );
        taskMatchPubTopicGen.setTaskHandler( taskHanlder );
        LOG.log( Level.SEVERE, "已创建公共题干生成器并启动, 任务ID: {0}, 时间间隔: {1}", new Object[]{ fmatchid, TaskMatchPubTopicGen.PERIOD_MS } );

        String department = String.valueOf(GameData.list_department.size());
        String division = String.valueOf(GameData.list_division.size());
        String branch = String.valueOf(GameData.list_branch.size());
        String user = String.valueOf(GameData.list_user.size());
        String group = String.valueOf(GameData.list_group.size());
        String groupuser = String.valueOf(GameData.list_groupuser.size());
        String pubtopic = String.valueOf(GameData.list_pubtopic.size());
        String topic = String.valueOf(GameData.list_topic.size());
        String pubquiz = String.valueOf(GameData.list_pubquiz.size());
        String quiz = String.valueOf(GameData.list_quiz.size());
        String quizpost = String.valueOf(GameData.list_quizpost.size());
        String answer = String.valueOf(GameData.list_answer.size());

        String log = "总队数:" + department + ", 支队数:" + division + ", 大队数:" + branch + ", 用户数:" + user + ", 用户组:" + group + "\n" +
                "关联事件数:" + topic + ", 关联问题数:" + quiz + ", 公共事件数:" + pubtopic + ", 公共问题数:" + pubquiz + ", 答案数:" + answer;
        System.out.println( log );
        // sendMessage( GameData.getMatchTeamObject( fmatchid ) );

        // 发送任务对象任务
        /*TaskMatchTeamObject taskMatchTeamObject = new TaskMatchTeamObject( fmatchid );
        dataMgr.setBinding(matchBinding+"."+TaskMatchTeamObject.class, taskMatchTeamObject);
        taskHanlder = AppContext.getTaskManager().schedulePeriodicTask( taskMatchTeamObject, TaskMatchTeamObject.DELAY_MS, TaskMatchTeamObject.PERIOD_MS );
        taskMatchTeamObject.setTaskHandler( taskHanlder );
        */
    }    

    public GameUser getUser( ClientSession session ){
        DataManager dataMgr = AppContext.getDataManager();
        String userName = session.getName();
        String userBinding = USER_BIND_PREFIX + userName;
        GameUser user = null;
        try {
            user = ( GameUser ) dataMgr.getBinding( userBinding );
        } catch ( NameNotBoundException ex ){
            LOG.log(Level.WARNING, "在从全部加入小队中移除时发生 {0}", ex.getMessage());
        }
        return user;
    }
    
    private void userLogout( ClientSession session ){
        GameUser user = getUser( session );
        DataManager dataMgr = AppContext.getDataManager();
        dataMgr.removeBinding( USER_BIND_PREFIX + user.getName() );
        user.disconnected( true );
    }

    private void removeFromAllPosts( ClientSession session ){
        GameUser user = getUser(session);
        if ( user == null ) return;
        for ( int i=0; i< GameData.list_matchTeamPost.size(); i++ ){
            Map<String,Object> p = GameData.list_matchTeamPost.get(i);
            if ( p.get("fuserid").equals(user.getUserInfo().get("fid"))){
                p.put("fuserid", -1);
                p.put("fusername", "");
            }
        }
    }

    private void removeFromAllTeams( ClientSession session ){
        GameUser user = getUser(session);
        GameTeam team = null;
        // 小队匹配
        String teamBinding;
        try{
            DataManager dataMgr = AppContext.getDataManager();
            for ( int i=0; i< GameData.list_matchTeam.size(); i++ ){
                Map<String,Object> t = GameData.list_matchTeam.get(i);
                teamBinding = TEAM_BIND_PREFIX + t.get("fbranchname") + "."+ t.get("fname");
                team = ( GameTeam ) dataMgr.getBinding( teamBinding );
                team.removeUser( user );
            }
        } catch ( NameNotBoundException ex ){
            LOG.log(Level.WARNING, "在从全部加入小队中移除时发生 {0}", ex.getMessage());
        }
    }

    private boolean joinTeam( Object data, ClientSession session ){
        // 小队匹配
        Map<String,Object> d = (Map<String,Object>) data;
        String teamBinding = "";
        for ( int i=0; i< GameData.list_matchTeam.size(); i++ ){
            Map<String,Object> t = GameData.list_matchTeam.get(i);
            if ( t.get("fid").equals(d.get("fteamid")))
            {
                teamBinding = TEAM_BIND_PREFIX + t.get("fbranchname") + "."+ t.get("fname");
                break;
            }
        }

        DataManager dataMgr = AppContext.getDataManager();
        GameTeam team = null;
        try{
            team = (GameTeam)dataMgr.getBinding(teamBinding);
        }catch (NameNotBoundException ex){
            LOG.log(Level.WARNING, "未找到系统对 Team 对象的绑定 {0}", ex.getMessage());
            return false;
        }
        
        // 岗位匹配
        if ( team != null ){
            for ( int i=0; i< GameData.list_matchTeamPost.size(); i++ ){
                Map<String,Object> p = GameData.list_matchTeamPost.get(i);
                if ( d.get("fteamid").equals(p.get("fteamid")) && d.get("fpostid").equals(p.get("fpostid")) && p.get("fuserid").equals( -1 ) )
                {
                    String userName = session.getName();
                    String userBinding = USER_BIND_PREFIX + userName;
                    // try to find user object, if non existent then create
                    GameUser user = null;
                    try {
                        user = ( GameUser ) dataMgr.getBinding( userBinding );
                        removeFromAllTeams ( session );
                        removeFromAllPosts ( session );
                        team.addUser(user);
                        
                        //LOG.log(Level.INFO, "{0}:{0}", new Object[]{ GameData.list_matchTeamPost.get(i).get("fuserid"), GameData.list_matchTeamPost.get(i).get("fusername")});
                        
                        p.put("fuserid", user.getUserInfo().get("fid"));
                        p.put("fusername", user.getUserInfo().get("fname"));
                        
                        VoResponseData rd = new VoResponseData();
                        rd.message = GameMessage.MESSAGE_GETMATCHTEAMPOSTLIST;
                        rd.data = GameData.getMatchTeamPostList( team.getId() );
                        sendMessage( rd );
                        
                        //LOG.log(Level.INFO, "{0}:{1}", new Object[]{ GameData.list_matchTeamPost.get(i).get("fuserid"), GameData.list_matchTeamPost.get(i).get("fusername")});
                        return true;
                    } catch (NameNotBoundException ex) {
                        LOG.log(Level.WARNING, "在加入小队时发生找不到绑定的错误 {0}", ex.getMessage());
                    }
                }
            }
        }
        return false;
    }

    private static void sendMessage( Object data ){
        Channel channel = AppContext.getChannelManager().getChannel( GameServer.CHANNEL_PRIMARY );
        ByteBuffer buf = null;

        try {
                buf = ByteBuffer.wrap(Global.SerializeAmf3( data ));
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "接收到数据处理结果时出错:{0}", ex);
            }

        channel.send( buf );
    }
}
