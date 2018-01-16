package pk.temp.bm.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Created by Abubakar on 7/19/2017.
 */
public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {

    private String _body;
    public MultiReadHttpServletRequest(HttpServletRequest request) {
        super(request);
        _body = "";
        try {
            BufferedReader bufferedReader = request.getReader();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                _body += line;
            }
        }
        catch (Exception e){
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(_body.getBytes());
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
