package com.jhs.exam.exam2.service;

import java.util.List;

import com.jhs.exam.exam2.app.App;
import com.jhs.exam.exam2.container.Container;
import com.jhs.exam.exam2.container.ContainerComponent;
import com.jhs.exam.exam2.dto.Member;
import com.jhs.exam.exam2.dto.ResultData;
import com.jhs.exam.exam2.repository.MemberRepository;
import com.jhs.exam.exam2.util.Ut;

public class MemberService implements ContainerComponent {
	private MemberRepository memberRepository;
	private EmailService emailService;

	public void init() {
		memberRepository = Container.memberRepository;
		emailService = Container.emailService;
	}

	// 재구현 완료[2021-08-09]
	// 로그인 여부를 확인하는 메서드
	public ResultData login(String loginId, String loginPw) {
		// loginId를 이용하여 member값을 구한다
		Member oldMember = memberRepository.getMemberByLoginId(loginId);

		// member값이 존재하지 않을 시 F-1 리턴
		if (oldMember == null) {
			return ResultData.from("F-1", "존재하지 않는 회원입니다.");
		}

		// 찾은 member의 loginPw값과 입력받은 loginPw와 비교하여 틀릴시 F-2 리턴
		if (oldMember.getLoginPw().equals(Ut.sha256(loginPw)) == false) {
			return ResultData.from("F-2", "비밀번호가 틀렸습니다.");
		}

		// 구한 member값을 저장하고 S-1 리턴
		return ResultData.from("S-1", "환영합니다.", "member", oldMember);
	}

	// 재구현 완료[2021-08-09]
	// 회원가입 함수
	public ResultData join(String loginId, String loginPw, String name, String nickname, String email,
			String cellphoneNo) {
		// loginId를 이용하여 member값을 구한다
		Member oldMember = memberRepository.getMemberByLoginId(loginId);

		// member값이 존재 하면 F-1 저장후 리턴
		if (oldMember != null) {
			return ResultData.from("F-1", Ut.f("`%s` 아이디는 이미 사용중 입니다.", loginId));
		}

		// name과 email를 통하여 member값을 구해온다
		oldMember = memberRepository.getMemberByNameAndEmail(name, email);

		// member값이 존재 하면 F-2 저장후 리턴
		if (oldMember != null) {
			return ResultData.from("F-2", Ut.f("`%s`님은 이메일 주소 `%s`를 사용하는 회원이 존재합니다.", name, email));
		}

		// 해당 변수를 이용하여 member를 만들어주는 함수
		memberRepository.join(loginId, Ut.sha256(loginPw), name, nickname, email, cellphoneNo);

		// S-1저장후 리턴
		return ResultData.from("S-1", "회원 가입이 완료 되었습니다.");
	}

	// members를 구하는 함수
	public List<Member> getForPrintMembers() {
		return memberRepository.getForPrintMembers();
	}

	// 재구현 완료[2021-08-09]
	// 메일을 발송하는 함수
	// body += "<a href=\"" + siteLoginUri + "\" target=\"_blank\">로그인 하러가기</a>";
	public ResultData sendTempLoginPwToEmail(Member actor) {
		App app = Container.app;
		// 메일 제목과 내용 만들기
		String siteName = app.getSiteName(); // 사이트 이름 리턴하는 함수
		String siteLoginUri = app.getLoginUri();
		String title = "[" + siteName + "] 임시 패스워드 발송"; // 이메일 제목
		String tempPassword = Ut.getTempPassword(6); // 임시 비밀번호 저장
		String body = "<h1>임시 패스워드 : " + tempPassword + "<h1>"; // 내용
		// 내용 + 해당 사이트 로그인페이지로 이동하는 a태크 생성
		body += "<a href=\"" + siteLoginUri + "\" target=\"_blank\">로그인 하러가기</a>";

		// 해당 member의 이메일이 존재하지 않을시 F-0 저장후 리턴
		if(actor.getEmail().length() == 0) {
			return ResultData.from("F-0", "해당 회원의 이메일이 없습니다.");
		}

		// 메일을 발송해주는 함수
		// 만약 notifyRs 값이 1이 아니면 메일 발송에 실패 한것
		int notifyRs = emailService.notify(actor.getEmail(), title, body);

		// notifyRs가 1이 아니면 F-1 저장후 리턴
		if(notifyRs != 1) {
			return ResultData.from("F-1", "이메일 발송이 실패하였습니다.");
		}

		// 해당 member의 비밀번호를 변경하는 메서드
		setTempLoginPw(actor, tempPassword);

		// 메일발송, 비밀번호 변경이 완료 되면 S-1, 완료 메세지 저장후 리턴
		return ResultData.from("S-1", Ut.f("임시 비밀번호를 `%s`로 발송 하였습니다.", actor.getEmail()));
	}
 
	// 재구현 완료[2021-08-09]
	// 해당 회원의 정보를 수정해주는 메서드
	public ResultData modify(Member member, String loginPw, String name, String nickname, String email,
			String cellphoneNo) {
		// 해당 member의 id값을 불러옴
		int id = member.getId();

		// name과 email이 회원정보와 하나라도 다를시 
		if(member.getName().equals(name) == false || member.getEmail().equals(email) == false) {
			// name과 email를 통하여 member값을 구해온다
			Member oldMeber = getMemberByNameAndEmail(name, email);
			
			// oldMeber값이 존재 할시 F-1, 오류메세지 저장후 리턴
			if(oldMeber != null) {
				return ResultData.from("F-1", Ut.f("`%s`님은 이메일 주소 `%s`를 사용하는 회원이 존재합니다.", name, email));
			}
		}

		// 회원의 정보를 수정해주는 메서드
		memberRepository.modify(id, Ut.sha256(loginPw), name, nickname, email, cellphoneNo);
		
		// S-1, 완료메세지 저장후 리턴
		return ResultData.from("S-1", "회원정보 수정이 완료되었습니다.");
	}
	
	// DB에 접근하여 해당 멤버 비밀번호 변경하는 함수
	private void setTempLoginPw(Member actor, String tempLoginPw) {
		memberRepository.modifyPassword(actor.getId(), Ut.sha256(tempLoginPw));
	}

	// loginId로 해당 member값을 불러와 리턴하는 함수
	public Member getMemberByLoginId(String loginId) {
		return memberRepository.getMemberByLoginId(loginId);
	}

	// name, email로 해당 member값을 불러와 리턴하는 함수
	public Member getMemberByNameAndEmail(String name, String email) {
		return memberRepository.getMemberByNameAndEmail(name, email);
	}

	// 해당 member가 관리자인지 아닌지 판별하는 함수
	public boolean isAdmin(Member member) {
		return member.getAuthLevel() >= 7;
	}

}
