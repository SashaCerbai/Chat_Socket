package chatsocket;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;

public class ChatServer extends JFrame {
    public ChatServer(){
    super("Chat Server");
    this.setSize(new Dimension(600,400));
    this.setLocationRelativeTo(null);
    this.setBackground(Color.yellow);
    
    }
}
