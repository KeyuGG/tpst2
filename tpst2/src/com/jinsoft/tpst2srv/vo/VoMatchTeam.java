/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv.vo;

/**
 *
 * @author KeyuGG
 */
public class VoMatchTeam {
    
    private int fid;            // 小队 id
    private String fname;       // 小队名称
    private int fmatchid;       // 任务 id
    private int fbranchid;      // 大队 id
    private String fbranchname; // 大队名称
    private VoMatchPost fpost;  // 角色
    
    public VoMatchTeam(int fid, String fname, int fmatchid, int fbranchid, String fbranchname ){
        this.fid = fid;
        this.fname = fname;
        this.fmatchid = fmatchid;
        this.fbranchid = fbranchid;
        this.fbranchname = fbranchname;
        this.fpost = new VoMatchPost();
    }
    
    public int getFId() { return this.fid; }
    public String getFName() { return this.fname; }
    public int getFMatchId() { return this.fmatchid; }
    public int getFBranchId() { return this.fbranchid; }
    public String getFBranchName() { return this.fbranchname; }
    public VoMatchPost getFPost() { return this.fpost; };

    @Override
    public String toString()
    {
        return fname == null ? "" : fname;
    }    
}
