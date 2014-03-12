package com.ar.bean;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import ReportTDK.Report;

import com.ar.util.AppProcessor;
import com.ar.util.AppServer;
import com.ar.util.DataHolder;
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
				String devLink = createDeviceReportByArea();
				response.put("FileOut",devLink);
				break;
			case "IssueReportByArea":
				String issueLink = createIssueReportByArea();
				response.put("FileOut",issueLink);
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
			String strFileOut = strFileName + StringUtil.format(new Date(), "yyyyMMddhhmmss");
//			String strFileOut = "dev_report";
			//read sql file
			String strSQL = new ReadSQLFile(templatePath + strFileName + ".sql")
					.getSQLQuery();
			//open connection
			open();
			//get parameter
			String strAreaCode = request.getString("area_code");
			String strStatus = request.getString("status");
			String strAreaSQL = "";
			//set sql parameter
			if(!strAreaCode.equals("ALL"))
			{
				strAreaSQL = " AND d.area_code ='"+strAreaCode+"' ";
			}
			strSQL = strSQL.replaceAll("<%p_area_code%>", strAreaSQL);
			
			if(!strStatus.equals("ALL"))
			{
				strAreaSQL = " AND d.status ='"+strStatus+"' ";
			}
			strSQL = strSQL.replaceAll("<%p_status%>", strAreaSQL);
			//prepare statement
			pstm = mcnMain.prepareStatement(strSQL);
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
			//create file
			//createRealFile(strFileOut);
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
	
	public String createIssueReportByArea() throws Exception
	{
		ResultSet rs = null;
		PreparedStatement pstm = null;
		try 
		{
			//template path
			String excelTemplatePath = AppServer.getParam("ExcelTemplatePath");
			String templatePath = AppServer.getParam("TemplatePath");
			String strReportOut = AppServer.getParam("ReportOut");
			String strFileName = "TemplateIssueReportByArea";
			String strFileOut = strFileName + StringUtil.format(new Date(), "yyyyMMddhhmmss");
//			String strFileOut = "issue_report";
			//read sql file
			String strSQL = new ReadSQLFile(templatePath + strFileName + ".sql").getSQLQuery();
			//open connection
			open();
			//get parameter
			String strDeviceId = request.getString("device_id");
			String strAreaSQL = "";
			//set sql parameter
			if(!strDeviceId.equals("ALL"))
			{
				strAreaSQL = " AND device_id ='"+strDeviceId+"' ";
			}
			strSQL = strSQL.replaceAll("<%p_device_id%>", strAreaSQL);
			//prepare statement
			pstm = mcnMain.prepareStatement(strSQL);
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
			//create file
			//createRealFile(strFileOut);
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
	private void createRealFile(String fileName){
		String reportPath = DataHolder.getReportPath();
		File reportFile = new File(reportPath, fileName + ".xls");
		File tmpReportFile = new File(AppServer.getParam("ReportOut"), fileName + ".xls");
		try {
			FileUtils.copyFile(tmpReportFile, reportFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
