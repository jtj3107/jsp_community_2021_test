package com.jhs.exam.exam2.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jhs.exam.exam2.app.App;
import com.jhs.exam.exam2.container.Container;
import com.jhs.exam.exam2.container.ContainerComponent;
import com.jhs.exam.exam2.util.Ut;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EmailService implements ContainerComponent{
	public void init() {
	}

	// 메일 발송하는 메서드
	public int notify(String to, String title, String body) {
		App app = Container.app;
		String smtpGmailId = app.getSmtpGmailId();
		String smtpGmailPw = app.getSmtpGmailPw();
		String from = "no-reply@lemon-cm.com";
		String fromName = app.getNotifyEmailFromName();
		
		return Ut.sendMail(smtpGmailId, smtpGmailPw, from, fromName, to, title, body);
	}
}
