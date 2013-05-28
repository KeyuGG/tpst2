/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv.vo;

import java.util.ArrayList;

/**
 *
 * @author KeyuGG
 */
public class VoMatch {
    
    private int fid;                        // 编号
    private String ftitle;                  // 标题
    private String fdescription;            // 详细说明
    private String fmapurl;                 // 地图地址
    private String fxmlurl;                 // 地图配置文件地址
    private String fxmllocurl;              // 地图配置文件地址
    private String fcreationtime;           // 创建时间
    private String femustarttime;           // 模拟开始时间
    private String femuendtime;             // 模拟结束时间
    private String femucurtime;             // 模拟当前时间
    private int fmapid;                  // 地图 id
    private int fstatus;                    // 当前状态
    private int fteamcount;                 // 小队数量
    private int fmatchtypeid;               // 类型 id
    private String fmatchtypename;          // 类型 名称
    private int fmatchmodelid;              // 模板 id
    private String fmatchmodelname;         // 模板名称
    private ArrayList<VoMatchTeam> fteamdata; // 小队数据
    private VoMatchResource fmatchresource; // 任务资源
    
    public VoMatch(int fid, String ftitle, String fdescription, String fmapurl, String fxmlurl, String fxmllocurl, String fcreationtime,
            String femustarttime, String femuendtime, int fmapid, int fteamcount, int fmatchtypeid, String fmatchtypename,
            int fmatchmodelid, String fmatchmodelname, ArrayList<VoMatchTeam> fteamdata,VoMatchResource fmatchresource){
        
        this.fid = fid; 
        this.ftitle = ftitle;
        this.fdescription = fdescription;
        this.fmapurl = fmapurl;
        this.fxmlurl = fxmlurl;
        this.fxmllocurl = fxmllocurl;
        this.fcreationtime = fcreationtime;
        this.femustarttime = femustarttime;
        this.femuendtime = femuendtime;
        this.fmapid = fmapid;
        this.fteamcount = fteamcount;
        this.fmatchtypeid = fmatchtypeid;
        this.fmatchtypename = fmatchtypename;
        this.fmatchmodelid = fmatchmodelid;
        this.fmatchmodelname = fmatchmodelname;
        this.fteamdata = fteamdata;
        this.fmatchresource = fmatchresource;
    }
    
    @Override
    public String toString()
    {
        return ftitle == null ? "" : ftitle;
    }
    
    public int getFId(){ return fid; }
    public String getFTitle(){ return ftitle; }
    public String getFDescription(){ return fdescription; }
    public String getFMapUrl(){ return fmapurl; }
    public String getFXmlUrl(){ return fxmlurl; }
    public String getFXmlLocUrl() { return fxmllocurl; }
    public String getFCreationTime(){ return fcreationtime; }
    public String getFEmuStartTime(){ return femustarttime; }
    public String getFEmuEndTime(){ return femuendtime; }
    public String getFEmuCurTime(){ return femucurtime; }
    
    public void setFEmuCurTime(String stime)
    {
        this.femucurtime = stime;
    }
    
    public int getFMapId(){ return fmapid; }
    public int getFStatus(){ return fstatus; }
    
    public void setFStatus(int status)
    {
        this.fstatus = status;
    }
    
    public int getFTeamCount(){ return fteamdata.size(); }
    public int getFMatchtypeId(){ return fmatchtypeid; }
    public String getFMatchTypeName(){ return fmatchtypename; }
    public int getFMatchmodelId(){ return fmatchmodelid; }
    public String getFMatchModelName(){ return fmatchmodelname; }
    public ArrayList<VoMatchTeam> getFTeamData(){ return fteamdata;}
    public VoMatchResource getFMatchResource(){ return fmatchresource;}
}
