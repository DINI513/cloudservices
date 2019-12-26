import com.fasterxml.jackson.annotation.*;

import java.util.List;

public class Body {
    private List<Label> objectLabels;

    @JsonProperty("object_labels")
    public List<Label> getObjectLabels() { return objectLabels; }
    @JsonProperty("object_labels")
    public void setObjectLabels(List<Label> value) { this.objectLabels = value; }
}
