/*
 * Copyright  : ZakCorp (c) Zakir Hussain
 * License    : MIT
 * Maintainer : zakirhussainb2693@gmail.com
 * Stability  : stable
 */

package com.zakcorp.snipdata.util;

import java.math.BigInteger;
import java.nio.charset.Charset;

import static com.zakcorp.snipdata.util.IllegalArguments.*;

public final class Base62 {
  private static final BigInteger BASE = BigInteger.valueOf( 62 );
  public static final Charset UTF_8 = Charset.forName( "UTF-8" );

  public static BigInteger decode( String base62String )
  {
    return decode( base62String, UTF_8 );
  }

  public static BigInteger decode( String base62String, Charset charset )
  {
    ensureNotNull( "Base62 String", base62String );
    ensureNotNull( "Charset", charset );
    return decode( base62String.getBytes( charset ) );
  }

  public static int decodeInt( String base62String )
    throws ArithmeticException
  {
    return decodeInt( base62String, UTF_8 );
  }

  public static int decodeInt( String base62String, Charset charset )
    throws ArithmeticException
  {
    ensureNotNull( "Base62 String", base62String );
    ensureNotNull( "Charset", charset );
    return decode( base62String.getBytes( charset ) ).intValueExact();
  }

  public static long decodeLong( String base62String )
    throws ArithmeticException
  {
    return decodeLong( base62String, UTF_8 );
  }

  public static long decodeLong( String base62String, Charset charset )
    throws ArithmeticException
  {
    ensureNotNull( "Base62 String", base62String );
    ensureNotNull( "Charset", charset );
    return decode( base62String.getBytes( charset ) ).longValueExact();
  }

  public static byte[] decodeBytes( String base62String )
    throws ArithmeticException
  {
    return decodeBytes( base62String, UTF_8 );
  }

  public static byte[] decodeBytes( String base62String, Charset charset )
    throws ArithmeticException
  {
    ensureNotNull( "Base62 String", base62String );
    ensureNotNull( "Charset", charset );
    return decode( base62String.getBytes( charset ) ).toByteArray();
  }

  private static BigInteger decode( byte[] base62Bytes )
  {
    BigInteger res = BigInteger.ZERO;
    BigInteger multiplier = BigInteger.ONE;
    for( int i = base62Bytes.length - 1; i >= 0; i-- )
    {
      res = res.add( multiplier.multiply( BigInteger.valueOf( alphabetValueOf( base62Bytes[i] ) ) ) );
      multiplier = multiplier.multiply( BASE );
    }
    return res;
  }

  private static int alphabetValueOf( byte bytee )
  {
    if( Character.isLowerCase( bytee ) )
    {
      return bytee - ( 'a' - 10 );
    }
    else if( Character.isUpperCase( bytee ) )
    {
      return bytee - ( 'A' - 10 - 26 );
    }
    return bytee - '0';
  }

  public static String encode( final Integer number )
  {
    ensureNotNull( "Number", number );
    return encode( new BigInteger( number.toString() ) );
  }

  public static String encode( final Long number )
  {
    ensureNotNull( "Number", number );
    return encode( new BigInteger( number.toString() ) );
  }

  public static String encode( final byte[] bytes )
  {
    ensureNotNull( "Bytes", bytes );
    ensureNotEmpty( "Bytes", bytes );
    return encode( new BigInteger( bytes ) );
  }

  public static String encode( final BigInteger number )
  {
    ensureNotNull( "Number", number );
    ensureGreater( "Number", number, BigInteger.ZERO );

    if( BigInteger.ZERO.compareTo( number ) == 0 )
    {
      return "0";
    }

    BigInteger value = number.add( BigInteger.ZERO );

    StringBuilder sb = new StringBuilder();
    while( BigInteger.ZERO.compareTo( value ) < 0 )
    {
      BigInteger[] quotientReminder = value.divideAndRemainder( BASE );
      int remainder = quotientReminder[1].intValue();
      if( remainder < 10 )
      {
        sb.insert( 0, (char) ( remainder + '0' ) );
      }
      else if( remainder < 10 + 26 )
      {
        sb.insert( 0, (char) ( remainder + 'a' - 10 ) );
      }
      else
      {
        sb.insert( 0, (char) ( remainder + 'A' - 10 - 26 ) );
      }
      // quotient
      value = quotientReminder[0];
    }
    return sb.toString();
  }

  private Base62()
  {
  }
}
