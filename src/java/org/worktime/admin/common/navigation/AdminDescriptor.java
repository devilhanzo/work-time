package org.worktime.admin.common.navigation;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class AdminDescriptor extends BaseDescriptor {

    private static final long serialVersionUID = 6822187362271025752L;

    private static final String BASE_MANAGES_DIR = "/wt_adm/";

    private List<ManageDescriptor> manages;

    private boolean containsNewManages() {
        for (ManageDescriptor manage : manages) {
            if (manage.isNewItem()){
                return true;
            }
        }
        return false;
    }

    public boolean isNewItems() {
        return (isNewItem() || containsNewManages());
    }

    public ManageDescriptor getManageById(String id) {
        for (ManageDescriptor manage : getManages()) {
            if (manage.getId().equals(id)) {
                return manage;
            }
        }
        return manages.get(0);
    }

    @XmlElementWrapper(name = "manages")
    @XmlElement(name = "manage")
    public List<ManageDescriptor> getManages() {
        return manages;
    }

    public void setManages(List<ManageDescriptor> manages) {
        this.manages = manages;
    }

}
