

        import java.io.*;
        import java.net.URL;
        import java.net.URLConnection;
        import java.nio.charset.Charset;

public class Main {

    public static void main(String[] args) {
        String filePath = "C:/Users/APARNA PAMIDI/Downloads/RestCall/Data/c_features.txt";
        try
        {
            LineNumberReader lineReader = new LineNumberReader(new FileReader(filePath));
            String lineText = null;

            while ((lineText = lineReader.readLine()) != null)
            {
                System.out.println(lineText + callURL("http://172.16.2.241:7180/group/?topic=realtime&msg"+lineText));
                System.out.println(lineReader.getLineNumber() + ": " + lineText);
            }

            lineReader.close();
        }
        catch (IOException ex)
        {
            System.err.println(ex);
        }

    }

    public static String callURL(String myURL) {
        System.out.println("Requested URL: " + myURL);
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            URL url = new URL(myURL);
            urlConn = url.openConnection();
            if (urlConn != null)
                urlConn.setReadTimeout(60 * 1000);
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);
                if (bufferedReader != null) {
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
            }
            in.close();
        } catch (Exception e) {
            throw new RuntimeException("Exception while calling URL:" + myURL, e);
        }

        return sb.toString();
    }
}


