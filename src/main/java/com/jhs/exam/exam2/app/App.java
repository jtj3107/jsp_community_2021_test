package com.jhs.exam.exam2.app;

import com.jhs.exam.exam2.container.Container;
import com.jhs.exam.exam2.container.ContainerComponent;
import com.jhs.exam.exam2.util.Ut;
import com.jhs.mysqliutil.MysqlUtil;

import lombok.Getter;

public class App implements ContainerComponent {
	@Getter
	private boolean ready = false;
	private String smtpGmailPw;

	@Override
	public void init() {
		smtpGmailPw = Ut.getFileContents("c:/work/jtj/SmtpGmailPw.txt");

		if (smtpGmailPw != null && smtpGmailPw.trim().length() > 0) {
			ready = true;
		}
	}

	public static boolean isDevMode() {
		// 이 부분을 false로 바꾸면 production 모드 이다.
		return true;
	}

	public static boolean isProductMode() {
		return isDevMode() == false;
	}

	// 정적 요소 세팅
	public static void start() {
		// DB 세팅
		MysqlUtil.setDBInfo("localhost", "geotjeoli", "gjl123414", "jsp_board");
		MysqlUtil.setDevMode(isDevMode());

		// 공용 객체 세팅
		Container.init();

	}

	public String getSiteName() {
		return "레몬 커뮤니티";
	}

	public String getSmtpGmailId() {
		return "jtj3926@gmail.com";
	}

	public String getSmtpGmailPw() {
		return smtpGmailPw;
	}

	public String getLoginUri() {
		return getBaseUri() + "/usr/member/login";
	}

	private String getBaseUri() {
		String appUri = getSiteProtocol() + "://" + getSiteDomain();

		if (getSitePort() != 80 && getSitePort() != 443) {
			appUri += ":" + getSitePort();
		}

		if (getContextName().length() > 0) {
			appUri += "/" + getContextName();
		}

		return appUri;
	}

	private String getContextName() {
		if (isProductMode()) {
			return "";
		}

		return "jsp_community_2021";
	}

	private int getSitePort() {
		return 8080;
	}

	private String getSiteDomain() {
		return "localhost";
	}

	private String getSiteProtocol() {
		if (isProductMode()) {
			return "https";
		}

		return "http";
	}

	public String getNotifyEmailFromName() {
		return "레몬 커뮤니티 알림봇";
	}
}
