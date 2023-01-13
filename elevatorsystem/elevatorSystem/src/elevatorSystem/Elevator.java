package elevatorSystem;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static elevatorSystem.Main.*;
 
public class Elevator {
    private List<Guest> inElevator = new ArrayList<Guest>(); //guest ����Ʈ
    private String stateBriefing; // ���������� ���� ����
    private boolean doorState; // ������������ �� ���� ����
    private float currentWeight; // ���������� �߷�
    private int currentLayer; // ���������� ���� ��
    private int destination; // ���������� ������
    private int callLayer; //ȣ��� ��
    private int calldestination; //ȣ���� ������
    private int state; //  GOING_UP, GOING_DOWN, PAUSE�� ���� �������
    private static final int PAUSE = 0;
    private static final int GOING_UP = 1;
    private static final int GOING_DOWN = 2;
    private static final int LAYER_DISTANCE = 12; // ���� �Ÿ�
    private static final int ELEVATOR_SPEED = 12;  // ���������� �ӵ�
    private static final float MAX_WEIGHT = 10000;  // �ִ� �߷�
    private static final int PER_0_33_LOAD = 0;
    private static final int PER_33_66_LOAD = 1;
    private static final int PER_66_100_LOAD = 2;
    
    public Elevator() {
        this.currentLayer = 1;
    }
    
    public String getBriefing() {
        return stateBriefing;
    }
    public float getCurrentWeight() {
        return currentWeight;
    }
    public int getCurrentLayer() {
        return currentLayer;
    }
    public boolean getDoorState() {
        return doorState;
    }
 
    /**
     * ���������� �˰���
     * 1. ���������� ��������� �ִ��� ���������� ���θ� Ȯ���Ѵ�.
     * 2. ���������� ž���� ����� �ִ��� Ȯ���Ѵ�.(ž�½ÿ� �߷��� Ȯ���Ѵ�.)
     * 3-1. ž���� ����� ������ �������� ������ �Ŀ� �������� �����Ѵ�.
     * 3-2. ž���� �����Ŀ��� ���������Ͱ� ����ִٸ�, �ǹ��� ���������� ��ٸ��� ����� Ž���Ѵ�.
     * 4. �ǹ����� ���������͸� ��ٸ��� ����� ���� ž���ڵ� ���ٸ� ���������� ������ ������.
     * 5. ������ �������� �ִٸ� �������� ����Ѵ�. (������ �˰��� 1,2 �� �����Ų��.)
     * @throws InterruptedException
     */
    public void goElevator() throws InterruptedException {
        
        while(layer != null) {
            algorithm1();
            algorithm2();
            
            if(inElevator.size() == 0) {
                if(algorithm3_2()) {
                    layer = null;
                    continue;
                }
            }
            else {
                algorithm3_1();
            }
            
            algorithm5();
            if(layer.length == currentLayer)
                state = PAUSE;
        }
    }
    
    // �����˰��� : �������� �������� ��� Ž��
    private void algorithm1() throws InterruptedException {
        
        for(int q = 0 ; q < inElevator.size() ; q++)
            
            if(inElevator.get(q).getDestination() == currentLayer) {
                
                commonProcess(inElevator.get(q).getName()+" ������");

                inElevator.get(q).setWaitTime(elapsedTime); // �����Ϸ� �� ����� �ɸ� �ð�
                result.add(inElevator.get(q));  //��� ����Ʈ�� ������ ��� ����
                currentWeight -= inElevator.get(q).getWeight();  // ������ ����� ���� ��
                inElevator.remove(q); // ������ ��� ����
                q--;
            }
    }
    
    // ž�� �˰��� : �ڸ��� ���´ٸ� �°� ž��
    private void algorithm2() throws InterruptedException {
        
        for(int q = 0 ; MAX_WEIGHT - currentWeight >= 50 && q < layer[currentLayer - 1].size() ; q++)
            
            if( boardingCheck(layer[currentLayer - 1].get(q)) ) {
                
                commonProcess(layer[currentLayer - 1].get(q).getName()+" ž����");
                inElevator.add(layer[currentLayer - 1].get(q)); // ���������Ϳ� ž�� �°� �߰�
                currentWeight += layer[currentLayer - 1].get(q).getWeight(); // �߷� �߰�
                layer[currentLayer - 1].remove(q); // ž���� ������ ���� 
                q--;
            }
    }
    
