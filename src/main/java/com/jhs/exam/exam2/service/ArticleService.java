package com.jhs.exam.exam2.service;

import java.util.List;

import com.jhs.exam.exam2.container.Container;
import com.jhs.exam.exam2.container.ContainerComponent;
import com.jhs.exam.exam2.dto.Article;
import com.jhs.exam.exam2.dto.Member;
import com.jhs.exam.exam2.dto.ResultData;
import com.jhs.exam.exam2.repository.ArticleRepository;
import com.jhs.exam.exam2.util.Ut;
import com.mysql.cj.x.protobuf.MysqlxCrud.Update;

public class ArticleService implements ContainerComponent {
	private ArticleRepository articleRepository;

	public void init() {
		articleRepository = Container.articleRepository;
	}

	// 재구현 완료[2021-08-10]
	public ResultData write(int boardId, int memberId, String title, String body) {
		// 받아온 변수를 이용해 게시물 작성하는 함수 후에 해당 게시물번호를 리턴
		int id = articleRepository.write(boardId, memberId, title, body);

		// S-1, 완료 메세지, id값을 리턴
		return ResultData.from("S-1", Ut.f("`%d`번 게시물이 작성되었습니다.", id), "id", id);
	}

	// 재구현 완료[2021-08-10]
	// 해당 변수를 받아 요구에 맞는 게시물리스트르 리턴하는 메서드
	public List<Article> getForPrintArticles(Member actor, int itemsCountInAPage, int page,
			String searchKeywordTypeCode, String searchKeyword, int boardId) {
		int limitPage = (page - 1) * itemsCountInAPage;
		int limitTake = itemsCountInAPage;

		// 요구에 맞는 게시물 리스트 변수에 저장
		List<Article> articles = articleRepository.getForPrintArticles(limitPage, limitTake, searchKeywordTypeCode,
				searchKeyword, boardId);

		// 각 게시물마다 권한이 있는 member에 수정,삭제 버튼 보이게 해주는 메서드
		for (Article article : articles) {
			updateForPrintData(actor, article);
		}

		return articles;
	}

	// 재구현 완료[2021-08-10]
	// 해당 id의 게시물을 찾는 메서드
	public Article getForPrintArticleById(Member actor, int id) {
		// 해당 게시물 id로 해당 게시물 불러오기
		Article article = articleRepository.getForPrintArticleById(id);

		// 접속한 멤버와 게시물로 수정,삭제 여부 판단해주는 함수
		updateForPrintData(actor, article);

		// 게시물 리턴
		return article;
	}

	private void updateForPrintData(Member actor, Article article) {
		// 멤버가 존재 하지 않으면 실행하지 않고 리턴
		if (actor == null) {
			return;
		}

		// article이 존재 하지 않으면 실행하지 않고 리턴
		if (article == null) {
			return;
		}

		// 접속한 멤버와 게시물을 비교하여 수정,삭제 true,false여부 판단(F-로 시작시 false S-로 시작시 true)
		boolean actorCanModify = actorCanModify(actor, article).isSuccess();
		boolean actorCanDelete = actorCanDelete(actor, article).isSuccess();

		// article변수에 해당 게시물의 작성자가 접속한 멤버일시 true저장 아닐시 false저장
		article.setExtra__actorCanModify(actorCanModify);
		article.setExtra__actorCanDelete(actorCanDelete);

	}

	// 재구현 완료[2021-08-10]
	// id번 게시물을 삭제하는 메서드
	public ResultData delete(int id) {
		articleRepository.delete(id);

		// 삭제후 S-1, 메세지, id값 저장후 리턴
		return ResultData.from("S-1", Ut.f("`%d`번 게시물이 삭제되었습니다.", id));
	}

	// 재구현 완료[2021-08-10]
	// 게시물을 수정하는 메서드
	public ResultData modify(int id, String title, String body) {
		articleRepository.modify(id, title, body);

		return ResultData.from("S-1", Ut.f("`%d`번 게시물이 수정되었습니다.", id));
	}

	// 재구현 완료[2021-08-10]
	// 접속한 회원이 해당 게시물 수정 권한이 있는지 알려주는 메서드
	public ResultData actorCanModify(Member member, Article article) {
		// 접속한 멤버의 id와 게시물에 저장된 작성자(memberId)를 저장
		int memberId = member.getId();
		int writerMemberId = article.getMemberId();

		// 접속하 멤버가 관리자이면 S-0 저장후 리턴
		if (member.getAuthLevel() == 7) {
			return ResultData.from("S-0", "관리자 권한으로 수정합니다.");
		}

		// memberId와 writerMemberId가 다를시 F-1저장 후 리턴
		if (memberId != writerMemberId) {
			return ResultData.from("F-1", "권한이 없습니다.");
		}

		// memberId와 writerMemberId가 같을시 S-1 저장후 리턴
		return ResultData.from("S-1", "수정 가능합니다.");
	}

	// 재구현 완료[2021-08-10]
	// 접속한 회원이 해당 게시물 삭제 권한이 있는지 알려주는 메서드
	public ResultData actorCanDelete(Member member, Article article) {
		// 접속한 member의 id와 게시물 작성자(memberId)를 변수에 저장
		int memberId = member.getId();
		int writerMemberId = article.getMemberId();

		// 접속하 멤버가 관리자이면 S-0 저장후 리턴
		if (member.getAuthLevel() == 7) {
			return ResultData.from("S-0", "관리자 권한으로 삭제 합니다.");
		}

		// memberId와 writerMemberId가 다를시 F-1저장후 리턴
		if (memberId != writerMemberId) {
			return ResultData.from("F-1", "권한이 없습니다.");
		}

		// memberId와 writerMemberId가 같을시 S-1저장후 리턴
		return ResultData.from("S-1", "삭제 가능합니다.");
	}

	// 재구현 완료[2021-08-10]
	// 해당 변수에 일치하는 게시물의 수를 리턴하는 메서드
	public int getArticlesCount(String searchKeywordTypeCode, String searchKeyword, int boardId) {
		// 해당 변수를 이용하여 게시물수를 구한뒤 리턴
		return articleRepository.getArticlesCount(searchKeywordTypeCode, searchKeyword, boardId);
	}

}
