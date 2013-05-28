/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv.vo;

/**
 *
 * @author KeyuGG
 */
public class VoMatchResource {

    private int fpmacount;
    private int fpmbcount;
    private int fpmccount;
    private int fpmzcount;
    private int fpccount;
    private int fpbcount;
    
    public VoMatchResource(int fpmacount,int fpmbcount,int fpmccount,int fpmzcount,int fpccount,int fpbcount){
        this.fpmacount = fpmacount;
        this.fpmbcount = fpmbcount;
        this.fpmccount = fpmccount;
        this.fpmzcount = fpmzcount;
        this.fpccount = fpccount;
        this.fpbcount = fpbcount;        
    }
    
    public int getFPmaCount() { return this.fpmacount; }
    public void getFPmaCount(int fpmacount)
    {
        this.fpmacount = fpmacount;
    }
    
    public int getFPmbCount() { return this.fpmbcount; }
    public void getFPmbCount(int fpmbcount)
    {
        this.fpmbcount = fpmbcount;
    }
    
    public int getFPmcCount() { return this.fpmccount; }
    public void getFPmcCount(int fpmccount)
    {
        this.fpmccount = fpmccount;
    }
    
    public int getFPmzCount() { return this.fpmzcount; }
    public void getFPmzCount(int fpmzcount)
    {
        this.fpmzcount = fpmzcount;
    }
    
    public int getFPcCount() { return this.fpccount; }
    public void getFPcCount(int fpccount)
    {
        this.fpccount = fpccount;
    }
    
    public int getFPbCount() { return this.fpbcount; }
    public void getFPbCount(int fpbcount)
    {
        this.fpbcount = fpbcount;
    }    
}
