package com.jinsoft.tpst2srv;

/**
 *
 * @author KeyuGG
 */
public class GameMessage {
    
    public static final int MESSAGE_GETDEPARTMENT                               = 0x10000;
    public static final int MESSAGE_GETDIVISION                                 = 0x10001;
    public static final int MESSAGE_GETBRANCH                                   = 0x10002;
    public static final int MESSAGE_GETGROUPLIST                                = 0x10003;
    public static final int MESSAGE_GETUSERGROUPLIST                            = 0x10004;
    public static final int MESSAGE_GETMATCHLIST                                = 0x10005;
    public static final int MESSAGE_GETMATCHTEAMLIST                            = 0x10006;
    public static final int MESSAGE_GETMATCHTEAMPOSTLIST                        = 0x10007;
    public static final int MESSAGE_GETMATCHTRAFFICLIGHT                        = 0x10008;
    public static final int MESSAGE_GETMATCHCAMERA                              = 0x10009;
    public static final int MESSAGE_GETMATCHTEAMROAD                            = 0x10010;
    public static final int MESSAGE_GETMATCHTEAMCROSSING                        = 0x10011;
    public static final int MESSAGE_GETMATCHTEAMCAR                             = 0x10012;
    public static final int MESSAGE_GETMATCHTEAMBIKE                            = 0x10013;
    public static final int MESSAGE_GETMATCHTEAMPOLICE                          = 0x10014;
    public static final int MESSAGE_GETMATCHTEAMOBJECT                          = 0x10015;
    public static final int MESSAGE_GETMATCHTOPIC                               = 0x10016;
    public static final int MESSAGE_GETMATCHTOPICATTACHMENT                     = 0x10017;
    public static final int MESSAGE_GETMATCHQUIZ                                = 0x10018;
    public static final int MESSAGE_GETMATCHANSWER                              = 0x10019;
    
    public static final int MESSAGE_SUBMITANSWER                                = 0x10040;
    public static final int MESSAGE_SUBMITANSWER_RESULT                         = 0x10041;
    
    public static final int MESSAGE_DROPMATCHTEAMOBJECT				= 0x10050;  
    public static final int MESSAGE_PICKMATCHTEAMOBJECT				= 0x10051;

    public static final int MESSAGE_USER_LOGIN                                  = 0x10100;
    public static final int MESSAGE_USER_LOGIN_SUCCESS                          = 0x10101;
    public static final int MESSAGE_USER_LOGIN_FAILED                           = 0x10102;
    public static final int MESSAGE_USER_LOGOUT                               	= 0x10103;
    public static final int MESSAGE_USER_REGIST                                 = 0x10104;
    public static final int MESSAGE_USER_REGIST_SUCCESS                         = 0x10105;
    public static final int MESSAGE_USER_REGIST_FAILED                          = 0x10106;
    
    public static final int MESSAGE_TEAM_JOIN                                   = 0x10200;
    public static final int MESSAGE_TEAM_JOIN_SUCCESS                           = 0x10201;
    public static final int MESSAGE_TEAM_JOIN_FAILED                            = 0x10202;
    public static final int MESSAGE_TEAM_DISJOIN                                = 0x10203;
    
    public static final int MESSAGE_MATCH_START                                 = 0x10300;
    public static final int MESSAGE_MATCH_STATUS                                = 0x10301;

    public static final int MESSAGE_TASK_MATCH_TIME                             = 0x10400;
    public static final int MESSAGE_TASK_GEN_TOPIC                              = 0x10401;
    public static final int MESSAGE_TASK_GEN_PUBLICTOPIC                        = 0x10410;
    
    public static final int MESSAGE_CHECKPOSTAVAILABLE                          = 0x10508;
    
    public static final int SERVER_UPDATE_DEPARTMENTLIST                        = 0x20000;
    public static final int SERVER_UPDATE_DIVISIONLIST                          = 0x20001;
    public static final int SERVER_UPDATE_BRANCHLIST                            = 0x20002;
    public static final int SERVER_UPDATE_USERLIST                              = 0x20003;
    public static final int SERVER_UPDATE_GROUPLIST                             = 0x20004;
    public static final int SERVER_UPDATE_GROUPUSERLIST                         = 0x20005;
    public static final int SERVER_UPDATE_MATCHLIST                             = 0x20006;
    public static final int SERVER_UPDATE_MATCHTEAMLIST                         = 0x20007;
    public static final int SERVER_UPDATE_MATCHPUBTOPICLIST                     = 0x20008;
    public static final int SERVER_UPDATE_MATCHTOPICLIST                        = 0x20009;
    public static final int SERVER_UPDATE_MATCHPUBQUIZLIST                      = 0x20010;
    public static final int SERVER_UPDATE_MATCHTOPICATTACHMENTLIST              = 0x20011;
    public static final int SERVER_UPDATE_MATCHQUIZLIST                         = 0x20012;
    public static final int SERVER_UPDATE_MATCHQUIZPOSTLIST                     = 0x20013;
    public static final int SERVER_UPDATE_MATCHANSWERLIST                       = 0x20014;
    
    public static final int GAME_INIT_TOPIC                                     = 0x20100;
    public static final int GAME_CULL_TOPIC                                     = 0x20101;
    public static final int GAME_GET_TOPIC                                      = 0x20103;
    public static final int GAME_GET_QUIZ                                       = 0x20104;
    public static final int GAME_SUBMIT_ANSWER                                  = 0x20105;
    public static final int GAME_GET_SCORE                                      = 0x20106;
    public static final int GAME_CHK_MATCH_ISSTARTED                            = 0x20107;
    
}
