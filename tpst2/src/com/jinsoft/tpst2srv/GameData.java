/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv;

import com.jinsoft.tpst2srv.vo.VoResponseData;
import com.sun.sgs.app.ManagedObject;
import java.io.Serializable;
import java.sql.ResultSet;
import javax.swing.*;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author KeyuGG
 */
public class GameData implements Serializable, ManagedObject{
    
    private static final Logger LOG = Logger.getLogger(GameData.class.getName());

    public static final String QUERY_ALL_DEPARTMENT = "SELECT fid, TRIM(fnumber) fnumber, TRIM(fname) fname FROM t_department ";
    public static final String QUERY_ALL_DIVISION = "SELECT fid, fdepartmentid, TRIM(fname) fname FROM t_division ";
    public static final String QUERY_ALL_BRANCH = "SELECT fid, fdivisionid, TRIM(fname) fname FROM t_branch ";
    public static final String QUERY_ALL_USER = "SELECT fid, TRIM(fname) fname, TRIM(fpassword) fpassword, fbranchid, fregisttime, flevel, fexp, TRIM(fdescription) fdescription, TRIM(fphotourl) fphotourl FROM t_user ";
    public static final String QUERY_ALL_GROUP = "SELECT fid, TRIM(fname) fname, fisroot, fisdrillmaster, fisexaminer, fisapprover FROM t_group";
    public static final String QUERY_ALL_GROUPUSER = "SELECT fuserid, fgroupid FROM t_groupuser";
    //public static final String QUERY_ALL_TOPIC = "SELECT fid,fbranchid,TRIM(fnumber)fnumber,TRIM(fdescription)fdescription,TRIM(fstartoccurrencetime)fstartoccurrencetime,TRIM(fendoccurrencetime)fendoccurrencetime,flasteditorid,flastedittime,fisapproved,TRIM(fapprovedtime)fapprovedtime,fedittime,fx,fy,fmapid,fexaminerid,fspotid,ftopictypeid,TRIM(findexedtime)findexedtime,fcrossid FROM t_topic";
    public static final String QUERY_ALL_TOPIC = "SELECT 0 fispublic, 1 favailable,0 ffinished, fid ftopicid, 0 fquizid, fbranchid,TRIM(fnumber)fnumber,TRIM(fdescription)fdescription,TRIM(fstartoccurrencetime)fstartoccurrencetime,TRIM(fendoccurrencetime)fendoccurrencetime,flasteditorid,flastedittime,fisapproved,TRIM(fapprovedtime)fapprovedtime,fedittime,fx,fy,fmapid,fexaminerid,fspotid,ftopictypeid,TRIM(findexedtime)findexedtime,fcrossid FROM t_topic WHERE TRIM(fdescription) NOT LIKE '%公共题%' order by random(); ";
    public static final String QUERY_ALL_PUBTOPIC = "SELECT 1 fispublic,1 favailable,0 ffinished, t1.ftopicid, t1.fid fquizid,-1 fbranchid,TRIM(t1.fnumber)fnumber,LEFT(TRIM(t2.fdescription),3)fdescription,'00:00', '23:59',0,'2013-02-25 15:11:54.577+08',8,'2012-01-01 00:00','2012-01-01 00:00',0,0,0,0,0,0,'2012-01-1 00:00',0 FROM t_quiz t1, t_topic t2  WHERE t1.ftopicid = t2.fid AND t2.fdescription LIKE '公共题%' order by random(); ";
    public static final String QUERY_ALL_ATTACHMENT = "SELECT fid,TRIM(ffilename)ffilename,TRIM(ftype)ftype,ftopicid,TRIM(fdescription)fdescription FROM t_topicattachment;";
    public static final String QUERY_ALL_QUIZ = "SELECT 0 fispublic,fid,TRIM(fnumber) fnumber,TRIM(fdescription)fdescription,ftype,fovertime,fzhzz,fyunqin,ftopicid,TRIM(fcorrectanswer)fcorrectanswer,ffullmarks,fisrelevance FROM t_quiz WHERE ftopicid IN(SELECT fid FROM t_topic WHERE fdescription NOT LIKE '公共题%');";
    public static final String QUERY_ALL_PUBQUIZ = "SELECT 1 fispublic,fid,TRIM(fnumber) fnumber,TRIM(fdescription)fdescription,ftype,fovertime,fzhzz,fyunqin,ftopicid,TRIM(fcorrectanswer)fcorrectanswer,ffullmarks,fisrelevance FROM t_quiz WHERE ftopicid IN(SELECT fid FROM t_topic WHERE fdescription LIKE '公共题%');";
    public static final String QUERY_ALL_QUIZPOST = "SELECT fid, fquizid, fpostid FROM t_postquiz";
    public static final String QUERY_ALL_ANSWER = "SELECT fid,TRIM(fno)fno,TRIM(fdescription)fdescription,fquizid FROM t_answer ORDER BY fquizid,fno";
    //public static final String EDIT_USER = "SELECT edituser(0, '李科宇', '123', '美貌与', 1, '', 0, 0)";
    public static final String EDIT_USER = "SELECT edituser(%d, '%s', '%s', '%s', %d, '%s', %d, %d) ";
    
    public static final String QUERY_GAME_INIT = "SELECT * FROM game_init() AS ( topic_count INTEGER, pubtopic_count INTEGER );";
    public static final String QUERY_GAME_CULLTOPIC = "SELECT game_culltopic FROM game_culltopic(%d,'%s');";

    public static final String QUERY_GAME_GET_TOPIC = "SELECT * FROM game_get_topic(%d,%d);";
    public static final String QUERY_GAME_GET_QUIZ = "SELECT * FROM game_get_quiz(%d,%d,%d,%d);";
    public static final String QUERY_GAME_SUBMIT_ANSWER = "SELECT * FROM game_submit_answer(%d,%d,'%s',%d,%d,%d,%d,%d) AS (score NUMERIC(14,4), result INTEGER, info TEXT);";

    public static final int DEPARTMENT                                          = 0;
    public static final int DIVISION                                            = 1;
    public static final int BRANCH                                              = 2;
    public static final int USER                                                = 3;
    public static final int MATCH                                               = 4;
    public static final int MATCHTEAM                                           = 5;
    public static final int MATCHPOST                                           = 6;
    
    public static final int GROUPID_ROOT                                        = 0x1; // 管理员
    public static final int GROUPID_DRILLMASTER					= 0x2; // 对抗指挥官
    public static final int GROUPID_EXAMINER					= 0x3; // 出题人
    public static final int GROUPID_APPROVER					= 0x4; // 审题人    
    
    public static final int POSTID_GENERALOFFICE                                = 1; // 办公室主任
    public static final int POSTID_ACCIDENTDIRECTOR                             = 2; // 事故中队长
    public static final int POSTID_ORDERDIRECTOR                                = 3; // 秩序中队长
    public static final int POSTID_COMMANDCENTER                                = 4; // 监控中心主任
    public static final int POSTID_BRANCHDIRECTOR                               = 5; // 大队长
    public static final int POSTID_POLITICALINSTRUCTOR                          = 6; // 副教导员
    public static final int POSTID_DRILLMASTER                                  = 7; // 对抗演练指挥教官
    
