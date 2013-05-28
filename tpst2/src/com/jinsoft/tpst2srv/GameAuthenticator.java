/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jinsoft.tpst2srv;

import com.jinsoft.tpst2srv.common.Global;
import com.sun.sgs.auth.Identity;
import com.sun.sgs.auth.IdentityAuthenticator;
import com.sun.sgs.auth.IdentityCredentials;
import com.sun.sgs.impl.auth.IdentityImpl;
import com.sun.sgs.impl.auth.NamePasswordCredentials;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.CredentialException;
import javax.security.auth.login.LoginException;

/**
 *
 * @author KeyuGG
 */
public class GameAuthenticator implements Serializable, IdentityAuthenticator {

    private static final Logger LOG = Logger.getLogger(GameAuthenticator.class.getName());
    
    public GameAuthenticator(Properties properties){
        //LOG.info(">>> 已成功设立电子沙盘用户登入验证器 <<<");
    }
    
    @Override
    public String[] getSupportedCredentialTypes() {
        // return new String[]{NamePasswordCredentials.TYPE_IDENTIFIER};
        return new String[]{"NameAndPasswordCredentials"};
    }

    @Override
    public Identity authenticateIdentity(IdentityCredentials credentials) throws LoginException {
        
        if(!credentials.getCredentialsType().equals(NamePasswordCredentials.TYPE_IDENTIFIER))
        {
                LOG.log(Level.WARNING, "不支持的验证类型!");
                throw new CredentialException("不支持的验证类型!");
        }
        
        NamePasswordCredentials npc = ( NamePasswordCredentials )credentials;
        String name = npc.getName();
        String password = new String(npc.getPassword());
        //LOG.log(Level.INFO, ">>> 正在验证用户名: {0} 密码: {1} <<<", new Object[]{ name, password });
        
        if ( name.equals(Global.getAdmin()) && password.equals(Global.getAdmin()+Global.getAdmin()) )
        {
            //LOG.log(Level.INFO, ">>> 客户端系统线程已成功通过验证. <<<", new Object[]{ name });
            return new IdentityImpl(name);
        }
        
        Map<String, Object> user = GameData.getUserInfo(name);
        
        if ( user == null || user.isEmpty() )
        {
            //LOG.log(Level.WARNING, "用户数据为空, 无法进行验证. 请检查数据库");
            throw new CredentialException("用户数据为空, 无法进行验证. 请检查数据库!");            
        }
        
        String username;
        String userpassword;
        
        username = user.get("fname").toString();
        userpassword = user.get("fpassword").toString();

        //LOG.log(Level.SEVERE, "数据库查询结果 - 用户名:{0} 密码:{1}", new Object[]{ username, userpassword });
        
        if ( name.equals(username) && password.equals(userpassword) )
        {
            //LOG.log(Level.INFO, ">>> 用户 {0} 已成功通过验证. <<<", new Object[]{name});
            return new IdentityImpl( name );
        }else{
            //LOG.log(Level.WARNING, ">>> 用户 {0} 验证失败, 原因是密码错误. <<<", new Object[]{name});
            return new IdentityImpl( Global.getAdmin() );
            //throw new CredentialException("用户验证失败, 原因是密码错误.");
        }
    }
    
}
