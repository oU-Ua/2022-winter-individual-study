package elevatorSystem;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
 
public class Main {
    private static final int BUILDING_HEIGHT = 25; // 빌딩 높이 설정
    @SuppressWarnings("unchecked")
    public static List<Guest>[] layer = new ArrayList[BUILDING_HEIGHT]; // 각 층 대기하는 승객 리스트
    public static Elevator elevator = new Elevator(); 
    public static List<Guest> result = new ArrayList<Guest>(); // 결과 리스트
    public static int elapsedTime; // 경과 시간
    
    //문 닫히고 열리는 시간 1초
    //사람 내리고 타는 시간 1초
    public static void main(String[] args) {
        try {
            startSetting();
            
            new Thread (new Runnable() {
 
                @Override
                public void run() {
                    while(layer != null) {
                        elapsedTime++;
 
                        try {
                            Thread.sleep(1000);
                            
                            System.out.println("\n\n\n\n\n\n\n\n\n\n");
                            System.out.printf("-------Elevator--------%n"
                                    + "경과시간 : %d초 %n"
                                    + "엘리베이터 상태 : %s %n"
                                    + "현재 위치 : %d층 %n"
                                    + "문상태 : %s %n"
                                    ,elapsedTime
                                    ,elevator.getBriefing()
                                    ,elevator.getCurrentLayer()
                                    ,elevator.getDoorState() == true ? "열림" : "닫힘");
                        } catch(Exception e) {
                            e.toString();
                        }
                    }
                    
                    for(int q = 0 ; q < result.size() ; q++) {
                        System.out.printf("%d. 이름: %s 경과시간: %d초 이동경로: %s층 -> %s층 %n"
                                ,q + 1
                                ,result.get(q).getName()
                                ,result.get(q).getWaitTime()
                                ,result.get(q).getStartLayer()
                                ,result.get(q).getDestination());
                    }
                    System.out.printf("총 이동시간 : %d초 ",elapsedTime);
                }
            }).start();
            
            elevator.goElevator();
        } catch (Exception e) {
            e.toString();
        }
    }
    private static void startSetting() {
        int seq = 0;
        
        for (int q = 0 ; q < layer.length ; q++) {
            
            layer[q] = new ArrayList<Guest>();
            int curLayer = q + 1;
            int isLoop = 0;
            
            try {                
                for(int w = 0 ; w < 4 ; w++) {
                    
                    int des = (int)(Math.random() * layer.length) + 1;
                    if(curLayer == des) {
                        w--;
                        continue;
                    }
                    
                    layer[q].add(new Guest(
                            des - curLayer > 0 ? 1 : 2,
                            50 + (int)(Math.random() * 31),
                            des,
                            curLayer,
                            "Guest"+ ++seq));
                }
                
            } catch(Exception e) {
                System.out.println("경고 : 잘못된 명령을 입력 하셨습니다!!");
                q--;
                continue;
            }
            
            System.out.println("-----"+curLayer+"층 대기명단----- ");
            for(int w = 0 ; w < layer[q].size() ; w++) 
                System.out.printf("이름 : %s %n"
                        + "몸무게 : %dkg %n"
                        + "현재층 : %d층 %n"
                        + "목적지 : %d층 %n"
                        + "---------%n"
                        ,layer[q].get(w).getName()
                        ,layer[q].get(w).getWeight()
                        ,layer[q].get(w).getCurrentLayer()
                        ,layer[q].get(w).getDestination());
            
        }
    }
}