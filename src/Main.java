import javax.swing.*;

/**
 * @author Vladimir Pantasenko
 */
public class Main {
    private static ServerFrame serverFrame;

    public static void main(String[] args) {
        LookAndFeel();
        serverFrame = new ServerFrame();
        serverFrame.setFrame();
    }

    public static ServerFrame getServerFrame() {
        return serverFrame;
    }

    private static void LookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
}
