package com.sc.robot.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fss.dictionary.Dictionary;
import com.fss.dictionary.DictionaryNode;
import com.fss.sql.Database;
import com.fss.thread.ThreadConstant;
import com.fss.util.AppException;
import com.sc.robot.dealgrabber.DealProp;
import com.sc.robot.util.AppServer;

/**
 * DEPQ 2011
 * 
 * @version 1.0
 */
public class ThreadSyncRequest extends
		com.fss.thread.ManageableThread {
	private int recordinsertcount = 0;
	private Connection cnMySQL = null;
	Connection connection;
	String oldPrice, newPrice, discount;
	DealProp MuaChungPro, CungMuaPro;
	private int iDelaytime = 30000;

	public ThreadSyncRequest() {
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vector getParameterDefinition() {
		Vector vtReturn = new Vector();
		// // Div properties
		// Vector vtDefinition = new Vector();
		// vtDefinition.addElement(createParameterDefinition("Div Name", "",
		// ParameterType.PARAM_TEXTBOX_MAX, "1024", "", "0"));
		// vtDefinition.addElement(createParameterDefinition("Value", "",
		// ParameterType.PARAM_TEXTBOX_MAX, "1024", "", "1"));
		// vtReturn.addElement(createParameterDefinition("List Div", "",
		// ParameterType.PARAM_TABLE, vtDefinition, "Isam list"));

		// Add all
		vtReturn.addAll(super.getParameterDefinition());
		return vtReturn;
	}

	public void fillParameter() throws AppException {
		iDelaytime = loadUnsignedInteger("DelayTime");
		super.fillParameter();
	}

	protected void beforeSession() throws Exception {
		MuaChungPro = new DealProp();
		CungMuaPro = new DealProp();
		// List Div
		// connection
//		cnMySQL = AppServer.getConnection("Oracle");
		recordinsertcount = 0;
	}

	protected void processSession() throws Exception {

		while (miThreadCommand != ThreadConstant.THREAD_STOP) {
			recordinsertcount = 0;
			log("dsfdsfdsfds");
			logInfo("Starting ...");

//			 //Done
			logInfo("Start get Cucre data");
			//getDeal(new Dictionary("resource/divsetting/CucReSetting-bottom.txt"));
			getDeal(new Dictionary("resource/divsetting/CucReSetting.txt"));
			logInfo("Done Getting Deal from cuc re. Total record insert to database:"
					+ recordinsertcount + " record(s)");
			recordinsertcount = 0;
			logInfo("End get Cucre data");

			 //  done
			 logInfo("Start get Cung mua data");
			 getDeal(new
			 Dictionary("resource/divsetting/CungmuaSetting.txt"));
			 logInfo("Done Getting Deal from cung mua. Total record insert to database:"
			 + recordinsertcount + " record(s)");
			 recordinsertcount = 0;
			 logInfo("End get cung mua data");

//			// done
			 logInfo("Start get HotDeal data");
			 getDeal(new
			 Dictionary("resource/divsetting/HotdealSetting.txt"));
			 logInfo("Done Getting Deal from hotdeal. Total record insert to database:"
			 + recordinsertcount + " record(s)");
			 recordinsertcount = 0;
			 logInfo("End get HotDeal data");
//
//			 // Done
			 logInfo("Start get Muachung data");
			 getDeal(new
			 Dictionary("resource/divsetting/MuachungSetting.txt"));
			 logInfo("Done Getting Deal from muachung. Total record insert to database:"
			 + recordinsertcount + " record(s)");
			 recordinsertcount = 0;
			 logInfo("End get Muachung data");
//			
//			// done
			 logInfo("Start get nhom mua data");
			 recordinsertcount = 0;
			 getDeal(new
			 Dictionary("resource/divsetting/NhommuaSetting-1.txt"));
			 getDeal(new
			 Dictionary("resource/divsetting/NhommuaSetting-2.txt"));
			 getDeal(new
			 Dictionary("resource/divsetting/NhommuaSetting-3.txt"));
			 logInfo("Done Getting Deal nhommua. Total record insert to database:"
			 + recordinsertcount + " record(s)");
			 logInfo("End get nhom mua data");
			Thread.sleep(iDelaytime * 1000);
		}
	}

	protected void afterSession() throws Exception {
		Database.closeObject(cnMySQL);
	}

	public int validateDeal(String dealName, String dealDiscount,
			String dealOldPrice, String dealNewPrice) throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String sqlQuery = "SELECT * FROM deal WHERE Name =?";
			pstm = cnMySQL.prepareStatement(sqlQuery);
			pstm.setString(1, dealName);
			rs = pstm.executeQuery();
			if (rs.next()) {
				while (rs.next()) {
					oldPrice = rs.getString("OldPrice").trim();
					newPrice = rs.getString("NewPrice").trim();
					discount = rs.getString("Discount").trim();
					if (oldPrice.equalsIgnoreCase(dealOldPrice)
							&& newPrice.equalsIgnoreCase(dealNewPrice)
							&& discount.equalsIgnoreCase(dealDiscount)) {
						return rs.getInt("ID");
					}
				}
			} else {
				return -1;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			Database.closeObject(pstm);
			Database.closeObject(rs);
		}
		return 1;
	}

	public boolean validateDatabase(String strDiscount, String strOldPrice,
			String strNewPrice, String strImageLink) throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			// Query
			String sqlQuery = "select * from deal where " + "DISCOUNT= ? "
					+ "and OLDPRICE= ? " + "and NEWPRICE= ? " + "or IMAGE= ?";
			// Prepare
			pstm = cnMySQL.prepareStatement(sqlQuery);
			pstm.setString(1, strDiscount);
			pstm.setString(2, strOldPrice);
			pstm.setString(3, strNewPrice);
			pstm.setString(4, strImageLink);
			// Execute
			rs = pstm.executeQuery();
			if (rs.next()) {
				return true;
			}
			return false;
		} catch (Exception ex) {

			ex.printStackTrace();
			throw ex;
		} finally {
			Database.closeObject(pstm);
			Database.closeObject(rs);
		}
	}

	/**
	 * Get deals from some site
	 * 
	 * This function gets the www, find the description
	 * 
	 * @param
	 * @return
	 * 
	 */

	@SuppressWarnings({ "unchecked", "unused" })
	private long getDeal(Dictionary dic) throws Exception {
		DictionaryNode node = dic.getChild("ListURL");
		long startTime = System.nanoTime();
		String strSourceID = dic.getString("SourceID");
		Vector<DictionaryNode> vtURL = node.getChildList();
		for (int index = 0; index < node.getChildList().size(); index++) {
			if (!vtURL.elementAt(index).mstrName.equals("Url"))
				continue;
			String strURL = vtURL.elementAt(index).mstrValue;
			String strCategoryID = vtURL.elementAt(index).getString("Cate");
			logInfo("Start get deal");
			logInfo("URL:" + strURL);
			logInfo("Cate:" + strCategoryID);
			Document docs = Jsoup.connect(strURL).get();

			String html = docs.html();
			Document doc = Jsoup.parse(html);
			// doc.at
			// doc.
			Elements listDeal = doc.select(dic.getString("DealClass"));
			Elements listScript = doc.select("script");
			String strName = "";
			String strLink = "";
			String strImage = "";
			String strDescription = "";
			String strBuyerCount = "";
			String strPlace = "";
			String strOldPrice = "";
			String strNewPrice = "";
			String strStartDate = "";
			String strIDEndDate = "";
			String strEndDate = "";
			String strDiscount = "";
			for (int i = 0; i < listDeal.size(); i++) {
				Element element = (Element) listDeal.get(i);
				strName = getValueOfElements(element, dic.getString("NameDiv"),
						dic.getString("NameDiv.Inner"));
				strLink = getAttOfElement(element,
						dic.getString("DeallinkDiv"),
						dic.getString("DeallinkDiv.Att"));
				strImage = getAttOfElement(element, dic.getString("ImageDiv"),
						dic.getString("ImageDiv.Att"));
				strDescription = getValueOfElements(element,
						dic.getString("DescriptionDiv"),
						dic.getString("DescriptionDiv.Inner"));
				strBuyerCount = getValueOfElements(element,
						dic.getString("BuyerCountDiv"),
						dic.getString("BuyerCountDiv.Inner"));
				strPlace = getValueOfElements(element,
						dic.getString("PlaceDiv"),
						dic.getString("PlaceDiv.Inner"));
				strOldPrice = getValueOfElements(element,
						dic.getString("OldPriceDiv"),
						dic.getString("OldPriceDiv.Inner"));
				strNewPrice = getValueOfElements(element,
						dic.getString("NewPriceDiv"),
						dic.getString("NewPriceDiv.Inner"));
				strDiscount = getValueOfElements(element,
						dic.getString("DiscountDiv"),
						dic.getString("DiscountDiv.Inner"));

				if (dic.getString("EndDateDiv.Type").equals("Search")) {
					strIDEndDate = element
							.getElementsByAttributeValueContaining(
									dic.getString("EndDateDiv.Match.Att"),
									dic.getString("EndDateDiv.Match.Value"))
							.attr(dic.getString("EndDateDiv.Match.Att"));
					strEndDate = getEndDate(listScript, strIDEndDate);
				} else {
//					strEndDate = getValueOfElements(element,
//							dic.getString("EndDateDiv"),
//							dic.getString("EndDateDiv.Inner"));
					strEndDate = element.select(dic.getString("EndDateDiv")).html();
				}
//				if (i == 0 || i == 1) {
//					logInfo("Name:" + strName);
//					logInfo("Old price:" + strOldPrice);
//					logInfo("New price:" + strNewPrice);
//					logInfo("Link:" + strLink);
//					logInfo("Image:" + strImage);
//					logInfo("Description:" + strDescription);
//					logInfo("Buyer Count:" + strBuyerCount);
//					logInfo("Place:" + strPlace);
//					logInfo("Discount:" + strDiscount);
//					logInfo("Enddate:" + strEndDate);
//				}
				// Check Database
				int newDealCreated = 0;
				if (!validateDatabase(strDiscount, strOldPrice, strNewPrice,
						strImage)) {
					insertMySQL(strName, strSourceID, strOldPrice, strNewPrice,
							strDiscount, strPlace, strCategoryID, strLink, "0",
							strBuyerCount, "2", strImage, strDescription,
							strEndDate);
					recordinsertcount++;
				}
			}
		}
		long estimatedTimeInNanoSeconds = System.nanoTime() - startTime;
		long estimatedTimeInSeconds = estimatedTimeInNanoSeconds / 1000000000;
		logInfo("Total time : " + estimatedTimeInSeconds);
		logInfo("End get deal");
		return estimatedTimeInNanoSeconds;
	}

	public String getEndDate(Elements listScript, String strEndDateID) {
		for (Element element : listScript) {
			if (element.toString().indexOf(strEndDateID) > 0)
				return element.toString();
		}
		return "";
	}

	public String getAttOfElement(Element element, String strClass,
			String strAtt) throws Exception {
		if (strAtt.equals("")) {
			return element.select(strClass).text();
		} else
			return element.select(strClass).attr(strAtt);
	}

	public String getValueOfElements(Element element, String strClass,
			String strInner) throws Exception {
		if (strInner.equals("")) {
			return element.select(strClass).text();
		} else
			return element.select(strClass).select(strInner).text();
	}
	

	private void insertMySQL(String strName, String strSourceID,
			String strOldPrice, String strNewPrice, String strDiscount,
			String strPlace, String strCategory, String strURL,
			String strViewClick, String strBuyerCount, String strActive,
			String strImage, String strDescrip, String strRemainTime)
			throws Exception {
		String strSQL = "";
		PreparedStatement pstm = null;
		try {
			cnMySQL.setAutoCommit(false);
			// strSQL
			strSQL = "INSERT INTO deal (ID,NAME, SOURCEID, OLDPRICE, "
					+ "NEWPRICE, DISCOUNT, INSERTEDDATE, "
					+ "PLACE, CATEGORY, URL, VIEWCLICK, "
					+ "BUYERCOUNT, status, IMAGE,DESCRIPTION,REMAINTIME) "
					+ "VALUES " + "(deal_seq.nextval,?,?,?, " + "?,?,sysdate, "
					+ "?,?,?, " + "?,?,?,?,?,? )";
			// prepare statement
			pstm = cnMySQL.prepareStatement(strSQL);
			pstm.setString(1, strName);
			pstm.setString(2, strSourceID);
			pstm.setString(3, strOldPrice);
			pstm.setString(4, strNewPrice);
			pstm.setString(5, strDiscount);
			pstm.setString(6, strPlace);
			pstm.setString(7, strCategory);
			pstm.setString(8, strURL);
			pstm.setString(9, strViewClick);
			pstm.setString(10, strBuyerCount);
			pstm.setString(11, strActive);
			pstm.setString(12, strImage);
			pstm.setString(13, strDescrip);
			pstm.setString(14, strRemainTime);
			pstm.execute();
			cnMySQL.commit();
		} catch (Exception ex) {
			cnMySQL.rollback();
			ex.printStackTrace();
			throw ex;
		} finally {
			Database.closeObject(pstm);
		}
	}
}
