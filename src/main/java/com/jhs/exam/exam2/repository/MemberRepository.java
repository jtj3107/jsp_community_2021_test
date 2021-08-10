package com.jhs.exam.exam2.repository;

import java.util.List;

import com.jhs.exam.exam2.container.ContainerComponent;
import com.jhs.exam.exam2.dto.Member;
import com.jhs.mysqliutil.MysqlUtil;
import com.jhs.mysqliutil.SecSql;

public class MemberRepository implements ContainerComponent {
	public void init() {

	}

	// 재구현 완료[2021-08-09]
	// 로그인아이디로 해당 member가 DB에 찾는 함수
	public Member getMemberByLoginId(String loginId) {
		SecSql sql = new SecSql();
		sql.append("select M.*");
		sql.append("FROM `member` AS M");
		sql.append("WHERE M.loginId = ?", loginId);

		return MysqlUtil.selectRow(sql, Member.class);
	}

	// 재구현 완료[2021-08-09]
	// 입력받은 변수를 이용 새로운 member를 DB에서 생성
	public int join(String loginId, String loginPw, String name, String nickname, String email, String cellphoneNo) {
		SecSql sql = new SecSql();
		sql.append("INSERT INTO `member`");
		sql.append("SET regDate = NOW()");
		sql.append(", updateDate = NOW()");
		sql.append(", loginId = ?", loginId);
		sql.append(", loginPw = ?", loginPw);
		sql.append(", name = ?", name);
		sql.append(", nickname = ?", nickname);
		sql.append(", email = ?", email);
		sql.append(", cellphoneNo = ?", cellphoneNo);

		return MysqlUtil.insert(sql);
	}

	// 재구현 완료[2021-08-09]
	// DB에서 members를 구해 리턴 함수
	public static List<Member> getForPrintMembers() {
		SecSql sql = new SecSql();
		sql.append("SELECT *");
		sql.append("FROM `member`");
		
		return MysqlUtil.selectRows(sql, Member.class);
	}

	// 재구현 완료[2021-08-09]
	// 해당 name과 email을 이용해 해당member를 DB에서 찾아 리턴하는 함수
	public Member getMemberByNameAndEmail(String name, String email) {
		SecSql sql = new SecSql();
		sql.append("SELECT M.*");
		sql.append("FROM `member` AS M");
		sql.append("WHERE M.name = ?", name);
		sql.append("AND M.email = ?", email);
		
		return MysqlUtil.selectRow(sql, Member.class);
	}

	// 재구현 완료[2021-08-09]
	// 해당 멤버 DB에서 비밀번호 변경하는 함수
	public void modifyPassword(int id, String loginPw) {
		SecSql sql = new SecSql();
		sql.append("UPDATE member AS M");
		sql.append("SET loginPw = ?", loginPw);
		sql.append("WHERE M.id = ?", id);
		
		MysqlUtil.update(sql);
	}

	// 재구현 완료[2021-08-09]
	// 해당 변수로 회원 정보를 수정하는 메서드
	public void modify(int id, String loginPw, String name, String nickname, String email, String cellphoneNo) {
		SecSql sql = new SecSql();
		sql.append("UPDATE member AS M");
		sql.append("SET loginPw = ?", loginPw);
		sql.append(", name = ?", name);
		sql.append(", nickname = ?", nickname);
		sql.append(", email = ?", email);
		sql.append(", cellphoneNo = ?", cellphoneNo);
		sql.append("WHERE M.id = ?", id);
		
		MysqlUtil.update(sql);

	}
}
