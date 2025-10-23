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
            if (isValidMoveForPiece(move, currentBoard)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    private boolean isValidMoveForPiece(ChessMove move, ChessBoard currentBoard) {
        ChessPosition tempStart = move.getStartPosition();
        ChessPosition tempEnd = move.getEndPosition();
        ChessBoard tempBoard = board.deepCopy();
        ChessPiece tempPiece = tempBoard.getPiece(tempStart);
        tempBoard.addPiece(tempStart, null);

        if (canMakeMove(tempBoard, tempEnd, tempPiece)) {
            tempBoard.addPiece(tempEnd, tempPiece);
            this.board = tempBoard;
            boolean result = !isInCheck(tempPiece.getTeamColor());
            this.board = currentBoard;
            return result;
        }
        return false;
    }

    private boolean canMakeMove(ChessBoard tempBoard, ChessPosition endPos, ChessPiece piece) {
        return tempBoard.getPiece(endPos) == null ||
                tempBoard.getPiece(endPos).getTeamColor() != piece.getTeamColor();
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

        if (piece == null || piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException();
        }

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        if (!moves.contains(move) || isInCheck(piece.getTeamColor())) {
            throw new InvalidMoveException();
        }

        executeMove(startPosition, endPosition, piece, move);
        switchTurns();
    }

    private void executeMove(ChessPosition start, ChessPosition end, ChessPiece piece, ChessMove move) {
        board.addPiece(start, null);
        board.addPiece(end, piece);

        // Handle pawn promotion
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
            ChessPiece promotion = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(end, promotion);
        }
    }

    private void switchTurns() {
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
        ChessPosition kingPos = kingPosition(teamColor);
        if (kingPos == null) return false;

        return check(kingPos, teamColor);
    }

    private ChessPosition kingPosition(TeamColor teamColor) {
        for (int i = 0; i < 64; i++) {
            int row = (i / 8) + 1;
            int col = (i % 8) + 1;
            ChessPosition position = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(position);
            if (piece != null && piece.getTeamColor() == teamColor &&
                    piece.getPieceType() == ChessPiece.PieceType.KING) {
                return position;
            }
        }
        return null;
    }

    private boolean check(ChessPosition targetPos, TeamColor defendingColor) {
        for (int i = 0; i < 64; i++) {
            int row = (i / 8) + 1;
            int col = (i % 8) + 1;
            ChessPosition position = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(position);

            if (piece != null && piece.getTeamColor() != defendingColor) {
                if (canPieceAttackPosition(piece, position, targetPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canPieceAttackPosition(ChessPiece piece, ChessPosition piecePos, ChessPosition targetPos) {
        Collection<ChessMove> moves = piece.pieceMoves(board, piecePos);
        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(targetPos)) {
                return true;
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
        return !hasValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !hasValidMoves(teamColor);
    }

    private boolean hasValidMoves(TeamColor teamColor) {
        for (int i = 0; i < 64; i++) {
            int row = (i / 8) + 1;
            int col = (i % 8) + 1;
            ChessPosition position = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(position);

            if (piece != null && piece.getTeamColor() == teamColor) {
                if (pieceValidMove(piece, position)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean pieceValidMove(ChessPiece piece, ChessPosition position) {
        Collection<ChessMove> moves = piece.pieceMoves(board, position);
        ChessBoard currentBoard = this.board;

        for (ChessMove move : moves) {
            if (valid(move, currentBoard)) {
                return true;
            }
        }
        return false;
    }

    private boolean valid(ChessMove move, ChessBoard currentBoard) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessBoard tempBoard = board.deepCopy();
        ChessPiece tempPiece = tempBoard.getPiece(startPosition);

        tempBoard.addPiece(startPosition, null);
        if (canMakeMove(tempBoard, endPosition, tempPiece)) {
            tempBoard.addPiece(endPosition, tempPiece);
            this.board = tempBoard;
            boolean result = !isInCheck(tempPiece.getTeamColor());
            this.board = currentBoard;
            return result;
        }
        return false;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

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
