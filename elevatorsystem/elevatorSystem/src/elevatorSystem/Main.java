package elevatorSystem;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
 
public class Main {
    private static final int BUILDING_HEIGHT = 25; // ���� ���� ����
    @SuppressWarnings("unchecked")
    public static List<Guest>[] layer = new ArrayList[BUILDING_HEIGHT]; // �� �� ����ϴ� �°� ����Ʈ
    public static Elevator elevator = new Elevator(); 
    public static List<Guest> result = new ArrayList<Guest>(); // ��� ����Ʈ
    public static int elapsedTime; // ��� �ð�
    
    //�� ������ ������ �ð� 1��
    //��� ������ Ÿ�� �ð� 1��
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
                                    + "����ð� : %d�� %n"
                                    + "���������� ���� : %s %n"
                                    + "���� ��ġ : %d�� %n"
                                    + "������ : %s %n"
                                    ,elapsedTime
                                    ,elevator.getBriefing()
                                    ,elevator.getCurrentLayer()
                                    ,elevator.getDoorState() == true ? "����" : "����");
                        } catch(Exception e) {
                            e.toString();
                        }
                    }
                    
                    for(int q = 0 ; q < result.size() ; q++) {
                        System.out.printf("%d. �̸�: %s ����ð�: %d�� �̵����: %s�� -> %s�� %n"
                                ,q + 1
                                ,result.get(q).getName()
                                ,result.get(q).getWaitTime()
                                ,result.get(q).getStartLayer()
                                ,result.get(q).getDestination());
                    }
                    System.out.printf("�� �̵��ð� : %d�� ",elapsedTime);
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
                System.out.println("��� : �߸��� ����� �Է� �ϼ̽��ϴ�!!");
                q--;
                continue;
            }
            
            System.out.println("-----"+curLayer+"�� �����----- ");
            for(int w = 0 ; w < layer[q].size() ; w++) 
                System.out.printf("�̸� : %s %n"
                        + "������ : %dkg %n"
                        + "������ : %d�� %n"
                        + "������ : %d�� %n"
                        + "---------%n"
                        ,layer[q].get(w).getName()
                        ,layer[q].get(w).getWeight()
                        ,layer[q].get(w).getCurrentLayer()
                        ,layer[q].get(w).getDestination());
            
        }
    }
}