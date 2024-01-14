package Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;


public class Population {
    protected int kLength = 9;
    protected int kCrossover = kLength / 2;
    protected int kInitialPopulation = 1000;
    protected int kPopulationLimit = 50;
    protected int kMin = 1;
    protected int kMax = 1000;
    protected float kMutationFrequency = 0.33f;
    protected ArrayList<Genome> Genomes = new ArrayList<Genome>();
    protected ArrayList<Genome> GenomeReproducers = new ArrayList<Genome>();
    protected ArrayList<Genome> GenomeResults = new ArrayList<Genome>();
    protected ArrayList<Genome> GenomeFamily = new ArrayList<Genome>();

    protected int CurrentPopulation = kInitialPopulation;
    protected int generation = 1;
    protected boolean best2 = true;
    protected Genome lastGenome;
    public int[][] board = new int[9][9];
    static final int SIZE = 9;
    static Random r = new Random();
    boolean isMakeNewGameSuccess = true;

    public Population() {

    }

    // Tạo quần thể ban đầu với các Genome ngẫu nhiên và tính toán độ thích nghi
    public void makePopulation() {
        for (int i = 0; i < kInitialPopulation; i++) {
            Genome aGenome = new Genome(kLength, kMin, kMax);
            aGenome.setCrossoverPoint(kCrossover);
            aGenome.calFitness();
            Genomes.add(aGenome);
        }
    }

    // Đột biến Genome với xác suất nhất định
    public void mutate(Genome aGene) {
        if (Genome.random.nextInt(100) < (int) (kMutationFrequency * 100.0)) {//33%
            aGene.mutate();
        }
    }

    // Tính toán độ thích nghi cho tất cả các Genome trong một danh sách
    public void calcculateFitnessForAll(ArrayList<Genome> genes) {
        for (Genome g : genes) {
            g.calculateFitness();
        }
    }

    // Thực hiện quá trình lai ghép giữa các Genome
    public void doCrossover(ArrayList<Genome> genes) {
        ArrayList<Genome> geneMoms = new ArrayList<Genome>();
        ArrayList<Genome> geneDads = new ArrayList<Genome>();
        for (int i = 0; i < genes.size(); i++) {
            if (Genome.random.nextInt(100) % 2 > 0) {
                geneMoms.add(genes.get(i));
            } else {
                geneDads.add(genes.get(i));
            }
        }
        // Can bang
        if (geneMoms.size() > geneDads.size()) {
            while (geneMoms.size() > geneDads.size()) {
                geneDads.add(geneMoms.get(geneMoms.size() - 1));
                geneMoms.remove(geneMoms.size() - 1);
            }
            if (geneDads.size() > geneMoms.size()) {
                geneDads.remove(geneDads.size() - 1);
            }
        } else {
            while (geneDads.size() > geneMoms.size()) {
                geneMoms.add(geneDads.get(geneDads.size() - 1));
                geneDads.remove(geneDads.size() - 1);
            }
            if (geneMoms.size() > geneDads.size()) {
                geneMoms.remove(geneMoms.size() - 1);
            }
        }
        // Lai ghep
        for (int i = 0; i < geneDads.size(); i++) {
            Genome childGene1 = geneDads.get(i).crossOver(geneMoms.get(i));
            Genome childGene2 = geneMoms.get(i).crossOver(geneDads.get(i));
            GenomeFamily.clear();
            GenomeFamily.add(geneDads.get(i));
            GenomeFamily.add(geneMoms.get(i));
            GenomeFamily.add(childGene1);
            GenomeFamily.add(childGene2);
            calcculateFitnessForAll(GenomeFamily);
            Collections.sort(GenomeFamily);
            GenomeResults.add(GenomeFamily.get(0));
            GenomeResults.add(GenomeFamily.get(1));
        }
    }

    // Tạo thế hệ tiếp theo của quần thể
    public void nextGeneration() {
        generation++;
        GenomeResults.clear();
        doCrossover(Genomes);
        Genomes = (ArrayList<Genome>) GenomeResults.clone();
        for (int i = 0; i < Genomes.size(); i++) {
            mutate(Genomes.get(i));
        }
        for (int i = 0; i < Genomes.size(); i++) {
            Genomes.get(i).calFitness();
        }
        Collections.sort(Genomes);

        for (int i = Genomes.size() - 1; i > kPopulationLimit; i--) {
            Genomes.remove(i);

        }
        CurrentPopulation = Genomes.size();
    }

