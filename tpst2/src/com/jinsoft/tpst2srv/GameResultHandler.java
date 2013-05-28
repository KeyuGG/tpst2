/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv;
import com.jinsoft.tpst2srv.common.Global;
import com.jinsoft.tpst2srv.vo.VoResponseData;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ManagedReference;
import com.zero_separation.pds.sql.SQLResult;
import com.zero_separation.pds.sql.SQLResultHandler;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KeyuGG
 */
public class GameResultHandler implements SQLResultHandler, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger( GameResultHandler.class.getName() );
    private int message;
    public static final String CHANNEL_PRIMARY = "CHANNEL_PRIMARY";
    private ManagedReference< Channel > channelPrimaryRef = null;
    private ManagedReference<ClientSession> currentSessionRef = null;
    
    public GameResultHandler( int message, ManagedReference<ClientSession> currentSessionRef ){
        this.message = message;
        this.currentSessionRef = currentSessionRef;
        ChannelManager channelMgr = AppContext.getChannelManager();
        Channel channel = channelMgr.getChannel( CHANNEL_PRIMARY );
        channelPrimaryRef = AppContext.getDataManager().createReference(channel);
    }
            
    @Override
    public void SQLQueryResult( SQLResult sqlr ) {
        //LOG.log(Level.INFO, "GameServer 收到数据库处理结果: 请求 - {0}, 结果 - {1}", new Object[]{ sqlr.getQuery(), sqlr.getResult() });
        VoResponseData rd = new VoResponseData();
        rd.message = message;

        if ( sqlr.getResult() == SQLResult.Result.SUCCESS ){
            switch ( message ){
                case GameMessage.MESSAGE_USER_REGIST: rd.data = true; sendSessionMessage( rd ); break; // 回送注册成功的消息
                case GameMessage.GAME_INIT_TOPIC: GameData.gameInitTopic( sqlr.getResultSet() ); break;
                case GameMessage.GAME_CULL_TOPIC: sendChannelMessage( rd ); break;
                case GameMessage.GAME_GET_TOPIC:
                    rd.message = GameMessage.GAME_GET_TOPIC; 
                    rd.data = GameData.getResultList(sqlr.getResultSet());
                    sendSessionMessage( rd );
                    break;
                case GameMessage.GAME_GET_QUIZ: 
                    rd.message = GameMessage.GAME_GET_QUIZ; 
                    rd.data = GameData.getResultList(sqlr.getResultSet());
                    sendSessionMessage( rd );
                    break;
                case GameMessage.GAME_SUBMIT_ANSWER:
                    rd.message = GameMessage.GAME_SUBMIT_ANSWER;
                    rd.data = GameData.getResultList(sqlr.getResultSet());
                    sendSessionMessage( rd );
                /*case GameMessage.SERVER_UPDATE_MATCHQUIZLIST: updateMatchQuizList( sqlr.getResultSet());
                    rd.message = GameMessage.MESSAGE_GETMATCHTOPIC;
                    rd.data = GameData.list_topic;
                    sendChannelMessage( rd ); 
                    break;*/
            }
            GameData.updateList( sqlr.getResultSet(), message);
        } else {
            LOG.log(Level.INFO, "数据库存取操作错误: 请求 - {0}, 结果 - {1}", new Object[]{ sqlr.getQuery(), sqlr.getResult() });            
        }
    }
    
    private void updateMatchQuizList( ResultSet rs ){
        GameData.updateMatchQuizList( rs );
    }
    
    private void sendChannelMessage( Object data ){
        ByteBuffer buf = null;
        
        try {
                buf = ByteBuffer.wrap(Global.SerializeAmf3( data ));
                //LOG.log(Level.SEVERE, "已经发送通道数据:{0} : {1}", new Object[]{((VoResponseData)data).message,((VoResponseData)data).data} );
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "接收到数据处理结果时出错:{0}", ex);
            }

        channelPrimaryRef.get().send( buf );
        //LOG.log(Level.SEVERE, "已经发送通道数据:{0} : {1}", new Object[]{((VoResponseData)data).message,((VoResponseData)data).data} );
    }
    
    private void sendSessionMessage( Object data ){
        
        ByteBuffer buf = null;
        
        try {
                buf = ByteBuffer.wrap(Global.SerializeAmf3( data ));
                //LOG.log(Level.SEVERE, "已经发送通道数据:{0} : {1}", new Object[]{((VoResponseData)data).message,((VoResponseData)data).data} );
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "接收到数据处理结果时出错:{0}", ex);
            }

        if ( currentSessionRef != null)
            currentSessionRef.get().send( buf );
    }
}
