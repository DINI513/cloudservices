import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@MultipartConfig
public class IndexPage extends HttpServlet {
    private final String accessKeyId = "hgYJQY3qvBcMh4Vwht7WPH";
    private final String secretAccessKey = "6Rt9PeUsDc1DNgBmDekSbDVafRmyfX31DbFJX4vBoSr6";
    private final String region = "us-east-1";
    private final String endpoint = "hb.bizmrg.com";
    private static AmazonS3 mailS3 = null;

    public void init(ServletConfig servletConfig) {
        if (mailS3 == null) {
            AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
            mailS3 = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withEndpointConfiguration(
                            new AmazonS3ClientBuilder.EndpointConfiguration(
                                    endpoint, region
                            )
                    )
                    .build();
        }

        try {
            super.init(servletConfig);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MyBucket> buckets = getBuckets();
        request.setAttribute("buckets", buckets);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part part = request.getPart("file");
        Path filePath = Paths.get(part.getSubmittedFileName());
        String fileName = filePath.getFileName().toString();
        InputStream inputStream = part.getInputStream();
        SaveToBucket(request.getParameter("loadingBucket"), fileName, inputStream);
        inputStream.close();
        doGet(request, response);
    }

    private List<MyBucket> getBuckets() {
        List<MyBucket> buckets = new ArrayList<>();
        List<Bucket> bucketList = mailS3.listBuckets();
        for (Bucket bucket : bucketList) {
            ListObjectsV2Result listObjectsV2Result = mailS3.listObjectsV2(bucket.getName());
            List<S3ObjectSummary> s3ObjectsSummary = listObjectsV2Result.getObjectSummaries();
            MyBucket myBucket = new MyBucket();
            myBucket.bucketName = bucket.getName();
            buckets.add(myBucket);
            for (S3ObjectSummary s3Object : s3ObjectsSummary) {
                MyObject myObject = new MyObject();
                myObject.objectName = s3Object.getKey();
                myObject.url = mailS3.getUrl(bucket.getName(), s3Object.getKey()).toString();
                myBucket.objects.add(myObject);
            }
        }

        return buckets;
    }

    public void SaveToBucket(String bucketName, String objectName, InputStream inputStream) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(inputStream.available());
            PutObjectRequest s3Object = new PutObjectRequest(bucketName, objectName, inputStream, metadata);
            s3Object.withCannedAcl(CannedAccessControlList.PublicReadWrite);
            mailS3.putObject(s3Object);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