    public static final int MATCH_STATUS_READY                                  = 0x0; // 任务准备就绪
    public static final int MATCH_STATUS_RUNNING                                = 0x1; // 任务正在执行
    public static final int MATCH_STATUS_STOPPED                                = 0x2; // 任务已结束
    
    public static final int MAP_OBJECT_NONE					= 0x0; // 无
    public static final int MAP_OBJECT_CAR                                      = 0x1; // 警车
    public static final int MAP_OBJECT_BIKE                                     = 0x2; // 警摩
    public static final int MAP_OBJECT_PMA					= 0x3; // 警员A
    public static final int MAP_OBJECT_PMB					= 0x4; // 警员B
    public static final int MAP_OBJECT_PMC					= 0x5; // 警员C
    public static final int MAP_OBJECT_PMX					= 0x6; // 警员X
    public static final int MAP_OBJECT_CROSSING					= 0x7; // 路口
    public static final int MAP_OBJECT_ROAD					= 0x8; // 路段
    public static final int MAP_OBJECT_TRAFFICLIGHT                             = 0x9; // 红绿灯
    public static final int MAP_OBJECT_CAMERA                                   = 0xa; // 摄像头
    
    public static final int QUIZ_TYPE_CHOICE					= 1;   // 单选
    public static final int QUIZ_TYPE_SELECTS                                   = 2;   // 多选
    public static final int QUIZ_TYPE_JUDGE                                     = 3;   // 判断
    public static final int QUIZ_TYPE_FILL					= 4;   // 填空
    public static final int QUIZ_TYPE_SEQUENTIALLY_SELECTS                      = 5;   // 多选排序
    public static final int QUIZ_TYPE_SUBJECTIVE				= 6;   // 主观题


    
    public static final int TIME_MICROSECONDS_HOUR                              = 3600000;
    
    public static List<Map<String, Object>> list_department;
    public static List<Map<String, Object>> list_division;
    public static List<Map<String, Object>> list_branch;
    public static List<Map<String, Object>> list_user;
    public static List<Map<String, Object>> list_group;
    public static List<Map<String, Object>> list_groupuser;
    public static List<Map<String, Object>> list_match;
    public static List<Map<String, Object>> list_matchMap;

    public static Document matchMapDoc;
    
    public static List<Map<String, Object>> list_matchModel = new ArrayList<>();
    public static List<Map<String, Object>> list_matchTrafficLight = new ArrayList<>();
    public static List<Map<String, Object>> list_matchCamera = new ArrayList<>();
    public static List<Map<String, Object>> list_matchRoad = new ArrayList<>();
    public static List<Map<String, Object>> list_matchCrossing = new ArrayList<>();
    public static List<Map<String, Object>> list_matchTeam = new ArrayList<>();
    public static List<Map<String, Object>> list_matchTeamPost = new ArrayList<>();
    public static List<Map<String, Object>> list_matchTeamCar = new ArrayList<>();
    public static List<Map<String, Object>> list_matchTeamBike = new ArrayList<>();
    public static List<Map<String, Object>> list_matchTeamPolice = new ArrayList<>();
    public static List<Map<String, Object>> list_matchTeamDropObject = new ArrayList<>();
    
    public static List<Map<String, Object>> list_topic = new ArrayList<>();
    public static List<Map<String, Object>> list_pubtopic = new ArrayList<>();
    public static List<Map<String, Object>> list_topicAttachment = new ArrayList<>();
    public static List<Map<String, Object>> list_quiz = new ArrayList<>();
    public static List<Map<String, Object>> list_pubquiz = new ArrayList<>();
    public static List<Map<String, Object>> list_quizpost = new ArrayList<>();
    public static List<Map<String, Object>> list_answer = new ArrayList<>();
    
    public static List<Map<String, Object>> list_matchTopic = new ArrayList<>();
    public static List<Map<String, Object>> list_matchQuiz = new ArrayList<>();
    public static List<Map<String, Object>> list_matchQuizSubmitted = new ArrayList<>();
    
    public static Map<String,Object> matchTime = new HashMap<>();

    //public static List<Map<String, Object>>\
    
    public static void cullTopic( ResultSet rs ){
        list_matchTopic.add(getResultMap( rs ));
        LOG.log(Level.INFO, "cullTopic:{0}", list_matchTopic );
    }
    
    public static void setMatchTime( Map<String,Object> mtime ){
        matchTime = mtime;
    }
    
    public static Map<String,Object> submitAnswer(String answer, int fquizid, int fuserid, int fteamid, int fispublic){

        Map<String, Object> result = new HashMap<>();
        List<Map<String,Object>> quizList;
        if ( fispublic == 0 ){
            quizList = list_quiz;
            result.put("fispublic", 0);
        }else{
            quizList = list_pubquiz;
            result.put("fispublic", 1);
        }
        
        // 初始化评分
        result.put("result", 0);
        result.put("score", (double)0.0);
        result.put("correct", -1);
        result.put("description", "答案提交时出错");

        for ( Map<String,Object> existAnswer:list_matchQuizSubmitted){
            if ( existAnswer.get("fquizid").equals(fquizid) )
            {
                result.put("result", 0);
                result.put("correct", false);
                result.put("score", (double)0.0);
                result.put("description", "此答案已经提交");
                return result;
            }
        }

        for ( Map<String,Object> quiz: quizList){
            //LOG.log(Level.INFO, "比较 fquizid:quiz.get(\"fid\"):{0},fquizid:{1}", new Object[]{ quiz.get("fid"), fquizid});
            if (quiz.get("fid").equals(fquizid)){
                LOG.log(Level.INFO, "开始评分:fid:{0},ftype:{1},fcorrectanswer:{2},ffullmarks:{3}", new Object[]{ quiz.get("fid"), quiz.get("ftype"), quiz.get("fcorrectanswer"),quiz.get("ffullmarks")});
                // 评分
                
                result.put("correct", 1);
                result.put("score", Double.parseDouble("0"));
                
                switch ( Integer.parseInt(quiz.get("ftype").toString())){
                    case QUIZ_TYPE_CHOICE:
                    case QUIZ_TYPE_SELECTS:
                    case QUIZ_TYPE_SEQUENTIALLY_SELECTS:
                    case QUIZ_TYPE_JUDGE:
                        if ( quiz.get("fcorrectanswer").equals( answer ) ){
                            result.put("score", Double.parseDouble(quiz.get("ffullmarks").toString()));
                            result.put("correct", 1);
                        }else{
                            result.put("score", Double.parseDouble("0"));
                            result.put("correct", 0);
                        }
                        break;
                     // 主观题不作处理, 标记为 -1
                }
                
                // 写入提交记录
                Map<String, Object> record = new HashMap<>();
                record.put("fteamid", fteamid);
                record.put("fquizid", fquizid);
                record.put("fuserid", fuserid);
                record.put("fanswer", answer);
                record.put("correct", result.get("correct"));
                list_matchQuizSubmitted.add( record );

                result.put("result", 1);
                result.put("description", "答案提交成功");
                return result;
            }
        }
        return result;
    }
    
