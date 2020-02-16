package fr.jg.springrest.data.services;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BodyResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream capture;

    private ServletOutputStream output;

    public BodyResponseWrapper(final HttpServletResponse response) {
        super(response);
        this.capture = new ByteArrayOutputStream(response.getBufferSize());
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (this.output == null) {
            this.output = new ServletOutputStream() {
                @Override
                public void write(final int b) {
                    BodyResponseWrapper.this.capture.write(b);
                }

                @Override
                public void flush() throws IOException {
                    BodyResponseWrapper.this.capture.flush();
                }

                @Override
                public void close() throws IOException {
                    BodyResponseWrapper.this.capture.close();
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(final WriteListener arg0) {
                }
            };
        }

        return this.output;
    }

    public byte[] getCaptureAsBytes() throws IOException {
        if (this.output != null) {
            this.output.close();
        }

        return this.capture.toByteArray();
    }

    public String getCaptureAsString() throws IOException {
        return new String(this.getCaptureAsBytes());
    }
}