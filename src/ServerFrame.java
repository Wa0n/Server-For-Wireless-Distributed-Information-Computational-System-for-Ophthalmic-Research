import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Vladimir Pantasenko
 */
public class ServerFrame {
    private JToggleButton STARTButton;
    private JButton TAKEPHOTOButton;
    private JLabel hostLabel;
    private JPanel root;
    private JButton INFOButton;
    private JLabel statusLabel;
    private JCheckBox cbOpenFile;
    private JProgressBar progressBarDwnld;
    private JFrame frame;

    public void setFrame() {
        frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        hostLabel.setText("Current IP : " + getCurrentIP());
        frame.add(root);
        statusLabel.setText(null);
        frame.pack();
        frame.setResizable(false);
        centerWindow(frame);
        frame.setVisible(true);

        final Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();

        STARTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (STARTButton.getText().equals("START")) {
                    STARTButton.setText("STOP");
                    STARTButton.isSelected();
                    server.startServer();
                } else {
                    STARTButton.setText("START");
                    server.stopServer();
                }
            }
        });

        TAKEPHOTOButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.sendMessageFromServer("Take photo please");
            }
        });

        INFOButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame,
                        "1. Запустите сервер нажав кнопку 'Start server'\n" +
                                "2. Запустите клиент на смартфоне.\n" +
                                "3. Откройте настройки\n" +
                                "4. Введите имя пациента.\n" +
                                "5. Введите отображаемый сервером IP-адресс\n" +
                                "6. Все сделанные фотографии будут сохранены в папку 'Received Photos'"
                );
            }
        });

        cbOpenFile.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (cbOpenFile.isSelected()) {
                    server.openPhotoAfterSaving(true);
                } else {
                    server.openPhotoAfterSaving(false);
                }
            }
        });

    }

    public void setProgressBarDwnld(boolean state) {
        progressBarDwnld.setIndeterminate(state);
    }

    public void setStatusText(String status) {
        statusLabel.setText(status);
    }

    private void centerWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 3);
        frame.setLocation(x, y);
    }

    private static String getCurrentIP() {
        try {
            InetAddress local = InetAddress.getLocalHost();
            return local.getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
