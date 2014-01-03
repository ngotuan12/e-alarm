package com.ar.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ar.util.Util;

public class GatewayService extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7104449940558154348L;

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// super
		super.service(request, response);
		//
		try {
			// print some parameter
			System.out.println(request.getCharacterEncoding());
			System.out
					.println(request.getHeader("Access-Control-Allow-Origin"));
			// process request
			Util.processRequest(request, response);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	}

}
