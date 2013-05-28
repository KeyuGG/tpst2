/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 *
 * @author KeyuGG
 */
public class GameObject implements Serializable, ManagedObject {
    
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = Logger.getLogger(GameObject.class.getName());
    
    private String fname;
    private String description;
    
    public GameObject( String fname, String description ) {
        this.fname = fname;
        this.description = description;
    }

    public void setName(String fname) {
        AppContext.getDataManager().markForUpdate(this);
        this.fname = fname;
    }
    
    public String getName() {
        return fname;
    }
    
    public void setDescription(String description) {
        AppContext.getDataManager().markForUpdate(this);
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }    
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getName();
    }
}
