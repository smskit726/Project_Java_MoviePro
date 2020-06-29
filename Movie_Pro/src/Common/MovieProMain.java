package Common;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

import Daum.BoxOfficeDaum;
import Daum.ReplyCrawlerDaum;
import Naver.BoxOfficeNaver;
import Naver.ReplyCrawlerNaver;
import persistence.ReplyDAO;

public class MovieProMain {

	public static void main(String[] args) throws Exception {
		BoxOfficeParser bParser = new BoxOfficeParser(); // 생성자 : 객체생성과 동시에 +a 작업을 하고 싶음
		BoxOfficeNaver bon = new BoxOfficeNaver();
		BoxOfficeDaum don = new BoxOfficeDaum();
		ReplyCrawlerNaver nCrawler = new ReplyCrawlerNaver();
		ReplyCrawlerDaum dCrawler = new ReplyCrawlerDaum();
		
		ReplyDAO rDao = new ReplyDAO();
		
		// 순위, 영화제목, 예매율, 장르, 상영시간, 개봉일자, 감독, 출연진, 누적관객수, 누적매출액, 네이버코드, 다음코드
		String[][] mvRank = new String[10][12];

		// 1. 박스오피스 정보 + 네이버 영화정보 + 다음 영화정보 (1~10위)

		// 1-1. BoxOffice Parsing :)
		mvRank = bParser.getParser();

		// 1-2. Naver BoxOffice Crawling :)
		mvRank = bon.naverMovieRank(mvRank);

		// 1-3. Daum BoxOffice Crawling :)
		mvRank = don.daumMovieRank(mvRank);
		
		// 2. View단 실행
		// userVal = 사용자가 입력한 영화번호(순위)
		int userVal = userInterFace(mvRank);
		
		// 3. 사용자가 선택한 영화의 네이버, 다음 댓글 정보를 수집 및 분석
		
		// 3-1. MongoDB 데이터 삭제
		// 수집하려는 댓글의 영화가 MongoDB에 저장되어 있는 영화라면 해당 영화 댓글 우선 삭제 후 새로운 댓글 저장
		rDao.deleteReply(mvRank[userVal-1][1]);
		
		// 3-2. Naver 댓글 수집 + MongoDB 저장
		HashMap<String,Integer> nMap = nCrawler.naverCrawler(mvRank[userVal-1][1],mvRank[userVal-1][10]);
		
		// 3-3. Daum 댓글 수집 + MongoDB 저장
		HashMap<String,Integer> dMap = dCrawler.daumCrawler(mvRank[userVal-1][1],mvRank[userVal-1][11]);
		
		// 4. 사용자에게 결과 출력
		double nTotal = nMap.get("total");
		double avgNaver = nTotal/nMap.get("cnt");
		double dTotal = dMap.get("total");
		double avgDaum = dTotal/dMap.get("cnt");
		
		DecimalFormat dropDot = new DecimalFormat(".#");
		DecimalFormat threeDot = new DecimalFormat("###,###");
		BigInteger money = new BigInteger(mvRank[userVal-1][9]);
		
		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("▦▦ Description of " + "\"" + mvRank[userVal-1][1] + "\"");
		System.out.println("▦▦ ==========================================================================================");
		System.out.println("▦▦ 개봉일자 : " + mvRank[userVal-1][5] + "         ▦▦ 예매율 : " + mvRank[userVal-1][2] + "%");
		System.out.println("▦▦ 장 르 : " + mvRank[userVal-1][3] + "                 ▦▦ 상영시간 : " + mvRank[userVal-1][4]);
		System.out.println("▦▦ 감 독 : " + mvRank[userVal-1][6]);
		System.out.println("▦▦ 출연진 : " + mvRank[userVal-1][7]);
		System.out.println("▦▦ 누적 → [관객수 : " + threeDot.format(Integer.parseInt(mvRank[userVal-1][8])) + "명] [매출액 : "+ threeDot.format(money) +"원]");
		System.out.println("▦▦ 네이버 → [댓글수 : " + nMap.get("cnt") + "건] [평 점 : " + dropDot.format(avgNaver) +"점]");
		System.out.println("▦▦ 다  음 → [댓글수 : " + dMap.get("cnt") + "건] [평 점 : " + dropDot.format(avgDaum) +"점]");
		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		
		
	}

