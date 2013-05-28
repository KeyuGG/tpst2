/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv.vo;

/**
 *
 * @author KeyuGG
 */
public class VoUser {
    private int fid;
    private int fbranchid;
    private String fname;
    private String fpassword;
    private String fregistime;
    private String fdescription;
    private String fphotourl;
    private int fexp;
    private int flevel;
    
    public VoUser(int fid, int fbranchid,String fname,String fpassword,String fregistime,String fdescription,String fphotourl,int fexp, int flevel){
        this.fid = fid;
        this.fbranchid = fbranchid;
        this.fname = fname;
        this.fpassword = fpassword;
        this.fregistime = fregistime;
        this.fdescription = fdescription;
        this.fphotourl = fphotourl;
        this.fexp = fexp;
        this.flevel = flevel;
    }
    
    public int getFId(){ return fid; }
    public int getFBranchid(){ return fbranchid; }
    public String getFName(){ return fname; }
    public String getFPassword(){ return fpassword; }
    public String getFRegistime(){ return fregistime; }
    public String getFDescription(){ return fdescription; }
    public String getFPhotourl(){ return fphotourl; }
    public int getFExp(){ return fexp; }
    public int getFLevel(){ return flevel; }
    
    public void setFId( int fid ){ this.fid=fid; }
    public void setFBranchid( int fbranchid ){ this.fbranchid=fbranchid; }
    public void setFName( String fname ){ this.fname=fname; }
    public void setFPassword( String fpassword ){ this.fpassword=fpassword; }
    public void setFRegistime( String fregistime ){ this.fregistime=fregistime; }
    public void setFDescription( String fdescription ){ this.fdescription=fdescription; }
    public void setFPhotourl( String fphotourl ){ this.fphotourl=fphotourl; }
    public void setFExp( int fexp ){ this.fexp=fexp; }
    public void setFLevel( int flevel ){ this.flevel=flevel; }
}
