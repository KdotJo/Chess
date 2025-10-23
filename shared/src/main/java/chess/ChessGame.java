package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        List<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);
        ChessBoard currentBoard = this.board;
        if (piece == null) {return validMoves;}

        Collection<ChessMove> tempMoves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : tempMoves) {
            ChessPosition tempStart = move.getStartPosition();
            ChessPosition tempEnd = move.getEndPosition();
            ChessBoard tempBoard = board.deepCopy();
            ChessPiece tempPiece = tempBoard.getPiece(tempStart);
            tempBoard.addPiece(tempStart, null);

            if (tempBoard.getPiece(tempEnd) == null || tempBoard.getPiece(tempEnd).getTeamColor() != tempPiece.getTeamColor()) {
                tempBoard.addPiece(tempEnd, tempPiece);
                this.board = tempBoard;
                if (!isInCheck(tempPiece.getTeamColor())) {
                    validMoves.add(move);
                }
                this.board = currentBoard;
            }
        }
        return validMoves;
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
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if ((piece != null) && (piece.getTeamColor() == teamColor)) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    ChessBoard currentBoard = this.board;

                    for (ChessMove move : moves) {
                        ChessPosition startPosition = move.getStartPosition();
                        ChessPosition endPosition = move.getEndPosition();
                        ChessBoard tempBoard = board.deepCopy();
                        ChessPiece tempPiece = tempBoard.getPiece(startPosition);
                        tempBoard.addPiece(startPosition, null);
                        if (tempBoard.getPiece(endPosition) == null ||
                                tempBoard.getPiece(endPosition).getTeamColor() != tempPiece.getTeamColor()) {
                            tempBoard.addPiece(endPosition, tempPiece);
                            this.board = tempBoard;
                            if (!isInCheck(tempPiece.getTeamColor())) {
                                return false;
                            }
                            this.board = currentBoard;
                        }
                    }
                }
            }
        }
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
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if ((piece != null) && (piece.getTeamColor() == teamColor)) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    ChessBoard currentBoard = this.board;

                    for (ChessMove move : moves) {
                        ChessPosition startPosition = move.getStartPosition();
                        ChessPosition endPosition = move.getEndPosition();
                        ChessBoard tempBoard = board.deepCopy();
                        ChessPiece tempPiece = tempBoard.getPiece(startPosition);
                        tempBoard.addPiece(startPosition, null);
                        if (tempPiece.getPieceType() == ChessPiece.PieceType.KING) {
                            if (isInCheck(tempPiece.getTeamColor())) {return false;}
                        }
                        if (tempBoard.getPiece(endPosition) == null ||
                                tempBoard.getPiece(endPosition).getTeamColor() != tempPiece.getTeamColor()) {
                            tempBoard.addPiece(endPosition, tempPiece);
                            this.board = tempBoard;
                            if (!isInCheck(tempPiece.getTeamColor())) {
                                return false;
                            }
                            this.board = currentBoard;
                        }
                    }
                }
            }
        }
        return true;
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
    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessGame that = (ChessGame) o;
        return Objects.equals(board, that.board) && color == that.color;
    }
    @Override
    public int hashCode() {
        return Objects.hash(board, color);
    }
}