    public static void updateList( ResultSet rs, int message ){
        switch (message){
           //case GameMessage.MESSAGE_USER_REGIST: GameQuery.query( QUERY_ALL_USER, new GameResultHandler( GameMessage.SERVER_UPDATE_USERLIST ) ); break;
            case GameMessage.SERVER_UPDATE_DEPARTMENTLIST: list_department = getResultList( rs ); break;
            case GameMessage.SERVER_UPDATE_DIVISIONLIST: list_division = getResultList( rs ); break;
            case GameMessage.SERVER_UPDATE_BRANCHLIST: list_branch = getResultList( rs ); break;
            case GameMessage.SERVER_UPDATE_USERLIST: list_user = getResultList( rs ); break;
            case GameMessage.SERVER_UPDATE_GROUPLIST: list_group = getResultList( rs ); break;
            case GameMessage.SERVER_UPDATE_GROUPUSERLIST: list_groupuser = getResultList( rs ); break;
            case GameMessage.SERVER_UPDATE_MATCHPUBTOPICLIST:  list_pubtopic = getResultList( rs ); break;
            case GameMessage.SERVER_UPDATE_MATCHTOPICLIST:  list_topic = getResultList( rs ); break;
            case GameMessage.SERVER_UPDATE_MATCHPUBQUIZLIST:  list_pubquiz = getResultList( rs ); break;
            case GameMessage.SERVER_UPDATE_MATCHTOPICATTACHMENTLIST:  list_topicAttachment = getResultList( rs ); break;
            case GameMessage.SERVER_UPDATE_MATCHQUIZLIST:  list_quiz = getResultList( rs ); break;
            case GameMessage.SERVER_UPDATE_MATCHQUIZPOSTLIST: list_quizpost = getResultList( rs );break;
            case GameMessage.SERVER_UPDATE_MATCHANSWERLIST: list_answer = getResultList( rs );break;
        }
    }

    public static void gameInitTopic( ResultSet rs ){
        Map<String,Object> result = getResultMap(rs);
        LOG.log(Level.INFO, "初始化公共题数量:{0}, 关联题数量:{1}", new Object[]{result.get("pubtopic_count"), result.get("topic_count")});
    }
    
    public static void updateMatchTopicList( ResultSet rs ){
        insertResultToList(rs, list_topic);

        String ftopicid = "-1";
        if ( list_topic.size() > 0)
            ftopicid = list_topic.get(list_topic.size()-1).get("fid").toString();
        
        //LOG.log(Level.SEVERE, "ftopicid:{0}, list_topic:{1}", new Object[]{ftopicid, list_topic});
        String fquizid = "-1";
        for (int i=0; i < GameData.list_topic.size()-1; i++){
            Map<String,Object> object = GameData.list_topic.get(i);
            if ( object.get("fid").toString().equals(ftopicid))
                fquizid += object.get("fquizid").toString() + ",";
        }
        if ( !"-1".equals(fquizid) )
            fquizid = fquizid.substring(2, fquizid.length() -1);
        
        
        String fquizidSubmitted = "-1";
        for (int i=0; i < GameData.list_matchQuizSubmitted.size()-1; i++){
            Map<String,Object> object = GameData.list_matchQuizSubmitted.get(i);
            if ( object.get("fid").toString().equals(ftopicid))
                fquizidSubmitted += object.get("fquizid").toString() + ",";
        }
        if ( !"-1".equals(fquizidSubmitted) )
            fquizidSubmitted = fquizidSubmitted.substring(2, fquizidSubmitted.length()-1);
        
        
        String fquizpostid = "-1";
        for (int i=0; i < GameData.list_quizpost.size()-1; i++){
            Map<String,Object> object = GameData.list_quizpost.get(i);
                fquizpostid += object.get("fquizid").toString() + ",";
        }
        if ( !"-1".equals(fquizpostid) )
            fquizpostid = fquizpostid.substring(2, fquizpostid.length()-1);
        
        //String sql = "SELECT fid,TRIM(fnumber) fnumber,TRIM(fdescription) fdescription,ftype,fovertime,fzhzz,fyunqin,ftopicid,TRIM(fcorrectanswer) fcorrectanswer,ffullmarks, fisrelevance FROM t_quiz WHERE ftopicid="+ ftopicid;
        String sql = "select t1.fid,trim(fnumber) fnumber,trim(fdescription) fdescription,ftype,fovertime,fzhzz,fyunqin,ftopicid,trim(fcorrectanswer) as fcorrectanswer,ffullmarks,fisrelevance,trim(t2.fname) ftypename "+
                     "from t_quiz t1,t_quiztype t2 where t1.ftype=t2.fid and t1.ftopicid=" + ftopicid + " " +
                     "and t1.fid in(" + fquizid + ") " +
                     "and t1.fid not in(" + fquizpostid + ") " +
                     "and t1.fid not in(" + fquizidSubmitted + ") " +
                     "--当试题关联了岗位的 " + 
                     "union all select t1.fid,trim(fnumber) fnumber,trim(fdescription) fdescription,ftype,fovertime,fzhzz,fyunqin,ftopicid,trim(fcorrectanswer) as fcorrectanswer,ffullmarks,fisrelevance,trim(t2.fname) ftypename " +
                     "from t_quiz t1,t_quiztype t2 where t1.ftype=t2.fid and t1.ftopicid=" + ftopicid + " " +
                     "and t1.fid in(" + fquizid + ") " + 
                     "and t1.fid in(" + fquizpostid + ") " +
                     "and t1.fid not in(" + fquizidSubmitted + ") order by fnumber ";
        LOG.log(Level.SEVERE, "生成获取问题的查询:{0}", sql);
        GameQuery.query( sql, new GameResultHandler( GameMessage.SERVER_UPDATE_MATCHQUIZLIST, null) );
    }
    
    public static void updateMatchQuizList( ResultSet rs ){
        insertResultToList(rs, list_quiz);
    }

    public static Document getMatchMapDocument(){
        
        if ( matchMapDoc == null )
        {
            try {
                SAXReader reader = new SAXReader();
                matchMapDoc = reader.read("websrv/webapps/tpst_web/assets/map/GEN01201303220902/map_layout.xml");
            } catch (DocumentException ex) {
                LOG.log(Level.SEVERE, "解析地图配置文件失败:{0}", ex.getMessage());
            }
        }
        
        return matchMapDoc;
    }

