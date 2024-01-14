package Views;

import java.awt.*;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.PanelUI;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.text.AttributeSet.ColorAttribute;
import javax.swing.text.AttributeSet.FontAttribute;

import Canvas.BoardGraphics;

import javax.swing.border.Border;

import Controller.Controller;
import Models.SudokuModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SudokuGame extends JFrame implements ActionListener {
    // Khai báo các thành phần giao diện như JTextField, JButton, JComboBox, vv.
    JTextField[][] board = new JTextField[9][9];
    int[][] state = new int[9][9];
    int[][] stateSolve = new int[9][9];
    int numberCellIllegal = 0;
    JButton jbNew, jbSolved, jbCheck, jbClear, jbSave, jbLoad;
    JButton lbMessage;
    JButton lbTimer;
    JButton lbRecord;
    JComboBox<String> comboBox;
    Controller controller;
    SudokuModel model;
    Random random = new Random();
    Font font = new Font("Arial", Font.BOLD, 16);
    ColorUIResource colorMain = new ColorUIResource(0, 128, 255);
    Color colorButton = new Color(0, 128, 255);
    Color colorOpacity = new Color(93, 174, 255);
    ColorUIResource colorCell = new ColorUIResource(178, 231, 250);
    ColorUIResource colorText = new ColorUIResource(255, 51, 51);
    Color colorSolved = new Color(255, 221, 221);

    private static ScheduledExecutorService executorService;
    int countTime;

    // Constructor của lớp SudokuGame
    public SudokuGame(Controller controller, SudokuModel model) {
        this.controller = controller;
        this.model = model;
        init();
    }


    // Phương thức khởi tạo giao diện
    public void init() {
        setTitle("Sudoku Game");
        setSize(700, 500);
        createView();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Phương thức tạo giao diện chính
    public void createView() {
        
        // Tạo các thành phần giao diện như bảng Sudoku, nút chức năng, vv.
        JPanel container = new JPanel();
        add(container);
        container.setLayout(new BorderLayout());
        JLayeredPane boardPanel = new BoardGraphics(new GridLayout(9, 9, 2, 2));

        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        controlPanel.setBackground(Color.white);
        boardPanel.setBorder(new LineBorder(colorMain, 5));
        container.add(boardPanel, BorderLayout.CENTER);
        container.add(controlPanel, BorderLayout.EAST);

        // Tạo ô nhập Sudoku và hiển thị lên bảng
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {

                board[i][j] = new JTextField();
                board[i][j].addActionListener(this);
                board[i][j].setHorizontalAlignment(JTextField.CENTER);

                board[i][j].setBorder(new LineBorder(colorMain, 1));
                board[i][j].setFont(new Font("Arial", Font.BOLD, 25));
                board[i][j].setForeground(colorText);

                boardPanel.add(board[i][j]);

            }
        }

        // Tạo các nút chức năng và hiển thị trên giao diện
        comboBox = new JComboBox<String>(new String[] { "Dễ", "Trung Bình", "Khó" });
        comboBox.setPreferredSize(new DimensionUIResource(110, 20));
        comboBox.setBackground(colorMain);
        comboBox.setForeground(Color.white);
        comboBox.setOpaque(true);

        JLabel label = new JLabel("Độ khó: ");
        JPanel headerPanel = new JPanel();
        headerPanel.add(label);
        headerPanel.add(comboBox);
        headerPanel.setBackground(Color.white);
        headerPanel.setBorder(new LineBorder(colorMain, 2));

        jbNew = new JButton("Trò chơi mới");
        jbNew.addActionListener(this);
        setUI(jbNew);
        setHover(jbNew);

        jbSolved = new JButton("Giải");
        setUI(jbSolved);
        setHover(jbSolved);
        jbSolved.addActionListener(this);

        jbCheck = new JButton("Kiểm tra");
        setUI(jbCheck);
        setHover(jbCheck);
        jbCheck.addActionListener(this);

        jbSave = new JButton("Lưu trò chơi");
        setUI(jbSave);
        setHover(jbSave);
        jbSave.addActionListener(this);

        jbLoad = new JButton("Tiếp tục trò chơi");
        setUI(jbLoad);
        setHover(jbLoad);
        jbLoad.addActionListener(this);

        JPanel panelBt = new JPanel();
        panelBt.setPreferredSize(new DimensionUIResource(200, 220));
        panelBt.setLayout(new GridLayout(7, 1, 5, 5));
        panelBt.add(headerPanel);

        panelBt.add(jbNew);
        panelBt.add(jbSolved);
        panelBt.add(jbCheck);
        panelBt.add(jbSave);
        panelBt.add(jbLoad);

        lbMessage = new JButton("Hãy tạo trò chơi mới");

        JButton lbTitle = new JButton("Sudoku Game");
        lbTitle.setBackground(Color.white);
        lbTitle.setBorder(null);
        lbTitle.setEnabled(true);
        lbTitle.setFont(new Font("Arial", Font.BOLD, 25));
        lbTitle.setForeground(colorMain);

        lbMessage.setFont(new Font("Arial", Font.BOLD, 15));
        lbMessage.setBackground(Color.white);
        lbMessage.setBorder(null);
        lbMessage.setEnabled(true);

        lbTimer = new JButton();
        lbTimer.setFont(new Font("Arial", Font.BOLD, 15));
        lbTimer.setBackground(Color.white);
        lbTimer.setBorder(null);
        lbTimer.setEnabled(false);

        lbRecord = new JButton();
        lbRecord.setFont(new Font("Arial", Font.BOLD, 15));
        lbRecord.setBackground(Color.white);
        lbRecord.setBorder(null);
        lbRecord.setEnabled(false);

        JPanel panelInfo = new JPanel();
        panelInfo.add(lbTitle);
        panelInfo.add(lbMessage);
        panelInfo.add(lbTimer);
        panelInfo.add(lbRecord);    
        panelInfo.setBorder(new LineBorder(colorMain,2));
        panelInfo.setLayout(new GridLayout(6, 1, 10, 10));

        panelInfo.setBackground(Color.white);

        controlPanel.add(panelInfo,BorderLayout.CENTER);
        controlPanel.add(panelBt,BorderLayout.NORTH);
    }

    // Các phương thức hỗ trợ thiết lập giao diện
    public void setUI(JComponent component) {
        component.setBackground(colorButton); // Đặt màu nền của thành phần thành colorButton.
        component.setForeground(Color.white); //: Đặt màu văn bản của thành phần thành trắng.
        component.setFont(font); // Đặt kiểu chữ của thành phần thành font.
        component.setCursor(new Cursor(Cursor.HAND_CURSOR));  //Thiết lập con trỏ chuột thành con trỏ tay,
        component.setBorder(null); // Loại bỏ đường viền của thành phần.

    }

    public void setHover(JComponent component) { //thêm hiệu ứng hover (khi di chuột vào và ra khỏi thành phần) cho một JComponent.
        component.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                component.setBackground(colorOpacity);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                component.setBackground(colorButton);
            }
        });
    }

    public void level() {         // Thiết lập độ khó của trò chơi Sudoku
        int level = 2;

        switch (comboBox.getSelectedIndex()) {
            case 0:
                level = 2;
                break;
            case 1:
                level = 3;
                break;
            case 2:
                level = 4;
                break;

            default:
                break;
        } 
        
        for (int l = 0; l < level; l++) { //level lần lặp
            for (int k = 0; k < 9; k++) {
                int i = 1 + random.nextInt(8);
                state[k][i] = 0; //đặt giá trị hàng k cột i = 0 <=> xóa khỏi bảng
            }
            for (int k = 0; k < 9; k++) {
                int i = 1 + random.nextInt(8);
                state[i][k] = 0;
            }
        }

    }
    // Kiểm tra xem người chơi đã giải đúng Sudoku chưa
    public void checkWin() {
        boolean win = true; // Biến đánh dấu người chơi có giải đúng không, ban đầu được đặt là true
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j].isEditable()) { // Nếu ô đó có thể chỉnh sửa (là ô mà người chơi điền)
                    String inputUser = board[i][j].getText(); // Lấy giá trị mà người chơi đã nhập
                    if (!inputUser.equals(String.valueOf(stateSolve[i][j]))) { // Nếu giá trị nhập không đúng
                        win = false; // Đánh dấu là người chơi chưa giải đúng
                        numberCellIllegal++; // Tăng số ô không hợp lệ
                        board[i][j].setBackground(Color.red); // Đặt màu nền của ô thành màu đỏ
                        board[i][j].setForeground(Color.yellow); // Đặt màu văn bản của ô thành màu vàng
                    } else {
                        board[i][j].setBackground(Color.white); // Nếu giá trị nhập đúng, đặt màu nền của ô thành trắng
                        board[i][j].setForeground(Color.black); // Đặt màu văn bản của ô thành đen
                    }
                }
            }
        }
        if (win == true) { // Nếu người chơi đã giải đúng Sudoku
            JOptionPane.showMessageDialog(this, "Chúc mừng bạn đã giải thành công"); // Hiển thị thông báo chúc mừng
            String textDisplay;
            if(model.SaveRecord(comboBox.getSelectedIndex() + 2, countTime)){
                textDisplay = "Phá vỡ kỉ lục!!! \n Tạo trò chơi mới";
            }
            else textDisplay = "Winer !!!\n Tạo trò chơi mới";
            lbMessage.setText(textDisplay); // Đặt văn bản thông báo trên label
            stopTimer();
        } else {
            lbMessage.setText(numberCellIllegal + " ô không hợp lệ"); // Đặt văn bản thông báo số ô không hợp lệ trên label
        }
        numberCellIllegal = 0; // Đặt lại số ô không hợp lệ về 0 cho lần kiểm tra tiếp theo
    }


    public void display2(int board[][]) {         // Hiển thị trạng thái bảng Sudoku
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
            }
        }
    }

    public void resetBoard() {         // Đặt lại bảng Sudoku về trạng thái ban đầu
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j].setText("");
                state[i][j] = 0;
                board[i][j].setBackground(Color.white);
                board[i][j].setEditable(true);
                board[i][j].setForeground(colorText);
            }
        }
    }

    public boolean isEmtry() {        // Kiểm tra xem bảng Sudoku có trống không
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!(state[i][j] == 0)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showBoard() {         // Hiển thị bảng Sudoku trên giao diện
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (state[i][j] != 0) {
                    board[i][j].setText(String.valueOf(state[i][j]));
                    board[i][j].setEditable(false);
                    board[i][j].setBackground(colorCell);
                    board[i][j].setForeground(Color.black);
                }
            }
        }
    }

    public void saveBoard(){ // Lưu dữ liệu của bảng hiện tại
        lbMessage.setText("Lưu dữ liệu thành công");
        model.SaveStateBoard(state, stateSolve, comboBox.getSelectedIndex() + 2, countTime);
    }

    public void loadBoard(){ // Lấy dữ liệu từ file để hiển thị
        lbMessage.setText("Lấy dữ liệu thành công");
        resetBoard();
        state = model.GetSavedState(comboBox.getSelectedIndex() + 2);
        stateSolve = model.GetSavedSolvedState(comboBox.getSelectedIndex() + 2);
        countTime = model.GetSaveTime(comboBox.getSelectedIndex() + 2);
        showBoard();
    }

    // Hàm để bắt đầu thời gian
    public void startTimer(){
        executorService = Executors.newSingleThreadScheduledExecutor();  // Tạo một đối tượng executorService với một thread duy nhất

        Runnable timerTask = () -> {  // Tạo một nhiệm vụ (Runnable) cho việc đếm thời gian
            countTime++;  //Tăng biến countTime mỗi lần nhiệm vụ được thực hiện
            String timeString = String.format("%02d:%02d", countTime / 60, countTime % 60); // Chuyển đổi thời gian từ giây sang chuỗi định dạng mm:ss
            lbTimer.setText("Timer: " + timeString);  // Cập nhật hiển thị của lbTimer
        };

        executorService.scheduleAtFixedRate(timerTask, 0, 1, TimeUnit.SECONDS); // Khởi tạo bộ đếm thời gian với delay = 0 và thời gian đếm là 1 giây
    }

    // Hàm để dừng thời gian
    public void stopTimer(){
        if(executorService == null) return;    // Kiểm tra xem executorService có tồn tại không
        executorService.shutdown();     // Tắt executorService
        try {
            // Chờ cho task được hoàn thành trong vòng 1 giây
            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                // Nếu task chưa hoàn thành sau 1 giây, bắt buộc tắt ngay lập tức
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) { // Xử lý ngoại lệ InterruptedException (nếu có)
            e.printStackTrace();
        }
    }

    public int[][] copyState(int[][] state) { // Sao chép trạng thái của bảng Sudoku
        int[][] result = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                result[i][j] = state[i][j];
            }
        }
        return result;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbNew) { //nút Trò chơi mới
            resetBoard();
            controller.makeNewGame();
            state = copyState(model.getGenome().getState());
            stateSolve = copyState(model.getGenome().getState());
            level();
            showBoard();
            countTime = 0;
            stopTimer();
            startTimer();
            if(model.isSuccess()) {
                lbMessage.setText("Tạo trò chơi thành công");
                String record = model.GetRecord(comboBox.getSelectedIndex() + 2) == -1 ? "Lần đầu chơi!!!" : Integer.toString( model.GetRecord(comboBox.getSelectedIndex() + 2));
                lbRecord.setText("Kỉ lục " + comboBox.getSelectedItem().toString() + " " + record);
            }else {
                
                lbMessage.setText("Tạo trò chơi thất bại");
            }
        }
        if (e.getSource() == jbSolved) { // nút giải
            if (!isEmtry()) {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {

                        if (board[i][j].isEditable()) {
                            board[i][j].setText(String.valueOf(stateSolve[i][j]));
                            board[i][j].setBackground(colorSolved);
                            board[i][j].setForeground(Color.red);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Bạn phải tạo trò chơi mới");

            }
        }
        if (e.getSource() == jbCheck) { //nút kiểm tra
            checkWin();
        }
        if(e.getSource() == jbSave){
            saveBoard();
        }
        if(e.getSource() == jbLoad){
            loadBoard();
            // Lấy thông tin về kỷ lục (record) từ model, ếu kỷ lục là -1, có thể hiểu là lần đầu chơi, ngược lại thì hiển thị thời gian kỷ lục.
            String record = model.GetRecord(comboBox.getSelectedIndex() + 2) == -1 ? "Lần đầu chơi!!!" : Integer.toString( model.GetRecord(comboBox.getSelectedIndex() + 2));
            lbRecord.setText("Kỉ lục " + comboBox.getSelectedItem().toString() + " " + record);
            stopTimer();
            startTimer();
        }
    }
}




//ExecutorService và TimerTask: Được sử dụng để thực hiện tính năng đếm thời gian trong trò chơi.
//ExecutorService được sử dụng để lên lịch thực hiện một nhiệm vụ (Runnable) cứ mỗi giây.
