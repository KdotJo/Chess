package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();
    private TeamColor color;
    private ChessPiece.PieceType pieceType;
    private ChessMove move;
    public ChessGame() {
        board.resetBoard();
        color = TeamColor.WHITE;
        this.pieceType = pieceType;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return color;
    }

    /**

     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        color = team;
    }
    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        List<ChessMove> valid_moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null || getTeamTurn() != piece.getTeamColor()) {return valid_moves;}

        Collection<ChessMove> temp_moves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : temp_moves) {
            ChessPosition temp_start = move.getStartPosition();
            ChessPosition temp_end = move.getEndPosition();
            ChessBoard temp_board = board.deepCopy();
            ChessPiece temp_piece = temp_board.getPiece(temp_start);
            temp_board.addPiece(temp_start, null);

            if (temp_board.getPiece(temp_end) == null || (temp_board.getPiece(temp_end) != null &&
                    temp_board.getPiece(temp_end).getTeamColor() != temp_piece.getTeamColor())) {
                temp_board.addPiece(temp_end, temp_piece);
                if (!isInCheck(temp_piece.getTeamColor())) {
                    valid_moves.add(move);
                }
            }
        }
        return valid_moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null || piece.getTeamColor() != getTeamTurn()) {throw new InvalidMoveException();}

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        if (!moves.contains(move) || isInCheck(piece.getTeamColor())) {
            throw new InvalidMoveException();
        }

        board.addPiece(startPosition, null);

        if (board.getPiece(endPosition) != null && board.getPiece(endPosition).getTeamColor() != piece.getTeamColor()) {
            board.addPiece(endPosition, piece);
        } else {
            board.addPiece(endPosition, piece);
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
            ChessPiece promotion = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(endPosition, promotion);
        }
        if (getTeamTurn() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = null;
        Collection<ChessMove> all = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if ((piece != null) && (piece.getTeamColor() == teamColor) && (piece.getPieceType() == ChessPiece.PieceType.KING)) {
                    kingPos = position;
                    break;
                }
            }
            if (kingPos != null) {break;}
        }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    all.addAll(moves);
                    for (ChessMove move : all) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPos = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if ((piece != null) && (piece.getTeamColor() == teamColor) && (piece.getPieceType() == ChessPiece.PieceType.KING)) {
                    kingPos = position;
                    break;
                }
            }
            if (kingPos != null) {break;}
        }
        ChessBoard temp = board.deepCopy();
        ChessPiece king = temp.getPiece(kingPos);
        Collection<ChessMove> king_moves = king.pieceMoves(board, kingPos);
        if (isInCheck(king.getTeamColor())) {
            for (ChessMove move : king_moves) {
                board.addPiece(move.getEndPosition(), king);
                king = board.getPiece(move.getEndPosition());
                if (isInCheck(king.getTeamColor())) {
                    board.addPiece(move.getStartPosition(), king);
                    king = board.getPiece(move.getStartPosition());
                }
                else {
                    return false;
                }
            }
        }
        setTeamTurn(color);
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
