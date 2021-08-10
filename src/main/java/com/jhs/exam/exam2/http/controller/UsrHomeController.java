package com.jhs.exam.exam2.http.controller;

import com.jhs.exam.exam2.app.App;
import com.jhs.exam.exam2.container.Container;
import com.jhs.exam.exam2.http.Rq;
import com.jhs.exam.exam2.util.Ut;

public class UsrHomeController extends Controller {
	public void init() {
		
	}
	
	@Override
	public void performAction(Rq rq) {
		switch (rq.getActionMethodName()) {
		case "main":
			actionShowMain(rq);
			break;
		case "doSendMail":
			actionDoSendMail(rq);
			break;
		default:
			rq.println("존재하지 않는 페이지 입니다.");
			break;
		}
	}

	// 해당 페이지로 이동하는 메서드
	private void actionShowMain(Rq rq) {
		rq.jsp("usr/home/main");
	}

	// 메일발송하는 메서드
	private void actionDoSendMail(Rq rq) {
		App app = Container.app;
		Ut.sendMail(app.getSmtpGmailId(), app.getSmtpGmailPw(), "no-reply@lemon-cm.com", "레몬 커뮤니티 알림", "jtj3926@gmail.com", "제목ㅋㅋㅋ", "내용 ㅋㅋㅋ");	
	}

}
