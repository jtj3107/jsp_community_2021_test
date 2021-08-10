<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="pageTitle" value="회원수정" />
<%@ include file="../part/head.jspf"%>

<section class="section section-member-modify flex-grow flex justify-center items-center">
	<div class="w-full max-w-md card-wrap">
		<div class="card bordered shadow-lg">
			<div class="card-title">
				<span>
					<i class="fas fa-user-plus"></i>
				</span>
				<span>회원수정</span>
			</div>

			<div class="px-4 py-4">
				<script>
					let MemberModify__submitDone = false;
					function MemberModify__submit(form) {
						if (MemberModify__submitDone) {
							return;
						}

						// 좌우 공백제거
						form.currentloginPw.value = form.currentloginPw.value.trim();

						if (form.currentloginPw.value.length == 0) {
							alert('현재 로그인 비밀번호를 입력해주세요.');
							form.currentloginPw.focus();

							return;
						}
										
						form.loginPw.value = form.loginPw.value.trim();

						if (form.loginPw.value.length == 0) {
							alert('변경할 로그인 비밀번호를 입력해주세요.');
							form.loginPw.focus();

							return;
						}
						
						form.loginPwConfirm.value = form.loginPwConfirm.value.trim();
						
						if (form.loginPwConfirm.value.length == 0) {
							alert('변경할 로그인 비밀번호 확인을 입력해주세요.');
							form.loginPwConfirm.focus();

							return;
						}
						
						if (form.loginPw.value != form.loginPwConfirm.value) {
							alert('로그인 비밀번호 확인이 일치하지 않습니다.');
							form.loginPwConfirm.focus();

							return;
						}
						
						form.name.value = form.name.value.trim();
						
						if (form.name.value.length == 0) {
							alert('이름을 입력해주세요.');
							form.name.focus();

							return;
						}
						
						form.nickname.value = form.nickname.value.trim();
						
						if (form.nickname.value.length == 0) {
							alert('닉네임을 입력해주세요.');
							form.nickname.focus();

							return;
						}
						
						form.email.value = form.email.value.trim();
						
						if (form.email.value.length == 0) {
							alert('이메일을 입력해주세요.');
							form.email.focus();

							return;
						}
						
						form.cellphoneNo.value = form.cellphoneNo.value.trim();
						
						if (form.cellphoneNo.value.length == 0) {
							alert('전화번호를 입력해주세요.');
							form.cellphoneNo.focus();

							return;
						}
												
						form.submit();
						MemberModify__submitDone = true;
					}
				</script>
				<form action="../member/doMemberModify" method="POST" onsubmit="MemberModify__submit(this); return false;">
					<input type="hidden" name="redirectUri" value="${param.afterLoginUri}" />
					
					<div class="form-control">
						<label class="label">
							<span class="label-text">현재 로그인 비밀번호</span>
						</label>
						<div>
							<input class="input input-bordered w-full" maxlength="100" name="currentloginPw" type="password" placeholder="현재 로그인 비밀번호를 입력해주세요." />
						</div>
					</div>
					
					<div class="form-control">
						<label class="label">
							<span class="label-text">로그인 비밀번호</span>
						</label>
						<div>
							<input class="input input-bordered w-full" maxlength="100" name="loginPw" type="password" placeholder="변경할 로그인 비밀번호를 입력해주세요." />
						</div>
					</div>
					
					<div class="form-control">
						<label class="label">
							<span class="label-text">로그인 비밀번호 확인</span>
						</label>
						<div>
							<input class="input input-bordered w-full" maxlength="100" name="loginPwConfirm" type="password" placeholder="변경할 로그인비밀번호 확인을 입력해주세요." />
						</div>
					</div>
					
					<div class="form-control">
						<label class="label">
							<span class="label-text">사용자 이름</span>
						</label>
						<div>
							<input class="input input-bordered w-full" maxlength="100" name="name" type="text" placeholder="이름을 입력해주세요." value="${member.name}" />
						</div>
					</div>
					
					<div class="form-control">
						<label class="label">
							<span class="label-text">사용자 닉네임</span>
						</label>
						<div>
							<input class="input input-bordered w-full" maxlength="100" name="nickname" type="text" placeholder="변경할 닉네임을 입력해주세요." value="${member.nickname}"/>
						</div>
					</div>
					
					<div class="form-control">
						<label class="label">
							<span class="label-text">사용자 이메일</span>
						</label>
						<div>
							<input class="input input-bordered w-full" maxlength="100" name="email" type="email" placeholder="변경할 이메일을 입력해주세요." value="${member.email}" />
						</div>
					</div>
					
					<div class="form-control">
						<label class="label">
							<span class="label-text">사용자 전화번호</span>
						</label>
						<div>
							<input class="input input-bordered w-full" maxlength="100" name="cellphoneNo" type="tel" placeholder="변경할 휴대전화번호를 입력해주세요." value="${member.cellphoneNo}" />
						</div>
					</div>

					<div class="btns">
						<button type="submit" class="btn btn-link">회원정보수정</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</section>
<%@ include file="../part/foot.jspf"%>