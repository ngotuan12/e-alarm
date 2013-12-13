package com.sc.robot.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fss.sql.Database;
import com.fss.thread.ThreadConstant;
import com.fss.util.DateUtil;
import com.fss.util.NumberUtil;
import com.fss.util.StringUtil;
import com.sc.robot.util.AppServer;

public class ThreadNormalizeDeal extends com.fss.thread.ManageableThread {
	private Connection cnOracle = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fss.thread.ManageableThread#processSession()
	 */
	@Override
	protected void processSession() throws Exception {
		while (miThreadCommand != ThreadConstant.THREAD_STOP) {
			normalize();
			Thread.sleep(60000);
		}
	}

	/**
	 * Ham tien hanh phan tich lai du lieu dau vao
	 * 
	 * @throws Exception
	 */
	private void normalize() throws Exception {
		String strSQL = "";
		String strSQLUpdateDeal = "";
		String strSQLInsertDealSummary = "";
		PreparedStatement pstmSelectDeal = null;
		PreparedStatement pstmUpdateDeal = null;
		PreparedStatement pstmInsertDealSummary = null;
		ResultSet rs = null;
		try {
			logInfo("Starting get deal.. ");
			// Select deal chua phan tich voi status = '2'
			strSQL = "SELECT a.id, a.sourceid, a.name, a.oldprice, a.newprice, a.discount, "
					+ "a.startdate, a.enddate, a.inserteddate, a.place, a.category, "
					+ "a.url, a.viewclick, a.description, a.buyercount, a.status, "
					+ "a.image, a.remaintime "
					+ "FROM deal a "
					+ "WHERE a.status = '2' and a.sourceid in (48,51,52,49,50) --and rownum<=1 and a.id = 15349 "; // 48:
			// hotdeal 
			// 51:nhommua
			strSQLUpdateDeal = "UPDATE deal SET "
					+ "error_message = ?, status = ? " + "WHERE id = ? ";
			strSQLInsertDealSummary = "INSERT INTO deal_summary "
					+ "(deal_sm_id, deal_id, NAME, oldprice, newprice, discount, "
					+ "startdate, enddate, inserteddate, place, category_id, url, "
					+ "viewclick, buyercount, status, image,source_id,description) "
					+ "VALUES (deal_summary_seq.NEXTVAL, ?, ?, ?, ?, ?, "
					+ "to_date(?,'dd/MM/yyyy hh24:mi:ss'), to_date(?,'dd/MM/yyyy hh24:mi:ss'), sysdate, ?, ?, ?, "
					+ "?, ?, '2', ?, ?,?) ";
			// Prepared statement
			pstmSelectDeal = cnOracle.prepareStatement(strSQL);
			pstmUpdateDeal = cnOracle.prepareStatement(strSQLUpdateDeal);
			pstmInsertDealSummary = cnOracle
					.prepareStatement(strSQLInsertDealSummary);
			// Get deal
			rs = pstmSelectDeal.executeQuery();
			Integer iDealID = 0;
			Integer iSourceID;
			Integer iCategoryID;
			String strName = "";
			String strOldPrice = "";
			Double dbOldPrice = 0d;
			String strViewClick = "";
			String strBuyerCount = "";
			String strNewPrice = "";
			Double dbNewPrice = 0d;
			Double dbBuyerCount = 0d;
			String strDiscount = "";
			Double dbDiscount = 0d;
			String strURL = "";
			String strImage = "";
			String strDescription = "";
			String strStartDate = "";
			String strEndDate = "";
			String strHTML = "";
			Timestamp dtInsertedDate = null;
			while (rs.next()) {
				iDealID = rs.getInt("id");
				iSourceID = rs.getInt("sourceid");
				iCategoryID = rs.getInt("category");
				strName = StringUtil.nvl(rs.getString("name"), "");
				strOldPrice = rs.getString("oldprice");
				strNewPrice = rs.getString("newprice");
				strDiscount = rs.getString("discount");
				strImage = rs.getString("image");
				strURL = rs.getString("url");
				strViewClick = rs.getString("viewclick");
				strBuyerCount = rs.getString("buyercount");
				strDescription = rs.getString("description");
				strEndDate = rs.getString("remaintime");
				dtInsertedDate = rs.getTimestamp("inserteddate");
				logInfo("Normalize deal " + iDealID);
				if (iSourceID == 52) {
					strHTML = getHTMLSource(strURL);
				}
				if (iSourceID == 50) {
					strHTML = getHTMLSource("http://www.cungmua.com" + strURL);
				}
				// Name
				if (strName == null) {
					updateDeal(pstmUpdateDeal, iDealID,
							"Normalize false with name is null ", "0");
					logInfo("Normalize false with name is null ");
					continue;
				}
				// URL
				strURL = normalizeURL(iSourceID, strURL);
				// Description
				strDescription = normalizDescription(iSourceID, strHTML,
						strDescription);
				// Old price
				try {
					dbOldPrice = normalizeOldPrice(strOldPrice);
				} catch (Exception ex) {
					updateDeal(pstmUpdateDeal, iDealID,
							"Normalize false with OldPrice = " + strOldPrice,
							"0");
					logInfo("Normalize false with OldPrice = " + strOldPrice);
					continue;
				}
				// New price
				try {
					dbNewPrice = normalizeNewPrice(strNewPrice);
				} catch (Exception ex) {
					updateDeal(pstmUpdateDeal, iDealID,
							"Normalize false with NewPrice = " + strNewPrice,
							"0");
					logInfo("Normalize false with NewPrice = " + strNewPrice);
					continue;
				}
				// Discount
				try {
					dbDiscount = normalizeDiscount(strDiscount);
				} catch (Exception ex) {
					updateDeal(pstmUpdateDeal, iDealID,
							"Normalize false with Discount = " + strDiscount,
							"0");
					logInfo("Normalize false with Discount = " + strDiscount);
					continue;
				}
				// Buyer Count
				try {
					dbBuyerCount = normalizeBuyerCount(iSourceID, strHTML,
							strBuyerCount);
				} catch (Exception ex) {
					updateDeal(pstmUpdateDeal, iDealID,
							"Normalize false with strBuyerCount = "
									+ strBuyerCount, "0");
					logInfo("Normalize false with BuyerCount = " + strDiscount);
					continue;
				}
				// End date
				try {
					strEndDate = normalizeEndDate(iSourceID, strEndDate,
							dtInsertedDate, strHTML);
				} catch (Exception ex) {
					updateDeal(pstmUpdateDeal, iDealID,
							"Normalize false with Enddate = " + strEndDate, "0");
					logInfo("Normalize false with Enddate = " + strEndDate);
					continue;
				}
				// Insert deal summary
				pstmInsertDealSummary.setInt(1, iDealID);
				pstmInsertDealSummary.setString(2, strName);
				pstmInsertDealSummary.setDouble(3, dbOldPrice);
				pstmInsertDealSummary.setDouble(4, dbNewPrice);
				pstmInsertDealSummary.setDouble(5, dbDiscount);
				pstmInsertDealSummary.setString(6, strStartDate);
				pstmInsertDealSummary.setString(7, strEndDate);
				pstmInsertDealSummary.setInt(8, 1);
				pstmInsertDealSummary.setInt(9, iCategoryID);
				pstmInsertDealSummary.setString(10, strURL);
				pstmInsertDealSummary.setString(11, strViewClick);
				pstmInsertDealSummary.setDouble(12, dbBuyerCount);
				pstmInsertDealSummary.setString(13, strImage);
				pstmInsertDealSummary.setInt(14, iSourceID);
				pstmInsertDealSummary.setString(15, strDescription);
				pstmInsertDealSummary.execute();
				// Update deal
				updateDeal(pstmUpdateDeal, iDealID, "", "1");
				logInfo("Normalize success deal " + iDealID);
			}
			logInfo("End get deal.. ");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			Database.closeObject(rs);
			Database.closeObject(pstmSelectDeal);
			Database.closeObject(pstmUpdateDeal);
			Database.closeObject(pstmInsertDealSummary);
		}
	}