    public static void updateMatchList()
    {
        if ( list_match == null )
        {
            list_match = new ArrayList<>();
        }else{
            list_match.clear();
        }

        Map<String, Object> item = new HashMap<>();
        item.put("fid", 0);                                                     // 编号
        item.put("ftitle", "[交警二十四小时]");                                  // 标题
        item.put("fdescription", "交警电子沙盘第一期实战演练任务");               // 详细说明
        item.put("fmapname", "GEN01201303220902");                              // 地图名称
        item.put("fmapurl", "/assets/map/GEN01201303220902/GEN01201303220902.tmx");               // 地图地址
        item.put("fxmlurl", "/assets/map/GEN01201303220902.xml");               // 地图配置文件地址
        item.put("fxmllocurl", "/assets/map/GEN01201303220902_loc.xml");        // 地图配置文件地址
        item.put("fcreationtime", 0);                                           // 创建时间
        item.put("fstarttime", 0);                                              // 开始时间
        item.put("fendtime", 0);                                                // 结束时间
        //item.put("femustarttime", "00:00:00");                                // 模拟开始时间
        //item.put("femuendtime", "23:59:59");                                  // 模拟结束时间
        item.put("fstatus", 0);                                                 // 当前状态
        
        list_match.add( item );
    }
    
    public static void updateMatchTeamList()
    {
        if ( list_matchTeam == null )
        {
            list_matchTeam = new ArrayList<>();
        }else{
            list_matchTeam.clear();
        }
        
        Map<String, Object> item = new HashMap<>();
        item.put("fid", 0);            // 小队 id
        item.put("fname", "红队");       // 小队名称
        item.put("fmatchid", 0);       // 任务 id
        item.put("fbranchid", 1);      // 大队 id
        item.put("fbranchname", "汇东新区大队"); // 大队名称
        
        list_matchTeam.add(item);
        
        item = new HashMap<>();
        item.put("fid", 1);            // 小队 id
        item.put("fname", "蓝队");       // 小队名称
        item.put("fmatchid", 0);       // 任务 id
        item.put("fbranchid", 1);      // 大队 id
        item.put("fbranchname", "汇东新区大队"); // 大队名称

        list_matchTeam.add(item);
    }
    
    public static void updateMatchTeamPostList()
    {
        if ( list_matchTeamPost == null )
        {
            list_matchTeamPost = new ArrayList<>();
        }else{
            list_matchTeamPost.clear();
        }
        
        Map<String, Object> item = new HashMap<>();
        item.put("fteamid", 0);              // 小队 ID
        item.put("fpostid", POSTID_GENERALOFFICE); // 岗位类型ID
        item.put("fpostname", "办公室主任");  // 岗位名称
        item.put("fuserid", -1);             // 用户 ID
        item.put("fusername", "");             // 用户名
        list_matchTeamPost.add(item);
        
        item = new HashMap<>();
        item.put("fteamid", 0);              // 小队 ID
        item.put("fpostid",  POSTID_ACCIDENTDIRECTOR);              // 岗位类型ID
        item.put("fpostname", "事故中队长");  // 岗位名称
        item.put("fuserid", -1);             // 用户 ID
        item.put("fusername", "");             // 用户名
        list_matchTeamPost.add(item);
        
        item = new HashMap<>();
        item.put("fteamid", 0);              // 小队 ID
        item.put("fpostid", POSTID_ORDERDIRECTOR);              // 岗位类型ID        
        item.put("fpostname", "秩序中队长");  // 岗位名称
        item.put("fuserid", -1);             // 用户 ID
        item.put("fusername", "");             // 用户名
        list_matchTeamPost.add(item);
        
        item = new HashMap<>();
        item.put("fteamid", 0);                 // 小队 ID
        item.put("fpostid", POSTID_COMMANDCENTER);                 // 岗位类型ID
        item.put("fpostname", "监控中心主任");   // 岗位名称
        item.put("fuserid", -1);                // 用户 ID
        item.put("fusername", "");             // 用户名
        list_matchTeamPost.add(item);
        
        item = new HashMap<>();
        item.put("fteamid", 0);                 // 小队 ID
        item.put("fpostid", POSTID_POLITICALINSTRUCTOR);                 // 岗位类型ID
        item.put("fpostname", "副教导员");      // 岗位名称
        item.put("fuserid", -1);                // 用户 ID
        item.put("fusername", "");             // 用户名
        list_matchTeamPost.add(item);
        
        item = new HashMap<>();
        item.put("fteamid", 0);                 // 小队 ID
        item.put("fpostid", POSTID_BRANCHDIRECTOR);                 // 岗位类型ID
        item.put("fpostname", "大队长");        // 岗位名称
        item.put("fuserid", -1);                // 用户 ID
        item.put("fusername", "");             // 用户名
        list_matchTeamPost.add(item);
        
        item = new HashMap<>();
        item.put("fteamid", 1);              // 小队 ID
        item.put("fpostid", POSTID_GENERALOFFICE);              // 岗位类型ID
        item.put("fpostname", "办公室主任");  // 岗位名称
        item.put("fuserid", -1);             // 用户 ID
        item.put("fusername", "");             // 用户名
        list_matchTeamPost.add(item);
        
        item = new HashMap<>();
        item.put("fteamid", 1);              // 小队 ID
        item.put("fpostid", POSTID_ACCIDENTDIRECTOR);              // 岗位类型ID
        item.put("fpostname", "事故中队长");  // 岗位名称
        item.put("fuserid", -1);             // 用户 ID
        item.put("fusername", "");             // 用户名
        list_matchTeamPost.add(item);
        
        item = new HashMap<>();
        item.put("fteamid", 1);              // 小队 ID
        item.put("fpostid", POSTID_ORDERDIRECTOR);              // 岗位类型ID        
        item.put("fpostname", "秩序中队长");  // 岗位名称
        item.put("fuserid", -1);             // 用户 ID
        item.put("fusername", "");             // 用户名
        list_matchTeamPost.add(item);
        
        item = new HashMap<>();
        item.put("fteamid", 1);                 // 小队 ID
        item.put("fpostid", POSTID_COMMANDCENTER);                 // 岗位类型ID
        item.put("fpostname", "监控中心主任");   // 岗位名称
        item.put("fuserid", -1);                // 用户 ID
        item.put("fusername", "");             // 用户名
        list_matchTeamPost.add(item);
        
        item = new HashMap<>();
        item.put("fteamid", 1);                 // 小队 ID
        item.put("fpostid", POSTID_POLITICALINSTRUCTOR);                 // 岗位类型ID
        item.put("fpostname", "副教导员");      // 岗位名称
        item.put("fuserid", -1);                // 用户 ID
        item.put("fusername", "");             // 用户名
        list_matchTeamPost.add(item);
        
        item = new HashMap<>();
        item.put("fteamid", 1);                 // 小队 ID
        item.put("fpostid", POSTID_BRANCHDIRECTOR);                 // 岗位类型ID
        item.put("fpostname", "大队长");        // 岗位名称
        item.put("fuserid", -1);                // 用户 ID
        item.put("fusername", "");             // 用户名
        list_matchTeamPost.add(item);        
    }
    
