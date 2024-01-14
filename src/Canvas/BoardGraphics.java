package Canvas;

import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.plaf.FontUIResource;

public class BoardGraphics extends JLayeredPane {
    GridLayout grid;
    // Constructor nhận một đối tượng GridLayout làm tham số
    public BoardGraphics(GridLayout grid) {
        this.grid=grid;
        this.setLayout(grid);

    }
    // Phương thức để vẽ lưới Sudoku
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // Đảm bảo rằng các thành phần cha được vẽ

        Graphics2D g2d = (Graphics2D) g;
        Stroke stroke1 = new BasicStroke(5f);   // Đặt độ dày của đường vẽ
        g2d.setStroke(stroke1);

        int x = 10;
//        g2d.drawRect(x, x, this.getWidth()-x, this.getHeight()-x);
        int widthCell = (int)((this.getWidth()-20)/3);
        int heightCell =(int)((this.getHeight()-20)/3);

        // Vẽ các đường kẻ ngang của lưới Sudoku
        for (int i = x+widthCell; i <= this.getWidth()-20; i += widthCell) {
            g2d.drawLine(i, x, i, this.getHeight()-20+x);
        }
        // Vẽ các đường kẻ dọc của lưới Sudoku
        for (int i = x+heightCell; i <= this.getHeight()-20; i += heightCell) {
            g2d.drawLine(x, i, this.getWidth()-20+x, i);
        }
    }

}
