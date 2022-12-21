package com.common.interceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.user.model.UserVO;

import lombok.extern.log4j.Log4j;

@Log4j
public class AdminCheckInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
		log.info("AdminCheck prehandler()");
		HttpSession ses = req.getSession();
		UserVO user = (UserVO)ses.getAttribute("loginUser");
		
		if(user!=null) {
			if(user.getStatus()!=9) {
				req.setAttribute("message", "관리자만 이용 가능");
				req.setAttribute("loc", req.getContextPath() + "/index");
				RequestDispatcher disp = req.getRequestDispatcher("/WEB-INF/views/msg.jsp");
				disp.forward(req, res);
				
				return false;
			} else {
				return true;
			}
		}
		return false;
		
	}
	
}
