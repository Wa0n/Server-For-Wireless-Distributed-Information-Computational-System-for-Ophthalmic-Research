import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Vladimir Pantasenko
 */
public class Server implements Runnable {
    private Socket connection;
    private ServerSocket server;
    private int port = 8080;
    private DataOutputStream output;
    private DataInputStream inputFileName;
    private DataInputStream inputPatientName;
    private Desktop desktop;
    private volatile boolean running;
    private ServerFrame serverFrame = Main.getServerFrame();
    private boolean openPhoto = true;

    @Override
    public void run() {
        while (true) {
            while (running) {
                try {
                    server = new ServerSocket(port);
                    serverFrame.setStatusText("server is running");
                    while (running) {
                        connection = server.accept();
                        try {
                            output = new DataOutputStream(connection.getOutputStream());
                            inputFileName = new DataInputStream(connection.getInputStream());
                            inputPatientName = new DataInputStream(connection.getInputStream());
                            receiveFileFromClient();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            connection.close();
                            inputFileName.close();
                            inputPatientName.close();
                            output.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startServer() {
        this.running = true;
    }

    public void stopServer() {
        this.running = false;
        try {
            if (connection != null) {
                connection.close();
            }
            if (server != null) {
                server.close();
                serverFrame.setStatusText("server is stopped");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageFromServer(String message) {
        if (connection != null) {
            try {
                output.writeUTF(message);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveFileFromClient() {
        try {
            String fileName = inputFileName.readUTF();

            serverFrame.setStatusText("receiving a file");
            serverFrame.setStatusText("file name is : " + fileName);

            String patientName = inputPatientName.readUTF();
            if (patientName == null) {
                patientName = "Unnamed";
            }
            serverFrame.setStatusText("the patient's name is : " + patientName);

            DataInputStream input = new DataInputStream(connection.getInputStream());
            serverFrame.setStatusText("downloading a file...");

            File path = new File("." + File.separator + "Received Photos", patientName);
            if (!path.exists()) {
                if (!path.mkdirs()) {
                    System.out.println("Cannot create directory.");
                }
            }
            File file = new File(path.getPath() + File.separator + fileName);
            FileOutputStream output = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int count = 0;
            serverFrame.setProgressBarDwnld(true);
            long startDwnld = System.currentTimeMillis();
            while (true) {
                count = input.read(b);
                if (count > 0) {
                    output.write(b, 0, count);
                }
                if (count == -1) {
                    long stopDwnld = System.currentTimeMillis();
                    serverFrame.setStatusText("file is downloaded and saved in " + (stopDwnld - startDwnld) + " ms");
                    break;
                }
            }
            serverFrame.setProgressBarDwnld(false);
            input.close();
            output.close();
            if (openPhoto) {
                openFileInDesktop(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openPhotoAfterSaving(boolean op) {
        this.openPhoto = op;
    }

    public void openFileInDesktop(File file) {
        try {
            Runtime.getRuntime().exec("cmd /c start .\\Retina_1_3.exe \"" + file.getPath() + "\"");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
