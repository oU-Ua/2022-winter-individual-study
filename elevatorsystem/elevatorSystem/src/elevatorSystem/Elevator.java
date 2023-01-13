package elevatorSystem;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static elevatorSystem.Main.*;
 
public class Elevator {
    private List<Guest> inElevator = new ArrayList<Guest>(); //guest 리스트
    private String stateBriefing; // 엘리베이터 현재 상태
    private boolean doorState; // 엘리베이터의 문 열고 닫음
    private float currentWeight; // 엘리베이터 중량
    private int currentLayer; // 엘리베이터 현재 층
    private int destination; // 엘리베이터 목적지
    private int callLayer; //호출된 층
    private int calldestination; //호출의 목적지
    private int state; //  GOING_UP, GOING_DOWN, PAUSE로 현재 운행상태
    private static final int PAUSE = 0;
    private static final int GOING_UP = 1;
    private static final int GOING_DOWN = 2;
    private static final int LAYER_DISTANCE = 12; // 층간 거리
    private static final int ELEVATOR_SPEED = 12;  // 엘리베이터 속도
    private static final float MAX_WEIGHT = 10000;  // 최대 중량
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
     * 엘리베이터 알고리즘
     * 1. 현재층에서 내릴사람이 있는지 엘리베이터 내부를 확인한다.
     * 2. 현재층에서 탑승할 사람이 있는지 확인한다.(탑승시에 중량을 확인한다.)
     * 3-1. 탑승한 사람이 있으면 목적지를 결정한 후에 목적지를 설정한다.
     * 3-2. 탑승이 끝난후에도 엘리베이터가 비어있다면, 건물에 엘리베이터 기다리는 사람을 탐색한다.
     * 4. 건물에도 엘리베이터를 기다리는 사람이 없고 탑승자도 없다면 엘리베이터 운행을 끝낸다.
     * 5. 설정된 목적지가 있다면 목적지로 출발한다. (층마다 알고리즘 1,2 를 실행시킨다.)
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
    
    // 하차알고리즘 : 목적지가 현재층인 사람 탐색
    private void algorithm1() throws InterruptedException {
        
        for(int q = 0 ; q < inElevator.size() ; q++)
            
            if(inElevator.get(q).getDestination() == currentLayer) {
                
                commonProcess(inElevator.get(q).getName()+" 하차중");

                inElevator.get(q).setWaitTime(elapsedTime); // 하차완료 한 사람이 걸린 시간
                result.add(inElevator.get(q));  //결과 리스트에 하차한 사람 저장
                currentWeight -= inElevator.get(q).getWeight();  // 하차한 사람의 무게 뺌
                inElevator.remove(q); // 하차한 사람 제거
                q--;
            }
    }
    
    // 탑승 알고리즘 : 자리가 남는다면 승객 탑승
    private void algorithm2() throws InterruptedException {
        
        for(int q = 0 ; MAX_WEIGHT - currentWeight >= 50 && q < layer[currentLayer - 1].size() ; q++)
            
            if( boardingCheck(layer[currentLayer - 1].get(q)) ) {
                
                commonProcess(layer[currentLayer - 1].get(q).getName()+" 탑승중");
                inElevator.add(layer[currentLayer - 1].get(q)); // 엘리베이터에 탑승 승객 추가
                currentWeight += layer[currentLayer - 1].get(q).getWeight(); // 중량 추가
                layer[currentLayer - 1].remove(q); // 탑승한 층에서 제거 
                q--;
            }
    }
    
    // 탐색 알고리즘 : 호출이 있는 지 탐색
    private boolean algorithm3_2() {
        int[] layerArr = Arrays.copyOf(exploreArr(), exploreArr().length); //exploreArr 리스트 복사
        
        for(int q = 0 ; q < layer.length ; q ++) {
            
            int layerNumber = layerArr[q];
            if(layer[layerNumber].size() >= 1) {
                destination = layerNumber + 1;
                state = destination - currentLayer > 0 ? GOING_UP : GOING_DOWN ; //목적지가 현재층보다 위면 GOING_UP, 목적지가 현재층보다 아래면 GOING_DOWN
                return false;
            }
        }
        return true;
    }

    //Advance call 알고리즘
    private void algorithm3_1() {
        int[] tmpArr = new int[inElevator.size()];
        int k1=0;
        int k2=0;
        for(int q = 0 ; q < tmpArr.length ; q++) {
        	tmpArr[q] = inElevator.get(q).getDestination();
        	int wait= inElevator.get(q).getWaitTime();
            int dest_1 = Math.abs(currentLayer - destination)+ callLayer + Math.abs(callLayer-calldestination); // collective control 수행시 이동거리
            int dest_2 = Math.abs(currentLayer - callLayer)+ Math.abs(callLayer-calldestination);  // 동시 수행시 이동거리
            k1= dest_1 > dest_2 ? dest_1 : dest_2; // 이동거리가 적은 방식을 사용한다고 가정  
            k2= dest_1 < dest_2 ? dest_1 : dest_2;  
            k2= k2/wait; // 가정한 방식을 적용할경우, 기다리게 되는 사람들의 대기시간을 가중 
            destination = k1 > k2 ? destination : calldestination;  // 선정된 방식에 따른 목적지 선정
        
        }
            
        Arrays.sort(tmpArr);
        destination = k1 > k2 ? destination : calldestination;
        state = destination - currentLayer > 0 ? GOING_UP : GOING_DOWN ;
    }

    private void algorithm5() throws InterruptedException {
        doorController(false);
        int distance = Math.abs(destination - currentLayer) * LAYER_DISTANCE; //이동거리 계산
        int tmpLayer = 0;
        
        while(distance != 0) {
            
            stateBriefing = "이동중 (목적지 : "+destination+"층)";
            Thread.sleep(1000);
            tmpLayer +=ELEVATOR_SPEED;
            
            if(tmpLayer == LAYER_DISTANCE) {
                distance -= tmpLayer;
                tmpLayer = 0;
                currentLayer += state == GOING_UP ? 1 : -1; // 현재 상태가 상승일경우, 현재층 1 증가, 반대일경우 1감소
                
                // 이동시 각 층에 호출이 있는지 확인
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
        // 엘리베이터가 정지상태이거나 진행방향이 같으면 탑승가능
        else
            return false;
    }
    
    private void doorController(boolean flag) throws InterruptedException {
        // 요청 flag와 state 일치시, 현상태 유지     true : 문이 열린 상태, false : 문이 닫힌 상태
    	if(doorState != flag) {
            Thread.sleep(1000);  //문 열고 닫히는데 1초
            doorState = flag;
        }
    }
    
    //진행 방향에 따라 진행방향에 맞춰 탐색
    private int[] exploreArr() {
        int[] tmpArr = new int[layer.length];
        int index = 0;
        
        switch(state) {
        case PAUSE:
            for(int q = 0 ; q < tmpArr.length ; q++)
                tmpArr[q] = q;
            break;
        case GOING_UP: //진행방향이 위일경우, 현 위치보다 위를 탐색
            index += exploreUp(tmpArr, index);
            index += exploreDown(tmpArr, index);
            break;
        case GOING_DOWN:  //진행방향이 아래일경우, 현 위치보다 아래를 탐색
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
    
    // 엘리베이터 상태를 실시간으로 변경
    private void commonProcess(String msg) throws InterruptedException {
        doorController(true);
        stateBriefing = msg;
        Thread.sleep(1000);
    }
}