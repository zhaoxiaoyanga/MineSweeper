package menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

class Grid extends JButton implements MouseListener {

    private boolean mine;    //是否是雷
    private boolean flag;    //方块是否已经被打开
    private boolean clickFlag;    //方块可以被打开
    private boolean redFlag;    //方块是否标记小红旗
    private boolean doubtFlag;    //方块是否标记问号
    protected int xLocation;    //方块的 x 坐标
    protected int yLocation;    //方块的 y 坐标
    protected GameBoard board;    //方块所在的游戏窗口
    private int boardHeight;    //实例的高度
    private int boardWidth;        //实例的宽度

    static Image gray = new ImageIcon(Grid.class.getResource("/images/gray.jpg")).getImage();


    public Grid(int x, int y, GameBoard board) {
        // 初始化时将方块变成白色。
        super(new ImageIcon(gray));

        this.board = board;
        //坐标
        xLocation = x;
        yLocation = y;
        // 长宽
        boardHeight = 50;
        boardWidth = 50;
        this.setSize(boardHeight, boardWidth);
        //初始化
        mine = false;        //是否是雷
        flag = false;        //默认方块被覆盖
        clickFlag = true;    //默认方块可以被点击
        redFlag = false;    //默认方块没有小红旗
        doubtFlag = false;    //默认没有标记问号
        //添加右键监听事件
        addMouseListener(this);
    }
    //点击方块
    public void click() {
        //如果方块被揭开，或者被标记上小红旗或问号，无法执行其他操作
        if (!isClickFlag() || isFlag()) {
            return;
        }
        //如果游戏刚开始
        if (!board.getThread().stopped) {
            //开始计时
            board.getThread().stopped = true;
            //随机生成地雷
            board.produceMine(board.getMines(), xLocation, yLocation);
        }
        //点击之后如果是雷，显示炸弹，游戏结束
        if (isMine()) {
            //结束计时
            board.getThread().stopped = false;
            //将打开的这个设置为雷
            setImage(Grid.class.getResource("/images/mine.jpg"));
            //显示所有没被打开的雷
            board.showMine();
            //弹出提示信息
            windowShow("不要灰心!  再来一局?", "游戏失败",
                    new ImageIcon(Grid.class.getResource("/images/failFace.jpg")));
        } else {
            //检查附近的雷，并展示出来
            board.checkGrid(xLocation, yLocation);
            //设置方块无法被点击
            setClickFlag(false);
            //检查是否成功
            if (board.isSuccess()) {
                //结束计时
                board.getThread().stopped = false;
                //显示所有没被打开的雷
                board.showMine();
                //弹出提示信息
                windowShow("赢得游戏!  再来一局?", "恭喜通关",
                        new ImageIcon(Grid.class.getResource("/images/passFace.jpg")));
            }
        }
    }

    boolean isMine() {
        return mine;
    }

    void setClickFlag(boolean b) {
        this.clickFlag = b;
    }

    boolean isFlag() {
        return flag;
    }

    private boolean isClickFlag() {
        return clickFlag;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        //如果方块被点击过或者被揭开，无法执行其他操作
        if (isFlag()) {
            return;
        }
        //如果右键点击
        if (e.getButton() == MouseEvent.BUTTON3) {
            //标记一次，覆盖上小红旗
            if(!isRedFlag() && !isDoubtFlag()) {
                setRedFlag(true);
                //此时无法被点击
                setClickFlag(false);
                setImage(Grid.class.getResource("/images/flag.jpg"));
                return;
            }
            //标记第二次，小红旗标记为问号
            if(isRedFlag()&& !isDoubtFlag()) {
                setRedFlag(false);
                setDoubtFlag(true);
                setImage(Grid.class.getResource("/images/doubt.jpg"));
                return;
            }
            //标记第三次，取消标记
            if(!isRedFlag() && isDoubtFlag()) {
                //此时可以被点击
                setClickFlag(true);
                setDoubtFlag(false);
                setImage(Grid.class.getResource("/images/gray.jpg"));
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


    private void setRedFlag(boolean b) {
        this.redFlag = b;
    }

    private boolean isDoubtFlag() {
        return doubtFlag;
    }

    private boolean isRedFlag() {
        return redFlag;
    }

    private void setDoubtFlag(boolean b) {
        this.doubtFlag = b;
    }

    //窗口展示信息
    public void windowShow(String msg, String title, Icon img) {
        int choose = JOptionPane.showConfirmDialog(board, msg, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,img);

        if (choose == JOptionPane.YES_OPTION){
            new Menu("扫雷");
        }
        // 关闭弹出窗口并重返菜单。
        board.dispose();
    }
    //方块展示
    public void setImage(URL filename){
        this.setIcon(new ImageIcon(filename));
    }

    public void setFlag(boolean b) {
        this.flag = b;
    }

    public void setMine(boolean b) {
        this.mine = b;
    }
}