import java.util.ArrayList;
import java.util.List;

public class MyBucket {
    public String bucketName;
    public List<MyObject> objects = new ArrayList<>();

    public String getBucketName() {
        return bucketName;
    }
    public List<MyObject> getObjects() {
        return objects;
    }
}
