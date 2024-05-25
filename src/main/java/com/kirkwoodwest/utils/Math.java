package com.kirkwoodwest.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Custom Math Class for remapping values
 */
public class Math {

  public static double map(double x, double in_min, double in_max, double out_min, double out_max) {
    return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
  }

  public static int valueLimit(int x, int min, int max) {
    return (x > min) ? ((x < max) ? x : max) : min;
  }

  public static String padInt(int padding, int value){
    return String.format("%0" + padding + "d", value);
  }

}
