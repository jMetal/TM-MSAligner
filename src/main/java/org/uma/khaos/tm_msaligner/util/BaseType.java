package org.uma.khaos.tm_msaligner.util;

public class BaseType {
  private int code;

  public BaseType(int code) {     this.code = code;   }

  public BaseType(char code) {
    if (code == 'M') this.code=0;
    else if (code == 'I') this.code=1;
    else if (code == 'O') this.code=2;
    else this.code=3;
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
    if (code == 0) return "Transmembrane";
    else if (code == 1) return "Inside";
    else if (code == 2) return "Outside";
    else return "unknown";
  }


  public boolean isNonTMRegion() {    return code > 0;  }

  public boolean isTMRegion() {    return code == 0;  }

  public String getAbrev() {
    if (code == 0) return "M";
    else if (code == 1) return "I";
    else if (code == 2) return "O";
    else return "U";
  }
}
