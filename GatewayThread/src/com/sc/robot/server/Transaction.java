package com.sc.robot.server;

/**
 * @author DEPQ
 * @version 1.0
 */
public class Transaction {
	private long transid;

	public Transaction() {
		transid = Manager.getTransactionSeq();
	}

	public long getTransID() {
		return transid;
	}
}
