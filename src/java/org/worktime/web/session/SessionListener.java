package org.worktime.web.session;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.log4j.Logger;

/** 
 * When the user session timedout,  
 * ({@link #sessionDestroyed(HttpSessionEvent)})method will be invoked. 
 * This method will make necessary cleanups (logging out user,  
 * updating db and audit logs, etc...) 
 * As a result; after this method, we will be in a clear 
 * and stable state. So nothing left to think about 
 * because session expired, user can do nothing after this point. 
 * 
 * Thanks to hturksoy 
 **/
public class SessionListener implements HttpSessionListener {
    private Logger logger = Logger.getLogger(getClass().getName());
    public SessionListener() {
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        logger.info("Created Session : " + event.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {

    // get the destroying session...  

        HttpSession session = event.getSession();

        logger.info("Destroyed Session :"  + session.getId() );

        /*         
         * nobody can reach user data after this point because  
         * session is invalidated already. 
         * So, get the user data from session and save its  
         * logout information before losing it. 
         * User's redirection to the timeout page will be  
         * handled by the SessionTimeoutFilter. 
         */

    // Only if needed  

        try {
            prepareLogoutInfoAndLogoutActiveUser(session);
        } catch (Exception e) {
            logger.info("Error while logging out at session destroyed : "
                    + e.getMessage());
        }
    }

    /** 
     * Clean your logout operations. 
     */
    public void prepareLogoutInfoAndLogoutActiveUser(HttpSession httpSession) {
// Only if needed  
    }
}
