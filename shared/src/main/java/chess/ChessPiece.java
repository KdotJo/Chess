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
                int rowChange = dir[0];
                int colChange = dir[1];
                int newRow = row + rowChange;
                int newCol = col + colChange;
                while ((newRow <= 8 && newRow >= 1) && (newCol <= 8  && newCol >= 1)) {
                    ChessPosition nextPosition = new ChessPosition(newRow, newCol);
                    ChessPiece nextPiece = board.getPiece(nextPosition);
                    if (nextPiece == null) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                    else if (this.getTeamColor() == nextPiece.getTeamColor()) {break;}
                    else {
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                        break;
                    }
                    newRow += rowChange;
                    newCol += colChange;
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
                int rowChange = dir[0];
                int colChange = dir[1];
                int newRow = row + rowChange;
                int newCol = col + colChange;
                while ((newRow <= 8 && newRow >= 1) && (newCol <= 8  && newCol >= 1)) {
                    ChessPosition nextPosition = new ChessPosition(newRow, newCol);
                    ChessPiece nextPiece = board.getPiece(nextPosition);
                    if (nextPiece == null) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                    else if (this.getTeamColor() == nextPiece.getTeamColor()) {break;}
                    else {
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                        break;
                    }
                    newRow += rowChange;
                    newCol += colChange;
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
                int rowChange = dir[0];
                int colChange = dir[1];
                int newRow = row + rowChange;
                int newCol = col + colChange;
                while ((newRow <= 8 && newRow >= 1) && (newCol <= 8  && newCol >= 1)) {
                    ChessPosition nextPosition = new ChessPosition(newRow, newCol);
                    ChessPiece nextPiece = board.getPiece(nextPosition);
                    if (nextPiece == null) {moves.add(new ChessMove(myPosition, nextPosition, null));}
                    else if (this.getTeamColor() == nextPiece.getTeamColor()) {break;}
                    else {
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                        break;
                    }
                    newRow += rowChange;
                    newCol += colChange;
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
                int rowChange = dir[0];
                int colChange = dir[1];
                int newRow = row + rowChange;
                int newCol = col + colChange;
                if ((newRow <= 8 && newRow >= 1) && (newCol <= 8  && newCol >= 1)) {
                    ChessPosition nextPosition = new ChessPosition(newRow, newCol);
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
                int rowChange = dir[0];
                int colChange = dir[1];
                int newRow = row + rowChange;
                int newCol = col + colChange;
                if ((newRow <= 8 && newRow >= 1) && (newCol <= 8  && newCol >= 1)) {
                    ChessPosition nextPosition = new ChessPosition(newRow, newCol);
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
            int newRow = row + directions;
            if ((newRow <= 8 && newRow >= 1) && (col <= 8  && col >= 1)) {
                ChessPosition nextPosition = new ChessPosition(newRow, col);
                ChessPiece nextPiece = board.getPiece(nextPosition);
                if (nextPiece == null) {
                    if (newRow == promotion_row) {
                        moves.add(new ChessMove(myPosition, nextPosition, PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, nextPosition, PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, nextPosition, PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, nextPosition, PieceType.BISHOP));
                    } else {
                        moves.add(new ChessMove(myPosition, nextPosition, null));
                    }
                }
            }
            if (row == starting_row) {
                int one_move = row + directions;
                int double_move = row + 2 * directions;
                ChessPosition nextPosition = new ChessPosition(one_move, col);
                ChessPosition nextPosition2 = new ChessPosition(double_move, col);
                ChessPiece nextPiece = board.getPiece(nextPosition);
                ChessPiece nextPiece2 = board.getPiece(nextPosition2);
                if (nextPiece == null && nextPiece2 == null) {
                    moves.add(new ChessMove(myPosition, nextPosition2, null));
                }
            }
            for (int dc : new int[]{1, -1}) {
                int colChange = col + dc;
                if ((newRow <= 8 && newRow >= 1) && (colChange <= 8  && colChange >= 1)) {
                    ChessPosition nextPosition = new ChessPosition(newRow, colChange);
                    ChessPiece nextPiece = board.getPiece(nextPosition);
                    if (nextPiece != null && getTeamColor() != nextPiece.getTeamColor()) {
                        if (newRow == promotion_row) {
                            moves.add(new ChessMove(myPosition, nextPosition, PieceType.QUEEN));
                            moves.add(new ChessMove(myPosition, nextPosition, PieceType.ROOK));
                            moves.add(new ChessMove(myPosition, nextPosition, PieceType.BISHOP));
                            moves.add(new ChessMove(myPosition, nextPosition, PieceType.KNIGHT));
                        }
                        else {
                            moves.add(new ChessMove(myPosition, nextPosition, null));
                        }
                    }
                }
            }
        }
            return moves;
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

