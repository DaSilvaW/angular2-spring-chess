package com.dynadrop.chess.model.piece;

import com.dynadrop.chess.model.Piece;
import com.dynadrop.chess.websocket.bean.Movement;
import com.dynadrop.chess.model.Board;


public class Knight implements Piece {
  private String htmlCode;
  private int color;

  public Knight(int color) {
    this.color = color;
    this.htmlCode = "&#9822;";
  }

  public boolean validateMovement (Movement movement, Board board) {
    System.out.println("TODO validateMovement for "+this.getClass());
    return true;
  }

}