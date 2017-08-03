package org.transformer.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 检查性异常
 * <b>Description</b>:Java语言规范将派生于Error类或RuntimeException类的所有异常都称为非检查异常.
 * 除非检查异常外的所有异常都称为检查异常.检查异常对方法调用者来说属于必须处理的异常.如果一个异常是
 * 致命的且不可恢复并且对于捕捉异常的方法不知如何处理时,或者捕获此类异常无任何益处,应该定义这类异常
 * 为非检查异常,由顶层专门的异常处理程序处理:像数据库连接错误,网络连接错误或者文件打不开等之类的异常
 * 一般属于非检查异常.这类异常一般与外部环境相关,一旦出现,基本无法有效处理.而对于一些具备可以回避异常
 * 或预料内的可以恢复并存在相应的处理方法的异常,可以定义该异常为检查异常.像一般由输入不合法数据引起的
 * 异常或者与业务相关的一些异常,基本上属于检查异常.当出现这类异常,一般可以经过有效处理或通过重试可以恢
 * 复正常状态.<br/>
 */
public class SystemException extends Exception {

  private static final long serialVersionUID = -1319644200157161399L;

  /** A wrapped Throwable. */
  protected Throwable cause;

  public SystemException() {
    super("Error occurred in application.");
  }

  public SystemException(String message) {
    super(message);
  }

  public SystemException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }

  @Override
  public synchronized Throwable initCause(Throwable cause) {
    this.cause = cause;
    return cause;
  }

  @Override
  public String getMessage() {
    // Get this exception's message.
    String msg = super.getMessage();
    Throwable parent = this;
    Throwable child;
    // Look for nested exceptions.
    while ((child = getNestedException(parent)) != null) {
      // Get the child's message.
      String msg2 = child.getMessage();
      // If we found a message for the child exception, 
      // we append it.
      if (msg2 != null) {
        if (msg != null) {
          msg += ": " + msg2;
        } else {
          msg = msg2;
        }
      }
      // Any nested ApplicationException will append its own
      // children, so we need to break out of here.
      if (child instanceof SystemException) {
        break;
      }
      parent = child;
    }
    // Return the completed message.
    return msg;
  }

  @Override
  public void printStackTrace() {
    // Print the stack trace for this exception.
    super.printStackTrace();
    Throwable parent = this;
    Throwable child;
    // Print the stack trace for each nested exception.
    while ((child = getNestedException(parent)) != null) {
      if (child != null) {
        System.err.print("Caused by: ");
        child.printStackTrace();
        if (child instanceof SystemException) {
          break;
        }
        parent = child;
      }
    }
  }

  @Override
  public void printStackTrace(PrintStream ps) {
    // Print the stack trace for this exception.
    super.printStackTrace(ps);
    Throwable parent = this;
    Throwable child;
    // Print the stack trace for each nested exception.
    while ((child = getNestedException(parent)) != null) {
      if (child != null) {
        ps.print("Caused by: ");
        child.printStackTrace(ps);
        if (child instanceof SystemException) {
          break;
        }
        parent = child;
      }
    }
  }

  @Override
  public void printStackTrace(PrintWriter pw) {
    // Print the stack trace for this exception.
    super.printStackTrace(pw);
    Throwable parent = this;
    Throwable child;
    // Print the stack trace for each nested exception.
    while ((child = getNestedException(parent)) != null) {
      if (child != null) {
        pw.print("Caused by: ");
        child.printStackTrace(pw);
        if (child instanceof SystemException) {
          break;
        }
        parent = child;
      }
    }
  }

  @Override
  public synchronized Throwable getCause() {
    return cause;
  }

  private Throwable getNestedException(Throwable throwable) {
    return throwable.getCause();
  }
}
