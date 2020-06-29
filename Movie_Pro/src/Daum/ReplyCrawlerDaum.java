package Daum;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import domain.ReplyDTO;
import persistence.ReplyDAO;

public class ReplyCrawlerDaum {
	int page = 1;
	int cnt = 0;
	int total = 0;
	String prePage = "";

	ReplyDAO rDao = new ReplyDAO();

	public HashMap<String, Integer> daumCrawler(String movieNm, String daumCode) throws IOException {
		
		while (true) {

			String url = "https://movie.daum.net/moviedb/grade?movieId="+daumCode+"&type=netizen&page=" + page;
			Document doc = Jsoup.connect(url).get();

			Elements replyList = doc.select("div.review_info");

			if (replyList.size() <= 0) {
				break;
			}
			
			String writer = "";
			int score = 0;
			String content = "";
			String regdate = "";

			for (Element reply : replyList) {
				writer = reply.select("em.link_profile").text();
				score = Integer.parseInt(reply.select("em.emph_grade").text());
				content = reply.select("p.desc_review").text();
				regdate = reply.select("span.info_append").text().substring(0,10);

				System.out.println(
						"▦▦ [Daum] ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
				System.out.println("▦▦ 평  점 : " + score);
				System.out.println("▦▦ 댓  글 : " + content);
				System.out.println("▦▦ 작성자 : " + writer);
				System.out.println("▦▦ 등록일 : " + regdate);
				
				
				// MongoDB에 저장 (댓글 1건)
				ReplyDTO rDto = new ReplyDTO(movieNm, content, writer, score, regdate);
				rDao.addReply(rDto);
				
				total += score;
				cnt+=1;
				
			}
			page += 1;
		}

		System.out.println("▦▦ ==========================================================================================");
		System.out.printf("▦▦ Daum [총 %d]건을 수집하였습니다.\n", cnt);
	
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("cnt", cnt);
		map.put("total", total);

		return map;
	}

}
