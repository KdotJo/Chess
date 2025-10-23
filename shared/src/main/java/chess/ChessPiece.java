package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        PieceType pieceType = this.getPieceType();

        switch (pieceType) {
            case BISHOP -> addPieceMoves(moves, board, myPosition, bishopDirections());
            case ROOK -> addPieceMoves(moves, board, myPosition, rookDirections());
            case QUEEN -> addPieceMoves(moves, board, myPosition, queenDirections());
            case KING -> addKingMoves(moves, board, myPosition);
            case KNIGHT -> addKnightMoves(moves, board, myPosition);
            case PAWN -> addPawnMoves(moves, board, myPosition);
        }
        return moves;
    }

    private int[][] bishopDirections() {
        return new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    }

    private int[][] rookDirections() {
        return new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    }

    private int[][] queenDirections() {
        return new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    }

    private int[][] knightDirections() {
        return new int[][]{{2, 1}, {2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}};
    }

    private void addPieceMoves(List<ChessMove> moves, ChessBoard board, ChessPosition myPosition, int[][] directions) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int[] dir : directions) {
            addMoves(moves, board, myPosition, row, col, dir[0], dir[1], true);
        }
    }

    private void addKingMoves(List<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int[] dir : queenDirections()) {
            addMoves(moves, board, myPosition, row, col, dir[0], dir[1], false);
        }
    }

    private void addKnightMoves(List<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int[] dir : knightDirections()) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isValidPosition(newRow, newCol)) {
                ChessPosition nextPosition = new ChessPosition(newRow, newCol);
                if (canMove(board, nextPosition)) {
                    moves.add(new ChessMove(myPosition, nextPosition, null));
                }
            }
        }
    }

    private void addPawnMoves(List<ChessMove> moves, ChessBoard board, ChessPosition myPosition) {
        int directions = (this.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startingRow = (this.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (this.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        pawnForwardMoves(moves, board, myPosition, row, col, directions, startingRow, promotionRow);

          pawnCaptureMoves(moves, board, myPosition, row, col, directions, promotionRow);
    }

    private void pawnForwardMoves(List<ChessMove> moves, ChessBoard board, ChessPosition myPosition,
                                  int row, int col, int directions, int startingRow, int promotionRow) {
        int newRow = row + directions;
        if (!isValidPosition(newRow, col)) return;

        ChessPosition nextPosition = new ChessPosition(newRow, col);
        ChessPiece nextPiece = board.getPiece(nextPosition);

        if (nextPiece == null) {
            addPawn(moves, myPosition, nextPosition, newRow, promotionRow);

             if (row == startingRow) {
                ChessPosition doublePosition = new ChessPosition(newRow + directions, col);
                if (board.getPiece(doublePosition) == null) {
                    moves.add(new ChessMove(myPosition, doublePosition, null));
                }
            }
        }
    }

    private void pawnCaptureMoves(List<ChessMove> moves, ChessBoard board, ChessPosition myPosition,
                                  int row, int col, int directions, int promotionRow) {
        int newRow = row + directions;

        for (int dc : new int[]{1, -1}) {
            int colChange = col + dc;
            if (isValidPosition(newRow, colChange)) {
                ChessPosition nextPosition = new ChessPosition(newRow, colChange);
                ChessPiece nextPiece = board.getPiece(nextPosition);

                if (nextPiece != null && getTeamColor() != nextPiece.getTeamColor()) {
                    addPawn(moves, myPosition, nextPosition, newRow, promotionRow);
                }
            }
        }
    }

    private void addPawn(List<ChessMove> moves, ChessPosition from, ChessPosition to,
                         int newRow, int promotionRow) {
        if (newRow == promotionRow) {
            addPromotion(moves, from, to);
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }

    private void addPromotion(List<ChessMove> moves, ChessPosition from, ChessPosition to) {
        PieceType[] promotionPieces = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};
        for (PieceType piece : promotionPieces) {
            moves.add(new ChessMove(from, to, piece));
        }
    }

     private void addMoves(List<ChessMove> moves, ChessBoard board, ChessPosition myPosition,
                          int startRow, int startCol, int rowChange, int colChange, boolean isSliding) {
        int newRow = startRow + rowChange;
        int newCol = startCol + colChange;

        while (isValidPosition(newRow, newCol)) {
            ChessPosition nextPosition = new ChessPosition(newRow, newCol);
            ChessPiece nextPiece = board.getPiece(nextPosition);

            if (nextPiece == null) {
                moves.add(new ChessMove(myPosition, nextPosition, null));
            } else {
                if (this.getTeamColor() != nextPiece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, nextPosition, null));
                }
                break;
            }

            if (!isSliding) break;

            newRow += rowChange;
            newCol += colChange;
        }
    }

       private boolean isValidPosition(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private boolean canMove(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        return piece == null || getTeamColor() != piece.getTeamColor();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || getClass() != obj.getClass()) {return false;}
        ChessPiece other = (ChessPiece) obj;
        return pieceColor == other.pieceColor && type == other.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
