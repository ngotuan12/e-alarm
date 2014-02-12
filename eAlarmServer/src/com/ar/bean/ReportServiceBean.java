package com.ar.bean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import ReportTDK.Report;

import com.ar.util.AppProcessor;
import com.ar.util.AppServer;
import com.ar.util.ReadSQLFile;
import com.fss.sql.Database;
import com.fss.util.StringUtil;

public class ReportServiceBean extends AppProcessor
{

	
	public void doGet() throws Exception
	{
		
	}

	@Override
	public void doPost() throws Exception
	{
		String request_type = request.getString("Method");
		switch (request_type)
		{
			case "DeviceReportByArea":
				String strLink = createDeviceReportByArea();
				System.out.println("http://127.0.0.1:8888/AlarmServer/report/" + strLink);
				response.put("FileOut",strLink);
				break;
		}

	}

	public void doDelete() throws Exception
	{

	}
	
	public String createDeviceReportByArea() throws Exception
	{
		ResultSet rs = null;
		PreparedStatement pstm = null;
		try 
		{
			//template path
			String excelTemplatePath = AppServer.getParam("ExcelTemplatePath");
			String templatePath = AppServer.getParam("TemplatePath");
			String strReportOut = AppServer.getParam("ReportOut");
			String strFileName = "TemplateDeviceReportByArea";
			//String strFileOut = strFileName + StringUtil.format(new Date(), "yyyyMMddhhmmss");
			String strFileOut = "report";
			//read sql file
			String strSQL = new ReadSQLFile(templatePath + strFileName + ".sql")
					.getSQLQuery();
			//open connection
			open();
			//prepare statement
			pstm = mcnMain.prepareStatement(strSQL);
//			String strAreaCode = "HN";
			//set sql parameter
//			strSQL = strSQL.replaceAll("<%p_area_code%>", strAreaCode);
			//execute query
			rs = pstm.executeQuery();
			//create report
			Report report = new Report(rs,
					excelTemplatePath + strFileName + ".xls", strReportOut
							+ strFileOut + ".xls");
			report.setParameter("$Report_Date",
					StringUtil.format(new Date(), "yyyy-MM-dd"));
			//fill data
			report.fillDataToExcel();
			//return link
			return strFileOut + ".xls";
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
			throw ex;
		}
		finally
		{
			Database.closeObject(rs);
			Database.closeObject(pstm);
			close();
		}
	}
}
