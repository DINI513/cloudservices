import com.fasterxml.jackson.annotation.*;

import java.util.List;

public class LabelElement {
    private String rus;
    private List<String> rusCategories;
    private double prob;
    private List<Long> coord;

    @JsonProperty("rus")
    public String getRus() { return rus; }
    @JsonProperty("rus")
    public void setRus(String value) { this.rus = value; }

    @JsonProperty("rus_categories")
    public List<String> getRusCategories() { return rusCategories; }
    @JsonProperty("rus_categories")
    public void setRusCategories(List<String> value) { this.rusCategories = value; }

    @JsonProperty("prob")
    public double getProb() { return prob; }
    @JsonProperty("prob")
    public void setProb(double value) { this.prob = value; }

    @JsonProperty("coord")
    public List<Long> getCoord() { return coord; }
    @JsonProperty("coord")
    public void setCoord(List<Long> value) { this.coord = value; }
}
