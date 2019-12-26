import com.fasterxml.jackson.annotation.*;

import java.util.List;

public class Label {
    private List<LabelElement> labels;

    @JsonProperty("labels")
    public List<LabelElement> getLabels() { return labels; }
    @JsonProperty("labels")
    public void setLabels(List<LabelElement> value) { this.labels = value; }
}