    // Ž�� �˰��� : ȣ���� �ִ� �� Ž��
    private boolean algorithm3_2() {
        int[] layerArr = Arrays.copyOf(exploreArr(), exploreArr().length); //exploreArr ����Ʈ ����
        
        for(int q = 0 ; q < layer.length ; q ++) {
            
            int layerNumber = layerArr[q];
            if(layer[layerNumber].size() >= 1) {
                destination = layerNumber + 1;
                state = destination - currentLayer > 0 ? GOING_UP : GOING_DOWN ; //�������� ���������� ���� GOING_UP, �������� ���������� �Ʒ��� GOING_DOWN
                return false;
            }
        }
        return true;
    }

    //Advance call �˰���
    private void algorithm3_1() {
        int[] tmpArr = new int[inElevator.size()];
        int k1=0;
        int k2=0;
        for(int q = 0 ; q < tmpArr.length ; q++) {
        	tmpArr[q] = inElevator.get(q).getDestination();
        	int wait= inElevator.get(q).getWaitTime();
            int dest_1 = Math.abs(currentLayer - destination)+ callLayer + Math.abs(callLayer-calldestination); // collective control ����� �̵��Ÿ�
            int dest_2 = Math.abs(currentLayer - callLayer)+ Math.abs(callLayer-calldestination);  // ���� ����� �̵��Ÿ�
            k1= dest_1 > dest_2 ? dest_1 : dest_2; // �̵��Ÿ��� ���� ����� ����Ѵٰ� ����  
            k2= dest_1 < dest_2 ? dest_1 : dest_2;  
            k2= k2/wait; // ������ ����� �����Ұ��, ��ٸ��� �Ǵ� ������� ���ð��� ���� 
            destination = k1 > k2 ? destination : calldestination;  // ������ ��Ŀ� ���� ������ ����
        
        }
            
        Arrays.sort(tmpArr);
        destination = k1 > k2 ? destination : calldestination;
        state = destination - currentLayer > 0 ? GOING_UP : GOING_DOWN ;
    }

    private void algorithm5() throws InterruptedException {
        doorController(false);
        int distance = Math.abs(destination - currentLayer) * LAYER_DISTANCE; //�̵��Ÿ� ���
        int tmpLayer = 0;
        
        while(distance != 0) {
            
            stateBriefing = "�̵��� (������ : "+destination+"��)";
            Thread.sleep(1000);
            tmpLayer +=ELEVATOR_SPEED;
            
            if(tmpLayer == LAYER_DISTANCE) {
                distance -= tmpLayer;
                tmpLayer = 0;
                currentLayer += state == GOING_UP ? 1 : -1; // ���� ���°� ����ϰ��, ������ 1 ����, �ݴ��ϰ�� 1����
                
                // �̵��� �� ���� ȣ���� �ִ��� Ȯ��
                if(distance != 0) {
                    algorithm1();
                    algorithm2();
                    doorController(false);
                }
            }
        }
    }
 
    private boolean boardingCheck(Guest guest) {
        if( (MAX_WEIGHT - currentWeight) >= guest.getWeight() )
            return state == PAUSE || state == guest.getState() || algorithm3_2() ? true : false;
        // ���������Ͱ� ���������̰ų� ��������� ������ ž�°���
        else
            return false;
    }
    
    private void doorController(boolean flag) throws InterruptedException {
        // ��û flag�� state ��ġ��, ������ ����     true : ���� ���� ����, false : ���� ���� ����
    	if(doorState != flag) {
            Thread.sleep(1000);  //�� ���� �����µ� 1��
            doorState = flag;
        }
    }
    
    //���� ���⿡ ���� ������⿡ ���� Ž��
    private int[] exploreArr() {
        int[] tmpArr = new int[layer.length];
        int index = 0;
        
        switch(state) {
        case PAUSE:
            for(int q = 0 ; q < tmpArr.length ; q++)
                tmpArr[q] = q;
            break;
        case GOING_UP: //��������� ���ϰ��, �� ��ġ���� ���� Ž��
            index += exploreUp(tmpArr, index);
            index += exploreDown(tmpArr, index);
            break;
        case GOING_DOWN:  //��������� �Ʒ��ϰ��, �� ��ġ���� �Ʒ��� Ž��
            index += exploreDown(tmpArr, index);
            index += exploreUp(tmpArr, index);
            break;
        }
        return tmpArr;
    }
    
    private int exploreUp(int[] arr, int index) {
 
        for(int q = arr.length - 1 ; q != currentLayer - 1 ; q--) {
            arr[index] = q;
            index ++;
        }
        return index;
    }
    
    private int exploreDown(int[] arr, int index) {
 
        for(int q = 0 ; q != currentLayer - 1 ; q++) {
            arr[index] = q;
            index ++;
        }
        return index;
    }
    
    // ���������� ���¸� �ǽð����� ����
    private void commonProcess(String msg) throws InterruptedException {
        doorController(true);
        stateBriefing = msg;
        Thread.sleep(1000);
    }
}