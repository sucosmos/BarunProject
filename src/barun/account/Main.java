package barun.account;

import javax.swing.SwingUtilities;

/**
 * 프로그램 진입점(Entry Point) 클래스입니다.
 * 
 * @author 한준희 (224512)
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        // Swing GUI는 Event Dispatch Thread에서 실행 (Swing 강의 내용 반영)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AccountManager manager = new AccountManager();
                BarunGUI gui = new BarunGUI(manager);
                gui.setLocationRelativeTo(null); // 화면 중앙에 띄우기
                gui.setVisible(true);
            }
        });
    }
}
