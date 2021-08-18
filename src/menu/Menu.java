package menu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class Menu extends JFrame implements ActionListener {

    private JButton start;
    private JRadioButton simple, ordinary, hard, custom;
    private JTextField width, height, mines;

    //创建一个主菜单
    public Menu(String title) {
        // 设置菜单标题。
        setTitle(title);

        // 创建菜单子标题。
        JLabel subtitle = new JLabel("难度选择");
        //由 x和 y指定左上角的新位置，由 width 和 height 指定新的大小。
        subtitle.setBounds(100, 10, 100, 20);
        add(subtitle);

        //初级选择按钮。
        simple = new JRadioButton("容易");
        simple.setBounds(40, 40, 150, 20);

        add(simple);
        //初级选择的描述。
        JLabel bDescFirstLine = new JLabel("10个雷");
        bDescFirstLine.setBounds(70, 60, 100, 20);
        JLabel bDescSecondLine = new JLabel("10 x 8 方格");
        bDescSecondLine.setBounds(70, 80, 100, 20);
        add(bDescFirstLine);
        add(bDescSecondLine);

        //中级选择按钮。
        ordinary = new JRadioButton("一般");
        ordinary.setBounds(40, 100, 150, 20);
        add(ordinary);

        //中级选择的描述。
        JLabel iDescFirstLine = new JLabel("30个雷");
        iDescFirstLine.setBounds(70, 120, 100, 20);
        JLabel iDescSecondLine = new JLabel("16 x 12 方格");
        iDescSecondLine.setBounds(70, 140, 100, 20);
        add(iDescFirstLine);
        add(iDescSecondLine);

        //高级选择按钮。
        hard = new JRadioButton("困难");
        hard.setBounds(40, 160, 160, 20);
        add(hard);

        //高级选择的描述。
        JLabel aDescFirstLine = new JLabel("50个雷");
        aDescFirstLine.setBounds(70, 180, 100, 20);
        JLabel aDescSecondLine = new JLabel("20 x 16 方格");
        aDescSecondLine.setBounds(70, 200, 100, 20);
        add(aDescFirstLine);
        add(aDescSecondLine);

        // 创建 "自定义" 选择按钮。
        custom = new JRadioButton("自定义");
        custom.setBounds(40, 220, 100, 20);
        add(custom);

        // 设置 "自定义" 选择的描述。
        JLabel widthLabel = new JLabel("宽度 (10-30):");
        widthLabel.setBounds(70, 240, 80, 20);
        add(widthLabel);

        width = new JTextField();
        width.setBounds(170, 240, 40, 20);
        add(width);

        JLabel heightLabel = new JLabel("高度 (8-18):");
        heightLabel.setBounds(70, 260, 90, 20);
        add(heightLabel);

        height = new JTextField();
        height.setBounds(170, 260, 40, 20);
        add(height);

        JLabel mineLabel = new JLabel("雷数 (10-100):");
        mineLabel.setBounds(70, 280, 90, 20);
        add(mineLabel);

        mines = new JTextField();
        mines.setBounds(170, 280, 40, 20);
        add(mines);

        // 创建 "开始游戏" 选择按钮。
        start = new JButton("开始游戏");
        start.setBounds(80, 320, 100, 20);
        add(start);

        // 初始化每个文本框的编辑状态。
        width.setEditable(false);
        height.setEditable(false);
        mines.setEditable(false);

        // 在每个按键上添加监听事件。
        custom.addActionListener((ActionListener) this);
        simple.addActionListener(this);
        ordinary.addActionListener(this);
        hard.addActionListener(this);
        start.addActionListener(this);

        // 确保单选。
        ButtonGroup group = new ButtonGroup();
        group.add(simple);
        group.add(ordinary);
        group.add(hard);
        group.add(custom);

        // 初始化菜单实例。
        simple.setSelected(true);
        setSize(280, 400);
        //将容器的布局设为绝对布局
        setLayout(null);
        //可视化窗口
        setVisible(true);
        //大小不能改变
        setResizable(false);
        //关闭按钮，点击它时，退出程序，并结束进程
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //显示坐标
        setLocation(500, 200);
    }
    //实现接口，对用户操作做出反应
    @Override
    public void actionPerformed(ActionEvent e) {
        // 如果用户选择 "自定义"
        if (e.getSource() == custom){
            //设置文本框为可编辑状态。
            width.setEditable(true);
            height.setEditable(true);
            mines.setEditable(true);
        }else if (e.getSource() == start) {
            // 如果用户点击 "开始游戏" 按钮，获得相对应的炸弹总数，游戏窗口的长和宽。
            int boardWidth = 0;
            int boardHeight = 0;
            int mine = 0;
            boolean errorFlag = false;

            //选择简单
            if (simple.isSelected()){
                boardWidth = 10;
                boardHeight = 8;
                mine = 10;
            } else if (ordinary.isSelected()) {
                //选择一般
                boardWidth = 16;
                boardHeight = 12;
                mine = 30;
            } else if (hard.isSelected()) {
                //选择困难
                boardWidth = 20;
                boardHeight = 16;
                mine = 50;
            } else {
                if(!checkValid(width.getText(), height.getText(), mines.getText()))
                {
                    // 设置标记并在窗口上弹出错误提示。
                    errorFlag = true;
                    JOptionPane.showMessageDialog(null, "请输入正确的数据!");

                } else {
                    boardWidth = Integer.parseInt(width.getText());
                    boardHeight = Integer.parseInt(height.getText());
                    mine = Integer.parseInt(mines.getText());
                }
            }

            if(!errorFlag)
            {
                // 关闭当前菜单窗口并弹出与之对应的游戏窗口。
                this.dispose();
                GameBoard b = new GameBoard("扫雷", boardWidth, boardHeight, mine);
            }

        } else{
            // 如果玩家即没有选择 "Custom" 也没有点击 "New Game" 按钮，这些文本框要设置成不可编辑的状态。
            width.setEditable(false);
            height.setEditable(false);
            mines.setEditable(false);
        }
    }
    private boolean checkValid(String bWidth, String bHeight, String bomb) {
        Pattern pattern = Pattern.compile("[0-9]*");
        //如果三者有一个为空
        if (bWidth == null || bHeight== null || bomb == null)
            return false;
        else if (bWidth.isEmpty() || bHeight.isEmpty() || bomb.isEmpty())
            return false;
            //如果有一个不是数字
        else if (!pattern.matcher(bWidth).matches() || !pattern.matcher(bHeight).matches() || !pattern.matcher(bomb).matches())
            return false;
        else if (Integer.parseInt(bWidth) < 10 || Integer.parseInt(bWidth) > 30 || Integer.parseInt(bHeight) < 8 || Integer.parseInt(bHeight) > 18
                || Integer.parseInt(bomb) < 10 || Integer.parseInt(bomb) > 100)
            return false;
        else
            return Integer.parseInt(bWidth) * Integer.parseInt(bHeight) >= Integer.parseInt(bomb);
    }
}
