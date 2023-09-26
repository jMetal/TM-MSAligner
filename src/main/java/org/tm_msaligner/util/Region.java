package org.tm_msaligner.util;

public class Region {
  private BaseType Type;
  private int Start;
  private int End;
  private final boolean valid;

  public Region(String str_type, String str_start, String str_end) {
    int end, start;
    end = Integer.parseInt(str_end);
    start = Integer.parseInt(str_start);
    str_type = str_type.toLowerCase();
    if ((end > start) && (str_type.equals("m") || str_type.equals("i") || str_type.equals("o"))) {
      if (str_type.equals("m")) Type = new BaseType(0);
      else if (str_type.equals("i")) Type = new BaseType(1);
      else if (str_type.equals("o")) Type = new BaseType(2);
      else Type = new BaseType(-1);

      Start = start;
      End = end;
      valid = true;

    } else {
      Type = null;
      Start = 0;
      End = 0;
      valid = false;
    }
  }

  public int getLength() {
    return valid ? End - Start + 1 : 0;
  }

  public boolean isValid() {
    return valid;
  }

  @Override
  public String toString() {
    return String.format(Type.getDesc() + " " + Start + " - " + End);
  }

  public BaseType getType() {
    return Type;
  }

  public void setType(BaseType type) {
    Type = type;
  }

  public int getStart() {
    return Start;
  }

  public void setStart(int start) {
    Start = start;
  }

  public int getEnd() {
    return End;
  }

  public void setEnd(int end) {
    End = end;
  }
}
