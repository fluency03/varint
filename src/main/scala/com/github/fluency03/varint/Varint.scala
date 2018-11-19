package com.github.fluency03.varint

import scala.annotation.tailrec

object Varint {

  val MSB = 0x80
  val LOW_7_BITS = 0x7F
  val INT_REST_BITES = 0xFFFFFF80
  val LONG_REST_BITES = 0xFFFFFFFFFFFFFF80L

  def encodeInt(num: Int): Array[Byte] = {
    @tailrec
    def rec(value: Int, acc: Array[Byte]): Array[Byte] =
      if ((value & INT_REST_BITES) == 0) acc :+ (value & LOW_7_BITS).toByte
      else rec(value >>> 7, acc :+ ((value & LOW_7_BITS) | MSB).toByte)

    rec(num, Array())
  }

  def encodeLong(num: Long): Array[Byte] = {
    @tailrec
    def rec(value: Long, acc: Array[Byte]): Array[Byte] =
      if ((value & LONG_REST_BITES) == 0) acc :+ (value & LOW_7_BITS).toByte
      else rec(value >>> 7, acc :+ ((value & LOW_7_BITS) | MSB).toByte)

    rec(num, Array())
  }

  def decodeToInt(bytes: Array[Byte]): (Int, Int) = decodeToInt(bytes, 0)

  def decodeToInt(bytes: Array[Byte], offset: Int): (Int, Int) = {
    @tailrec
    def rec(index: Int, shift: Int, acc: Int): (Int, Int) =
      if (index >= bytes.length) throw new IllegalArgumentException("Cannot find the ending Byte.")
      else if ((bytes(index) & MSB) == 0) (acc | (bytes(index) << shift), index + 1 - offset)
      else rec(index + 1, shift + 7, acc | ((bytes(index) & LOW_7_BITS) << shift))

    rec(offset, 0, 0)
  }

  def decodeToLong(bytes: Array[Byte]): (Long, Int) = decodeToLong(bytes, 0)

  def decodeToLong(bytes: Array[Byte], offset: Int): (Long, Int) = {
    @tailrec
    def rec(index: Int, shift: Long, acc: Long): (Long, Int) =
      if (index >= bytes.length) throw new IllegalArgumentException("Cannot find the ending Byte.")
      else if ((bytes(index) & MSB) == 0) (acc | (bytes(index).toLong << shift), index + 1 - offset)
      else rec(index + 1, shift + 7, acc | ((bytes(index).toLong & LOW_7_BITS) << shift))

    rec(offset, 0L, 0L)
  }

  def extractLength(bytes: Array[Byte], offset: Int): Int = {
    @tailrec
    def rec(index: Int): Int =
      if (index >= bytes.length) 0
      else if ((bytes(index) & MSB) == 0) index + 1 - offset
      else rec(index + 1)

    rec(offset)
  }

}