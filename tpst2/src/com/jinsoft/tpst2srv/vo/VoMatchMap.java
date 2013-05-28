/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv.vo;

/**
 *
 * @author KeyuGG
 */
public class VoMatchMap {
    
    private int fid;
    private String fname;
    private String fmapurl;
    private String fxmlurl;
    private String fxmllocurl;
    
    public VoMatchMap(int fid, String fname, String fmapurl, String fxmlurl, String fxmllocurl)
    {
        this.fid = fid;
        this.fname = fname;
        this.fmapurl = fmapurl;
        this.fxmlurl = fxmlurl;
        this.fxmllocurl = fxmllocurl;
    }
    
    public int getFId() { return this.fid; }
    public String getFName() { return this.fname; }
    public String getFMapUrl() { return this.fmapurl; }
    public String getFXmlUrl() { return this.fxmlurl; }
    public String getFXmlLocUrl() { return this.fxmllocurl; }
    
    @Override
    public String toString()
    {
        return fname == null ? "" : fname;
    }
}
