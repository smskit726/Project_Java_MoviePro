package Daum;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BoxOfficeDaum {

	String baseUrl = "http://ticket2.movie.daum.net/Movie/MovieRankList.aspx";
	int finalCnt = 0; // 수집을 멈추기 위한 변수 (1~10위까지 완료)

	public String[][] daumMovieRank(String[][] mvRank) throws IOException {

		Document doc = Jsoup.connect(baseUrl).get();
		Elements movieList = doc.select("div.desc_boxthumb > strong.tit_join > a");

		for (Element movie : movieList) {

			if (finalCnt == 10) {
				// 1~10위까지의 영화정보 수집 완료! 빠져나가세요~
				break;
			}

			String title = movie.text();
			
			int flag = 999;

			for (int i = 0; i < mvRank.length; i++) {
				if (mvRank[i][1].contentEquals(title)) {
					// BoxOffice 1~10위권 내의 영화로 판별 크롤링
					flag = i; // 0 ~ 9 값만 input
					break;
				}
			}

			// 1~10위권 외의 영화 -> 크롤링X
			// flag가 0 ~ 9 사이의 값이면 크롤링 시작
			if (flag == 999) {
				continue;
			}
			
			String url = movie.attr("href");
			Document movieDoc = Jsoup.connect(url).get();

			// 상세정보가 없는 영화는 생략
			if (movieDoc.select("span.txt_name").size() == 0) {
				continue;
			}

			String daumHref = movieDoc.select("a.area_poster").get(0).attr("href");
			String daumCode = daumHref.substring(daumHref.lastIndexOf("=") + 1, daumHref.lastIndexOf("#"));
			
			mvRank[flag][11] = daumCode;
			finalCnt += 1;

		}
		return mvRank;
	}

}
