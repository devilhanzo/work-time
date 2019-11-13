package org.worktime.admin.common.navigation;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class AdminNavigator implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 3970933260901989658L;
    private static final String ADMIN_VIEW_PARAMETER = "type";
    private static final String MANAGE_VIEW_PARAMETER = "kind";
    private static final String SEPARATOR = "/";
    private static final String MANAGE_PREFIX = "-manage";
    private static final String MANAGES_FOLDER = "manages/";

    @ManagedProperty(value = "#{adminNavigationParser.groupsList}")
    private List<GroupDescriptor> groups;
    private AdminDescriptor currentAdmin;
    private ManageDescriptor currentManage;
    private String manage;
    private String admin;

    @PostConstruct
    public void init() {
        currentAdmin = null;
        currentManage = null;
    }

    public AdminDescriptor getCurrentAdmin() {
        String id = getViewParameter(ADMIN_VIEW_PARAMETER);
        if (currentAdmin == null || !currentAdmin.getId().equals(id)) {
            if (id != null) {
                currentAdmin = findAdminById(id);
                currentManage = null;
            }
            if (currentAdmin == null) {
                currentAdmin = groups.get(0).getAdmins().get(0);
                currentManage = null;
            }
        }
        return currentAdmin;
    }

    public ManageDescriptor getCurrentManage() {
        String id = getViewParameter(MANAGE_VIEW_PARAMETER);
        if (currentManage == null || !currentManage.getId().equals(id)) {
            if (id != null) {
                currentManage = getCurrentAdmin().getManageById(id);
            }
            if (currentManage == null) {
                currentManage = getCurrentAdmin().getManages().get(0);
            }
        }
        return currentManage;
    }

    private String getViewParameter(String name) {
        FacesContext fc = FacesContext.getCurrentInstance();
        String param = (String) fc.getExternalContext().getRequestParameterMap().get(name);
        if (param != null && param.trim().length() > 0) {
            return param;
        } else {
            return null;
        }
    }

    public AdminDescriptor findAdminById(String id) {
        Iterator<GroupDescriptor> it = groups.iterator();
        while (it.hasNext()) {
            GroupDescriptor group = it.next();
            Iterator<AdminDescriptor> dit = group.getAdmins().iterator();
            while (dit.hasNext()) {
                AdminDescriptor locDemo = (AdminDescriptor) dit.next();
                if (locDemo.getId().equals(id)) {
                    return locDemo;
                }
            }
        }
        return null;
    }

    public String getManageURI() {
        FacesContext context = FacesContext.getCurrentInstance();

        NavigationHandler handler = context.getApplication().getNavigationHandler();

        if (handler instanceof ConfigurableNavigationHandler) {
            ConfigurableNavigationHandler navigationHandler = (ConfigurableNavigationHandler) handler;

            NavigationCase navCase = navigationHandler.getNavigationCase(context, null, getCurrentAdmin().getId()
                + SEPARATOR + getCurrentManage().getId());

            return navCase.getToViewId(context);
        }

        return null;
    }

   
    public String getManageIncludeURI() {
        String manageURI = getManageURI();
        StringBuffer manageURIBuffer = new StringBuffer(manageURI);
        int folderOffset = manageURIBuffer.lastIndexOf(currentManage.getId());
        int fileNameOffset = manageURIBuffer.lastIndexOf(currentManage.getId()) + currentManage.getId().length()
            + MANAGE_PREFIX.length() + 1;
        String result = new StringBuffer(manageURI).insert(folderOffset, MANAGES_FOLDER).insert(fileNameOffset,
            MANAGE_PREFIX).toString();
        return result;
    }

    public List<GroupDescriptor> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupDescriptor> groups) {
        this.groups = groups;
    }

    public String getManage() {
        return manage;
    }

    public void setManage(String manage) {
        this.manage = manage;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
