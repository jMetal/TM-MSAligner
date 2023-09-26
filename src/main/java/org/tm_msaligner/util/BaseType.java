package org.tm_msaligner.util;

public class BaseType {
  private int code;

  public static final char chrTMRegion = 'M';
  public static final char chrInsideRegion = 'I';
  public static final char chrOutsideRegion = 'O';

  public static final int codeTMRegion = 0;
  public static final int codeInsideRegion = 1;
  public static final int codeOutsideRegion = 2;

  public static final int codeUnknownRegion = 3;

  public BaseType(int code) {     this.code = code;   }

  public BaseType(char code) {
    if (code == this.chrTMRegion) this.code=this.codeTMRegion;
    else if (code == this.chrInsideRegion) this.code=this.codeInsideRegion;
    else if (code == this.chrOutsideRegion) this.code=this.codeOutsideRegion;
    else this.code=this.codeUnknownRegion;
  }

  public BaseType(BaseType basetype) {
    code = basetype.getCode();
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public boolean equals(BaseType B) {
    return code == B.getCode();
  }

  public String getDesc() {
    if (code == this.codeTMRegion) return "Transmembrane";
    else if (code == this.codeInsideRegion) return "Inside";
    else if (code == this.codeOutsideRegion) return "Outside";
    else return "unknown";
  }


  public boolean isNonTMRegion() {    return code > codeTMRegion;  }

  public boolean isTMRegion() {    return code == codeTMRegion;  }

  public String getAbrev() {
    if (code == this.codeTMRegion) return "M";
    else if (code == this.codeInsideRegion) return "I";
    else if (code == this.codeOutsideRegion) return "O";
    else return "U";
  }
}
