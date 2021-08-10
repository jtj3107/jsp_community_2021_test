package com.jhs.exam.exam2.repository;

import java.util.List;

import com.jhs.exam.exam2.container.ContainerComponent;
import com.jhs.exam.exam2.dto.Article;
import com.jhs.mysqliutil.MysqlUtil;
import com.jhs.mysqliutil.SecSql;

public class ArticleRepository implements ContainerComponent {
	public void init() {

	}

	// 재구현 완료[2021-08-10]
	// 게시물을 해당 변수에 맞게 DB에 저장후 해당 게시물 번호를 리턴
	public int write(int boardId, int memberId, String title, String body) {
		SecSql sql = new SecSql();
		sql.append("INSERT INTO article");
		sql.append("SET regDate = NOW()");
		sql.append(", updateDate = NOW()");
		sql.append(", boardId = ?", boardId);
		sql.append(", memberId = ?", memberId);
		sql.append(", title = ?", title);
		sql.append(", body = ?", body);

		return MysqlUtil.insert(sql);
	}

	// 재구현 완료[2021-08-10]
	// 해당 변수에 맞는 게시물을 DB에서 찾아 리턴하는 함수
	public List<Article> getForPrintArticles(int limitPage, int limitTake, String searchKeywordTypeCode,
			String searchKeyword, int boardId) {
		SecSql sql = new SecSql();
		sql.append("SELECT A.*");
		sql.append(", IFNULL(M.nickname, '삭제된회원') AS extra__writerName");
		sql.append(", B.name AS extra_boardName");
		sql.append("FROM article AS A");
		sql.append("LEFT JOIN `member` AS M");
		sql.append("ON A.memberId = M.id");
		sql.append("INNER JOIN board AS B");
		sql.append("ON A.boardId = B.id");
		sql.append("WHERE 1");
		// searchKeyword이 null이 거나 없을 경우 동작X
		if (searchKeyword != null && searchKeyword.length() > 0) {
			switch (searchKeywordTypeCode) {
			case "body":
				sql.append("AND A.body LIKE CONCAT('%', ?, '%')", searchKeyword);
				break;
			case "title,body":
				sql.append("AND (");
				sql.append("A.body LIKE CONCAT('%', ?, '%')", searchKeyword);
				sql.append("OR");
				sql.append("A.title LIKE CONCAT('%', ?, '%')", searchKeyword);
				sql.append(")");
				break;
			case "title":
			default:
				sql.append("AND A.title LIKE CONCAT('%', ?, '%')", searchKeyword);
				break;
			}
		}
		// 게시판 번호가 0일시 동작X
		if (boardId != 0) {
			sql.append("AND A.boardId = ?", boardId);
		}
		sql.append("ORDER BY A.id DESC");
		sql.append("LIMIT ?, ?", limitPage, limitTake);

		return MysqlUtil.selectRows(sql, Article.class);

	}

	// 재구현 완료[2021-08-10]
	// id번 게시물을 DB에서 찾아 리턴하는 함수
	public Article getForPrintArticleById(int id) {
		SecSql sql = new SecSql();
		sql.append("SELECT *");
		sql.append(", IFNULL(M.nickname, '삭제된회원') AS extra__writerName");
		sql.append("FROM article AS A");
		sql.append("LEFT JOIN `member` AS M");
		sql.append("ON A.memberId = M.id");
		sql.append("WHERE A.id = ?", id);

		return MysqlUtil.selectRow(sql, Article.class);
	}

	// 해당 id의 게시물을 DB에서 삭제
	public int delete(int id) {
		SecSql sql = new SecSql();
		sql.append("DELETE FROM article");
		sql.append("WHERE id = ?", id);

		return MysqlUtil.delete(sql);
	}

	// 재구현 완료[2021-08-10]
	// 게시물을 수정하는 메서드
	public int modify(int id, String title, String body) {
		SecSql sql = new SecSql();
		sql.append("UPDATE article AS A");
		sql.append("SET updateDate = NOW()");
		if (title != null) {
			sql.append(", title = ?", title);
		}

		if (body != null) {
			sql.append(", body = ?", body);
		}
		sql.append("WHERE A.id = ?", id);

		return MysqlUtil.update(sql);
	}

	// 재구현 완료[2021-08-10]
	// 해당 변수를 이용하여 해당되는 게시물 갯수를 DB에서 받아와 리턴
	public int getArticlesCount(String searchKeywordTypeCode, String searchKeyword, int boardId) {
		SecSql sql = new SecSql();
		sql.append("SELECT COUNT(*) AS cnt");
		sql.append("FROM article AS A");
		sql.append("WHERE 1");
		// searchKeyword이 null이 거나 없을 경우 동작X
		if (searchKeyword != null && searchKeyword.length() > 0) {
			switch (searchKeywordTypeCode) {
			case "body":
				sql.append("AND A.body LIKE CONCAT('%', ?, '%')", searchKeyword);
				break;
			case "title,body":
				sql.append("AND (");
				sql.append("A.body LIKE CONCAT('%', ?, '%')", searchKeyword);
				sql.append("OR");
				sql.append("A.title LIKE CONCAT('%', ?, '%')", searchKeyword);
				sql.append(")");
				break;
			case "title":
			default:
				sql.append("AND A.title LIKE CONCAT('%', ?, '%')", searchKeyword);
				break;
			}
		}
		// 게시판 번호가 0일시 동작X
		if (boardId != 0) {
			sql.append("AND A.boardId = ?", boardId);
		}

		return MysqlUtil.selectRowIntValue(sql);
	}

}
