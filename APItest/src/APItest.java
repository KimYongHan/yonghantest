import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class APItest {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/test";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "dydgks12!";

	public static void main(String[] args) {
		try {
			// 변수 설정
			String apiURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0";
			String authKey = "j1bhDAbejJ2cGqvGu65q6WUmO%2BVnc%2Bp4HpwugpaJ5I6CiBBWukhVd3ySjtzTR97aGl9TP0sv5xxf%2FF5dPzW2hA%3D%3D";
			String nx = "62";
			String ny = "130";
			String base_date = "20240503";
			String base_time = "1200";
			String dataType = "JSON";

			StringBuilder urlBuilder = new StringBuilder(apiURL);
			urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + authKey);
			urlBuilder.append("&" + URLEncoder.encode("numOfRows=10", "UTF-8")); // 표 개수
			urlBuilder.append("&" + URLEncoder.encode("pageNo=1", "UTF-8")); // 페이지 수
			urlBuilder
					.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(dataType, "UTF-8")); // 받으려는타입
																														
			urlBuilder.append(
					"&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(base_date, "UTF-8")); // 조회하고싶은날짜
																													 
			urlBuilder.append(
					"&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(base_time, "UTF-8")); // 조회하고싶은시간
																													
			urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); // x좌표
			urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); // y좌표

			URL url = new URL(urlBuilder.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");

			BufferedReader rd;
			if (conn.getResponseCode() == 200) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String result = "";
				String line;
				while ((line = rd.readLine()) != null) {
					result += line;
				}
				rd.close();

				// 여기서 데이터를 DB에 삽입
				insertDataIntoDB(result);
			} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				// For other response codes, handle the error stream
				String errorMsg = "";
				String line;
				while ((line = rd.readLine()) != null) {
					errorMsg += line;
				}
				rd.close();
				System.out.println("Error: " + errorMsg);
			}

			conn.disconnect();
		} catch (IOException e) {
			System.out.println("Error: Site not functioning properly");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// DB에 데이터 삽입
	private static void insertDataIntoDB(String jsonData) throws SQLException {
		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String sql = "INSERT INTO your_table_name (json_data) VALUES (?)";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, jsonData);
				stmt.executeUpdate();
				System.out.println("Data inserted into the database successfully.");
			}
		}
	}

	// DB에서 데이터 조회 (GET 요청에 대한 처리)
	private static void getDataFromDB() throws SQLException {
		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String sql = "SELECT json_data FROM your_table_name";
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						String jsonData = rs.getString("json_data");
						// 여기서 데이터 처리
						System.out.println(jsonData);
					}
				}
			}
		}
	}
}
