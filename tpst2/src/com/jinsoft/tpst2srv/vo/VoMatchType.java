/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv.vo;

/**
 *
 * @author KeyuGG
 */
public class VoMatchType {
    
    private int fid;
    private String fname;
    
    public VoMatchType(int fid, String fname)
    {
        this.fid = fid;
        this.fname = fname;
    }
    
    public int getFid() { return this.fid; }
    public String getFname() { return this.fname; }
    
    @Override
    public String toString()
    {
        return fname == null ? "" : fname;
    }
}
