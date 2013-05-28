/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv.vo;

/**
 *
 * @author KeyuGG
 */
public class VoMatchModel {
    
    private int fid;
    private int fmatchtypeid;
    private String fname;
    
    public VoMatchModel(int fid, int fmatchtypeid, String fname)
    {
        this.fid = fid;
        this.fname = fname;
        this.fmatchtypeid = fmatchtypeid;
    }
    
    public int getFId() { return this.fid; }
    public String getFName() { return this.fname; }
    public int getFMatchTypeId() { return this.fmatchtypeid; }
    
    @Override
    public String toString()
    {
        return fname == null ? "" : fname;
    }
}
