package com.kirkwoodwest.utils;

public class Array {
  public static int indexOfIntArray(int[] array, int key) {
    int returnvalue = -1;
    for (int i = 0; i < array.length; ++i) {
      if (key == array[i]) {
        returnvalue = i;
        break;
      }
    }
    return returnvalue;
  }

  public static int[] generateIncrementalArray(int start_value, int index_count){
    //Builds the entire cc list...
    int[] cc_list = new int[index_count];
    for(int i=0; i < index_count; i++) {
      cc_list[i] = i + start_value;
    }
    return cc_list;
  }
}