	// VIEW : 프로그램 시작 인터페이스 + 사용자 값 입력
	public static int userInterFace(String[][] mvRank) {
		Scanner sc = new Scanner(System.in);
		int userVal = 0;  // 사용자 입력 값 변수
		
		// 2 View단

		// 2-1. 유저에게 BocOffice 예매율 1~10위까지의 정보 제공

		// 현재 날짜 계산하기
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
		String today = sdf.format(cal.getTime());
		SimpleDateFormat engSdf = new SimpleDateFormat("MMMM,dd", Locale.ENGLISH);
		String engDay = engSdf.format(cal.getTime());

		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("▦▦ >> Movie_Pro ver1.2");
		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("▦▦ >> Developer : MinSeog Shin(-)");
		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("▦▦ >> TODAY : " + today);
		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		System.out.println("▦▦ >> BoxOffice Rank (" + engDay + ")");

		for (int i = 0; i < mvRank.length; i++) {

			String noneCode = ""; // 초기화

			if (mvRank[i][10] == null) {
				noneCode = " <정보 없음>";
			}
			System.out.println("▦▦ " + mvRank[i][0] + "위 " + mvRank[i][1] + noneCode);

			/*
			 * if(mvRank[i][10] == null) { System.out.println("▦▦ " + mvRank[i][0] + "위 " +
			 * mvRank[i][1] + "<정보 없음> "); }else { System.out.println("▦▦ " + mvRank[i][0] +
			 * "위 " + mvRank[i][1]); }
			 */

		}

		// 2-2. 사용자가 입력하는 부분

		// 유효성 체크
		// (1 ~ 10) 까지의 값(정상)
		// 1. (1 ~ 10) 이외의 숫자를 넣었을 때
		// 2. 정보없는 영화를 선택했을 때
				
		while (true) {
			
			System.out.println(
					"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
			System.out.println("▦▦ >> 보고싶은 영화 번호(순위)를 입력하세요.");
			System.out.print("▦▦ >> 번호 : ");
			userVal = sc.nextInt();
			
			if (userVal < 0 || userVal > 10) {
				// 잘못된 값! (userVal = 0 -> 이스터에그)
				System.out.println("▦▦ >> [Warning] 1 ~ 10사이의 숫자를 입력하세요 :( ");
				continue;
			} else if (mvRank[userVal - 1][10] == null) {
				// 사용자가 입력한 번호의 영화의 정보가 있는지 없는지 체크
				System.out.println("▦▦ >> [Warning] 해당 영화는 상영정보가 없습니다. 다른영화를 선택해 주세요 :( ");
				continue;
			} else {
				// 통과 : 사용자의 값이 0~10
				sc.close();
				break;
			}
		}

		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");

		return userVal;
	}

	// mvRank 출력하는 코드
	public static void printArr(String[][] mvRank) {
		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
		for (int i = 0; i < mvRank.length; i++) {
			System.out.print(mvRank[i][0] + "\t");
			System.out.print(mvRank[i][1] + "\t");
			System.out.print(mvRank[i][2] + "\t");
			System.out.print(mvRank[i][3] + "\t");
			System.out.print(mvRank[i][4] + "\t");
			System.out.print(mvRank[i][5] + "\t");
			System.out.print(mvRank[i][6] + "\t");
			System.out.print(mvRank[i][7] + "\t");
			System.out.print(mvRank[i][8] + "\t");
			System.out.print(mvRank[i][9] + "\t");
			System.out.print(mvRank[i][10] + "\t");
			System.out.println(mvRank[i][11]);
		}
		System.out.println(
				"■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
	}

}
