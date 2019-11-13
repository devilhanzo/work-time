package org.worktime.admin.common.navigation;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class GroupDescriptor extends BaseDescriptor {

    private static final long serialVersionUID = -3481702232804120885L;

    private List<AdminDescriptor> admins;

    private boolean containsNewAdmins() {
        for (AdminDescriptor demo : admins) {
            if (demo.isNewItems()) {
                return true;
            }
        }
        return false;
    }

    public boolean isNewItems() {
        return isNewItem() || containsNewAdmins();
    }

    @XmlElementWrapper(name = "admins")
    @XmlElement(name = "admin")
    public List<AdminDescriptor> getAdmins() {
        return admins;
    }

    public void setAdmins(List<AdminDescriptor> admins) {
        this.admins = admins;
    }

}
