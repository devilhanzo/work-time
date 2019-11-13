/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.worktime.web.session;

import java.io.IOException;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author PucK
 */
@ManagedBean
@SessionScoped
public class SessionLogoutControl implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private Logger logger = Logger.getLogger(getClass().getName());
    final String homeUrl = "/work-time/";
    final String logMessage = "Log out by user : redirect to welcome page";
    
    public void logout() throws IOException {        
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
        session.invalidate();
        FacesContext.getCurrentInstance().getExternalContext().redirect(homeUrl);
        logger.info(logMessage);
    }
}
