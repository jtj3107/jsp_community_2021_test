package com.jhs.exam.exam2.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jhs.exam.exam2.app.App;
import com.jhs.exam.exam2.container.Container;
import com.jhs.exam.exam2.dto.Member;
import com.jhs.exam.exam2.util.Ut;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Rq {
	private HttpServletRequest req;
	private HttpServletResponse resp;
	// 입력된 uri가 설정된 uri의 길이보다 짧으면 true로 변경
	// 올바른 접근인지 파악하기 위해 사용한다
	@Getter
	private boolean isInvalid = false;
	// 입력된 uri 해당 index에 맞게 저장
	// [usr]/article/list
	@Getter
	private String controllerTypeName;
	// usr/[article]/list
	@Getter
	private String controllerName;
	// usr/article/[list]
	@Getter
	private String actionMethodName;
	
	// admin계정 여부를 확인
	@Getter
	@Setter
	private boolean isAdmin = false;
	
	// 로그인 여부 확인
	@Getter
	@Setter
	private boolean isLogined = false;
	
	// 로그인중이면 해당 멤버의 번호 저장
	@Getter
	@Setter
	private int loginedMemberId = 0;
	
	// 로그인중이면 해당 멤버 정보 저장
	@Getter
	@Setter
	private Member loginedMember = null;
	
	// App 클래스를 객체화
	@Getter
	private App app = Container.app;
	
	// 로그인이 필요한 기능에서 로그인을 하지 않으면 true리턴(로그인하지 않음)
	public boolean isNotLogined() {
		return isLogined == false;
	}

	public Rq(HttpServletRequest req, HttpServletResponse resp) {
		// 들어오는 파리미터를 UTF-8로 해석
		try {
			req.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 서블릿이 HTML 파일을 만들때 UTF-8 로 쓰기
		resp.setCharacterEncoding("UTF-8");

		// HTML이 UTF-8 형식이라는 것을 브라우저에게 알린다.
		resp.setContentType("text/html; charset=UTF-8");

		this.req = req;
		this.resp = resp;

		// 도메인 이후 하부 주소를 가죠오는 메서드
		String requestUri = req.getRequestURI();
		// 가져온 uri를 "/"를 기준으로 나누어 배열에 저장
		String[] requestUriBits = requestUri.split("/");

		// 해당 커뮤니티는 길이를 5로 지정
		// []/jsp_community_2021/usr/article/modify = 5
		int minBitsCount = 5;

		// 저장된 배열의 길이가 minBitsCount보다 작을시 isInvalid를 true로 변경후 리턴
		if (requestUriBits.length < minBitsCount) {
			isInvalid = true;
			return;
		}

		// index지정
		int controllerTypeNameIndex = 2;
		int controllerNameIndex = 3;
		int actionMethodNameIndex = 4;

		// 저장된 배열에 맞는 index를 지정하고 배열값 변수에 저장
		this.controllerTypeName = requestUriBits[controllerTypeNameIndex];
		this.controllerName = requestUriBits[controllerNameIndex];
		this.actionMethodName = requestUriBits[actionMethodNameIndex];
	}

	// resp.getWriter().append(변수)를 쓰는대신 간소화 문자를 출력해주는 메서드
	public void print(String str) {
		try {
			resp.getWriter().append(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// print메서드에 줄바꿈을 더한 메서드
	public void println(String str) {
		print(str + "\n");
	}

	// 해당 파일 위치를 변수에 담아 이동시켜주는 메서드
	public void jsp(String jspPath) {
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/WEB-INF/" + jspPath + ".jsp");
		try {
			requestDispatcher.forward(req, resp);
		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	// 사용자가 입력한 값(문자)을 읽어오는 메서드
	public String getParam(String paramName, String defaultValue) {
		// 변수 paramName 값을 읽어와 paramValue에 저장
		String paramValue = req.getParameter(paramName);

		// paramValue에 저장된 값이 null이거나 길이가 0일경우 defaultValue값 리턴
		if (paramValue == null || paramValue.trim().length() == 0) {
			return defaultValue;
		}

		// 저장된 paramValue값 리턴
		return paramValue;
	}

	// 사용자가 입력한 값(정수만 가능)을 읽어오는 메서드
	public int getIntParam(String paramName, int defaultValue) {
		// 변수 paramName 값을 읽어와 paramValue에 저장
		String paramValue = req.getParameter(paramName);

		// paramValue 값이 null일 경우 defaultValue값을 리턴
		if (paramValue == null) {
			return defaultValue;
		}

		// paramValue값이 있을경우 정수화 하여 리턴
		try {
			return Integer.parseInt(paramValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	// 문자 출력 함수에 변수를 넣어 출력해주는 메서드
	public void printf(String format, Object... args) {
		print(Ut.f(format, args));
	}

	// 입력한 변수 msg를 출력하고 페이지 뒤로가기 메서드
	public void historyBack(String msg) {
		println("<script>");
		if (msg != null && msg.trim().length() > 0) {
			printf("alert('%s');\n", msg.trim());
		}
		println("history.back();");
		println("</script>");
	}

	// println메서드가 문자형이 아닌 자료형을 받을시 문자화 하여 출력해주는 메서드
	public void println(Object obj) {
		println(obj.toString());
	}

	// 변수를 받아 String attrName이라는 이름 arrtValue라는 값으로 저장
	public void setAttr(String attrName, Object attrValue) {
		req.setAttribute(attrName, attrValue);
	}

	// 변수 msg를 출력하고 redirectUri이동하는 메서드
	public void replace(String msg, String redirectUri) {
		println("<script>");
		if (msg != null && msg.trim().length() > 0) {
			printf("alert('%s');\n", msg.trim());
		}
		printf("location.replace('%s');\n", redirectUri);
		println("</script>");
	}

	// 입력받은 변수(attrValue)를 attrName이름으로 세션에 저장하는 메서드
	public void setSessionAttr(String attrName, String attrValue) {
		req.getSession().setAttribute(attrName, attrValue);
	}

	// attrName이름의 세션을 삭제하는 메서드
	public void removeSessionAttr(String attrName) {
		req.getSession().removeAttribute(attrName);
	}

	// attrName이름의 세션을 불러오는 메서드
	public <T> T getSessionAttr(String attrName, T defaultValue) {
		if (req.getSession().getAttribute(attrName) == null) {
			return defaultValue;
		}

		return (T) req.getSession().getAttribute(attrName);
	}

	// uri가 controllerTypeName, controllerName, actionMethodName 에 저장된 값을 저장한 메서드
	public String getActionPath() {
		return "/" + controllerTypeName + "/" + controllerName + "/" + actionMethodName;
	}
	
	// req.setAttribute으로 저장한 attrName이름의 매개변수를 가져오는 메서드(문자)
	public String getAttr(String attrName, String defaultValue) {
		String attrValue = (String)req.getAttribute(attrName);

		if (attrValue == null) {
			return defaultValue;
		}

		return attrValue;
	}
	
	// req.setAttribute으로 저장한 attrName이름의 매개변수를 가져오는 메서드(정수)
	public int getIntAttr(String attrName, int defaultValue) {
		Integer attrValue = (Integer)req.getAttribute(attrName);

		if (attrValue == null) {
			return defaultValue;
		}
		
		return attrValue;
	}
	
	// map을 편하게 만들어주는 메서드
	private Map<String, Object> getParamMap() {
		Map<String, Object> params = new HashMap<>();

		// 요청 페이지의 모든 인자 이름이 저장된 목록을 반환
		Enumeration<String> parameterNames = req.getParameterNames();

		while (parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			Object paramValue = req.getParameter(paramName);

			params.put(paramName, paramValue);
		}

		return params;
	}

	// map으로 저장된 값을 json 형식으로 바꿔주는 메서드
	public String getParamMapJsonStr() {
		return Ut.toJson(getParamMap(), "");
	}
	
	// 자바스크립트 변수를 어디서든 쓸 수 있도록 해주는 메서드
	private Map<String, Object> getBaseTypeAttrMap() {
		Map<String, Object> attrs = new HashMap<>();

		Enumeration<String> attrNames = req.getAttributeNames();

		while (attrNames.hasMoreElements()) {
			String attrName = attrNames.nextElement();
			Object attrValue = req.getAttribute(attrName);

			if( attrName.equals("rq") ) {
				continue;
			}
			
			if( Ut.isBaseType(attrValue) == false) {
				continue;
			}
			attrs.put(attrName, attrValue);
		}

		return attrs;
	}

	public String getBaseTypeAttrs() {
		return Ut.toJson(getBaseTypeAttrMap(), "");
	}
	
	// 현재의 uri를 찾아주는 메서드
	public String getCurrentUri() {
		String uri = req.getRequestURI();
		String queryStr = req.getQueryString();
		
		if( queryStr != null && queryStr.length() > 0) {
			uri += "?" + queryStr;
		}
		
		return uri;
	}
	
	// 현재 페이지를 인코딩하는 메서드
	public String getEncodedCurrentUri() {
		return Ut.getUriEncoded(getCurrentUri());
	}
	
	// 이전 페이지를 인코딩하는 메서드
	public String getEncodedAfterLoginUri() {
		return Ut.getUriEncoded(getAfterLoginUri());
	}

	// 전 페이지 uri값을 저장해 리턴해주는 메서드
	public String getAfterLoginUri() {
		String afterLoginUri = getParam("afterLoginUri", "");
		
		if( afterLoginUri.length() > 0) {
			return afterLoginUri;
		}
		
		return getCurrentUri();
	}
	
	// 페이지에 오류를 확인하는 메서드
	public void debugParams() {
		print("<h1>debugParams</h1>");
		print("<pre>");
		print(Ut.toPrettyJson(getParamMap(), ""));
		print("</pre>");
	}

	// 관리자 인지 아닌지 확인하는 메서드
	public boolean isNotAdmin() {
		return !isAdmin;
	}
}
