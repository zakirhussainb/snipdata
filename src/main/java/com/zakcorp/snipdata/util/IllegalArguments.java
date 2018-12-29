/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.util;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

public final class IllegalArguments {
  private static final String WAS_NULL = " was null.";
  private static final String WAS_EMPTY = " was empty.";
  private static final String WAS_ZERO = " was zero.";

  /**
   * Ensure not null.
   *
   * @param name  Name
   * @param value Value
   *
   * @throws IllegalArgumentException if value is null
   */
  public static void ensureNotNull( String name, Object value )
  {
    if( value != null )
    {
      return;
    }
    throw new IllegalArgumentException( name + WAS_NULL );
  }

  /**
   * Ensure not empty.
   *
   * @param name  Name
   * @param value Value
   *
   * @throws IllegalArgumentException if value is null or empty
   */
  public static void ensureNotEmpty( String name, String value )
  {
    ensureNotNull( name, value );
    if( value.length() == 0 )
    {
      throw new IllegalArgumentException( name + WAS_EMPTY );
    }
  }

  /**
   * Ensure not empty.
   *
   * @param name  Name
   * @param value Value
   *
   * @throws IllegalArgumentException if value is null or empty
   */
  public static void ensureNotEmpty( String name, CharSequence value )
  {
    ensureNotNull( name, value );
    if( value.length() == 0 )
    {
      throw new IllegalArgumentException( name + WAS_EMPTY );
    }
  }

  /**
   * Ensure not empty.
   *
   * @param name  Name
   * @param trim  Trim value before check if true, don't otherwise
   * @param value Value
   *
   * @throws IllegalArgumentException if value is null or empty
   */
  public static void ensureNotEmpty( String name, boolean trim, String value )
  {
    ensureNotNull( name, value );
    if( value.length() == 0 || ( trim && value.trim().length() == 0 ) )
    {

      throw new IllegalArgumentException( name + WAS_EMPTY );
    }
  }

  /**
   * Ensure not empty.
   *
   * @param name  Name
   * @param value Value
   *
   * @throws IllegalArgumentException if value is null or empty
   */
  public static void ensureNotEmpty( String name, Object[] value )
  {
    ensureNotNull( name, value );
    if( value.length == 0 )
    {
      throw new IllegalArgumentException( name + WAS_EMPTY );
    }
  }

  /**
   * Ensure not empty.
   *
   * @param name  Name
   * @param value Value
   *
   * @throws IllegalArgumentException if value is null or empty
   */
  public static void ensureNotEmpty( String name, byte[] value )
  {
    ensureNotNull( name, value );
    if( value.length == 0 )
    {
      throw new IllegalArgumentException( name + WAS_EMPTY );
    }
  }

  /**
   * Ensure not empty.
   *
   * @param name  Name
   * @param value Value
   *
   * @throws IllegalArgumentException if value is null or empty
   */
  public static void ensureNotEmpty( String name, Collection<?> value )
  {
    ensureNotNull( name, value );
    if( value.isEmpty() )
    {
      throw new IllegalArgumentException( name + WAS_EMPTY );
    }
  }

  /**
   * Ensure not empty.
   *
   * @param name  Name
   * @param value Value
   *
   * @throws IllegalArgumentException if value is null or empty
   */
  public static void ensureNotEmpty( String name, Properties value )
  {
    ensureNotNull( name, value );
    if( value.isEmpty() )
    {
      throw new IllegalArgumentException( name + WAS_EMPTY );
    }
  }

  /**
   * Ensure not empty.
   *
   * @param name  Name
   * @param value Value
   *
   * @throws IllegalArgumentException if value is null or empty
   */
  public static void ensureNotEmpty( String name, Map<?, ?> value )
  {
    ensureNotNull( name, value );
    if( value.isEmpty() )
    {
      throw new IllegalArgumentException( name + WAS_EMPTY );
    }
  }

  /**
   * Ensures that the string array instance is not null and that it has entries that are not null or empty
   * either without trimming the string.
   *
   * @param name  Name
   * @param value Value
   *
   * @throws IllegalArgumentException if value is null or empty
   */
  public static void ensureNotEmptyContent( String name, String[] value )
  {
    ensureNotEmptyContent( name, false, value );
  }

  /**
   * Ensures that the string array instance is not null and that it has entries that are not null or empty.
   *
   * @param name  Name
   * @param trim  Trim flag
   * @param value Value
   *
   * @throws IllegalArgumentException if value is null or empty
   */
  public static void ensureNotEmptyContent( String name, boolean trim, String[] value )
  {
    ensureNotEmpty( name, value );
    for( int i = 0; i < value.length; i++ )
    {
      ensureNotEmpty( value[i] + "[" + i + "]", value[i] );
      if( trim )
      {
        ensureNotEmpty( value[i] + "[" + i + "]", value[i].trim() );
      }
    }
  }

  /**
   * Ensure not zero.
   *
   * @param name  Name
   * @param value Value
   *
   * @throws IllegalArgumentException if value is null or zero
   */
  public static void ensureNotZero( String name, Integer value )
  {
    ensureNotNull( name, value );
    if( value == 0 )
    {
      throw new IllegalArgumentException( name + WAS_ZERO );
    }
  }

  public static void ensureInRange( String name, Integer value, Integer from, Integer to )
  {
    ensureNotNull( name, value );
    if( value < from || value > to )
    {
      throw new IllegalArgumentException( name + " was not in range [" + from + "," + to + "]." );
    }
  }

  public static void ensureGreaterOrEqual( String name, Integer value, Integer lower )
  {
    ensureNotNull( name, value );
    if( value < lower )
    {
      throw new IllegalArgumentException( name + " was lesser than " + lower );
    }
  }

  public static void ensureGreater( String name, Integer value, Integer lower )
  {
    ensureNotNull( name, value );
    if( value <= lower )
    {
      throw new IllegalArgumentException( name + " was lesser than or equal to " + lower );
    }
  }

  public static void ensureGreater( String name, BigInteger value, BigInteger lower )
  {
    ensureNotNull( name, value );
    if( lower.compareTo( value ) > 0 )
    {
      throw new IllegalArgumentException( name + " was lesser than or equal to " + lower );
    }
  }

  public static void ensureLesser( String name, Integer value, Integer higher )
  {
    ensureNotNull( name, value );
    if( value >= higher )
    {
      throw new IllegalArgumentException( name + " was higher than or equal to " + higher );
    }
  }

  public static void ensureLesserOrEqual( String name, Integer value, Integer higher )
  {
    ensureNotNull( name, value );
    if( value > higher )
    {
      throw new IllegalArgumentException( name + " was higher than " + higher );
    }
  }

  private IllegalArguments()
  {
  }
}
