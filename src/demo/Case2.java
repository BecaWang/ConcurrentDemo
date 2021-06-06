package demo;

import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * 參考: https://blog.csdn.net/liuyu973971883/article/details/107917079
 * @author 
 * 模擬三個運動員執行鐵人三項
 * 把phaser.arriveAndDeregister();下在最外層的catch 
 * 受傷的運動員2號 會停止執行下去，且程式會跑完
 *
 *
 */
public class Case2 {
    private static Random random = new Random(System.currentTimeMillis());

	public static void main(String[] args) {
        //初始化3個parties
        Phaser phaser = new Phaser(3);
        for (int i=1;i<4;i++){
            new Athlete(phaser,i).start();
        }
	}
	
    private static class Athlete extends Thread{
    	private Phaser phaser;
        private int no;//运动员编号
        
        public Athlete(Phaser phaser,int no) {
            this.phaser = phaser;
            this.no = no;
        }
        
        @Override
        public void run() {
            try {
                System.out.println("運動員:" +no+": 当前处于第："+phaser.getPhase()+"阶段");
                System.out.println("運動員:" +no+": start swimming");
                TimeUnit.SECONDS.sleep(random.nextInt(5));
                System.out.println("運動員:" +no+": end swimming");
                phaser.arriveAndAwaitAdvance();

                System.out.println("運動員:" +no+": 当前处于第："+phaser.getPhase()+"阶段");
                System.out.println("運動員:" +no+": start bicycle");
                TimeUnit.SECONDS.sleep(random.nextInt(5));

                System.out.println("運動員:" +no+": end bicycle");                
                //模擬當第二個運動員騎腳踏車後受傷離場
                if (no == 2 && phaser.getPhase() == 1) {
                        System.out.println("運動員:" +no+" 受傷");
                		throw new InterruptedException("harming");
                }else {
                    //等待其他运动员完成骑行
                    phaser.arriveAndAwaitAdvance();
                }

                System.out.println("運動員:" +no+": 当前处于第："+phaser.getPhase()+"阶段");
                System.out.println("運動員:" +no+": start running");
                TimeUnit.SECONDS.sleep(random.nextInt(5));
                System.out.println("運動員:" +no+": end running");
                phaser.arriveAndAwaitAdvance();
            } catch (InterruptedException e) {
                e.printStackTrace();
                phaser.arriveAndDeregister(); 
            }
        }

    }

}
