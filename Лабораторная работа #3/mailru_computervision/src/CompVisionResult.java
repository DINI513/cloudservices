import com.fasterxml.jackson.annotation.*;

public class CompVisionResult {
    private Body body;

    @JsonProperty("body")
    public Body getBody() { return body; }
    @JsonProperty("body")
    public void setBody(Body value) { this.body = value; }
}
