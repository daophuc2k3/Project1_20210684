package Models;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SudokuModel {
    Population population;
    public SudokuModel() {
        population = new Population();
    }
    public void makeNewGame() {
        population.makeNewGame();
    }
    public Genome getGenome() {
        return population.getGenome();         // Trả về Genome tốt nhất từ population
    }
    public boolean isSuccess() {  // Kiểm tra xem việc tạo trò chơi mới có thành công không
        return population.isMakeNewGameSuccess();
    }

    // #region SaveLoad

    public int[][] GetSavedState(int level){
        String fileName = "Easy";
            switch (level) {
                case 3:
                    fileName = "Medium";
                    break;
                case 4:
                    fileName = "Hard";
                    break;
                default:
                    break;
            }
        int[][] x = new int[9][9];     // Khai báo một mảng 2 chiều có kích thước 9x9 để lưu trữ trạng thái đã lưu
        Path path = Paths.get(fileName + ".txt");     // Tạo đối tượng Path từ tên tệp và đường dẫn
        try{
            List<String> lines = Files.readAllLines(path);         // Đọc tất cả các dòng từ tệp và lưu vào danh sách (List)
            for(int i = 1; i < 10; i++){ // Vòng lặp qua từng dòng trong danh sách lines
                String line = lines.get(i);   // Lấy một dòng từ danh sách
                var listValue = line.split(",");           // Tách các giá trị trong dòng bằng dấu phẩy và lưu vào một mảng
                for(int j = 0; j < 9; j++){
                    x[i -1][j] = Integer.parseInt(listValue[j]);  // Vòng lặp qua từng giá trị trong mảng và chuyển đổi thành số nguyên
                }
            }
        } catch (IOException e){          // Xử lý ngoại lệ IOException (nếu có) - không làm gì cả (để trống)

        }
        return x;
    }

    public int[][] GetSavedSolvedState(int level){ // lưu những ô đã điền
        String fileName = "Easy";
            switch (level) {
                case 3:
                    fileName = "Medium";
                    break;
                case 4:
                    fileName = "Hard";
                    break;
                default:
                    break;
            }
        int[][] x = new int[9][9];
        Path path = Paths.get(fileName + ".txt");
        try{
            List<String> lines = Files.readAllLines(path);
            for(int i = 10; i < 19; i++){
                String line = lines.get(i);
                var listValue = line.split(",");
                for(int j = 0; j < 9; j++){
                    x[i - 10][j] = Integer.parseInt(listValue[j]);
                }
            }
        } catch (IOException e){

        }
        return x;
    }

    // Lấy thời gian lưu lại của level
    public int GetSaveTime(int level){
         String fileName = "Easy";
            switch (level) {
                case 3:
                    fileName = "Medium";
                    break;
                case 4:
                    fileName = "Hard";
                    break;
                default:
                    break;
            }
        int x;
        Path path = Paths.get(fileName + ".txt");
        try{
            List<String> lines = Files.readAllLines(path);    // Đọc tất cả các dòng từ tệp và lưu vào danh sách (List)
            x = Integer.parseInt(lines.get(lines.size() - 1));       // Lấy giá trị thời gian từ dòng cuối cùng của tệp và chuyển đổi thành số nguyên
        } catch (IOException e){    // Xử lý ngoại lệ IOException (nếu có) - đặt giá trị mặc định của x là 0
            x = 0;
        }
        return x;
    }

    // Hàm lưu dữ liệu của bảng
    public void SaveStateBoard(int[][] boardState, int[][] solveState, int level, int time){
        // Lấy fileName
        try {
            String fileName = "Easy";
            switch (level) {
                case 3:
                    fileName = "Medium";
                    break;
                case 4:
                    fileName = "Hard";
                    break;
                default:
                    break;
            }
            // Kiểm tra xem tệp đã tồn tại hay chưa, và tạo mới nếu chưa
            File saveFile = new File(fileName + ".txt");
            if (saveFile.createNewFile()) {
                System.out.println("File created: " + saveFile.getName());
            } else {
                System.out.println("File already exists.");
            }

            // Ghi vào file
            FileWriter writer = new FileWriter(saveFile);

            // Ghi cấp độ vào tệp
            writer.write(Integer.toString(level));
            writer.write(System.getProperty( "line.separator" ));
            for (int i = 0; i < 9; i++) {
                String line = "";
                for (int j = 0; j < 8; j++) {
                    line += Integer.toString(boardState[i][j]);
                    line += ",";
                }
                line += Integer.toString(boardState[i][8]);
                writer.write(line);
                writer.write(System.getProperty( "line.separator" ));
            }

            // Ghi trạng thái giải đáp vào tệp
            for (int i = 0; i < 9; i++) {
                String line = "";
                for (int j = 0; j < 8; j++) {
                    line += Integer.toString(solveState[i][j]);
                    line += ",";
                }
                line += Integer.toString(solveState[i][8]);
                writer.write(line);
                writer.write(System.getProperty( "line.separator" ));
            }
            // Ghi thời gian vào tệp
            writer.write(Integer.toString(time));
            writer.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    //#endregion

    //#region Record Timer

    // Lưu lại record - Ở lần chơi đầu sẽ lưu vào file luôn, không cần check xem thời gian ở mức nào.
    public boolean SaveRecord(int level, int time){

        var timeRecord = GetRecord(level);
        if(time > timeRecord && timeRecord > 0) return false;

        String fileName = "EasyTime";
            switch (level) {
                case 3:
                    fileName = "MediumTime";
                    break;
                case 4:
                    fileName = "HardTime";
                    break;
                default:
                    break;
            }
        File saveFile = new File(fileName + ".txt");
        try {
            if (saveFile.createNewFile()) { //Thử tạo một file mới. Nếu file đã tồn tại, hiển thị thông điệp tương ứng.
            System.out.println("File created: " + saveFile.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileWriter writer = new FileWriter(saveFile); //: Ghi thời gian mới vào file. Chuyển đổi thời gian thành chuỗi trước khi ghi vào file.
            writer.write(Integer.toString(time));
            writer.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return true;
    }

    // Lấy record thời gian theo độ khó - Nếu chơi lần đầu thì thời gian lấy được là -1.
    public int GetRecord(int level){
        String fileName = "EasyTime";
            switch (level) {
                case 3:
                    fileName = "MediumTime";
                    break;
                case 4:
                    fileName = "HardTime";
                    break;
                default:
                    break;
            }
        Path path = Paths.get(fileName + ".txt");
        try {
            String highScore = Files.readString(path);
            return Integer.parseInt(highScore);
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return -1;
        }
    }

    //#endregion

    public static void main(String[] args) {
        SudokuModel s = new SudokuModel();
        s.makeNewGame();
    }
}
