package Models;

import java.util.HashSet;
import java.util.Random;

public class Genome implements Comparable<Genome> {
    int state[][] = new int[9][9]; // Mảng 2 chiều chứa trạng thái Sudoku 9x9
    static Random random = new Random();     // Đối tượng Random để tạo số ngẫu nhiên
    public long length;
    public int crossoverPoint;
    public float currentFitness = 0.0f;     // Biểu diễn độ thích nghi hiện tại của Genome
    int theMin = 0;
    int theMax = 1000;
    HashSet<Integer> rowMap = new HashSet<Integer>();
    HashSet<Integer> columnMap = new HashSet<Integer>();
    HashSet<Integer> squareMap = new HashSet<Integer>();


    @Override
    public int compareTo(Genome o) {
        if (this.currentFitness < o.currentFitness)
            return 1;
        else if (this.currentFitness > o.currentFitness)
            return -1;
        else
            return 0;
    }

    public void setCrossoverPoint(int crossoverPoint) {
        this.crossoverPoint = crossoverPoint;
    }

    public Genome() {

    }

    public Genome(long length, int min, int max) {    //sinh các số ngẫu nhiên
        this.length = length;
        this.theMax = max;
        this.theMin = min;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.state[i][j] = 1+random.nextInt(8);
            }
        }
    }

    public void mutate() {
        int mutationIndex1 = random.nextInt(9);
        int mutationIndex2 = random.nextInt(9);
        int mutationIndex3 = random.nextInt(9);
        if (random.nextInt(2) == 1) { //Thay đổi giá trị tại vị trí
            this.state[mutationIndex1][mutationIndex2] = mutationIndex3 + 1;
        } else { //Trao đổi giá trị giữa hai ô trong cùng một hàng hoặc cùng một cột
            int temp = 0;
            if (random.nextInt(2) == 1) {
                temp = this.state[mutationIndex1][mutationIndex2];
                this.state[mutationIndex1][mutationIndex2] = this.state[mutationIndex3][mutationIndex2];
                this.state[mutationIndex3][mutationIndex2] = temp;
            } else {
                temp = this.state[mutationIndex2][mutationIndex1];
                this.state[mutationIndex2][mutationIndex1] = this.state[mutationIndex2][mutationIndex3];
                this.state[mutationIndex2][mutationIndex3] = temp;
            }
        }
    } //tăng cơ hội tìm kiếm ra giải pháp tối ưu hơn cho sudoku

    public float calculateFitness() {
        float fitnesssRows = 0;
        float fitnessColumns = 0;
        float fitnessSquare = 0;
        // Rows and Columns
        for (int i = 0; i < 9; i++) {
            rowMap.clear();
            columnMap.clear();
            for (int j = 0; j < 9; j++) {
                rowMap.add(state[i][j]);
                columnMap.add(state[j][i]);
            }
            fitnesssRows += (float) (1.0f / (float) (9 + 1 - rowMap.size())) / 9.0f;
            fitnessColumns += (float) (1.0f / (float) (9 + 1 - columnMap.size())) / 9.0f;
        }
        // Square
        for (int l = 0; l < 3; l++) {
            for (int k = 0; k < 3; k++) {
                squareMap.clear();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        squareMap.add(state[i + k * 3][j + l * 3]);
                    }
                }
                fitnessSquare += (float) (1.0f / (float) (9 + 1 - squareMap.size())) / 9.0f;
            }
        }
        return currentFitness = fitnesssRows * fitnessColumns * fitnessSquare;
    }

    public int[][] getState() {
        return state;
    }

    public float calFitness() {
        calculateFitness();
        return currentFitness;
    }

    public void copy(Genome dest) {
        Genome gene = dest;
        gene.length = length;
        gene.theMin = theMin;
        gene.theMax = theMax;
    }

    public Genome crossOver(Genome g) { //lai ghép giữa 2 cá thể
        // Tạo hai cá thể con mới gene1 và gene2
        Genome gene1 = new Genome();
        Genome gene2 = new Genome();
        // Sao chép thông tin từ cá thể cha mẹ (g) vào gene1 và gene2
        g.copy(gene1);
        g.copy(gene2);

        // Nếu số ngẫu nhiên là 1, thực hiện lai ghép theo chiều ngang (cột)
        if (random.nextInt(2) == 1) {
            for (int j = 0; j < 9; j++) {
                crossoverPoint = random.nextInt(8) + 1; // Chọn một điểm ngẫu nhiên để thực hiện lai ghép
                for (int k = 0; k < crossoverPoint; k++) {        // Sao chép giá trị từ cha mẹ vào gene1 và gene2 cho đến điểm lai ghép
                    gene1.state[k][j] = g.state[k][j];
                    gene2.state[k][j] = this.state[k][j];
                }
                for (int k = crossoverPoint; k < 9; k++) {       // Sao chép giá trị từ cha mẹ vào gene1 và gene2 sau điểm lai ghép
                    gene2.state[k][j] = g.state[k][j];
                    gene1.state[k][j] = this.state[k][j];
                }
            }
        } else {
            // Nếu số ngẫu nhiên là 0, thực hiện lai ghép theo chiều dọc (hàng)
            for (int j = 0; j < 9; j++) {
                crossoverPoint = random.nextInt(8) + 1;
                for (int k = 0; k < crossoverPoint; k++) {
                    gene1.state[j][k] = g.state[j][k];
                    gene2.state[j][k] = this.state[j][k];
                }
                for (int k = crossoverPoint; k < 9; k++) {
                    gene2.state[j][k] = g.state[j][k];
                    gene1.state[j][k] = this.state[j][k];
                }
            }
        }
        Genome gene = null;
        if (random.nextInt(2) == 1) {
            gene = gene1;
        } else
            gene = gene2; // Chọn ngẫu nhiên giữa gene1 và gene2 để trả về cá thể con

        return gene; // Trả về cá thể con sau khi đã thực hiện quá trình lai ghép

    }
}