    public static void loadMatchObject(List<Map<String, Object>> list, String objectName, int fmatchid){
        Document doc = getMatchMapDocument();
        Element root = doc.getRootElement();
        
        for (Iterator ie = root.elementIterator(); ie.hasNext();) {
            Element element = (Element) ie.next();
            if (element.attributeValue("name").equals(objectName)) {
                for (Iterator ieson = element.elementIterator(); ieson.hasNext();) {
                    Element elementSon = (Element) ieson.next();
                    Map<String, Object> item = new HashMap<>();
                    item.put("fmatchid", fmatchid );
                    for (Iterator iaSon = elementSon.attributeIterator(); iaSon.hasNext();) {
                        Attribute aSon = (Attribute) iaSon.next();
                        item.put(aSon.getName(), aSon.getData());
                    }
                    list.add(item);
                }
            }
        }
    }
    
    public static void updateMatchTrafficLight(){
        if ( list_matchTrafficLight == null )
            list_matchTrafficLight = new ArrayList<>();
        else list_matchTrafficLight.clear();
        
        loadMatchObject(list_matchTrafficLight, "trafficlight", 0);
        System.out.println("更新交通灯记录:" + list_matchTrafficLight.size() + "笔");
    }
    
    public static void updateMatchCamera(){
        if ( list_matchCamera == null )
            list_matchCamera = new ArrayList<>();
        else list_matchCamera.clear();
        
        loadMatchObject(list_matchCamera, "cam", 0);
        System.out.println("更新摄像头记录:" + list_matchCamera.size() + "笔");
    }

    public static void updateMatchRoad( int fmatchid ){
        if ( list_matchRoad == null )
            list_matchRoad = new ArrayList<>();
        else list_matchRoad.clear();
        
        Document doc = getMatchMapDocument();
        Element root = doc.getRootElement();
        
        for (Iterator ie = root.elementIterator(); ie.hasNext();) {
            Element element = (Element) ie.next();
            if (element.attributeValue("name").equals("road")) {
                for (Iterator ieson = element.elementIterator(); ieson.hasNext();) {
                    Element elementSon = (Element) ieson.next();
                    Map<String, Object> item = new HashMap<>();
                    item.put("fmatchid", fmatchid );
                    for (Iterator iaSon = elementSon.attributeIterator(); iaSon.hasNext();) {
                        Attribute aSon = (Attribute) iaSon.next();
                        //System.out.println("找到属性:" + aSon.getName());
                        item.put(aSon.getName(), aSon.getData());
                        
                        for (Iterator i2 = elementSon.elementIterator(); i2.hasNext();) {
                            Element e2 = (Element) i2.next();
                            for (Iterator ia2 = e2.attributeIterator(); ia2.hasNext();) {
                                Attribute a2 = (Attribute) ia2.next();
                                //System.out.println("找到属性:" + a2.getName());
                                item.put(a2.getName(), a2.getData());
                            }
                        }
                    }
                    list_matchRoad.add(item);
                }
            }
        }
        
        System.out.println("更新路段记录:" + list_matchRoad.size() + "笔");
        
        /*String row;
        for(int i=0;i<list_matchTeamRoad.size();i++){
            row = "name:" + list_matchRoad.get(i).get("name").toString() + "," +
                    "x:" + list_matchRoad.get(i).get("x").toString() + "," +
                    "y:" + list_matchRoad.get(i).get("y").toString() + "," +
                    "points:" + list_matchRoad.get(i).get("points").toString();
            System.out.println(row);
        }*/  
    }
    
    public static void updateMatchCrossing(){
        if ( list_matchCrossing == null )
            list_matchCrossing = new ArrayList<>();
        else list_matchCrossing.clear();

        loadMatchObject(list_matchCrossing, "crossing", 0);
        System.out.println("更新路口记录:" + list_matchCrossing.size() + "笔");
    }
    
    public static void updateMatchTeamCar(){
        if ( list_matchTeamCar == null )
            list_matchTeamCar = new ArrayList<>();
        else list_matchTeamCar.clear();
        
        Map<String, Object> item;
        // 红队
        item = new HashMap<>();
        item.put("fmatchid", 0);
        item.put("fteamid", 0);
        item.put("fadd", 2);
        item.put("fcount", 6);
        list_matchTeamCar.add(item);

        // 蓝队
        item = new HashMap<>();
        item.put("fmatchid", 0);
        item.put("fteamid", 1);
        item.put("fadd", 2);
        item.put("fcount", 6);
        list_matchTeamCar.add(item);
    }
    
    public static void updateMatchTeamBike(){
        if ( list_matchTeamBike == null )
            list_matchTeamBike = new ArrayList<>();
        else list_matchTeamBike.clear();
        
        Map<String, Object> item;
        
        // 红队
        item = new HashMap<>();
        item.put("fmatchid", 0);
        item.put("fteamid", 0);
        item.put("fadd", 2);
        item.put("fcount", 10);
        list_matchTeamBike.add(item);

        // 蓝队
        item = new HashMap<>();
        item.put("fmatchid", 0);
        item.put("fteamid", 1);
        item.put("fadd", 2);
        item.put("fcount", 10);
        list_matchTeamBike.add(item);
    }

    public static void updateMatchTeamPolice(){
        if ( list_matchTeamPolice == null )
            list_matchTeamPolice = new ArrayList<>();
        else list_matchTeamPolice.clear();

        Map<String, Object> item;

        // 红队
        item = new HashMap<>();
        item.put("fmatchid", 0);
        item.put("fteamid", 0);
        item.put("ftype", 0);
        item.put("fname", "A类");
        item.put("fcount", 0);
        item.put("fpower", 5);
        item.put("fcount", 11);
        list_matchTeamPolice.add(item);

        item = new HashMap<>();
        item.put("fmatchid", 0);
        item.put("fteamid", 0);
        item.put("ftype", 1);
        item.put("fname", "B类");
        item.put("fcount", 0);
        item.put("fpower", 4);
        item.put("fcount", 11);
        list_matchTeamPolice.add(item);
        
        item = new HashMap<>();
        item.put("fmatchid", 0);
        item.put("fteamid", 0);
        item.put("ftype", 2);
        item.put("fname", "C类");
        item.put("fcount", 0);
        item.put("fpower", 3);
        item.put("fcount", 11);
        list_matchTeamPolice.add(item);
        
        item = new HashMap<>();
        item.put("fmatchid", 0);
        item.put("fteamid", 0);
        item.put("ftype", 3);
        item.put("fname", "协警");
        item.put("fcount", 0);
        item.put("fpower", 2);
        item.put("fcount", 30);
        list_matchTeamPolice.add(item);
        
        // 蓝队
        item = new HashMap<>();
        item.put("fmatchid", 0);
        item.put("fteamid", 1);
        item.put("ftype", 0);
        item.put("fname", "A类");
        item.put("fcount", 0);
        item.put("fpower", 5);
        item.put("fcount", 11);
        list_matchTeamPolice.add(item);

        item = new HashMap<>();
        item.put("fmatchid", 0);
        item.put("fteamid", 1);
        item.put("ftype", 1);
        item.put("fname", "B类");
        item.put("fcount", 0);
        item.put("fpower", 4);
        item.put("fcount", 11);
        list_matchTeamPolice.add(item);
        
        item = new HashMap<>();
        item.put("fmatchid", 0);
        item.put("fteamid", 1);
        item.put("ftype", 2);
        item.put("fname", "C类");
        item.put("fcount", 0);
        item.put("fpower", 3);
        item.put("fcount", 11);
        list_matchTeamPolice.add(item);
        
        item = new HashMap<>();
        item.put("fmatchid", 0);
        item.put("fteamid", 1);
        item.put("ftype", 3);
        item.put("fname", "协警");
        item.put("fcount", 0);
        item.put("fpower", 2);
        item.put("fcount", 30);
        list_matchTeamPolice.add(item);
    }    
    
