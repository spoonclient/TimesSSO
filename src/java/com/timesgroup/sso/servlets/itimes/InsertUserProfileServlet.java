package com.timesgroup.sso.servlets.itimes;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.timesgroup.sso.constants.SSOConstants;
import com.timesgroup.sso.hibernate.apis.ITimesDataAccessManager;
import com.timesgroup.sso.utils.CryptoUtility;

public class InsertUserProfileServlet extends HttpServlet{
	
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		final Logger mylogger = Logger.getLogger(InsertUserProfileServlet.class);

		String emailId = request.getParameter(SSOConstants.InsertUserProfile.PARAM_EMAILID);
		String firstName=request.getParameter(SSOConstants.InsertUserProfile.PARAM_FIRSTNAME);
		String lastName=request.getParameter(SSOConstants.InsertUserProfile.PARAM_LASTNAME);
		String password=request.getParameter(SSOConstants.InsertUserProfile.PARAM_PASSWORD);
		String dateOfBirth=request.getParameter(SSOConstants.InsertUserProfile.PARAM_DOB);
		String ipAddress=request.getParameter(SSOConstants.InsertUserProfile.PARAM_IPADDRESS);
		String siteReg=request.getParameter(SSOConstants.InsertUserProfile.PARAM_SITEREGISTERED);
		String gender=request.getParameter(SSOConstants.InsertUserProfile.PARAM_GENDER);
		String hs=request.getParameter(SSOConstants.InsertUserProfile.PARAM_HS);
		String city=request.getParameter(SSOConstants.InsertUserProfile.PARAM_CITY);
		
		
		PrintWriter responseWriter = null;
		try {
			responseWriter = response.getWriter();
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		if (emailId ==null || (emailId.trim().compareTo("") == 0) ||
			firstName ==null || (firstName.trim().compareTo("") == 0) ||
			lastName ==null || (lastName.trim().compareTo("") == 0) ||
			dateOfBirth ==null || (dateOfBirth.trim().compareTo("") == 0) ||
			password ==null || (password.trim().compareTo("") == 0) ||
			ipAddress ==null || (ipAddress.trim().compareTo("") == 0) ||
			siteReg ==null || (siteReg.trim().compareTo("") == 0) ||
			gender ==null || (gender.trim().compareTo("") == 0)
			) {

			mylogger.error(SSOConstants.InsertUserProfile.ERROR_WITH_INSERTUSERPROFILE_REQUEST_PARAMETERS);
			responseWriter.write(SSOConstants.InsertUserProfile.ERROR_WITH_INSERTUSERPROFILE_REQUEST_PARAMETERS);
			return;
		}
		
		
		if(emailId!=null && !emailId.matches(SSOConstants.VALID_EMAILID_PATTERN)){
			
			mylogger.error(SSOConstants.InsertUserProfile.MESSAGE_INVALID_EMAILID);
			responseWriter.write(SSOConstants.InsertUserProfile.ERROR_MESSAGE_INVALIDEMAILID);
			return;
		}
		
		if(emailId!=null && emailId.length()>100){
			
			mylogger.error(SSOConstants.InsertUserProfile.MESSAGE_INVALID_EMAILID);
			responseWriter.write(SSOConstants.InsertUserProfile.ERROR_MESSAGE_INVALIDEMAILID_INCORRECTLENGTH);
			return;
		}
		
		if(firstName!=null && !firstName.matches(SSOConstants.VALID_USERID_PATTERN)){
			
			mylogger.error(SSOConstants.InsertUserProfile.ERROR_MESSAGE_INVALIDFIRSTNAME);
			responseWriter.write(SSOConstants.InsertUserProfile.ERROR_MESSAGE_INVALIDFIRSTNAME);
			return;
		}

		if(firstName!=null && firstName.length()>50){
			
			mylogger.error(SSOConstants.InsertUserProfile.MESSAGE_INVALID_FIRSTNAME);
			responseWriter.write(SSOConstants.InsertUserProfile.ERROR_MESSAGE_INVALIDFIRSTNAME_INCORRECTLENGTH);
			return;
		}
		
		if(lastName!=null && !lastName.matches(SSOConstants.VALID_USERID_PATTERN)){
			
			mylogger.error(SSOConstants.InsertUserProfile.ERROR_MESSAGE_INVALIDLASTNAME);
			responseWriter.write(SSOConstants.InsertUserProfile.ERROR_MESSAGE_INVALIDLASTNAME);
			return;
		}

		if(lastName!=null && lastName.length()>50){
			
			mylogger.error(SSOConstants.InsertUserProfile.MESSAGE_INVALID_LASTNAME);
			responseWriter.write(SSOConstants.InsertUserProfile.ERROR_MESSAGE_INVALIDLASTNAME_INCORRECTLENGTH);
			return;
		}
		
		//TODO check date in valid format
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date parsedDate=null;
		try{
		     parsedDate = df.parse(dateOfBirth);           
		     mylogger.debug("DATE =" + df.format(parsedDate));
		} catch (ParseException e)
		{
		     e.printStackTrace();
		}
		
		if(parsedDate==null){
			 
			mylogger.error(SSOConstants.InsertUserProfile.MESSAGE_INVALID_DOB);
			responseWriter.write(SSOConstants.InsertUserProfile.ERROR_MESSAGE_INVALIDDOB);
			return;
		}
		
		
		password=(new CryptoUtility()).performEncrypt(password.trim());
		Date currentDate=new Date(); 
		
		ITimesDataAccessManager iTimesDataAccessManager = new ITimesDataAccessManager();
		int status = iTimesDataAccessManager.insertUserProfile(emailId.toLowerCase(),firstName.toLowerCase(),
				lastName.toLowerCase(),parsedDate,password,ipAddress, siteReg, gender, city , currentDate);
        
		if(status==0){
		
			mylogger.error(SSOConstants.InsertUserProfile.MESSAGE_ERROR_INSERTION);
			responseWriter.write(SSOConstants.InsertUserProfile.ERROR_INSERTION);
			return;
		}
		
		
		if(status>0){
			
			String str=SSOConstants.XML_URL+"<NewDataSet>\n<Table>\n";
			str+="<usr_id_vc>iti"+status+"</usr_id_vc>\n"; 
			str+="<eml_vc>"+emailId+"</eml_vc>\n"; 
			str+="<psswrd_vc>"+password+"</psswrd_vc>\n"; 
			str+="<CRT_DT>"+currentDate+"</CRT_DT>\n"; 
			str+="<DateRegistered>"+currentDate+"</DateRegistered>\n"; 
			str+="<site_reg />\n"; 
			str+="<frst_nm_vc>"+firstName+"</frst_nm_vc>\n";  
			str+="<lst_nm_vc>"+lastName+"</lst_nm_vc>\n";  
			str+="<Address />\n";  
			str+="<Gender>"+gender+"</Gender>\n";  
			str+="<state />\n";  
			str+="<country />\n";  
			str+="<dob>"+dateOfBirth+"</dob>\n";  
			str+="<Referrel>OpenInv</Referrel>\n";  
			str+="<UserType>1</UserType>\n"; 
			str+="</Table></NewDataSet>\n";

			responseWriter.write(str);
			return;
			
		}
		
		
		
		
		
	}
}
