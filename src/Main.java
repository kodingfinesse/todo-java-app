import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

public class Main {
    private static DefaultListModel<String> listModel = new DefaultListModel<>();
    private static JList<String> taskList;

    public static void main(String[] args) {
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("To-Do List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        // Initialize task list with custom rendering
        taskList = new JList<>(listModel);
        taskList.setCellRenderer(new TaskCellRenderer());
        taskList.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(taskList);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));

        // Input field for adding tasks
        JTextField taskField = new JTextField();
        taskField.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(taskField, BorderLayout.CENTER);

        // Button to add tasks
        JButton addButton = new JButton("Add Task");
        addButton.setBackground(new Color(51, 153, 255));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(addButton, BorderLayout.EAST);

        // Button to remove tasks
        JButton removeButton = new JButton("Remove Task");
        removeButton.setBackground(new Color(255, 77, 77));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(removeButton, BorderLayout.WEST);

        frame.add(panel, BorderLayout.SOUTH);

        // Action listener to add a task
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String task = taskField.getText();
                if (!task.isEmpty()) {
                    listModel.addElement(task);
                    taskField.setText("");
                    saveTasks(); // Save task list after adding
                }
            }
        });

        // Action listener to remove a task
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = taskList.getSelectedIndex();
                if (index != -1) {
                    listModel.remove(index);
                    saveTasks(); // Save task list after removal
                }
            }
        });

        // Enable task editing on double-click
        taskList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = taskList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String currentTask = listModel.getElementAt(index);
                        String newTask = JOptionPane.showInputDialog(frame, "Edit Task:", currentTask);
                        if (newTask != null && !newTask.trim().isEmpty()) {
                            listModel.set(index, newTask);
                            saveTasks(); // Save task list after editing
                        }
                    }
                }
            }
        });

        frame.setVisible(true);

        // Load tasks from file on startup
        loadTasks();
    }

    // Custom renderer to style completed tasks differently
    static class TaskCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value.toString().startsWith("[Completed] ")) {
                c.setForeground(Color.GRAY);
                c.setFont(new Font("Arial", Font.ITALIC, 16));
            } else {
                c.setForeground(Color.BLACK);
                c.setFont(new Font("Arial", Font.PLAIN, 16));
            }
            if (isSelected) {
                c.setBackground(new Color(204, 229, 255));
            }
            return c;
        }
    }

    // Save tasks to a file
    private static void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.txt"))) {
            for (int i = 0; i < listModel.size(); i++) {
                writer.write(listModel.getElementAt(i));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load tasks from a file
    private static void loadTasks() {
        try (Scanner scanner = new Scanner(new File("tasks.txt"))) {
            while (scanner.hasNextLine()) {
                listModel.addElement(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            // File not found, nothing to load
        }
    }
}
