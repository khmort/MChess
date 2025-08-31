package chessboard.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import chessboard.ChessBoard;
import chessboard.Move;
import chessboard.SimpleChessBoard;
import chessboard.Square;
import engine.ChessEngine;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Queen;
import pieces.Rook;

public class ChessBoardRenderer extends JPanel {

	private static Image WK;
	private static Image WQ;
	private static Image WR;
	private static Image WB;
	private static Image WN;
	private static Image WP;
	private static Image BK;
	private static Image BQ;
	private static Image BR;
	private static Image BB;
	private static Image BN;
	private static Image BP;
	static {
		try {
			WK = loadImage("white-king");
			WQ = loadImage("white-queen");
			WR = loadImage("white-rook");
			WB = loadImage("white-bishop");
			WN = loadImage("white-knight");
			WP = loadImage("white-pawn");
			BK = loadImage("black-king");
			BQ = loadImage("black-queen");
			BR = loadImage("black-rook");
			BB = loadImage("black-bishop");
			BN = loadImage("black-knight");
			BP = loadImage("black-pawn");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final Image NAME_TO_IMAGE[] = new Image[] { null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, WB, null, null, null, null, null, null, null, null, WK, null, null, WN, null, WP, WQ, WR,
			null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, BB, null, null,
			null, null, null, null, null, null, BK, null, null, BN, null, BP, BQ, BR };
	
	static int moveCounter = 0;
	static Random rand = new Random();
		
	private SimpleChessBoard board;
	private char selectedPiece = '-';
	private int selectedSquare = -1;
	private List<Move> legalMoves = new ArrayList<>();
	private Move cpuMove = null;
	private ChessEngine engine;

	private static Image loadImage(String pieceName) throws IOException {
		InputStream stream = ChessBoardRenderer.class.getResourceAsStream("/images/" + pieceName + ".png");
		Image img = ImageIO.read(stream);
		stream.close();
		return img;
	}

	public ChessBoardRenderer(SimpleChessBoard cb) {
		board = cb;
		engine = new ChessEngine();
		initListeners();
	}

	private void initListeners() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);

				int x = e.getX() / cellWidth(), y = e.getY() / cellHeight();
				int clickedSquare = y * 8 + x;

				if (selectedPiece != '-') {

					Move m = legalMoves.stream()
								.filter(move -> move.targetSquare == clickedSquare).findFirst()
								.orElse(null);

					if (m != null) {

						board.doMove(m);

						Thread engineThread = new Thread(new Runnable() {

							@Override
							public void run() {

								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								/*--------------------------------------*
								 | محسابه رندوم depth موتور             |
								 | شانس بیشتری برای depth < 9 وجود دارد |
								 *--------------------------------------*/
								int depth;
								if (rand.nextInt(0, 10) < 8) {
									depth = rand.nextInt(6, 9);
								} else {
									depth = 9;
								}
								System.out.println("Move Number ." + moveCounter + " - Depth: " + depth);
								System.out.println("Engine is is searching ...");
								cpuMove = engine.calculateBestMove(board, depth);
								System.out.println("Engine move: " + cpuMove);
								engine.printSearchResult();
								System.out.println();

								board.doMove(cpuMove);
								repaint();
							}
						});

						engineThread.start();
						moveCounter++;

					}

					selectedPiece = '-';
					selectedSquare = -1;
					legalMoves.clear();

				} else {

					char clickedPiece = '-';
					long pos = 1l << clickedSquare;
					if ((pos & board.getOccupancies(0)) != 0) {
						for (char wp : ChessBoard.COLOR_TO_NAMES[0]) {
							if ((pos & board.getBitboard(wp)) != 0) {
								clickedPiece = wp;
								break;
							}
						}
					} else if ((pos & board.getOccupancies(1)) != 0) {
						for (char bp : ChessBoard.COLOR_TO_NAMES[1]) {
							if ((pos & board.getBitboard(bp)) != 0) {
								clickedPiece = bp;
								break;
							}
						}
					}

					if (clickedPiece != '-') {

						selectedPiece = clickedPiece;
						selectedSquare = clickedSquare;

						/*-------------------*
						 | ساخت کپی از  بورد |
						 *-------------------*/
						ChessBoard copy = new ChessBoard();
						for (char pieceName : ChessBoard.NAMES) {
							copy.setBitboard(pieceName, board.getBitboard(pieceName));
						}
						copy.setBitboard(selectedPiece, 1l << selectedSquare);
						copy.castleRight = board.castleRight;
						copy.setSide(board.getSide());
						copy.squareToName = board.squareToName;
						copy.updateOccupancies();

						legalMoves.clear();
						if (selectedPiece == 'P') {
							Pawn.generateMoves(legalMoves, copy, 'P', 0, 7, 2, -8, 'Q', 'N');
						} else if (selectedPiece == 'p') {
							Pawn.generateMoves(legalMoves, copy, 'p', 1, 2, 7, 8, 'q', 'n');
						} else if (selectedPiece == 'K') {
							King.generateMoves(legalMoves, copy, 'K', 0, 58, 62,
								King.WHITE_QUEEN_SIDE_OCC, King.WHITE_KING_SIDE_OCC);
						} else if (selectedPiece == 'k') {
							King.generateMoves(legalMoves, copy, 'k', 1, 2, 6,
								King.BLACK_QUEEN_SIDE_OCC, King.BLACK_KING_SIDE_OCC);
						} else if (selectedPiece == 'Q' || selectedPiece == 'q') {
							Queen.generateMoves(legalMoves, copy, selectedPiece, copy.getSide());
						} else if (selectedPiece == 'B' || selectedPiece == 'b') {
							Bishop.generateMoves(legalMoves, copy, selectedPiece, copy.getSide());
						} else if (selectedPiece == 'R' || selectedPiece == 'r') {
							Rook.generateMoves(legalMoves, copy, selectedPiece, copy.getSide());
						} else if (selectedPiece == 'N' || selectedPiece == 'n') {
							Knight.generateMoves(legalMoves, copy, selectedPiece, copy.getSide());
						}
					}

				}

				repaint();

			}
		});
	}

	@Override
	public void paint(Graphics arg0) {
		super.paint(arg0);
		Graphics2D g2 = (Graphics2D) arg0;
		drawChessBoard(g2);
		drawLegalChoises(g2);
		drawCpuMove(g2);
		drawSelectedSquare(g2);
		drawChessPieces(g2);
	}

	int cellWidth() {
		return getWidth() / 8;
	}

	int cellHeight() {
		return getHeight() / 8;
	}

	void drawChessBoard(Graphics2D g2) {

		int cellWidth = cellWidth(), cellHeight = cellHeight();

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (i % 2 == j % 2) {
					g2.setColor(Color.LIGHT_GRAY);
				} else {
					g2.setColor(Color.gray);
				}
				g2.fillRect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
				g2.setColor(Color.white);
				g2.drawString(Square.squareToName(8 * i + j), j * cellWidth + 2, i * cellHeight + 12);
			}
		}

	}

	void drawChessPieces(Graphics2D g2) {
		int square;
		long bitboard;
		int x, y;
		int cellWidth = cellWidth(), cellHeight = cellHeight();

		for (char piece : ChessBoard.NAMES) {
			bitboard = board.getBitboard(piece);
			while (bitboard != 0) {
				square = Long.numberOfTrailingZeros(bitboard);
				bitboard &= ~(1l << square);
				x = square % 8;
				y = square / 8;
				g2.drawImage(NAME_TO_IMAGE[piece], x * cellWidth, y * cellHeight, cellWidth, cellHeight,
						getFocusCycleRootAncestor());
			}
		}
	}

	void drawLegalChoises(Graphics2D g2) {
		int x, y;
		for (Move m : legalMoves) {
			x = m.targetSquare % 8;
			y = m.targetSquare / 8;
			g2.setColor(new Color(0, 255, 0, 100));
			g2.fillRect(x * cellWidth(), y * cellHeight(), cellWidth(), cellHeight());
		}
	}

	void drawSelectedSquare(Graphics2D g2) {
		int x, y;
		if (selectedSquare != -1) {
			x = selectedSquare % 8;
			y = selectedSquare / 8;
			g2.setColor(Color.yellow);
			g2.drawRect(x * cellWidth() + 5, y * cellHeight() + 5, cellWidth() - 10, cellHeight() - 10);
		}
	}

	void drawCpuMove(Graphics2D g2) {
		if (cpuMove != null) {
			int x, y;
			x = cpuMove.sourceSquare % 8;
			y = cpuMove.sourceSquare / 8;

			g2.setColor(Color.red);
			g2.drawRect(x * cellWidth() + 5, y * cellHeight() + 5, cellWidth() - 10, cellHeight() - 10);

			x = cpuMove.targetSquare % 8;
			y = cpuMove.targetSquare / 8;

			g2.setColor(new Color(0, 0, 255, 100));
			g2.fillRect(x * cellWidth(), y * cellHeight(), cellWidth(), cellHeight());
		}
	}
}
