package org.transformer.support.warpper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

/**
* This class is used for wrapped response for getting cached data.
* 
*/
public class HttpServletResponseWrapper extends javax.servlet.http.HttpServletResponseWrapper {

  /**
  * Indicate that getOutputStream() or getWriter() is not called yet.
  */
  public static final int OUTPUT_NONE = 0;

  /**
  * Indicate that getWriter() is already called.
  */
  public static final int OUTPUT_WRITER = 1;

  /**
  * Indicate that getOutputStream() is already called.
  */
  public static final int OUTPUT_STREAM = 2;

  private int outputType = OUTPUT_NONE;

  private int status = SC_OK;
  private ServletOutputStream output = null;
  private PrintWriter writer = null;
  private ByteArrayOutputStream buffer = null;

  public HttpServletResponseWrapper(HttpServletResponse resp) {
    super(resp);
    buffer = new ByteArrayOutputStream();
  }

  @Override
  public int getStatus() {
    return status;
  }

  @Override
  public void setStatus(int status) {
    super.setStatus(status);
    this.status = status;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void setStatus(int status, String string) {
    super.setStatus(status, string);
    this.status = status;
  }

  @Override
  public void sendError(int status, String string) throws IOException {
    super.sendError(status, string);
    this.status = status;
  }

  @Override
  public void sendError(int status) throws IOException {
    super.sendError(status);
    this.status = status;
  }

  @Override
  public void sendRedirect(String location) throws IOException {
    super.sendRedirect(location);
    this.status = SC_MOVED_TEMPORARILY;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if (outputType == OUTPUT_STREAM) {
      throw new IllegalStateException();
    } else if (outputType == OUTPUT_WRITER) {
      return writer;
    } else {
      outputType = OUTPUT_WRITER;
      writer = new PrintWriter(new OutputStreamWriter(buffer, getCharacterEncoding()));
      return writer;
    }
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (outputType == OUTPUT_WRITER) {
      throw new IllegalStateException();
    } else if (outputType == OUTPUT_STREAM) {
      return output;
    } else {
      outputType = OUTPUT_STREAM;
      output = new WrappedOutputStream(buffer);
      return output;
    }
  }

  @Override
  public void flushBuffer() throws IOException {
    if (outputType == OUTPUT_WRITER) {
      writer.flush();
    }
    if (outputType == OUTPUT_STREAM) {
      output.flush();
    }
  }

  @Override
  public void reset() {
    outputType = OUTPUT_NONE;
    buffer.reset();
  }

  /**
  * Call this method to get cached response data.
  * @return byte array buffer.
  * @throws IOException io异常
  */
  public byte[] getResponseData() throws IOException {
    flushBuffer();
    return buffer.toByteArray();
  }

  /**
  * This class is used to wrap a ServletOutputStream and 
  * store output stream in byte[] buffer.
  */
  class WrappedOutputStream extends ServletOutputStream {

    @SuppressWarnings("hiding")
    private ByteArrayOutputStream buffer;

    public WrappedOutputStream(ByteArrayOutputStream buffer) {
      this.buffer = buffer;
    }

    @Override
    public void write(int bf) throws IOException {
      buffer.write(bf);
    }

    public byte[] toByteArray() {
      return buffer.toByteArray();
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }
  }

}