    public static List<Map<String, Object>> getMatchList ( int fuserid ){
        if ( list_match == null )
            return new ArrayList<>();
        else
            return list_match;
    }

    public static List<Map<String, Object>> getMatchTeamList( int fmatchid ){
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i=0; i< list_matchTeam.size(); i++){
            if ( fmatchid == (int) list_matchTeam.get(i).get("fmatchid")){
                list.add(list_matchTeam.get(i));
            }
        }
        return list;
    }
    
    public static boolean dropMatchTeamDropObject( HashMap data ){
        if ( list_matchTeamDropObject == null )
            list_matchTeamDropObject = new ArrayList<>();
        
        for ( Map<String, Object> o:list_matchTeamDropObject )
        {
            if ( o.get("fteamid").equals(data.get("fteamid")) &&
            o.get("fobjectid").equals(data.get("fobjectid")) &&
            o.get("fobjectname").equals(data.get("fobjectname")) &&
            o.get("fobjectx").equals(data.get("fobjectx")) &&
            o.get("fobjecty").equals(data.get("fobjecty")) &&
            o.get("fmatchid").equals(data.get("fmatchid")) &&
            o.get("fparentname").equals(data.get("fparentname")) &&
            o.get("fparenttype").equals(data.get("fparenttype")))
                return false;
        }

        Map<String, Object> object = new HashMap<>();
        object.put("fteamid",data.get("fteamid"));
        object.put("fobjectid",data.get("fobjectid"));
        object.put("fobjectname",data.get("fobjectname"));
        object.put("fobjectx",data.get("fobjectx"));
        object.put("fobjecty",data.get("fobjecty"));
        object.put("fmatchid",data.get("fmatchid"));
        object.put("fparentname",data.get("fparentname"));
        object.put("fparenttype",data.get("fparenttype"));
        
        list_matchTeamDropObject.add(object);
        return true;
    }
    
    public static boolean pickMatchTeamDropObject( HashMap data ){
        if ( list_matchTeamDropObject == null )
            list_matchTeamDropObject = new ArrayList<>();
        
        for ( Map<String, Object> o:list_matchTeamDropObject )
        {
            if ( o.get("fteamid").equals(data.get("fteamid")) &&
            o.get("fobjectid").equals(data.get("fobjectid")) &&
            o.get("fobjectname").equals(data.get("fobjectname")) &&
            o.get("fobjectx").equals(data.get("fobjectx")) &&
            o.get("fobjecty").equals(data.get("fobjecty")) &&
            o.get("fmatchid").equals(data.get("fmatchid")) &&
            o.get("fparentname").equals(data.get("fparentname")) &&
            o.get("fparenttype").equals(data.get("fparenttype")))
            {
                list_matchTeamDropObject.remove(o);
                return true;
            }
        }
        return false;
    }
    
    public static List<Map<String, Object>> getMatchTeamPostList( int fteamid ){
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i=0; i< list_matchTeamPost.size(); i++){
            if ( fteamid == (int) list_matchTeamPost.get(i).get("fteamid")){
                list.add(list_matchTeamPost.get(i));
            }
        }
        return list;
    }
    
    public static int getMatchStatus( int fmatchid ){
        for ( Map<String, Object> match: list_match )
            if ( match.get("fid").equals(fmatchid) ){
                return (int)match.get("fstatus");
            }
        
        return -1;
    }
    
    
    
    public static Map<String, Object> joinTeam( int fteamid, int fpostid ){
        Map<String, Object> result = new HashMap<>();
        if ( list_matchTeam != null )
        for (int i=0; i< list_matchTeam.size(); i++){
            if ( fteamid == (int) list_matchTeam.get(i).get("fid")){
                
            }
        }
        return null;
    }
    
    public static Map<String, Object> isPostAvailable( int fteamid, String fpostidfield ){
        Map<String, Object> result = new HashMap<>();
        result.put("name", fpostidfield);
        if ( list_matchTeam != null )
        for (int i=0; i< list_matchTeam.size(); i++){
            if ( fteamid == (int) list_matchTeam.get(i).get("fid")){
                if ( (int) list_matchTeam.get(i).get( fpostidfield ) > -1 )
                {
                    result.put("result", true);
                } else {
                    result.put("result", false);
                }
            }
        }
        result.put("result", false);
        return result;
    }
    
    public static List<Map<String,Object>> getMatchTopic ( int fteamid, int fpostid ){
        List<Map<String, Object>> list = new ArrayList<>();
        
        return list;
    }
    
    public static List<Map<String,Object>> getMatchAttachment( int ftopicid ){
        List<Map<String, Object>> list = new ArrayList<>();
        
        for ( Map<String, Object> object: list_topicAttachment)
            if ( object.get("ftopicid").equals(ftopicid))
                list.add(object);
        
        return list;
    }
    
    public static List<Map<String, Object>> getMatchQuiz( int ftopicid, int fpostid, int fquizid ){
        //LOG.log(Level.INFO, "传入的参数 ftopicid:{0}, fpostid:{1}, fquizid:{2}", new Object[]{ftopicid, fpostid, fquizid});
        List<Map<String, Object>> list = new ArrayList<>();
        if ( fquizid == 0)
        {
            for (Map<String,Object> quiz:list_quiz){
                if ( quiz.get("ftopicid").equals(ftopicid)){
                    for (Map<String,Object> quizPost:list_quizpost)
                        if (quizPost.get("fpostid").equals(fpostid) && quizPost.get("fquizid").equals(quiz.get("fid"))){
                            list.add(quiz);
                        }
                }
            }
        }else{
            for (Map<String,Object> quiz:list_pubquiz){
                if ( quiz.get("fid").equals(fquizid)){
                    list.add(quiz);
                    break;
                }
            }
        }
        LOG.log(Level.CONFIG, "取出问题列表:{0}", list.size());
        return list;
    }
    
    public static List<Map<String,Object>> getMatchAnswer( int fquizid ) {
        List<Map<String, Object>> list = new ArrayList<>();
        
        for (Map<String, Object> answer: list_answer)
            if ( answer.get("fquizid").equals(fquizid))
                list.add(answer);
        return list;
    }
    
    public static List<Map<String, Object>> getMatchTrafficLight( int fmatchid ){
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i=0; i< list_matchTrafficLight.size(); i++){
            if ( fmatchid == (int) list_matchTrafficLight.get(i).get("fmatchid")){
                list.add(list_matchTrafficLight.get(i));
            }
        }
        return list;
    }
    
    public static List<Map<String, Object>> getMatchCamera( int fmatchid ){
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i=0; i< list_matchCamera.size(); i++){
            if ( fmatchid == (int) list_matchCamera.get(i).get("fmatchid")){
                list.add(list_matchCamera.get(i));
            }
        }
        return list;
    }
    
    public static List<Map<String, Object>> getMatchTeamRoad( int fmatchid ){
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i=0; i< list_matchRoad.size(); i++){
            if ( fmatchid == (int) list_matchRoad.get(i).get("fmatchid")){
                list.add(list_matchRoad.get(i));
            }
        }
        return list;
    }
    
    public static List<Map<String, Object>> getMatchTeamCrossing( int fmatchid ){
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i=0; i< list_matchCrossing.size(); i++){
            if ( fmatchid == (int) list_matchCrossing.get(i).get("fmatchid")){
                list.add(list_matchCrossing.get(i));
            }
        }
        return list;
    }
    public static List<Map<String, Object>> getMatchTeamCar( int fmatchid ){
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i=0; i< list_matchTeamCar.size(); i++){
            if ( fmatchid == (int) list_matchTeamCar.get(i).get("fmatchid")){
                list.add(list_matchTeamCar.get(i));
            }
        }
        return list;
    }
    
    public static List<Map<String, Object>> getMatchTeamBike( int fmatchid ){
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i=0; i< list_matchTeamBike.size(); i++){
            if ( fmatchid == (int) list_matchTeamBike.get(i).get("fmatchid")){
                list.add(list_matchTeamBike.get(i));
            }
        }
        return list;
    }
    
    public static List<Map<String, Object>> getMatchTeamPolice( int fmatchid ){
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i=0; i< list_matchTeamPolice.size(); i++){
            if ( fmatchid == (int) list_matchTeamPolice.get(i).get("fmatchid")){
                list.add(list_matchTeamPolice.get(i));
            }
        }
        return list;
    }

    public static List<Map<String, Object>> getDepartment()
    {
        return list_department;
    }    

    public static List<Map<String, Object>> getDivision( int fdepartmentid ){
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i=0; i< list_division.size(); i++){
            if ( fdepartmentid == (int) list_division.get(i).get("fdepartmentid")){
                list.add(list_division.get(i));
            }
        }
        return list;
    }
    
    public static List<Map<String, Object>> getBranch( int fdivisionid ){
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i=0; i< list_branch.size(); i++){
            if ( fdivisionid == (int) list_branch.get(i).get("fdivisionid") ){
                list.add(list_branch.get(i));
            }
        }
        return list;
    }
    
    public static List<Map<String, Object>> getGroupList(){
        return list_group;
    }    
    
    public static List<Map<String, Object>> getUserGroupList( int id  ){
        List<Map<String, Object>> userGroupList = new ArrayList<>();
        for (int i=0; i< list_groupuser.size(); i++) {
            if ( id == (int)list_groupuser.get(i).get("fuserid")){
                userGroupList.add(list_groupuser.get(i));
            }
        }
        return userGroupList;
    }
    
    public static Map<String, Object> getUserInfo( String name ){
        Map<String, Object> userInfo = new HashMap<>();
        boolean bgot = false;
        for (int i=0; i< list_user.size(); i++) {
            if ( name.equals(list_user.get(i).get("fname").toString())){

                userInfo.put("fid", list_user.get(i).get("fid"));
                userInfo.put("fname", list_user.get(i).get("fname"));
                userInfo.put("fpassword", list_user.get(i).get("fpassword"));
                userInfo.put("fbranchid", list_user.get(i).get("fbranchid"));
                userInfo.put("fregisttime", list_user.get(i).get("fregisttime"));
                userInfo.put("flevel", list_user.get(i).get("flevel"));
                userInfo.put("fexp", list_user.get(i).get("fexp"));
                userInfo.put("fdescription", list_user.get(i).get("fdescription"));
                userInfo.put("fphotourl", list_user.get(i).get("fphotourl"));

                bgot = true;
                break;
            }
        }

        if ( bgot ) return userInfo;
        else return null;
    }
    
    public static boolean dropMatchTeamObject( Object data ){

        Map<String,Object> d = (Map<String,Object>) data;
        int fteamid = Integer.parseInt(d.get("fteamid").toString());
        int fobjectid = Integer.parseInt(d.get("fobjectid").toString());
        String fobjectname = d.get("fobjectname").toString();
        double fobjectx = Double.parseDouble(d.get("fobjectx").toString());
        double fobjecty = Double.parseDouble(d.get("fobjecty").toString());
        int fmatchid = Integer.parseInt(d.get("fmatchid").toString());
        String fparentname = d.get("fparentname").toString();
        int fparenttype = Integer.parseInt(d.get("fparenttype").toString());
        
        if ( list_matchTeamDropObject == null )
            list_matchTeamDropObject = new ArrayList<>();

        for ( Map<String, Object> o:list_matchTeamDropObject )
        {
            if ( o.get("fteamid").equals(fteamid) &&
            o.get("fobjectid").equals(fobjectid) &&
            o.get("fobjectname").equals(fobjectname) &&
            o.get("fobjectx").equals(fobjectx) &&
            o.get("fobjecty").equals(fobjecty) &&
            o.get("fmatchid").equals(fmatchid) &&
            o.get("fparentname").equals(fparentname) &&
            o.get("fparenttype").equals(fparenttype))
                return false;
        }

        //LOG.log(Level.WARNING, "没有匹配记录, 准备添加, 名称{0}", fobjectname);

        Map<String, Object> newOne = new HashMap<>();
        newOne.put("fteamid",fteamid);
        newOne.put("fobjectid",fobjectid);
        newOne.put("fobjectname",fobjectname);
        newOne.put("fobjectx",fobjectx);
        newOne.put("fobjecty",fobjecty);
        newOne.put("fmatchid",fmatchid);
        newOne.put("fparentname",fparentname);
        newOne.put("fparenttype",fparenttype);
        
        list_matchTeamDropObject.add( newOne );

        //LOG.log(Level.INFO, "fitemd={0}, fobjectid={1}", new Object[]{
       //                 fteamid, fobjectid });
        switch ( fobjectid )
        {
            case MAP_OBJECT_CAR:
                for(Map<String,Object> object: list_matchTeamCar)
                {
                    //LOG.log(Level.INFO, "fitemd={0}, fobjectid={1}, object.get(\"fteamid\")={2}", new Object[]{
                        //fteamid, fobjectid, object.get("fteamid") });
                    if ( object.get("fteamid").equals(fteamid) )
                    {
                        object.put("fcount", ((int)object.get("fcount")) - 1);
                        return true;
                    }
                }
            case MAP_OBJECT_BIKE:
                for(Map<String,Object> object: list_matchTeamBike)
                    if ( object.get("fteamid").equals(fteamid) )
                    {
                        object.put("fcount", ((int)object.get("fcount")) - 1);
                        return true;
                    }
            case MAP_OBJECT_PMA:
                for(Map<String,Object> object: list_matchTeamPolice)
                    if ( object.get("fteamid").equals(fteamid) && object.get("ftype").equals(0))
                    {
                        object.put("fcount", ((int)object.get("fcount")) - 1);
                        return true;
                    }
            case MAP_OBJECT_PMB:
                for(Map<String,Object> object: list_matchTeamPolice)
                    if ( object.get("fteamid").equals(fteamid) && object.get("ftype").equals(1))
                    {
                        object.put("fcount", ((int)object.get("fcount")) - 1);
                        return true;
                    }
            case MAP_OBJECT_PMC:
                for(Map<String,Object> object: list_matchTeamPolice)
                    if ( object.get("fteamid").equals(fteamid) && object.get("ftype").equals(2))
                    {
                        object.put("fcount", ((int)object.get("fcount")) - 1);
                        return true;
                    }
            case MAP_OBJECT_PMX:
                for(Map<String,Object> object: list_matchTeamPolice)
                    if ( object.get("fteamid").equals(fteamid) && object.get("ftype").equals(3))
                    {
                        object.put("fcount", ((int)object.get("fcount")) - 1);
                        return true;
                    }
        }
        return false;
    }
    
    public static boolean pickMatchTeamObject( Object data ){
        
        Map<String,Object> d = (Map<String,Object>) data;

        int fteamid = Integer.parseInt(d.get("fteamid").toString());
        int fobjectid = Integer.parseInt(d.get("fobjectid").toString());
        String fobjectname = d.get("fobjectname").toString();
        double fobjectx = Double.parseDouble(d.get("fobjectx").toString());
        double fobjecty = Double.parseDouble(d.get("fobjecty").toString());
        int fmatchid = Integer.parseInt(d.get("fmatchid").toString());
        String fparentname = d.get("fparentname").toString();
        int fparenttype = Integer.parseInt(d.get("fparenttype").toString());
        
        if ( list_matchTeamDropObject == null ) return false;
        
        boolean bfound = false;
        for ( Map<String, Object> o:list_matchTeamDropObject )
        {
            if ( o.get("fteamid").equals(fteamid) &&
            o.get("fobjectid").equals(fobjectid) &&
            o.get("fobjectname").equals(fobjectname) &&
            o.get("fobjectx").equals(fobjectx) &&
            o.get("fobjecty").equals(fobjecty) &&
            o.get("fmatchid").equals(fmatchid) &&
            o.get("fparentname").equals(fparentname) &&
            o.get("fparenttype").equals(fparenttype))
            {
                bfound = true;
                list_matchTeamDropObject.remove(o);
                break;
            }
        }

        if ( bfound == false ) return false;

        //LOG.log(Level.INFO, "switch ( fobjectid ){0}", true );
        switch ( fobjectid )
        {
            case MAP_OBJECT_CAR:
                for(Map<String,Object> object: list_matchTeamCar)
                    if ( object.get("fteamid").equals(fteamid) )
                    {
                        object.put("fcount", ((int)object.get("fcount")) + 1);
                        return true;
                    }
            case MAP_OBJECT_BIKE:
                for(Map<String,Object> object: list_matchTeamBike)
                    if ( object.get("fteamid").equals(fteamid) )
                    {
                        object.put("fcount", ((int)object.get("fcount")) + 1);
                        return true;
                    }
            case MAP_OBJECT_PMA:
                for(Map<String,Object> object: list_matchTeamPolice)
                    if ( object.get("fteamid").equals(fteamid) && object.get("ftype").equals(0))
                    {
                        object.put("fcount", ((int)object.get("fcount")) + 1);
                        return true;
                    }
            case MAP_OBJECT_PMB:
                for(Map<String,Object> object: list_matchTeamPolice)
                    if ( object.get("fteamid").equals(fteamid) && object.get("ftype").equals(1))
                    {
                        object.put("fcount", ((int)object.get("fcount")) + 1);
                        return true;
                    }
            case MAP_OBJECT_PMC:
                for(Map<String,Object> object: list_matchTeamPolice)
                    if ( object.get("fteamid").equals(fteamid) && object.get("ftype").equals(2))
                    {
                        object.put("fcount", ((int)object.get("fcount")) + 1);
                        return true;
                    }
            case MAP_OBJECT_PMX:
                for(Map<String,Object> object: list_matchTeamPolice)
                    if ( object.get("fteamid").equals(fteamid) && object.get("ftype").equals(3))
                    {
                        object.put("fcount", ((int)object.get("fcount")) + 1);
                        return true;
                    }
        }
        return false;
    }
    
    public static VoResponseData getMatchTeamObject( int fmatchid ){

        VoResponseData rd = new VoResponseData();
        rd.message = GameMessage.MESSAGE_GETMATCHTEAMOBJECT;

        List<Map<String, Object>> list;
        Map<String, Object> data;
        
        data = new HashMap<>();
        
        list = new ArrayList<>();
        for ( Map<String, Object> o: GameData.list_matchTeamCar ){
            if (o.get("fmatchid").equals(fmatchid))
                list.add(o);
        }
        data.put("car", list);
        
        list = new ArrayList<>();
        for ( Map<String, Object> o: GameData.list_matchTeamBike ){
            if (o.get("fmatchid").equals(fmatchid))
                list.add(o);
        }
        data.put("bike", list);
            
        list = new ArrayList<>();
        for ( Map<String, Object> o: GameData.list_matchTeamPolice ){
            if (o.get("fmatchid").equals(fmatchid))
                list.add(o);
        }
        data.put("police", list);
        
        if ( GameData.list_matchTeamDropObject == null )
               GameData.list_matchTeamDropObject = new ArrayList<>();
        
        list = new ArrayList<>();
        for ( Map<String, Object> o: GameData.list_matchTeamDropObject ){
            if (o.get("fmatchid").equals(fmatchid))
                list.add(o);
        }
        data.put("dropobject", list);
        
        rd.data = data;
        //LOG.log(Level.SEVERE, "已经读取缓存地图数据:{0}", data);
        return rd;
    }
    
    public static void insertResultToList(ResultSet rs, List<Map<String, Object>> targetList){
        List<Map<String, Object>> list = getResultList(rs);
        for (int i=0; i< list.size(); i++){
            targetList.add(list.get(i));
        }
    }
    
    public static Map<String, Object> getResultMap(ResultSet rs){
        try {
            Map<String, Object> map = new HashMap<>();
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            rs.first();
            for (int i = 1; i <= columnCount; i++) {
                map.put(md.getColumnName(i), rs.getObject(i));
            }
            return map;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }    

    public static List<Map<String, Object>> getResultList(ResultSet rs){
        try {
            List<Map<String, Object>> list = new ArrayList<>();
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            //rs.first();
            while(rs.next()){
                Map<String, Object> rowData = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getObject(i));
                }
                list.add( rowData );
            }
            return list;
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
