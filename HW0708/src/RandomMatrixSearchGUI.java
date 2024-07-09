import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomMatrixSearchGUI extends JFrame {
  private JTextField sizeField;
  private JButton generateButton;
  private JPanel matrixPanel;
  private JPanel sortedMatrixPanel;
  private JScrollPane matrixScrollPane;
  private JScrollPane sortedMatrixScrollPane;
  private JTextField searchField;
  private JButton searchButton;
  private JButton clearButton;
  private JCheckBox sequentialCheckBox;
  private JCheckBox binaryCheckBox;
  private JCheckBox hashCheckBox;
  private JTextArea resultArea;
  private int[][] matrix;
  private int[][] sortedMatrix;
  private Set<Integer> uniqueNumbers;

  public RandomMatrixSearchGUI() {
    setTitle("隨機矩陣生成與搜尋 - 陳憶柔D1204387");
    setSize(1200, 800);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    JPanel controlPanel = new JPanel();
    controlPanel.add(new JLabel("矩陣大小 (n*n):"));
    sizeField = new JTextField(5);
    controlPanel.add(sizeField);
    generateButton = new JButton("生成矩陣");
    controlPanel.add(generateButton);
    add(controlPanel, BorderLayout.NORTH);

    matrixPanel = new JPanel();
    matrixPanel.setLayout(new GridLayout(0, 1));
    matrixScrollPane = new JScrollPane(matrixPanel);
    matrixScrollPane.setBorder(BorderFactory.createTitledBorder("原始矩陣"));
    add(matrixScrollPane, BorderLayout.WEST);

    sortedMatrixPanel = new JPanel();
    sortedMatrixPanel.setLayout(new GridLayout(0, 1));
    sortedMatrixScrollPane = new JScrollPane(sortedMatrixPanel);
    sortedMatrixScrollPane.setBorder(BorderFactory.createTitledBorder("排序後矩陣"));
    add(sortedMatrixScrollPane, BorderLayout.CENTER);

    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BorderLayout());

    JPanel searchPanel = new JPanel();
    searchPanel.add(new JLabel("搜尋數字:"));
    searchField = new JTextField(5);
    searchPanel.add(searchField);
    searchButton = new JButton("搜尋");
    searchPanel.add(searchButton);
    clearButton = new JButton("清除");
    searchPanel.add(clearButton);
    sequentialCheckBox = new JCheckBox("循序搜尋");
    binaryCheckBox = new JCheckBox("二元搜尋");
    hashCheckBox = new JCheckBox("雜湊搜尋");
    searchPanel.add(sequentialCheckBox);
    searchPanel.add(binaryCheckBox);
    searchPanel.add(hashCheckBox);
    bottomPanel.add(searchPanel, BorderLayout.NORTH);

    resultArea = new JTextArea(10, 50);
    resultArea.setEditable(false);
    bottomPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

    add(bottomPanel, BorderLayout.SOUTH);

    generateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        generateMatrix();
      }
    });

    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        searchMatrix();
      }
    });

    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clearAll();
      }
    });
  }

  private void generateMatrix() {
    String sizeText = sizeField.getText();
    try {
      int size = Integer.parseInt(sizeText);
      if (size <= 0) {
        JOptionPane.showMessageDialog(this, "請輸入一個正整數！", "錯誤", JOptionPane.ERROR_MESSAGE);
        return;
      }
      matrix = generateUniqueRandomMatrix(size);
      sortedMatrix = sortMatrixRows(matrix);
      uniqueNumbers = createHashSet(matrix);
      displayMatrix(matrix, matrixPanel);
      displayMatrix(sortedMatrix, sortedMatrixPanel);
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "請輸入有效的整數！", "錯誤", JOptionPane.ERROR_MESSAGE);
    }
  }

  private int[][] generateUniqueRandomMatrix(int size) {
    int[][] matrix = new int[size][size];
    Set<Integer> uniqueNumbers = new HashSet<>();
    Random random = new Random();

    for (int i = 0; i < size * size; i++) {
      int number;
      do {
        number = random.nextInt(size * size) + 1;
      } while (uniqueNumbers.contains(number));
      uniqueNumbers.add(number);
      matrix[i / size][i % size] = number;
    }
    return matrix;
  }

  private int[][] sortMatrixRows(int[][] matrix) {
    int[][] sortedMatrix = new int[matrix.length][matrix.length];
    for (int i = 0; i < matrix.length; i++) {
      sortedMatrix[i] = Arrays.copyOf(matrix[i], matrix[i].length);
      Arrays.sort(sortedMatrix[i]);
    }
    return sortedMatrix;
  }

  private Set<Integer> createHashSet(int[][] matrix) {
    Set<Integer> set = new HashSet<>();
    for (int[] row : matrix) {
      for (int value : row) {
        set.add(value);
      }
    }
    return set;
  }

  private void displayMatrix(int[][] matrix, JPanel panel) {
    panel.removeAll();
    panel.setLayout(new GridLayout(matrix.length, 1));
    for (int[] row : matrix) {
      JPanel rowPanel = new JPanel();
      rowPanel.setLayout(new GridLayout(1, row.length));
      for (int value : row) {
        rowPanel.add(new JLabel(String.valueOf(value), SwingConstants.CENTER));
      }
      panel.add(rowPanel);
    }
    panel.revalidate();
    panel.repaint();
  }

  private void searchMatrix() {
    String searchText = searchField.getText();
    try {
      int target = Integer.parseInt(searchText);
      if (sequentialCheckBox.isSelected()) {
        long startTime = System.nanoTime();
        int[] result = sequentialSearch(matrix, target);
        long endTime = System.nanoTime();
        long searchTime = endTime - startTime;
        displaySearchResult("循序搜尋", target, result, searchTime);
      }
      if (binaryCheckBox.isSelected()) {
        long startTime = System.nanoTime();
        int[] result = binarySearch(sortedMatrix, target);
        long endTime = System.nanoTime();
        long searchTime = endTime - startTime;
        displaySearchResult("二元搜尋", target, result, searchTime);
      }
      if (hashCheckBox.isSelected()) {
        long startTime = System.nanoTime();
        boolean found = uniqueNumbers.contains(target);
        long endTime = System.nanoTime();
        long searchTime = endTime - startTime;
        displaySearchResult("雜湊搜尋", target, found ? new int[]{-1, -1} : null, searchTime);
      }
    } catch (NumberFormatException ex) {
      JOptionPane.showMessageDialog(this, "請輸入有效的數字！", "錯誤", JOptionPane.ERROR_MESSAGE);
    }
  }

  private int[] sequentialSearch(int[][] matrix, int target) {
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        if (matrix[i][j] == target) {
          return new int[]{i, j};
        }
      }
    }
    return null;
  }

  private int[] binarySearch(int[][] matrix, int target) {
    for (int i = 0; i < matrix.length; i++) {
      int index = Arrays.binarySearch(matrix[i], target);
      if (index >= 0) {
        return new int[]{i, index};
      }
    }
    return null;
  }

  private void displaySearchResult(String method, int target, int[] result, long searchTime) {
    if (result != null && result[0] != -1) {
      resultArea.append(method + ": 找到數字 " + target + "，位置 (" + result[0] + ", " + result[1] + "), 搜尋耗時 " + searchTime + " 納秒\n");
    } else if (result != null) {
      resultArea.append(method + ": 找到數字 " + target + "，搜尋耗時 " + searchTime + " 納秒\n");
    } else {
      resultArea.append(method + ": 未找到數字 " + target + ", 搜尋耗時 " + searchTime + " 納秒\n");
    }
  }

  private void clearAll() {
    resultArea.setText("");
    matrixPanel.removeAll();
    sortedMatrixPanel.removeAll();
    matrixPanel.revalidate();
    sortedMatrixPanel.revalidate();
    matrixPanel.repaint();
    sortedMatrixPanel.repaint();
    matrix = null;
    sortedMatrix = null;
    uniqueNumbers = null;
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new RandomMatrixSearchGUI().setVisible(true);
      }
    });
  }
}
