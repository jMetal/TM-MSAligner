package org.uma.khaos.tm_msaligner.util;

import org.uma.jmetal.util.errorchecking.JMetalException;

import java.util.Arrays;

public class AAArray {
  private AA[] array;

  public AAArray() {
    array = new AA[0];
  }

  public AAArray(String string) {
    array = new AA[string.length()];
    for (int i=0; i<string.length(); i++)
    	array[i] = new AA(string.charAt(i));
  }

  public AAArray(String string, String stringregiones) {
    array = new AA[string.length()];
    for (int i=0; i<string.length(); i++)
      array[i] = new AA(string.charAt(i), stringregiones.charAt(i));
  }

  public AAArray(AA[] array) {
    this.array = new AA[array.length];
    for (int i=0; i< array.length; i++)
    	this.array[i] = new AA(array[i]);
  }

  public AA[] getCharArray() {
    return array;
  }

  public void setCharArray(AA[] array) {
    this.array = array;
  }

   public AA AAAt(int index) {
    return array[index];
  }

  public void setAAAt(int index, AA aa) {
    if (index >= array.length || index < 0) {
      throw new RuntimeException("Index value (" + index + ") greater than size (" + array.length + ") or lower than 0");
    }
    array[index] = aa;
  }

  public void insert(int index, AA aa) {

    if (index < 0 || index > array.length) {
      throw new JMetalException("Insert index (" + index + ") is incorrect");
    }
    int size = array.length;
    expand(array.length + 1);
    System.arraycopy(array, index, array, index + 1, size - index);
    array[index] = aa;

  }

  public void delete(int index) {
    if (index >= array.length) {
      throw new JMetalException("Index value (" + index + ") greater or equal than size (" + array.length + ")");
    }

    AA[] array2 = new AA[array.length - 1];
    if (index > 0)
      System.arraycopy(array, 0, array2, 0, index);
    if (index < array.length - 1)
      System.arraycopy(array, index + 1, array2, index, array.length - index - 1);

    array = array2;
    array2 = null;
  }

  public void move(int index1, int index2, int newpos) {

    int len = index2 - index1 + 1;
    AA[] array2 = new AA[len];
    System.arraycopy(array, index1, array2, 0, len);

    if (newpos > index2) {

      System.arraycopy(array, index2 + 1, array, index1, newpos - index2 - 1);

      System.arraycopy(array2, 0, array, index1 + (newpos - index2 - 1), len);

    } else {

      System.arraycopy(array, newpos, array, newpos + len, index1 - newpos);
      System.arraycopy(array2, 0, array, newpos, len);

    }
    array2 = null;
  }

 
  public AA[] substringChar(int position, int length) {
    if ((position + length) > array.length) {
      throw new JMetalException("The position (" + position + ") + length (" + length + ") is " +
              "greater than the array size (" + array.length + ")");
    }

    AA[] result = new AA[length];

    System.arraycopy(array, position, result, 0, length);

    return result;
  }

  public int getSize() {
    return array.length;
  }

  @Override
  public String toString() {
	char[] array_chr = new char[array.length];
	for(int i=0; i<array.length;i++)
		array_chr[i]= array[i].getLetter();
    return new String(array_chr);
  }
 

  public void expand(int newCapacity) {
    array = Arrays.copyOf(array, newCapacity);
  }

  public char charAt(int i) {
	  return array[i].getLetter();
  }
 
  public void printConsole() {

	  for(int i=0; i<array.length;i++) {
		  System.out.print(array[i].getLetter());
	  }

      System.out.print("\n");

    for(int i=0; i<array.length;i++) {
      System.out.print(array[i].getType().getAbrev());
    }

    System.out.print("\n");
  }
  
}