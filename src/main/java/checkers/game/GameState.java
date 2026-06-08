package checkers.game;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Board board;
    private PieceColor currentPlayer;
    private int whitePieces;
    private int blackPieces;
    private GameStatus status;

    public enum GameStatus {
        PLAYING,
        WHITE_WINS,
        BLACK_WINS,
        DRAW
    }

    public GameState() {
        board = new Board();
        currentPlayer = PieceColor.BLACK;
        whitePieces = 12;
        blackPieces = 12;
        status = GameStatus.PLAYING;
    }

    public Board getBoard() {
        return board;
    }

    public PieceColor getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PieceColor player) {
        currentPlayer = player;
    }

    public GameStatus getStatus() {
        return status;
    }

    public boolean makeMove(Move move) {
        Piece piece = board.getPiece(move.getFromRow(), move.getFromCol());
        if (piece == null || piece.getColor() != currentPlayer) {
            return false;
        }

        board.setPiece(move.getToRow(), move.getToCol(), piece);
        board.removePiece(move.getFromRow(), move.getFromCol());

        // Проверяем превращение в дамку
        if ((piece.getColor() == PieceColor.WHITE && move.getToRow() == 0) ||
            (piece.getColor() == PieceColor.BLACK && move.getToRow() == Board.getSize() - 1)) {
            piece.makeKing();
        }

        // Обработка взятия
        if (move.isCapture()) {
            int[] captured = move.getCapturedPiece();
            board.removePiece(captured[0], captured[1]);
            if (piece.getColor() == PieceColor.WHITE) {
                blackPieces--;
            } else {
                whitePieces--;
            }
        }

        // Переход хода
        currentPlayer = currentPlayer == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;

        // Проверяем условия победы
        updateGameStatus();

        return true;
    }

    private void updateGameStatus() {
        if (blackPieces == 0) {
            status = GameStatus.WHITE_WINS;
        } else if (whitePieces == 0) {
            status = GameStatus.BLACK_WINS;
        } else if (!hasValidMoves(currentPlayer)) {
            status = currentPlayer == PieceColor.WHITE ? 
                GameStatus.BLACK_WINS : GameStatus.WHITE_WINS;
        }
    }

    public boolean hasValidMoves(PieceColor player) {
        for (int row = 0; row < Board.getSize(); row++) {
            for (int col = 0; col < Board.getSize(); col++) {
                if (!board.getValidMoves(row, col, player).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getWhitePieces() { return whitePieces; }
    public int getBlackPieces() { return blackPieces; }
}
