package Controller;

import Models.SudokuModel;
import Views.SudokuGame;

public class SudokuController implements Controller {
    SudokuModel model;
    SudokuGame view;
    // Constructor nhận một đối tượng SudokuModel làm tham số
    public SudokuController (SudokuModel model) {
        this.model = model;
        view = new SudokuGame(this, model);   // Tạo một đối tượng SudokuGame và truyền controller và model vào nó
    }
    @Override
    public void makeNewGame() {
        model.makeNewGame();
    }  // Gọi phương thức makeNewGame của SudokuModel

}