/*
 *                             _ooOoo_
 *                            o8888888o
 *                            88" . "88
 *                            (| -_- |)
 *                            O\  =  /O
 *                         ____/`---'\____
 *                       .'  \\|     |//  `.
 *                      /  \\|||  :  |||//  \
 *                     /  _||||| -:- |||||-  \
 *                     |   | \\\  -  /// |   |
 *                     | \_|  ''\---/''  |   |
 *                     \  .-\__  `-`  ___/-. /
 *                   ___`. .'  /--.--\  `. . __
 *                ."" '<  `.___\_<|>_/___.'  >'"".
 *               | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *               \  \ `-.   \_ __\ /__ _/   .-` /  /
 *          ======`-.____`-.___\_____/___.-`____.-'======
 *                             `=---='
 *          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *                     佛祖保佑        永无BUG
 *
 * @author FeAor
 * 2020/11/16:遗留问题：测试不同规格矩阵需求
 * 2020/11/20:基本正常
 */

package src;
import java.util.Arrays;
import java.util.Scanner;

public class main {
    static int[][] array = new int[100][100];
    static int[] available = new int[30];
    static int[] need = new int[30];
    static int[] request = new int[30];
    static int process;
    static int processId;
    static int resource;
    static Scanner Input = new Scanner(System.in);

    public static void main(String[] args) {


        System.out.println("进程数：(<100)");
        process = Input.nextInt();

        System.out.println("资源数：(<30)");
        resource = Input.nextInt();

        System.out.println("输入可用资源数");

        for (int i = 0; i < resource; i++) {
            available[i] = Input.nextInt();
        }

        System.out.println("输入最大需求矩阵");
        for (int i = 0; i < process; i++) {
            for (int j = 0; j < resource; j++) {
                array[i][j] = Input.nextInt();
            }
        }

        System.out.println("输入分配矩阵");
        for (int i = 0; i < process; i++) {
            for (int j = resource; j < 2*resource; j++) {
                array[i][j] = Input.nextInt();
            }
        }

        //计算需求矩阵
        for (int i = 0; i < process; i++) {
            for (int j = 2*resource ,n = 0; j < 3*resource; j++,n++) {
                array[i][j] = array[i][n] - array[i][n+resource];
            }
        }
        for (int i = 0; i < process; i++) {
            System.arraycopy(array[i], 0, array[i], 0, 3*resource);
        }//备份一次

        //显示基础信息，只运行一次
        SecurityCheck(101);
        System.out.println("\n");

        do {
            System.out.println("=================================================\n可分配资源");
            for (int i = 0; i < resource; i++) {
                System.out.print(available[i]+" | ");
            }System.out.println("\n");
            Request();    //应该由具体函数操作调用
        } while (true);

    }

    public static void Display(){   //输出矩阵
        System.out.println("\t|MAX   |Alloc |Need  |Flag");
        for (int i = 0; i < process; i++) {
            System.out.print("进程"+i);
            for (int j = 0; j <= 3*resource; j++) {
                if(j%resource == 0) System.out.print("|");
                System.out.print(array[i][j]+" ");
            }
            System.out.println("\n");
        }
    }

    public static void Request(){
        int sum = 0;
        int[] need = new int[request.length];
        System.out.println("输入请求资源进程：");
        processId = Input.nextInt();    //指定进程名
        System.out.println("输入请求资源数量：");//request
        for (int i = 0; i < resource; i++) {
            request[i] = Input.nextInt();
            need[i] = array[processId][2*resource+i];
        }
        if(!bankersCompare(need,request)){
            System.out.println("申请超需求，请重新输入");
            Request();
        }else{
            for (int i = 0; i < resource; i++) {//此处对几大数组进行直接修改，需注意
                available[i] -= request[i];
                array[processId][i+resource] += request[i];     //allocation + request
                array[processId][i+2*resource] -= request[i];   //need - request
            }//预分配若 need==0 available+。。。。

            for (int i = 0; i < resource; i++) {
                sum += array[processId][2*resource+i]; //need求和
            }
            if(sum==0) {
                for (int i = 0; i < resource; i++) {
                    available[i] += array[processId][i];//由于对available进行了修改，所以加的是max
                }
            }
            //以上else内对合法的request直接进行了分配
            //然后进行安全检查
            SecurityCheck(processId);
        }

    }

    public static void SecurityCheck(int processId){//int[] request
        Scanner Input = new Scanner(System.in);
        int[] work = Arrays.copyOf(available, resource);    //创建work数组
        int[] resourceNum = new int[resource];
        boolean flag1 = false;
        boolean flag2 = false;
        int sum = 0;
        int sum2 = 0;
        int sum3 = 0;
        int flag3 = 0;
        int numComplete = 0;
        int times = 0;

        for (int i = 0; i < process; i++) {
            array[i][3*resource] = 0;
        }

        //安全检查前先排除已经完毕的进程
        for (int i = 0; i < process; i++) {
            for (int j = 0; j < resource; j++) {
                sum2 += array[i][2*resource+j];//need求和若为0则运行完成
            }
            if(sum2 == 0) numComplete++;
            else sum2 = 0;
        }
        flag3 = process - numComplete;

        while(!flag2){      //此处遍历为了不断循环直到找到整个安全序列
            times++;
            sum3 = 0;
            numComplete = 0;
            for (int i = 0; i < process; i++) {
                sum3 += array[i][3*resource];//flag求和
            }
            flag2 = sum3 == flag3;//此处检查是否全部运行过


            for (int i = 0; i < process; i++) {         //遍历检查
                sum = 0;
                for (int k = 0; k < resource; k++) {
                    need[k] = array[i][2*resource+k];    //设置一下need
                    sum += array[i][2*resource+k]; //need求和看是否完成
                }

                if(sum > 0){
                    if(array[i][3*resource] == 0 && bankersCompare(work,need)){     //判断目前进程i是否满足需求
                        for (int j = 0; j < resource; j++) {
                            work[j] += array[i][resource+j];    //改变work,work + allocation
                        }
                        array[i][3*resource] = 1;       //改变标志，即finish
                        System.out.print("("+i+")-->");
                        //从头开始找
                    }
                }
            }

            if( times>process-numComplete){
                flag1 = true;       //此处两个flag，两种跳出机制
                break;
            }else flag1= false;

        }

        System.out.println("\n");

        if(flag1){
            System.out.println("<<不安全,已退回>>");
            for (int i = 0; i < resource; i++) {    //此处对几大数组进行直接修改，需注意（退回操作）
                available[i] += request[i];
                array[processId][i+resource] -= request[i];
                array[processId][i+2*resource] += request[i];
            }
        }
        else System.out.println("<<安全>>");
        for (int j = 0; j < process; j++) {
            array[j][3*resource] = 0;
        }

        for (int i = 0; i < resource; i++) {
            System.out.print(available[i]+" | ");
        }System.out.println("\n");
        Display();
    }

    public static boolean bankersCompare(int[] a,int[] b){
        int flag = 0;
        for (int i = 0; i < a.length; i++) {
            if(a[i] >= b[i]) flag++;
            else flag--;
        }
        return flag == a.length;
    }

}


