package menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameBoard extends JFrame implements ActionListener {
    private JPanel topPanel = new JPanel();        //创建容器，容纳顶端按钮
    private JPanel boardPanel = new JPanel();    //创建一个容器,容纳方格
    //显示时间文本
    private static final String SHOW_TIME = "00:00:00";
    //开始时间
    private long startTime = System.currentTimeMillis();
    // 计数线程
    private CountingThread thread = new CountingThread();

    private int boardHeight;    //行数
    private int boardWidth;        //列数
    private int gameWidth;
    private int gameHeight;
    private Grid[][] board;        //存放方块
    private JButton reset;        //重新开始游戏
    private JLabel mineCount;    //显示雷的总数
    private int mines;            //获取雷总数
    private JLabel time;        //显示时间
    //用来得到某一方块周围方块坐标
    public static final int[] distantX = {-1, 0, 1};
    public static final int[] distanty = {-1, 0, 1};

    public GameBoard(String title, int width, int height, int mine) {
        super();
        this.boardWidth = width;
        this.boardHeight = height;
        // 窗体的标题
        setTitle(title);
        //窗体的大小
        this.gameWidth = width * 50;
        this.gameHeight = height * 50 + 150;
        setSize(gameWidth, gameHeight);
        //设置为流式布局
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        //添加顶端显示
        mines = mine;
        addTopButton(mines);
        //生成游戏界面
        addGame(width, height);
        //窗口大小可变
        setResizable(true);
        //关闭按钮，点击它时，退出程序，并结束进程
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //显示坐标
        setLocation(500, 50);
        // 使窗口可视化。
        setVisible(true);
        //启动线程
        getThread().start();
    }

    public void addTopButton(int mine) {
        //定义布局为流式布局
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, gameWidth / 9, 10));

        time = new JLabel("时间: " + SHOW_TIME);
        time.setSize(gameWidth / 6, 100);
        time.setFont(new Font("宋体", Font.BOLD, 16));

        //设置文本
        reset = new JButton("重新开始");
        //设置大小
        reset.setSize(gameWidth / 6, 100);
        //设置字体
        reset.setFont(new Font("宋体", Font.BOLD, 16));

        mineCount = new JLabel("雷数: " + mine);
        mineCount.setSize(gameWidth / 6, 100);
        mineCount.setFont(new Font("宋体", Font.BOLD, 16));

        //添加到布局中
        topPanel.add(time);
        topPanel.add(reset);
        topPanel.add(mineCount);
        //添加监听
        reset.addActionListener((ActionListener) this);
    }
    //生成游戏界面
    public void addGame(int width, int height) {
        // 创建游戏初始方块。
        this.board = new Grid[width][height];
        //设置布局:网格布局，几行几列
        boardPanel.setLayout(new GridLayout(height, width));
        //生成width * height的方格
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                //初始化方块
                board[x][y] = new Grid(x, y, this);
                //进行监听
                board[x][y].addActionListener(this);
                //添加到容器中
                boardPanel.add(board[x][y]);
            }
        }
    }
    //在除去给定坐标的剩余空格上随机产生炸弹
    public void produceMine(int number, int xLocation, int yLocation) {
        int count = 0;
        while(count < number) {
            int x, y;
            //随机数乘小于x-1控制范围在0~x-1，不会超过数组范围
            x = (int)(Math.random() * boardWidth - 1);
            //随机数乘小于y-1控制范围在0~y-1
            y = (int)(Math.random() * boardHeight - 1);
            //如果是给定坐标，跳过
            if(x == xLocation && y == yLocation) {
                continue;
            }
            //该点没有雷就埋雷
            if(!board[x][y].isMine()){
                //数量+1
                count++;
                //该点是雷
                board[x][y].setMine(true);
            }
        }
    }
    //检测坐标是否在范围内
    public boolean isGridRadius(int x, int y) {
        return x < 0 || x >= boardWidth || y < 0 || y >= boardHeight;
    }
    //判断是否赢得游戏
    public boolean isSuccess() {
        // 声明 Grid 实例。
        Grid gridObject;
        for (int y = 0; y < boardHeight; y++){
            for (int x = 0; x < boardWidth; x++){
                //只要还有一个没被打开的方块不是雷
                gridObject = board[x][y];
                if (!gridObject.isFlag() && !gridObject.isMine()) {
                    return false;
                }
            }
        }
        return true;
    }
    //揭开一个不是雷的方块后，检测周围雷数。并呈现出来
    public void checkGrid(int currentX, int currentY) {
        //用来统计地雷个数
        int count = 0;
        Grid currentGrid;

        //坐标范围不能超过边界
        if (isGridRadius(currentX, currentY)) {
            return;
            //方块不能已经被打开
        }else if(board[currentX][currentY].isFlag()) {
            return;
        }else {
            // 获取当前方块对象
            currentGrid = board[currentX][currentY];
            //设置方块已经被打开
            currentGrid.setFlag(true);
            // 声明 Grid 实例。
            Grid gridObject;
            for(int x : distantX) {
                for(int y : distanty) {
                    //坐标正确
                    if(!isGridRadius(currentX + x, currentY + y)) {
                        //不计算当前方块
                        if(!(x == 0 && y == 0)) {
                            //如果有一个为雷，count+1
                            gridObject = board[currentX + x][currentY + y];
                            if(gridObject.isMine()) {
                                count++;
                            }
                        }
                    }
                }
            }
        }
        if (count != 0) {
            //该块设置为雷个数
            currentGrid.setImage(GameBoard.class.getResource("/images/"
                    + count + ".jpg"));
        }else {
            //将该块设置为空白
            currentGrid.setImage(GameBoard.class.getResource("/images/blank.jpg"));
            // 声明 Grid 实例。
            Grid gridObject;
            for(int x : distantX) {
                for(int y : distanty) {
                    //坐标正确
                    if(!isGridRadius(currentX + x, currentY + y)) {
                        //不计算当前方块
                        if(!(x == 0 && y == 0)) {
                            /*如果不为雷，继续进行检测
                             *顺序分别为：左上，左，左下
                             * 		上，下
                             * 		右上，右，右下*/
                            gridObject = board[currentX + x][currentY + y];
                            if(!gridObject.isMine()) {
                                checkGrid(currentX + x, currentY + y);
                            }
                        }
                    }
                }
            }
        }
    }
    //游戏失败，显示出所有的炸弹
    public void showMine() {
        // 声明 Grid 实例。
        Grid gridObject;
        for(int j = 0; j < boardHeight; j++ ) {
            for(int i = 0; i < boardWidth; i++ ) {
                //如果是雷，而且没有被揭开
                gridObject = board[i][j];
                if(gridObject.isMine() && !gridObject.isFlag()) {
                    //设置为被揭开
                    gridObject.setFlag(true);
                    //设置为无法点击
                    gridObject.setClickFlag(false);
                    //设置为雷图片
                    gridObject.setImage(GameBoard.class.getResource("/images/mine.jpg"));
                }
            }
        }
    }
    // 被点击的方块，需要处理点击情况
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        if(button.equals(reset)) {
            //重新开始游戏
            new Menu("扫雷");
            // 关闭当前菜单窗口并弹出与之对应的游戏窗口。
            this.dispose();
        }else {
            Grid g = (Grid)e.getSource();
            g.click();
        }
    }

    public CountingThread getThread() {
        return thread;
    }

    public void setThread(CountingThread thread) {
        this.thread = thread;
    }

    public int getMines() {
        return mines;
    }

    public class CountingThread extends Thread{
        //当游戏结束，stopped为true，刚开始为false
        public boolean stopped = false;
        private CountingThread() {
            //设置线程为守护线程
            setDaemon(true);
        }
        @Override
        public void run() {
            while (true) {
                if (stopped) {
                    //获取当前时间
                    long elapsed = (System.currentTimeMillis() - startTime)/1000;
                    //展示到文本中
                    time.setText("时间: " + format(elapsed));
                }
                try {
                    // 1秒更新一次显示
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        // 将秒数格式化
        private String format(long elapsed) {
            int hour, minute, second;

            second = (int) (elapsed % 60);
            elapsed = elapsed / 60;

            minute = (int) (elapsed % 60);
            elapsed = elapsed / 60;

            hour = (int) (elapsed % 60);

            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
    }
}

