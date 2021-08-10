package com.jhs.exam.exam2.http.controller;

import com.jhs.exam.exam2.container.Container;
import com.jhs.exam.exam2.dto.Member;
import com.jhs.exam.exam2.dto.ResultData;
import com.jhs.exam.exam2.http.Rq;
import com.jhs.exam.exam2.service.MemberService;
import com.jhs.exam.exam2.util.Ut;

public class UsrMemberController extends Controller {
	// memberService객체를 Container에서 받아온다
	private MemberService memberService;

	public void init() {
		memberService = Container.memberService;
	}

	@Override
	public void performAction(Rq rq) {
		// uri에 ActionMethodName이 아래 case에 해당하는지 확인
		switch (rq.getActionMethodName()) {
		// 해당 함수 실행
		case "login":
			actionShowLogin(rq);
			break;
		case "doLogin":
			actionDoLogin(rq);
			break;
		case "doLogout":
			actionDoLogout(rq);
			break;
		case "join":
			actionShowJoin(rq);
			break;
		case "doJoin":
			actionDoJoin(rq);
			break;
		case "findLoginId":
			actionShowFindLoginId(rq);
			break;
		case "doFindLoginId":
			actionDoFindLoginId(rq);
			break;
		case "findLoginPw":
			actionShowFindLoginPw(rq);
			break;
		case "doFindLoginPw":
			actionDoFindLoginPw(rq);
			break;
		case "memberModify":
			actionShowMemberModify(rq);
			break;
		case "doMemberModify":
			actionDoMemberModify(rq);
			break;
		// 없을시 메세지 출력후 break;
		default:
			rq.println("존재하지 않는 페이지 입니다.");
			break;
		}
	}

	// 회원정보 수정 페이지에서 받은 파라미터로 회원정보를 수정하는 메서드
	private void actionDoMemberModify(Rq rq) {
		// 회원정보 수정 페이지에서 받은 파라미터를 각 변수에 담는다
		String loginPw = rq.getParam("loginPw", "");
		String name = rq.getParam("name", "");
		String nickname = rq.getParam("nickname", "");
		String email = rq.getParam("email", "");
		String cellphoneNo = rq.getParam("cellphoneNo", "");
		
		// 접속한 회원의 member 값을 저장
		Member member = rq.getLoginedMember();
		String currentloginPw = rq.getParam("currentloginPw", "");
		
		// 입력한 현재 비밀번호와 로그인한 회원의 비밀번호가 다를경우 뒤로가기
		if(member.getLoginPw().equals(Ut.sha256(currentloginPw)) == false) {
			rq.historyBack("현재 비밀번호가 틀렸습니다.");
			return;
		}

		// 각 파라미터가 값이 없이 해당 메서드 접근시 리턴
		if (loginPw.length() == 0) {
			rq.historyBack("loginPw를 입력해주세요.");
			return;
		}

		if (name.length() == 0) {
			rq.historyBack("name를 입력해주세요.");
			return;
		}

		if (nickname.length() == 0) {
			rq.historyBack("nickname를 입력해주세요.");
			return;
		}
		if (email.length() == 0) {
			rq.historyBack("email를 입력해주세요.");
			return;
		}

		if (cellphoneNo.length() == 0) {
			rq.historyBack("cellphoneNo를 입력해주세요.");
			return;
		}

		// 접속한 회원의 정보를 수정 해주는 메서드
		ResultData modifyRd = memberService.modify(member, loginPw, name, nickname, email, cellphoneNo);

		// modifyRd 값이 F-로 시작시 오류메세지 출력후 뒤로가기
		if (modifyRd.isFail()) {
			rq.historyBack(modifyRd.getMsg());
			return;
		}

		// 회원정보 수정페이지에서 redirectUri파라미터 값을 저장합니다 없을시 ../home/main 저장
		String redirectUri = rq.getParam("redirectUri", "../home/main");

		// 성공메세지 출력후 저장된 redirectUri 페이지로 이동
		rq.replace(modifyRd.getMsg(), redirectUri);
	}

