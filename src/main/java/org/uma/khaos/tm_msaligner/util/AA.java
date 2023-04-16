package org.uma.khaos.tm_msaligner.util;

public class AA {
  private char letter;
  private BaseType type;

  public static final char GAP_IDENTIFIER = '-';

  public AA(char letter, BaseType type) {
    this.letter = letter;
    this.type = type;
  }

  public AA(char letter, char type) {
    this.letter = letter;
    this.type = new BaseType(type);
  }

  public AA(char letter, BaseType typeA, BaseType typeB) {
    this.letter = letter;
    if(typeA.isTMRegion()){
      if(typeB.isTMRegion()){
        this.type = new BaseType(typeA);
      }else{
        this.type = new BaseType(typeB);
      }
    }else{
      this.type = new BaseType(typeA);
    }
  }

  public AA(char letter) {
    this.letter = letter;
    this.type = new BaseType(-1);
  }

  public AA(AA aa) {
    this.letter = aa.getLetter();
    this.type = new BaseType(aa.getType());
  }

  public char getLetter() {
    return letter;
  }

  public void setLetter(char letter) {
    this.letter = letter;
  }

  public BaseType getType() {
    return type;
  }

  public void setType(BaseType type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return String.valueOf(letter);
  }

  public boolean isGap() {    return letter == GAP_IDENTIFIER;  }
}
