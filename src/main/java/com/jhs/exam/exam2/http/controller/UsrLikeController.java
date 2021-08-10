package com.jhs.exam.exam2.http.controller;

import com.jhs.exam.exam2.http.Rq;

public class UsrLikeController extends Controller {
	public void init() {

	}

	@Override
	public void performAction(Rq rq) {
		switch (rq.getActionMethodName()) {
		case "doLike":
			actionDoLike(rq);
			break;
//		case "doDisLike":
//			actionDoDisLike(rq);
//			break;
		default:
			rq.println("존재하지 않는 페이지 입니다.");
			break;
		}
	}

	private void actionDoLike(Rq rq) {
		String relTypeCode = rq.getParam("relTypeCode", "");
		int relId = rq.getIntParam("relId", 0);
		String redirectUrl = rq.getParam("redirectUrl", "../article/detail?id=" + relId);

		rq.print(relTypeCode);
		rq.print(relId + "");
		rq.print(redirectUrl);
	}

}