	private String getHTMLSource(String strURL) throws Exception {

		return Jsoup.connect(strURL).get().html();
	}

	private Double normalizeBuyerCount(int iSourceID, String strHTMl,
			String strBuyerCount) throws Exception {
		try {
			if (iSourceID == 52) {
				Document doc = Jsoup.parse(strHTMl);
				String str = "";
				Pattern p = Pattern
						.compile("[0-9]+[,|.][0-9]*[,|.][0-9]*|[0-9]+[,|.][0-9]*|[0-9]*[,|.][0-9]+|[0-9]+");
				Matcher m = p.matcher(doc
						.select("div.save-amo-time > span.amo").html());
				m.find();
				str = m.group();
				strBuyerCount = "havedata";
				return Math.abs(NumberUtil.toNumber(str, "####0.##"));
			}
			if (strBuyerCount == null)
				throw new Exception("BuyerCount is null");
			String str = "";
			Pattern p = Pattern
					.compile("[0-9]+[,|.][0-9]*[,|.][0-9]*|[0-9]+[,|.][0-9]*|[0-9]*[,|.][0-9]+|[0-9]+");
			Matcher m = p.matcher(strBuyerCount);
			m.find();
			str = m.group();
			return Math.abs(NumberUtil.toNumber(str, "####0.##"));
		} catch (Exception ex) {
			throw ex;
		}

	}

