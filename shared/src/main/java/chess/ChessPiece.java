package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            int[][] directions = {
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}
            };
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            for (int [] dir : directions ) {
                int row_change = dir[0];
                int col_change = dir[1];
                int new_row = row + row_change;
                int new_col = col + col_change;
                while ((new_row <= 8 && new_row >= 1) && (new_col <= 8  && new_col >= 1)) {
                    ChessPosition nextPosition = new ChessPosition(new_row, new_col);
                    ChessPiece nextPiece = board.getPiece(nextPosition);
                    if (nextPiece == null) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                    else if (this.getTeamColor() == nextPiece.getTeamColor()) {break;}
                    else {
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                        break;
                    }
                    new_row += row_change;
                    new_col += col_change;
                }
            }
        }
        if (piece.getPieceType() == PieceType.ROOK) {
            int [][] directions = {
                    {1, 0},
                    {0, 1},
                    {-1, 0},
                    {0, -1}
            };
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            for (int [] dir : directions ) {
                int row_change = dir[0];
                int col_change = dir[1];
                int new_row = row + row_change;
                int new_col = col + col_change;
                while ((new_row <= 8 && new_row >= 1) && (new_col <= 8  && new_col >= 1)) {
                    ChessPosition nextPosition = new ChessPosition(new_row, new_col);
                    ChessPiece nextPiece = board.getPiece(nextPosition);
                    if (nextPiece == null) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                    else if (this.getTeamColor() == nextPiece.getTeamColor()) {break;}
                    else {
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                        break;
                    }
                    new_row += row_change;
                    new_col += col_change;
                }
            }
        }
        if (piece.getPieceType() == PieceType.QUEEN) {
            int [][] directions = {
                    {1, 0},
                    {0, 1},
                    {-1, 0},
                    {0, -1},
                    {1, 1},
                    {1, -1},
                    {-1, 1},
                    {-1, -1}
            };
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            for (int [] dir : directions ) {
                int row_change = dir[0];
                int col_change = dir[1];
                int new_row = row + row_change;
                int new_col = col + col_change;
                while ((new_row <= 8 && new_row >= 1) && (new_col <= 8  && new_col >= 1)) {
                    ChessPosition nextPosition = new ChessPosition(new_row, new_col);
                    ChessPiece nextPiece = board.getPiece(nextPosition);
                    if (nextPiece == null) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                    else if (this.getTeamColor() == nextPiece.getTeamColor()) {break;}
                    else {
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                        break;
                    }
                    new_row += row_change;
                    new_col += col_change;
                }
            }
        }
        if (piece.getPieceType() == PieceType.KING) {
            int [][] directions = {
                    {1, 0},
                    {0, 1},
                    {-1, 0},
                    {0, -1},
                    {1, 1},
                    {1, -1},
                    {-1, 1},
                    {-1, -1}
            };
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            for (int [] dir : directions ) {
                int row_change = dir[0];
                int col_change = dir[1];
                int new_row = row + row_change;
                int new_col = col + col_change;
                if ((new_row <= 8 && new_row >= 1) && (new_col <= 8  && new_col >= 1)) {
                    ChessPosition nextPosition = new ChessPosition(new_row, new_col);
                    ChessPiece nextPiece = board.getPiece(nextPosition);
                    if (nextPiece == null || getTeamColor() != nextPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                    }
                }
            }
        }
        if (piece.getPieceType() == PieceType.KNIGHT) {
            int [][] directions = {
                    {2, 1},
                    {2, -1},
                    {1, 2},
                    {1, -2},
                    {-1, 2},
                    {-1, -2},
                    {-2, 1},
                    {-2, -1},

            };
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            for (int [] dir : directions ) {
                int row_change = dir[0];
                int col_change = dir[1];
                int new_row = row + row_change;
                int new_col = col + col_change;
                if ((new_row <= 8 && new_row >= 1) && (new_col <= 8  && new_col >= 1)) {
                    ChessPosition nextPosition = new ChessPosition(new_row, new_col);
                    ChessPiece nextPiece = board.getPiece(nextPosition);
                    if (nextPiece == null || getTeamColor() != nextPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                    }
                }
            }
        }
        if (piece.getPieceType() == PieceType.PAWN) {
            int directions = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
            int starting_row = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
            int promotion_row = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            int new_row = row + directions;
            int new_col = col + directions;
            ChessPosition nextPosition = new ChessPosition(new_row, new_col);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            if ((new_row <= 8 && new_row >= 1) && (new_col <= 8  && new_col >= 1)) {
                if (new_row == promotion_row) {
                    moves.add(new ChessMove(myPosition, nextPosition, PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, nextPosition, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, nextPosition, PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, nextPosition, PieceType.BISHOP));
                }
                else {moves.add(new ChessMove(myPosition, nextPosition, null));}
            }
        }
            return moves;
    }
}
