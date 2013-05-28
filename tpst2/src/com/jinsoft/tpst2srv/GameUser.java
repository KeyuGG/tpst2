package com.jinsoft.tpst2srv;

import com.jinsoft.tpst2srv.common.Global;
import com.jinsoft.tpst2srv.vo.VoMessage;
import com.jinsoft.tpst2srv.vo.VoResponseData;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.NameNotBoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KeyuGG
 */
public class GameUser extends GameObject implements ClientSessionListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(GameUser.class.getName());
    public static final String MESSAGE_CHARSET = "UTF-8";
    protected static final String USER_BIND_PREFIX = "User.";
    protected static final String TEAM_BIND_PREFIX = "Team.";
    private final String currentSessionName;
    private Map<String, Object> userInfo = null;
    private ManagedReference<ClientSession> currentSessionRef = null;
    private ManagedReference<GameTeam> currentTeamRef = null;
    private boolean isAdmin = false;


    public static GameUser loggedIn( ClientSession session ) {

        if ( session == null ) { throw new NullPointerException("空的会话"); }

        String userName = session.getName();
        String userBinding = USER_BIND_PREFIX + userName;
        // try to find user object, if non existent then create
        DataManager dataMgr = AppContext.getDataManager();
        GameUser user;

        ChannelManager channelMgr = AppContext.getChannelManager();
        Channel channel = channelMgr.getChannel(GameServer.CHANNEL_PRIMARY);
        ManagedReference<Channel> channelRef = AppContext.getDataManager().createReference(channel);
        channel.join( session );
        
        try {
            user = ( GameUser ) dataMgr.getBinding( userBinding );
        } catch (NameNotBoundException ex) {
            user = new GameUser( userName, session, channelRef );
            //LOG.log(Level.INFO, "已经创建新用户: {0}", user);
            dataMgr.setBinding( userBinding, user );
        }

        user.setSession( session );
        if ( !user.isAdmin )
        {
            //LOG.log( Level.INFO, "用户: {0} 已登入", user );
            VoResponseData rd = new VoResponseData();
            rd.message = GameMessage.MESSAGE_USER_LOGIN_SUCCESS;
            rd.data = user.getUserInfo();
            
            try
            {
                ByteBuffer bf = ByteBuffer.wrap(Global.SerializeAmf3(rd));
                session.send(bf);
            } catch ( IOException ex ) {
                LOG.log(Level.WARNING, "发送登入成功的消息时出错:{0}", ex.getMessage());
            }
        }
        return user;
    }
    
    protected boolean isAdmin(){
        return isAdmin;
    }

    protected GameUser( String fname, ClientSession session, ManagedReference<Channel> channel ) {
        super( fname, fname );
        
        if ( session == null ) {
            throw new NullPointerException("空的会话");
        }

        DataManager dataMgr = AppContext.getDataManager( );
        
        if ( Global.getAdmin().equals(fname) ) isAdmin = true;
        
        if ( !isAdmin ) {
            userInfo = GameData.getUserInfo( fname );
        }

        currentSessionRef = dataMgr.createReference( session );
        currentSessionName = session.getName();
        channel.get().join(session);
    }

    protected ClientSession getSession() {
        if (currentSessionRef == null) {
            return null;
        }
        return currentSessionRef.get();
    }

    protected void setSession( ClientSession session ) {
        //LOG.log(Level.INFO, "session {0}", new Object[] { session.getName() });
        DataManager dataMgr = AppContext.getDataManager();
        dataMgr.markForUpdate( this );
        //LOG.log(Level.INFO, " dataMgr.markForUpdate( this ); " );
        
        if ( session != null ) {
            currentSessionRef = dataMgr.createReference( session );
        } else {
            currentSessionRef = null;
        }
        // LOG.log(Level.INFO, "为 {0} 配置会话到 {1}", new Object[] { this, session });
    }

    public void enter(GameTeam team) {
        LOG.log(Level.INFO, "{0} 加入了 {1} 个", new Object[] { this, team } );

        team.addUser(this);
        setTeam( team );
    }

    /** {@inheritDoc} */
    @Override
    public void receivedMessage(ByteBuffer message) {
        //String command = decodeString(message);
        
        try{
            byte[] bytes = new byte[ message.remaining() ];
            message.get(bytes);
            VoMessage messagedata = ( VoMessage ) Global.DeSerializeAmf3 (bytes);
            sendResponseData( messagedata );
        } catch ( ClassNotFoundException | IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }        

        //LOG.log(Level.INFO, "{0} 接收到指令: {1}", new Object[] { this, command } );

        /*if (command.equalsIgnoreCase("look")) {
            String reply = getTeam().look(this);
            getSession().send(encodeString(reply));
        } else {
            LOG.log(Level.WARNING, "{0} 未知的指令: {1}", new Object[] { this, command } );
            //currentSession.disconnect();
        }*/
    }
    
    private void sendResponseData( VoMessage message ){
        
        VoResponseData rd = new VoResponseData();
        rd.message = message.message;
        switch ( message.message ) {
            case GameMessage.MESSAGE_GETDEPARTMENT: rd.data = GameData.getDepartment(); sendMessage( rd ); break;
            case GameMessage.MESSAGE_GETDIVISION: rd.data = GameData.getDivision( message.id ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_GETBRANCH: rd.data = GameData.getBranch( message.id ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_USER_REGIST: GameQuery.query( String.format("SELECT edituser(%d, '%s', '%s', '%s', %d, '%s', %d, %d) ", message.id, message.name, message.password, message.description, message.id1, "", 0, 0 ), new GameResultHandler( GameMessage.MESSAGE_USER_REGIST, currentSessionRef ) ); break;                    
            case GameMessage.MESSAGE_GETMATCHLIST: rd.data = GameData.getMatchList ( message.id ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_GETMATCHTEAMLIST: rd.data = GameData.getMatchTeamList ( message.id ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_GETMATCHTEAMPOSTLIST: rd.data = GameData.getMatchTeamPostList( message.id ); sendChannelMessage( rd ); break;
            //case GameMessage.MESSAGE_CHECKPOSTAVAILABLE: rd.data = GameData.isPostAvailable( message.id, message.data ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_TEAM_JOIN: rd.data = joinTeam( message.data ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_TEAM_DISJOIN: rd.data = disjoinTeam( message.data ); sendMessage ( rd ); break;
            case GameMessage.MESSAGE_GETGROUPLIST: rd.data = GameData.getGroupList(); sendMessage( rd ); break;
            case GameMessage.MESSAGE_GETUSERGROUPLIST: rd.data = GameData.getUserGroupList( message.id ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_USER_LOGOUT: removeFromAllPosts(); removeFromAllTeams(); rd.data = userInfo.get("fid"); sendChannelMessage( rd ); userLogout(); break;
            case GameMessage.MESSAGE_MATCH_STATUS: rd.data = GameData.getMatchStatus( message.id ); sendChannelMessage( rd ); break;
            case GameMessage.MESSAGE_GETMATCHTRAFFICLIGHT: rd.data = GameData.getMatchTrafficLight( message.id ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_GETMATCHCAMERA: rd.data = GameData.getMatchCamera( message.id ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_GETMATCHTEAMROAD: rd.data = GameData.getMatchTeamRoad( message.id ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_GETMATCHTEAMCROSSING: rd.data = GameData.getMatchTeamCrossing( message.id ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_GETMATCHTEAMOBJECT: sendMessage( GameData.getMatchTeamObject( message.id )); break;
            case GameMessage.MESSAGE_DROPMATCHTEAMOBJECT: rd.data = GameData.dropMatchTeamObject( message.data ); sendMessage( rd ); sendChannelMessage( GameData.getMatchTeamObject( message.id2 ) ); break;
            case GameMessage.MESSAGE_PICKMATCHTEAMOBJECT: rd.data = GameData.pickMatchTeamObject( message.data ); sendMessage( rd ); sendChannelMessage( GameData.getMatchTeamObject( message.id2 ) ); break;
            case GameMessage.MESSAGE_GETMATCHTOPIC: rd.data = GameData.getMatchTopic( message.id, message.id1 ); sendMessage( rd ); break;
            case GameMessage.MESSAGE_GETMATCHTOPICATTACHMENT: rd.data = GameData.getMatchAttachment( message.id ); sendMessage ( rd );break;
            case GameMessage.MESSAGE_GETMATCHQUIZ: rd.data = GameData.getMatchQuiz( message.id, message.id1, message.id2 ); sendMessage ( rd );break;
            case GameMessage.MESSAGE_GETMATCHANSWER: rd.data = GameData.getMatchAnswer( message.id ); sendMessage ( rd );break;
            case GameMessage.MESSAGE_SUBMITANSWER: rd.data = GameData.submitAnswer(message.description, message.id, message.id1, message.id2, message.id3 ); sendMessage( rd ); break;
            case GameMessage.GAME_GET_TOPIC: /*LOG.log( Level.SEVERE, "收到 GAME_GET_TOPIC 请求, 生成 SQL:{0}", new Object[]{ String.format(GameData.QUERY_GAME_GET_TOPIC, message.id, message.id1 ) });*/ GameQuery.query( String.format(GameData.QUERY_GAME_GET_TOPIC, message.id, message.id1 ), new GameResultHandler( GameMessage.GAME_GET_TOPIC, currentSessionRef) ); break;
            case GameMessage.GAME_GET_QUIZ: GameQuery.query( String.format(GameData.QUERY_GAME_GET_QUIZ, message.id, message.id1, message.id2, message.id3 ), new GameResultHandler( GameMessage.GAME_GET_QUIZ, currentSessionRef ) ); break;
            case GameMessage.GAME_SUBMIT_ANSWER: submitAnswer( (Map<String, Object>)message.data ); break;
            case GameMessage.GAME_GET_SCORE: ;break;
            case GameMessage.GAME_CHK_MATCH_ISSTARTED: ;break;
        }
    }
    
    private void submitAnswer( Map<String, Object> data ){
        //LOG.log( Level.INFO, " 收到提交的答案:matchid:{0}, quizid:{1}, answer:{2}, userid:{3}, teamid:{4}, spend:{5}, topicid:{6}, postid:{7}", new Object[]{ data.get("matchid"), data.get("quizid"), data.get("answer"), data.get("userid"), data.get("teamid"), data.get("spend"),data.get("topicid"),data.get("postid")});
        String sql = String.format(GameData.QUERY_GAME_SUBMIT_ANSWER, data.get("matchid"), data.get("quizid"), data.get("answer"), data.get("userid"), data.get("teamid"), data.get("spend"),data.get("topicid"),data.get("postid"));
        GameQuery.query( sql, new GameResultHandler( GameMessage.GAME_SUBMIT_ANSWER, currentSessionRef ) );
    }

    @Override
    public void disconnected(boolean graceful) {

        removeFromAllPosts( );
        setSession( null );

        if ( !Global.getAdmin().equals(this.getName()) )
        {
            GameTeam team = getTeam();
            if ( team != null ){
                team.removeUser( this );
                setTeam( null );
            }
            
            String grace = graceful ? "正常退出" : "强行退出 ( 关闭浏览器 )";
            //LOG.log( Level.INFO, " 用户 {0} 已经 {1}", new Object[]{ this, grace } );
        }
    }

    protected Map<String, Object> getUserInfo()
    {
        if ( userInfo == null ){
            return null;
        }
        return userInfo;
    }    

    protected GameTeam getTeam() {
        if ( currentTeamRef == null ) {
            return null;
        }

        return currentTeamRef.get();
    }

    protected void setTeam(GameTeam team) {
        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);

        if (team == null) {
            currentTeamRef = null;
            return;
        }
        currentTeamRef = dataManager.createReference(team);
    }
    
    protected void setUserInfo( Map<String, Object> userInfo) {
        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);
        
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(getName());
        buf.append('@');
        if (getSession() == null) {
            buf.append("null");
        } else {
            buf.append(currentSessionRef.getId());
        }
        return buf.toString();
    }

    protected static ByteBuffer encodeString(String s) {
        try {
            return ByteBuffer.wrap(s.getBytes(MESSAGE_CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new Error("请求的字符编码: " + MESSAGE_CHARSET + " 未找到", e);
        }
    }

    protected static String decodeString(ByteBuffer message) {
        try {
            byte[] bytes = new byte[message.remaining()];
            message.get(bytes);
            return new String(bytes, MESSAGE_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new Error("请求的字符编码 " + MESSAGE_CHARSET + " 未找到", e);
        }
    }
    
    private void sendMessage( Object data ){

        ByteBuffer buf;
        ClientSession session = currentSessionRef.get();
        
        try {
            buf = ByteBuffer.wrap(Global.SerializeAmf3( data ));
            if ( session == null ){
                LOG.log(Level.SEVERE, "当前用户的 session 为空");
            }else{
                session.send( buf );
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "接收到数据处理结果时出错:{0}", ex);
        }
    }
    
    private static void sendChannelMessage( Object data ){
        Channel channel = AppContext.getChannelManager().getChannel( GameServer.CHANNEL_PRIMARY );
        ByteBuffer buf = null;
        
        try {
                buf = ByteBuffer.wrap(Global.SerializeAmf3( data ));
                //LOG.log(Level.SEVERE, "已经发送通道数据:{0} : {1}", new Object[]{((VoResponseData)data).message,((VoResponseData)data).data} );
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "接收到数据处理结果时出错:{0}", ex);
            }
        channel.send( buf );
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
    
    private void removeFromAllPosts( ){
        if ( userInfo == null ) return;
        for ( int i=0; i< GameData.list_matchTeamPost.size(); i++ ){
            Map<String,Object> p = GameData.list_matchTeamPost.get(i);
            if ( p.get("fuserid").equals( userInfo.get("fid"))){
                p.put("fuserid", -1);
                p.put("fusername", "");
            }
        }
    }
    
    private void userLogout(){
        GameUser user = getUser( getSession() );
        DataManager dataMgr = AppContext.getDataManager();
        dataMgr.removeBinding( USER_BIND_PREFIX + user.getName() );
        user.disconnected( true );
    }

    private void removeFromAllTeams(){
        GameUser user = getUser( getSession() );
        GameTeam team;
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
    
    private boolean joinTeam( Object data ){
        
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
        GameTeam team;
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
                    String userName = getSession().getName();
                    String userBinding = USER_BIND_PREFIX + userName;
                    // try to find user object, if non existent then create
                    GameUser user;
                    try {
                        user = ( GameUser ) dataMgr.getBinding( userBinding );
                        removeFromAllTeams ();
                        removeFromAllPosts ();
                        team.addUser(user);
                        
                        //LOG.log(Level.INFO, "{0}:{0}", new Object[]{ GameData.list_matchTeamPost.get(i).get("fuserid"), GameData.list_matchTeamPost.get(i).get("fusername")});
                        
                        p.put("fuserid", user.getUserInfo().get("fid"));
                        p.put("fusername", user.getUserInfo().get("fname"));
                        
                        VoResponseData rd = new VoResponseData();
                        rd.message = GameMessage.MESSAGE_GETMATCHTEAMPOSTLIST;
                        rd.data = GameData.getMatchTeamPostList( team.getId() );
                        //sendMessage( rd );
                        sendChannelMessage( rd );
                        
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
    
    private boolean disjoinTeam( Object data ){
        
        // 小队匹配
        String teamBinding = "";
        for ( int i=0; i< GameData.list_matchTeam.size(); i++ ){
            Map<String,Object> t = GameData.list_matchTeam.get(i);
            if ( t.get("fid").equals((int)data))
            {
                teamBinding = TEAM_BIND_PREFIX + t.get("fbranchname") + "."+ t.get("fname");
                break;
            }
        }

        DataManager dataMgr = AppContext.getDataManager();
        GameTeam team;
        try{
            team = (GameTeam)dataMgr.getBinding(teamBinding);
        }catch (NameNotBoundException ex){
            LOG.log(Level.WARNING, "未找到系统对 Team 对象的绑定 {0}", ex.getMessage());
            return false;
        }
        
        removeFromAllTeams();
        removeFromAllPosts();
        
        VoResponseData rd = new VoResponseData();
        rd.message = GameMessage.MESSAGE_GETMATCHTEAMPOSTLIST;
        rd.data = GameData.getMatchTeamPostList( team.getId() );
        //sendMessage( rd );
        sendChannelMessage( rd );
        
        //LOG.info("已发送\"离开\"消息");
        
        return true;
    }
}