	private Double normalizeDiscount(String strDiscount) throws Exception {
		try {
			if (strDiscount == null)
				throw new Exception("discount is null");
			String str = "";
			Pattern p = Pattern
					.compile("[0-9]+[,|.][0-9]*[,|.][0-9]*|[0-9]+[,|.][0-9]*|[0-9]*[,|.][0-9]+|[0-9]+");
			Matcher m = p.matcher(strDiscount);
			m.find();
			str = m.group();

			str = str.replaceAll(",", "@");
			str = str.replaceAll("\\.", "");
			str = str.replaceAll("@", "\\.");
			str = str.replaceAll("-", "");
			return Math.abs(NumberUtil.toNumber(str, "####0.##"));
		} catch (Exception ex) {
			throw ex;
		}
	}

	private String normalizeURL(int iSourceID, String strURL) throws Exception {
		if (iSourceID == 49) {
			return "http://cucre.vn" + strURL;
		} else if (iSourceID == 48) {
			return "http://hotdeal.vn" + strURL;
		} else if (iSourceID == 50) {
			return "http://www.cungmua.com" + strURL;
		} else
			return strURL;
	}

	private String normalizDescription(int iSourceID, String strHTMl,
			String strDescription) throws Exception {
		if (iSourceID == 50) {
			Document doc = Jsoup.parse(strHTMl);
			String str = "";
			str = doc.select("div.product_detail_content").text();
			return str;
		} else
			return strDescription;
	}

