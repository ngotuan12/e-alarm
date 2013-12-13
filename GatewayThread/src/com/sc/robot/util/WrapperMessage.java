package com.sc.robot.util;

import java.io.InputStream;
import java.io.OutputStream;


import com.fis.queue.message.MessageQueue;
import com.sc.robot.server.Transaction;

/**
 * @author DEPQ 2011
 * @version 1.0
 */
public class WrapperMessage
    extends MessageQueue {
  private long startTime = System.currentTimeMillis();
  private String command;
  private com.sc.robot.server.TelnetServer telnet;
  private Transaction transaction;
  private boolean isProcessed = false;
  private String response;
  private String response_code;
  public WrapperMessage() {

  }

  public WrapperMessage(String command, com.sc.robot.server.TelnetServer telnet,
                        Transaction trans, long start) {
    this.command = command;
    this.telnet = telnet;
    this.transaction = trans;
    this.startTime = start;
  }

  public boolean isProcessed() {
    return isProcessed;
  }

  public Transaction getTransaction() {
    return transaction;
  }

  public String getCommand() {
    return command;
  }

  public com.sc.robot.server.TelnetServer getTelnetServer() {
    return telnet;
  }

  public long getStartTime() {
    return this.startTime;
  }

  public String getResponse() {
    return response;
  }

  public String getResponseCode() {
    return response_code;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public void setTelnetServer(com.sc.robot.server.TelnetServer telnet) {
    this.telnet = telnet;
  }

  public void setStartTime(long start) {
    this.startTime = start;
  }

  public void setTransaction(Transaction trans) {
    this.transaction = trans;
  }

  public void setResponse(String res) {
    this.response = res;
  }

  public void setResponseCode(String res_code) {
   this.response_code = res_code;
 }


  public void setProcessed(boolean processed) {
    this.isProcessed = processed;
  }

  public void load(InputStream inputStream) throws Exception {
  }

  public void store(OutputStream outputStream) throws Exception {
  }
}
