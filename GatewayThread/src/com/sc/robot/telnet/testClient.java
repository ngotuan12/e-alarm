package com.sc.robot.telnet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.fss.sql.Database;
import com.sc.robot.util.AppServer;

public class testClient
{
	public static void main(String[] args) throws Exception
	{
		Connection cn = AppServer.getConnection("Oracle");
		Connection cnMySQL = AppServer.getConnection("MySQL");
		String strSQL= "";
		PreparedStatement pstm = null;
		ResultSet rsData = null;
		try
		{
			//Oracle
			strSQL = "Select 1 FROM dual ";
			pstm = cn.prepareStatement(strSQL);
			pstm.execute();
			pstm.close();
			System.out.println("Connect oracle sucessfully");
			//MySQL
			strSQL = "Select * FROM deal ";
			pstm = cnMySQL.prepareStatement(strSQL);
			pstm.execute();
			System.out.println("Connect MySQL sucessfully");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			Database.closeObject(pstm);
			Database.closeObject(cn);
		}
	}
}
