package com.jinsoft.tpst2srv;
import com.jinsoft.tpst2srv.common.Global;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.AppListener;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.Delivery;
import com.sun.sgs.app.ManagedReference;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author KeyuGG
 */
public class GameServer implements Serializable, AppListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger( GameServer.class.getName() );
    public static final String CHANNEL_PRIMARY = "CHANNEL_PRIMARY";
    private ManagedReference< Channel > channelPrimaryRef;
    private Set<ManagedReference< GameTeam >> teamRefList;
    public static final String TEAM_BIND_PREFIX = "Team.";
    public static final String MATCH_BIND_PREFIX = "Match.";
    
    @Override
    public void initialize(Properties props) {
        initializeSafeSandBox();
        initializeChanel();
        initializeGameData();
        initializeGameTeam();
    }

    @Override
    public ClientSessionListener loggedIn( ClientSession session ) {
        
        if ( !session.getName().equals(Global.getAdmin()) )
        {            
            //LOG.log(Level.INFO, "普通用户: {0} 已登入", session.getName());
        }else{
            //LOG.log(Level.INFO, "管理员用户: {0} 已登入", session.getName());
        }
        
        GameUser user = GameUser.loggedIn( session );
        
        /*
        if ( !user.isAdmin() )
            LOG.log(Level.SEVERE, "用户大队编号为:{0}", user.getUserInfo().get("fbranchid"));
            */
        return user;
    }
    
    private void initializeGameTeam(){
        
        GameData.updateMatchList();
        GameData.updateMatchCamera();
        GameData.updateMatchTrafficLight();
        GameData.updateMatchTeamList();
        GameData.updateMatchTeamPostList();
        GameData.updateMatchRoad( 0 );
        GameData.updateMatchCrossing();
        GameData.updateMatchTeamCar();
        GameData.updateMatchTeamBike();
        GameData.updateMatchTeamPolice();

        DataManager dataManager = AppContext.getDataManager();
        teamRefList = new HashSet<>();
        for ( int i=0; i< GameData.list_matchTeam.size(); i++ ){
            Map<String,Object> o = GameData.list_matchTeam.get(i);
            GameTeam team = new GameTeam((int)o.get("fid"), o.get("fname").toString(), (int)o.get("fmatchid"), (int)o.get("fbranchid"), o.get("fbranchname").toString());
            String teamBinding = TEAM_BIND_PREFIX + team.getBranchname() + "."+ team.getName();
            ManagedReference< GameTeam > teamRef = dataManager.createReference( team );
            dataManager.setBinding(teamBinding, team);
            teamRefList.add( teamRef );
        }
    }
    
    public Set<ManagedReference< GameTeam >> getTeamRefs(){
        return teamRefList;
    }

    private void initializeChanel(){
        ChannelManager channelMgr = AppContext.getChannelManager();
        Channel channel = channelMgr.createChannel(CHANNEL_PRIMARY, new GameChannelListener(), Delivery.RELIABLE);
        channelPrimaryRef = AppContext.getDataManager().createReference(channel);
        
        //channelMgr.createChannel(CHANNEL_AMF, new GameChannelListener(), Delivery.RELIABLE);        
        //LOG.log(Level.SEVERE, "已完成设定电子沙盘游戏通道");
    }
    
    private void initializeSafeSandBox(){
        GameSafeSandBox sandbox = new GameSafeSandBox();
        sandbox.start();
        //LOG.log(Level.SEVERE, "已完成安装电子沙盘游戏安全沙箱");
    }

    private void initializeGameData(){
        GameQuery.query( GameData.QUERY_ALL_DEPARTMENT, new GameResultHandler( GameMessage.SERVER_UPDATE_DEPARTMENTLIST, null ) );
        GameQuery.query( GameData.QUERY_ALL_DIVISION, new GameResultHandler( GameMessage.SERVER_UPDATE_DIVISIONLIST, null ) );
        GameQuery.query( GameData.QUERY_ALL_BRANCH, new GameResultHandler( GameMessage.SERVER_UPDATE_BRANCHLIST, null ) );
        GameQuery.query( GameData.QUERY_ALL_USER, new GameResultHandler( GameMessage.SERVER_UPDATE_USERLIST, null ) );
        GameQuery.query( GameData.QUERY_ALL_GROUP, new GameResultHandler( GameMessage.SERVER_UPDATE_GROUPLIST, null ) );
        GameQuery.query( GameData.QUERY_ALL_GROUPUSER, new GameResultHandler( GameMessage.SERVER_UPDATE_GROUPUSERLIST, null ) );
        GameQuery.query( GameData.QUERY_ALL_PUBTOPIC, new GameResultHandler( GameMessage.SERVER_UPDATE_MATCHPUBTOPICLIST, null ) );
        GameQuery.query( GameData.QUERY_ALL_TOPIC, new GameResultHandler( GameMessage.SERVER_UPDATE_MATCHTOPICLIST, null ) );
        GameQuery.query( GameData.QUERY_ALL_PUBQUIZ, new GameResultHandler( GameMessage.SERVER_UPDATE_MATCHPUBQUIZLIST, null ) );
        GameQuery.query( GameData.QUERY_ALL_ATTACHMENT, new GameResultHandler( GameMessage.SERVER_UPDATE_MATCHTOPICATTACHMENTLIST, null ) );
        GameQuery.query( GameData.QUERY_ALL_QUIZ, new GameResultHandler( GameMessage.SERVER_UPDATE_MATCHQUIZLIST, null ) );
        GameQuery.query( GameData.QUERY_ALL_QUIZPOST, new GameResultHandler( GameMessage.SERVER_UPDATE_MATCHQUIZPOSTLIST, null ) );
        GameQuery.query( GameData.QUERY_ALL_ANSWER, new GameResultHandler( GameMessage.SERVER_UPDATE_MATCHANSWERLIST, null ) );
        
        GameQuery.query( GameData.QUERY_GAME_INIT, new GameResultHandler( GameMessage.GAME_INIT_TOPIC, null ) );

        //LOG.log(Level.INFO, "已完成初始化 总队, 支队, 大队, 用户数据缓存:\n总队记录总数: {0}\n支队记录总数: {1}\n大队记录总数: {0}", new Object[]{GameData.list_department.size(), GameData.list_division.size(), GameData.list_branch.size()});
    }
}
