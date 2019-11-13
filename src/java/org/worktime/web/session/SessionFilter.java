/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.worktime.web.session;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * When the session destroyed, MySessionListener will do necessary logout
 * operations. Later, at the first request of client, this filter will be fired
 * and redirect the user to the appropriate timeout page if the session is not
 * valid.
 *
 *
 */
public class SessionFilter implements Filter {
// This should be your default Home or Login page  
// "login.seam" if you use Jboss Seam otherwise "login.jsf"   
// "login.xhtml" or whatever  

    private Logger logger = Logger.getLogger(getClass().getName());
    private String unauthorizedUrl;
    private String loginUrl;
    private String suspendedUrl;
    private String timeoutUrl;
    final String timeoutPage = "login.jsf";
    final String suspendedPage = "suspended.jsf";
    final String unauthorizedPage = "unauthorized.jsf";
    final String departmentQuery = "department_";
    final String orgQuery = "org_";
    final String adminQuery = "manage_";
    final String manualQuery = "manual";
    final String bugReportQuery = "bug_report";
    final String requireQuery = "require";
    final String holidayQuery = "holiday";
    final String userAttribute = "uName";
    final String privSuspendedAttribute = "priv_suspended";
    final String admPrivAttribute = "adm_priv";
    final String orgPrivAttribute = "org_priv";
    final String deptPrivAttribute = "dept_priv";
    final String suspendedMessage = "Suspended user! redirecting to suspended page : ";
    final String notLoginMessage = "User does not login! redirecting to login page : ";
    final String invalidMessage = "Session is invalid! redirecting to timeout page : ";
    final String yes = "Y";
    final String no = "N";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request,
            ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if ((request instanceof HttpServletRequest)
                && (response instanceof HttpServletResponse)) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            HttpSession session = httpServletRequest.getSession(true);
            String pageRequested = httpServletRequest.getRequestURI();
            String pageQuery = httpServletRequest.getQueryString();
// is session expire control required for this request?
            if (isSessionControlRequiredForThisResource(httpServletRequest)) {
// is session invalid?
                if (isSessionInvalid(httpServletRequest)) {
                    timeoutUrl = httpServletRequest.getContextPath()
                            + "/" + timeoutPage;
                    logger.info(invalidMessage + timeoutUrl);
                    httpServletResponse.sendRedirect(timeoutUrl);
                    return;
                }
            }
// is user session created?            
            if (session.getAttribute(userAttribute) == null && !pageRequested.contains(timeoutPage)
                    && !pageQuery.contains(manualQuery) && !pageQuery.contains(bugReportQuery)
                    && !pageQuery.contains(requireQuery) && !pageQuery.contains(holidayQuery)) {
                loginUrl = httpServletRequest.getContextPath()
                        + "/" + timeoutPage;
                logger.info(notLoginMessage + loginUrl);
                httpServletResponse.sendRedirect(loginUrl);
                UserLogin.setReqURL(getReqURL(httpServletRequest));
                return;
            }
            // Deny suspended user on all module, except own report module.
            if (pageQuery != null) {
                if (session.getAttribute(userAttribute) != null && (pageQuery.contains(departmentQuery)
                        || pageQuery.contains(orgQuery) || pageQuery.contains(adminQuery)) && yes.equals(session.getAttribute(privSuspendedAttribute))) {
                    suspendedUrl = httpServletRequest.getContextPath()
                            + "/" + suspendedPage;
                    logger.info(suspendedMessage + suspendedUrl);
                    System.out.println(suspendedUrl);
                    httpServletResponse.sendRedirect(suspendedUrl);
                    UserLogin.setReqURL(getReqURL(httpServletRequest));
                    return;
                }
            }
            // Department Report privilege check. *If not redirect to Unauthorized page.
            if (pageQuery != null) {
                if (session.getAttribute(userAttribute) != null && pageQuery.contains(departmentQuery)
                        && no.equals(session.getAttribute(privSuspendedAttribute))
                        && no.equals(session.getAttribute(deptPrivAttribute))) {
                    unauthorizedUrl = httpServletRequest.getContextPath()
                            + "/" + unauthorizedPage;
                    logger.info(suspendedMessage + unauthorizedUrl);
                    System.out.println(unauthorizedUrl);
                    httpServletResponse.sendRedirect(unauthorizedUrl);
                    UserLogin.setReqURL(getReqURL(httpServletRequest));
                    return;
                }
            }
            // Organize Report privilege check. *If not redirect to Unauthorized page.
            if (pageQuery != null) {
                if (session.getAttribute(userAttribute) != null && pageQuery.contains(orgQuery)
                        && no.equals(session.getAttribute(privSuspendedAttribute))
                        && no.equals(session.getAttribute(orgPrivAttribute))) {
                    unauthorizedUrl = httpServletRequest.getContextPath()
                            + "/" + unauthorizedPage;
                    logger.info(suspendedMessage + unauthorizedUrl);
                    System.out.println(unauthorizedUrl);
                    httpServletResponse.sendRedirect(unauthorizedUrl);
                    UserLogin.setReqURL(getReqURL(httpServletRequest));
                    return;
                }
            }
            // Admin privilege check. *If not redirect to Unauthorized page.
            if (pageQuery != null) {
                if (session.getAttribute(userAttribute) != null && pageQuery.contains(adminQuery)
                        && no.equals(session.getAttribute(privSuspendedAttribute))
                        && no.equals(session.getAttribute(admPrivAttribute))) {
                    unauthorizedUrl = httpServletRequest.getContextPath()
                            + "/" + unauthorizedPage;
                    logger.info(suspendedMessage + unauthorizedUrl);
                    System.out.println(unauthorizedUrl);
                    httpServletResponse.sendRedirect(unauthorizedUrl);
                    UserLogin.setReqURL(getReqURL(httpServletRequest));
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    /*
     * session shouldn't be checked for some pages. For example: for timeout
     * page.. Since we're redirecting to timeout page from this filter, if we
     * don't disable session control for it, filter will again redirect to it
     * and this will be result with an infinite loop...
     */
    private boolean isSessionControlRequiredForThisResource(HttpServletRequest httpServletRequest) {
        String requestPath = httpServletRequest.getRequestURI();
        boolean controlRequired = !StringUtils.contains(requestPath, timeoutPage);
        return controlRequired;
    }

    private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
        boolean sessionInValid = (httpServletRequest.getRequestedSessionId() != null)
                && !httpServletRequest.isRequestedSessionIdValid();
        return sessionInValid;
    }

    private String getReqURL(HttpServletRequest req) {
        String reqUrl = req.getRequestURI().toString();
        String queryString = req.getQueryString();
        if (queryString != null) {
            reqUrl += "?" + queryString;
        }
        return reqUrl;
    }

    @Override
    public void destroy() {
    }
}
