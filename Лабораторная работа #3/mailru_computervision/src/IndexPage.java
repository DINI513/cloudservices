import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;

@MultipartConfig
public class IndexPage extends HttpServlet {
    private Random rand = new Random();
    public void init(ServletConfig servletConfig) {
        try {
            super.init(servletConfig);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part part = request.getPart("file");
        InputStream inputStream = part.getInputStream();
        response.setContentLength(inputStream.available());
        response.setContentType("image/jpg");
        OutputStream outStream = response.getOutputStream();
        Recognize(inputStream, outStream);
        inputStream.close();
        outStream.flush();
        doGet(request, response);
    }

    private void Recognize(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] arrImage = inputStream.readAllBytes();
        inputStream = new ByteArrayInputStream(arrImage);
        String request = GetFromMailVision(inputStream);
        CompVisionResult compVisionResult = JsonConverter.fromJsonString(request);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(arrImage));
        Graphics graphics = image.getGraphics();
        ((Graphics2D) graphics).setStroke(new BasicStroke(2));
        List<LabelElement> labelElements = compVisionResult.getBody().getObjectLabels().get(0).getLabels();
        CheckRequest(labelElements);
        for(int i = 0; i < labelElements.size(); i++){
            LabelElement labelElement = labelElements.get(i);
            PaintObjectToImage(graphics, labelElement);
        }

        ImageIO.write(image, "jpg", outputStream);
    }

    private String GetFromMailVision(InputStream inputStream) throws IOException {
        String charset = "Windows-1251";
        String CRLF = "\r\n";
        String params = "{\"mode\":[\"object\"],\"images\":[{\"name\":\"img\"}]}";
        URL url = new URL("https://smarty.mail.ru/api/v1/objects/detect?oauth_provider=mcs&oauth_token=26zaz7USAw9RnS73ZpvAbh43Yb23s8FcZanvUuEM6K5uvSRnU6");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        String boundary = Long.toHexString(System.currentTimeMillis());
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        OutputStream output = conn.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);

        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"meta\"").append(CRLF);
        writer.append(CRLF).append(params).append(CRLF).flush();

        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + "img.jpg" + "\"").append(CRLF);
        writer.append("Content-Type: Content-Type: image/*").append(CRLF);
        writer.append(CRLF).flush();
        inputStream.transferTo(output);
        output.flush();
        writer.append(CRLF).flush();

        writer.append("--" + boundary + "--").append(CRLF).flush();

        BufferedReader rd  = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        return rd.readLine();
    }

    private void PaintObjectToImage(Graphics graphics, LabelElement labelElement) throws UnsupportedEncodingException {
        graphics.setColor(RandomColor().brighter());
        graphics.drawRect(
                labelElement.getCoord().get(0).intValue(),
                labelElement.getCoord().get(1).intValue(),
                labelElement.getCoord().get(2).intValue() - labelElement.getCoord().get(0).intValue(),
                labelElement.getCoord().get(3).intValue() - labelElement.getCoord().get(1).intValue());

        graphics.setFont(new Font("TimesRoman", Font.PLAIN, 18));
        String label = new String(labelElement.getRus().getBytes(Charset.forName("Windows-1251")), "UTF-8") + " " + labelElement.getProb();
        int width = graphics.getFontMetrics().stringWidth(label);
        int height = graphics.getFontMetrics().getHeight();
        Rectangle2D rect = (new Rectangle2D.Double(
                labelElement.getCoord().get(0).intValue(),
                labelElement.getCoord().get(1).intValue(),
                width,
                height));
        ((Graphics2D) graphics).fill(rect);
        graphics.setColor(Color.black);
        graphics.drawString(
                label,
                labelElement.getCoord().get(0).intValue() + 2,
                labelElement.getCoord().get(1).intValue() + 18);
    }

    private void CheckRequest(List<LabelElement> labelElements){
        labelElements.sort((a, b) -> a.getProb() > b.getProb() ? -1 : a.getProb() == b.getProb() ? 0 : 1);
        int i = 0;
        while (i < labelElements.size() - 1){
            int j = i + 1;
            while (j < labelElements.size()){
                if (labelElements.get(i).getCoord().equals(labelElements.get(j).getCoord())){
                    labelElements.remove(j);
                }
                else{
                    j++;
                }
            }

            i++;
        }
    }

    private Color RandomColor(){
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        Color randomColor = new Color(r, g, b);
        return randomColor;
    }
}

