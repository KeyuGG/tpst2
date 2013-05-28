/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv.vo;

/**
 *
 * @author KeyuGG
 */
public final class VoMatchPost {
    
    private int fgeneralofficeid;                   // 办公室主任
    private String fgeneralofficename;
    private int faccidentdirectorid;                // 事故中队长
    private String faccidentdirectorname;
    private int forderdirectorid;                   // 秩序中队长
    private String forderdirectorname;
    private int fcommandcenterid;                   // 监控中心主任
    private String fcommandcentername;
    private int fbranchdirectorid;                  // 大队长
    private String fbranchdirectorname;
    private int fpoliticalinstructorid;             // 副教导员
    private String fpoliticalinstructorname;
    private int fdrillmasterid;                     // 对抗演练指挥教官
    private String fdrillmastername;
    
    /*
    public VoMatchPost(int fgeneralofficeid,String fgeneralofficename,int faccidentdirectorid,String faccidentdirectorname,
                        int forderdirectorid,String forderdirectorname,int fcommandcenterid,String fcommandcentername,int fbranchdirectorid,String fbranchdirectorname,    
                        int fpoliticalinstructorid,String fpoliticalinstructorname,int fdrillmasterid, String fdrillmastername){
    
    this.fgeneralofficeid = fgeneralofficeid;
    this.fgeneralofficename = fgeneralofficename;
    this.faccidentdirectorid = faccidentdirectorid;
    this.faccidentdirectorname = faccidentdirectorname;
    this.forderdirectorid = forderdirectorid;
    this.forderdirectorname = forderdirectorname;
    this.fcommandcenterid = fcommandcenterid;
    this.fcommandcentername = fcommandcentername;
    this.fbranchdirectorid = fbranchdirectorid;
    this.fbranchdirectorname = fbranchdirectorname;
    this.fpoliticalinstructorid = fpoliticalinstructorid;
    this.fpoliticalinstructorname = fpoliticalinstructorname;
    this.fdrillmasterid = fdrillmasterid;
    this.fdrillmastername = fdrillmastername;
}*/
    public VoMatchPost(){
        init();
    }
    
    public int getFGeneralofficeid(){ return fgeneralofficeid; };
    public String getFGeneralofficename(){ return fgeneralofficename; };
    public int getFAccidentdirectorid(){ return faccidentdirectorid; };
    public String getFAccidentdirectorname(){ return faccidentdirectorname; };
    public int getFOrderdirectorid(){ return forderdirectorid; };
    public String getFOrderdirectorname(){ return forderdirectorname; };
    public int getFCommandcenterid(){ return fcommandcenterid; };
    public String getFCommandcentername(){ return fcommandcentername; };
    public int getFBranchdirectorid(){ return fbranchdirectorid; };
    public String getFBranchdirectorname(){ return fbranchdirectorname; };
    public int getFPoliticalinstructorid(){ return fpoliticalinstructorid; };
    public String getFPoliticalinstructorname(){ return fpoliticalinstructorname; };
    public int getFDrillmasterid(){ return fdrillmasterid; };
    public String getFDrillmastername(){ return fdrillmastername; };
    
    public void setFGeneraloffice( int fgeneralofficeid, String fgeneralofficename ){ this.fgeneralofficeid = fgeneralofficeid; this.fgeneralofficename = fgeneralofficename; };
    public void setFAccidentdirector( int faccidentdirectorid, String faccidentdirectorname ){ this.faccidentdirectorid = faccidentdirectorid; this.faccidentdirectorname = faccidentdirectorname; };
    public void setFOrderdirector( int forderdirectorid, String forderdirectorname ){ this.forderdirectorid = forderdirectorid; this.forderdirectorname = forderdirectorname; };
    public void setFCommandcenter( int fcommandcenterid, String fcommandcentername ){ this.fcommandcenterid = fcommandcenterid; this.fcommandcentername = fcommandcentername; };
    public void setFBranchdirector( int fbranchdirectorid, String fbranchdirectorname ){ this.fbranchdirectorid = fbranchdirectorid; this.fbranchdirectorname = fbranchdirectorname; };
    public void setFPoliticalinstructor( int fpoliticalinstructorid, String fpoliticalinstructorname ){ this.fpoliticalinstructorid = fpoliticalinstructorid; this.fpoliticalinstructorname = fpoliticalinstructorname; };
    public void setFDrillmaster( int fdrillmasterid, String fdrillmastername ){ this.fdrillmasterid = fdrillmasterid; this.fdrillmastername = fdrillmastername; };
    
    public void init(){
        this.fgeneralofficeid = -1;
        this.fgeneralofficename = "";
        this.faccidentdirectorid = -1;
        this.faccidentdirectorname = "";
        this.forderdirectorid = -1;
        this.forderdirectorname = "";
        this.fcommandcenterid = -1;
        this.fcommandcentername = "";
        this.fbranchdirectorid = -1;
        this.fbranchdirectorname = "";
        this.fpoliticalinstructorid = -1;
        this.fpoliticalinstructorname = "";
        this.fdrillmasterid = -1;
        this.fdrillmastername = "";        
    }
}