	private String normalizeEndDate(int iSourceID, String strEndDate,
			Date dtInsertedDate, String strHTMl) throws Exception {
		if (strEndDate == null)
			return null;
		if (iSourceID == 51) // Mua chung
		{
			String[] str = strEndDate.split(",");
			Date dt = new Date(Long.parseLong(str[2]) * 1000);
			return StringUtil.format(dt, "dd/MM/yyyy HH:mm:ss");
		}
		if (iSourceID == 48) // Hot deal
		{
			String[] str = strEndDate.split(":");
			logInfo(StringUtil.format(dtInsertedDate, "dd/MM/yyyy HH:mm:ss"));
			logInfo(strEndDate);
			Date dt = DateUtil
					.addHour(dtInsertedDate, Integer.parseInt(str[0]));
			dt = DateUtil.addMinute(dt, Integer.parseInt(str[1]));
			dt = DateUtil.addSecond(dt, Integer.parseInt(str[2]));
			logInfo(StringUtil.format(dt, "dd/MM/yyyy HH:mm:ss"));
			return StringUtil.format(dt, "dd/MM/yyyy HH:mm:ss");
		}
		if (iSourceID == 52) // Nhom mua
		{
			Document doc = Jsoup.parse(strHTMl);
			String str = doc.select("div.save-amo-time").outerHtml();
			Pattern p = Pattern.compile("[0-9]");
			Matcher m = p.matcher(strEndDate);
			m.find();
			str = m.group();
			Date dt = DateUtil.addDay(new Date(), Integer.parseInt(str));
			return StringUtil.format(dt, "dd/MM/yyyy hh:mm:ss");
		}
		if (iSourceID == 49) // Nhom mua
		{
			String str = "";
			Pattern p = Pattern.compile("[0-9]");
			Matcher m = p.matcher(strEndDate);
			m.find();
			str = m.group();
			Date dt = DateUtil.addDay(new Date(), Integer.parseInt(str));
			return StringUtil.format(dt, "dd/MM/yyyy hh:mm:ss");
		}
//		if (iSourceID == 50) // cung mua
//		{
//			Document doc = Jsoup.parse(strHTMl);
//			String str = "";
//			
//			Elements listScript = doc.select("script");
//			String strScriptMatch = "";
//			for (Element element : listScript) {
//				if (element.html().indexOf("dateFuture") > 0
//						&& element.html().indexOf("dateNow") > 0)
//					strScriptMatch = element.toString();
//			}
//			logInfo(strScriptMatch);
//			String strEnd = strScriptMatch.substring(strScriptMatch.indexOf(";"));
//			//end
//			String strYear = strEnd.substring(strEnd.indexOf("2"), strEnd.indexOf(",")).trim();
//			int year = Integer.parseInt(strYear);
//			strEnd = strEnd.substring(strEnd.indexOf(",")+1);
//			
//			String strMonth = strEnd.substring(0,strEnd.indexOf(",")).trim();
//			int month = Integer.parseInt(strMonth);
//			strEnd = strEnd.substring(strEnd.indexOf(",")+1);
//			
//			String strDay = strEnd.substring(0,strEnd.indexOf(",")).trim();
//			int day = Integer.parseInt(strDay);
//			strEnd = strEnd.substring(strEnd.indexOf(",")+1);
//			
//			String strHours = strEnd.substring(0,strEnd.indexOf(",")).trim();
//			int hours = Integer.parseInt(strHours);
//			strEnd = strEnd.substring(strEnd.indexOf(",")+1);
//			
//			String strMinutes = strEnd.substring(0,strEnd.indexOf(",")).trim();
//			int minutes = Integer.parseInt(strMinutes);
//			strEnd = strEnd.substring(strEnd.indexOf(",")+1);
//			
//			String strSecond = strEnd.substring(0,strEnd.indexOf(")")).trim();
//			int second = Integer.parseInt(strSecond);
//			
//			
//			Date dt = new Date(year-1900, month, day, hours, minutes, second);
//			return StringUtil.format(dt, "dd/MM/yyyy hh:mm:ss");
//		}
		return null;
	}

	private Double normalizeNewPrice(String strNewPrice) throws Exception {
		try {
			if (strNewPrice == null)
				throw new Exception("new price is null");
			String str = "";
			Pattern p = Pattern
					.compile("[0-9]+[,|.][0-9]*[,|.][0-9]*|[0-9]+[,|.][0-9]*|[0-9]*[,|.][0-9]+|[0-9]+");
			Matcher m = p.matcher(strNewPrice);
			m.find();
			str = m.group();

			str = str.replaceAll(",", "@");
			str = str.replaceAll("\\.", "");
			str = str.replaceAll("@", "\\.");
			return NumberUtil.toNumber(str, "####0.####");
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static void main(String[] args) {
		String str = "100.121,23213";
		str = str.replaceAll(",", "@");
		str = str.replaceAll("\\.", "");
		str = str.replaceAll("@", "\\.");
		System.out.println(str);
	}

	private Double normalizeOldPrice(String strOldPrice) throws Exception {
		try {
			if (strOldPrice == null)
				return null;
			String str = "";
			Pattern p = Pattern
					.compile("[0-9]+[,|.][0-9]*[,|.][0-9]*|[0-9]+[,|.][0-9]*|[0-9]*[,|.][0-9]+|[0-9]+");
			Matcher m = p.matcher(strOldPrice);
			m.find();
			str = m.group();

			str = str.replaceAll(",", "@");
			str = str.replaceAll("\\.", "");
			str = str.replaceAll("@", "\\.");
			return NumberUtil.toNumber(str, "####0.####");
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * @param pstm
	 * @param dbID
	 * @param strError
	 * @param strStatus
	 * @throws Exception
	 */
	private void updateDeal(PreparedStatement pstm, Integer dbID,
			String strError, String strStatus) throws Exception {
		pstm.setString(1, strError);
		pstm.setString(2, strStatus);
		pstm.setInt(3, dbID);
		pstm.execute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fss.thread.ManageableThread#beforeSession()
	 */
	@Override
	protected void beforeSession() throws Exception {
		cnOracle = AppServer.getConnection("Oracle");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fss.thread.ManageableThread#afterSession()
	 */
	@Override
	protected void afterSession() throws Exception {
		Database.closeObject(cnOracle);
	}
}
