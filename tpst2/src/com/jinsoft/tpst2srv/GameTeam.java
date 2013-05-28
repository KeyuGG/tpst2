/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KeyuGG
 */
public class GameTeam extends GameObject {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(GameTeam.class.getName());

    private final Set<ManagedReference<GameObject>> items = new HashSet<>();
    private final Set<ManagedReference<GameUser>> users = new HashSet<>();
    
    private int fid;
    private String fname;
    private int fmatchid;
    private int fbranchid;
    private String fbranchname;
    
    public GameTeam( int fid, String fname, int fmatchid, int fbranchid, String fbranchname ) {
        super( fname, fbranchname );
        this.fid = fid;
        this.fname = fname;
        this.fmatchid = fmatchid;
        this.fbranchid = fbranchid;
        this.fbranchname = fbranchname;     
    }
    
    public int getId() { return fid; }
    public int getMatchid() { return fmatchid; }
    public int getBranchid() { return fbranchid; }
    public String getBranchname() { return fbranchname; }
    

    public boolean addItem(GameObject item) {
        LOG.log(Level.INFO, "{0} 已放置到 {1}", new Object[] { item, this });

        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);

        return items.add(dataManager.createReference(item));
    }

    public boolean addUser(GameUser user) {
        //LOG.log(Level.INFO, "{0} 进入 {1}", new Object[] { user, this });
        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);

        return users.add(dataManager.createReference(user));
    }

    public boolean removeUser( GameUser user ) {
        //LOG.log( Level.INFO, " getTeam().removeUser(this); 开始 AppContext={0}", 1 );
        DataManager dataManager = AppContext.getDataManager();
        //LOG.log( Level.INFO, " DataManager dataManager = AppContext.getDataManager(); ");
        
        if ( user != null) {
            //LOG.log(Level.INFO, "{0} 离开 {1}", new Object[] { user, this });
            dataManager.markForUpdate(this);            
        }else{
            LOG.info("用户为空");
        }
        return users.remove( dataManager.createReference(user) );
    }

    public String look(GameUser looker) {
        LOG.log(Level.INFO, "{0} 在小队 {1}", new Object[] { looker, this });

        StringBuilder output = new StringBuilder();
        output.append("你正在").append(getName()).append("小队.\n");

        List<GameUser> otherUsers =
            getUsersExcluding(looker);

        if (!otherUsers.isEmpty()) {
            output.append("也在这里");
            appendPrettyList(output, otherUsers);
            output.append(".\n");
        }

        if (!items.isEmpty()) {
            output.append("在地板上你看见:\n");
            for (ManagedReference<GameObject> itemRef : items) {
                GameObject item = itemRef.get();
                output.append(item.getName()).append('\n');
            }
        }

        return output.toString();
    }

    /**
     * Appends the names of the {@code GameObject}s in the list
     * to the builder, separated by commas, with an "and" before the final
     * item.
     *
     * @param builder the {@code StringBuilder} to append to
     * @param list the list of items to format
     */
    private void appendPrettyList(StringBuilder builder, List<? extends GameObject> list)
    {
        if (list.isEmpty()) {
            return;
        }

        int lastIndex = list.size() - 1;
        GameObject last = list.get(lastIndex);

        Iterator<? extends GameObject> it =
            list.subList(0, lastIndex).iterator();
        if (it.hasNext()) {
            GameObject other = it.next();
            builder.append(other.getName());
            while (it.hasNext()) {
                other = it.next();
                builder.append(" ,");
                builder.append(other.getName());
            }
            builder.append(" and ");
        }
        builder.append(last.getName());
    }

    /**
     * Returns a list of users in this team excluding the given
     * user.
     *
     * @param user the user to exclude
     * @return the list of users
     */
    private List<GameUser> getUsersExcluding(GameUser user)
    {
        if (users.isEmpty()) {
            return Collections.emptyList();
        }

        ArrayList<GameUser> otherUsers = new ArrayList<>(users.size());

        for (ManagedReference<GameUser> userRef : users) {
            GameUser other = userRef.get();
            if (!user.equals(other)) {
                otherUsers.add(other);
            }
        }

        return Collections.unmodifiableList(otherUsers);
    }
}