	// 회원정보 수정 페이지로 이동하는 메서드
	private void actionShowMemberModify(Rq rq) {
		Member member = rq.getLoginedMember();

		rq.setAttr("member", member);

		rq.jsp("usr/member/memberModify");
	}

	// 재구현 완료[2021-08-04]
	// 비밀번호 찾기 페이지에서 이동
	// 비밀번호 찾기 메서드
	private void actionDoFindLoginPw(Rq rq) {
		// 비밀번호 찾기 페이지에서 입력한 loginId와 email을 받아 변수에 저장
		String loginId = rq.getParam("loginId", "");
		String email = rq.getParam("email", "");

		// 비정상적으로 loginId와 email이 없을 경우 메세지 출력후 뒤로가기
		if (loginId.length() == 0) {
			rq.historyBack("loginId를 입력해주세요.");
			return;
		}

		if (email.length() == 0) {
			rq.historyBack("email를 입력해주세요.");
			return;
		}

		// loginId로 해당 member 찾는 메서드
		Member member = memberService.getMemberByLoginId(loginId);

		// member가 존재하지 않을시 메세지 출력후 뒤로가기
		if (member == null) {
			rq.historyBack("존재하지 않는 회원입니다.");
			return;
		}

		// member의 이메일가 입력한 이메일이 틀릴시 메세지 출력후 뒤로가기
		if (member.getEmail().equals(email) == false) {
			rq.historyBack("입력하신 이메일과 회원님의 이메일이 일치하지 않습니다.");
			return;
		}

		// 해당 member의 비밀번호를 임시비밀번호로 바꾸고 해당 이메일로 보내주는 메서드
		ResultData sendTempLoginPwToEmailRd = memberService.sendTempLoginPwToEmail(member);

		// sendTempLoginPwToEmailRd 변수가 F-로 시작할시 메세지 출력후 뒤로가기
		if (sendTempLoginPwToEmailRd.isFail()) {
			rq.historyBack(sendTempLoginPwToEmailRd.getMsg());
			return;
		}

		// sendTempLoginPwToEmailRd 변수가 S-로 시작할시 메세지 출력후 메인 페이지로 이동하는 메서드
		rq.replace(sendTempLoginPwToEmailRd.getMsg(), "../home/main");
	}

	// 비밀번호 찾기 페이지로 이동
	private void actionShowFindLoginPw(Rq rq) {
		rq.jsp("usr/member/findLoginPw");
	}
	
	// 재구현 완료[2021-08-06]
	// 로그인아이디 찾기 함수(로그인아이디 찾기 페이지에서 이동)
	private void actionDoFindLoginId(Rq rq) {
		// 로그인 아이디 찾기 페이지에서 받은 파라미터 값을 변수에 저장
		String name = rq.getParam("name", "");
		String email = rq.getParam("email", "");
		
		// 피라미터 값이 없을경우 메세지 출력후 뒤로가기
		if(name.length() == 0) {
			rq.historyBack("name을 입력해주세요.");
			return;
		}
		
		if(email.length() == 0) {
			rq.historyBack("email를 입력해주세요.");
			return;
		}
		
		// 해당 변수에 일치하는 회원을 찾아주는 메서드
		Member oldMember = memberService.getMemberByNameAndEmail(name, email);
		
		// 일치하는 회원이 없을 경우 메세지 출력후 뒤로가기
		if(oldMember == null) {
			rq.historyBack("존재하지 않는 회원정보 입니다.");
			return;
		}
		
		// 이동할 페이지를 로그인아이디를 포함해 저장
		String redirectUri = "../member/login?loginId=" + oldMember.getLoginId();
		
		// 찾은 로그인 아이디를 보여주고 페이지 이동
		rq.replace(Ut.f("회원님의 로그인 아이디는 `%s` 입니다.", oldMember.getLoginId()), redirectUri);
		
	}

	// 로그인아이디 찾기 페이지로 이동하는 함수
	private void actionShowFindLoginId(Rq rq) {
		rq.jsp("usr/member/findLoginId");
	}

