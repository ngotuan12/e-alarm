package com.sc.robot.thread;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.fss.sql.Database;
import com.fss.thread.ThreadConstant;
import com.fss.util.StringUtil;
import com.sc.robot.util.AppServer;

public class ThreadSynchronizeMySQL extends com.fss.thread.ManageableThread
{
	private Connection cnOracle = null;
	private Connection cnMySQL = null;
	String oldPrice, newPrice, discount;

	public ThreadSynchronizeMySQL()
	{

	}

	protected void beforeSession() throws Exception
	{
		// connection
		logInfo("create connection..");
		cnOracle = AppServer.getConnection("Oracle");
		cnMySQL = AppServer.getConnection("MySQL");
		logInfo("create connection sucessful.");
	}

	@Override
	protected void processSession() throws Exception
	{
		while (miThreadCommand != ThreadConstant.THREAD_STOP)
		{
			logInfo("Start sync..");
			SyncOracleandMySQl();
			logInfo("End sync..");
			Thread.sleep(6000);
		}
	}

	protected void afterSession() throws Exception
	{
		Database.closeObject(cnOracle);
		Database.closeObject(cnMySQL);
	}

	public void SyncOracleandMySQl() throws Exception
	{
		PreparedStatement pstmOracleSelect = null;
		PreparedStatement pstmMySQLInsert = null;
		PreparedStatement pstmMySQLCheckExist = null;
		PreparedStatement pstmMySQLUpdate = null;
		PreparedStatement pstmOracleUpdate = null;
		ResultSet rs = null;
		ResultSet rsCheck = null;
		String strName = "";
		Double dbSourceID = 0d;
		Double dbNewPrice = 0d;
		Double dbOldPrice = 0d;
		Double dbDiscount = 0d;
		Date dtStartDate;
		String strEndDate;
		Double dbPlace = 0d;
		Double dbCategoryId = 0d;
		String strUrl = "";
		Double dbViewClick = 0d;
		String strDescription = "";
		String strBuyerCount = "";
		String strImage = "";
		String strID = "";
		try
		{
			cnMySQL.setAutoCommit(false);
			// Query
			String sqlOracleSelect = "SELECT a.deal_sm_id, a.source_id, a.name, a.oldprice, a.newprice, a.discount, "
					+ "a.startdate, to_char(a.enddate,'dd/MM/yyyy hh24:mi:ss') enddate, a.inserteddate, a.place, a.category_id, "
					+ "a.url, a.viewclick, a.description, a.buyercount, a.status, "
					+ "a.image " + "FROM deal_summary a WHERE a.status = '2' ";
			String strOracleUpdate = "UPDATE DEAL_SUMMARY SET "
					+ "status = '1'" + "WHERE deal_sm_id = ? ";
			String strCheckExistMySQL = "SELECT 1 FROM deal WHERE deal_sm_id = ? ";
			String strInsertMySQL = "INSERT INTO DEAL (NAME, SOURCEID, OLDPRICE, "
					+ "NEWPRICE, DISCOUNT, STARTDATE, ENDDATE, INSERTEDDATE, "
					+ "PLACE, CATEGORY, URL, VIEWCLICK, DESCRIPTION, "
					+ "BUYERCOUNT, active, IMAGE,deal_sm_id) "
					+ "VALUES "
					+ "(?,?,?,?,?, "
					+ "?, STR_TO_DATE(?,'%d/%m/%Y %H:%i:%s'), now(), "
					+ "?,?,?,?,?, " + "?,?,?,? )";
			String strUpdateMySQL = "UPDATE deal SET name = ?,sourceid = ?, oldprice = ?, "
					+ "newprice = ?, discount = ?, startdate = ?, enddate = STR_TO_DATE(?,'%d/%m/%Y %H:%i:%s'), inserteddate = now(), "
					+ "place = ?, category = ?, url= ?, viewclick = ?, description = ?, buyercount = ?, "
					+ "active = ?, image = ? " + "WHERE deal_sm_id = ? ";

			// Prepare
			pstmOracleUpdate = cnOracle.prepareStatement(strOracleUpdate);
			pstmOracleSelect = cnOracle.prepareStatement(sqlOracleSelect);
			pstmMySQLCheckExist = cnMySQL.prepareStatement(strCheckExistMySQL);
			pstmMySQLInsert = cnMySQL.prepareStatement(strInsertMySQL);
			pstmMySQLUpdate = cnMySQL.prepareStatement(strUpdateMySQL);
			// Execute
			rs = pstmOracleSelect.executeQuery();
			while (rs.next())
			{
				strID = rs.getString("deal_sm_id");
				strName = rs.getString("name");
				dbSourceID = rs.getDouble("source_id");
				dbNewPrice = rs.getDouble("newprice");
				dbOldPrice = rs.getDouble("oldprice");
				dbDiscount = rs.getDouble("discount");
				dtStartDate = rs.getDate("startdate");
				strEndDate = rs.getString("enddate");
				dbPlace = rs.getDouble("place");
				dbCategoryId = rs.getDouble("category_id");
				strUrl = rs.getString("url");
				dbViewClick = rs.getDouble("viewclick");
				strDescription = rs.getString("description");
				strBuyerCount = StringUtil.nvl(rs.getString("buyercount"), "0");
				strImage = rs.getString("image");
				// Check exists
				pstmMySQLCheckExist.setString(1, strID);
				rsCheck = pstmMySQLCheckExist.executeQuery();
				// if exists
				if (!rsCheck.next())
				{
					// Insert MySQL
					pstmMySQLInsert.setString(1, strName);
					pstmMySQLInsert.setDouble(2, dbSourceID);
					pstmMySQLInsert.setDouble(3, dbOldPrice);
					pstmMySQLInsert.setDouble(4, dbNewPrice);
					pstmMySQLInsert.setDouble(5, dbDiscount);
					pstmMySQLInsert.setDate(6, dtStartDate);
					pstmMySQLInsert.setString(7, strEndDate);
					pstmMySQLInsert.setDouble(8, dbPlace);
					pstmMySQLInsert.setDouble(9, dbCategoryId);
					pstmMySQLInsert.setString(10, strUrl);
					pstmMySQLInsert.setDouble(11, dbViewClick);
					pstmMySQLInsert.setString(12, strDescription);
					pstmMySQLInsert.setString(13, strBuyerCount);
					pstmMySQLInsert.setString(14, "1");
					pstmMySQLInsert.setString(15, strImage);
					pstmMySQLInsert.setString(16, strID);
					// Execute
					pstmMySQLInsert.execute();
					logInfo("Insert MySQL successfully");
				}
				// if not exists
				else
				{
					// Update MySQL
					pstmMySQLUpdate.setString(1, strName);
					pstmMySQLUpdate.setDouble(2, dbSourceID);
					pstmMySQLUpdate.setDouble(3, dbOldPrice);
					pstmMySQLUpdate.setDouble(4, dbNewPrice);
					pstmMySQLUpdate.setDouble(5, dbDiscount);
					pstmMySQLUpdate.setDate(6, dtStartDate);
					pstmMySQLUpdate.setString(7, strEndDate);
					pstmMySQLUpdate.setDouble(8, dbPlace);
					pstmMySQLUpdate.setDouble(9, dbCategoryId);
					pstmMySQLUpdate.setString(10, strUrl);
					pstmMySQLUpdate.setDouble(11, dbViewClick);
					pstmMySQLUpdate.setString(12, strDescription);
					pstmMySQLUpdate.setString(13, strBuyerCount);
					pstmMySQLUpdate.setString(14, "1");
					pstmMySQLUpdate.setString(15, strImage);
					pstmMySQLUpdate.setString(16, strID);
					// Execute
					pstmMySQLUpdate.execute();
					logInfo("Update MySQL successfully");
				}
				// Update to Oracle
				pstmOracleUpdate.setString(1, strID);
				// Execute
				pstmOracleUpdate.execute();
				logInfo("Synchronize successful with ID = " + strID);
				// Commit
				cnMySQL.commit();
				cnOracle.commit();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		finally
		{
			Database.closeObject(rs);
			Database.closeObject(rsCheck);
			Database.closeObject(pstmOracleSelect);
			Database.closeObject(pstmOracleUpdate);
			Database.closeObject(pstmMySQLCheckExist);
			Database.closeObject(pstmMySQLInsert);
			Database.closeObject(pstmMySQLUpdate);
		}

	}
}
