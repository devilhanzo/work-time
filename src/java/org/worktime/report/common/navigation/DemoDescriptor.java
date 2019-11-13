package org.worktime.report.common.navigation;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class DemoDescriptor extends BaseDescriptor {

    private static final long serialVersionUID = 6822187362271025752L;

    private static final String BASE_SAMPLES_DIR = "/report/";

    private List<SampleDescriptor> samples;

    private boolean containsNewSamples() {
        for (SampleDescriptor sample : samples) {
            if (sample.isNewItem()){
                return true;
            }
        }
        return false;
    }

    public boolean isNewItems() {
        return (isNewItem() || containsNewSamples());
    }

    public SampleDescriptor getSampleById(String id) {
        for (SampleDescriptor sample : getSamples()) {
            if (sample.getId().equals(id)) {
                return sample;
            }
        }
        return samples.get(0);
    }

    @XmlElementWrapper(name = "docs")
    @XmlElement(name = "doc")
    public List<SampleDescriptor> getSamples() {
        return samples;
    }

    public void setSamples(List<SampleDescriptor> samples) {
        this.samples = samples;
    }

}
