import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class PetAdoptionCenter extends JFrame {
    private static final String PETS_FILE = "pets.txt";
    private static final String ADOPTERS_FILE = "adopters.txt";
    private static final String ADOPTIONS_FILE = "adoptions.txt";

    private JLabel promoImageLabel;
    private JButton uploadImageBtn;

    public PetAdoptionCenter() {
        setTitle("Pet Adoption Center");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(255, 239, 213));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 204, 153));
        headerPanel.setPreferredSize(new Dimension(1100, 160));

        ImageIcon pawIcon = new ImageIcon("cat_paw.png");
        Image pawImg = pawIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        pawIcon = new ImageIcon(pawImg);
        JLabel title = new JLabel(" Pet Adoption Center", pawIcon, JLabel.LEFT);
        title.setFont(new Font("Georgia", Font.BOLD, 52));
        title.setForeground(new Color(90, 40, 0));
        title.setBorder(BorderFactory.createEmptyBorder(25, 25, 10, 10));
        headerPanel.add(title, BorderLayout.WEST);

        JPanel promoPanel = new JPanel(new BorderLayout());
        promoPanel.setOpaque(false);

        promoImageLabel = new JLabel("Insert Image", SwingConstants.CENTER);
        promoImageLabel.setPreferredSize(new Dimension(450, 130));
        promoImageLabel.setBorder(BorderFactory.createDashedBorder(Color.GRAY, 2, 5));
        promoImageLabel.setOpaque(true);
        promoImageLabel.setBackground(Color.WHITE);
        promoImageLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        promoImageLabel.setForeground(Color.GRAY);

        uploadImageBtn = new JButton("📸");
        uploadImageBtn.setMargin(new Insets(1, 5, 1, 5));
        uploadImageBtn.setFont(new Font("Arial", Font.BOLD, 14));
        uploadImageBtn.setFocusable(false);
        uploadImageBtn.setToolTipText("Upload Image");
        uploadImageBtn.addActionListener(e -> uploadPromoImage());

        JPanel imageTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        imageTop.setOpaque(false);
        imageTop.add(uploadImageBtn);

        promoPanel.add(imageTop, BorderLayout.NORTH);
        promoPanel.add(promoImageLabel, BorderLayout.CENTER);
        headerPanel.add(promoPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        loadSavedPromoImage(); // 🆕 Load saved image if available

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        centerPanel.setBackground(new Color(255, 239, 213));

        JPanel petPanel = createOptionPanel("🐱 Pet Options",
                new String[]{"🔍 Search Pet by ID", "➕ Add New Pet", "📋 View All Pets"},
                new ActionListener[]{
                        e -> searchById(PETS_FILE, "Pet ID"),
                        e -> addPet(),
                        e -> showDataListWithDelete(PETS_FILE, "All Pets", new String[]{"Pet ID", "Name", "Type", "Breed", "Age", "Gender"})
                });

        JPanel adoptionPanel = createOptionPanel("🏠 Adoption Options",
                new String[]{"🔍 Search Adopter by ID", "📝 Register Adopter", "🤝 Adopt a Pet", "📖 View Adoption Records"},
                new ActionListener[]{
                        e -> searchById(ADOPTERS_FILE, "Adopter ID"),
                        e -> registerAdopter(),
                        e -> adoptPet(),
                        e -> showDataListWithDelete(ADOPTIONS_FILE, "Adoption Records", new String[]{"Pet ID", "Adopter ID", "Date"})
                });

        centerPanel.add(petPanel);
        centerPanel.add(adoptionPanel);

        add(centerPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel("Developed by Asif Sarwar and Farhan Istiak Siam", SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.ITALIC, 14));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(footer, BorderLayout.PAGE_END);
    }

    private JPanel createOptionPanel(String title, String[] options, ActionListener[] actions) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 255, 240));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 204, 153), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 26));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setForeground(new Color(139, 69, 19));
        panel.add(label);
        panel.add(Box.createVerticalStrut(15));

        for (int i = 0; i < options.length; i++) {
            JButton btn = new JButton(options[i]);
            btn.setFont(new Font("SansSerif", Font.BOLD, 18));
            btn.setMaximumSize(new Dimension(300, 45));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setBackground(new Color(255, 204, 153));
            btn.setForeground(Color.DARK_GRAY);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(210, 105, 30), 1),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(actions[i]);
            panel.add(btn);
            panel.add(Box.createVerticalStrut(12));
        }

        return panel;
    }

    private void uploadPromoImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(450, 130, Image.SCALE_SMOOTH);
            promoImageLabel.setText("");
            promoImageLabel.setIcon(new ImageIcon(scaled));

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("promoImage.ser"))) {
                out.writeObject(selectedFile.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadSavedPromoImage() {
        File file = new File("promoImage.ser");
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                String path = (String) in.readObject();
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                    Image scaled = icon.getImage().getScaledInstance(450, 130, Image.SCALE_SMOOTH);
                    promoImageLabel.setText("");
                    promoImageLabel.setIcon(new ImageIcon(scaled));
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void addPet() {
        JTextField[] fields = new JTextField[6];
        Object[] inputs = {
                "Pet ID:", fields[0] = new JTextField(),
                "Name:", fields[1] = new JTextField(),
                "Type (Dog/Cat):", fields[2] = new JTextField(),
                "Breed:", fields[3] = new JTextField(),
                "Age:", fields[4] = new JTextField(),
                "Gender:", fields[5] = new JTextField()
        };

        int option = JOptionPane.showConfirmDialog(this, inputs, "Add Pet", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            for (JTextField f : fields) {
                if (f.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill out all fields.");
                    return;
                }
            }

            try (PrintWriter out = new PrintWriter(new FileWriter(PETS_FILE, true))) {
                String[] values = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    values[i] = fields[i].getText();
                }
                out.println(String.join(",", values));
                JOptionPane.showMessageDialog(this, "Pet added successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerAdopter() {
        JTextField[] fields = new JTextField[4];
        Object[] inputs = {
                "Adopter ID:", fields[0] = new JTextField(),
                "Name:", fields[1] = new JTextField(),
                "Contact:", fields[2] = new JTextField(),
                "Address:", fields[3] = new JTextField()
        };

        int option = JOptionPane.showConfirmDialog(this, inputs, "Register Adopter", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            for (JTextField f : fields) {
                if (f.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill out all fields.");
                    return;
                }
            }

            try (PrintWriter out = new PrintWriter(new FileWriter(ADOPTERS_FILE, true))) {
                String[] values = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    values[i] = fields[i].getText();
                }
                out.println(String.join(",", values));
                JOptionPane.showMessageDialog(this, "Adopter registered!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void adoptPet() {
        JTextField[] fields = new JTextField[3];
        Object[] inputs = {
                "Pet ID:", fields[0] = new JTextField(),
                "Adopter ID:", fields[1] = new JTextField(),
                "Date (DD-MM-YYYY):", fields[2] = new JTextField()
        };

        int option = JOptionPane.showConfirmDialog(this, inputs, "Adopt Pet", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            for (JTextField f : fields) {
                if (f.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill out all fields.");
                    return;
                }
            }

            try (PrintWriter out = new PrintWriter(new FileWriter(ADOPTIONS_FILE, true))) {
                String[] values = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    values[i] = fields[i].getText();
                }
                out.println(String.join(",", values));
                JOptionPane.showMessageDialog(this, "Pet adopted!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDataListWithDelete(String file, String title, String[] headers) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        StringBuilder builder = new StringBuilder();

        if (file.equals(ADOPTIONS_FILE)) {
            builder.append("Pet ID | Adopter ID | Date | Adopter Name | Contact | Address\n");
            builder.append("-".repeat(100)).append("\n");

            Map<String, String[]> adopterMap = new HashMap<>();
            try (BufferedReader br = new BufferedReader(new FileReader(ADOPTERS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 4) {
                        adopterMap.put(parts[0], new String[]{parts[1], parts[2], parts[3]});
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 3) {
                        String petId = parts[0];
                        String adopterId = parts[1];
                        String date = parts[2];
                        String[] adopterInfo = adopterMap.getOrDefault(adopterId, new String[]{"N/A", "N/A", "N/A"});
                        builder.append(String.format("%s | %s | %s | %s | %s | %s\n",
                                petId, adopterId, date, adopterInfo[0], adopterInfo[1], adopterInfo[2]));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            builder.append(String.join(" | ", headers)).append("\n");
            builder.append("-".repeat(80)).append("\n");

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line.replace(",", " | ")).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        area.setText(builder.toString());
        area.setEditable(false);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);

        JTextField deleteField = new JTextField();
        JButton deleteBtn = new JButton("Delete");

        deleteBtn.addActionListener(e -> {
            String id = deleteField.getText();
            if (!id.isEmpty()) {
                deleteById(file, id);
                JOptionPane.showMessageDialog(this, "Deleted.");
            }
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(new JLabel("Enter ID to delete:"), BorderLayout.WEST);
        bottom.add(deleteField, BorderLayout.CENTER);
        bottom.add(deleteBtn, BorderLayout.EAST);
        panel.add(bottom, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel, title, JOptionPane.PLAIN_MESSAGE);
    }

    private void deleteById(String filePath, String idToDelete) {
        File inputFile = new File(filePath);
        File tempFile = new File("temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(idToDelete + ",")) {
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    private void searchById(String filePath, String label) {
        String id = JOptionPane.showInputDialog(this, "Enter " + label + ":");
        if (id == null || id.isEmpty()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(id + ",")) {
                    JOptionPane.showMessageDialog(this, "Found: " + line.replace(",", " | "));
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Not found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PetAdoptionCenter().setVisible(true));
    }
}