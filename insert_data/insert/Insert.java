package insert_data.insert;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import insert_data.connection.Access;

public class Insert {

	private Access access = new Access();
	
	BlockingQueue<String> address = new LinkedBlockingQueue<>();
	
	public static void main(String[] args) throws Exception {
		Insert in = new Insert();
		in.insert2();
	}
	
	public void insert2() throws InterruptedException {
		
		/* 아 욕나오네 */
		ExecutorService execute = Executors.newFixedThreadPool(10);
		
		Runnable worker = () -> {
			Connection con = null;
			PreparedStatement ps = null;
			
			try {
				con = access.getConnection();
				con.setAutoCommit(false);
				
				ps = con.prepareStatement("insert into build_address(\r\n"
						+ "province,\r\n"
						+ "district,\r\n"
						+ "town,\r\n"
						+ "lot_main,\r\n"
						+ "lot_sub,\r\n"
						+ "road_name,\r\n"
						+ "build_num_main,\r\n"
						+ "build_num_sub,\r\n"
						+ "building_name,\r\n"
						+ "detailed_building,\r\n"
						+ "zipcode,\r\n"
						+ "row_num)\r\n"
						+ "Values\r\n"
						+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, build_address_rownum.NEXTVAL)");
				
				int batchSize = 0;
				
				while(true) {
					String readLine = address.take();
					
					if("finish".equals(readLine)) {
						break;
					}
					
					/*
					 1 시도명         : province
					 2 시군구명       : district
					 3 법정읍면동명   : town
					 4 지번본번       : lotMain          int
					 5 지번부번       : lotSub           int
					 6 도로명         : roadName
					 7 건물본번       : buildNumMain     int
					 8 건물부번       : buildNumSub      int
					 9 건물명         : buildingName
					 10 상세건물명    : detailedBuilding
					 11 우편번호      : zipcode
					 */
					String[] datas = readLine.split(",");
					String province = datas[0];
					String district = datas[1];
					String town = datas[2];
					int lotMain = Integer.parseInt(datas[3]);
					int lotSub = Integer.parseInt(datas[4]);
					String roadName = datas[5];
					int buildNumMain = Integer.parseInt(datas[6]);
					int buildNumSub = Integer.parseInt(datas[7]);
					String buildingName = datas[8];
					String detailedBuilding = datas[9];
					String zipcode = datas[10];
					
					ps.setString(1, province);
					ps.setString(2, district);
					ps.setString(3, town);
					ps.setInt(4, lotMain);
					ps.setInt(5, lotSub);
					ps.setString(6, roadName);
					ps.setInt(7, buildNumMain);
					ps.setInt(8, buildNumSub);
					ps.setString(9, buildingName);
					ps.setString(10, detailedBuilding);
					ps.setString(11, zipcode);
					
					ps.addBatch();
					batchSize++;
					
					System.out.println(province + district + roadName);
					
					if(batchSize % 2000 == 0) {
						ps.executeBatch();
						con.commit();
						batchSize = 0;
					}
				}
				ps.executeBatch();
				con.commit();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					ps.close();
					con.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		for(int i = 0; i < 10; i++) {
			execute.submit(worker);
		}
		
		try(BufferedReader br = new BufferedReader(new FileReader("src/build_address.txt"))) {
			String line;
			while((line = br.readLine()) != null) {
				address.put(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			for(int i = 0; i < 10; i++) {
				address.put("finish");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		execute.shutdown();
		execute.awaitTermination(2, TimeUnit.HOURS);
		
	}
}