    // Trả về Genome tốt nhất trong quần thể
    public Genome getGenome() {
        return lastGenome;
    }

    // Tạo một trò chơi Sudoku mới
    public void makeNewGame() {
        makePopulation();
        for (int i = 0; i < 1000; i++) { // tạo ra 1000 thế hệ để tối ưu hóa lời giải Sudoku.
            nextGeneration();
            Collections.sort(Genomes);     // Sắp xếp danh sách Genomes theo thứ tự giảm dần của độ thích nghi.
            lastGenome = Genomes.get(0);    // Lấy Genome có độ thích nghi cao nhất (Genome đầu tiên sau khi sắp xếp)
        }
        int[][] lastState = lastGenome.getState();
        makeProblem(lastState);
        solveGame();
    }


    // Giải trò chơi Sudoku sử dụng thuật toán Backtracking
    public void solveGame() {

        if (solve()) {
            System.out.println("Tạo Thành công");
            isMakeNewGameSuccess = true;
//            System.out.println("Hàm đánh giá: " + calculateFitness(lastGenome.getState()));

        } else {
            System.out.println("Thất bại");
            isMakeNewGameSuccess = false;
        }
        GenomeFamily.clear();
        GenomeResults.clear();
        GenomeReproducers.clear();
        Genomes.clear();
    }

    // Kiểm tra xem việc tạo trò chơi mới có thành công không
    public boolean isMakeNewGameSuccess() {
        return isMakeNewGameSuccess;
    }

    public void makeProblem(int[][] state) {
        // Loại bỏ các giá trị trùng lặp trong từng hàng và cột
        for (int k = 0; k < 9; k++) {
            for (int i = 0; i < 9; i++) {
                if (i < 8) {
                    for (int j = i + 1; j < 9; j++) {
                        if (state[k][i] == state[k][j]) {
                            state[k][i] = 0;
                        }
                    }
                }
            }
        }
        for (int k = 0; k < 9; k++) {
            for (int i = 0; i < 9; i++) {
                if (i < 8) {
                    for (int j = i + 1; j < 9; j++) {
                        if (state[i][k] == state[j][k]) {
                            state[i][k] = 0;
                        }
                    }
                }
            }
        }
        // Loại bỏ ngẫu nhiên một số ô
        for (int l = 0; l < 5; l++) {
            for (int k = 0; k < 9; k++) {
                int i = 1 + r.nextInt(8);
                state[k][i] = 0;
            }
            for (int k = 0; k < 9; k++) {
                int i = 1 + r.nextInt(8);
                state[i][k] = 0;
            }
        }
    }

    // we check if a possible number is already in a row
    private boolean isInRow(int row, int number) {
        for (int i = 0; i < SIZE; i++)
            if (lastGenome.getState()[row][i] == number)
                return true;

        return false;
    }

    // we check if a possible number is already in a column
    private boolean isInCol(int col, int number) {
        for (int i = 0; i < SIZE; i++)
            if (lastGenome.getState()[i][col] == number)
                return true;

        return false;
    }

    // we check if a possible number is in its 3x3 box
    private boolean isInBox(int row, int col, int number) {
        int r = row - row % 3;
        int c = col - col % 3;

        for (int i = r; i < r + 3; i++)
            for (int j = c; j < c + 3; j++)
                if (lastGenome.getState()[i][j] == number)
                    return true;

        return false;
    }

    // combined method to check if a number possible to a row,col position is ok
    private boolean isOk(int row, int col, int number) {
        return !isInRow(row, number) && !isInCol(col, number) && !isInBox(row, col, number);
    }

    // Solve method. We will use a recursive BackTracking algorithm
    public boolean solve() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                // Tìm ô trống
                if (lastGenome.getState()[row][col] == 0) {
                    // Thử các số khả dụng
                    for (int number = 1; number <= SIZE; number++) {
                        if (isOk(row, col, number)) {
                            // Số hợp lệ. Điều này đáp ứng các ràng buộc của Sudoku
                            lastGenome.getState()[row][col] = number;

                            if (solve()) { // Bắt đầu backtracking đệ quy
                                return true;
                            } else {  // Nếu không tìm ra giải pháp, ta xóa ô và tiếp tục thử số khác
                                lastGenome.getState()[row][col] = 0;
                            }
                        }
                    }

                    return false; // Trả về false nếu không có giải pháp
                }
            }
        }

        return true; // Sudoku đã được giải
    }
}
