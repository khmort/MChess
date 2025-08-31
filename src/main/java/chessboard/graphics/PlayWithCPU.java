package chessboard.graphics;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import chessboard.Move;
import chessboard.SimpleChessBoard;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Rook;

public class PlayWithCPU {
    public static void main(String[] args) throws NumberFormatException, ClassNotFoundException, IOException {
		
		Pawn.init();
		Bishop.init();
		Rook.init();
		Knight.init();
		King.init();
		
		JFrame frame = new JFrame();
		JPanel chessPanel = new JPanel(new BorderLayout());
		
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton cpu = new JButton("CPU is white!");
		buttonsPanel.add(cpu);
		
		chessPanel.add(buttonsPanel, "South");
		
		SimpleChessBoard chessboard = new SimpleChessBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		ChessBoardRenderer gchessboard = new ChessBoardRenderer(chessboard);
		
		chessPanel.add(gchessboard, "Center");
		
		// initial listeners
		cpu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Move predefMove = new Move('P', Move.NULL, Move.NULL, 52, 36,
					false, true, false, false, 0b1111);
				chessboard.doMove(predefMove);
				gchessboard.repaint();
			}
		});
		
		frame.getContentPane().add(chessPanel);
		frame.setSize(650, 700);
		frame.setDefaultCloseOperation(3);
		frame.setVisible(true);
	}
}
