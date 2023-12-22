// 2023
// TetrisApp.java
// Vlad Deordiy
// Special thanks to zetcode

package tetris;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class TetrisApp extends JFrame{
    public TetrisApp(){
        initUI();
    }

    private void initUI(){
        setSize(Commons.columns * Commons.squareSize, Commons.rows * Commons.squareSize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        add(new Board());
        pack();
    }

    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run(){
                TetrisApp app = new TetrisApp();
                app.setVisible(true);
            }
        });
    }
}