	// 재구현 완료[2021-08-06]
	// 회원가입 함수(회원가입 페이지에서 연결)
	private void actionDoJoin(Rq rq) {
		// 회원가입 페이지에서 받아온 파리미터를 변수에 저장
		String loginId = rq.getParam("loginId", "");
		String loginPw = rq.getParam("loginPw", "");
		String name = rq.getParam("name", "");
		String nickname = rq.getParam("nickname", "");
		String email = rq.getParam("email", "");
		String cellphoneNo = rq.getParam("cellphoneNo", "");
		
		// 파라미터 값이 없을 경우 메세지 출력후 뒤로가기
		if(loginId.length() == 0) {
			rq.historyBack("loginId를 입력해주세요.");
			return;
		}
		
		if(loginPw.length() == 0) {
			rq.historyBack("loginPw를 입력해주세요.");
			return;
		}
		
		if(name.length() == 0) {
			rq.historyBack("name를 입력해주세요.");
			return;
		}
		
		if(nickname.length() == 0) {
			rq.historyBack("nickname를 입력해주세요.");
			return;
		}
		
		if(email.length() == 0) {
			rq.historyBack("email를 입력해주세요.");
			return;
		}
		
		if(cellphoneNo.length() == 0) {
			rq.historyBack("cellphoneNo를 입력해주세요.");
			return;
		}
		
		// 저장한 변수를 이용하여 회원가입하는 메서드
		ResultData joinRd = memberService.join(loginId, loginPw, name, nickname, email, cellphoneNo);
		
		// joinRd값이 F-로 시작시 메세지 출력후 뒤로가기
		if(joinRd.isFail()) {
			rq.historyBack(joinRd.getMsg());
			return;
		}
		
		// 메세지 출력후 해당 페이지로 이동
		rq.replace(joinRd.getMsg(), "../member/login");
	
	}

	// 회원가입 페이지로 이동하는 메서드
	private void actionShowJoin(Rq rq) {
		rq.jsp("usr/member/join");
	}

	// 재구현 완료[2021-08-06]
	// 로그아웃 메서드
	private void actionDoLogout(Rq rq) {
		// 로그인된 json 형식의 로그인멤버를 삭제
		rq.removeSessionAttr("loginedMemberJson");
		rq.replace("로그아웃 되었습니다.", "../../");
	}

	// 재구현 완료[2021-08-06]
	// 로그인 페이지에서 연결
	// 로그인 가능 여부를 판별하는 메서드
	private void actionDoLogin(Rq rq) {
		// 로그인 페이지에서 받아온 파라미터를 해당 변수에 저장
		String loginId = rq.getParam("loginId", "");
		String loginPw = rq.getParam("loginPw", "");
		
		// 파라미터 값이 없을 경우 메세지 출력 후 뒤로가기
		if(loginId.length() == 0) {
			rq.historyBack("loginId를 입력해주세요.");
			return;
		}
		
		if(loginPw.length() == 0) {
			rq.historyBack("loginPw를 입력해주세요.");
			return;
		}
		
		// 해당 변수를 이용하여 로그인 여부 확인하는 메서드
		ResultData loginRd = memberService.login(loginId, loginPw);
		
		// loginRd값이 F-로 시작시 메세지 출력후 뒤로가기
		if(loginRd.isFail()) {
			rq.historyBack(loginRd.getMsg());
			return;
		}
		
		// loginRd의 body에 있는 member을 가져와 저장
		Member member = (Member)loginRd.getBody().get("member");
		
		// member값을 loginedMemberJson라는 이름으로 json형식으로 저장
		rq.setSessionAttr("loginedMemberJson", Ut.toJson(member, ""));
		
		// 로그인 정보 입력 페이지에서 redirectUri의 값을 받아 저장 없을시 ../home/main 저장
		String redirectUri = rq.getParam("redirectUri", "../home/main");
		
		// 성공메세지 출력후 해당 페이지로 이동
		rq.replace(loginRd.getMsg(), redirectUri);
	}

	// 로그인 페이지로 이동 하는 함수
	private void actionShowLogin(Rq rq) {
		rq.jsp("usr/member/login");
	}
}
